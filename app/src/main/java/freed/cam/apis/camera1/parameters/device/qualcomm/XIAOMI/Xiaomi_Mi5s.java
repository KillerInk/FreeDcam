package freed.cam.apis.camera1.parameters.device.qualcomm.XIAOMI;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.ModeParameterInterface;
import freed.cam.apis.camera1.parameters.device.BaseQcomNew;

/**
 * Created by GeorgeKiarie on 12/23/2016.
 */

public class Xiaomi_Mi5s extends BaseQcomNew {

    public Xiaomi_Mi5s(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
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
