package lesson5.rumpilstilstkin.ru.databaseex;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;


public interface Endpoints {

    @GET("users")
    Call<List<Model>> loadUsers();
}
