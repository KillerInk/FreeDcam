package freed.cam.apis.camera2.parameters.manual;

import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.qcom.CaptureRequestQcom;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera2.Camera2;
import freed.settings.SettingKeys;

public class ManualSaturationQcomApi2 extends AbstractParameter<Camera2> {

    public ManualSaturationQcomApi2(Camera2 cameraUiWrapper) {
        super(cameraUiWrapper, SettingKeys.M_SATURATION);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setIntValue(int valueToSet, boolean setToCamera) {
        super.setValue(valueToSet,setToCamera);
        cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestQcom.saturation, currentInt,setToCamera);
    }


    @Override
    public String getStringValue() {
        return stringvalues[currentInt];
    }
}
