package digi.kitplay.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.SparseIntArray;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.pm.PackageInfoCompat;

import digi.kitplay.BR;
import digi.kitplay.BuildConfig;
import digi.kitplay.MVVMApplication;
import digi.kitplay.R;
import digi.kitplay.data.AppRepository;
import digi.kitplay.data.Repository;
import digi.kitplay.data.download.Download;
import digi.kitplay.data.download.DownloadProgressListener;
import digi.kitplay.data.local.sqlite.AppDbService;
import digi.kitplay.data.model.api.response.CheckUpdateResponse;
import digi.kitplay.data.model.db.ActionEntity;
import digi.kitplay.databinding.ActivityMainBinding;
import digi.kitplay.databinding.LayoutSocketDisconnectedBinding;
import digi.kitplay.di.component.ActivityComponent;
import digi.kitplay.ui.base.activity.BaseActivity;
import digi.kitplay.ui.base.activity.BaseCallback;
import digi.kitplay.ui.main.connection.ConnectWifiActivity;
import digi.kitplay.ui.main.dialog.KeyboardDialog;
import digi.kitplay.ui.main.layout.HeightProvider;
import digi.kitplay.ui.main.layout.TVRootLayout;
import digi.kitplay.utils.DeviceUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import digi.kitplay.utils.SnowFlakeIdService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import timber.log.Timber;


public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> implements TVRootLayout.AdminEventListener
, KeyboardDialog.KeyboardListener, View.OnClickListener {

    private final SparseIntArray map = new SparseIntArray();
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    final String[] PERMISSIONS = {
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_WIFI_STATE,
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private ActivityResultLauncher<String[]> multiplePermissionActivityResultLauncher;
    private ActivityResultLauncher<Intent> connectWifiLauncher;
    private final ActivityResultLauncher<Intent> installApkLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK){
            viewModel.showSuccessMessage(getString(R.string.update_success));
        } else {
            viewModel.showErrorMessage(getString(R.string.update_fail));
        }
        hideProgressBar();
        if (viewModel.mUpdateStatus.getValue()){
            viewModel.mUpdateStatus.setValue(false);
        }
        viewModel.hideLoading();

    });

    private boolean inBackground = false;
    private boolean lockScan = false;
    //
    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // lay ssid tu share ref
        super.onCreate(savedInstanceState);
        setupHeader();

        viewBinding.btnPushAction.setOnClickListener(v -> {
            ActionEntity actionEntity = new ActionEntity();
            actionEntity.setId(SnowFlakeIdService.getInstance().nextId());
            actionEntity.setDescription("Action " + System.currentTimeMillis());
            actionEntity.setStatus(0);
            actionEntity.setTimestamp(System.currentTimeMillis());
            viewModel.pushAction(actionEntity);
        });

        viewModel.observeActions();
        observeAndProcessActions();

    }

    public void observeAndProcessActions() {
        viewModel.actionsLiveData.observeForever(actionEntities -> {
            // Kiểm tra xem có hành động nào cần xử lý không
            boolean hasPendingAction = false;
            for (int i = 0; i < actionEntities.size(); i++) {
                long timestamp = actionEntities.get(i).getTimestamp();
                int status = actionEntities.get(i).getStatus();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = dateFormat.format(new Date(timestamp));
                Timber.tag("ObserveAction").e("Action : Timestamp: %s, Status: %d", formattedDate, status);
            }
            for (ActionEntity action : actionEntities) {
                if (action.getStatus() == 0) { // Nếu có hành động PENDING
                    hasPendingAction = true;
                    break;
                }
            }

            if (hasPendingAction) {
                // Nếu còn hành động PENDING, bắt đầu xử lý
                viewModel.processNextAction(new MainCallback<ActionEntity>() {
                    @Override
                    public void doError(Throwable error) {
                        Timber.tag("MockAPI").e("Error occurred: %s", error.getMessage());
                    }

                    @Override
                    public void doFail() {
                        Timber.tag("MockAPI").e("Action failed to send");
                        // Sau khi thất bại, tiếp tục kiểm tra hành động kế tiếp
                        viewModel.processNextAction(this);

                    }

                    @Override
                    public void doSuccess(ActionEntity actionEntity) {
                        Timber.tag("MockAPI").e("Action %s sent successfully", actionEntity.getId());
                        // Pop action và tiếp tục kiểm tra hành động kế tiếp
                        viewModel.popAction(actionEntity);
                    }

                    @Override
                    public void doSuccess() {
                        Timber.tag("MockAPI").e("Action sent successfully (generic)");
                    }
                });
            } else {
                Timber.tag("MockAPI").e("No pending actions left to process");
            }
        });
    }

    private void setupHeader() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        viewBinding.setMainActivity(this);
        viewBinding.root.requestFocus();
        (viewBinding.root).setListener(this);

        // apk path
        viewModel.fileApkUpdate = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + File.separator + "/download_app.apk";

        multiplePermissionActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),result -> {
            if (result.containsValue(false)){
                showRequestPermission();
            } else {
                registerToNetworkListener();
            }
        });
        connectWifiLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
            lockScan = false;
        });
        viewBinding.setLifecycleOwner(this);

        viewModel.mNetworkStatus.observe(this, aBoolean -> {
            if (!aBoolean){
                focusOnWifiButton();
                ((MVVMApplication) application).deleteSocket();
            } else {
                focusOnRootView();
            }
        });
        viewModel.mUpdateStatus.observe(this,aBoolean -> {
            if (aBoolean){
                showUpdate();
            } else {
                hideUpdateApp();
            }
        });

        checkAndDeleteApkFile();
        listenToKeyboard();
        askPermission();
    }

    public void checkAndDeleteApkFile(){
        Handler handler = new Handler();
        handler.post(() -> {
            File apk = new File(viewModel.fileApkUpdate);
            if (apk.exists()){
                Timber.d("apk file exist");
        if (apk.delete()){
            Timber.d("file Deleted :%s",viewModel.fileApkUpdate);
        } else {
            Timber.d("file not Deleted :%s",viewModel.fileApkUpdate);
        }
            }
        });

    }

    private HeightProvider heightProvider;
    private void listenToKeyboard(){
        heightProvider = new HeightProvider(this).init().setHeightListener(new HeightProvider.HeightListener() {
            @Override
            public void onHeightChanged(int height) {
//                viewBinding.settingsRoot.getRoot().setTranslationY(-height);
            }
        });
    }




    ConnectivityManager connectivityManager;
    ConnectivityManager.NetworkCallback networkCallback;
    private void registerToNetworkListener(){
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            final Handler handler = new Handler();

            connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
                hideLostWifi();
            }else {
                showLostWifi();
            };
            networkCallback = new ConnectivityManager.NetworkCallback() {
                final Runnable endCall = () -> {
                    // if execution has reached here - feel free to cancel the call
                    // because no connection was established in a second
                    Timber.d("lost connection");
                    showLostWifi();
                };

                public void onAvailable(Network network) {
                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (!wifiInfo.getSSID().replace("\"", "").equals(viewModel.getSsid()) && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && !inBackground) {
                        Timber.d("remove wifi network %s, saved %s", wifiInfo.getSSID(), viewModel.getSsid());
                        wifiManager.removeNetwork(wifiManager.getConnectionInfo().getNetworkId());
                    } else {
                        if (viewModel.mNetworkStatus.getValue()!= null && !viewModel.mNetworkStatus.getValue()) {
                            hideLostWifi();
                            if (!viewModel.checkForUpdate){
                                viewModel.checkForUpdate = true;
                                // TODO temporary disable this function
//                                checkForUpdate();
                            }
                        }
                    }
                    // we've got a connection, remove callbacks (if we have posted any)
                    handler.removeCallbacks(endCall);
                    Timber.d("available");
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    // Schedule an event to take place in a second
                    handler.postDelayed(endCall, 3000);

                }
            };
            connectivityManager.registerNetworkCallback(request, networkCallback);
    }

    private void unRegisterToNetworkListener(){
        if (networkCallback != null){
            connectivityManager.unregisterNetworkCallback(networkCallback);
            networkCallback = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        inBackground = true;
        Timber.d("#on pause");
        viewModel.dispose();
    }

    @Override
    protected void onResume() {
        inBackground = false;
        super.onResume();
        Timber.d("#on resume");
//        viewModel.startSocketTick();
    }

    @Override
    protected void onDestroy() {
        Timber.d("#on destroy");
        super.onDestroy();
        unRegisterToNetworkListener();
        heightProvider.dismiss();
    }

    private void showLostWifi() {
        viewModel.mNetworkStatus.postValue(false);
    }
    private void hideLostWifi(){
        viewModel.mNetworkStatus.postValue(true);
    }

    private void askPermission() {
        if (!hasPermissions(PERMISSIONS)){
            multiplePermissionActivityResultLauncher.launch(PERMISSIONS);
        } else {
            registerToNetworkListener();
        }
    }
    private boolean hasPermissions(String[] permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    Timber.d("Permission is not granted: %s", permission);
                    return false;
                }
                Timber.d("Permission already granted: %s",permission);
            }
            return true;
        }
        return false;
    }
    private void showRequestPermission(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.permission_request)
                .setMessage(R.string.permission_request_message)
                .setPositiveButton("OK", (dialogInterface, i) -> finish()).show();
    }

    private void focusOnWifiButton() {
        viewBinding.noConnection.setFocusable(true);
        viewBinding.root.setFocusable(false);
        Timber.d("wifi view request focus");
        viewBinding.noConnection.requestFocus();
    }
    private void focusOnRootView(){
        viewBinding.noConnection.setFocusable(false);
        viewBinding.root.setFocusable(true);
        Timber.d("root view request focus");
        viewBinding.root.requestFocus();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public int getBindingVariable() {
        return BR.mainViewModel;
    }

    @Override
    public void performDependencyInjection(ActivityComponent buildComponent) {
        buildComponent.inject(this);
    }

    @Override
    public void onAdminEvent() {
        KeyboardDialog keyboardDialog = new KeyboardDialog();
        keyboardDialog.show(getSupportFragmentManager(),"keyboard");
        if(Boolean.TRUE.equals(viewModel.isSettingVisible.get())){
//            animationOpenOrCloseSetting();
        }
    }

    @Override
    public void login(String code) {
        if (code.equals("123456")){
            connectToWifi();
        }
    }
    public void connectToWifi(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // android 10
            if(DeviceUtils.isTV(getApplicationContext())){
                Intent it = new Intent(Settings.ACTION_WIFI_SETTINGS);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                connectWifiLauncher.launch(it);
            } else{

                Intent it = new Intent(Settings.Panel.ACTION_WIFI);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                connectWifiLauncher.launch(it);
            }
        } else {
          navigateToWifi();
        }

    }

    public void navigateToWifi(){
        Intent it = new Intent(this, ConnectWifiActivity.class);
        startActivity(it);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.no_connection:
                if (!lockScan){
                    lockScan = true;
                    connectToWifi();
                }
                break;
            case R.id.wifi:
                openWifiSetting();
                break;
            case R.id.update_btn:
                downloadApkFile(viewModel.checkUpdateResponse.getUrl());
                break;
            default:
                break;
        }
    }
    private void closeSettings(){
        Timber.d("ip %s:, port %s:",viewModel.ip.get(),viewModel.port.get());
        Timber.d("update port and ip");
        DeviceUtils.hideSoftKeyboard(this);
//        viewModel.mSettingsStatus.setValue(false);
    }

    private void rotateDevice(){
        try {
            int currentScreenOrientation = viewModel.deviceRotationState;
            if (currentScreenOrientation == 0){
//                setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                Settings.System.putInt(getContentResolver(), Settings.System.USER_ROTATION, 1);
                viewModel.deviceRotationState = 1;

            } else if (currentScreenOrientation == 1){
//                setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                Settings.System.putInt(getContentResolver(), Settings.System.USER_ROTATION, 2);
                viewModel.deviceRotationState = 2;
            } else if (currentScreenOrientation == 2){
                Settings.System.putInt(getContentResolver(), Settings.System.USER_ROTATION, 3);
                viewModel.deviceRotationState = 3;
            } else if (currentScreenOrientation == 3){
                Settings.System.putInt(getContentResolver(), Settings.System.USER_ROTATION, 0);
                viewModel.deviceRotationState = 0;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void updateApplication(){
        if (viewModel.mProgressStatus.getValue()){
            viewModel.showErrorMessage(getString(R.string.updating));
        } else {
            checkForUpdate();
        }
    }
    private void hideUpdateApp(){
        viewBinding.root.setFocusable(true);
        viewBinding.root.requestFocus();
        viewBinding.updateRoot.updateBtn.setFocusable(false);
    }
    private void showUpdate(){
        viewBinding.updateRoot.updateBtn.setFocusable(true);
        viewBinding.root.setFocusable(false);
        Timber.d("update request focus");
        viewBinding.updateRoot.updateBtn.requestFocus();
    }

    private void checkForUpdate() {

        viewModel.checkUpdate(new BaseCallback() {
            @Override
            public void doError(Throwable error) {

            }

            @Override
            public void doSuccess() {
                try {
                    long currentVersionCode = PackageInfoCompat.getLongVersionCode(getPackageManager().getPackageInfo(getPackageName(), 0));

                    if(needUpdate(currentVersionCode, viewModel.checkUpdateResponse) && !viewModel.mUpdateStatus.getValue()){
//                         tai apk
                        Timber.d("Download apk");
                        downloadApkFile(viewModel.checkUpdateResponse.getUrl());
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void doFail() {

            }
        });
    }
    // Tai apk
    private void downloadApkFile(String url){
        Timber.d(viewModel.fileApkUpdate);
        if (Boolean.TRUE.equals(viewModel.mUpdateStatus.getValue())){
            viewModel.showLoading();
        }
        DownloadProgressListener listener = new DownloadProgressListener() {
            int lastProgresss = 0;
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                Download download = new Download();
                download.setTotalFileSize(contentLength);
                download.setCurrentFileSize(bytesRead);
                int progress = (int) ((bytesRead * 100) / contentLength);
                download.setProgress(progress);
                if(progress > lastProgresss && progress%5 == 0){
                    lastProgresss = progress;
                    viewModel.progress.set(lastProgresss);
                }
            }
        };
        showProgressBar();
        viewModel.downloadApk(new BaseCallback() {
            @Override
            public void doError(Throwable error) {
                hideProgressBar();
                viewModel.showErrorMessage(getString(R.string.error));
                Timber.d(error);
            }

            @Override
            public void doSuccess() {
                hideProgressBar();
                installApk();
            }

            @Override
            public void doFail() {
                hideProgressBar();
                viewModel.showErrorMessage(getString(R.string.error));
            }
        },listener,url);
    }
    private void installApk(){
        if(viewModel.fileApkUpdate!=null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri downloaded_apk = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider",new File(viewModel.fileApkUpdate));
                Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(downloaded_apk,"application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                installApkLauncher.launch(intent);
            }else{
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(viewModel.fileApkUpdate)), "application/vnd.android.package-archive");
                installApkLauncher.launch(intent);
            }

        }
    }
    // kIem tra can update
    private boolean needUpdate(long currentVersionCode, CheckUpdateResponse checkUpdateResponse){
        return checkUpdateResponse.getCurrentVersion() != -1 && (currentVersionCode < checkUpdateResponse.getCurrentVersion());
    }


    private void showProgressBar(){
        viewModel.mProgressStatus.setValue(true);
    }

    private void hideProgressBar(){
        viewModel.mProgressStatus.setValue(false);
        viewModel.progress.set(0);
    }
    private void openWifiSetting(){
        connectToWifi();
    }

    @Override
    public boolean showHeader() {
        return true;
    }

    @Override
    public void onBackPressed() {

    }
    AlertDialog dialog;
    private void showDialog(){
        if (dialog == null){
            LayoutSocketDisconnectedBinding layoutSocketDisconnectedBinding = LayoutSocketDisconnectedBinding.inflate(getLayoutInflater());
            dialog = new AlertDialog.Builder(this)
                    .setView(layoutSocketDisconnectedBinding.getRoot())
                    .show();
            Timber.d("show socket dialog");
        }
    }

    @Override
    public void onConnectionOpened() {
        super.onConnectionOpened();

    }

    @Override
    public void onConnectionFailed() {
        super.onConnectionFailed();
    }



}
