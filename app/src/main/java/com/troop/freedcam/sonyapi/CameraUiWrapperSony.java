package com.troop.freedcam.sonyapi;

import android.view.SurfaceView;

import com.troop.freedcam.camera.I_error;
import com.troop.freedcam.camera2.modules.ModuleHandlerApi2;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.sonyapi.modules.ModuleHandlerSony;
import com.troop.freedcam.sonyapi.parameters.ParameterHandlerSony;
import com.troop.freedcam.sonyapi.sonystuff.ServerDevice;
import com.troop.freedcam.sonyapi.sonystuff.SimpleSsdpClient;
import com.troop.freedcam.sonyapi.sonystuff.SimpleStreamSurfaceView;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.TextureView.ExtendedSurfaceView;

/**
 * Created by troop on 11.12.2014.
 */
public class CameraUiWrapperSony  extends AbstractCameraUiWrapper
{
    protected SimpleStreamSurfaceView surfaceView;

    private SimpleSsdpClient mSsdpClient;
    ServerDevice serverDevice;
    CameraHolderSony cameraHolder;

    public CameraUiWrapperSony()
    {

    }

    public CameraUiWrapperSony(SurfaceView preview, AppSettingsManager appSettingsManager, I_error errorHandler) {
        super(preview, appSettingsManager, errorHandler);
        this.surfaceView = (SimpleStreamSurfaceView)preview;
        this.cameraHolder = new CameraHolderSony(preview.getContext(), surfaceView);
        camParametersHandler = new ParameterHandlerSony(cameraHolder, appSettingsManager);
        moduleHandler = new ModuleHandlerSony(cameraHolder, appSettingsManager);
        mSsdpClient = new SimpleSsdpClient();
        StartPreviewAndCamera();

    }

    @Override
    public void SwitchModule(String moduleName) {
        moduleHandler.SetModule(moduleName);
    }

    @Override
    public void StartPreviewAndCamera()
    {
        ErrorHappend("Start searching for PlayMemory Device");
        mSsdpClient.search(new SimpleSsdpClient.SearchResultHandler() {
            @Override
            public void onDeviceFound(ServerDevice device)
            {
                ErrorHappend("Found Device " + device.getModelName());
                serverDevice = device;
                cameraHolder.OpenCamera(serverDevice);

            }

            @Override
            public void onFinished()
            {
                ErrorHappend("Finished Searching");
            }

            @Override
            public void onErrorFinished()
            {
                ErrorHappend("Searching faild");
            }
        });
    }

    @Override
    public void StopPreviewAndCamera() {
        cameraHolder.CloseCamera();
    }

    @Override
    public void DoWork() {
        moduleHandler.DoWork();
    }

    @Override
    public void ErrorHappend(String error) {
        super.ErrorHappend(error);
    }
}
