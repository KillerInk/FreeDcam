package freed.cam.apis.camera1.parameters.device.qualcomm.ZTE;

import android.hardware.Camera;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusRect;
import freed.cam.apis.basecamera.parameters.manual.ManualParameterInterface;
import freed.cam.apis.basecamera.parameters.modes.ModeParameterInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.device.BaseQcomDevice;
import freed.cam.apis.camera1.parameters.manual.whitebalance.BaseCCTManual;
import freed.cam.apis.camera1.parameters.manual.zte.ShutterManualZTE;
import freed.cam.apis.camera1.parameters.modes.NightModeZTE;

/**
 * Created by GeorgeKiarie on 9/22/2016.
 */
public class ZTE_Z11 extends BaseQcomDevice {
    public ZTE_Z11(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public ManualParameterInterface getExposureTimeParameter() {
        return new ShutterManualZTE(parameters, cameraUiWrapper);
    }

   /* @Override
    public ManualParameterInterface getManualFocusParameter() {
        return new BaseFocusManual(parameters, KEYS.KEY_MANUAL_FOCUS_POSITION,0,79,KEYS.KEY_FOCUS_MODE_MANUAL, cameraUiWrapper,1,1);
    }*/

    @Override
    public ManualParameterInterface getCCTParameter() {
        return new BaseCCTManual(parameters,KEYS.WB_MANUAL_CCT,8000,2000, cameraUiWrapper,100, KEYS.WB_MODE_MANUAL_CCT);
    }

    @Override
    public ModeParameterInterface getNightMode() {
        return new NightModeZTE(parameters, cameraUiWrapper);
    }

    @Override
    public void SetFocusArea(FocusRect focusAreas) {
        parameters.set("touch-aec","on");
        parameters.set("touch-index-af", focusAreas.x + "," + focusAreas.y);
        ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
    }
}
