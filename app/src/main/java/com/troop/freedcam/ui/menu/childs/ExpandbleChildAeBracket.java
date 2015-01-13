package com.troop.freedcam.ui.menu.childs;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.modules.I_ModuleEvent;
import com.troop.freedcam.camera.parameters.modes.SimpleModeParameter;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.ExpandableGroup;

import java.util.ArrayList;

/**
 * Created by troop on 13.01.2015.
 */
public class ExpandbleChildAeBracket extends ExpandableChildDngSupport
{

    SimpleModeParameter simpleModeParameter;

    public ExpandbleChildAeBracket(Context context, ExpandableGroup group, AppSettingsManager appSettingsManager, String name, String settingsname) {
        super(context, group, appSettingsManager, name, settingsname);
    }

    protected void initt(Context context)
    {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expandablechildboolean, this);
        aSwitch = (Switch)findViewById(R.id.switch1);
        aSwitch.setText(Name);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                appSettingsManager.setString(settingsname, isChecked +"");
                parameterHandler.isAeBracketActive = isChecked;
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
        this.simpleModeParameter = (SimpleModeParameter)parameterHolder;
        String dng  = appSettingsManager.getString(AppSettingsManager.SETTING_AEBRACKETACTIVE);
        this.parameterHandler = parameterHandler;
        if (dng.equals(""))
        {
            appSettingsManager.setString(AppSettingsManager.SETTING_AEBRACKETACTIVE, "false");
            dng = "false";
        }
        if (dng.equals("false")) {
            aSwitch.setChecked(false);
            parameterHandler.isAeBracketActive = false;
        }
        else
        {
            parameterHandler.isAeBracketActive = true;
            aSwitch.setChecked(true);
        }

    }

    @Override
    public String ModuleChanged(String module)
    {
        if (modulesToShow.contains(module) )
        {
            if (!isVisible && parameterHandler.AE_Bracket != null && parameterHandler.AE_Bracket.IsSupported())
             group.submenu.addView(this);
        }
        else if (isVisible && !modulesToShow.contains(module))
        {
            group.submenu.removeView(this);
        }

        return super.ModuleChanged(module);
    }
}
