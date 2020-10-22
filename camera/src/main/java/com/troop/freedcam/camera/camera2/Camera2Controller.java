package com.troop.freedcam.camera.camera2;

import android.graphics.ImageFormat;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.camera.basecamera.AbstractCameraController;
import com.troop.freedcam.camera.camera2.modules.I_PreviewWrapper;
import com.troop.freedcam.camera.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.eventbus.events.CameraStateEvents;
import com.troop.freedcam.processor.RenderScriptProcessor;
import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.utils.Log;

import org.greenrobot.eventbus.Subscribe;

public class Camera2Controller extends AbstractCameraController<ParameterHandlerApi2,CameraHolderApi2,FocusHandler> implements CameraValuesChangedCaptureCallback.WaitForFirstFrameCallback {

    private final String TAG = Camera2Controller.class.getSimpleName();
    private RenderScriptProcessor mProcessor;
    private boolean cameraIsOpen = false;
    public CaptureSessionHandler captureSessionHandler;
    public CameraValuesChangedCaptureCallback cameraBackroundValuesChangedListner;

    @Subscribe
    public void onCameraOpen(CameraStateEvents.CameraOpenEvent event)
    {
        Log.d(TAG, "onCameraOpen, initCamera");
        mainToCameraHandler.initCamera();
    }

    @Subscribe
    public void onCameraClose(CameraStateEvents.CameraCloseEvent event)
    {
        try {
            Log.d(TAG, "onCameraClose");
            cameraIsOpen = false;
            mProcessor.kill();
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }
    }

    @Subscribe
    public void onPreviewOpen(CameraStateEvents.PreviewOpenEvent message) {
        Log.d(TAG, "onPreviewOpen");
    }

    @Subscribe
    public void onPreviewClose(CameraStateEvents.PreviewCloseEvent message) {
        Log.d(TAG, "onPreviewClose");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void createCamera() {
        Log.d(TAG, "createCamera");
        mProcessor = new RenderScriptProcessor(renderScriptManager, ImageFormat.YUV_420_888);
        parametersHandler = new ParameterHandlerApi2(this);
        moduleHandler = new ModuleHandlerApi2(this);
        focusHandler = new FocusHandler(this);

        cameraHolder = new CameraHolderApi2(this);
        cameraBackroundValuesChangedListner = new CameraValuesChangedCaptureCallback(parametersHandler,focusHandler);
        cameraBackroundValuesChangedListner.setWaitForFirstFrameCallback(this);
        captureSessionHandler = new CaptureSessionHandler(this, cameraBackroundValuesChangedListner);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initCamera() {
        Log.d(TAG,"initCamera");
        captureSessionHandler.CreatePreviewRequestBuilder();
        ((FocusHandler) focusHandler).startListning();
        parametersHandler.Init();
        Log.d(TAG, "initCamera Camera Opened and Preview Started");

        CameraStateEvents.fireCameraOpenFinishEvent();
        moduleHandler.setModule(SettingsManager.getInstance().GetCurrentModule());
    }

    @Override
    public void startCamera() {
        startListning();
        Log.d(TAG, "onResume");
        if (PreviewSurfaceRdy && textureHolder.getSurfaceTexture() != null)
            startCameraAsync();
    }

    @Override
    public void stopCamera() {
        try {
            Log.d(TAG, "Stop Camera");
            if (cameraHolder != null)
                cameraHolder.CloseCamera();
            cameraIsOpen = false;
            if (focusHandler != null)
                ((FocusHandler) focusHandler).stopListning();
            if (parametersHandler != null)
                parametersHandler.unregisterListners();
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }
    }

    @Override
    public void restartCamera() {
        Log.d(TAG, "Restart Camera");
        stopCamera();
        startCamera();
    }

    @Override
    public void startPreview() {
        Log.d(TAG, "Start Preview");
        I_PreviewWrapper mi = ((I_PreviewWrapper) moduleHandler.getCurrentModule());
        if (mi != null) {
            mi.startPreview();
        }
    }

    @Override
    public void stopPreview() {
        Log.d(TAG, "Stop Preview");
        if (moduleHandler == null)
            return;
        I_PreviewWrapper mi = ((I_PreviewWrapper) moduleHandler.getCurrentModule());
        if (mi != null) {
            mi.stopPreview();
        }
    }

    @Override
    public void onFirstFrame() {
        Log.d(TAG,"onFirstFrame");
        //workaround, that seem to kill front camera when switching picformat
        if (!SettingsManager.getInstance().getIsFrontCamera())
            parametersHandler.setManualSettingsToParameters();
    }
}
