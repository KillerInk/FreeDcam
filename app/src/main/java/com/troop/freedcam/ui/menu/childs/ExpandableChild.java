package com.troop.freedcam.ui.menu.childs;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.modules.I_ModuleEvent;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.ExpandableGroup;

import java.util.ArrayList;

/**
 * Created by troop on 18.08.2014.
 */
public class ExpandableChild extends LinearLayout implements I_ModuleEvent, AbstractModeParameter.I_ModeParameterEvent
{
    protected String Name;
    protected AbstractModeParameter parameterHolder;
    protected AppSettingsManager appSettingsManager;
    Context context;
    TextView nameTextView;
    TextView valueTextView;
    protected String settingsname;
    protected ArrayList<String> modulesToShow;
    ExpandableGroup group;
    boolean isVisible = false;


    public ExpandableChild(Context context, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname) {
        super(context);
        this.group = group;
        this.Name = name;
        this.appSettingsManager = appSettingsManager;
        this.settingsname = settingsname;
        init(context);
    }


    protected void init(Context context)
    {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expandable_childs, this);
        nameTextView = (TextView)findViewById(R.id.tvChild);
        nameTextView.setText(Name);
        valueTextView = (TextView)findViewById(R.id.tvChildValue);
        modulesToShow = new ArrayList<String>();
    }

    public String getName() {
        return Name;
    }

    public String Value()
    {
        if (!parameterHolder.GetValue().equals(""))
            return parameterHolder.GetValue();
        else
            return appSettingsManager.getString(settingsname);
    }
    public void setValue(String value)
    {
        valueTextView.setText(value);
        parameterHolder.SetValue(value, true);
        appSettingsManager.setString(settingsname, value);
        Log.d(getTAG(), "Set " + Name + ":" + value);
    }

    public I_ModeParameter getParameterHolder(){ return parameterHolder;}
    public void setParameterHolder(AbstractModeParameter parameterHolder, ArrayList<String> modulesToShow)
    {
        this.parameterHolder = parameterHolder;
        this.modulesToShow = modulesToShow;
        if (parameterHolder != null)
            parameterHolder.addEventListner(this);


        if (parameterHolder.IsSupported())
        {
            String campara = parameterHolder.GetValue();
            onValueChanged(campara);
            onIsSupportedChanged(true);
        }
    }



    @Override
    public String ModuleChanged(String module)
    {
        if(modulesToShow.contains(module) || modulesToShow.contains(ModuleHandler.MODULE_ALL))
            this.setVisibility(VISIBLE);
        else
            this.setVisibility(GONE);
        return null;
    }

    protected String getTAG()
    {
        return "freedcam." + Name;
    }

    @Override
    public void onValueChanged(String val)
    {
        String settingValue = appSettingsManager.getString(settingsname);
        if (settingValue.equals("")) {
            appSettingsManager.setString(settingsname, val);
            Log.d(getTAG(), "No appSetting set default " + Name + ":" + val);
        }
        if (!settingValue.equals(val))
        {
            parameterHolder.SetValue(settingValue, false);
            appSettingsManager.setString(settingsname, settingValue);
            val = settingValue;
            Log.d(getTAG(), "Load default appsetting " + Name + ":" + val);
        }
        valueTextView.setText(val);

    }

    @Override
    public void onIsSupportedChanged(boolean isSupported)
    {
        if (isSupported && !isVisible)
        {
            onValueChanged(parameterHolder.GetValue());
            isVisible = true;
            group.submenu.addView(this);
        }
        else if(!isSupported && isVisible)
        {
            isVisible = false;
            group.submenu.removeView(this);
        }
    }

    @Override
    public void onIsSetSupportedChanged(boolean isSupported)
    {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }
}
