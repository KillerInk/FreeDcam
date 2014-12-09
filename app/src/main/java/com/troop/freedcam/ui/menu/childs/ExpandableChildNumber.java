package com.troop.freedcam.ui.menu.childs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;

import java.util.ArrayList;

/**
 * Created by troop on 02.12.2014.
 */
public class ExpandableChildNumber extends ExpandableChild implements I_VideoProfile
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

    public ExpandableChildNumber(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableChildNumber(Context context) {
        super(context);
    }

    public ExpandableChildNumber(Context context, AppSettingsManager appSettingsManager,String settingsname, ArrayList<String> modulesToShow, AbstractCameraUiWrapper cameraUiWrapper)
    {
        super(context);
        this.appSettingsManager = appSettingsManager;
        this.settingsname = settingsname;
        this.modulesToShow = modulesToShow;
        this.cameraUiWrapper = cameraUiWrapper;
        initt(context);

    }

    public ExpandableChildNumber(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init(Context context) {

    }

    private void initt(Context context)
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
            this.setVisibility(VISIBLE);
        else
            this.setVisibility(GONE);


    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void setName(String Name) {
        super.setName(Name);
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
        return null;
    }

    @Override
    public void setParameterHolder(I_ModeParameter parameterHolder, AppSettingsManager appSettingsManager, String settingsname, ArrayList<String> modulesToShow, AbstractCameraUiWrapper cameraUiWrapper) {

    }

    @Override
    public void AddModulesToShow(ArrayList<String> modulesToShow) {
        super.AddModulesToShow(modulesToShow);
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
