package com.troop.freedcam.camera2;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera2.modules.ModuleHandlerApi2;
import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.i_camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;

/**
 * Created by troop on 07.12.2014.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CameraUiWrapperApi2 extends AbstractCameraUiWrapper implements TextureView.SurfaceTextureListener, I_ParametersLoaded

{
    public BaseCameraHolderApi2 cameraHolder;
    Context context;
    AutoFitTextureView preview;
    protected I_error errorHandler;



    private static String TAG = StringUtils.TAG + CameraUiWrapperApi2.class.getSimpleName();

    @Override
    public String CameraApiName() {
        return AppSettingsManager.API_2;
    }

    public CameraUiWrapperApi2()
    {

    }

    public CameraUiWrapperApi2(Context context, AutoFitTextureView preview)
    {
        super();
        this.preview = preview;
        preview.setSurfaceTextureListener(this);
        this.context = context;
        errorHandler = this;
        //attache the callback to the Campreview
        //previewSize.getHolder().addCallback(this);
        this.cameraHolder = new BaseCameraHolderApi2(context, this, uiHandler, backgroundHandler);
        super.cameraHolder = this.cameraHolder;
        camParametersHandler = new ParameterHandlerApi2(this, uiHandler);
        cameraHolder.SetParameterHandler(camParametersHandler);
        moduleHandler = new ModuleHandlerApi2(cameraHolder, backgroundHandler);
        camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
        Focus = new FocusHandlerApi2(this);
        cameraHolder.Focus = Focus;
        Logger.d(TAG, "Constructor done");
    }



    @Override
    protected void startCamera() {
        cameraHolder.OpenCamera(AppSettingsManager.APPSETTINGSMANAGER.GetCurrentCamera());
        Logger.d(TAG, "opencamera");
    }

    @Override
    protected void stopCamera()
    {
        Logger.d(TAG, "Stop Camera");
        cameraHolder.CloseCamera();
    }

    @Override
    protected void startPreview()
    {
        Logger.d(TAG, "Stop Preview");
        cameraHolder.StartPreview();
    }

    @Override
    protected void stopPreview()
    {
        Logger.d(TAG, "Stop Preview");
        cameraHolder.StopPreview();
    }

    @Override
    public void ParametersLoaded()
    {
        //camParametersHandler.PictureSize.addEventListner(this);
        //cameraHolder.StartPreview();
    }

    @Override
    public void onCameraOpen(String message)
    {
        cameraHolder.SetSurface(preview);

        Logger.d(TAG, "Camera Opened and Preview Started");
        super.onCameraOpen(message);
        moduleHandler.SetModule(AppSettingsManager.APPSETTINGSMANAGER.GetCurrentModule());
    }

    @Override
    public void onCameraClose(String message)
    {
        super.onCameraClose(message);
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
        Logger.d(TAG, "SurfaceTextureAvailable");
        if (!PreviewSurfaceRdy) {
            this.PreviewSurfaceRdy = true;
            StartCamera();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
    {
        StopPreview();
        StopCamera();
        Logger.d(TAG, "Surface destroyed");
        this.PreviewSurfaceRdy = false;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void OnError(final String error) {
        super.onCameraError(error);
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
        return null;
    }

}
