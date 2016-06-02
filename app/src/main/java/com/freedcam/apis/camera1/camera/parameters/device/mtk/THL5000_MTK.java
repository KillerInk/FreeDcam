package com.freedcam.apis.camera1.camera.parameters.device.mtk;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera1.camera.parameters.device.BaseMTKDevice;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class THL5000_MTK extends BaseMTKDevice {
    public THL5000_MTK(Handler uihandler, Camera.Parameters parameters, CameraUiWrapper cameraUiWrapper) {
        super(uihandler, parameters, cameraUiWrapper);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        return new DngProfile(64, 4192, 3104, DngProfile.Plain, DngProfile.RGGB, 0,matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
    }
}
