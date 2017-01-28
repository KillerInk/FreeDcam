package freed.cam.apis.camera1.parameters.device.qualcomm.WIKO;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.camera1.parameters.device.BaseMTKDevice;
import freed.dng.DngProfile;

/**
 * Created by troop on 29.09.2016.
 */

public class Wikio_Stairway extends BaseMTKDevice {
    public Wikio_Stairway(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }
}
