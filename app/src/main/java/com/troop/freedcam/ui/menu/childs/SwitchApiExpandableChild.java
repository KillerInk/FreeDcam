package com.troop.freedcam.ui.menu.childs;

import android.os.Build;

import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.MainActivity_v2;
import com.troop.freedcam.ui.menu.ExpandableGroup;

import java.util.ArrayList;

/**
 * Created by troop on 13.12.2014.
 */
public class SwitchApiExpandableChild extends ExpandableChild
{
    MainActivity_v2 context;


    public SwitchApiExpandableChild(MainActivity_v2 context, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname) {
        super(context, group, name, appSettingsManager, settingsname);
        this.context = context;
        this.parameterHolder = new simpleModeParam();
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
        context.ActivateSonyApi(value);
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
                return new String[] {AppSettingsManager.API_SONY, AppSettingsManager.API_1, /*AppSettingsManager.API_2*/};
            else
                return new String[] {AppSettingsManager.API_SONY, AppSettingsManager.API_1};
        }


    }
}
