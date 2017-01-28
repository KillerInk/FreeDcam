package freed.cam.apis.camera1.parameters.device.qualcomm.LENOVO;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusRect;
import freed.cam.apis.basecamera.parameters.modes.AbstractModeParameter;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.device.BaseQcomNew;

/**
 * Created by GeorgeKiarie on 11/22/2016.
 */
public class MotoG3 extends BaseQcomNew {


    public MotoG3(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public AbstractModeParameter getOpCodeParameter() {
        return null;
    }

    @Override
    public void SetFocusArea(FocusRect focusAreas) {
        parameters.set("touch-aec","on");
        parameters.set("touch-index-af", focusAreas.x + "," + focusAreas.y);
        ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
    }
}