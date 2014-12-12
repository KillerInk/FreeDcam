package com.troop.freedcam.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.troop.freedcam.R;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.I_error;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.TextureView.ExtendedSurfaceView;
import com.troop.freedcam.ui.handler.ApiHandler;
import com.troop.freedcam.ui.handler.FocusImageHandler;
import com.troop.freedcam.ui.handler.HardwareKeyHandler;
import com.troop.freedcam.ui.handler.HelpOverlayHandler;
import com.troop.freedcam.ui.handler.ShutterHandler;
import com.troop.freedcam.ui.handler.TimerHandler;
import com.troop.freedcam.ui.menu.ManualMenuHandler;
import com.troop.freedcam.ui.menu.MenuHandler;
import com.troop.freedcam.ui.handler.ThumbnailHandler;
import com.troop.freedcam.ui.switches.CameraSwitchHandler;
import com.troop.freedcam.ui.switches.FlashSwitchHandler;
import com.troop.freedcam.ui.switches.ModuleSwitchHandler;
import com.troop.freedcam.ui.switches.NightModeSwitchHandler;

/**
 * Created by troop on 18.08.2014.
 */
public class MainActivity_v2 extends MenuVisibilityActivity implements I_error
{
    ExtendedSurfaceView cameraPreview;
    AbstractCameraUiWrapper cameraUiWrapper;
    AppSettingsManager appSettingsManager;
    MenuHandler menuHandler;
    public ShutterHandler shutterHandler;
    CameraSwitchHandler cameraSwitchHandler;
    ModuleSwitchHandler moduleSwitchHandler;
    FlashSwitchHandler flashSwitchHandler;
    ThumbnailHandler thumbnailHandler;
    HardwareKeyHandler hardwareKeyHandler;
    public ManualMenuHandler manualMenuHandler;
    FocusImageHandler focusImageHandler;
    TextView exitButton;
    MainActivity_v2 activity;
    ApiHandler apiHandler;
    TimerHandler timerHandler;
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

        thumbnailHandler = new ThumbnailHandler(this);
        apiHandler = new ApiHandler();

        cameraUiWrapper = apiHandler.getCameraUiWrapper(this,cameraPreview, appSettingsManager, this);

        initCameraStuff(cameraUiWrapper);

        activity = this;
        //orientationHandler = new OrientationHandler(this, cameraUiWrapper);
        cameraUiWrapper.moduleHandler.moduleEventHandler.AddWorkFinishedListner(thumbnailHandler);
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(cameraPreview);
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(cameraPreview);

        timerHandler = new TimerHandler(this);
        cameraUiWrapper.moduleHandler.moduleEventHandler.AddRecoderChangedListner(timerHandler);
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(timerHandler);

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


    private void initCameraStuff(AbstractCameraUiWrapper cameraUiWrapper)
    {
        menuHandler = new MenuHandler(this, cameraUiWrapper, appSettingsManager, cameraPreview);
        shutterHandler = new ShutterHandler(this, cameraUiWrapper);
        cameraSwitchHandler = new CameraSwitchHandler(this, cameraUiWrapper, appSettingsManager, cameraPreview);
        moduleSwitchHandler = new ModuleSwitchHandler(this, cameraUiWrapper, appSettingsManager);
        flashSwitchHandler = new FlashSwitchHandler(this, cameraUiWrapper, appSettingsManager);
        nightModeSwitchHandler = new NightModeSwitchHandler(this, cameraUiWrapper, appSettingsManager);
        hardwareKeyHandler = new HardwareKeyHandler(this, cameraUiWrapper);
        manualMenuHandler = new ManualMenuHandler(this, cameraUiWrapper, appSettingsManager);
        focusImageHandler = new FocusImageHandler(this, cameraUiWrapper);
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
        boolean haskey = hardwareKeyHandler.OnKeyUp(keyCode, event);
        if (!haskey)
            haskey = super.onKeyUp(keyCode, event);

        return haskey;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        hardwareKeyHandler.OnKeyDown(keyCode, event);
        return true;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event)
    {
        boolean haskey = hardwareKeyHandler.OnKeyLongPress(keyCode, event);
        if (!haskey)
            haskey = super.onKeyLongPress(keyCode, event);
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

        //cameraUiWrapper.camParametersHandler.SetPictureOrientation(orientation);
        return orientation;
    }

    @Override
    public void OnError(String error)
    {
        Toast toast = Toast.makeText(this, error, Toast.LENGTH_LONG);
        toast.show();
    }
}
