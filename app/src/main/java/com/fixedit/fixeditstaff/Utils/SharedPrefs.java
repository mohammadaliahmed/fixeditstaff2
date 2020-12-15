package com.fixedit.fixeditstaff.Utils;

import android.content.Context;
import android.content.SharedPreferences;


import com.fixedit.fixeditstaff.ApplicationClass;
import com.fixedit.fixeditstaff.Models.ServicemanModel;
import com.fixedit.fixeditstaff.Models.User;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by AliAh on 20/02/2018.
 */

public class SharedPrefs {


    private SharedPrefs() {

    }


    public static void setAdminFcmKey(String value) {

        preferenceSetter("setAdminFcmKey", value);
    }

    public static String getAdminFcmKey() {
        return preferenceGetter("setAdminFcmKey");
    }


    public static void setIsLoggedIn(String value) {

        preferenceSetter("isLoggedIn", value);
    }

    public static String getIsLoggedIn() {
        return preferenceGetter("isLoggedIn");
    }


    public static void setFcmKey(String fcmKey) {
        preferenceSetter("fcmKey", fcmKey);
    }

    public static String getFcmKey() {
        return preferenceGetter("fcmKey");
    }



    public static void setUser(ServicemanModel model) {

        Gson gson = new Gson();
        String json = gson.toJson(model);
        preferenceSetter("customerModel", json);
    }

    public static ServicemanModel getUser() {
        Gson gson = new Gson();
        ServicemanModel customer = gson.fromJson(preferenceGetter("customerModel"), ServicemanModel.class);
        return customer;
    }




    public static void preferenceSetter(String key, String value) {
        SharedPreferences pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String preferenceGetter(String key) {
        SharedPreferences pref;
        String value = "";
        pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        value = pref.getString(key, "");
        return value;
    }

    public static void logout() {
        SharedPreferences pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }
}
