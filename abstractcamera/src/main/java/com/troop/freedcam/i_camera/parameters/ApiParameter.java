package com.troop.freedcam.i_camera.parameters;

import android.os.Build;
import android.os.Handler;

import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;

/**
 * Created by troop on 21.07.2015.
 */
public class ApiParameter extends AbstractModeParameter
{
    I_Activity i_activity;
    AppSettingsManager appSettingsManager;

    public ApiParameter(Handler uiHandler, I_Activity i_activity, AppSettingsManager appSettingsManager) {
        super(uiHandler);
        this.i_activity = i_activity;
        this.appSettingsManager = appSettingsManager;
    }

    @Override
    public String[] GetValues()
    {
        if (Build.VERSION.SDK_INT  >= 21)
        {
            if (appSettingsManager.IsCamera2FullSupported().equals("true"))
                return new String[]{AppSettingsManager.API_SONY, AppSettingsManager.API_2};
            else
                return new String[] {AppSettingsManager.API_SONY, AppSettingsManager.API_1};
        }
        else
            return new String[] {AppSettingsManager.API_SONY, AppSettingsManager.API_1};
    }

    @Override
    public String GetValue() {
        String ret = appSettingsManager.getCamApi();
        if (ret.equals(""))
            ret = AppSettingsManager.API_1;
        return ret;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {
        appSettingsManager.setCamApi(valueToSet);
        i_activity.SwitchCameraAPI(valueToSet);
    }

    @Override
    public boolean IsSupported() {
        return true;
    }
}
