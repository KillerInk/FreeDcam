package com.freedcam.ui.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;

import com.freedcam.apis.i_camera.AbstractCameraUiWrapper;

/**
 * Created by troop on 21.07.2015.
 */
public class MenuItemGPS extends MenuItem
{
    public MenuItemGPS(Context context) {
        super(context);
    }

    public MenuItemGPS(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        super.SetParameter(cameraUiWrapper.camParametersHandler.locationParameter);

    }
}
