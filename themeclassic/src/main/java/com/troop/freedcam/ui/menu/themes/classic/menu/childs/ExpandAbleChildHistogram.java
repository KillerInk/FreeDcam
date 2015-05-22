package com.troop.freedcam.ui.menu.themes.classic.menu.childs;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.camera.parameters.modes.SimpleModeParameter;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.sonyapi.CameraUiWrapperSony;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.menu.themes.R;
import com.troop.freedcam.ui.menu.themes.classic.menu.ExpandableGroup;
import com.troop.freedcam.utils.DeviceUtils;

/**
 * Created by troop on 28.03.2015.
 */
public class ExpandAbleChildHistogram extends ExpandableChildOrientationHack
{
    boolean fromSettings = false;

    I_Activity i_activity;
    public ExpandAbleChildHistogram(Context context, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname, I_Activity i_activity)
    {
        super(context, group, name, appSettingsManager, settingsname);
        this.i_activity = i_activity;
    }

    @Override
    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper) {
        this.cameraUiWrapper = cameraUiWrapper;
        if (cameraUiWrapper instanceof CameraUiWrapper || cameraUiWrapper instanceof CameraUiWrapperSony)
        {
            ((SimpleModeParameter) parameterHolder).setIsSupported(true);
            if (appSettingsManager.getString(AppSettingsManager.SETTING_HISTOGRAM).equals("true"))
            {
                fromSettings = true;
                aSwitch.setChecked(true);
            }
        }
        else
            ((SimpleModeParameter)parameterHolder).setIsSupported(false);
        aSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        final String check = aSwitch.isChecked() +"";
        appSettingsManager.setString(AppSettingsManager.SETTING_HISTOGRAM,  check);
        i_activity.ShowHistogram(aSwitch.isChecked());
    }
}
