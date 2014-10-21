package com.troop.freecamv2.ui.menu;

import android.content.Context;
import android.util.AttributeSet;

import com.troop.freecamv2.camera.CameraUiWrapper;
import com.troop.freecamv2.camera.parameters.modes.I_ModeParameter;
import com.troop.freecamv2.ui.AppSettingsManager;

import java.util.ArrayList;

/**
 * Created by troop on 20.10.2014.
 */
public class PictureFormatExpandableChild extends ExpandableChild {
    public PictureFormatExpandableChild(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureFormatExpandableChild(Context context) {
        super(context);
    }

    public PictureFormatExpandableChild(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
    }

    @Override
    public String Value() {
        return super.Value();
    }

    @Override
    public I_ModeParameter getParameterHolder() {
        return super.getParameterHolder();
    }

    @Override
    public void setParameterHolder(I_ModeParameter parameterHolder, AppSettingsManager appSettingsManager, String settingsname, ArrayList<String> modulesToShow, CameraUiWrapper cameraUiWrapper) {
        super.setParameterHolder(parameterHolder, appSettingsManager, settingsname, modulesToShow, cameraUiWrapper);
    }
}
