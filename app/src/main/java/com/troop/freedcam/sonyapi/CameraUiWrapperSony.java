package com.troop.freedcam.sonyapi;

import android.view.SurfaceView;

import com.troop.freedcam.i_camera.interfaces.I_Module;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.sonyapi.modules.ModuleHandlerSony;
import com.troop.freedcam.sonyapi.parameters.ParameterHandlerSony;
import com.troop.freedcam.sonyapi.sonystuff.ServerDevice;
import com.troop.freedcam.sonyapi.sonystuff.SimpleSsdpClient;
import com.troop.freedcam.sonyapi.sonystuff.SimpleStreamSurfaceView;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 11.12.2014.
 */
public class CameraUiWrapperSony  extends AbstractCameraUiWrapper
{
    protected SimpleStreamSurfaceView surfaceView;

    private SimpleSsdpClient mSsdpClient;
    ServerDevice serverDevice;
    CameraHolderSony cameraHolder;
    AppSettingsManager appSettingsManager;

    public CameraUiWrapperSony(SurfaceView preview, AppSettingsManager appSettingsManager) {
        super(preview, appSettingsManager);
        this.surfaceView = (SimpleStreamSurfaceView)preview;
        this.appSettingsManager = appSettingsManager;
        this.cameraHolder = new CameraHolderSony(preview.getContext(), surfaceView, this, backGroundThread, backGroundHandler, uiHandler);
        camParametersHandler = new ParameterHandlerSony(cameraHolder, appSettingsManager, backGroundHandler, uiHandler);
        cameraHolder.ParameterHandler = (ParameterHandlerSony)camParametersHandler;
        moduleHandler = new ModuleHandlerSony(cameraHolder, appSettingsManager);
        mSsdpClient = new SimpleSsdpClient();
    }

    @Override
    public void SwitchModule(String moduleName) {
        moduleHandler.SetModule(moduleName);
    }


    @Override
    protected void startCamera()
    {
        mSsdpClient.search(new SimpleSsdpClient.SearchResultHandler() {
            @Override
            public void onDeviceFound(ServerDevice device) {
                serverDevice = device;
                appSettingsManager.context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cameraHolder.OpenCamera(serverDevice);
                    }
                });


            }

            @Override
            public void onFinished()
            {
                if (serverDevice == null)
                    onCameraError("");

            }

            @Override
            public void onErrorFinished() {
                    onCameraError("");
            }
        });
        onCameraOpen("");
    }

    @Override
    protected void stopCamera() {
        cameraHolder.CloseCamera();
    }

    @Override
    protected void stopPreview() {
        super.stopPreview();
    }

    @Override
    protected void startPreview() {
        super.startPreview();
    }

    @Override
    public void onCameraOpen(String message) {
        super.onCameraOpen(message);
    }

    @Override
    public void onCameraError(String error) {
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
}
