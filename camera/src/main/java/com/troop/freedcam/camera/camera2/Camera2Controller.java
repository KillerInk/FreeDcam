package com.troop.freedcam.camera.camera2;

import android.graphics.ImageFormat;
import android.graphics.Point;
import android.os.Build;
import android.util.Size;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.camera.basecamera.AbstractCameraController;
import com.troop.freedcam.camera.camera2.modules.I_PreviewWrapper;
import com.troop.freedcam.camera.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.camera.events.CameraStateEvents;
import com.troop.freedcam.processor.RenderScriptProcessor;
import com.troop.freedcam.processor.RenderScriptProcessorInterface;
import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.utils.Log;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Camera2Controller extends AbstractCameraController<ParameterHandlerApi2,CameraHolderApi2,FocusHandler,ModuleHandlerApi2> implements CameraValuesChangedCaptureCallback.WaitForFirstFrameCallback {

    private final String TAG = Camera2Controller.class.getSimpleName();
    private RenderScriptProcessor mProcessor;
    private boolean cameraIsOpen = false;
    public CaptureSessionHandler captureSessionHandler;
    public CameraValuesChangedCaptureCallback cameraBackroundValuesChangedListner;
    //limits the preview to use maximal that size for preview
    //when set to high it its possbile to get a laggy preview with active focuspeak
    public static int MAX_PREVIEW_WIDTH = 1920;
    public static int MAX_PREVIEW_HEIGHT = 1080;

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

        CameraStateEvents.fireCameraOpenFinishEvent(this);
        moduleHandler.setModule(SettingsManager.getInstance().GetCurrentModule());
    }

    @Override
    public void startCamera() {
        startListning();
        Log.d(TAG, "onResume");
        if (textureHolder.getSurfaceTexture() != null) {
            int[] ids = SettingsManager.getInstance().getCameraIds();
            int currentid = SettingsManager.getInstance().GetCurrentCamera();
            cameraIsOpen = cameraHolder.OpenCamera(ids[currentid]);
        }
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Size getSizeForPreviewDependingOnImageSize(int imageformat, int mImageWidth, int mImageHeight)
    {
        List<Size> sizes = new ArrayList<>();
        Size[] choices = ((CameraHolderApi2)cameraHolder).map.getOutputSizes(imageformat);
        Point displaysize = captureSessionHandler.getDisplaySize();
        double ratio = (double)mImageWidth/mImageHeight;
        for (Size s : choices)
        {
            if (s.getWidth() <= MAX_PREVIEW_WIDTH && s.getHeight() <= MAX_PREVIEW_HEIGHT && ratioMatch((double)s.getWidth()/s.getHeight(),ratio))
                sizes.add(s);
        }
        if (sizes.size() > 0) {
            return Collections.max(sizes, new CameraHolderApi2.CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable previewSize size");
            Size s = choices[0];
            if (s.getWidth() > displaysize.x && s.getHeight() > displaysize.y)
                return new Size(displaysize.x, displaysize.y);
            return choices[0];
        }
    }

    private boolean ratioMatch(double preview, double image)
    {
        double rangelimter = 0.1;

        if (preview+rangelimter >= image && preview-rangelimter <= image)
            return true;
        else
            return false;
    }

    @Override
    public RenderScriptProcessorInterface getFocusPeakProcessor() {
        return mProcessor;
    }
}
