package freed.cam.apis.camera1.parameters.manual.krilin;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualParameter;

/**
 * Created by troop on 27.03.2017.
 */

public class ManualIsoKrilin extends AbstractManualParameter {

    private final Camera.Parameters parameters;

    public ManualIsoKrilin(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
        this.parameters =  parameters;
        isSupported = true;
        isVisible = isSupported;
        stringvalues = cameraUiWrapper.getAppSettingsManager().manualIso.getValues();
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
            parameters.set("hw-professional-mode", "off");
        } else {

            parameters.set("hw-hwcamera-flag", "on");
            parameters.set("hw-professional-mode", "on");
            parameters.set(cameraUiWrapper.getAppSettingsManager().manualIso.getKEY(), stringvalues[currentInt]);
        }
        ThrowCurrentValueStringCHanged(stringvalues[valueToSet]);
    }
}
