package freed.cam.apis.camera1.parameters.manual.krilin;

import android.hardware.Camera;

import java.util.Set;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

/**
 * Created by troop on 27.03.2017.
 */

public class ManualIsoKrilin extends AbstractParameter {

    private final Camera.Parameters parameters;
    String key;

    public ManualIsoKrilin(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper, SettingKeys.M_ManualIso);
        this.parameters =  parameters;
        isSupported = true;
        isVisible = isSupported;
        stringvalues = SettingsManager.get(SettingKeys.M_ManualIso).getValues();
        key = SettingsManager.get(SettingKeys.M_ManualIso).getKEY();
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
            parameters.set("hw-professional-mode", "off");
        } else {

            parameters.set("hw-hwcamera-flag", "on");
            parameters.set("hw-professional-mode", "on");
            parameters.set(key, stringvalues[currentInt]);
        }
        fireStringValueChanged(stringvalues[valueToSet]);
    }

}
