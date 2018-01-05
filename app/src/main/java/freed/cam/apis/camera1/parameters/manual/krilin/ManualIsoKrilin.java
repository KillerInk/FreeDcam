package freed.cam.apis.camera1.parameters.manual.krilin;

import android.hardware.Camera;

import java.util.Set;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.settings.mode.SettingMode;

/**
 * Created by troop on 27.03.2017.
 */

public class ManualIsoKrilin extends BaseManualParameter {


    public ManualIsoKrilin(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper, SettingKeys.Key key) {
        super(parameters,cameraUiWrapper,key);
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
            parameters.set(key_value, stringvalues[currentInt]);
        }
        fireStringValueChanged(stringvalues[valueToSet]);
        settingMode.set(String.valueOf(valueToSet));
    }

}
