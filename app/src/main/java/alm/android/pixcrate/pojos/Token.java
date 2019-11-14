package alm.android.pixcrate.pojos;

import com.google.gson.annotations.SerializedName;

public class Token {

    @SerializedName("access_token")
    public String access_token;
    @SerializedName("status")
    public Integer status;

    public Token(String access_token, Integer status) {
        this.access_token = access_token;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Token{" +
                "access_token='" + access_token + '\'' +
                ", status=" + status +
                '}';
    }
}
