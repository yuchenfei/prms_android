package cn.flyingspace.prms.bean;

import java.util.List;

/**
 * Created by yu199 on 2018/2/6.
 */

public class ItemsResult {

    /**
     * auth_result : false
     * daily_times : 1
     * daily_time_interval : 08:00-09:00
     * daily_status : 0
     * temp_id : [1]
     * temp_time : ["08:00"]
     * temp_ok : [1]
     */

    private boolean auth_result;
    private int daily_times;
    private String daily_time_interval;
    private int daily_status;
    private List<Integer> temp_id;
    private List<String> temp_time;
    private List<Integer> temp_ok;

    public boolean isAuth_result() {
        return auth_result;
    }

    public void setAuth_result(boolean auth_result) {
        this.auth_result = auth_result;
    }

    public int getDaily_times() {
        return daily_times;
    }

    public void setDaily_times(int daily_times) {
        this.daily_times = daily_times;
    }

    public String getDaily_time_interval() {
        return daily_time_interval;
    }

    public void setDaily_time_interval(String daily_time_interval) {
        this.daily_time_interval = daily_time_interval;
    }

    public int getDaily_status() {
        return daily_status;
    }

    public void setDaily_status(int daily_status) {
        this.daily_status = daily_status;
    }

    public List<Integer> getTemp_id() {
        return temp_id;
    }

    public void setTemp_id(List<Integer> temp_id) {
        this.temp_id = temp_id;
    }

    public List<String> getTemp_time() {
        return temp_time;
    }

    public void setTemp_time(List<String> temp_time) {
        this.temp_time = temp_time;
    }

    public List<Integer> getTemp_ok() {
        return temp_ok;
    }

    public void setTemp_ok(List<Integer> temp_ok) {
        this.temp_ok = temp_ok;
    }
}
