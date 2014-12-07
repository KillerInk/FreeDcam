package com.troop.freedcam.camera2;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.FocusHandler;
import com.troop.freedcam.camera.I_error;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.TextureView.ExtendedSurfaceView;

/**
 * Created by troop on 07.12.2014.
 */
public class CameraUiWrapperApi2 extends CameraUiWrapper
{
    public BaseCameraHolderApi2 cameraHolder;
    Context context;

    public CameraUiWrapperApi2()
    {

    }

    public CameraUiWrapperApi2(Context context, ExtendedSurfaceView preview, AppSettingsManager appSettingsManager, I_error errorHandler) {
        this.preview = preview;
        this.appSettingsManager = appSettingsManager;
        this.context = context;
        //attache the callback to the Campreview
        preview.getHolder().addCallback(this);
        this.cameraHolder = new BaseCameraHolderApi2(context);
        this.errorHandler = errorHandler;
        cameraHolder.errorHandler = errorHandler;
        camParametersHandler = new CamParametersHandler(cameraHolder);
        cameraHolder.ParameterHandler = camParametersHandler;
        camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
        preview.ParametersHandler = camParametersHandler;

        moduleHandler = new ModuleHandler(cameraHolder, appSettingsManager);
        Focus = new FocusHandler(this);
        cameraHolder.Focus = Focus;
    }

    @Override
    public void ErrorHappend(String error) {
        super.ErrorHappend(error);
    }

    @Override
    public void SwitchModule(String moduleName) {
        super.SwitchModule(moduleName);
    }

    @Override
    public void DoWork() {
        super.DoWork();
    }

    @Override
    public void StartPreviewAndCamera() {
        super.StartPreviewAndCamera();
    }

    @Override
    public void StopPreviewAndCamera() {
        super.StopPreviewAndCamera();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        super.surfaceChanged(holder, format, width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
    }

    @Override
    public void ParametersLoaded() {
        super.ParametersLoaded();
    }

    @Override
    public void onError(int i, Camera camera) {
        super.onError(i, camera);
    }
}
