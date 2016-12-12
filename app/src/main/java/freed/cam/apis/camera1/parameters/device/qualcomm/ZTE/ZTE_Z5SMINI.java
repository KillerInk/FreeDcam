package freed.cam.apis.camera1.parameters.device.qualcomm.ZTE;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualParameter;
import freed.cam.apis.basecamera.parameters.manual.ManualParameterInterface;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.basecamera.parameters.modes.ModeParameterInterface;
import freed.cam.apis.camera1.parameters.device.BaseQcomDevice;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.cam.apis.camera1.parameters.manual.qcom.SkintoneManualPrameter;
import freed.cam.apis.camera1.parameters.modes.NightModeZTE;
import freed.cam.apis.camera1.parameters.modes.VirtualLensFilter;
import freed.dng.DngProfile;

/**
 * Created by GeorgeKiarie on 9/22/2016.
 */
public class ZTE_Z5SMINI extends BaseQcomDevice {
    public ZTE_Z5SMINI(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }





    @Override
    public ManualParameterInterface getSkintoneParameter() {
        AbstractManualParameter Skintone = new SkintoneManualPrameter(parameters, cameraUiWrapper);
        parametersHandler.PictureFormat.addEventListner(((BaseManualParameter) Skintone).GetPicFormatListner());
        cameraUiWrapper.GetModuleHandler().addListner(((BaseManualParameter) Skintone).GetModuleListner());
        return Skintone;
    }

    @Override
    public ModeParameterInterface getNightMode() {
        return new NightModeZTE(parameters, cameraUiWrapper);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }

    @Override
    public DngProfile getDngProfile(int filesize) {

        // case 20500480:
        return new DngProfile(64, 4212, 3120, DngProfile.Qcom, DngProfile.RGGB, DngProfile.ROWSIZE, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.G4));

        // return null;
    }

    @Override
    public ModeParameterInterface getLensFilter() {
        return new VirtualLensFilter(parameters, cameraUiWrapper);
    }


}