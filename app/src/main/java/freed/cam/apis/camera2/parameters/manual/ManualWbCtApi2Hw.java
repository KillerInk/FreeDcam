package freed.cam.apis.camera2.parameters.manual;

import android.annotation.TargetApi;
import android.os.Build;

import camera2_hidden_keys.huawei.CaptureRequestHuawei;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera2.Camera2Fragment;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ManualWbCtApi2Hw  extends AbstractParameter
{
    private boolean isSupported;

    private final String TAG = ManualWbCtApi2Hw.class.getSimpleName();

    public ManualWbCtApi2Hw(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper, SettingKeys.M_Whitebalance);
        stringvalues = SettingsManager.get(SettingKeys.M_Whitebalance).getValues();
        currentInt = 0;
        setViewState(ViewState.Visible);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int GetValue()
    {
        return currentInt;
    }

    @Override
    public String GetStringValue()
    {
        return stringvalues[currentInt];
    }

    @Override
    public String[] getStringValues() {
        return stringvalues;
    }

    @Override
    public void setValue(int valueToSet, boolean setToCamera)
    {
        super.setValue(valueToSet, setToCamera);
        int toset;
        currentInt =valueToSet;
        if (valueToSet == 0) // = auto
            toset = 0;
        else
            toset = Integer.parseInt(stringvalues[currentInt]);
        ((Camera2Fragment) cameraUiWrapper).captureSessionHandler.SetParameterRepeating(CaptureRequestHuawei.HUAWEI_SENSOR_WB_VALUE, toset,setToCamera);

    }

}
