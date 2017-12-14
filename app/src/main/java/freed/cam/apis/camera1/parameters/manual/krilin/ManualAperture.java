package freed.cam.apis.camera1.parameters.manual.krilin;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.Settings;
import freed.settings.SettingsManager;

/**
 * Created by troop on 27.03.2017.
 */

public class ManualAperture extends AbstractParameter
{
    private Camera.Parameters parameters;
    public ManualAperture(CameraWrapperInterface cameraUiWrapper, Camera.Parameters parameters)
    {
        super(cameraUiWrapper);
        this.parameters = parameters;
        isSupported = true;
        isVisible = isSupported;
        stringvalues = SettingsManager.get(Settings.M_Aperture).getValues();
    }

    @Override
    public boolean IsSetSupported() {
        return true;
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
            parameters.set(SettingsManager.get(Settings.M_Aperture).getKEY(), stringvalues[currentInt]);
        }
        fireStringValueChanged(stringvalues[valueToSet]);
    }
}
