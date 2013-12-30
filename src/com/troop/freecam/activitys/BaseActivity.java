package com.troop.freecam.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.troop.freecam.CameraManager;
import com.troop.freecam.R;

/**
 * Created by troop on 30.12.13.
 */
public class BaseActivity extends Activity
{
    public  ViewGroup appViewGroup;
    public SharedPreferences preferences;
    public boolean recordVideo = false;
    protected CameraManager camMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        //setContentView(R.layout.activity_main);
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        appViewGroup = (ViewGroup) inflater.inflate(R.layout.main, null);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        recordVideo = preferences.getBoolean("recordVideo", false);

        setContentView(R.layout.main);
    }
}
