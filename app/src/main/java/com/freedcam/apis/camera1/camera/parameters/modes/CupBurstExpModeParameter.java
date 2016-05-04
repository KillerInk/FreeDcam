package com.freedcam.apis.camera1.camera.parameters.modes;

import android.os.Handler;

import com.freedcam.apis.camera1.camera.BaseCameraHolder;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;

import java.util.HashMap;

/**
 * Created by Ar4eR on 05.02.16.
 */
public class CupBurstExpModeParameter extends BaseModeParameter
{
    final String TAG = CupBurstExpModeParameter.class.getSimpleName();
    private AppSettingsManager appSettingsManager;
    public CupBurstExpModeParameter(Handler uihandler, HashMap<String, String> parameters, BaseCameraHolder cameraHolder, String values, AppSettingsManager appSettingsManager) {
        super(uihandler, parameters, cameraHolder, "capture-burst-exposures", "");
        this.appSettingsManager = appSettingsManager;

        this.isSupported = false;
        try {
            String cbe =  parameters.get("capture-burst-exposures");
            if (cbe != null || !cbe.equals(""))
                this.isSupported = true;
        }
        catch (Exception ex) {
        }
        try {
            String aeb =  parameters.get("ae-bracket-hdr");
            if (aeb != null || !aeb.equals(""))
                this.isSupported = true;
        }
        catch (Exception ex) {
        }
    }

    @Override
    public boolean IsSupported() {
        return this.isSupported;
    }

    @Override
    public String[] GetValues() {
        return new String[] {"off","on"};
    }


    @Override
    public void SetValue(String valueToSet, boolean setToCam) {

        parameters.put("ae-bracket-hdr","Off");
        try {
            baseCameraHolder.SetCameraParameters(parameters);
        } catch (Exception ex) {
            Logger.exception(ex);
        }
        String newvalue[] = "0,0,0".split(",");
        if (valueToSet.equals("on")) {
            //if (baseCameraHolder.ParameterHandler.aeb1.GetValue() != null) {
                //newvalue[0] = baseCameraHolder.ParameterHandler.aeb1.GetValue();
            newvalue[0] = appSettingsManager.getString(AppSettingsManager.SETTING_AEB1);
            if(newvalue[0] == null || newvalue[0].equals(""))
                newvalue[0] = "5";
            newvalue[1] = appSettingsManager.getString(AppSettingsManager.SETTING_AEB2);
            if(newvalue[1] == null || newvalue[1].equals(""))
                newvalue[1] = "0";
            newvalue[2] = appSettingsManager.getString(AppSettingsManager.SETTING_AEB3);
            if(newvalue[2] == null || newvalue[2].equals(""))
                newvalue[2] = "-5";
        }

        parameters.put("capture-burst-exposures",newvalue[0]+","+newvalue[1]+","+newvalue[2]);
        try {
            baseCameraHolder.SetCameraParameters(parameters);
            //super.BackgroundValueHasChanged(newvalue[0]+","+newvalue[1]+","+newvalue[2]);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    @Override
    public String GetValue() {
        String tmp = parameters.get(value);
        if (tmp == null || tmp == "")
            return "off";
        else
            return (parameters.get(value));
    }
}
