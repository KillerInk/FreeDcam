package com.troop.freecamv2.ui.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freecam.R;
import com.troop.freecamv2.camera.parameters.modes.I_ModeParameter;
import com.troop.freecamv2.ui.AppSettingsManager;

/**
 * Created by troop on 18.08.2014.
 */
public class ExpandableChild extends LinearLayout
{
    private String Name;
    private I_ModeParameter parameterHolder;
    private AppSettingsManager appSettingsManager;
    Context context;
    TextView nameTextView;
    TextView valueTextView;
    private String settingsname;

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
        parameterHolder.SetValue(value);
        appSettingsManager.setString(settingsname, value);
    }

    public I_ModeParameter getParameterHolder(){ return parameterHolder;}
    public void setParameterHolder( I_ModeParameter parameterHolder, AppSettingsManager appSettingsManager, String settingsname)
    {
        this.parameterHolder = parameterHolder;
        this.appSettingsManager = appSettingsManager;
        this.settingsname = settingsname;
        String campara = parameterHolder.GetValue();
        String settingValue = appSettingsManager.getString(settingsname);
        if (settingValue.equals(""))
            appSettingsManager.setString(settingsname, campara);
        if (!settingValue.equals(campara) && !settingValue.equals(""))
            parameterHolder.SetValue(settingValue);
        nameTextView.setText(Name);
        valueTextView.setText(parameterHolder.GetValue());
    }
}
