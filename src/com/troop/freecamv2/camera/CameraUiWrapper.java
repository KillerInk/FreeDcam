package com.troop.freecamv2.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.TextureView;

import com.troop.freecamv2.camera.modules.ModuleHandler;

import com.troop.freecam.manager.SoundPlayer;
import com.troop.freecam.surfaces.CamPreview;
import com.troop.freecam.utils.DeviceUtils;
import com.troop.freecamv2.camera.parameters.CamParametersHandler;
import com.troop.freecamv2.ui.AppSettingsManager;

/**
 * Created by troop on 16.08.2014.
 */
public class CameraUiWrapper implements TextureView.SurfaceTextureListener
{
    private TextureView preview;
    public ModuleHandler moduleHandler;
    public BaseCameraHolder cameraHolder;
    AppSettingsManager appSettingsManager;
    SoundPlayer soundPlayer;
    public CamParametersHandler camParametersHandler;


    public CameraUiWrapper(TextureView preview, AppSettingsManager appSettingsManager, SoundPlayer soundPlayer)
    {
        this.preview = preview;
        this.soundPlayer = soundPlayer;
        this.appSettingsManager = appSettingsManager;
        //attache the callback to the Campreview
        preview.setSurfaceTextureListener(this);
        cameraHolder = new BaseCameraHolder();
        camParametersHandler = new CamParametersHandler(cameraHolder);
        moduleHandler = new ModuleHandler(cameraHolder, appSettingsManager, soundPlayer);
    }



//Module Handler START
    public void SwitchModule(String moduleName)
    {
        moduleHandler.SetModule(moduleName);
    }

    public void DoWork()
    {
        moduleHandler.DoWork();
    }

//Module Handler END


    private boolean openCamera()
    {
        if (Camera.getNumberOfCameras() > 0) {
            cameraHolder.OpenCamera(appSettingsManager.GetCurrentCamera());
            return true;
        }

        return false;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        StartPreviewAndCamera();
    }

    public void StartPreviewAndCamera() {
        if (openCamera())
        {
            cameraHolder.SetPreviewTexture(preview.getSurfaceTexture());
            camParametersHandler.LoadParametersFromCamera();
            cameraHolder.StartPreview();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
    {
        StopPreviewAndCamera();
        return false;
    }

    public void StopPreviewAndCamera() {
        cameraHolder.StopPreview();
        cameraHolder.CloseCamera();
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
