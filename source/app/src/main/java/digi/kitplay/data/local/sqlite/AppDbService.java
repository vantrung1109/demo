package digi.kitplay.data.local.sqlite;

import digi.kitplay.data.model.db.ActionEntity;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;
import java.util.concurrent.Callable;
import androidx.lifecycle.LiveData;
import javax.inject.Inject;

import digi.kitplay.data.model.db.UserEntity;
import io.reactivex.rxjava3.core.Single;

public class AppDbService implements DbService {

    private final AppDatabase mAppDatabase;

    @Inject
    public AppDbService(AppDatabase appDatabase) {
        this.mAppDatabase = appDatabase;
    }

    //ActionEntity
    @Override
    public Observable<List<ActionEntity>> getAllDbAction() {
        return Observable.fromCallable(new Callable<List<ActionEntity>>() {
            @Override
            public List<ActionEntity> call() throws Exception {
                return mAppDatabase.getDbActionDao().loadAll();
            }
        });
    }

    @Override
    public LiveData<List<ActionEntity>> loadAllActionsToLiveData() {
        return mAppDatabase.getDbActionDao().loadAllToLiveData();
    }

    @Override
    public LiveData<List<ActionEntity>> getActionsByStatus(String status) {
        return mAppDatabase.getDbActionDao().getActionsByStatus(status);
    }

    @Override
    public Observable<Long> insertAction(ActionEntity action) {
        return Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return mAppDatabase.getDbActionDao().insert(action);
            }
        });
    }

//    @Override
//    public Observable<Boolean> updateAction(ActionEntity action) {
//        return Observable.fromCallable(new Callable<Boolean>() {
//            @Override
//            public Boolean call() throws Exception {
//                mAppDatabase.getDbActionDao().(action);
//                return true;
//            }
//        });
//    }

    @Override
    public Observable<Boolean> updateActionStatus(Long id, Integer status) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                mAppDatabase.getDbActionDao().updateActionStatus(id, status);
                return true;
            }
        });
    }

    @Override
    public Observable<ActionEntity> getNextPendingAction() {
        return Observable.fromCallable(new Callable<ActionEntity>() {
            @Override
            public ActionEntity call() throws Exception {
                return mAppDatabase.getDbActionDao().getNextAction();
            }
        });
    }

    @Override
    public Observable<Boolean> deleteAction(ActionEntity action) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                mAppDatabase.getDbActionDao().delete(action);
                return true;
            }
        });
    }

    @Override
    public Observable<ActionEntity> findActionById(Long id) {
        return Observable.fromCallable(new Callable<ActionEntity>() {
            @Override
            public ActionEntity call() throws Exception {
               return mAppDatabase.getDbActionDao().findById(id);
            }
        });
    }

}
