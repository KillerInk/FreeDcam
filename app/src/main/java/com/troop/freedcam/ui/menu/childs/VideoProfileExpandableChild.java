package com.troop.freedcam.ui.menu.childs;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.modules.VideoModuleG3;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.TextureView.I_PreviewSizeEvent;
import com.troop.freedcam.ui.menu.ExpandableGroup;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.ArrayList;

/**
 * Created by troop on 17.11.2014.
 */
public class VideoProfileExpandableChild extends ExpandableChild
{
    private I_PreviewSizeEvent previewSizeEvent;
    AbstractCameraUiWrapper cameraUiWrapper;
    public I_VideoProfile videoProfileChanged;

    public VideoProfileExpandableChild(Context context, I_PreviewSizeEvent previewSizeEvent, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname)
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
        if (DeviceUtils.isLGADV() && Build.VERSION.SDK_INT < 21)
        {
            VideoModuleG3 g3 = (VideoModuleG3) cameraUiWrapper.moduleHandler.GetCurrentModule();
            g3.UpdatePreview();
        }
        if (videoProfileChanged != null)
            videoProfileChanged.VideoProfileChanged(value);
        /*if (DeviceUtils.isLGADV())
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

    @Override
    public void setParameterHolder(AbstractModeParameter parameterHolder, ArrayList<String> modulesToShow) {
        super.setParameterHolder(parameterHolder, modulesToShow);
        //String campara = parameterHolder.GetValue();
        String settingValue = appSettingsManager.getString(settingsname);
        if (settingValue == null || settingValue == "") {
            settingValue = parameterHolder.GetValues()[0];
            appSettingsManager.setString(settingsname, settingValue);
            if (videoProfileChanged != null)
                videoProfileChanged.VideoProfileChanged(settingValue);
        }


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
