package com.troop.freecamv2.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.troop.freecam.R;

import com.troop.freecamv2.camera.CameraUiWrapper;
import com.troop.freecamv2.ui.TextureView.ExtendedSurfaceView;
import com.troop.freecamv2.ui.handler.FocusImageHandler;
import com.troop.freecamv2.ui.handler.HardwareKeyHandler;
import com.troop.freecamv2.ui.handler.ShutterHandler;
import com.troop.freecamv2.ui.menu.ManualMenuHandler;
import com.troop.freecamv2.ui.menu.MenuHandler;
import com.troop.freecamv2.ui.handler.ThumbnailHandler;
import com.troop.freecamv2.ui.switches.CameraSwitchHandler;
import com.troop.freecamv2.ui.switches.FlashSwitchHandler;
import com.troop.freecamv2.ui.switches.ModuleSwitchHandler;

/**
 * Created by troop on 18.08.2014.
 */
public class MainActivity_v2 extends MenuVisibilityActivity
{
    ExtendedSurfaceView cameraPreview;
    CameraUiWrapper cameraUiWrapper;
    AppSettingsManager appSettingsManager;
    MenuHandler menuHandler;
    ShutterHandler shutterHandler;
    CameraSwitchHandler cameraSwitchHandler;
    ModuleSwitchHandler moduleSwitchHandler;
    FlashSwitchHandler flashSwitchHandler;
    ThumbnailHandler thumbnailHandler;
    HardwareKeyHandler hardwareKeyHandler;
    ManualMenuHandler manualMenuHandler;
    FocusImageHandler focusImageHandler;
    TextView exitButton;
    MainActivity_v2 activity;
    OrientationEventListener orientationEventListener;
    private int currentOrientation = 0;
    LinearLayout cameraControlsLayout;
    ListView switchControlsSubmenu;
    LinearLayout switchCOntrolLayout;
    LinearLayout menuControlLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.activity =this;
        appSettingsManager = new AppSettingsManager(PreferenceManager.getDefaultSharedPreferences(this));
        cameraPreview = (ExtendedSurfaceView)findViewById(R.id.CameraPreview);
        cameraPreview.appSettingsManager = appSettingsManager;
        cameraPreview.setOnTouchListener(surfaceTouche);
        cameraUiWrapper = new CameraUiWrapper(cameraPreview, appSettingsManager,null);

        menuHandler = new MenuHandler(this, cameraUiWrapper, appSettingsManager, cameraPreview);
        shutterHandler = new ShutterHandler(this, cameraUiWrapper);
        cameraSwitchHandler = new CameraSwitchHandler(this, cameraUiWrapper, appSettingsManager, cameraPreview);
        moduleSwitchHandler = new ModuleSwitchHandler(this, cameraUiWrapper, appSettingsManager);
        flashSwitchHandler = new FlashSwitchHandler(this, cameraUiWrapper, appSettingsManager);
        activity = this;

        thumbnailHandler = new ThumbnailHandler(this);
        cameraUiWrapper.moduleHandler.moduleEventHandler.AddWorkFinishedListner(thumbnailHandler);
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(cameraPreview);
        hardwareKeyHandler = new HardwareKeyHandler(this, cameraUiWrapper);
        manualMenuHandler = new ManualMenuHandler(this, cameraUiWrapper, appSettingsManager);
        focusImageHandler = new FocusImageHandler(this, cameraUiWrapper);
        exitButton = (TextView)findViewById(R.id.textView_Exit);
        if( ViewConfiguration.get(this).hasPermanentMenuKey())
        {
            exitButton.setVisibility(View.GONE);
        }
        else
        {
            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                    activity.finish();
                }
            });
        }

        cameraControlsLayout = (LinearLayout)findViewById(R.id.layout__cameraControls);
        switchControlsSubmenu = (ListView)findViewById(R.id.listView_popup);
        switchCOntrolLayout = (LinearLayout)findViewById(R.id.moduleSwitch_placeholder);
        menuControlLayout = (LinearLayout)findViewById(R.id.v2_settings_menu);
        orientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation)
            {
                if (currentOrientation != calcCurrentOrientation(orientation))
                {
                    currentOrientation = calcCurrentOrientation(orientation);
                    setRotationToCam(currentOrientation);
                    rotateViews(-currentOrientation);
                }
            }
        };

    }

    private int calcCurrentOrientation(int orientation)
    {
        int orientationToRet = 0;
        if (orientation >= 315 || orientation < 45)
            orientationToRet = 90;
        else if (orientation < 135 && orientation > 45)
            orientationToRet = 180;
        else if (orientation >= 135 && orientation < 230)
            orientationToRet = 270;
        return orientationToRet;
    }

    private void rotateViews(int orientation)
    {
        for (int i = 0; i < cameraControlsLayout.getChildCount(); i++ )
        {
            cameraControlsLayout.getChildAt(i).setRotation(orientation);
        }
        //switchCOntrolLayout.setRotation(orientation);
        rotateSettingsMenu(orientation);
    }

    private void rotateSettingsMenu(int orientation)
    {
        LinearLayout settingsLayout = (LinearLayout)findViewById(R.id.v2_settings_menu);



        //switchControlsSubmenu.setRotation(orientation);


        if (orientation == -90 || orientation == -270 )
        {
            int h = settingsLayout.getHeight();
            int w = settingsLayout.getWidth();
            settingsLayout.setRotation(orientation);
            settingsLayout.setTranslationX((h-w)/2);
            settingsLayout.setTranslationY((w-h)/2);
            settingsLayout.requestLayout();
        }
        else
        {
            settingsLayout.setRotation(orientation);
            settingsLayout.setTranslationX(0);
            settingsLayout.setTranslationY(0);
            settingsLayout.requestLayout();
        }

    }

    private void setRotationToCam(int orientation)
    {
        //cameraUiWrapper.cameraHolder.GetCamera().setDisplayOrientation(orientation);
        cameraUiWrapper.camParametersHandler.SetPictureOrientation(orientation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        orientationEventListener.enable();

    }

    @Override
    protected void onPause() {
        super.onPause();
        orientationEventListener.disable();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // .
        // Add code if needed
        // .
    }



    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        boolean haskey = hardwareKeyHandler.OnKeyEvent(keyCode, event);
        if (!haskey)
            haskey = super.onKeyUp(keyCode, event);

        return haskey;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return super.onTouchEvent(event);


    }


    View.OnTouchListener surfaceTouche = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            activity.onTouchEvent(event);
            return focusImageHandler.onTouchEvent(event);

        }
    };
}
