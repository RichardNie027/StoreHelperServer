package com.tlg.storehelper.pojo;

import java.io.Serializable;

public class BaseResponseEntity implements Serializable {

    public int code = -1;   //如需重命名，在子类重定义变量且加注释@SerializedName("error_code")
    public String msg = "";

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean success() {
        return true;
    }

    public boolean tokenInvalid() {
        return true;
    }

    public void setSuccessfulMessage(String msg) {
        this.code = 200;
        this.msg = msg;
    }
}
