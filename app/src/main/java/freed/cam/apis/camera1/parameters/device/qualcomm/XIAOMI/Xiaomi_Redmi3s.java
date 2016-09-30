package freed.cam.apis.camera1.parameters.device.qualcomm.XIAOMI;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.camera1.parameters.device.BaseQcomNew;
import freed.dng.DngProfile;

/**
 * Created by troop on 29.09.2016.
 */

public class Xiaomi_Redmi3s extends BaseQcomNew {
    public Xiaomi_Redmi3s(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        switch (filesize) {
            case 16510976://mi 4c
                return new DngProfile(64, 4208, 3120, DngProfile.Mipi16, DngProfile.GRBG, 0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
        }
        return null;
    }
}
