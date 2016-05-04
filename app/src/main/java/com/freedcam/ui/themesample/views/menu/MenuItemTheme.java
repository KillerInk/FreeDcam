package com.freedcam.ui.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.freedcam.apis.i_camera.parameters.AbstractModeParameter;
import com.freedcam.utils.AppSettingsManager;

/**
 * Created by troop on 14.06.2015.
 */
public class MenuItemTheme extends MenuItem {
    public MenuItemTheme(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuItemTheme(Context context) {
        super(context);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
    }

    @Override
    protected void inflateTheme(LayoutInflater inflater) {
        super.inflateTheme(inflater);
    }

    @Override
    public void SetParameter(AbstractModeParameter parameter)
    {
        if (parameter == null)
        {
            this.setVisibility(GONE);
            return;
        }
        else
            this.setVisibility(VISIBLE);
        this.parameter = parameter;
        String s = appSettingsManager.GetTheme();
        if (s == null || s.equals("")) {
            s = "Sample";
            appSettingsManager.setString(settingsname, s);
        }
        valueText.setText(s);
    }

    @Override
    public void SetValue(String value) {
        appSettingsManager.SetTheme(value);
        i_activity.SetTheme(value);
        onValueChanged(value);
    }
}
