package com.freedcam.apis.camera1.parameters.device.mtk;

import android.content.Context;
import android.hardware.Camera;

import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.parameters.device.BaseMTKDevice;
import com.troop.androiddng.DngProfile;

/**
 * Created by GeorgeKiarie on 6/7/2016.
 */
public class Elephone_P9000 extends BaseMTKDevice {
    public Elephone_P9000(Camera.Parameters parameters, I_CameraUiWrapper cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
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

            case 26257920:
                return new DngProfile(64, 4208, 3120, DngProfile.Plain,DngProfile.RGGB, 0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 25958400:
                return new DngProfile(64, 4160, 3120, DngProfile.Plain,DngProfile.RGGB, 0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 26615808:
                return new DngProfile(64, 4864, 2736, DngProfile.Plain,DngProfile.RGGB, 0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
        }
        return null;
    }
}