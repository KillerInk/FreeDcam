package com.freedcam.apis.camera1.camera;


import android.content.Context;
import android.os.Build;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.apis.basecamera.camera.Size;
import com.freedcam.apis.basecamera.camera.interfaces.I_Module;
import com.freedcam.apis.basecamera.camera.interfaces.I_error;
import com.freedcam.apis.basecamera.camera.modules.I_Callbacks;
import com.freedcam.apis.basecamera.camera.modules.I_ModuleEvent;
import com.freedcam.apis.basecamera.camera.parameters.I_ParametersLoaded;
import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter;
import com.freedcam.apis.camera1.camera.modules.ModuleHandler;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.camera1.camera.renderscript.FocusPeakProcessorAp1;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.Logger;
import com.freedcam.utils.RenderScriptHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by troop on 16.08.2014.
 */
public class CameraUiWrapper extends AbstractCameraUiWrapper implements SurfaceHolder.Callback, I_ParametersLoaded, I_Callbacks.ErrorCallback, I_ModuleEvent
{
    protected ExtendedSurfaceView preview;
    protected I_error errorHandler;
    private static String TAG = CameraUiWrapper.class.getSimpleName();
    public CameraHolderApi1 cameraHolder;
    public FocusPeakProcessorAp1 focusPeakProcessorAp1;
    boolean cameraRdy = false;

    @Override
    public String CameraApiName() {
        return AppSettingsManager.API_1;
    }

    public CameraUiWrapper(SurfaceView preview, TextureViewRatio previewTexture, Context context, AppSettingsManager appSettingsManager, RenderScriptHandler renderScriptHandler)
    {
        super(appSettingsManager);

        this.preview = (ExtendedSurfaceView)preview;
        //attache the callback to the Campreview
        preview.getHolder().addCallback(this);

        this.errorHandler = this;
        this.cameraHolder = new CameraHolderApi1(this, uiHandler,appSettingsManager);
        super.cameraHolder = cameraHolder;
        this.cameraHolder.errorHandler = errorHandler;

        this.camParametersHandler = new CamParametersHandler(this, uiHandler,context,appSettingsManager);
        this.cameraHolder.SetParameterHandler(camParametersHandler);
        camParametersHandler.AddParametersLoadedListner(this);
        this.preview.ParametersHandler = camParametersHandler;
        //camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this.preview);
        moduleHandler = new ModuleHandler(cameraHolder,context,appSettingsManager);
        moduleHandler.moduleEventHandler.addListner(this);

        Focus = new FocusHandler(this);
        this.cameraHolder.Focus = Focus;
        if (Build.VERSION.SDK_INT >= 18) {
            focusPeakProcessorAp1 = new FocusPeakProcessorAp1(previewTexture, this, context,renderScriptHandler);
            SetCameraChangedListner(focusPeakProcessorAp1);
        }
        else
            previewTexture.setVisibility(View.GONE);
        Logger.d(TAG, "Ctor done");
    }


    @Override
    public void StartCamera() {
        cameraHolder.OpenCamera(appSettingsManager.GetCurrentCamera());
        Logger.d(TAG, "opencamera");
    }

    @Override
    public void StopCamera() {
        Logger.d(TAG, "Stop Camera");
        cameraHolder.CloseCamera();
    }

    @Override
    public void StartPreview() {
        cameraHolder.StartPreview();
    }

    @Override
    public void StopPreview() {
        Logger.d(TAG, "Stop Preview");
        cameraHolder.StopPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        Logger.d(TAG, "surface created");
        PreviewSurfaceRdy = true;
        StartCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        PreviewSurfaceRdy =false;

        StopPreview();
        StopCamera();
    }

    @Override
    public void ParametersLoaded()
    {
        camParametersHandler.PictureSize.addEventListner(onPreviewSizeShouldChange);
        //camParametersHandler.VideoSize.addEventListner(onPreviewSizeShouldChange);
    }

    @Override
    public void onError(int i)
    {
        errorHandler.OnError("Got Error from camera: " + i);
        try
        {
            cameraHolder.CloseCamera();
        }
        catch (Exception ex)
        {
           Logger.e(TAG,ex.getMessage());
        }
    }

    //this gets called when the cameraholder has open the camera
    @Override
    public void onCameraOpen(String message)
    {
        cameraRdy = true;
        super.onCameraOpen(message);
        ((CamParametersHandler)camParametersHandler).LoadParametersFromCamera();
        cameraHolder.SetErrorCallback(CameraUiWrapper.this);
        cameraHolder.SetSurface(preview.getHolder());
        cameraHolder.StartPreview();
        super.onCameraOpenFinish("");
    }

    @Override
    public void onCameraClose(String message)
    {
        cameraRdy = false;
        super.onCameraClose(message);
    }

    @Override
    public void onPreviewOpen(String message) {
        super.onPreviewOpen(message);

    }

    @Override
    public void onPreviewClose(String message) {
        super.onPreviewClose(message);
        cameraHolder.ResetPreviewCallback();
    }

    @Override
    public void onCameraError(String error) {
        super.onCameraError(error);
    }

    @Override
    public void onCameraStatusChanged(String status)
    {
        super.onCameraStatusChanged(status);
    }

    @Override
    public void onModuleChanged(I_Module module) {
        super.onModuleChanged(module);
    }

    @Override
    public void onCameraOpenFinish(String message) {
        super.onCameraOpenFinish(message);
    }

    @Override
    public void OnError(String error) {
        super.onCameraError(error);
    }

    AbstractModeParameter.I_ModeParameterEvent onPreviewSizeShouldChange = new AbstractModeParameter.I_ModeParameterEvent() {

        @Override
        public void onValueChanged(String val)
        {
            if(moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_PICTURE)
                    || moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_HDR)
                    || moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_INTERVAL)
                    || moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_STACKING))
            {
                Size sizefromCam = new Size(camParametersHandler.PictureSize.GetValue());
                List<Size> sizes = new ArrayList<>();
                String[] stringsSizes = camParametersHandler.PreviewSize.GetValues();
                for (String s : stringsSizes) {
                    sizes.add(new Size(s));
                }
                final Size size = getOptimalPreviewSize(sizes, sizefromCam.width, sizefromCam.height);
                Logger.d(TAG, "set size to " + size.width + "x" + size.height);

                camParametersHandler.PreviewSize.SetValue(size.width + "x" + size.height, true);
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (preview != null)
                            preview.setAspectRatio(size.width, size.height);
                        if (focusPeakProcessorAp1 != null)
                            focusPeakProcessorAp1.SetAspectRatio(size.width,size.height);
                    }
                });

            }
            else if (moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_LONGEXPO) || moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_VIDEO))
            {
                Size sizefromCam = new Size("1920x1080");

                List<Size> sizes = new ArrayList<>();
                String[] stringsSizes = camParametersHandler.PreviewSize.GetValues();
                for (String s : stringsSizes) {
                    sizes.add(new Size(s));
                }
                final Size size = getOptimalPreviewSize(sizes, sizefromCam.width, sizefromCam.height);
                Logger.d(TAG, "set size to " + size.width + "x" + size.height);
                camParametersHandler.PreviewSize.SetValue(size.width + "x" + size.height, true);
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (preview != null)
                            preview.setAspectRatio(size.width, size.height);
                        if (focusPeakProcessorAp1 != null)
                            focusPeakProcessorAp1.SetAspectRatio(size.width,size.height);
                    }
                });

            }
        }

        @Override
        public void onIsSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onIsSetSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onValuesChanged(String[] values) {

        }

        @Override
        public void onVisibilityChanged(boolean visible) {

        }
    };

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.2;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;
        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        // Try to find an size match aspect ratio and size
        for (Size size : sizes)
        {
            if(DeviceUtils.IS(DeviceUtils.Devices.ZTE_ADV)) {
                if (size.width <= 1440 && size.height <= 1080 && size.width >= 640 && size.height >= 480) {
                    double ratio = (double) size.width / size.height;
                    if (ratio < targetRatio + ASPECT_TOLERANCE && ratio > targetRatio - ASPECT_TOLERANCE) {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - h);
                        break;
                    }

                }
            }
            else {
                if (size.width <= 1280 && size.height <= 720 && size.width >= 640 && size.height >= 480) {
                    double ratio = (double) size.width / size.height;
                    if (ratio < targetRatio + ASPECT_TOLERANCE && ratio > targetRatio - ASPECT_TOLERANCE) {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - h);
                        break;
                    }

                }
            }
        }
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes)
            {
                if(DeviceUtils.IS(DeviceUtils.Devices.ZTE_ADV)) {
                    if (size.width <= 1440 && size.height <= 1080 && size.width >= 640 && size.height >= 480) {
                        if (Math.abs(size.height - h) < minDiff) {
                            optimalSize = size;
                            minDiff = Math.abs(size.height - h);
                        }
                    }
                }
                else {
                    if (size.width <= 1280 && size.height <= 720 && size.width >= 640 && size.height >= 480) {
                        if (Math.abs(size.height - h) < minDiff) {
                            optimalSize = size;
                            minDiff = Math.abs(size.height - h);
                        }
                    }
                }
            }
        }
        Logger.d(TAG, "Optimal preview size " + optimalSize.width + "x" + optimalSize.height);
        return optimalSize;
    }

    @Override
    public void ModuleChanged(String module)
    {
        onPreviewSizeShouldChange.onValueChanged("");
    }

    @Override
    public int getMargineLeft() {
        return preview.getLeft();
    }

    @Override
    public int getMargineRight() {
        return preview.getRight();
    }

    @Override
    public int getMargineTop() {
        return preview.getTop();
    }

    @Override
    public int getPreviewWidth() {
        return preview.getWidth();
    }

    @Override
    public int getPreviewHeight() {
        return preview.getHeight();
    }

    @Override
    public SurfaceView getSurfaceView() {
        return preview;
    }
}
