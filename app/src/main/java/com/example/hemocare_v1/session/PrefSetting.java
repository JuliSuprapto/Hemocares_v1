package com.example.hemocare_v1.session;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.example.hemocare_v1.MainActivity;

public class PrefSetting {

    public static String _id;
    public static String nik;
    public static String fullname;
    public static String username;
    public static String password;
    public static String phone;
    public static String address;
    public static String email;
    public static String profilephoto;
    public static String role;

    Activity activity;

    public PrefSetting(Activity activity) {
        this.activity = activity;
    }

    public SharedPreferences getSharedPreferences() {
        SharedPreferences preferences = activity.getSharedPreferences("AccessDetails", Context.MODE_PRIVATE);
        return preferences;
    }

    public void isLogin(SessionManager sessionManager, SharedPreferences preferences) {
        sessionManager = new SessionManager(activity);
        if (sessionManager.isLoggedIn()) {
            preferences = getSharedPreferences();
            _id = preferences.getString("_id", "");
            nik = preferences.getString("nik", "");
            fullname = preferences.getString("fullname", "");
            username = preferences.getString("username", "");
            password = preferences.getString("password", "");
            phone = preferences.getString("phone", "");
            address = preferences.getString("address", "");
            email = preferences.getString("email", "");
            profilephoto = preferences.getString("profilephoto", "");
            role = preferences.getString("role", "");
        } else {
            sessionManager.setLogin(false);
            sessionManager.setSessId(0);
            Intent i = new Intent(activity, activity.getClass());
            activity.startActivity(i);
            activity.finish();
        }
    }

    public void checkLogin(SessionManager sessionManager, SharedPreferences preferences) {
        sessionManager = new SessionManager(activity);
        _id = preferences.getString("_id", "");
        nik = preferences.getString("nik", "");
        fullname = preferences.getString("fullname", "");
        username = preferences.getString("username", "");
        password = preferences.getString("password", "");
        phone = preferences.getString("phone", "");
        address = preferences.getString("address", "");
        email = preferences.getString("email", "");
        profilephoto = preferences.getString("profilephoto", "");
        role = preferences.getString("role", "");
        if (sessionManager.isLoggedIn()) {
            if (role.equals("1")) {
                Intent i = new Intent(activity, MainActivity.class);
                activity.startActivity(i);
                activity.finish();
            }
        }
    }

    public void storeRegIdSharedPreferences(Context context, String _id, String nik, String fullname, String username, String password, String phone, String address, String email, String profilephoto, String role, SharedPreferences preferences){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("_id", _id);
        editor.putString("nik", nik);
        editor.putString("fullname", fullname);
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("phone", phone);
        editor.putString("address", address);
        editor.putString("email", email);
        editor.putString("profilephoto", profilephoto);
        editor.putString("role", role);
        editor.commit();
    }

}
