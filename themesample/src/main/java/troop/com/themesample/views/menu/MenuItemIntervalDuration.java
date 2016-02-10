package troop.com.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;

/**
 * Created by GeorgeKiarie on 10/4/2015.
 */
public class MenuItemIntervalDuration extends MenuItem
{
    AbstractCameraUiWrapper cameraUiWrapper;

    public MenuItemIntervalDuration(Context context) {
        super(context);
    }

    public MenuItemIntervalDuration(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void SetStuff(I_Activity i_activity, AppSettingsManager appSettingsManager, String settingvalue) {
        super.SetStuff(i_activity, appSettingsManager, settingvalue);
        onValueChanged(appSettingsManager.getString(AppSettingsManager.SETTING_INTERVAL_DURATION));
    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        super.SetParameter(cameraUiWrapper.camParametersHandler.IntervalDuration);
    }

    @Override
    public String[] GetValues() {

        //return new String[] {StringUtils.ON, StringUtils.OFF};
        return parameter.GetValues();
    }

    @Override
    public void SetValue(String value)
    {
        appSettingsManager.setString(AppSettingsManager.SETTING_INTERVAL_DURATION, value);
        onValueChanged(value);
        parameter.SetValue(value,true);
    }
}