package freed.cam.apis.camera1.parameters.device.mediatek.LUMIGON;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.AbstractModeParameter;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.camera1.parameters.device.BaseMTKDevice;
import freed.cam.apis.camera1.parameters.modes.OpCodeParameter;
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
    public AbstractModeParameter getOpCodeParameter() {
        return new OpCodeParameter(cameraUiWrapper.GetAppSettingsManager());
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        return new DngProfile(64, 4192, 3104, DngProfile.Plain, DngProfile.GRBG, 0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.LumingonOV));
    }

}
