package cn.flyingspace.prms;

import android.content.Context;
import android.content.SharedPreferences;


class Token {
    private static final String SPNAME = "PRMS_TOKEN";

    private Context context;

    Token(Context context) {
        this.context = context;
    }

    String get() {
        SharedPreferences preferences = context.getSharedPreferences(SPNAME, 0);
        return preferences.getString("token", "");
    }

    void set(String token) {
        SharedPreferences preferences = context.getSharedPreferences(SPNAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    void clear() {
        SharedPreferences preferences = context.getSharedPreferences(SPNAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", "");
        editor.apply();
    }
}
