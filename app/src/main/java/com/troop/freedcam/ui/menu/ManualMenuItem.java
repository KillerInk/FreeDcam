package com.troop.freedcam.ui.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.troop.freedcam.R;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.i_camera.parameters.I_ManualParameter;

/**
 * Created by troop on 01.09.2014.
 */
public class ManualMenuItem extends LinearLayout implements View.OnClickListener, AbstractManualParameter.I_ParameterEvent
{
    Context context;
    TextView textView;
    private ToggleButton toggleButton;
    public final String name;
    ManualMenuHandler manualMenuHandler;
    public AbstractManualParameter manualParameter;

    public ManualMenuItem(Context context, String name, ManualMenuHandler manualMenuHandler, AbstractManualParameter parameter) {
        super(context);
        this.context =context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.toggle_control, this);
        textView = (TextView)findViewById(R.id.toggle_textView);
        this.name = name;
        this.manualMenuHandler = manualMenuHandler;
        this.manualParameter = parameter;
        textView.setText(name);
        toggleButton = (ToggleButton)findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        manualMenuHandler.DisableOtherItems(name);
    }

    public void DisableItem()
    {
        toggleButton.setChecked(false);
    }

    public void EnableItem() {toggleButton.setChecked(true);}

    @Override
    public void onIsSupportedChanged(boolean value) {

    }

    @Override
    public void onMaxValueChanged(int max) {

    }

    @Override
    public void onMinValueChanged(int min) {

    }

    @Override
    public void onCurrentValueChanged(int current) {

    }
}
