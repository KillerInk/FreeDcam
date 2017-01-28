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
}
