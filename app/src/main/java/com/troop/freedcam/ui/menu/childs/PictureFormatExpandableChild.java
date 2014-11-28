package com.troop.freedcam.ui.menu.childs;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.parameters.modes.I_ModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.childs.ExpandableChild;
import com.troop.freedcam.utils.DeviceUtils;

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
    public void setValue(String value)
    {
        //TODO this is ugly need to find a different way.. class design fail
        if (cameraUiWrapper.camParametersHandler.rawSupported)
        {
            if (value.equals("raw") || value.equals("dng"))
            {
                //galaxy nexus and atrix2
                if (DeviceUtils.isOmap() && !DeviceUtils.isO3d())
                {
                    parameterHolder.SetValue("raw", true);
                }
                else if (DeviceUtils.isMediaTekTHL5000())
                {
                    //set raw
                    cameraUiWrapper.camParametersHandler.setTHL5000Raw(true);
                }
                else if (cameraUiWrapper.camParametersHandler.BayerMipiFormat != null)
                    parameterHolder.SetValue(cameraUiWrapper.camParametersHandler.BayerMipiFormat, true);
                else if (DeviceUtils.isXperiaL())
                    parameterHolder.SetValue("raw", true);
                else
                {
                    parameterHolder.SetValue(value, false);
                }
            }
            else
            {
                if (DeviceUtils.isMediaTekTHL5000())
                {
                   //set jpeg
                    cameraUiWrapper.camParametersHandler.setTHL5000Raw(false);
                }
                else
                    parameterHolder.SetValue(value, true);
            }
        }
        else
            parameterHolder.SetValue(value, true);
        valueTextView.setText(value);
        appSettingsManager.setString(settingsname, value);
        Log.d(getTAG(), "Set " + Name + ":" + value);
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
        this.parameterHolder = parameterHolder;
        this.appSettingsManager = appSettingsManager;
        this.settingsname = settingsname;
        this.cameraUiWrapper = cameraUiWrapper;
        String campara = parameterHolder.GetValue();
        String settingValue = appSettingsManager.getString(settingsname);
        //TODO code design fail
        //process raw supported devices
        settingValue = setDeviceSettings(parameterHolder, appSettingsManager, settingsname, cameraUiWrapper, campara, settingValue);
        nameTextView.setText(Name);
        valueTextView.setText(appSettingsManager.getString(settingsname));
        appSettingsManager.setString(settingsname, settingValue);
        AddModulesToShow(modulesToShow);
    }

    private String setDeviceSettings(I_ModeParameter parameterHolder, AppSettingsManager appSettingsManager, String settingsname, CameraUiWrapper cameraUiWrapper, String campara, String settingValue) {
        if (cameraUiWrapper.camParametersHandler.rawSupported)
        {
            if (settingValue.equals(""))
            {
                appSettingsManager.setString(settingsname, "jpeg");
                settingValue = "jpeg";
            }
            if (settingValue.equals("raw") || settingValue.equals("dng"))
            {
                if (DeviceUtils.isMediaTekTHL5000())
                {
                    cameraUiWrapper.camParametersHandler.setTHL5000Raw(true);
                }
                else
                {
                    //BayerMipiFormat is null if its not in the picture-formats
                    if (cameraUiWrapper.camParametersHandler.BayerMipiFormat != null)
                        parameterHolder.SetValue(cameraUiWrapper.camParametersHandler.BayerMipiFormat, false);
                    else
                    {
                        parameterHolder.SetValue(settingValue, false);
                    }
                }
            }
        }
        //process all other devices
        else
        {
            if (settingValue.equals(""))
                appSettingsManager.setString(settingsname, campara);
            parameterHolder.SetValue(settingValue, false);
        }
        return settingValue;
    }
}
