package freed.cam.apis.camera1.parameters.device.mtk;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.camera1.parameters.device.BaseMTKDevice;
import freed.dng.DngProfile;

/**
 * Created by GeorgeKiarie on 8/4/2016.
 */
public class Lumigon_T3 extends BaseMTKDevice {
    public Lumigon_T3(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        return new DngProfile(64, 4192, 3104, DngProfile.Plain, DngProfile.GBRG, 0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
    }

}
