package freed.cam.apis.camera1.parameters.device.qualcomm.ZTE;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.camera1.parameters.device.BaseQcomNew;
import freed.dng.DngProfile;

/**
 * Created by GeorgeKiarie on 12/23/2016.
 */

public class ZTE_ZMAX_ZPRO extends BaseQcomNew {

    public ZTE_ZMAX_ZPRO(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        switch (filesize)
        {
            case 16510976:
                return new DngProfile(64, 4208, 3136, DngProfile.Mipi, DngProfile.RGGB, 0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.IMX135));
        }
        return null;
    }

}
