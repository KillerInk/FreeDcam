package com.troop.freedcam.sonyapi;

import android.view.SurfaceView;

import com.troop.freedcam.camera.I_error;
import com.troop.freedcam.camera2.modules.ModuleHandlerApi2;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.sonyapi.modules.ModuleHandlerSony;
import com.troop.freedcam.sonyapi.parameters.ParameterHandlerSony;
import com.troop.freedcam.sonyapi.sonystuff.ServerDevice;
import com.troop.freedcam.sonyapi.sonystuff.SimpleSsdpClient;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.TextureView.ExtendedSurfaceView;

/**
 * Created by troop on 11.12.2014.
 */
public class CameraUiWrapperSony  extends AbstractCameraUiWrapper
{
    protected SurfaceView extendedSurfaceView;

    private SimpleSsdpClient mSsdpClient;
    ServerDevice serverDevice;
    CameraHolderSony cameraHolder;

    public CameraUiWrapperSony()
    {

    }

    public CameraUiWrapperSony(SurfaceView preview, AppSettingsManager appSettingsManager, I_error errorHandler) {
        super(preview, appSettingsManager, errorHandler);
        this.extendedSurfaceView = preview;
        this.cameraHolder = new CameraHolderSony();
        camParametersHandler = new ParameterHandlerSony(cameraHolder, appSettingsManager);
        moduleHandler = new ModuleHandlerSony(cameraHolder, appSettingsManager);
        mSsdpClient = new SimpleSsdpClient();
        mSsdpClient.search(new SimpleSsdpClient.SearchResultHandler() {
            @Override
            public void onDeviceFound(ServerDevice device) {
                serverDevice = device;
            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onErrorFinished() {

            }
        });
    }

    @Override
    public void SwitchModule(String moduleName) {
        super.SwitchModule(moduleName);
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
    public void DoWork() {
        super.DoWork();
    }
}
