package com.troop.freecamv2.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.troop.freecam.R;
import com.troop.freecamv2.ui.menu.SwipeMenuListner;

/**
 * Created by troop on 18.08.2014.
 */
public class MenuVisibilityActivity extends Activity
{
    protected ViewGroup appViewGroup;
    LinearLayout settingsLayout;
    LinearLayout manualSettingsLayout;
    LinearLayout seekbarLayout;

    SwipeMenuListner swipeMenuListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        appViewGroup = (ViewGroup) inflater.inflate(R.layout.main_v2, null);
        setContentView(R.layout.main_v2);

        settingsLayout = (LinearLayout)findViewById(R.id.v2_settings_menu);
        settingsLayout.setVisibility(View.GONE);
        manualSettingsLayout = (LinearLayout)findViewById(R.id.v2_manual_menu);
        manualSettingsLayout.setVisibility(View.GONE);
        seekbarLayout = (LinearLayout)findViewById(R.id.v2_seekbar_layout);
        seekbarLayout.setVisibility(View.GONE);

        swipeMenuListner = new SwipeMenuListner(settingsLayout, manualSettingsLayout, seekbarLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return  swipeMenuListner.onTouchEvent(event);
    }
}
