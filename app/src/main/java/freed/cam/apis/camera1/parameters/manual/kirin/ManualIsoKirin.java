package freed.cam.apis.camera1.parameters.manual.kirin;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.settings.SettingKeys;

/**
 * Created by troop on 27.03.2017.
 */

public class ManualIsoKirin extends BaseManualParameter {


    public ManualIsoKirin(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper, SettingKeys.Key key) {
        super(parameters,cameraUiWrapper,key);
        setViewState(ViewState.Visible);
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
