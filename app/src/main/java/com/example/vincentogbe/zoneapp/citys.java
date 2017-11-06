package com.example.vincentogbe.zoneapp;

/**
 * Created by vincent ogbe on 06/11/2017.
 */
import android.app.Activity;
import android.content.SharedPreferences;

public class citys {

    SharedPreferences prefs;

    public citys(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    // If the user has not chosen a city yet, return
    // Sydney as the default city
    String getCity(){
        return prefs.getString("city", "Sydney, AU");
    }

    void setCity(String city){
        prefs.edit().putString("city", city).commit();
    }

}
