package com.troop.freedcam.ui.handler;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Handler;
import android.renderscript.RenderScript;

import com.troop.apis.SonyCameraFragment;
import com.troop.freedcam.apis.AbstractCameraFragment;
import com.troop.freedcam.apis.Camera1Fragment;
import com.troop.freedcam.apis.Camera2Fragment;
import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_Module;
import com.troop.freedcam.ui.AppSettingsManager;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import troop.com.imageconverter.ViewfinderProcessor;

/**
 * Created by troop on 11.12.2014.
 */

public class ApiHandler implements I_CameraChangedListner
{
    private static String TAG = ApiHandler.class.getSimpleName();
    AppSettingsManager appSettingsManager;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    boolean waitforclose = false;
    Handler handler = new Handler();
    BaseCameraHolderApi2 cam2;
    ApiEvent event;

    public ApiHandler(final AppSettingsManager appSettingsManager, ApiEvent event) {
        this.appSettingsManager = appSettingsManager;
        this.event = event;
    }

    public void CheckApi()
    {
        if (appSettingsManager.getString(AppSettingsManager.CAMERA2FULLSUPPORTED).equals(""))
        {
            if (Build.VERSION.SDK_INT >= 21)
            {
                waitforclose = true;

                final AppSettingsManager app = appSettingsManager;
                cam2 = new BaseCameraHolderApi2(app.context, ApiHandler.this,handler, app);
                cam2.OpenCamera(0);

            }
            else {
                appSettingsManager.setString(AppSettingsManager.CAMERA2FULLSUPPORTED, "false");
                event.apiDetectionDone();
            }
        }
        else
            event.apiDetectionDone();
    }

    public boolean ApiCheckDone()
    {
        if(waitforclose)
            return false;
        else
            return true;
    }

    public AbstractCameraFragment getCameraFragment(AppSettingsManager appSettingsManager)
    {
        AbstractCameraFragment ret;
        if (appSettingsManager.getCamApi().equals(AppSettingsManager.API_SONY))
        {
            ret = new SonyCameraFragment();

        }
        else if (appSettingsManager.getCamApi().equals(AppSettingsManager.API_2))
        {
            ret = new Camera2Fragment();
        }
        else
        {
            ret = new Camera1Fragment();
        }
        return ret;
    }


    @Override
    public void onCameraOpen(String message)
    {
        if (cam2.isLegacyDevice()) {
            appSettingsManager.setString(AppSettingsManager.CAMERA2FULLSUPPORTED, "false");
            appSettingsManager.setCamApi(AppSettingsManager.API_1);
        }
        else {
            appSettingsManager.setString(AppSettingsManager.CAMERA2FULLSUPPORTED, "true");
            appSettingsManager.setCamApi(AppSettingsManager.API_2);
        }
        cam2.CloseCamera();
        waitforclose = false;
        event.apiDetectionDone();
    }

    @Override
    public void onCameraOpenFinish(String message) {

    }

    @Override
    public void onCameraClose(String message) {

    }

    @Override
    public void onPreviewOpen(String message) {

    }

    @Override
    public void onPreviewClose(String message) {

    }

    @Override
    public void onCameraError(String error) {

    }

    @Override
    public void onCameraStatusChanged(String status) {

    }

    @Override
    public void onModuleChanged(I_Module module) {

    }

    public interface ApiEvent
    {
        void apiDetectionDone();
    }
}
