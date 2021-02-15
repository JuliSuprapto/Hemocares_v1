package com.example.hemocare_v1.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import net.grandcentrix.tray.TrayPreferences;

public class Prefs extends TrayPreferences {

    public static final String PREF_FIRST_TIME = "first.time";
    public static final String PREF_IS_LOGEDIN = "is.login";
    public static final String PREF_SELECTED_CITY = "pref_selected_city";
    public static String PREF_FIREBASE_TOKEN = "firebase.token";
    public static String PREF_FIREBASE_STORED = "firebase.is.stored";
    public static String PREF_ACCESS_TOKEN = "access.token";
    public static String PREF_NO_KK = "noKK.users";
    public static String PREF_NO_REKENING = "noRek.users";
    public static String PREF_BANK = "Bank.users";
    public static String PREF_ROLE = "role.users";
    public static String PREF_CITY_ID = "city.id";
    public static String INTENT_TRACKER_TYPE = "intent.tracker.type";
    public static String DEFAULT_INVALID_TOKEN = "0030";
    public static String ANGKOT_ID= "angkot.id";
    public static String INTENT_TRIP_ID = "intent.trip.id";
    public static String INTENT_ANGKOT_TRIP_WITH_MESSAGE = "intent.angkot.trip.with.message";
    public static final int ANGKOT_CHARTER_PENDING_STATUS = 1;
    public static final int ANGKOT_CHARTER_ONTRIP_STATUS = 2;
    public static final int ANGKOT_CHARTER_FINISH_STATUS = 3;
    public static final int ANGKOT_CHARTER_CANCELED_STATUS = 4;
    public static final String NOTIFICATION_TYPE_ANGKOT_CHARTER_STATUS = "10";
    public static final String NOTIFICATION_TYPE_ANGKOT_CHARTER_MESSAGE = "11";
    public static final String ACTION_NOTIFICATION_BROADCAST = "ACTION_NOTIFICATION_BROADCAST";
    public static final String BROADCAST_MESSAGE = "broadcast.message";
    public static final String BROADCAST_TYPE = "broadcast.type";
    public static final String PREF_MY_LATITUDE = "pref.my.latitude";
    public static final String PREF_MY_LONGITUDE = "pref.my.longitude";
    public static final String PREF_STORE_PROFILE = "pref.store.profile";
    public static final String PREF_STORE_LIST_CITY = "pref.store.list.city";
    public static final String PREF_STORE_FEATURES = "pref.store.list.features";
    public static final String PREF_STORE_NOTIFICATION = "pref.store.notification";
    public static final String PREFS_LAST_REQUEST_FORGOT_PASSWORD = "last.request.forgot.password";

    public Prefs(@NonNull Context context) {
        super(context, "myAppPreferencesModule", 1);
    }

}
