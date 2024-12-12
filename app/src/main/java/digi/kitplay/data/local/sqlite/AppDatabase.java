package digi.kitplay.data.local.sqlite;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import digi.kitplay.data.local.sqlite.dao.DbActionDao;
import digi.kitplay.data.local.sqlite.dao.DbUserDao;
import digi.kitplay.data.model.db.ActionEntity;
import digi.kitplay.data.model.db.UserEntity;

@Database(entities = {UserEntity.class, ActionEntity.class}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DbUserDao getDbUserDao();
    public abstract DbActionDao getDbActionDao();
}
