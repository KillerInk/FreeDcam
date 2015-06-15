package troop.com.themesample.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.i_camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;

import troop.com.themesample.R;

/**
 * Created by troop on 11.06.2015.
 */
public class UiSettingsChild extends LinearLayout implements I_ModuleEvent, AbstractModeParameter.I_ModeParameterEvent ,I_ParametersLoaded
{
    protected Context context;
    protected TextView headerText;
    protected TextView valueText;
    protected AbstractModeParameter parameter;
    protected I_Activity i_activity;
    protected String TAG;
    protected AppSettingsManager appSettingsManager;
    protected String settingsname;

    public UiSettingsChild(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public void SetStuff(I_Activity i_activity, AppSettingsManager appSettingsManager, String settingvalue)
    {
        this.i_activity = i_activity;
        this.appSettingsManager = appSettingsManager;
        this.settingsname = settingvalue;
    }

    public UiSettingsChild(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        init(context);
        //get custom attributs
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.UiSettingsChild,
                0, 0
        );
        //try to set the attributs
        try
        {
            TAG = (String)a.getText(R.styleable.UiSettingsChild_HeaderText);
            headerText.setText(a.getText(R.styleable.UiSettingsChild_HeaderText));
            valueText.setText(a.getText(R.styleable.UiSettingsChild_ValueText));
        }
        finally {
            a.recycle();
        }
        Log.d(TAG, "Ctor done");
    }

    public UiSettingsChild(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setEnabled(true);
        this.setClickable(true);
        this.context = context;
        init(context);
    }

    protected void init(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflateTheme(inflater);
        headerText = (TextView)findViewById(R.id.textView);
        valueText = (TextView)findViewById(R.id.textView2);
    }

    protected void inflateTheme(LayoutInflater inflater)
    {
        inflater.inflate(R.layout.ui_settingschild, this);
    }

    public void SetParameter(AbstractModeParameter parameter)
    {
        if (parameter == null || !parameter.IsSupported())
        {
            onIsSupportedChanged(false);
            Log.d(TAG, "Paramters is null or Unsupported");
            return;
        }
        else {
            onIsSupportedChanged(true);
        }
        this.parameter = parameter;
        if (parameter != null)
            parameter.addEventListner(this);
        setTextToTextBox(parameter);
    }

    public void setTextToTextBox(AbstractModeParameter parameter)
    {
        if (parameter.IsSupported())
        {
            String campara = parameter.GetValue();
            if (campara != null && !campara.equals(""))
                onValueChanged(campara);
            //onIsSupportedChanged(true);
        }
        else
            onIsSupportedChanged(false);
    }

    public String[] GetValues()
    {
        if (parameter != null && parameter.IsSupported())
            return parameter.GetValues();
        else return null;
    }

    public void SetValue(String value)
    {
        if (parameter != null && parameter.IsSupported())
        {
            parameter.SetValue(value, true);
            onValueChanged(value);
        }
    }

    //AbstractModeParameter.I_ModeParameterEvent implementation
    @Override
    public void onValueChanged(String val)
    {
        Log.d(TAG, "Set Value to:" + val);
        valueText.setText(val);
    }

    @Override
    public void onIsSupportedChanged(boolean isSupported)
    {
        Log.d(TAG, "isSupported:" + isSupported);
        if (isSupported)
            this.setVisibility(VISIBLE);
        else
            this.setVisibility(GONE);
    }

    @Override
    public void onIsSetSupportedChanged(boolean isSupported)
    {
        Log.d(TAG, "isSetSupported:" + isSupported);
        this.setEnabled(isSupported);
    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public String ModuleChanged(String module) {
        return null;
    }

    @Override
    public void ParametersLoaded()
    {
        Log.d(TAG, "Parameters Loaded");
        if (parameter != null && parameter.IsSupported())
            setTextToTextBox(parameter);
        else
            onIsSupportedChanged(false);
    }
}
