package com.freedcam.apis.camera2.camera2;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.view.SurfaceView;
import android.view.TextureView;

import com.freedcam.apis.camera2.camera2.modules.ModuleHandlerApi2;
import com.freedcam.apis.camera2.camera2.parameters.ParameterHandlerApi2;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;
import com.freedcam.apis.i_camera.AbstractCameraUiWrapper;
import com.freedcam.apis.i_camera.interfaces.I_error;
import com.freedcam.utils.StringUtils;


/**
 * Created by troop on 07.12.2014.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CameraUiWrapperApi2 extends AbstractCameraUiWrapper implements TextureView.SurfaceTextureListener

{
    public BaseCameraHolderApi2 cameraHolder;
    private Context context;
    private AutoFitTextureView preview;
    protected I_error errorHandler;

    private static String TAG = StringUtils.TAG + CameraUiWrapperApi2.class.getSimpleName();

    @Override
    public String CameraApiName() {
        return AppSettingsManager.API_2;
    }

    public CameraUiWrapperApi2(Context context, AutoFitTextureView preview)
    {
        super();
        this.preview = preview;
        this.preview.setSurfaceTextureListener(this);
        this.context = context;
        this.errorHandler = this;
        this.cameraHolder = new BaseCameraHolderApi2(context, this, uiHandler);
        super.cameraHolder = this.cameraHolder;
        this.camParametersHandler = new ParameterHandlerApi2(this, uiHandler);
        this.cameraHolder.SetParameterHandler(camParametersHandler);
        this.moduleHandler = new ModuleHandlerApi2(cameraHolder);
        this.Focus = new FocusHandlerApi2(this);
        this.cameraHolder.Focus = Focus;
        Logger.d(TAG, "Constructor done");
    }

    @Override
    public void StartCamera() {
        cameraHolder.OpenCamera(AppSettingsManager.APPSETTINGSMANAGER.GetCurrentCamera());
        Logger.d(TAG, "opencamera");
    }

    @Override
    public void StopCamera() {
        Logger.d(TAG, "Stop Camera");
        cameraHolder.CloseCamera();
    }

    @Override
    public void StartPreview() {
        Logger.d(TAG, "Stop Preview");
        cameraHolder.StartPreview();
    }

    @Override
    public void StopPreview()
    {
        Logger.d(TAG, "Stop Preview");
        cameraHolder.StopPreview();
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
