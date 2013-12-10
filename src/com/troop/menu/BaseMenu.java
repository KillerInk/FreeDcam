package com.troop.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.troop.freecam.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.R;

/**
 * Created by troop on 27.08.13.
 */
public abstract class BaseMenu implements Button.OnClickListener
{
    CameraManager camMan;
    MainActivity activity;
    SharedPreferences preferences;

    public  BaseMenu(CameraManager camMan , MainActivity activity)
    {
        this.camMan = camMan;
        this.activity = activity;
        preferences = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    public View GetPlaceHolder ()
    {
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();

        RelativeLayout.LayoutParams paramsx = new RelativeLayout.LayoutParams(screenWidth, screenHeight);
        paramsx.leftMargin = 70;
        paramsx.topMargin = 0;

        LayoutInflater inflater;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View canvasView;
        canvasView = inflater.inflate(R.layout.placeholder, null);

        activity.appViewGroup.addView(canvasView);
        return  canvasView;
    }
}
