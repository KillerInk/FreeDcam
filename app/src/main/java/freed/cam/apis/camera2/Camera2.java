package freed.cam.apis.camera2;

import android.graphics.Point;
import android.os.Build;
import android.util.Size;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.AbstractCamera;
import freed.cam.apis.basecamera.CameraThreadHandler;
import freed.cam.apis.camera2.modules.I_PreviewWrapper;
import freed.cam.apis.camera2.parameters.ParameterHandlerApi2;
import freed.cam.apis.camera2.parameters.ae.FreedAeManger;
import freed.cam.previewpostprocessing.PreviewPostProcessingModes;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2 extends AbstractCamera<ParameterHandlerApi2,CameraHolderApi2,ModuleHandlerApi2,FocusHandler> implements CameraValuesChangedCaptureCallback.WaitForFirstFrameCallback {
    private static final String TAG = Camera2.class.getSimpleName();

    //limits the preview to use maximal that size for preview
    //when set to high it its possbile to get a laggy preview with active focuspeak
    public static int MAX_PREVIEW_WIDTH = 1920;
    public static int MAX_PREVIEW_HEIGHT = 1080;

    public CaptureSessionHandler captureSessionHandler;
    public CameraValuesChangedCaptureCallback cameraBackroundValuesChangedListner;
    private boolean cameraIsOpen = false;
    private FreedAeManger freedAeManger;

    public Camera2()
    {
        super();
        Log.d(TAG, "createCamera");
        parametersHandler = new ParameterHandlerApi2(this);
        moduleHandler = new ModuleHandlerApi2(this);
        focusHandler = new FocusHandler(this);

        cameraHolder = new CameraHolderApi2(this);
        cameraBackroundValuesChangedListner = new CameraValuesChangedCaptureCallback(this);
        cameraBackroundValuesChangedListner.setWaitForFirstFrameCallback(this);
        if (settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get().equals(PreviewPostProcessingModes.OpenGL.name()) && settingsManager.get(SettingKeys.HISTOGRAM_STATS_QCOM).get())
            preview.setHistogramFeed(cameraBackroundValuesChangedListner);
        captureSessionHandler = new CaptureSessionHandler(this, cameraBackroundValuesChangedListner);
        freedAeManger = new FreedAeManger(this, ActivityFreeDcamMain.userMessageHandler(),settingsManager);
    }


    @Override
    public void initCamera() {
        Log.d(TAG,"initCamera");
        captureSessionHandler.CreatePreviewRequestBuilder();
        parametersHandler.Init();
        //cameraHolder.SetSurface(getPreview().getSurfaceTexture());
        Log.d(TAG, "initCamera Camera Opened and Preview Started");
        cameraHolder.fireCameraOpenFinished();
        moduleHandler.setModule(settingsManager.GetCurrentModule());
        //parametersHandler.SetAppSettingsToParameters();
    }

    @Override
    public void startCamera() {
        if (!cameraIsOpen && cameraHolder != null) {
            Log.d(TAG, "Start Camera");
            cameraIsOpen = cameraHolder.OpenCamera(settingsManager.getCameraIds()[settingsManager.GetCurrentCamera()]);
        } else
            Log.d(TAG, "Camera is already open");
    }

    @Override
    public void stopCamera() {
        try {
            Log.d(TAG, "Stop Camera");
            if (cameraHolder != null)
                cameraHolder.CloseCamera();
            cameraIsOpen = false;
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
        if (!settingsManager.getIsFrontCamera())
            parametersHandler.setManualSettingsToParameters();
        if (settingsManager.getGlobal(SettingKeys.USE_FREEDCAM_AE).get())
            freedAeManger.turnDefaultAeOff();
    }

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
    public void onCameraOpen() {
        Log.d(TAG, "onCameraOpen, initCamera");
        CameraThreadHandler.initCameraAsync();
        if (settingsManager.getGlobal(SettingKeys.USE_FREEDCAM_AE).get())
            freedAeManger.start();
    }

    @Override
    public void onCameraOpenFinished() {
        Log.d(TAG, "onCameraOpenFinished");
    }

    @Override
    public void onCameraClose() {
        if (settingsManager.getGlobal(SettingKeys.USE_FREEDCAM_AE).get())
            freedAeManger.stop();
        try {
            Log.d(TAG, "onCameraClose");
            cameraIsOpen = false;
            preview.close();
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }
    }

    @Override
    public void onCameraError(String error) {
        Log.d(TAG, "onCameraError " +error);
    }

    @Override
    public void onCameraChangedAspectRatioEvent(freed.cam.apis.basecamera.Size size) {

    }

    public FreedAeManger getFreedAeManger() {
        return freedAeManger;
    }
}
