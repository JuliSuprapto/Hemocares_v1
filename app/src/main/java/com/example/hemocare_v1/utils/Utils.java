package com.example.hemocare_v1.utils;

public class Utils {

    public static void storeProfile(String user) {
        App.getPref().put(Prefs.PREF_STORE_PROFILE, user);
    }

    public static boolean isLoggedIn() {
        return App.getPref().getBoolean(Prefs.PREF_IS_LOGEDIN, false);
    }

}
