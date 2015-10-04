package troop.com.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;

/**
 * Created by GeorgeKiarie on 10/4/2015.
 */
public class MenuItemInterval extends MenuItem
{
    AbstractCameraUiWrapper cameraUiWrapper;

    public MenuItemInterval(Context context) {
        super(context);
    }

    public MenuItemInterval(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        if (cameraUiWrapper instanceof CameraUiWrapper)
            this.setVisibility(VISIBLE);
        else
            this.setVisibility(GONE);
        if (!appSettingsManager.getString(AppSettingsManager.SETTING_INTERVAL).equals("off"));

    }

    @Override
    public String[] GetValues() {
        String [] intv = {"off","1 sec","2 sec","3 sec","4 sec","5 sec","6 sec","7 sec","8 sec","9 sec",
                "10 sec","11 sec","12 sec","13 sec","14 sec","15 sec","16 sec","17 sec","18 sec","19 sec","20 sec",
                "21 sec","22 sec","23 sec","24 sec","25 sec","26 sec","27 sec","28 sec","29 sec","30 sec","60 sec","120 sec","240 sec"};
        //return new String[] {StringUtils.ON, StringUtils.OFF};
        return intv;
    }

    @Override
    public void SetValue(String value)
    {
        appSettingsManager.setString(AppSettingsManager.SETTING_INTERVAL,  value);

      /*  if (value.equals(StringUtils.ON))
            appSettingsManager.setString(AppSettingsManager.SETTING_OrientationHack,  "true");
        else
            appSettingsManager.setString(AppSettingsManager.SETTING_OrientationHack, "false");
        ((CamParametersHandler)cameraUiWrapper.camParametersHandler).SetCameraRotation();
        ((CamParametersHandler)cameraUiWrapper.camParametersHandler).SetPictureOrientation(0);
        onValueChanged(value); */
    }
}