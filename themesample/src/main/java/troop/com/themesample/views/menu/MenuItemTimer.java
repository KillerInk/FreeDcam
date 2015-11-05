package troop.com.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;

/**
 * Created by GeorgeKiarie on 10/4/2015.
 */
public class MenuItemTimer extends MenuItem
{
    AbstractCameraUiWrapper cameraUiWrapper;

    public MenuItemTimer(Context context) {
        super(context);
    }

    public MenuItemTimer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        if (cameraUiWrapper instanceof CameraUiWrapper)
            this.setVisibility(VISIBLE);
        else
            this.setVisibility(GONE);


    }

    @Override
    public void SetStuff(I_Activity i_activity, AppSettingsManager appSettingsManager, String settingvalue) {
        super.SetStuff(i_activity, appSettingsManager, settingvalue);
        onValueChanged(appSettingsManager.getString(AppSettingsManager.SETTING_TIMER));
    }

    @Override
    public String[] GetValues() {
        String [] intv = {"0 sec","5 sec","10 sec","15 sec","20 sec"};
        //return new String[] {StringUtils.ON, StringUtils.OFF};
        return intv;
    }

    @Override
    public void SetValue(String value)
    {
        appSettingsManager.setString(AppSettingsManager.SETTING_TIMER, value);
        onValueChanged(value);
    }
}