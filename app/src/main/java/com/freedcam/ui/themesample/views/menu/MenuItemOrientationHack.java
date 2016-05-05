package com.freedcam.ui.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.camera2.camera.CameraUiWrapperApi2;
import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.StringUtils;

/**
 * Created by troop on 21.07.2015.
 */
public class MenuItemOrientationHack extends MenuItem
{
    private AbstractCameraUiWrapper cameraUiWrapper;

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
            this.setVisibility(View.VISIBLE);
        else
            this.setVisibility(View.GONE);
        if (appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals(""))
            appSettingsManager.setString(AppSettingsManager.SETTING_OrientationHack, StringUtils.OFF);
        if (appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals(StringUtils.ON))
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
        appSettingsManager.setString(AppSettingsManager.SETTING_OrientationHack, value);
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
