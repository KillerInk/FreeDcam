package com.freedcam.apis.camera1.camera.parameters.device;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class Retro_MTK extends BaseMTKDevice {
    public Retro_MTK(Handler uihandler, Camera.Parameters parameters, CameraHolderApi1 cameraHolder, CamParametersHandler camParametersHandler) {
        super(uihandler, parameters, cameraHolder, camParametersHandler);
    }

    @Override
    public DngProfile getDngProfile(int filesize)
    {
        //TODO NOT SURE IF THATS THE CORRECT SIZE someone forgot to commit that device...
        switch (filesize) {
            case 5364240: //testing matrix DEVICE????
                return new DngProfile(0, 2688, 1520, DngProfile.Mipi, DngProfile.GRBG, DngProfile.HTCM8_rowSize,
                        matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.OmniVision));
        }
        return null;
    }
}
