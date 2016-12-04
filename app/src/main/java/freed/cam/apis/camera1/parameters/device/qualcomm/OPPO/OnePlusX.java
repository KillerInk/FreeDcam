package freed.cam.apis.camera1.parameters.device.qualcomm.OPPO;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.dng.DngProfile;

/**
 * Created by GeorgeKiarie on 12/4/2016.
 */
public class OnePlusX extends OnePlusOne {

    public OnePlusX(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }


    @Override
    public boolean IsDngSupported() {
        return true;
    }
    @Override
    public DngProfile getDngProfile(int filesize)
    {
        switch (filesize)
        {
            case 9990144:
                return new DngProfile(16, 3264, 2448, DngProfile.Mipi16, DngProfile.BGGR, 0,matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.OmniVision));
            case 10653696:
                return new DngProfile(0, 3264, 2448, DngProfile.Qcom, DngProfile.BGGR, 0,matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.OmniVision));

            case 16424960:
                return new DngProfile(64, 4208, 3120, DngProfile.Mipi, DngProfile.GRBG, DngProfile.ROWSIZE,matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
     }
        return null;
    }

}
