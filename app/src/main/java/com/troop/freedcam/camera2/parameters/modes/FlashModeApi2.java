package com.troop.freedcam.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.os.Build;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;

/**
 * Created by troop on 12.12.2014.
 */
public class FlashModeApi2 extends BaseModeApi2 {
    public FlashModeApi2(BaseCameraHolderApi2 baseCameraHolderApi2) {
        super(baseCameraHolderApi2);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean IsSupported() {
        return cameraHolder.characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {
        super.SetValue(valueToSet, setToCamera);
    }

    @Override
    public String GetValue()
    {
        int[] values = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
        return super.GetValue();
    }

    @Override
    public String[] GetValues() {
        return super.GetValues();
    }
}
