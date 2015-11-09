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

public class ApiHandler
{
    private static String TAG = ApiHandler.class.getSimpleName();
    AppSettingsManager appSettingsManager;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    ApiEvent event;

    public ApiHandler(final AppSettingsManager appSettingsManager, ApiEvent event) {
        this.appSettingsManager = appSettingsManager;
        this.event = event;
    }

    public void CheckApi()
    {
        if (appSettingsManager.IsCamera2FullSupported().equals(""))
        {
            if (Build.VERSION.SDK_INT >= 21)
            {

                final AppSettingsManager app = appSettingsManager;
                boolean legacy = IsLegacy();
                if (legacy) {
                    appSettingsManager.SetCamera2FullSupported("false");
                    appSettingsManager.setCamApi(AppSettingsManager.API_1);
                }
                else {
                    appSettingsManager.SetCamera2FullSupported("true");
                    appSettingsManager.setCamApi(AppSettingsManager.API_2);
                }
                event.apiDetectionDone();
            }
            else {
                appSettingsManager.SetCamera2FullSupported("false");
                event.apiDetectionDone();
            }
        }
        else
            event.apiDetectionDone();
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


    

    public interface ApiEvent
    {
        void apiDetectionDone();
    }

    private boolean IsLegacy()
    {
        boolean legacy = true;
        try
        {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            CameraManager manager = (CameraManager) appSettingsManager.context.getSystemService(Context.CAMERA_SERVICE);
            //manager.openCamera("0", null, null);
            CameraCharacteristics characteristics = manager.getCameraCharacteristics("0");
            if (characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL) != CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY)
                legacy = false;
            else
                legacy = true;
            manager = null;
            characteristics = null;

        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally
        {

            mCameraOpenCloseLock.release();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return  legacy;
    }
}
