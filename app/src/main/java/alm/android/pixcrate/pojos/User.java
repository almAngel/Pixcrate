package alm.android.pixcrate.pojos;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {
    @SerializedName("_id")
    public String _id;
    @SerializedName("email")
    public String email;
    @SerializedName("username")
    public String username;
    @SerializedName("password")
    public String password;
    @SerializedName("access_token")
    public String access_token;

    public User(@Nullable String _id, @Nullable String email, @Nullable String username, @Nullable String password, @Nullable String access_token) {
        this._id = _id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.access_token = access_token;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
