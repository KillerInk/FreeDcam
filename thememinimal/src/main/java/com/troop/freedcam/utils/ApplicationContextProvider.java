package com.troop.freedcam.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by George on 3/17/2015.
 */
public class ApplicationContextProvider extends Application {

    private static Context sContext;

    @Override
    public void onCreate()
    {
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getContext()
    {
        return sContext;
    }

}