package freed.cam.apis.camera1.parameters.device.mtk;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.AbstractModeParameter;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.camera1.parameters.device.BaseMTKDevice;
import freed.cam.apis.camera1.parameters.modes.OpCodeParameter;
import freed.dng.DngProfile;

/**
 * Created by GeorgeKiarie on 6/17/2016.
 */
public class Zoppo_8speed extends BaseMTKDevice {


    public Zoppo_8speed(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
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
            case 26257920:
                return new DngProfile(64, 4208, 3120, DngProfile.Plain,DngProfile.RGGB, 0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.IMX214));
        }
        return null;
    }


}