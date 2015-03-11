package com.troop.freedcam.ui.menu.childs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.parameters.modes.SimpleModeParameter;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.ExpandableGroup;

import java.util.ArrayList;

/**
 * Created by troop on 02.12.2014.
 */
public class ExpandableChildTimelapseFps extends ExpandableChild implements I_VideoProfile
{

    Button plus;
    Button minus;
    EditText editText;

    float min;
    float max;
    float current;
    final float mover = 0.1f;
    final float bigmover = 1;
    I_VideoProfile videoProfile;
    SimpleModeParameter parameterHolder;


    public ExpandableChildTimelapseFps(Context context, ExpandableGroup group, AppSettingsManager appSettingsManager, String name, String settingsname)
    {
        super(context,group,name, appSettingsManager, settingsname);
        initt(context);

    }

    @Override
    protected void init(Context context) {

    }

    protected
    void initt(Context context)
    {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expandable_childs_number, this);
        this.plus = (Button)findViewById(R.id.button_plus);
        this.minus = (Button)findViewById(R.id.button_minus);
        this.editText = (EditText)findViewById(R.id.editText_number);
        this.plus.setClickable(true);
        this.minus.setClickable(true);

        //this.setClickable(false);

        minus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((current - bigmover) >= 1 )
                    current -= bigmover;
                else if (current - mover > min)
                    current -= mover;
                setCurrent(current);
            }
        });
        plus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (current >= 1 && current + bigmover <= max)
                    current += bigmover;
                else if (current + mover <= 1)
                {
                    current += mover;
                }
                setCurrent(current);

            }
        });

        String fps = this.appSettingsManager.getString(settingsname);
        if (fps == null || fps.equals(""))
            fps = "30";
        editText.setText(fps + " fps");
        current = Float.parseFloat(fps);

        this.modulesToShow = new ArrayList<String>();
    }

    public void setMinMax(float min, float max)
    {
        this.min = min;
        this.max = max;
    }

    public void setCurrent(float current)
    {
        String form = String.format("%.1f", current).replace(",", ".");
        try {

            current = Float.parseFloat(form);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        appSettingsManager.setString(settingsname, current+"");
        editText.setText(current + " fps");
    }

    public double GetCurrent()
    {
        return current;
    }

    @Override
    public void VideoProfileChanged(String videoProfile)
    {
        if (videoProfile.contains("Timelapse"))
        {
            if (!isVisible)
            {
                isVisible =true;
                group.submenu.addView(this);
                parameterHolder.setIsSupported(true);
            }
        }
        else
        {
            if (isVisible)
            {
                isVisible = false;
                group.getItems().remove(this);
                parameterHolder.setIsSupported(false);
            }

        }
        //reload this way subitems
        group.ModuleChanged("");
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String Value() {
        return null;
    }

    @Override
    public void setValue(String value) {

    }

    @Override
    public I_ModeParameter getParameterHolder() {
        return super.getParameterHolder();
    }

    @Override
    public void setParameterHolder(AbstractModeParameter parameterHolder, ArrayList<String> modulesToShow)
    {
        super.setParameterHolder(parameterHolder, modulesToShow);
        if (parameterHolder instanceof SimpleModeParameter)
            this.parameterHolder = (SimpleModeParameter)parameterHolder;
    }

    @Override
    public String ModuleChanged(String module) {
        return super.ModuleChanged(module);
    }

    @Override
    protected String getTAG() {
        return super.getTAG();
    }




}
