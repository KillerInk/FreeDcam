package com.troop.freedcam.i_camera;

import android.os.Handler;
import android.os.HandlerThread;
import android.view.SurfaceView;

import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraUiWrapper;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.interfaces.I_Module;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 09.12.2014.
 */
public abstract class AbstractCameraUiWrapper implements I_CameraUiWrapper, I_CameraChangedListner
{
    public String TAG = AbstractCameraUiWrapper.class.getSimpleName();
    public AbstractModuleHandler moduleHandler;
    public AbstractParameterHandler camParametersHandler;
    public AbstractCameraHolder cameraHolder;
    public AbstractFocusHandler Focus;
    protected I_error errorHandler;
    I_CameraChangedListner cameraChangedListner;

    protected HandlerThread backGroundThread;
    protected Handler backGroundHandler;
    protected Handler uiHandler;

    public AbstractCameraUiWrapper(){};
    public AbstractCameraUiWrapper(SurfaceView preview, AppSettingsManager appSettingsManager, I_error errorHandler)
    {
        if (backGroundThread == null) {
            backGroundThread = new HandlerThread(TAG);
            backGroundThread.start();
            backGroundHandler = new Handler(backGroundThread.getLooper());
            uiHandler = new Handler(appSettingsManager.context.getMainLooper());
        }
    };

    public void SetCameraChangedListner(I_CameraChangedListner cameraChangedListner)
    {
        this.cameraChangedListner = cameraChangedListner;
    }

    public void ErrorHappend(String error)
    {
        if  (errorHandler != null)
            errorHandler.OnError(error);
    }


    @Override
    public void SwitchModule(String moduleName)
    {
        moduleHandler.SetModule(moduleName);
    }


    //start the camera and preview in the background
    @Override
    public void StartPreviewAndCamera()
    {
        backGroundHandler.post(new Runnable() {
            @Override
            public void run() {
                startCameraAndPreview();
            }
        });
    }

    //override this to handle what happens in the background when StartPreviewAndCamera() is called
    protected void startCameraAndPreview()
    {

    }

    @Override
    public void StopPreviewAndCamera() {
        backGroundHandler.post(new Runnable() {
            @Override
            public void run() {
                stopCameraAndPreview();
            }
        });
    }

    //override this to handle what happens in the background when StopPreviewAndCamera() is called
    protected void stopCameraAndPreview()
    {

    }

    @Override
    public void DoWork() {
        moduleHandler.DoWork();
    }

    @Override
    public void onCameraOpen(final String message)
    {
        if (cameraChangedListner != null)
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraOpen(message);
                }
            });


    }

    @Override
    public void onCameraError(final String error) {
        if (cameraChangedListner != null)
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraError(error);
                }
            });
    }

    @Override
    public void onCameraStatusChanged(final String status)
    {
        if (cameraChangedListner != null)
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraStatusChanged(status);
                }
            });


    }

    @Override
    public void onModuleChanged(final I_Module module) {
        if (cameraChangedListner != null)
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onModuleChanged(module);
                }
            });

    }
}
