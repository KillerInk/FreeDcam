package com.troop.freecamv2.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.troop.freecam.R;
import com.troop.freecamv2.ui.handler.FocusImageHandler;
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
    int flags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LOW_PROFILE;




        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        appViewGroup = (ViewGroup) inflater.inflate(R.layout.main_v2, null);
        setContentView(R.layout.main_v2);

        settingsLayout = (LinearLayout)findViewById(R.id.v2_settings_menu);
        settingsLayout.setAlpha(0f);
        //settingsLayout.setVisibility(View.GONE);
        manualSettingsLayout = (LinearLayout)findViewById(R.id.v2_manual_menu);
        manualSettingsLayout.setAlpha(0f);
        //manualSettingsLayout.setVisibility(View.GONE);
        seekbarLayout = (LinearLayout)findViewById(R.id.v2_seekbar_layout);
        seekbarLayout.setAlpha(0f);
        //seekbarLayout.setVisibility(View.GONE);

        swipeMenuListner = new SwipeMenuListner(settingsLayout, manualSettingsLayout, seekbarLayout);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                seekbarLayout.setVisibility(View.GONE);
                seekbarLayout.setAlpha(1f);
                manualSettingsLayout.setVisibility(View.GONE);
                manualSettingsLayout.setAlpha(1f);
                settingsLayout.setVisibility(View.GONE);
                settingsLayout.setAlpha(1f);
            }
        };
        new Handler().postDelayed(runnable, 3000);
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        HIDENAVBAR();
    }

    public void HIDENAVBAR()
    {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else
        {
            //HIDE nav and action bar
            final View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(flags);
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if (Build.VERSION.SDK_INT >= 16)
                        getWindow().getDecorView().setSystemUiVisibility(flags);
                }
            });
            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);

        }



    }
}
