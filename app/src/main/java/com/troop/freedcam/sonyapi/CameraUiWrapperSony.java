package com.troop.freedcam.sonyapi;

import android.view.SurfaceView;

import com.troop.freedcam.camera.I_error;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.TextureView.ExtendedSurfaceView;

/**
 * Created by troop on 11.12.2014.
 */
public class CameraUiWrapperSony  extends AbstractCameraUiWrapper
{
    protected ExtendedSurfaceView extendedSurfaceView;

    public CameraUiWrapperSony() {
    }

    public CameraUiWrapperSony(ExtendedSurfaceView preview, AppSettingsManager appSettingsManager, I_error errorHandler) {
        super(preview, appSettingsManager, errorHandler);
        this.extendedSurfaceView = preview;
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
