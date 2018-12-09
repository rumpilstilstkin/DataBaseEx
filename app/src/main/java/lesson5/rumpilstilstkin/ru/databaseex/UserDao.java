package lesson5.rumpilstilstkin.ru.databaseex;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;


@Dao
public interface UserDao {

    @Query("SELECT * FROM users")
    List<Users> getAll();

    @Query("DELETE FROM users")
    int deleteAll();

    @Insert
    void insert(Users employee);

    @Update
    void update(Users employee);

    @Delete
    void delete(Users employee);
}
