package com.troop.freedcam.camera2.parameters.modes;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;

/**
 * Created by troop on 12.12.2014.
 */
public class PictureFormatParameterApi2 extends BaseModeApi2 {
    public PictureFormatParameterApi2(BaseCameraHolderApi2 baseCameraHolderApi2) {
        super(baseCameraHolderApi2);
    }

    @Override
    public boolean IsSupported() {
        return super.IsSupported();
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        super.SetValue(valueToSet, setToCamera);
    }

    @Override
    public String GetValue() {
        return super.GetValue();
    }

    @Override
    public String[] GetValues() {
        return super.GetValues();
    }
}
