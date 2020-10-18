package com.troop.freedcam.camera.camera1.parameters.ae;

import android.hardware.Camera;

import com.troop.freedcam.camera.R;

import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.parameters.AbstractParameter;
import com.troop.freedcam.camera.basecamera.parameters.ae.AeManager;
import com.troop.freedcam.camera.basecamera.parameters.ae.AeStates;
import com.troop.freedcam.camera.camera1.parameters.ParametersHandler;
import com.troop.freedcam.settings.SettingKeys;

/**
 * Created by KillerInk on 29.12.2017.
 */

public class AeManagerMtkCamera1 extends AeManager
{
    private Camera.Parameters parameters;

    public AeManagerMtkCamera1(CameraControllerInterface cameraControllerInterface, Camera.Parameters parameters) {
        super(cameraControllerInterface);
        this.parameters =parameters;
    }

    @Override
    public void setExposureTime(int valueToSet, boolean setToCamera) {
        if (valueToSet == 0)
        {
            parameters.set("m-ss", "0");
        }
        else
        {
            String shutterstring = manualExposureTime.getStringValues()[valueToSet];
            if (shutterstring.contains("/")) {
                String[] split = shutterstring.split("/");
                Double a = Double.parseDouble(split[0]) / Double.parseDouble(split[1]);
                shutterstring = "" + a;
            }
            parameters.set("m-ss", FLOATtoThirty(shutterstring));
        }
        ((ParametersHandler) cameraControllerInterface.getParameterHandler()).SetParametersToCamera(parameters);
    }

    private String FLOATtoThirty(String a)
    {
        Float b =  Float.parseFloat(a);
        float c = b * 1000;
        return String.valueOf(c);
    }

    @Override
    public void setIso(int valueToSet, boolean setToCamera) {
        if (valueToSet == 0)
        {
            parameters.set("m-sr-g", "0");
            setAeMode(AeStates.auto);
        }
        else
        {
            //cap-isp-g= 1024 == iso100? cause cap-sr-g=7808 / 1024 *100 = 762,5 same with 256 = 3050
            parameters.set("m-sr-g", String.valueOf(Integer.valueOf(manualIso.getStringValues()[valueToSet])/100 *256));
            setAeMode(AeStates.manual);
        }
        ((ParametersHandler) cameraControllerInterface.getParameterHandler()).SetParametersToCamera(parameters);
    }

    @Override
    public void setExposureCompensation(int valueToSet, boolean setToCamera) {

    }

    @Override
    public void setAeMode(AeStates aeState) {

        if (aeState == activeAeState)
            return;
        activeAeState = aeState;
        if (aeState == AeStates.auto)
            setToAuto();
        else if (aeState == AeStates.manual)
            setToManual();
    }

    private void setToAuto()
    {
        String t = cameraControllerInterface.getParameterHandler().get(SettingKeys.IsoMode).GetStringValue();
        if (!t.equals(ContextApplication.getStringFromRessources(R.string.iso100_)))
            cameraControllerInterface.getParameterHandler().get(SettingKeys.IsoMode).SetValue(ContextApplication.getStringFromRessources(R.string.iso100_), true);
        else
            cameraControllerInterface.getParameterHandler().get(SettingKeys.IsoMode).SetValue(ContextApplication.getStringFromRessources(R.string.auto_), true);
        cameraControllerInterface.getParameterHandler().get(SettingKeys.IsoMode).SetValue(t, true);
        //back in auto mode
        //set exposure ui item to enable
        /*exposureCompensation.fireIsSupportedChanged(true);
        exposureCompensation.fireIsReadOnlyChanged(true);*/
        manualIso.setViewState(AbstractParameter.ViewState.Enabled);
        manualExposureTime.setViewState(AbstractParameter.ViewState.Disabled);
    }


    private void setToManual()
    {
        //hide manualexposuretime ui item
        /*exposureCompensation.fireIsSupportedChanged(false);*/
        //turn flash off when ae is off. else on some devices it applys only manual stuff only for a few frames
        manualExposureTime.setValue(manualExposureTime.GetValue(),true);
        //enable manualiso item in ui
        manualIso.setViewState(AbstractParameter.ViewState.Enabled);
        //enable manual exposuretime in ui
        manualExposureTime.setValue(manualExposureTime.GetValue(),true);
        manualExposureTime.setViewState(AbstractParameter.ViewState.Enabled);
        manualExposureTime.fireStringValueChanged(manualExposureTime.GetStringValue());
    }
}
