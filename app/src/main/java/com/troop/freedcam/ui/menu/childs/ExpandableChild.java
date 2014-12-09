package com.troop.freedcam.ui.menu.childs;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.modules.I_ModuleEvent;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;

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
    protected ArrayList<String> modulesToShow;
    AbstractCameraUiWrapper cameraUiWrapper;

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

    protected void init(Context context)
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
    public void setParameterHolder( I_ModeParameter parameterHolder, AppSettingsManager appSettingsManager, String settingsname, ArrayList<String> modulesToShow, AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.parameterHolder = parameterHolder;
        this.appSettingsManager = appSettingsManager;
        this.settingsname = settingsname;
        this.cameraUiWrapper = cameraUiWrapper;
        String campara = parameterHolder.GetValue();
        String settingValue = appSettingsManager.getString(settingsname);
        if (settingValue.equals("")) {
            appSettingsManager.setString(settingsname, campara);
            Log.d(getTAG(), "No appSetting set default " + Name + ":" + campara);
        }
        if (campara != null &&!settingValue.equals(campara) && !settingValue.equals("") && !campara.equals("")) {
            parameterHolder.SetValue(settingValue, false);
            appSettingsManager.setString(settingsname, settingValue);
            Log.d(getTAG(), "Load default appsetting " + Name + ":" + campara);
        }
        nameTextView.setText(Name);
        valueTextView.setText(appSettingsManager.getString(settingsname));
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
        return "freedcam." + Name;
    }
}
