package com.troop.freedcam.ui.menu.themes.classic.menu.childs;

import android.content.Context;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_PreviewSizeEvent;
import com.troop.freedcam.ui.menu.themes.classic.menu.ExpandableGroup;

import java.util.ArrayList;

/**
 * Created by troop on 17.11.2014.
 */
public class VideoProfileExpandableChild extends ExpandableChild
{
    private I_PreviewSizeEvent previewSizeEvent;
    AbstractCameraUiWrapper cameraUiWrapper;
    public I_VideoProfile videoProfileChanged;

    public VideoProfileExpandableChild(Context context, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname)
    {
        super(context, group, name, appSettingsManager, settingsname);
        this.previewSizeEvent = previewSizeEvent;
    }


    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String Value() {
        return super.Value();
    }

    @Override
    public void setValue(String value) {
        appSettingsManager.setString(settingsname, value);
        nameTextView.setText(Name);
        valueTextView.setText(appSettingsManager.getString(settingsname));

        parameterHolder.SetValue(value,false);

        if (videoProfileChanged != null)
            videoProfileChanged.VideoProfileChanged(value);
        /*if (DeviceUtils.isLG_G3())
        {
            previewSizeEvent.OnPreviewSizeChanged(cameraUiWrapper.camParametersHandler.VideoProfilesG3.GetCameraProfile(value).videoFrameWidth,
                    cameraUiWrapper.camParametersHandler.VideoProfilesG3.GetCameraProfile(value).videoFrameHeight);

        }
        else
        {
            previewSizeEvent.OnPreviewSizeChanged(cameraUiWrapper.camParametersHandler.VideoProfiles.GetCameraProfile(value).videoFrameWidth,
                    cameraUiWrapper.camParametersHandler.VideoProfiles.GetCameraProfile(value).videoFrameHeight);
        }*/
    }

    @Override
    public I_ModeParameter getParameterHolder() {
        return super.getParameterHolder();
    }


    public void setParameterHolder(AbstractModeParameter parameterHolder, ArrayList<String> modulesToShow)
    {
        super.setParameterHolder(parameterHolder, modulesToShow);

        //String campara = parameterHolder.GetValue();
        String settingValue = appSettingsManager.getString(settingsname);
        if (settingValue == null || settingValue.equals("")) {
            settingValue = parameterHolder.GetValues()[0];
            appSettingsManager.setString(settingsname, settingValue);

        }

        if (videoProfileChanged != null)
            videoProfileChanged.VideoProfileChanged(settingValue);
        nameTextView.setText(Name);
        valueTextView.setText(appSettingsManager.getString(settingsname));
        //appSettingsManager.setString(settingsname, settingValue);
    }

    @Override
    public String ModuleChanged(String module) {
        return super.ModuleChanged(module);
    }

    @Override
    protected String getTAG() {
        return super.getTAG();
    }

}
