package digi.kitplay.ui.main;

import android.os.Handler;
import android.os.Looper;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Random;

import digi.kitplay.BuildConfig;
import digi.kitplay.MVVMApplication;
import digi.kitplay.constant.Constants;
import digi.kitplay.data.Repository;
import digi.kitplay.data.download.DownloadAPI;
import digi.kitplay.data.download.DownloadProgressListener;
import digi.kitplay.data.model.api.ApiModelUtils;
import digi.kitplay.data.model.api.response.CheckUpdateResponse;
import digi.kitplay.data.model.api.response.SocketResponse;
import digi.kitplay.data.model.db.ActionEntity;
import digi.kitplay.data.model.db.UserEntity;
import digi.kitplay.data.socket.Command;
import digi.kitplay.data.socket.dto.Device;
import digi.kitplay.data.socket.dto.Message;
import digi.kitplay.ui.base.activity.BaseCallback;
import digi.kitplay.ui.base.activity.BaseViewModel;
import digi.kitplay.utils.FileUtils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class MainViewModel extends BaseViewModel {
    public boolean checkForUpdate = false;
    public int currentPage;
    public ObservableInt observable = new ObservableInt();
    public ObservableInt socketTick = new ObservableInt();
    public androidx.databinding.Observable.OnPropertyChangedCallback callback;

    public MutableLiveData<Boolean> mNetworkStatus = new MutableLiveData<>();
    public MutableLiveData<Boolean> mProgressStatus = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> mUpdateStatus = new MutableLiveData<>(false);
    public ObservableInt progress = new ObservableInt(0);
    public Integer deviceRotationState = 0;
    public CheckUpdateResponse checkUpdateResponse;
    public String fileApkUpdate;
    public ObservableField<String> ip = new ObservableField<>("");
    public ObservableField<String> port = new ObservableField<>("");
    public ObservableField<Boolean> isSettingVisible = new ObservableField<>(false);
    public ObservableField<Integer> orderCount = new ObservableField<>(0);
    public ObservableField<Integer> foodCount = new ObservableField<>(0);
    public ObservableField<String> md5 = new ObservableField<>();
    public ObservableField<String> subMd5 = new ObservableField<>();


    public MutableLiveData<List<ActionEntity>> actionsLiveData = new MutableLiveData<>();


    public MainViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }


    public void dispose() {
        compositeDisposable.clear();
    }

    public void checkUpdate(BaseCallback callback) {
        compositeDisposable.add(
                repository.getApiService().checkUpdate(BuildConfig.UPDATE_URL)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (response != null) {
                                checkUpdateResponse = response;
                                callback.doSuccess();
                            } else {
                                callback.doFail();
                            }
                        }, callback::doError)
        );
    }

    public void downloadApk(BaseCallback callback, DownloadProgressListener listener, String url) {

        compositeDisposable.add(new DownloadAPI(Constants.SHOP_BASE_URL, listener).downloadAPK(url)
                .subscribeOn(Schedulers.io())
                .doOnNext(responseBody -> FileUtils.writeResponseBodyToDisk(responseBody, fileApkUpdate))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    //do update
                    callback.doSuccess();
                }, callback::doError));
    }


    public String getSsid() {
        return repository.getSsid();
    }

    public void setSSid(String sSid) {
        repository.setSsid(sSid);
    }

    public String getSavedPort() {
        return repository.getPort();
    }

    public String getSavedIp() {
        return repository.getIp();
    }

    public void checkDeviceId(String deviceId) {
        showLoading();
        compositeDisposable.add(
                repository.getApiService().checkDeviceId(new Device(deviceId))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> {
                                    if (response.isResult() && response.getData() != null) {
                                        SocketResponse socketResponse = response.getData();
                                        String wsUrl = "ws://" + socketResponse.getIpSocket() + ":" + socketResponse.getPortSocket() + "/ws";
                                        application.createSocket(wsUrl);
                                    } else {
                                        showErrorMessage(response.getMessage());
                                        hideLoading();
                                    }
                                }, throwable -> {
                                    Timber.e(throwable);
                                    hideLoading();
                                }
                        ));
    }

    public void pushAction(ActionEntity action) {
        compositeDisposable.add(
                repository.getSqLiteService().insertAction(action)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(insertedId -> {
                            Timber.tag("ActionLiveData").e("Inserted id: %d", insertedId);
                        }, throwable -> {
                        })
        );
    }

    public void popAction(ActionEntity action) {
        compositeDisposable.add(
                repository.getSqLiteService().deleteAction(action)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(isDeleted -> {
                            Timber.tag("ObserveAction").e("Deleted: %b", isDeleted);
                        }, throwable -> {
                        })
        );
    }

    public void processNextAction(MainCallback callback) {
        compositeDisposable.add(
                repository.getSqLiteService().getNextPendingAction()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(actionEntity -> {
                            repository.getSqLiteService().updateActionStatus(actionEntity.getId(), 1); // IN_PROGRESS
                            // Gọi API
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                // Sử dụng Random để xác định thành công hoặc thất bại
                                boolean isSuccess = new Random().nextBoolean();
                                if (isSuccess) {
                                    Timber.tag("MockAPI").e("Action %s sent successfully", actionEntity.getId());
                                    repository.getSqLiteService().updateActionStatus(actionEntity.getId(), 2);
                                    callback.doSuccess(actionEntity); // Thành công
                                } else {
                                    Timber.tag("MockAPI").e("Action %s failed to send", actionEntity.getId());
                                    repository.getSqLiteService().updateActionStatus(actionEntity.getId(), 0);
                                    callback.doFail(); // Thất bại
                                }
                            }, 2000); // Giả lập thời gian chờ 2 giây
                        }, throwable -> {
                            Timber.e(throwable, "No pending actions to process");
                        })
        );
    }

    public void observeActions() {
        repository.getSqLiteService().loadAllActionsToLiveData()
                .observeForever(actionEntities -> {
                    actionsLiveData.setValue(actionEntities);
                });
    }

}
