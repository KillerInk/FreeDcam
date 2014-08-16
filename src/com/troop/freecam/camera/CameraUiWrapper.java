package com.troop.freecam.camera;

import android.view.SurfaceHolder;

import com.troop.freecam.camera.modules.ModuleHandler;
import com.troop.freecam.manager.AppSettingsManager;
import com.troop.freecam.manager.SoundPlayer;
import com.troop.freecam.surfaces.CamPreview;

/**
 * Created by troop on 16.08.2014.
 */
public class CameraUiWrapper implements SurfaceHolder.Callback
{
    private CamPreview preview;
    private ModuleHandler moduleHandler;
    private BaseCameraHolder cameraHolder;
    AppSettingsManager appSettingsManager;
    SoundPlayer soundPlayer;


    public CameraUiWrapper(CamPreview preview, AppSettingsManager appSettingsManager, SoundPlayer soundPlayer)
    {
        this.preview = preview;
        this.soundPlayer = soundPlayer;
        this.appSettingsManager = appSettingsManager;
        //attache the callback to the Campreview
        preview.getHolder().addCallback(this);
        cameraHolder = new BaseCameraHolder();
        moduleHandler = new ModuleHandler(cameraHolder, appSettingsManager, soundPlayer);
    }

//SURFACEHOLDER Interface START
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

//SURFACEHOLDER Interface END
}
