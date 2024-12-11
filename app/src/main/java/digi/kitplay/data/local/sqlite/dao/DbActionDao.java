package digi.kitplay.data.local.sqlite.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import digi.kitplay.data.model.db.ActionEntity;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface DbActionDao {
    @Query("SELECT * FROM db_actions WHERE status = :status")
    LiveData<List<ActionEntity>> getActionsByStatus(String status);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(ActionEntity entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ActionEntity> listEntity);

    @Query("SELECT * FROM db_actions")
    List<ActionEntity> loadAll();

    @Query("SELECT * FROM db_actions")
    LiveData<List<ActionEntity>> loadAllToLiveData();

    @Query("SELECT * FROM db_actions WHERE id=:id")
    ActionEntity findById(long id);

    @Delete
    void delete(ActionEntity entity);

    // Lấy hành động chưa xử lý (status = 0), theo thứ tự thời gian
    @Query("SELECT * FROM db_actions WHERE status = 0 ORDER BY timestamp DESC LIMIT 1")
    ActionEntity getNextAction();

    // Cập nhật trạng thái của action
    @Query("UPDATE db_actions SET status = :newStatus WHERE id = :id")
    void updateActionStatus(Long id, Integer newStatus);

    @Query("SELECT * FROM db_actions WHERE status = 0 ORDER BY timestamp ASC LIMIT 1")
    ActionEntity getNextPendingAction();

}
