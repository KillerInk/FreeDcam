package com.troop.freecamv2.camera;

import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.troop.freecamv2.camera.modules.ModuleHandler;
import com.troop.freecam.manager.AppSettingsManager;
import com.troop.freecam.manager.SoundPlayer;
import com.troop.freecam.surfaces.CamPreview;
import com.troop.freecam.utils.DeviceUtils;

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
    public void surfaceCreated(SurfaceHolder holder)
    {
        if (openCamera())
        {

        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraHolder.CloseCamera();
    }

//SURFACEHOLDER Interface END


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
        if (Camera.getNumberOfCameras() == 3)
        {
            if (appSettingsManager.Cameras.GetCamera().equals(AppSettingsManager.Preferences.MODE_3D)) {
                if (DeviceUtils.isEvo3d())
                    return cameraHolder.OpenCamera(100);
                else
                    return cameraHolder.OpenCamera(2);
            } else if (appSettingsManager.Cameras.GetCamera().equals(AppSettingsManager.Preferences.MODE_2D))
                return cameraHolder.OpenCamera(0);
            else if (appSettingsManager.Cameras.GetCamera().equals(AppSettingsManager.Preferences.MODE_Front))
                return cameraHolder.OpenCamera(1);
        }
        else if(Camera.getNumberOfCameras() == 2)
        {
            if (appSettingsManager.Cameras.GetCamera().equals(AppSettingsManager.Preferences.MODE_2D))
                return cameraHolder.OpenCamera(0);
            else if (appSettingsManager.Cameras.GetCamera().equals(AppSettingsManager.Preferences.MODE_Front))
                return cameraHolder.OpenCamera(1);
        }
        else if(Camera.getNumberOfCameras() == 1)
        {
            return cameraHolder.OpenCamera(1);
        }

        return false;
    }

}
