package digi.kitplay.ui.base.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import digi.kitplay.MVVMApplication;
import digi.kitplay.R;
import digi.kitplay.constant.Constants;
import digi.kitplay.data.model.api.ApiModelUtils;
import digi.kitplay.data.socket.KittyRealtimeEvent;
import digi.kitplay.data.socket.dto.Message;
import digi.kitplay.databinding.DialogConnectionErrorBinding;
import digi.kitplay.databinding.LayoutUiHeaderBinding;
import digi.kitplay.databinding.LayoutWifiKeyboardBinding;
import digi.kitplay.di.component.ActivityComponent;
import digi.kitplay.di.component.DaggerActivityComponent;
import digi.kitplay.di.module.ActivityModule;
import digi.kitplay.ui.main.MainViewModel;
import digi.kitplay.ui.main.connection.ConnectWifiActivity;
import digi.kitplay.ui.main.dialog.KeyboardDialog;
import digi.kitplay.utils.DeviceUtils;
import digi.kitplay.utils.DialogUtils;
import digi.kitplay.utils.NetworkUtils;

import javax.inject.Inject;
import javax.inject.Named;

import timber.log.Timber;

public abstract class BaseActivity<B extends ViewDataBinding, V extends BaseViewModel>
        extends AppCompatActivity implements KittyRealtimeEvent{

    protected B viewBinding;

    @Inject
    protected V viewModel;

    @Inject
    protected Context application;

    @Named("access_token")
    @Inject
    protected String token;

    @Named("device_id")
    @Inject
    protected String deviceId;

    private Dialog progressDialog;

    // Listen all action from local
    private BroadcastReceiver globalApplicationReceiver;
    private IntentFilter filterGlobalApplication;
    protected Dialog networkDialog;

    private final BroadcastReceiver statusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //handle status listener
            String action = intent.getAction();
            if (action != null && (action.equals(Intent.ACTION_POWER_CONNECTED) ||
                    action.equals(Intent.ACTION_POWER_DISCONNECTED) ||
                    action.equals(Intent.ACTION_BATTERY_CHANGED) ||
                    action.equals(Intent.ACTION_BATTERY_LOW) ||
                    action.equals(Intent.ACTION_BATTERY_OKAY) ||
                    action.equals(BatteryManager.ACTION_CHARGING) ||
                    action.equals(BatteryManager.ACTION_DISCHARGING))) {
                setupBatteryLevel(intent);
            }

            //wifi
            if (action != null && (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION) ||
                    action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)||
                    action.equals(WifiManager.RSSI_CHANGED_ACTION))) {
                setupWifiLevel();
            }

            //bluetooth
            if (action != null && action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                handleBluetooth();
            }

            //time update
            headerBinding.textClock.setTimeZone(TimeZone.getDefault().getID());
        }

    };
    private void setupBatteryLevel(Intent it) {
        try {
            String action = it.getAction();
            if (action != null && action.equals(Intent.ACTION_POWER_CONNECTED)) {
                headerBinding.batteryIcon.setImageResource(R.drawable.battery_charging);
            } else {
                int deviceStatus = it.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                int percent;

                float level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                float scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                percent = (int) ((level / scale) * 100.0f);

                if (deviceStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
                    headerBinding.batteryIcon.setImageResource(R.drawable.battery_charging);
                }

                if (level > 0 && scale > 0) {
                    headerBinding.setBatteryPercent(percent);
                    headerBinding.executePendingBindings();
                    if (deviceStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
                        headerBinding.batteryIcon.setImageResource(R.drawable.battery_charging);
                    } else {
                        percentBattery(percent);
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private boolean checkRange(int min, int max, int value) {
        return value >= min && value < max;
    }
    private void percentBattery(int percent) {
        if (checkRange(0, 10, percent)) {
            headerBinding.batteryIcon.setImageResource(R.drawable.battery_empty);
        } else if (checkRange(10, 20, percent)) {
            headerBinding.batteryIcon.setImageResource(R.drawable.battery_10);
        } else if (checkRange(20, 30, percent)) {
            headerBinding.batteryIcon.setImageResource(R.drawable.battery_20);
        } else if (checkRange(30, 40, percent)) {
            headerBinding.batteryIcon.setImageResource(R.drawable.battery_30);
        } else if (checkRange(40, 50, percent)) {
            headerBinding.batteryIcon.setImageResource(R.drawable.battery_40);
        } else if (checkRange(50, 60, percent)) {
            headerBinding.batteryIcon.setImageResource(R.drawable.battery_50);
        } else if (checkRange(60, 70, percent)) {
            headerBinding.batteryIcon.setImageResource(R.drawable.battery_60);
        } else if (checkRange(70, 80, percent)) {
            headerBinding.batteryIcon.setImageResource(R.drawable.battery_70);
        } else if (checkRange(80, 90, percent)) {
            headerBinding.batteryIcon.setImageResource(R.drawable.battery_80);
        } else if (checkRange(90, 100, percent)) {
            headerBinding.batteryIcon.setImageResource(R.drawable.battery_90);
        } else if (percent >= 100) {
            headerBinding.batteryIcon.setImageResource(R.drawable.battery_100);
        }
    }

    private void setupWifiLevel() {
        try {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int numberOfLevels = 5;
            int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
            if (wifiManager.isWifiEnabled()) {
                if (DeviceUtils.isNetworkAvailable(this)) {
                    if (level == 0) {
                        headerBinding.wifi.setImageResource(R.drawable.wifi_off_outline_svgrepo_com);
                    } else if (level == 1) {
                        headerBinding.wifi.setImageResource(R.drawable.wifi_icon_1);
                    } else if (level == 2) {
                        headerBinding.wifi.setImageResource(R.drawable.wifi_icon_2);
                    } else if (level == 3) {
                        headerBinding.wifi.setImageResource(R.drawable.wifi_icon_3);
                    } else if (level == 4) {
                        headerBinding.wifi.setImageResource(R.drawable.wifi_icon_full);
                    }
                } else {
                    headerBinding.wifi.setImageResource(R.drawable.wifi_off_outline_svgrepo_com);
                }

            } else if (!wifiManager.isWifiEnabled()) {
                headerBinding.wifi.setImageResource(R.drawable.wifi_off_outline_svgrepo_com);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void handleBluetooth() {
        BluetoothAdapter bluetoothadapter = BluetoothAdapter.getDefaultAdapter();
        try {
            if (bluetoothadapter!=null&&bluetoothadapter.isEnabled()) {
                headerBinding.bluetooth.setVisibility(View.VISIBLE);
            } else {
                headerBinding.bluetooth.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void registerStatusBarService() {
        IntentFilter filter;
        filter = new IntentFilter();

        //battery
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        filter.addAction(Intent.ACTION_BATTERY_OKAY);
        filter.addAction(BatteryManager.ACTION_CHARGING);
        filter.addAction(BatteryManager.ACTION_DISCHARGING);

        //wifi
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);

        //bluetooth
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        //air plan mode
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);


        Intent registered = this.registerReceiver(statusReceiver, filter);

        //battery status
        //https://developer.android.com/training/monitoring-device-state/battery-monitoring.html
        setupBatteryLevel(registered);

        //wifi status
        setupWifiLevel();

        //bluetooth status
        handleBluetooth();

    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        performDependencyInjection(getBuildComponent());
        super.onCreate(savedInstanceState);
        performDataBinding();
        updateCurrentAcitivity();

        viewModel.setToken(token);
        viewModel.setDeviceId(deviceId);
        viewModel.mIsLoading.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback(){

            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if(((ObservableBoolean)sender).get()){
                    showProgressbar(getResources().getString(R.string.msg_loading));
                }else{
                    hideProgress();
                }
            }
        });
        viewModel.mErrorMessage.observe(this, toastMessage -> {
            if(toastMessage!=null){
                toastMessage.showMessage(getApplicationContext());
            }
        });
        viewModel.progressBarMsg.observe(this, progressBarMsg ->{
            if (progressBarMsg != null){
                changeProgressBarMsg(progressBarMsg);
            }
        });
        filterGlobalApplication = new IntentFilter();
        filterGlobalApplication.addAction(Constants.ACTION_EXPIRED_TOKEN);
        globalApplicationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action==null){
                    return;
                }
                if (action.equals(Constants.ACTION_EXPIRED_TOKEN)){
                    doExpireSession();
                }
            }
        };



        // setting header
        setHeader();
    }
    LayoutUiHeaderBinding headerBinding;
    private void setHeader(){
        if (showHeader()){
            headerBinding = DataBindingUtil.getBinding(viewBinding.getRoot().findViewById(R.id.root_header));
            if (headerBinding != null){
                headerBinding.setLeftTitle(leftTitle);
                headerBinding.setIconTitle(iconTitle);
                headerBinding.setCenterTitle(centerTitle);
                headerBinding.setPrinterId(printerId);
                registerStatusBar();
            }
        }
    }
    private void registerStatusBar(){
        registerStatusBarService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(statusReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(globalApplicationReceiver, filterGlobalApplication);
        updateCurrentAcitivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(globalApplicationReceiver);
    }

    public abstract @LayoutRes int getLayoutId();

    public abstract int getBindingVariable();

    public void doExpireSession() {
        //implement later

    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void performDataBinding() {
        viewBinding = DataBindingUtil.setContentView(this, getLayoutId());
        viewBinding.setVariable(getBindingVariable(), viewModel);
        viewBinding.executePendingBindings();
    }

    public void showProgressbar(String msg){
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        progressDialog = DialogUtils.createDialogLoading(this, msg);
        progressDialog.show();
    }

    public void changeProgressBarMsg(String msg){
        if (progressDialog != null){
            ((TextView)progressDialog.findViewById(R.id.progressbar_msg)).setText(msg);
        }
    }

    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    private ActivityComponent getBuildComponent() {
        return DaggerActivityComponent.builder()
                .appComponent(((MVVMApplication)getApplication()).getAppComponent())
                .activityModule(new ActivityModule(this))
                .build();
    }

    public abstract void performDependencyInjection(ActivityComponent buildComponent);

    private void updateCurrentAcitivity(){
        MVVMApplication mvvmApplication = (MVVMApplication)application;
        mvvmApplication.setCurrentActivity(this);
    }

    public boolean showHeader(){
        return false;
    }

    ObservableField<String> leftTitle = new ObservableField<>();
    ObservableField<Bitmap> iconTitle = new ObservableField<>();
    ObservableField<String> printerId = new ObservableField<>();
    ObservableField<String> centerTitle;
    public void setCenterTitle(String msg){
        if (centerTitle == null){
            centerTitle = new ObservableField<>(msg);
        } else {
            centerTitle.set(msg);
        }
    }
    public void setLeftTitle(String msg, Bitmap icon, String printer){
        leftTitle.set(msg);
        iconTitle.set(icon);
        printerId.set(printer);
    }

    @Override
    public void onConnectionClosed() {

    }

    @Override
    public void onConnectionClosing() {

    }

    @Override
    public void onMessageReceived(Message message) {
        viewModel.messageReceived(message);
    }

    @Override
    public void onConnectionFailed() {

    }

    @Override
    public void onConnectionOpened() {

    }

    protected void showConnectionError(){
        networkDialog = new Dialog(this);
        DialogConnectionErrorBinding binding = DialogConnectionErrorBinding.inflate(getLayoutInflater());

        binding.executePendingBindings();
        binding.btnRetry.setOnClickListener(view ->{
            if(viewModel instanceof MainViewModel){
                ((MVVMApplication) application).deleteSocket();
                ((MainViewModel) viewModel).checkDeviceId(deviceId);
            }
        });
        binding.setConfigured(viewModel.serverConfigured());
        binding.btnSetting.setOnClickListener(view ->{
            KeyboardDialog keyboardDialog = new KeyboardDialog();
            keyboardDialog.show(getSupportFragmentManager(),"keyboard");
        });
        if (networkDialog.getWindow() != null) {
            networkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        networkDialog.setContentView(binding.getRoot());
        networkDialog.setCanceledOnTouchOutside(false);
        networkDialog.show();

    }
}
