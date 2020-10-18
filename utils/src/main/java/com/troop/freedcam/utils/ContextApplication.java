package com.troop.freedcam.utils;

import android.app.Application;
import android.content.Context;

public class ContextApplication extends Application {

    private static Context context;

    public static String getStringFromRessources(int ressourcesStringID) {
        return context.getResources().getString(ressourcesStringID);
    }

    public static String[] getStringArrayFromRessource(int id)
    {
        return getContext().getResources().getStringArray(id);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //EventBus.builder().throwSubscriberException(BuildConfig.DEBUG).installDefaultEventBus();
        context = getApplicationContext();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        context = null;
    }

    public static Context getContext() {
        return context;
    }
}
