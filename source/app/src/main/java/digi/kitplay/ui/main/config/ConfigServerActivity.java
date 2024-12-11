package digi.kitplay.ui.main.config;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.databinding.Observable;

import digi.kitplay.BR;
import digi.kitplay.R;
import digi.kitplay.databinding.ActivityConfigServerBinding;
import digi.kitplay.di.component.ActivityComponent;
import digi.kitplay.ui.base.activity.BaseActivity;

import digi.kitplay.ui.main.MainActivity;
import digi.kitplay.ui.main.connection.ConnectWifiActivity;
import digi.kitplay.utils.DeviceUtils;
import timber.log.Timber;

public class ConfigServerActivity extends BaseActivity<ActivityConfigServerBinding, ConfigServerViewModel> {

    private ActivityResultLauncher<Intent> connectWifiLauncher;
    @Override
    public int getLayoutId() {
        return R.layout.activity_config_server;
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
        viewBinding.setA(this);
        viewBinding.setVm(viewModel);
        viewBinding.setLifecycleOwner(this);

        viewBinding.back.setOnClickListener(v -> back());

        connectWifiLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        });
    }


    public void save(){
        viewModel.saveServerIpPort();
        finish();
    }

    public void navigateToWifi(){
        Intent it = new Intent(this, ConnectWifiActivity.class);
        startActivity(it);
    }

    public void back(){
        finish();
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
//            if (!DeviceUtils.checkWifiEnable(this)) {
//                DeviceUtils.onOffWifi(this, true);
//            }
            navigateToWifi();
        }

    }

    private void navigateToConfigServer() {
        Intent it = new Intent(this, ConfigServerActivity.class);
        startActivity(it);
    }

}
