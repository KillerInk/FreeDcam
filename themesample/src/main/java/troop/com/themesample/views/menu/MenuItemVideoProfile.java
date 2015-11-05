package troop.com.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;

import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;

/**
 * Created by troop on 22.07.2015.
 */
public class MenuItemVideoProfile extends MenuItem
{
    public MenuItemVideoProfile(Context context) {
        super(context);
    }

    public MenuItemVideoProfile(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void SetValue(String value) {
        appSettingsManager.setString(AppSettingsManager.SETTING_VIDEPROFILE, value);
        onValueChanged(value);
        parameter.SetValue(value, true);
    }

    @Override
    public void SetStuff(I_Activity i_activity, AppSettingsManager appSettingsManager, String settingvalue) {
        super.SetStuff(i_activity, appSettingsManager, settingvalue);
    }

    @Override
    public String[] GetValues() {
        return super.GetValues();
    }
}
