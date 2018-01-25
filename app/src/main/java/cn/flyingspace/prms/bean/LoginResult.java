package cn.flyingspace.prms.bean;

public class LoginResult {
    public static final int ERROE_PASS = 0;
    public static final int HAS_BEEN_USED = 1;

    /**
     * result : true
     * token : token
     * reason : 0
     */

    private boolean result;
    private String token;
    private int reason;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }
}
