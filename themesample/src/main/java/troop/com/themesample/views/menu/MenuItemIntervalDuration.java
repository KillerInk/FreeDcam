package troop.com.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.utils.StringUtils;

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
        if (cameraUiWrapper instanceof CameraUiWrapper)
            this.setVisibility(VISIBLE);
        else
            this.setVisibility(GONE);


    }

    @Override
    public String[] GetValues() {
        String [] intv = {"off","1 min", "2 min", "5 min","10 min","15 min","20 min ","25 min","30 min","60 min"/*,"Bulb"*/};
        //return new String[] {StringUtils.ON, StringUtils.OFF};
        return intv;
    }

    @Override
    public void SetValue(String value)
    {
        appSettingsManager.setString(AppSettingsManager.SETTING_INTERVAL_DURATION, value);
        onValueChanged(value);

      /*  if (value.equals(StringUtils.ON))
            appSettingsManager.setString(AppSettingsManager.SETTING_OrientationHack,  "true");
        else
            appSettingsManager.setString(AppSettingsManager.SETTING_OrientationHack, "false");
        ((CamParametersHandler)cameraUiWrapper.camParametersHandler).SetCameraRotation();
        ((CamParametersHandler)cameraUiWrapper.camParametersHandler).SetPictureOrientation(0);
        onValueChanged(value); */
    }
}