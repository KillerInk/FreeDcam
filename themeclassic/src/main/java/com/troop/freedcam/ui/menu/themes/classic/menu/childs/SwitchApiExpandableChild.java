package com.troop.freedcam.ui.menu.themes.classic.menu.childs;

import android.content.Context;
import android.os.Build;
import android.os.Handler;

import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.menu.themes.classic.menu.ExpandableGroup;

import java.util.ArrayList;

/**
 * Created by troop on 13.12.2014.
 */
public class SwitchApiExpandableChild extends ExpandableChild
{
    I_Activity activity;


    public SwitchApiExpandableChild(Context context,I_Activity activity, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname) {
        super(context, group, name, appSettingsManager, settingsname);
        this.activity = activity;
        this.parameterHolder = new simpleModeParam(null);
    }



    @Override
    public void setParameterHolder(AbstractModeParameter parameterHolder, ArrayList<String> modulesToShow)
    {
        super.setParameterHolder(this.parameterHolder, modulesToShow);
        nameTextView.setText("Switch Api");
        valueTextView.setText(Value());

    }

    @Override
    public String ModuleChanged(String module)
    {

        return null;
    }

    @Override
    public void setValue(String value)
    {
        appSettingsManager.setCamApi(value);
        valueTextView.setText(value);
        activity.SwitchCameraAPI(value);
    }

    @Override
    public String Value()
    {
        String ret = appSettingsManager.getCamApi();
        if (ret.equals(""))
            ret = "api1";
        return ret;
    }

    @Override
    public String getName() {
        return "sonyconnect";
    }



    class simpleModeParam extends AbstractModeParameter
    {
        public simpleModeParam(Handler uiHandler) {
            super(uiHandler);
        }

        @Override
        public boolean IsSupported() {
            return true;
        }

        @Override
        public void SetValue(String valueToSet, boolean setToCamera) {


        }

        @Override
        public String GetValue() {
            return null;
        }

        @Override
        public String[] GetValues()
        {
            if (Build.VERSION.SDK_INT  >= 21)
                return new String[] {AppSettingsManager.API_SONY, AppSettingsManager.API_1, AppSettingsManager.API_2};
            else
                return new String[] {AppSettingsManager.API_SONY, AppSettingsManager.API_1};
        }


    }
}
