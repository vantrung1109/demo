package digi.kitplay.ui.main.connection;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import digi.kitplay.MVVMApplication;
import digi.kitplay.data.Repository;
import digi.kitplay.ui.base.activity.BaseViewModel;
import timber.log.Timber;

public class ConnectWifiViewModel extends BaseViewModel {
    public String mCurrentSSID;
    public boolean mCurrentType;
    public ObservableField<String> mPassword = new ObservableField<>("");
    public MutableLiveData<Boolean> mWifiStatus = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isWifiEnable = new MutableLiveData<>(false);
    public ObservableField<Boolean> isVisibility = new ObservableField<>(false);
    public ConnectWifiViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);

    }
    public void setIsVisibilityPassword(){
        isVisibility.set(Boolean.FALSE.equals(isVisibility.get()));
    }
    public void saveWifiInfo(){
        repository.setWifiPassword(mPassword.get());
        repository.setSsid(mCurrentSSID);
        Timber.d("Save wifi info, ssid %s, pwd %s",mCurrentSSID,mPassword.get());
    }
}
