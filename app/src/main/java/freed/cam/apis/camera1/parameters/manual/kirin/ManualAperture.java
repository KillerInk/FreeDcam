package freed.cam.apis.camera1.parameters.manual.kirin;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.SettingKeys;

/**
 * Created by troop on 27.03.2017.
 */

public class ManualAperture extends AbstractParameter
{
    private Camera.Parameters parameters;
    public ManualAperture(CameraWrapperInterface cameraUiWrapper, Camera.Parameters parameters)
    {
        super(cameraUiWrapper,SettingKeys.M_Aperture);
        this.parameters = parameters;
        setViewState(ViewState.Visible);
    }

    @Override
    public void setValue(int valueToSet, boolean setToCamera)
    {
        currentInt = valueToSet;
        if (valueToSet == 0) {
            parameters.set("hw-hwcamera-flag", "on");
            parameters.set("hw-big-aperture-mode", "off");
        } else {

            parameters.set("hw-hwcamera-flag", "on");
            parameters.set("hw-big-aperture-mode", "on");
            parameters.set(settingsManager.get(SettingKeys.M_Aperture).getCamera1ParameterKEY(), stringvalues[currentInt]);
        }
        fireStringValueChanged(stringvalues[valueToSet]);
    }
}
