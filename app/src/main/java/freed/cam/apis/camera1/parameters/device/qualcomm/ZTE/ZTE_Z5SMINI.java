package freed.cam.apis.camera1.parameters.device.qualcomm.ZTE;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.ModeParameterInterface;
import freed.cam.apis.camera1.parameters.device.BaseQcomDevice;
import freed.cam.apis.camera1.parameters.modes.NightModeZTE;
import freed.cam.apis.camera1.parameters.modes.VirtualLensFilter;

/**
 * Created by GeorgeKiarie on 9/22/2016.
 */
public class ZTE_Z5SMINI extends BaseQcomDevice {
    public ZTE_Z5SMINI(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public ModeParameterInterface getNightMode() {
        return new NightModeZTE(parameters, cameraUiWrapper);
    }

    @Override
    public ModeParameterInterface getLensFilter() {
        return new VirtualLensFilter(parameters, cameraUiWrapper);
    }


}