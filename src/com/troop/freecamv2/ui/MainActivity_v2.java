package com.troop.freecamv2.ui;

import android.app.ActionBar;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.troop.freecam.R;

import com.troop.freecamv2.camera.CameraUiWrapper;
import com.troop.freecamv2.camera.I_error;
import com.troop.freecamv2.ui.TextureView.ExtendedSurfaceView;
import com.troop.freecamv2.ui.handler.FocusImageHandler;
import com.troop.freecamv2.ui.handler.HardwareKeyHandler;
import com.troop.freecamv2.ui.handler.HelpOverlayHandler;
import com.troop.freecamv2.ui.handler.ShutterHandler;
import com.troop.freecamv2.ui.menu.ManualMenuHandler;
import com.troop.freecamv2.ui.menu.MenuHandler;
import com.troop.freecamv2.ui.handler.ThumbnailHandler;
import com.troop.freecamv2.ui.menu.OrientationHandler;
import com.troop.freecamv2.ui.switches.CameraSwitchHandler;
import com.troop.freecamv2.ui.switches.FlashSwitchHandler;
import com.troop.freecamv2.ui.switches.ModuleSwitchHandler;
import com.troop.freecamv2.ui.switches.NightModeSwitchHandler;

/**
 * Created by troop on 18.08.2014.
 */
public class MainActivity_v2 extends MenuVisibilityActivity implements I_error
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
    //OrientationHandler orientationHandler;
    //HelpOverlayHandler helpOverlayHandler;
    NightModeSwitchHandler nightModeSwitchHandler;
    I_error error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.activity =this;
        appSettingsManager = new AppSettingsManager(PreferenceManager.getDefaultSharedPreferences(this));
        cameraPreview = (ExtendedSurfaceView)findViewById(R.id.CameraPreview);
        cameraPreview.appSettingsManager = appSettingsManager;
        cameraPreview.setOnTouchListener(surfaceTouche);
        cameraUiWrapper = new CameraUiWrapper(cameraPreview, appSettingsManager, this);


        menuHandler = new MenuHandler(this, cameraUiWrapper, appSettingsManager, cameraPreview);
        shutterHandler = new ShutterHandler(this, cameraUiWrapper);
        cameraSwitchHandler = new CameraSwitchHandler(this, cameraUiWrapper, appSettingsManager, cameraPreview);
        moduleSwitchHandler = new ModuleSwitchHandler(this, cameraUiWrapper, appSettingsManager);
        flashSwitchHandler = new FlashSwitchHandler(this, cameraUiWrapper, appSettingsManager);
        nightModeSwitchHandler = new NightModeSwitchHandler(this, cameraUiWrapper, appSettingsManager);
        activity = this;

        //orientationHandler = new OrientationHandler(this, cameraUiWrapper);

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
        helpOverlayHandler = (HelpOverlayHandler)findViewById(R.id.helpoverlay);
        helpOverlayHandler.appSettingsManager = appSettingsManager;
        if (appSettingsManager.getShowHelpOverlay() == false)
        {
            RelativeLayout view = (RelativeLayout) helpOverlayHandler.getParent();
            view.removeView(helpOverlayHandler);
            helpOverlayOpen = false;
            helpOverlayHandler.setVisibility(View.GONE);
        }
        else
        {
            helpOverlayOpen = true;
        }

    }



    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
        orientationHandler.Stop();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // .
        // Add code if needed
        // .
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        orientationHandler.Start();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);


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

    @Override
    public int OrientationChanged(int orientation)
    {   super.OrientationChanged(orientation);

        cameraUiWrapper.camParametersHandler.SetPictureOrientation(orientation);
        return orientation;
    }

    @Override
    public void OnError(String error)
    {
        Toast toast = Toast.makeText(this, error, Toast.LENGTH_SHORT);
        toast.show();
    }
}
