package com.troop.freecamv2.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.TextureView;

import com.troop.freecamv2.camera.modules.ModuleHandler;
import com.troop.freecam.manager.AppSettingsManager;
import com.troop.freecam.manager.SoundPlayer;
import com.troop.freecam.surfaces.CamPreview;
import com.troop.freecam.utils.DeviceUtils;

/**
 * Created by troop on 16.08.2014.
 */
public class CameraUiWrapper implements TextureView.SurfaceTextureListener
{
    private TextureView preview;
    private ModuleHandler moduleHandler;
    private BaseCameraHolder cameraHolder;
    AppSettingsManager appSettingsManager;
    SoundPlayer soundPlayer;


    public CameraUiWrapper(TextureView preview, AppSettingsManager appSettingsManager, SoundPlayer soundPlayer)
    {
        this.preview = preview;
        this.soundPlayer = soundPlayer;
        this.appSettingsManager = appSettingsManager;
        //attache the callback to the Campreview
        preview.setSurfaceTextureListener(this);
        cameraHolder = new BaseCameraHolder();

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

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (openCamera())
        {
            cameraHolder.SetPreviewTexture(preview.getSurfaceTexture());
            cameraHolder.StartPreview();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
    {
        cameraHolder.StopPreview();
        cameraHolder.CloseCamera();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
