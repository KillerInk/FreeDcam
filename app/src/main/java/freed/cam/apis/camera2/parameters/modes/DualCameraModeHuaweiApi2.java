package freed.cam.apis.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import java.util.Map;

import freed.cam.apis.basecamera.CameraThreadHandler;
import freed.cam.apis.camera2.Camera2;
import freed.settings.SettingKeys;
import freed.utils.StringUtils;

/**
 * Created by troop on 29.03.2017.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class DualCameraModeHuaweiApi2 extends BaseModeApi2
{

    protected CaptureRequest.Key<Byte> parameterKey;


    public DualCameraModeHuaweiApi2(Camera2 cameraUiWrapper, SettingKeys.Key key, CaptureRequest.Key<Byte> parameterKey) {
        super(cameraUiWrapper, key, null);
        this.parameterKey = parameterKey;
        captureSessionHandler.SetParameterRepeating(parameterKey,(byte)0,true);
        if (settingMode.isSupported()) {
            parameterValues = StringUtils.StringArrayToIntHashmap(settingMode.getValues());
            setViewState(ViewState.Visible);
        }
        else settingMode = null;
    }

    @Override
    public void setValue(String valueToSet, boolean setToCamera)
    {
        super.setValue(valueToSet,setToCamera);
        if (settingsManager.get(SettingKeys.SECONDARY_SENSOR_SIZE).isSupported())
        {
            if (setToCamera) {
                CameraThreadHandler.restartPreviewAsync();
            }
        }

        int toset = parameterValues.get(valueToSet);
        captureSessionHandler.SetParameterRepeating(parameterKey, Byte.valueOf((byte) toset), setToCamera);
        fireStringValueChanged(valueToSet);

    }

    @Override
    public String getStringValue()
    {
        if (parameterKey == null)
            return null;
        Byte b = captureSessionHandler.getPreviewParameter(parameterKey);
        if (b == null)
        {
            setViewState(ViewState.Hidden);
            return "";
        }
        int i = b.intValue();
        for (Map.Entry s : parameterValues.entrySet())
            if (s.getValue().equals(i))
                return s.getKey().toString();
        return "";
    }

    @Override
    public String[] getStringValues() {
        return parameterValues.keySet().toArray(new String[parameterValues.size()]);
    }
}
