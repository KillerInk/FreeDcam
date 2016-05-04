package com.freedcam.apis.sonyremote.sonyapi;

import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.freedcam.apis.i_camera.interfaces.I_Module;
import com.freedcam.apis.sonyremote.sonyapi.modules.ModuleHandlerSony;
import com.freedcam.apis.sonyremote.sonyapi.parameters.ParameterHandlerSony;
import com.freedcam.apis.sonyremote.sonyapi.sonystuff.ServerDevice;
import com.freedcam.apis.sonyremote.sonyapi.sonystuff.SimpleStreamSurfaceView;
import com.freedcam.apis.i_camera.AbstractCameraUiWrapper;
import com.freedcam.ui.AppSettingsManager;
import com.freedcam.utils.FreeDPool;

/**
 * Created by troop on 11.12.2014.
 */
public class CameraUiWrapperSony  extends AbstractCameraUiWrapper implements SurfaceHolder.Callback
{
    protected SimpleStreamSurfaceView surfaceView;

    public ServerDevice serverDevice;
    CameraHolderSony cameraHolder;


    @Override
    public String CameraApiName() {
        return AppSettingsManager.API_SONY;
    }

    public CameraUiWrapperSony(SurfaceView preview) {
        super();
        this.surfaceView = (SimpleStreamSurfaceView)preview;
        this.surfaceView.getHolder().addCallback(this);
        this.cameraHolder = new CameraHolderSony(preview.getContext(), surfaceView, this, uiHandler);
        camParametersHandler = new ParameterHandlerSony(this, uiHandler, (SimpleStreamSurfaceView)surfaceView);
        cameraHolder.ParameterHandler = (ParameterHandlerSony)camParametersHandler;

        moduleHandler = new ModuleHandlerSony(cameraHolder);
        this.Focus = new FocusHandlerSony(this);
        super.cameraHolder = cameraHolder;
        cameraHolder.focusHandlerSony =(FocusHandlerSony) Focus;
        cameraHolder.moduleHandlerSony = (ModuleHandlerSony)moduleHandler;
    }


    @Override
    public void StartCamera()
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                cameraHolder.OpenCamera(serverDevice);
                onCameraOpen("");
            }
        });
    }

    @Override
    public void StopCamera() {
        cameraHolder.CloseCamera();
    }


    @Override
    public void onCameraOpen(String message) {
        super.onCameraOpen(message);
    }

    @Override
    public void onCameraError(String error) {
        cameraHolder.isPreviewRunning = false;
        PreviewSurfaceRdy = false;
        surfaceView.stop();
        super.onCameraError(error);
    }

    @Override
    public void onCameraStatusChanged(String status) {
        super.onCameraStatusChanged(status);
    }

    @Override
    public void onModuleChanged(I_Module module) {
        super.onModuleChanged(module);
    }

    @Override
    public void DoWork() {
        moduleHandler.DoWork();
    }

    @Override
    public void onCameraClose(String message) {

    }

    @Override
    public void onPreviewOpen(String message) {

    }

    @Override
    public void onPreviewClose(String message) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        StopPreview();
        StopCamera();
    }

    @Override
    public void OnError(String error) {
        super.onCameraError(error);
    }

    @Override
    public int getMargineLeft() {
        return surfaceView.getLeft();
    }

    @Override
    public int getMargineRight() {
        return surfaceView.getRight();
    }

    @Override
    public int getMargineTop() {
        return surfaceView.getTop();
    }

    @Override
    public int getPreviewWidth() {
        return surfaceView.getWidth();
    }

    @Override
    public int getPreviewHeight() {
        return surfaceView.getHeight();
    }

    @Override
    public SurfaceView getSurfaceView() {
        return surfaceView;
    }
}