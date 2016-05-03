package com.troop.freedcam.i_camera.parameters;

import android.os.Handler;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.utils.StringUtils;

/**
 * Created by troop on 22.07.2015.
 */
public class HistogramParameter extends AbstractModeParameter
{
    private I_Activity i_activity;
    private AppSettingsManager appSettingsManager;
    private AbstractCameraUiWrapper cameraUiWrapper;
    private boolean isSupported = false;

    public HistogramParameter(Handler uiHandler, I_Activity i_activity, AppSettingsManager appSettingsManager, AbstractCameraUiWrapper cameraUiWrapper) {
        super(uiHandler);
        this.i_activity = i_activity;
        this.appSettingsManager = appSettingsManager;
        this.cameraUiWrapper = cameraUiWrapper;
        if (cameraUiWrapper.CameraApiName().equals(AppSettingsManager.API_1) || cameraUiWrapper.CameraApiName().equals(AppSettingsManager.API_SONY)) {
            this.isSupported = true;
            if (appSettingsManager.getString(AppSettingsManager.SETTING_HISTOGRAM).equals(StringUtils.ON))
                i_activity.ShowHistogram(true);
        }
    }


    @Override
    public String[] GetValues() {
        return new String[]{StringUtils.OFF, StringUtils.ON};
    }

    @Override
    public String GetValue() {
        return appSettingsManager.getString(AppSettingsManager.SETTING_HISTOGRAM);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        boolean show = true;
        if (valueToSet.equals(StringUtils.OFF))
            show = false;
        appSettingsManager.setString(AppSettingsManager.SETTING_HISTOGRAM,  valueToSet);
        i_activity.ShowHistogram(show);
    }

    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }
}
