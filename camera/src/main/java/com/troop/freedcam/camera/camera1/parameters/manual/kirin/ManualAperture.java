package com.troop.freedcam.camera.camera1.parameters.manual.kirin;

import android.hardware.Camera;

import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.parameters.AbstractParameter;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;

/**
 * Created by troop on 27.03.2017.
 */

public class ManualAperture extends AbstractParameter
{
    private Camera.Parameters parameters;
    public ManualAperture(CameraControllerInterface cameraUiWrapper, Camera.Parameters parameters)
    {
        super(cameraUiWrapper,SettingKeys.M_Aperture);
        this.parameters = parameters;
        setViewState(ViewState.Visible);
    }

    @Override
    public void setValue(int valueToSet, boolean setToCamera)
    {
        currentInt = valueToSet;
        if (valueToSet == 0) {
            parameters.set("hw-hwcamera-flag", "on");
            parameters.set("hw-big-aperture-mode", "off");
        } else {

            parameters.set("hw-hwcamera-flag", "on");
            parameters.set("hw-big-aperture-mode", "on");
            parameters.set(SettingsManager.get(SettingKeys.M_Aperture).getCamera1ParameterKEY(), stringvalues[currentInt]);
        }
        fireStringValueChanged(stringvalues[valueToSet]);
    }
}
