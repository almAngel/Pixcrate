package alm.android.pixcrate.pojos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DefaultResponse implements Serializable {

    @SerializedName("msg")
    private String msg;
    @SerializedName("status")
    private Integer status;

    public DefaultResponse(String msg, Integer status) {
        this.msg = msg;
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
