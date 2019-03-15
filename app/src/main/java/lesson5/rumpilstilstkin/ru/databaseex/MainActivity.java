package lesson5.rumpilstilstkin.ru.databaseex;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String EXT_TIME = "ext_time";
    private final static String EXT_COUNT = "ext_count";

    private TextView mInfoTextView;
    private ProgressBar progressBar;

    List<Model> modelList = new ArrayList<>();
    Realm realm;
    UserDataSource usersDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usersDataSource = new UserDataSource(this);
        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SugarContext.terminate();
    }

    private void initViews() {
        mInfoTextView = findViewById(R.id.tvLoad);
        progressBar = findViewById(R.id.progressBar);
        findViewById(R.id.btnLoad).setOnClickListener(this);
        findViewById(R.id.btnSaveAllSugar).setOnClickListener(this);
        findViewById(R.id.btnSelectAllSugar).setOnClickListener(this);
        findViewById(R.id.btnDeleteAllSugar).setOnClickListener(this);
        findViewById(R.id.btnSaveAllRealm).setOnClickListener(this);
        findViewById(R.id.btnSelectAllRealm).setOnClickListener(this);
        findViewById(R.id.btnDeleteAllRealm).setOnClickListener(this);
        findViewById(R.id.btnSaveAllRoom).setOnClickListener(this);
        findViewById(R.id.btnSelectAllRoom).setOnClickListener(this);
        findViewById(R.id.btnDeleteAllRoom).setOnClickListener(this);
        findViewById(R.id.btnSaveAllSQLLiteOrigin).setOnClickListener(this);
        findViewById(R.id.btnSelectAllSQLLiteOrigin).setOnClickListener(this);
        findViewById(R.id.btnDeleteAllSQLLiteOrigin).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoad:
                loadData();
                break;
            case R.id.btnSaveAllSugar:
                execute(this::saveShugar);
                break;
            case R.id.btnSelectAllSugar:
                execute(this::getAllShugar);
                break;
            case R.id.btnDeleteAllSugar:
                execute(this::deleteAllShugar);
                break;
            case R.id.btnSaveAllRealm:
                execute(this::saveRealm);
                break;
            case R.id.btnSelectAllRealm:
                execute(this::getAllRealm);
                break;
            case R.id.btnDeleteAllRealm:
                execute(this::deleteAllRealm);
                break;
            case R.id.btnSaveAllRoom:
                execute(this::saveRoom);
                break;
            case R.id.btnSelectAllRoom:
                execute(this::getAllRoom);
                break;
            case R.id.btnDeleteAllRoom:
                execute(this::deleteAllRoom);
                break;
            case R.id.btnSaveAllSQLLiteOrigin:
                execute(this::saveSQLLiteOrigin);
                break;
            case R.id.btnSelectAllSQLLiteOrigin:
                execute(this::getAllSQLLiteOrigin);
                break;
            case R.id.btnDeleteAllSQLLiteOrigin:
                execute(this::deleteAllSQLLiteOrigin);
                break;
        }
    }

    private DisposableSingleObserver<Bundle> createObserver() {
        return new DisposableSingleObserver<Bundle>() {

            @Override
            protected void onStart() {
                super.onStart();
                progressBar.setVisibility(View.VISIBLE);
                mInfoTextView.setText("");
            }

            @Override
            public void onSuccess(@NonNull Bundle bundle) {
                progressBar.setVisibility(View.GONE);
                mInfoTextView.append("количество = " + bundle.getInt(EXT_COUNT) +
                                     "\n милисекунд = " + bundle.getLong(EXT_TIME));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                progressBar.setVisibility(View.GONE);
                mInfoTextView.setText("ошибка БД: " + e.getMessage());
            }
        };
    }

    private void loadData() {
        mInfoTextView.setText("");
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = connectivityManager.getActiveNetworkInfo();

        if (networkinfo != null && networkinfo.isConnected()) {
            // запускаем
            progressBar.setVisibility(View.VISIBLE);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.github.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            Call<List<Model>> call = retrofit.create(Endpoints.class).loadUsers();
            downloadOneUrl(call);
        }
        else {
            Toast.makeText(this, "Подключите интернет", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadOneUrl(Call<List<Model>> call) {
        call.enqueue(new Callback<List<Model>>() {

            @Override
            public void onResponse(Call<List<Model>> call, Response<List<Model>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Model curModel = null;
                        mInfoTextView.append("\n Size = " + response.body().size() +
                                             "\n-----------------");
                        for (int i = 0; i < response.body().size(); i++) {
                            curModel = response.body().get(i);
                            modelList.add(curModel);
                            mInfoTextView.append(
                                    "\nLogin = " + curModel.getLogin() +
                                    "\nId = " + curModel.getUserId() +
                                    "\nURI = " + curModel.getAvatar() +
                                    "\n-----------------");
                        }
                    }
                }
                else {
                    System.out.println("onResponse error: " + response.code());
                    mInfoTextView.setText("onResponse error: " + response.code());
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Model>> call, Throwable t) {
                System.out.println("onFailure " + t);
                mInfoTextView.setText("onFailure " + t.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint("CheckResult")
    private void execute(Callable<Bundle> call) {
        Single<Bundle> singleDeleteAll = Single.create((SingleOnSubscribe<Bundle>) emitter -> {
            try {
                emitter.onSuccess(call.call());
            }
            catch (Exception e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        singleDeleteAll.subscribeWith(createObserver());
    }

    private Bundle saveShugar() {
        Date first = new Date();
        for (Model curItem : modelList) {
            new SugarModel(
                    curItem.getLogin(),
                    curItem.getUserId(),
                    curItem.getAvatar()
            ).save();
        }
        Date second = new Date();
        List<SugarModel> tempList = SugarModel.listAll(SugarModel.class);
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, tempList.size());
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        return bundle;
    }

    private Bundle getAllShugar() {
        Date first = new Date();
        List<SugarModel> tempList = SugarModel.listAll(SugarModel.class);
        Date second = new Date();
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, tempList.size());
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        return bundle;
    }

    private Bundle deleteAllShugar() {
        Date first = new Date();
        int count = SugarModel.deleteAll(SugarModel.class);
        Date second = new Date();
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, count);
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        return bundle;
    }

    private Bundle saveRealm() {
        realm = Realm.getDefaultInstance();
        Date first = new Date();
        realm.executeTransaction(realm1 -> {
            for (Model curItem : modelList) {
                try {
                    RealmModel realmModel = realm1.createObject(RealmModel.class);
                    realmModel.setUserID(curItem.getUserId());
                    realmModel.setLogin(curItem.getLogin());
                    realmModel.setAvatarUrl(curItem.getAvatar());
                }
                catch (Exception e) {
                    realm1.cancelTransaction();
                    realm1.close();
                    throw e;
                }
            }
        });
        Date second = new Date();
        long count = realm.where(RealmModel.class).count();
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, (int) count);
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        realm.close();
        return bundle;
    }

    private Bundle getAllRealm() {
        realm = Realm.getDefaultInstance();
        Date first = new Date();
        RealmResults<RealmModel> tempList = realm.where(RealmModel.class).findAll();
        Date second = new Date();
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, tempList.size());
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        realm.close();
        return bundle;
    }

    private Bundle deleteAllRealm() {
        realm = Realm.getDefaultInstance();
        final RealmResults<RealmModel> tempList = realm.where(RealmModel.class).findAll();
        int size = tempList.size();
        Date first = new Date();
        realm.executeTransaction(realm -> tempList.deleteAllFromRealm());
        Date second = new Date();
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, size);
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        realm.close();
        return bundle;
    }

    private Bundle saveRoom() {
        UserDao userDao = MainApp.getDb().userDao();
        Date first = new Date();
        for (Model curItem : modelList) {
            userDao.insert(new Users(
                    curItem.getLogin(),
                    curItem.getUserId(),
                    curItem.getAvatar()
            ));
        }
        Date second = new Date();
        List<Users> tempList = userDao.getAll();
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, tempList.size());
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        return bundle;
    }

    private Bundle getAllRoom() {
        UserDao userDao = MainApp.getDb().userDao();

        Date first = new Date();
        List<Users> tempList = userDao.getAll();
        Date second = new Date();
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, tempList.size());
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        return bundle;
    }

    private Bundle deleteAllRoom() {
        UserDao userDao = MainApp.getDb().userDao();

        Date first = new Date();
        int size = userDao.deleteAll();
        Date second = new Date();
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, size);
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        return bundle;
    }


    private Bundle saveSQLLiteOrigin() {
        Date first = new Date();
        usersDataSource.open();
        for (Model curItem : modelList) {
            usersDataSource.addUser(curItem);
        }
        Date second = new Date();
        List<Model> tempList = usersDataSource.getAllUsers();
        usersDataSource.close();
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, tempList.size());
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        return bundle;
    }

    private Bundle getAllSQLLiteOrigin() {
        Date first = new Date();
        usersDataSource.open();
        List<Model> tempList = usersDataSource.getAllUsers();
        usersDataSource.close();
        Date second = new Date();
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, tempList.size());
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        return bundle;
    }

    private Bundle deleteAllSQLLiteOrigin() {
        Date first = new Date();
        usersDataSource.open();
        int count = usersDataSource.deleteAll();
        usersDataSource.close();
        Date second = new Date();
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, count);
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        return bundle;
    }
}
