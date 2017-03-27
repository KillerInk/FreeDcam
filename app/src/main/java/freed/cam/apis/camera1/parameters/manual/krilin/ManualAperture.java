package freed.cam.apis.camera1.parameters.manual.krilin;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualParameter;

/**
 * Created by troop on 27.03.2017.
 */

public class ManualAperture extends AbstractManualParameter
{
    private Camera.Parameters parameters;
    public ManualAperture(CameraWrapperInterface cameraUiWrapper, Camera.Parameters parameters)
    {
        super(cameraUiWrapper);
        this.parameters = parameters;
        isSupported = true;
        isVisible = isSupported;
        stringvalues = cameraUiWrapper.GetAppSettingsManager().manualAperture.getValues();
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }

    @Override
    public void SetValue(int valueToSet)
    {
        currentInt = valueToSet;
        if (valueToSet == 0) {
            parameters.set("hw-hwcamera-flag", "on");
            parameters.set("hw-big-aperture-mode", "off");
        } else {

            parameters.set("hw-hwcamera-flag", "on");
            parameters.set("hw-big-aperture-mode", "on");
            parameters.set(cameraUiWrapper.GetAppSettingsManager().manualIso.getKEY(), stringvalues[currentInt]);
        }
        ThrowCurrentValueStringCHanged(stringvalues[valueToSet]);
    }
}
