package com.troop.freecamv2.ui;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

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
    Activity activity;
    ThumbnailHandler thumbnailHandler;
    HardwareKeyHandler hardwareKeyHandler;
    ManualMenuHandler manualMenuHandler;
    FocusImageHandler focusImageHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appSettingsManager = new AppSettingsManager(PreferenceManager.getDefaultSharedPreferences(this));
        cameraPreview = (ExtendedSurfaceView)findViewById(R.id.CameraPreview);
        cameraUiWrapper = new CameraUiWrapper(cameraPreview, appSettingsManager,null);

        menuHandler = new MenuHandler(this, cameraUiWrapper, appSettingsManager);
        shutterHandler = new ShutterHandler(this, cameraUiWrapper);
        cameraSwitchHandler = new CameraSwitchHandler(this, cameraUiWrapper, appSettingsManager, cameraPreview);
        moduleSwitchHandler = new ModuleSwitchHandler(this, cameraUiWrapper, appSettingsManager);
        flashSwitchHandler = new FlashSwitchHandler(this, cameraUiWrapper, appSettingsManager);
        activity = this;

        thumbnailHandler = new ThumbnailHandler(this);
        cameraUiWrapper.moduleHandler.moduleEventHandler.AddWorkFinishedListner(thumbnailHandler);
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
        boolean ret = focusImageHandler.onTouchEvent(event);
        if (ret)
            return super.onTouchEvent(event);
        else
            return ret;


    }


}
