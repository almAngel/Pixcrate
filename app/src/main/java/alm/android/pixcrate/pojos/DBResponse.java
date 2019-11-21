package alm.android.pixcrate.pojos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DBResponse implements Serializable {

    @SerializedName("msg")
    private String msg;
    @SerializedName("cause")
    private String cause;
    @SerializedName("causeCode")
    private Integer causeCode;
    @SerializedName("status")
    private Integer status;

    public DBResponse(String msg, String cause, Integer causeCode, Integer status) {
        this.msg = msg;
        this.cause = cause;
        this.causeCode = causeCode;
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public Integer getCauseCode() {
        return causeCode;
    }

    public void setCauseCode(Integer causeCode) {
        this.causeCode = causeCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
