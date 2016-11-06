package freed.cam.apis.camera1.parameters.device.qualcomm.YU;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.camera1.parameters.device.BaseQcomNew;
import freed.dng.DngProfile;

/**
 * Created by troop on 06.11.2016.
 */

public class Yu_Yuphoria extends BaseQcomNew {
    public Yu_Yuphoria(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        switch (filesize) {
            case 9990144:
                return new DngProfile(64, 3264, 2448, DngProfile.Mipi, DngProfile.BGGR, 4080, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
        }
        return null;
    }
}
