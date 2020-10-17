package com.troop.freedcam.camera.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import com.troop.freedcam.R;

import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.camera2.Camera2Fragment;

/**
 * Created by Ingo on 03.10.2016.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AeLockModeApi2 extends BaseModeApi2 {
    public AeLockModeApi2(CameraControllerInterface cameraUiWrapper) {
        super(cameraUiWrapper, null);
        setViewState(ViewState.Visible);
    }


    @Override
    public String GetStringValue() {
        if (((Camera2Fragment)cameraUiWrapper).captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AE_LOCK))
            return ContextApplication.getStringFromRessources(R.string.true_);
        else
            return ContextApplication.getStringFromRessources(R.string.false_);
    }

    @Override
    public String[] getStringValues() {
        return new String[]{ContextApplication.getStringFromRessources(R.string.false_), ContextApplication.getStringFromRessources(R.string.true_)};
    }

    @Override
    public void setValue(String valueToSet, boolean setToCamera) {

        ((Camera2Fragment) cameraUiWrapper).captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AE_LOCK, valueToSet.equals(ContextApplication.getStringFromRessources(R.string.true_)), setToCamera);
    }
}
