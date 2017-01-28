package freed.cam.apis.camera1.parameters.device.qualcomm.XIAOMI;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.ModeParameterInterface;
import freed.cam.apis.camera1.parameters.device.BaseQcomNew;

/**
 * Created by troop on 21.10.2016.
 */

public class Xiaomi_Mi5 extends BaseQcomNew {
    public Xiaomi_Mi5(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public ModeParameterInterface getHDRMode() {
        return null;
    }


    @Override
    public ModeParameterInterface getVideoStabilisation() {
        return null;
    }

}
