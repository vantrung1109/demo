package digi.kitplay.ui.base.activity;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import digi.kitplay.MVVMApplication;
import digi.kitplay.data.Repository;
import digi.kitplay.data.model.other.ToastMessage;

import digi.kitplay.data.socket.dto.Message;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import lombok.Setter;

public class BaseViewModel extends ViewModel {
    protected CompositeDisposable compositeDisposable;
    protected final ObservableBoolean mIsLoading = new ObservableBoolean();
    protected final MutableLiveData<String> progressBarMsg = new MutableLiveData<>();
    protected final MutableLiveData<ToastMessage> mErrorMessage = new MutableLiveData<>();

    @Setter
    protected String token;

    @Setter
    protected String deviceId;

    protected final Repository repository;
    protected final MVVMApplication application;

    public BaseViewModel(Repository repository, MVVMApplication application){
        this.repository = repository;
        this.application = application;
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }

    public void showLoading(){
        mIsLoading.set(true);
    }

    public void hideLoading(){
        mIsLoading.set(false);
    }

    public void showSuccessMessage(String message){
        mErrorMessage.setValue(new ToastMessage(ToastMessage.TYPE_SUCCESS,message));
    }

    public void showNormalMessage(String message){
        mErrorMessage.setValue(new ToastMessage(ToastMessage.TYPE_NORMAL,message));
    }

    public void showWarningMessage(String message){
        mErrorMessage.setValue(new ToastMessage(ToastMessage.TYPE_WARNING,message));
    }

    public void showErrorMessage(String message){
        mErrorMessage.setValue(new ToastMessage(ToastMessage.TYPE_ERROR,message));
    }

    public void changeProgressBarMsg(String message){
        progressBarMsg.setValue(message);
    }

    public void messageReceived(Message message){
    }
    public void sendMessage(Message message){
        application.sendMessage(message);
    }

    public Boolean serverConfigured(){
        return !repository.getPort().isEmpty() && !repository.getIp().isEmpty();
    }
}
