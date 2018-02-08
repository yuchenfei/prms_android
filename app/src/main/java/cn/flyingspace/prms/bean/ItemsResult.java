package cn.flyingspace.prms.bean;

import java.util.List;

/**
 * Created by yu199 on 2018/2/6.
 */

public class ItemsResult {

    /**
     * auth_result : false
     * index : 0
     */

    private boolean auth_result;
    private int index;
    private List<Integer> meeting_index;
    private List<String> meeting_time;
    private List<Integer> meeting_ok;

    public boolean isAuth_result() {
        return auth_result;
    }

    public void setAuth_result(boolean auth_result) {
        this.auth_result = auth_result;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<Integer> getMeeting_index() {
        return meeting_index;
    }

    public void setMeeting_index(List<Integer> meeting_index) {
        this.meeting_index = meeting_index;
    }

    public List<String> getMeeting_time() {
        return meeting_time;
    }

    public void setMeeting_time(List<String> meeting_time) {
        this.meeting_time = meeting_time;
    }

    public List<Integer> getMeeting_ok() {
        return meeting_ok;
    }

    public void setMeeting_ok(List<Integer> meeting_ok) {
        this.meeting_ok = meeting_ok;
    }
}
