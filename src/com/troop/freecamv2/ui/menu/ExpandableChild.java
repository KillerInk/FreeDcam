package com.troop.freecamv2.ui.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freecam.R;
import com.troop.freecamv2.camera.CameraUiWrapper;
import com.troop.freecamv2.camera.modules.I_ModuleEvent;
import com.troop.freecamv2.camera.modules.ModuleHandler;
import com.troop.freecamv2.camera.parameters.modes.I_ModeParameter;
import com.troop.freecamv2.ui.AppSettingsManager;
import com.troop.freecamv2.utils.DeviceUtils;
import com.troop.freecamv2.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by troop on 18.08.2014.
 */
public class ExpandableChild extends LinearLayout implements I_ModuleEvent
{
    protected String Name;
    protected I_ModeParameter parameterHolder;
    protected AppSettingsManager appSettingsManager;
    Context context;
    TextView nameTextView;
    TextView valueTextView;
    protected String settingsname;
    ArrayList<String> modulesToShow;
    CameraUiWrapper cameraUiWrapper;

    public ExpandableChild(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExpandableChild(Context context) {
        super(context);
        init(context);
    }

    public ExpandableChild(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context)
    {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expandable_childs, this);
        nameTextView = (TextView)findViewById(R.id.tvChild);
        valueTextView = (TextView)findViewById(R.id.tvChildValue);
        modulesToShow = new ArrayList<String>();
    }

    public String getName() {
        return Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }
    public String Value() {
        return parameterHolder.GetValue();
    }
    public void setValue(String value)
    {
        valueTextView.setText(value);
        parameterHolder.SetValue(value, true);
        appSettingsManager.setString(settingsname, value);
        Log.d(getTAG(), "Set " + Name + ":" + value);
    }

    public I_ModeParameter getParameterHolder(){ return parameterHolder;}
    public void setParameterHolder( I_ModeParameter parameterHolder, AppSettingsManager appSettingsManager, String settingsname, ArrayList<String> modulesToShow, CameraUiWrapper cameraUiWrapper)
    {
        this.parameterHolder = parameterHolder;
        this.appSettingsManager = appSettingsManager;
        this.settingsname = settingsname;
        this.cameraUiWrapper = cameraUiWrapper;
        String campara = parameterHolder.GetValue();
        String settingValue = appSettingsManager.getString(settingsname);
        if (settingValue.equals("")) {

            Log.d(getTAG(), "No appSetting set default " + Name + ":" + campara);
        }
        if (!settingValue.equals(campara) && !settingValue.equals("")) {
            parameterHolder.SetValue(settingValue, false);
            Log.d(getTAG(), "Load default appsetting " + Name + ":" + campara);
        }
        nameTextView.setText(Name);
        valueTextView.setText(parameterHolder.GetValue());
        AddModulesToShow(modulesToShow);
    }

    public void AddModulesToShow(ArrayList<String> modulesToShow)
    {
        this.modulesToShow = modulesToShow;
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
        return "freecam." + Name;
    }
}
