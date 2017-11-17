package com.example.francesco.mapboxapp;

import android.app.Application;
import android.content.Context;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

/**
 * Created by pietrodimarco on 11/17/17.
 */

public class AppContext extends Application{
    private static Context context;

    public void onCreate(){
        super.onCreate();
        AppContext.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return AppContext.context;
    }
}
