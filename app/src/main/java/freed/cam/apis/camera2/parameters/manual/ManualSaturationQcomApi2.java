package freed.cam.apis.camera2.parameters.manual;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qcom.CaptureRequestQcom;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera2.Camera2Fragment;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class ManualSaturationQcomApi2 extends AbstractParameter {

    public ManualSaturationQcomApi2(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper, SettingKeys.M_Saturation);
        if (SettingsManager.get(SettingKeys.M_Saturation).isSupported())
        {
            setViewState(ViewState.Visible);
            stringvalues = SettingsManager.get(SettingKeys.M_Saturation).getValues();
            if (stringvalues == null || stringvalues.length == 0) {
                setViewState(ViewState.Hidden);
            }
            else
                setViewState(ViewState.Visible);
            currentInt = 0;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void SetValue(int valueToSet, boolean setToCamera) {
        currentInt = valueToSet;
        ((Camera2Fragment) cameraUiWrapper).captureSessionHandler.SetParameterRepeating(CaptureRequestQcom.saturation, currentInt,setToCamera);
        fireStringValueChanged(currentInt+"");
    }


    @Override
    public String GetStringValue() {
        return stringvalues[currentInt];
    }
}
