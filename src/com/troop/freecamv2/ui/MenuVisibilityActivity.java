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
import android.widget.TextView;

import com.troop.freecam.R;
import com.troop.freecamv2.ui.handler.FocusImageHandler;
import com.troop.freecamv2.ui.handler.HelpOverlayHandler;
import com.troop.freecamv2.ui.menu.I_orientation;
import com.troop.freecamv2.ui.menu.I_swipe;
import com.troop.freecamv2.ui.menu.OrientationHandler;
import com.troop.freecamv2.ui.menu.SwipeMenuListner;

/**
 * Created by troop on 18.08.2014.
 */
public class MenuVisibilityActivity extends Activity implements I_swipe, I_orientation
{
    protected ViewGroup appViewGroup;
    public LinearLayout settingsLayout;
    public LinearLayout settingsLayoutHolder;
    boolean settingsLayloutOpen = false;
    public LinearLayout manualSettingsLayout;
    public LinearLayout seekbarLayout;
    LinearLayout manualMenuHolder;

    LinearLayout cameraControlsLayout;
    boolean manualMenuOpen = false;
    protected boolean helpOverlayOpen = false;

    SwipeMenuListner swipeMenuListner;
    OrientationHandler orientationHandler;
    int flags;

    protected HelpOverlayHandler helpOverlayHandler;
    int helplayoutrot;

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
        manualMenuHolder = (LinearLayout)findViewById(R.id.manualMenuHolder);
        settingsLayout = (LinearLayout)findViewById(R.id.v2_settings_menu);
        settingsLayoutHolder = (LinearLayout)findViewById(R.id.settings_menuHolder);
        settingsLayout.removeView(settingsLayoutHolder);
        settingsLayloutOpen = false;

        //settingsLayout.setAlpha(0f);
        //settingsLayout.setVisibility(View.GONE);
        manualSettingsLayout = (LinearLayout)findViewById(R.id.v2_manual_menu);
        //manualSettingsLayout.setAlpha(0f);
        //manualSettingsLayout.setVisibility(View.GONE);
        seekbarLayout = (LinearLayout)findViewById(R.id.v2_seekbar_layout);
        manualMenuHolder.removeView(manualSettingsLayout);
        manualMenuHolder.removeView(seekbarLayout);

        cameraControlsLayout = (LinearLayout)findViewById(R.id.layout__cameraControls);

        //seekbarLayout.setAlpha(0f);
        //seekbarLayout.setVisibility(View.GONE);

        swipeMenuListner = new SwipeMenuListner(this);
        orientationHandler = new OrientationHandler(this, this);



        /*Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //seekbarLayout.setVisibility(View.GONE);
                //seekbarLayout.setAlpha(1f);
                //manualSettingsLayout.setVisibility(View.GONE);
                //manualSettingsLayout.setAlpha(1f);
                //settingsLayout.setVisibility(View.GONE);
                //settingsLayout.setAlpha(1f);
            }
        };
        new Handler().postDelayed(runnable, 4000);*/
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

    @Override
    public void doHorizontalSwipe()
    {
        if (swipeMenuListner.startX - swipeMenuListner.currentX < 0)
        {
            if (!settingsLayloutOpen) {
                settingsLayout.addView(settingsLayoutHolder);
                settingsLayloutOpen = true;
            }
        }
        else
        {
            if (settingsLayloutOpen) {
                settingsLayout.removeView(settingsLayoutHolder);
                settingsLayloutOpen = false;
            }
        }
    }

    @Override
    public void doVerticalSwipe()
    {
        if (swipeMenuListner.startY  - swipeMenuListner.currentY < 0)
        {
            if (!manualMenuOpen) {
                manualMenuHolder.addView(manualSettingsLayout);
                manualMenuHolder.addView(seekbarLayout);
                manualMenuOpen = true;
            }
        }
        else
        {
            if (manualMenuOpen)
            {
                manualMenuHolder.removeView(manualSettingsLayout);
                manualMenuHolder.removeView(seekbarLayout);
                manualMenuOpen = false;
            }
        }
    }

    @Override
    public int OrientationChanged(int orientation)
    {
        rotateViews(-orientation);
        return orientation;
    }

    private void rotateViews(int orientation)
    {
        TextView textView = (TextView)seekbarLayout.findViewById(R.id.textView_seekbar);
        textView.setRotation(orientation);
        if (helpOverlayOpen && helplayoutrot != orientation)
        {
            for (int i = 0; i < helpOverlayHandler.getChildCount(); i++)
            {
                int h = helpOverlayHandler.getChildAt(i).getHeight();
                int w = helpOverlayHandler.getChildAt(i).getWidth();
                helpOverlayHandler.getChildAt(i).getLayoutParams().height = w ;
                helpOverlayHandler.getChildAt(i).getLayoutParams().width = h ;

                //helpOverlayLayout.getChildAt(i).setTop(0);

                helpOverlayHandler.getChildAt(i).setRotation(orientation);
                helpOverlayHandler.getChildAt(i).setTranslationX(0);
                helpOverlayHandler.getChildAt(i).setTranslationY(0);
                helpOverlayHandler.getChildAt(i).requestLayout();

            }
            //helpOverlayLayout.setLeft(0);
            helplayoutrot = -orientation;
        }

        for (int i = 0; i < cameraControlsLayout.getChildCount(); i++ )
        {
            cameraControlsLayout.getChildAt(i).setRotation(orientation);
        }
        //switchCOntrolLayout.setRotation(orientation);
        rotateSettingsMenu(orientation);

        //*int lasvis = manualSettingsLayout.getVisibility();
        //float lastalp = manualSettingsLayout.getAlpha();
        //manualSettingsLayout.setAlpha(0f);
        //manualSettingsLayout.setVisibility(View.VISIBLE);
        for (int i = 0; i < manualSettingsLayout.getChildCount(); i++)
        {
            View view =  manualSettingsLayout.getChildAt(i);
            int h = view.getLayoutParams().height;
            int w = view.getLayoutParams().width;
            if (h == 0 || w == 0)
                return;
            view.getLayoutParams().height = w;
            view.getLayoutParams().width = h;
            view.requestLayout();
            view.setRotation(orientation);
        }
        //manualSettingsLayout.setAlpha(lastalp);
        //manualSettingsLayout.setVisibility(lasvis);
        //int oldw = switchCOntrolLayout.getWidth();
        //int oldh = switchCOntrolLayout.getHeight();
        //switchCOntrolLayout.getLayoutParams().height = oldw;
        //switchCOntrolLayout.getLayoutParams().width = oldh;


        //switchCOntrolLayout.setRotation(orientation);
        //switchCOntrolLayout.requestLayout();
        //switchCOntrolLayout.setBottom(0);
    }

    private void rotateSettingsMenu(int orientation)
    {
        int h = settingsLayout.getHeight();
        int w = settingsLayout.getWidth();
        if (h == 0 || w == 0)
        {
            return;
        }
        settingsLayout.getLayoutParams().height = w;
        settingsLayout.getLayoutParams().width = h;
        settingsLayout.requestLayout();
        settingsLayout.setRotation(orientation);
    }
}
