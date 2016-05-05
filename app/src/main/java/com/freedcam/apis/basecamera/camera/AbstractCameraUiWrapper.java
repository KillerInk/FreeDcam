package com.freedcam.apis.basecamera.camera;

import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceView;

import com.freedcam.apis.basecamera.camera.interfaces.I_CameraChangedListner;
import com.freedcam.apis.basecamera.camera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.camera.interfaces.I_Module;
import com.freedcam.apis.basecamera.camera.interfaces.I_error;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler;
import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;
import com.freedcam.utils.AppSettingsManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by troop on 09.12.2014.
 */
public abstract class AbstractCameraUiWrapper implements I_CameraUiWrapper, I_CameraChangedListner, I_error
{
    private final String TAG = AbstractCameraUiWrapper.class.getSimpleName();
    public AbstractModuleHandler moduleHandler;
    public AbstractParameterHandler camParametersHandler;
    public AbstractCameraHolder cameraHolder;
    public AbstractFocusHandler Focus;

    protected boolean PreviewSurfaceRdy = false;

    private List<I_CameraChangedListner> cameraChangedListners;

    protected Handler uiHandler;
    protected AppSettingsManager appSettingsManager;


    public abstract String CameraApiName();

    protected AbstractCameraUiWrapper(AppSettingsManager appSettingsManager)
    {   cameraChangedListners = new CopyOnWriteArrayList<>();
        uiHandler = new Handler(Looper.getMainLooper());
        this.appSettingsManager = appSettingsManager;
    }


    public void SetCameraChangedListner(I_CameraChangedListner cameraChangedListner)
    {
        cameraChangedListners.add(cameraChangedListner);
    }

    @Override
    public void StartCamera()
    {
    }

    @Override
    public void StopCamera()
    {
    }

    @Override
    public void StopPreview()
    {
    }


    @Override
    public void StartPreview()
    {
    }

    @Override
    public void DoWork()
    {
        moduleHandler.DoWork();
    }

    @Override
    public void onCameraOpen(final String message)
    {
        for (final I_CameraChangedListner cameraChangedListner : cameraChangedListners )
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraOpen(message);
                }
            });


    }

    @Override
    public void onCameraError(final String error) {
        for (final I_CameraChangedListner cameraChangedListner : cameraChangedListners )
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
        for (final I_CameraChangedListner cameraChangedListner : cameraChangedListners )
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraStatusChanged(status);
                }
            });


    }

    @Override
    public void onCameraClose(final String message)
    {
        camParametersHandler.locationParameter.stopLocationListining();
        for (final I_CameraChangedListner cameraChangedListner : cameraChangedListners )
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraClose(message);
                }
            });


    }

    @Override
    public void onPreviewOpen(final String message)
    {
        for (final I_CameraChangedListner cameraChangedListner : cameraChangedListners )
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onPreviewOpen(message);
                }
            });
    }

    @Override
    public void onPreviewClose(final String message) {
        for (final I_CameraChangedListner cameraChangedListner : cameraChangedListners )
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onPreviewClose(message);
                }
            });
    }

    @Override
    public void onModuleChanged(final I_Module module) {
        for (final I_CameraChangedListner cameraChangedListner : cameraChangedListners )
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onModuleChanged(module);
                }
            });

    }

    @Override
    public void onCameraOpenFinish(final String message)
    {
        for (final I_CameraChangedListner cameraChangedListner : cameraChangedListners )
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraOpenFinish(message);
                }
            });

    }

    public abstract int getMargineLeft();
    public abstract int getMargineRight();
    public abstract int getMargineTop();
    public abstract int getPreviewWidth();
    public abstract int getPreviewHeight();
    public abstract SurfaceView getSurfaceView();
}
