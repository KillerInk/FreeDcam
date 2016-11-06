package freed.cam.apis.camera1.parameters.device.qualcomm.ASUS;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.camera1.parameters.device.BaseQcomDevice;
import freed.dng.DngProfile;

/**
 * Created by troop on 06.11.2016.
 */

public class Asus_Zenfone_Go extends BaseQcomDevice
{

    public Asus_Zenfone_Go(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
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
            case 6144000:
                return new DngProfile(64, 2560, 1920, DngProfile.Mipi, DngProfile.GRBG, 0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.IMX135));
        }
        return null;
    }
}
