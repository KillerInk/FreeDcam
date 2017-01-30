package freed.cam.apis.camera1.parameters.device.qualcomm.LG;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualParameter;
import freed.cam.apis.basecamera.parameters.modes.AbstractModeParameter;
import freed.cam.apis.camera1.parameters.manual.lg.AE_Handler_LGG4;
import freed.cam.apis.camera1.parameters.manual.lg.CCTManualG4;

/**
 * Created by GeorgeKiarie on 11/28/2016.
 */
public class LG_V20 extends LG_G2
{
    private final AE_Handler_LGG4 ae_handler_lgg4;
    public LG_V20(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters,cameraUiWrapper);
        ae_handler_lgg4 = new AE_Handler_LGG4(parameters, cameraUiWrapper);
        parameters.set("lge-camera","1");
    }
}
