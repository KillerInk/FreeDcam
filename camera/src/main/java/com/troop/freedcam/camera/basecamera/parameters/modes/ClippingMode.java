package com.troop.freedcam.camera.basecamera.parameters.modes;

import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.utils.ContextApplication;

/**
 * Created by KillerInk on 23.01.2018.
 */

public class ClippingMode extends HistogramParameter {

    private String state = "off";

    public ClippingMode(CameraControllerInterface cameraUiWrapper) {
        super(cameraUiWrapper);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        state = valueToSet;
        if (valueToSet.equals(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_)))
        {
            cameraUiWrapper.getFocusPeakProcessor().setClippingEnable(true);
            fireStringValueChanged(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_));
        }
        else {
            cameraUiWrapper.getFocusPeakProcessor().setClippingEnable(false);
            fireStringValueChanged(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_));
        }

    }

    @Override
    public String GetStringValue() {
        return state;
    }
}
