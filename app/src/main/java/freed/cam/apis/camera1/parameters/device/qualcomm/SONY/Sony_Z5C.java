package freed.cam.apis.camera1.parameters.device.qualcomm.SONY;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.camera1.parameters.device.BaseQcomNew;
import freed.dng.DngProfile;

/**
 * Created by GeorgeKiarie on 9/19/2016.
 */
public class Sony_Z5C extends BaseQcomNew {


    public Sony_Z5C(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }
}