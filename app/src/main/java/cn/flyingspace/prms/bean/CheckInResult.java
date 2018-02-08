package cn.flyingspace.prms.bean;

/**
 * Created by yu199 on 2018/2/5.
 */

public class CheckInResult {

    /**
     * auth_result : false
     * status_code : -1
     */

    private boolean auth_result;
    private int status_code;

    public boolean isAuth_result() {
        return auth_result;
    }

    public void setAuth_result(boolean auth_result) {
        this.auth_result = auth_result;
    }

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }
}
