package freed.cam.apis.camera1.parameters.device.qualcomm.LG;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.camera1.parameters.device.BaseQcomDevice;
import freed.dng.DngProfile;

/**
 * Created by troop on 09.11.2016.
 */

public class LG_L5 extends BaseQcomDevice {
    public LG_L5(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }


    @Override
    public DngProfile getDngProfile(int filesize) {
        switch (filesize)
        {
            case 6659840:
                return new DngProfile(0, 2580,1920,DngProfile.Qcom, DngProfile.RGGB,0,
                        matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
        }
        return null;
    }
}
