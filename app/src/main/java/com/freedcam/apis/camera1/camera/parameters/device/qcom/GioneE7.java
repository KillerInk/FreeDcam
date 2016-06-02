package com.freedcam.apis.camera1.camera.parameters.device.qcom;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.camera1.camera.parameters.device.BaseQcomDevice;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class GioneE7 extends BaseQcomDevice {
    public GioneE7(Handler uihandler, Camera.Parameters parameters, CameraUiWrapper cameraUiWrapper) {
        super(uihandler, parameters, cameraUiWrapper);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }

    @Override
    public DngProfile getDngProfile(int filesize)
    {
        switch (filesize)
        {
            case 9990144://e7 front mipi
                return new DngProfile(16, 3264, 2448, DngProfile.Mipi, DngProfile.BGGR, 4080,matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 10653696://e7 front qcom
            {
                //TODO somethings wrong with it;
                return new DngProfile(16, 3264, 2448, DngProfile.Qcom, DngProfile.BGGR, 0,matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            }
            case 19906560://e7mipi
                return new DngProfile(16, 4608, 3456, DngProfile.Mipi, DngProfile.BGGR, 0,
                        matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.OmniVision));
            case 21233664: //e7qcom
                return new DngProfile(16, 4608, 3456, DngProfile.Qcom, DngProfile.BGGR, 0,
                        matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.OmniVision));
        }
        return null;
    }
}
