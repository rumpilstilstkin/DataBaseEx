package lesson5.rumpilstilstkin.ru.databaseex;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity
public class Users {
    private String login;

    @PrimaryKey
    @NonNull
    private String userId;

    private String avatarUrl;

    Users(String login, @NonNull String userId, String avatarUrl){
        this.login = login;
        this.userId = userId;
        this.avatarUrl = avatarUrl;
    }

    public String getLogin() {
        return login;
    }

    public String getUserId() {
        return userId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
