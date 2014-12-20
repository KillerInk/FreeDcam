package com.troop.freedcam.camera2;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.TextureView;

import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.camera2.modules.ModuleHandlerApi2;
import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 07.12.2014.
 */
public class CameraUiWrapperApi2 extends AbstractCameraUiWrapper implements TextureView.SurfaceTextureListener, I_ParametersLoaded
{
    public BaseCameraHolderApi2 cameraHolder;
    Context context;
    AppSettingsManager appSettingsManager;
    TextureView preview;

    String TAG = CameraUiWrapperApi2.class.getSimpleName();

    public CameraUiWrapperApi2()
    {

    }

    public CameraUiWrapperApi2(Context context, TextureView preview, AppSettingsManager appSettingsManager)
    {
        super(null, appSettingsManager);
        this.preview = preview;
        preview.setSurfaceTextureListener(this);
        this.appSettingsManager = appSettingsManager;
        this.context = context;
        //attache the callback to the Campreview
        //preview.getHolder().addCallback(this);
        this.cameraHolder = new BaseCameraHolderApi2(context, this, backGroundThread, backGroundHandler, uiHandler);
        super.cameraHolder = this.cameraHolder;
        camParametersHandler = new ParameterHandlerApi2(cameraHolder, appSettingsManager, backGroundHandler, uiHandler);
        cameraHolder.ParameterHandler = (ParameterHandlerApi2)camParametersHandler;
        camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
        //preview.ParametersHandler = camParametersHandler;

        moduleHandler = new ModuleHandlerApi2(cameraHolder, appSettingsManager);
        Focus = new FocusHandlerApi2(this);
        cameraHolder.Focus = Focus;
        Log.d(TAG, "Constructor done");

    }

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
    public void ParametersLoaded() {
        //cameraHolder.StartPreview();
    }

    @Override
    public void onCameraOpen(String message)
    {
        cameraHolder.SetSurface(preview);
        cameraHolder.StartPreview();
        Log.d(TAG, "Camera Opened and Preview Started");
        super.onCameraOpen(message);
    }

    @Override
    public void onCameraClose(String message)
    {

    }

    @Override
    public void onPreviewOpen(String message) {

    }

    @Override
    public void onPreviewClose(String message) {

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
    {
        Log.d(TAG, "SurfaceTextureAvailable");
        PreviewSurfaceRdy = true;
        //StartPreview();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
    {
        StopPreview();
        StopCamera();
        Log.d(TAG, "Surface destroyed");
        PreviewSurfaceRdy = false;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
