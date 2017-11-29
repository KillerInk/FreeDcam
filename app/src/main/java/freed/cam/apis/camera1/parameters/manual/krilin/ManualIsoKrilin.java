package freed.cam.apis.camera1.parameters.manual.krilin;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.AppSettingsManager;

/**
 * Created by troop on 27.03.2017.
 */

public class ManualIsoKrilin extends AbstractParameter {

    private final Camera.Parameters parameters;
    String key;

    public ManualIsoKrilin(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
        this.parameters =  parameters;
        isSupported = true;
        isVisible = isSupported;
        stringvalues = AppSettingsManager.getInstance().manualIso.getValues();
        key = AppSettingsManager.getInstance().manualIso.getKEY();
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }

    @Override
    public void setValue(int valueToSet)
    {
        currentInt = valueToSet;
        if (valueToSet == 0) {
            parameters.set("hw-hwcamera-flag", "on");
            parameters.set("hw-professional-mode", "off");
        } else {

            parameters.set("hw-hwcamera-flag", "on");
            parameters.set("hw-professional-mode", "on");
            parameters.set(key, stringvalues[currentInt]);
        }
        fireStringValueChanged(stringvalues[valueToSet]);
    }

}
