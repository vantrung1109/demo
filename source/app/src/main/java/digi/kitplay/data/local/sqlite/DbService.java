package digi.kitplay.data.local.sqlite;

import androidx.lifecycle.LiveData;


import java.util.List;

import digi.kitplay.data.model.db.ActionEntity;
import digi.kitplay.data.model.db.UserEntity;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface DbService {
    Observable<List<ActionEntity>> getAllDbAction();
    LiveData<List<ActionEntity>> loadAllActionsToLiveData();
    LiveData<List<ActionEntity>> getActionsByStatus(String status);
    Observable<Long> insertAction(ActionEntity action);
    Observable<Boolean> updateActionStatus(Long id, Integer newStatus);
    Observable<ActionEntity> getNextPendingAction();
    Observable<Boolean> deleteAction(ActionEntity action);
    Observable<ActionEntity> findActionById(Long id);

}
