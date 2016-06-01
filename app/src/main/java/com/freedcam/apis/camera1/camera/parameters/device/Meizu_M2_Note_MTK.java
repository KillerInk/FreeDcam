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
public class Meizu_M2_Note_MTK extends BaseMTKDevice {
    public Meizu_M2_Note_MTK(Handler uihandler, Camera.Parameters parameters, CameraHolderApi1 cameraHolder, CamParametersHandler camParametersHandler) {
        super(uihandler, parameters, cameraHolder, camParametersHandler);
    }

    @Override
    public DngProfile getDngProfile(int filesize)
    {
        switch (filesize) {
            case 26023936:
                return new DngProfile(64, 4192, 3104, DngProfile.Plain, DngProfile.RGGB, 0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
        }
        return null;
    }
}
