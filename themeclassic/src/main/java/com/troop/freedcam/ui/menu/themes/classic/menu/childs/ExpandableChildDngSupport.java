package com.troop.freedcam.ui.menu.themes.classic.menu.childs;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.parameters.modes.SimpleModeParameter;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.ExpandableGroup;

import java.util.ArrayList;

/**
 * Created by troop on 08.01.2015.
 */
public class ExpandableChildDngSupport extends ExpandableChildTimelapseFps
{

    protected Switch aSwitch;
    protected AbstractParameterHandler parameterHandler;

    public ExpandableChildDngSupport(Context context, ExpandableGroup group, AppSettingsManager appSettingsManager, String name, String settingsname) {
        super(context, group, appSettingsManager, name, settingsname);
    }

    @Override
    protected void init(Context context) {

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
                parameterHandler.isDngActive = isChecked;
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
        String dng  = appSettingsManager.getString(AppSettingsManager.SETTING_DNG);
        this.parameterHandler = parameterHandler;
        if (dng.equals(""))
        {
            appSettingsManager.setString(AppSettingsManager.SETTING_DNG, "false");
            dng = "false";
        }
        if (dng.equals("false")) {
            aSwitch.setChecked(false);
            parameterHandler.isDngActive = false;
        }
        else
        {
            parameterHandler.isDngActive = true;
            aSwitch.setChecked(true);
        }
    }



    @Override
    public void VideoProfileChanged(String videoProfile)
    {
        if (videoProfile != null && !videoProfile.equals("") && videoProfile.contains("bayer"))
        {
            if (!isVisible)
            {
                isVisible = true;
                group.submenu.addView(this);

            }
            ((SimpleModeParameter)parameterHolder).setIsSupported(true);
        }
        else
        {
            if (isVisible)
            {
                isVisible =false;
                group.submenu.removeView(this);


            }
            ((SimpleModeParameter)parameterHolder).setIsSupported(false);
        }
        //reload this way subitems
        group.ModuleChanged("");

    }


}
