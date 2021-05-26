package freed.cam.apis.camera2.parameters.manual;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera2.Camera2;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ManualApertureApi2 extends AbstractParameter<Camera2> {
    float apertureValues[];
    public ManualApertureApi2(SettingKeys.Key key) {
        super(key);
    }

    public ManualApertureApi2(Camera2 cameraUiWrapper, SettingKeys.Key settingMode) {
        super(cameraUiWrapper, settingMode);
        String[] arr = settingsManager.get(SettingKeys.M_Aperture).getValues();
        if (arr != null && arr.length > 1)
        {
            apertureValues = new float[arr.length];
            for (int i = 0; i < arr.length; i++)
            {
                apertureValues[i] = Float.parseFloat(arr[i]);
            }
            currentInt =  Integer.parseInt(settingsManager.get(SettingKeys.M_Aperture).get());
            setViewState(ViewState.Visible);
        }
        else
            setViewState(ViewState.Hidden);
    }


    @Override
    public void SetValue(int valueToSet, boolean setToCamera) {
        currentInt = valueToSet;
        float valtoset= apertureValues[currentInt];
        cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequest.LENS_APERTURE, valtoset,setToCamera);
        fireStringValueChanged(String.valueOf(apertureValues[currentInt]));
    }

    @Override
    public String[] getStringValues() {
        return settingsManager.get(SettingKeys.M_Aperture).getValues();
    }

    @Override
    public String GetStringValue() {
        return String.valueOf(apertureValues[currentInt]);
    }
}
