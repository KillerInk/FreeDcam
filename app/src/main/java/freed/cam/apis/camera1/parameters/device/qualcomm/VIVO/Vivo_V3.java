package freed.cam.apis.camera1.parameters.device.qualcomm.VIVO;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.camera1.parameters.device.BaseQcomDevice;
import freed.dng.DngProfile;

/**
 * Created by troop on 13.10.2016.
 */

public class Vivo_V3 extends BaseQcomDevice {
    public Vivo_V3(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        switch (filesize)
        {
            case 16424960:
                return new DngProfile(64, 4208, 3120, DngProfile.Mipi, DngProfile.BGGR, DngProfile.ROWSIZE, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.G4));
            case 17522688:
                return new DngProfile(64, 4212, 3120, DngProfile.Qcom, DngProfile.BGGR, DngProfile.ROWSIZE, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.G4));
        }
        return null;
    }

    @Override
    public int getCurrentIso() {
        return Integer.parseInt(parameters.get("snap_iso_value"));
    }

    @Override
    public float getCurrentExposuretime() {
        return Float.parseFloat(parameters.get("snap_exp_time"));
    }
}
