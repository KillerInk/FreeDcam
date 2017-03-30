package freed.cam.apis.camera2.parameters.modes;

import android.hardware.camera2.CaptureRequest;

import com.huawei.camera2ex.CaptureRequestEx;

import java.util.Map;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.utils.AppSettingsManager;
import freed.utils.StringUtils;

/**
 * Created by troop on 29.03.2017.
 */

public class DualCameraModeHuaweiApi2 extends BaseModeApi2
{

    protected CaptureRequest.Key<Byte> parameterKey;

    public DualCameraModeHuaweiApi2(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
    }

    public DualCameraModeHuaweiApi2(CameraWrapperInterface cameraUiWrapper, AppSettingsManager.SettingMode settingMode, CaptureRequest.Key<Byte> parameterKey) {
        super(cameraUiWrapper, settingMode, null);
        this.parameterKey = parameterKey;
        isSupported = settingMode.isSupported();
        ((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).captureSessionHandler.SetParameterRepeating(parameterKey,(byte)0);
        if (isSupported)
            parameterValues = StringUtils.StringArrayToIntHashmap(settingMode.getValues());
        else settingMode = null;
    }

    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        int toset = parameterValues.get(valueToSet);
        ((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).captureSessionHandler.SetParameterRepeating(parameterKey, Byte.valueOf((byte)toset));

        onValueHasChanged(valueToSet);

    }

    @Override
    public String GetValue()
    {
        if (parameterKey == null)
            return null;
        Byte b = ((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).captureSessionHandler.get(parameterKey);
        int i = b.intValue();
        for (Map.Entry s : parameterValues.entrySet())
            if (s.getValue().equals(i))
                return s.getKey().toString();
        return "";
    }

    @Override
    public String[] GetValues() {
        return parameterValues.keySet().toArray(new String[parameterValues.size()]);
    }
}
