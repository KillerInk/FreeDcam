package troop.com.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;

/**
 * Created by troop on 21.07.2015.
 */
public class MenuItemOrientationHack extends MenuItem
{
    AbstractCameraUiWrapper cameraUiWrapper;

    public MenuItemOrientationHack(Context context) {
        super(context);
    }

    public MenuItemOrientationHack(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        if (cameraUiWrapper instanceof CameraUiWrapper)
            this.setVisibility(VISIBLE);
        else
            this.setVisibility(GONE);
        if (appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals("true"))
            onValueChanged(StringUtils.ON);
        else
            onValueChanged(StringUtils.OFF);
    }

    @Override
    public String[] GetValues() {
        return new String[] {StringUtils.ON, StringUtils.OFF};
    }

    @Override
    public void SetValue(String value)
    {
        if (value.equals(StringUtils.ON))
            appSettingsManager.setString(AppSettingsManager.SETTING_OrientationHack,  "true");
        else
            appSettingsManager.setString(AppSettingsManager.SETTING_OrientationHack, "false");
        ((CamParametersHandler)cameraUiWrapper.camParametersHandler).SetCameraRotation();
        ((CamParametersHandler)cameraUiWrapper.camParametersHandler).SetPictureOrientation(0);
        onValueChanged(value);
    }
}
