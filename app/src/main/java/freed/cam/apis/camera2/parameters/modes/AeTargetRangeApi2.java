package freed.cam.apis.camera2.parameters.modes;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import android.util.Range;

import androidx.annotation.RequiresApi;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.SettingKeys;

/**
 * Created by troop on 22.06.2017.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AeTargetRangeApi2 extends BaseModeApi2 {

    private CaptureRequest.Key<Range<Integer>> key;

    public AeTargetRangeApi2(CameraWrapperInterface cameraUiWrapper, SettingKeys.Key settingMode, CaptureRequest.Key<Range<Integer>> parameterKey) {
        super(cameraUiWrapper, settingMode);
        this.key = parameterKey;
    }


    @Override
    public void setValue(String valueToSet, boolean setToCamera)
    {
        currentString = valueToSet;
        fireStringValueChanged(valueToSet);
        String[] toset = valueToSet.split(",");
        Range t = new Range(Integer.parseInt(toset[0]), Integer.parseInt(toset[1]));
        captureSessionHandler.SetParameterRepeating(key, t,setToCamera);
        if (settingMode != null)
            settingMode.set(valueToSet);
    }

    @Override
    public String GetStringValue() {
        return currentString;
    }

    @Override
    public String[] getStringValues() {
        return stringvalues;
    }
}
