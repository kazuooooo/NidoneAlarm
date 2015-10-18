package com.example.matsumotokazuya.mynidonealarm;

import android.app.Application;
import android.content.Context;

/**
 * Created by matsumotokazuya on 2015/10/18.
 */
public class MyApplication extends Application {
    private static Context context;

    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
