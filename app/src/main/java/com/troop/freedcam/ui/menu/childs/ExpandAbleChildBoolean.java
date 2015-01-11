package com.troop.freedcam.ui.menu.childs;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.ExpandableGroup;

import java.util.ArrayList;

import static com.troop.freedcam.ui.menu.childs.ExpandableChildNumber.*;

/**
 * Created by troop on 08.01.2015.
 */
public class ExpandAbleChildBoolean extends ExpandableChildNumber
{

    Switch aSwitch;
    AbstractParameterHandler parameterHandler;

    public ExpandAbleChildBoolean(Context context, ExpandableGroup group, AppSettingsManager appSettingsManager, String name,String settingsname) {
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
        if (videoProfile.contains("bayer-mipi"))
        {
            if (!group.getItems().contains(this))
            {
                group.getItems().add(this);

            }
        }
        else
        {
            if (group.getItems().contains(this))
                group.getItems().remove(this);
        }
        //reload this way subitems
        group.ModuleChanged("");

    }


}
