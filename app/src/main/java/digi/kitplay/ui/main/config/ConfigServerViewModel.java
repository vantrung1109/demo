package digi.kitplay.ui.main.config;

import android.util.Log;

import androidx.databinding.ObservableField;

import digi.kitplay.MVVMApplication;
import digi.kitplay.data.Repository;
import digi.kitplay.ui.base.activity.BaseViewModel;

import timber.log.Timber;

public class ConfigServerViewModel extends BaseViewModel {

    public ObservableField<String> serverIp = new ObservableField<>("");
    public ObservableField<String> serverPort = new ObservableField<>("");
    public ObservableField<String> rabbitMqPort = new ObservableField<>("");
    public ObservableField<String> rabbitMqUsername = new ObservableField<>("");
    public ObservableField<String> rabbitMqPassword = new ObservableField<>("");
    public ObservableField<Boolean> isVisibility = new ObservableField<>(false);
    public ConfigServerViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void setIsVisibilityPassword(){
        isVisibility.set(Boolean.FALSE.equals(isVisibility.get()));
    }

    public void saveServerIpPort(){
        repository.setIp(serverIp.get());
        repository.setPort(serverPort.get());
        repository.getSharedPreferences().setRabbitmqPort(rabbitMqPort.get());
        repository.getSharedPreferences().setRabbitmqUsername(rabbitMqUsername.get());
        repository.getSharedPreferences().setRabbitmqPassword(rabbitMqPassword.get());
        Timber.tag("TAG").d("saveServerIpPort: " + serverIp.get() + "/" + serverPort.get());
    }

}
