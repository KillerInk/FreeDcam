package troop.com.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;

import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.ui.AppSettingsManager;

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
        if (appSettingsManager.GetCurrentModule().equals(AbstractModuleHandler.MODULE_VIDEO))
            parameter.SetValue(value, true);
    }

    @Override
    public String[] GetValues() {
        return super.GetValues();
    }
}
