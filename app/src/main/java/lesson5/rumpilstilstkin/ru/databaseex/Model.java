package lesson5.rumpilstilstkin.ru.databaseex;


import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;


public class Model {

    private String login;

    private String id;

    @SerializedName("avatar_url")
    private String avatar;

    @Nullable
    public String getAvatar() {
        return avatar;
    }

    @Nullable
    public String getLogin() {
        return login;
    }

    public String getUserId() {
        return id;
    }
}
