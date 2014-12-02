package com.troop.freedcam.ui.menu.childs;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.parameters.modes.I_ModeParameter;
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

    double min;
    double max;
    double current;
    I_VideoProfile videoProfile;

    public ExpandableChildNumber(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableChildNumber(Context context) {
        super(context);
    }

    public ExpandableChildNumber(Context context, AppSettingsManager appSettingsManager,String settingsname, ArrayList<String> modulesToShow, CameraUiWrapper cameraUiWrapper)
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
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                double i = 0;
                try
                {
                    i = Double.parseDouble(s.toString());
                }
                catch (Exception ex)
                {}
                if (i > 0 && i <= 30 );
                    current = i;
                
            }
        });
        this.plus.setClickable(true);
        this.minus.setClickable(true);

        //this.setClickable(false);

        minus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current > min)
                {
                    current--;
                    editText.setText(current + "");
                }
            }
        });
        plus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current < max)
                {
                    current++;
                    editText.setText(current +"");
                }
            }
        });

        String fps = this.appSettingsManager.getString(settingsname);
        if (fps == null || fps.equals(""))
            fps = "30";
        editText.setText(fps);
        current = Double.parseDouble(fps);

        this.modulesToShow = new ArrayList<String>();
    }

    public void setMinMax(double min, double max)
    {
        this.min = min;
        this.max = max;
    }

    public void setCurrent(double current)
    {
        this.current = current;
        editText.setText(current +"");
    }

    public double GetCurrent()
    {
        return current;
    }

    @Override
    public void VideoProfileChanged(String videoProfile) {

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
    public void setParameterHolder(I_ModeParameter parameterHolder, AppSettingsManager appSettingsManager, String settingsname, ArrayList<String> modulesToShow, CameraUiWrapper cameraUiWrapper) {

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
