package com.troop.freedcam.ui.menu.themes.classic.menu.childs;

import android.content.Context;

import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.themes.classic.menu.ExpandableGroup;


import java.util.ArrayList;

/**
 * Created by troop on 22.02.2015.
 */
public class ExpandableChildExternalShutter extends ExpandableChild
{
    public static String VoLP = "Vol+";
    public static String VoLM = "Vol-";
    public static String Hook = "Hook";


    public ExpandableChildExternalShutter(Context context, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname)
    {
        super(context, group, name, appSettingsManager, settingsname);
        this.parameterHolder = new ParameterExternalShutter();

    }



    @Override
    public void setValue(String value)
    {
        parameterHolder.SetValue(value, false);
        valueTextView.setText(value);
    }

    @Override
    public String Value() {
        return parameterHolder.GetValue();
    }

    @Override
    public void setParameterHolder(AbstractModeParameter parameterHolder, ArrayList<String> modulesToShow) {
        super.setParameterHolder(this.parameterHolder, modulesToShow);
        valueTextView.setText(this.parameterHolder.GetValue());
    }

    @Override
    protected void init(Context context) {
        super.init(context);
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public I_ModeParameter getParameterHolder() {
        return super.getParameterHolder();
    }

    @Override
    public String ModuleChanged(String module)
    {
        return module;
    }

    @Override
    protected String getTAG() {
        return super.getTAG();
    }

    @Override
    public void onValueChanged(String val) {

    }

    @Override
    public void onIsSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onIsSetSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    private class ParameterExternalShutter extends AbstractModeParameter {
        private String[] values = {VoLP, VoLM, Hook};

        public ParameterExternalShutter()
        {}

        public boolean IsSupported()
        {
            return true;
        }
        public void setIsSupported(boolean s)
        {

        }

        public void SetValue(String valueToSet, boolean setToCamera)
        {
            appSettingsManager.setString(settingsname, valueToSet);
        }

        public String GetValue()
        {
            if (appSettingsManager.getString(settingsname).equals(""))
                return "Hook";
            else
                return appSettingsManager.getString(settingsname);
        }

        public String[] GetValues() {
            return values;
        }
    }
}
