package digi.kitplay.ui.base.fragment;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import digi.kitplay.MVVMApplication;
import digi.kitplay.data.Repository;
import digi.kitplay.data.model.other.ToastMessage;

import lombok.Setter;

public class BaseFragmentViewModel extends ViewModel {

    protected final Repository repository;
    protected final MVVMApplication application;
    protected final MutableLiveData<ToastMessage> mErrorMessage = new MutableLiveData<>();
    protected final ObservableBoolean mIsLoading = new ObservableBoolean();


    @Setter
    protected String token;

    public BaseFragmentViewModel(Repository repository, MVVMApplication application) {
        this.repository = repository;
        this.application = application;
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
    public void showLoading(){
        mIsLoading.set(true);
    }

    public void hideLoading(){
        mIsLoading.set(false);
    }
}
