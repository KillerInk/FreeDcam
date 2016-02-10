package com.troop.freedcam.i_camera.parameters;

import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 21.07.2015.
 */
public class ParameterExternalShutter extends AbstractModeParameter
{
    private static String VoLP = "Vol+";
    private static String VoLM = "Vol-";
    private static String Hook = "Hook";
    private AppSettingsManager appSettingsManager;
    private String[] values = {VoLP, VoLM, Hook};

    public ParameterExternalShutter(AppSettingsManager appSettingsManager)
    {
        super(null);
        this.appSettingsManager = appSettingsManager;
    }

    public boolean IsSupported()
    {
        return true;
    }
    public void setIsSupported()
    {

    }

    public void SetValue(String valueToSet, boolean setToCamera)
    {
        appSettingsManager.setString(AppSettingsManager.SETTING_EXTERNALSHUTTER, valueToSet);
    }

    public String GetValue()
    {
        if (appSettingsManager.getString(AppSettingsManager.SETTING_EXTERNALSHUTTER).equals(""))
            return "Hook";
        else
            return appSettingsManager.getString(AppSettingsManager.SETTING_EXTERNALSHUTTER);
    }

    public String[] GetValues() {
        return values;
    }
}
