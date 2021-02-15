package com.example.hemocare_v1.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionManager {

    private static String TAG = SessionManager.class.getSimpleName();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;

    public static final String PREF_NAME = "angkutan";
    public static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    public static final String KEY_IS_SKIP = "isSkip";
    public static final String KEY_SESSID = "sessId";

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn){
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.commit();
        Log.d(TAG, "User logged session is modified");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void setSkip(boolean isSkip){
        editor.putBoolean(KEY_IS_SKIP, isSkip);
        editor.commit();
        Log.d(TAG, "User skip logged in screen modified");
    }

    public boolean isSkip(){
        return pref.getBoolean(KEY_IS_SKIP, false);
    }

    public void setSessId(Integer sessid){
        editor.putInt(KEY_SESSID, sessid);
        editor.commit();
        Log.d(TAG, "User session is modified");
    }

    public Integer getSessId(){
        return pref.getInt(KEY_SESSID,0);
    }

}
