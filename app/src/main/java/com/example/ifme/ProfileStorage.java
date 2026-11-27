package com.example.ifme;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileStorage {

    private static final String PREF_NAME = "profile";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_START_DATE = "start_date";

    private final SharedPreferences prefs;

    public ProfileStorage(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    public void saveProfile(String name, String email) {
        prefs.edit()
                .putString(KEY_NAME, name)
                .putString(KEY_EMAIL, email)
                .apply();
    }

    public String getName() {
        return prefs.getString(KEY_NAME, "");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public String getStartDate() {
        String startDate = prefs.getString(KEY_START_DATE, null);
        if (startDate == null) {
            startDate = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(new Date());
            prefs.edit().putString(KEY_START_DATE, startDate).apply();
        }
        return startDate;
    }
}