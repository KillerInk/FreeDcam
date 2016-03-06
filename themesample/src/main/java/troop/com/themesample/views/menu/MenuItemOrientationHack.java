package troop.com.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.camera2.CameraUiWrapperApi2;
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
        if (cameraUiWrapper instanceof CameraUiWrapper || cameraUiWrapper instanceof CameraUiWrapperApi2)
            this.setVisibility(VISIBLE);
        else
            this.setVisibility(GONE);
        if (AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_OrientationHack).equals(""))
            AppSettingsManager.APPSETTINGSMANAGER.setString(AppSettingsManager.SETTING_OrientationHack, StringUtils.OFF);
        if (AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_OrientationHack).equals(StringUtils.ON))
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
        AppSettingsManager.APPSETTINGSMANAGER.setString(AppSettingsManager.SETTING_OrientationHack, value);
        if (cameraUiWrapper instanceof CameraUiWrapper) {
            ((CamParametersHandler) cameraUiWrapper.camParametersHandler).SetCameraRotation();
            ((CamParametersHandler) cameraUiWrapper.camParametersHandler).SetPictureOrientation(0);
        }
        else if(cameraUiWrapper instanceof CameraUiWrapperApi2)
        {
            ((CameraUiWrapperApi2) cameraUiWrapper).cameraHolder.StopPreview();
            ((CameraUiWrapperApi2) cameraUiWrapper).cameraHolder.StartPreview();

        }
        onValueChanged(value);
    }
}
