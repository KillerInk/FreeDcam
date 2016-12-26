package freed.cam.apis.camera1.parameters.device.qualcomm.LENOVO;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;

/**
 * Created by GeorgeKiarie on 12/23/2016.
 */

public class MotoG_2k14 extends Moto_X2k14 {

    public MotoG_2k14(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }
}
