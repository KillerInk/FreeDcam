package com.troop.freedcam.ui.menu.themes.classic.menu.childs;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.themes.R;
import com.troop.freedcam.ui.menu.themes.classic.menu.ExpandableGroup;

import java.util.ArrayList;

/**
 * Created by troop on 13.01.2015.
 */
public class ExpandbleChildAeBracket extends ExpandableChildDngSupport
{


    public ExpandbleChildAeBracket(Context context, ExpandableGroup group, AppSettingsManager appSettingsManager, String name, String settingsname) {
        super(context, group, appSettingsManager, name, settingsname);
    }

    @Override
    protected void init(Context context) {

    }

    protected void initt(Context context)
    {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expandablechildboolean_on_off, this);
        aSwitch = (Switch)findViewById(R.id.switch1);
        aSwitch.setText(Name);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                appSettingsManager.setString(settingsname, isChecked +"");
                parameterHandler.isAeBracketActive = isChecked;
                setAeBracketValue(parameterHandler, isChecked +"");
            }
        });

    }

    @Override
    public String Value() {
        return aSwitch.isChecked()+"";
    }

    @Override
    public void setValue(String value) {

    }


    public void setParameterHolder(AbstractModeParameter parameterHolder, ArrayList<String> modulesToShow,AbstractParameterHandler parameterHandler)
    {
        super.setParameterHolder(parameterHolder, modulesToShow);

        String dng  = appSettingsManager.getString(AppSettingsManager.SETTING_AEBRACKETACTIVE);
        this.parameterHandler = parameterHandler;
        if (dng.equals(""))
        {
            appSettingsManager.setString(AppSettingsManager.SETTING_AEBRACKETACTIVE, "false");
            dng = "false";
        }
        setAeBracketValue(parameterHandler, dng);
        ModuleChanged("");

    }

    private void setAeBracketValue(AbstractParameterHandler parameterHandler, String dng) {
        if (dng.equals("false")) {
            aSwitch.setChecked(false);
            parameterHandler.isAeBracketActive = false;
            parameterHandler.AE_Bracket.SetValue(false+"", true);
        }
        else
        {
            parameterHandler.isAeBracketActive = true;
            aSwitch.setChecked(true);
            parameterHandler.AE_Bracket.SetValue(true+"", true);
        }
    }

    @Override
    public String ModuleChanged(String module)
    {
        return super.ModuleChanged(module);
    }


}
