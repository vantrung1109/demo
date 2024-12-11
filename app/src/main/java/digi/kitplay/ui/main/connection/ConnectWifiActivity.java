package digi.kitplay.ui.main.connection;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.Observable;
import androidx.recyclerview.widget.LinearLayoutManager;

import digi.kitplay.BR;
import digi.kitplay.R;
import digi.kitplay.databinding.ActivityConnectWifiBinding;
import digi.kitplay.databinding.LayoutWifiKeyboardBinding;
import digi.kitplay.di.component.ActivityComponent;
import digi.kitplay.ui.base.activity.BaseActivity;
import digi.kitplay.ui.main.adapter.WifiAdapter;
import digi.kitplay.ui.main.config.ConfigServerActivity;
import digi.kitplay.utils.DeviceUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

import timber.log.Timber;

public class ConnectWifiActivity extends BaseActivity<ActivityConnectWifiBinding, ConnectWifiViewModel>
implements View.OnClickListener {

    @Override
    public int getLayoutId() {
        return R.layout.activity_connect_wifi;
    }

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    public void performDependencyInjection(ActivityComponent buildComponent) {
        buildComponent.inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DeviceUtils.checkWifiEnable(this)) {
            viewModel.isWifiEnable.postValue(true);
        }
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        viewBinding.setA(this);
        viewBinding.setVm(viewModel);
        viewBinding.setLifecycleOwner(this);
        viewModel.mWifiStatus.observe(this, aBoolean -> {
            // do nothing
        });
        registerWifiScanReceiver();

        viewBinding.switchState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    if(viewModel.isWifiEnable.getValue()){
                        return;
                    }
                    onWifi();
                }else {
                    if(!viewModel.isWifiEnable.getValue()){
                        return;
                    }
                    offWifi();
                }
            }
        });

    }

    private void onWifi(){
        DeviceUtils.onOffWifi(this, true);
        viewModel.isWifiEnable.postValue(true);
    }

    private void offWifi(){
        DeviceUtils.onOffWifi(this, false);
        viewModel.isWifiEnable.postValue(false);
    }

    private boolean isRegistered = false;
    WifiManager wifiManager;
    private List<ScanResult> scanResults;
    private boolean lock = false;
    private final BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {

        private void updateDialogState(NetworkInfo.DetailedState state) {
            if (lock) {
               switch (state) {
                    case CONNECTING:
                        viewModel.changeProgressBarMsg(getString(R.string.connecting));
                        break;
                    case AUTHENTICATING:
                        viewModel.changeProgressBarMsg(getString(R.string.authenticating));
                        break;
                    case OBTAINING_IPADDR:
                        viewModel.changeProgressBarMsg(getString(R.string.optaining_id));
                        break;
                    case CONNECTED:
                        navigateConfig();
                        viewModel.changeProgressBarMsg(getString(R.string.connect_success));
                        viewModel.hideLoading();
                        lock = false;
                        break;
                    case FAILED:
                        viewModel.changeProgressBarMsg(getString(R.string.connect_failed));
                        viewModel.hideLoading();
                        lock = false;
                        break;
                    default:
                        break;
                }
            }
        }

        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                scanResults = wifiManager.getScanResults();
                scanResults = scanResults.stream().distinct().filter(r -> r.SSID!=null && !r.SSID.equals("")).collect(Collectors.toList());
                if (!lock){
                    if (success) {
                        scanSuccess();
                    } else {
                        scanFailure();
                    }
                }
            } else {
                NetworkInfo networkInfo = (NetworkInfo) intent.getExtras().get("networkInfo");
                if(networkInfo!=null){
                    updateDialogState(networkInfo.getDetailedState());
                }
            }


        }
    };

    private void registerWifiScanReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        isRegistered = true;
        this.registerReceiver(wifiScanReceiver, intentFilter);
        boolean success = wifiManager.startScan();
    }

    private void unRegisterWifiScanReceiver() {
        Timber.d("unRegisterWifiScanReceiver");
        isRegistered = false;
        unregisterReceiver(wifiScanReceiver);

    }

    private void scanSuccess() {
        Timber.d("Scan success");
        if (adapter == null){
            viewModel.mWifiStatus.setValue(true);
            initializeAdapter();
        }
    }

    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        Timber.d("scan failure");
        if (adapter != null){
            updateAdapter();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isRegistered) {
            unRegisterWifiScanReceiver();
            isRegistered = false;
        }
    }

    private void updateAdapter() {
        if (scanResults != null){
            Timber.d("update adapter");
            adapter.setWifiList(scanResults);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegistered) {
            unRegisterWifiScanReceiver();
            isRegistered = false;
        }
    }

    WifiAdapter adapter;

    private void initializeAdapter() {
        Timber.d("initialize adapter");
        viewBinding.rc.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WifiAdapter(scanResults, (ssid, type) -> {
            viewModel.mCurrentSSID = ssid;
            viewModel.mCurrentType = type;
            if (type){
                showKeyboard();
            } else {
                connectToWifi(ConnectWifiActivity.this, viewModel.mCurrentSSID,"",false);
            }
        });
        viewBinding.rc.setAdapter(adapter);
    }

    private void showKeyboard(){
        Dialog dialog = new Dialog(this);
        LayoutWifiKeyboardBinding layoutWifiKeyboardBinding = LayoutWifiKeyboardBinding.inflate(getLayoutInflater());
        layoutWifiKeyboardBinding.setPassword(viewModel.mPassword);
        layoutWifiKeyboardBinding.setVm(viewModel);
        viewModel.isVisibility.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                int currentCursor = layoutWifiKeyboardBinding.edtPw.getSelectionStart();
                if(!viewModel.isVisibility.get()){

                    layoutWifiKeyboardBinding.edtPw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    layoutWifiKeyboardBinding.edtPw.setSelection(currentCursor);
                }else {
                    layoutWifiKeyboardBinding.edtPw.setTransformationMethod(null);
                    layoutWifiKeyboardBinding.edtPw.setSelection(currentCursor);
                }
            }
        });
        layoutWifiKeyboardBinding.executePendingBindings();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        layoutWifiKeyboardBinding.txtWifi.setText("Kết nối với "+viewModel.mCurrentSSID);
        layoutWifiKeyboardBinding.btnConnect.setOnClickListener(view -> {
            lock = true;
            viewModel.saveWifiInfo();
            connectToWifi(ConnectWifiActivity.this, viewModel.mCurrentSSID, viewModel.mPassword.get(),false);
            viewModel.mPassword.set("");
            dialog.dismiss();
        });
        layoutWifiKeyboardBinding.btnCancel.setOnClickListener(view -> {
            lock = false;
            dialog.dismiss();
        });
        dialog.setContentView(layoutWifiKeyboardBinding.getRoot());
        dialog.show();

    }

    public void connectToWifi(Context context, String ssid, String password, boolean removeOther) {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        if (removeOther) {
            @SuppressLint("MissingPermission") List<WifiConfiguration> listOld = wifiManager.getConfiguredNetworks();
            if (listOld != null) {
                for (WifiConfiguration item : listOld) {
                    wifiManager.removeNetwork(item.networkId);
                }
            }

        }
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = String.format("\"%s\"", ssid);
        if (password.equals("")) {
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else {
            conf.preSharedKey = String.format("\"%s\"", password);
        }
        int netId = wifiManager.addNetwork(conf);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
        viewModel.showLoading();
    }

    private void navigateConfig(){
        finish();
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.back:
//                finish();
//                break;
//            default:
//                break;
//        }
    }


    public void back() {
        finish();
    }

    @Override
    public boolean showHeader() {
//        setLeftTitle("--");
//        setCenterTitle("--");
        return true;
    }
}
