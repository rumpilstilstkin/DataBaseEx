package lesson5.rumpilstilstkin.ru.databaseex;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.orm.SugarContext;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainApp extends Application {
    private static AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();

        SugarContext.init(this);

        Realm.init(this);

        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded().build();

        Realm.setDefaultConfiguration(configuration);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database").build();
    }

    public static AppDatabase getDb(){
        return db;
    }
}

