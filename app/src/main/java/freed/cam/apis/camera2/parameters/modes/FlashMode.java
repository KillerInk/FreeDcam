package freed.cam.apis.camera2.parameters.modes;

import android.graphics.Paint;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.camera2.Camera2;
import freed.settings.SettingKeys;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class FlashMode extends BaseModeApi2 {
    public FlashMode(Camera2 cameraUiWrapper, SettingKeys.Key settingMode) {
        super(cameraUiWrapper, settingMode);
    }

/*
    @Override
    public String[] getStringValues() {
        return new String[]{"Auto","Always","Off","Torch"};
    }*/

    @Override
    protected void setValue(String valueToSet, boolean setToCamera) {

        if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.font_flash_auto)))
            setToAuto();
        else if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.font_flash_always)))
            setToAlwaysFlash();
        else if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.font_flash_off)))
            setToOff();
        else if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.font_flash_torch)))
            setToTorch();
        currentString = valueToSet;
        settingMode.set(valueToSet);
        fireStringValueChanged(valueToSet);
    }

    private void setToAuto()
    {
        captureSessionHandler.SetParameterRepeating(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF,true);
        captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH,false);
        captureSessionHandler.SetParameterRepeating(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_SINGLE,true);
    }

    private void setToAlwaysFlash()
    {
        captureSessionHandler.SetParameterRepeating(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF,true);
        captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH,false);
        captureSessionHandler.SetParameterRepeating(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_SINGLE,true);

    }

    private void setToOff()
    {
        captureSessionHandler.SetParameterRepeating(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF,false);
        captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON,true);
    }

    private void setToTorch()
    {
        captureSessionHandler.SetParameterRepeating(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF,true);
        captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON,false);
        captureSessionHandler.SetParameterRepeating(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH,true);

    }
}
