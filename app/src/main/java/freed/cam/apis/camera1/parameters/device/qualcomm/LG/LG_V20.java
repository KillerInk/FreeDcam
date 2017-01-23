package freed.cam.apis.camera1.parameters.device.qualcomm.LG;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualParameter;
import freed.cam.apis.basecamera.parameters.manual.ManualParameterInterface;
import freed.cam.apis.basecamera.parameters.modes.AbstractModeParameter;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.camera1.parameters.manual.lg.AE_Handler_LGG4;
import freed.cam.apis.camera1.parameters.manual.lg.CCTManualG4;
import freed.dng.DngProfile;

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

    @Override
    public ManualParameterInterface getExposureTimeParameter() {
        return ae_handler_lgg4.getShutterManual();
    }

    @Override
    public ManualParameterInterface getIsoParameter() {
        return ae_handler_lgg4.getManualIso();
    }


    @Override
    public AbstractManualParameter getCCTParameter() {
        return new CCTManualG4(parameters, cameraUiWrapper);
    }
    public boolean IsDngSupported() {
        return true;
    }
    @Override
    public DngProfile getDngProfile(int filesize) {
        return new DngProfile(64, 4656, 3492, DngProfile.Mipi, DngProfile.RGGB, DngProfile.ROWSIZE, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.IMX298));
    }


    @Override
    public AbstractModeParameter getOpCodeParameter() {
        return null;
    }
}
