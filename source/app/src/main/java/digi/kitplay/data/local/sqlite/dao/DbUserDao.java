package digi.kitplay.data.local.sqlite.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import java.util.List;

import digi.kitplay.data.model.db.UserEntity;

@Dao
public interface DbUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(UserEntity userEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<UserEntity> userEntities);

    @Query("SELECT * FROM db_users")
    List<UserEntity> loadAll();

    @Query("SELECT * FROM db_users")
    LiveData<List<UserEntity>> loadAllToLiveData();

    @Query("SELECT * FROM db_users WHERE id=:id")
    UserEntity findById(long id);

    @Delete
    void delete(UserEntity userEntity);

}
