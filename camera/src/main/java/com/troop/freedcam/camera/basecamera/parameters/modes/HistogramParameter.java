package com.troop.freedcam.camera.basecamera.parameters.modes;


import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.utils.ContextApplication;

/**
 * Created by KillerInk on 15.01.2018.
 */

public class HistogramParameter extends FocusPeakMode {

    private String state = "off";

    public HistogramParameter(CameraControllerInterface cameraUiWrapper) {
        super(cameraUiWrapper);
    }
    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        state = valueToSet;
        if (valueToSet.equals(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_)))
        {
            cameraUiWrapper.getFocusPeakProcessor().setHistogramEnable(true);
            fireStringValueChanged(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_));
        }
        else {
            cameraUiWrapper.getFocusPeakProcessor().setHistogramEnable(false);
            fireStringValueChanged(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_));
        }

    }

    @Override
    public String GetStringValue() {
        return state;
    }
}
