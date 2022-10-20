package freed.cam.apis.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.text.TextUtils;

import java.util.HashMap;

import freed.cam.apis.camera2.Camera2;
import freed.settings.SettingKeys;

/**
 * Created by Ingo on 03.10.2016.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JpegQualityModeApi2 extends BaseModeApi2 {
    public JpegQualityModeApi2(Camera2 cameraUiWrapper) {
        super(cameraUiWrapper, SettingKeys.JPEG_QUALITY);
        parameterValues =new HashMap<>();
        settingMode.setIsSupported(true);
        for (int i= 1; i <= 100; i+=1)
        {
            parameterValues.put(i+"", i);
        }
        setViewState(ViewState.Visible);
    }



    @Override
    public String getStringValue()
    {
        if(TextUtils.isEmpty(settingsManager.get(SettingKeys.JPEG_QUALITY).get()))
            return "100";
        else
            return settingsManager.get(SettingKeys.JPEG_QUALITY).get();
    }

    @Override
    public String[] getStringValues() {
        return parameterValues.keySet().toArray(new String[parameterValues.size()]);
    }

    @Override
    public void setValue(String valueToSet, boolean setToCamera) {
        settingsManager.get(SettingKeys.JPEG_QUALITY).set(valueToSet);
        cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequest.JPEG_QUALITY, (byte)Integer.parseInt(valueToSet),setToCamera);
        fireStringValueChanged(valueToSet);
    }
}
