package com.troop.freedcam.camera;


import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import com.troop.freedcam.PreviewHandler;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesG3Parameter;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesParameter;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.interfaces.I_Module;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.i_camera.modules.I_Callbacks;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by troop on 16.08.2014.
 */
public class CameraUiWrapper extends AbstractCameraUiWrapper implements SurfaceHolder.Callback, I_ParametersLoaded, I_Callbacks.ErrorCallback, I_ModuleEvent
{
    protected ExtendedSurfaceView preview;
    protected I_error errorHandler;
    public AppSettingsManager appSettingsManager;
    private static String TAG = CameraUiWrapper.class.getSimpleName();
    public BaseCameraHolder cameraHolder;
    PreviewHandler previewHandler;



    @Override
    public String CameraApiName() {
        return AppSettingsManager.API_1;
    }

    public CameraUiWrapper(SurfaceView preview,TextureViewRatio previewTexture, AppSettingsManager appSettingsManager)
    {
        super(appSettingsManager);
        this.preview = (ExtendedSurfaceView)preview;
        this.appSettingsManager = appSettingsManager;
        //attache the callback to the Campreview
        preview.getHolder().addCallback(this);

        this.errorHandler = this;
        this.cameraHolder = new BaseCameraHolder(this, uiHandler);
        super.cameraHolder = cameraHolder;
        this.cameraHolder.errorHandler = errorHandler;

        camParametersHandler = new CamParametersHandler(this, appSettingsManager, uiHandler);
        this.cameraHolder.ParameterHandler = camParametersHandler;
        camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
        this.preview.ParametersHandler = camParametersHandler;
        //camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this.preview);
        moduleHandler = new ModuleHandler(cameraHolder, appSettingsManager);
        moduleHandler.moduleEventHandler.addListner(this);

        Focus = new FocusHandler(this);
        this.cameraHolder.Focus = Focus;
        previewHandler = new PreviewHandler(previewTexture, this, appSettingsManager.context);
        Log.d(TAG, "Ctor done");


    }

    //this get handled in backgroundThread when StartPreviewAndCamera() was called
    @Override
    protected void startCamera() {
        cameraHolder.OpenCamera(appSettingsManager.GetCurrentCamera());
        Log.d(TAG, "opencamera");
    }

    @Override
    protected void stopCamera()
    {
        Log.d(TAG, "Stop Camera");
        cameraHolder.CloseCamera();
    }

    @Override
    protected void startPreview()
    {
        Log.d(TAG, "Stop Preview");
        cameraHolder.StartPreview();
    }

    @Override
    protected void stopPreview()
    {
        Log.d(TAG, "Stop Preview");
        cameraHolder.StopPreview();
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        Log.d(TAG, "surface created");
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
        StopCamera();
    }

    @Override
    public void ParametersLoaded()
    {
        camParametersHandler.PictureSize.addEventListner(onPreviewSizeShouldChange);
        camParametersHandler.VideoSize.addEventListner(onPreviewSizeShouldChange);


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
            ex.printStackTrace();
        }
    }

    //this gets called when the cameraholder has open the camera
    @Override
    public void onCameraOpen(String message)
    {
        super.onCameraOpen(message);
        cameraHolder.SetErrorCallback(this);
        cameraHolder.SetSurface(preview.getHolder());
        CamParametersHandler camParametersHandler1 = (CamParametersHandler) camParametersHandler;
        camParametersHandler1.LoadParametersFromCamera();
        cameraHolder.StartPreview();

        super.onCameraOpenFinish("");


    }

    @Override
    public void onCameraClose(String message)
    {
        super.onCameraClose(message);
    }

    @Override
    public void onPreviewOpen(String message) {
        super.onPreviewOpen(message);
        cameraHolder.SetPreviewCallback(previewHandler);
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
            if(moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_PICTURE) || moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_HDR))
            {
                Size sizefromCam = new Size(camParametersHandler.PictureSize.GetValue());
                List<Size> sizes = new ArrayList<Size>();
                String[] stringsSizes = camParametersHandler.PreviewSize.GetValues();
                for (String s : stringsSizes) {
                    sizes.add(new Size(s));
                }
                Size size = getOptimalPreviewSize(sizes, sizefromCam.width, sizefromCam.height);
                Log.d(TAG, "set size to " + size.width + "x" + size.height);

                camParametersHandler.PreviewSize.SetValue(size.width + "x" + size.height, true);
                if (preview != null)
                    preview.setAspectRatio(size.width, size.height);
                if (previewHandler != null)
                    previewHandler.SetAspectRatio(size.width,size.height);
            }
            else if (moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_LONGEXPO) || moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_VIDEO))
            {
                Size sizefromCam = new Size("1920x1080");

                List<Size> sizes = new ArrayList<Size>();
                String[] stringsSizes = camParametersHandler.PreviewSize.GetValues();
                for (String s : stringsSizes) {
                    sizes.add(new Size(s));
                }
                Size size = getOptimalPreviewSize(sizes, sizefromCam.width, sizefromCam.height);
                Log.d(TAG, "set size to " + size.width + "x" + size.height);
                camParametersHandler.PreviewSize.SetValue(size.width + "x" + size.height, true);
                if (preview != null)
                    preview.setAspectRatio(size.width, size.height);
                if (previewHandler != null)
                    previewHandler.SetAspectRatio(size.width,size.height);
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
    };

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.2;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;
        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        // Try to find an size match aspect ratio and size
        for (Size size : sizes)
        {
            if (size.width <= 1280) {
                double ratio = (double) size.width / size.height;
                if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes)
            {
                if (size.width <= 1280 ) {
                    if (Math.abs(size.height - targetHeight) < minDiff) {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - targetHeight);
                    }
                }
            }
        }
        Log.d(TAG,"Optimal preview size " +optimalSize.width + "x" + optimalSize.height);
        return optimalSize;
    }

    @Override
    public String ModuleChanged(String module)
    {
        onPreviewSizeShouldChange.onValueChanged("");
        return null;
    }
}
