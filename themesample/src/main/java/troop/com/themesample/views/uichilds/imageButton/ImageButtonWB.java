package troop.com.themesample.views.uichilds.imageButton;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;

import troop.com.themesample.R;
import troop.com.themesample.subfragments.Interfaces;

/**
 * Created by GeorgeKiarie on 12/12/2015.
 */
public class ImageButtonWB extends LinearLayout implements I_ModuleEvent, AbstractModeParameter.I_ModeParameterEvent ,I_ParametersLoaded,View.OnClickListener
{
    protected Context context;
    //protected TextView headerText;
    //protected TextView valueText;

    protected ImageButton paramV;
    protected AbstractModeParameter parameter;
    protected I_Activity i_activity;
    protected String TAG;
    protected AppSettingsManager appSettingsManager;
    protected String settingsname;
    protected Interfaces.I_MenuItemClick onItemClick;

    public ImageButtonWB(Context context) {
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

    public ImageButtonWB(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        init(context);


    }

    public ImageButtonWB(Context context, AttributeSet attrs, int defStyleAttr) {
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
        paramV = (ImageButton)findViewById(R.id.imgBTN);

        //headerText = (TextView)findViewById(R.id.textView);
        //headerText.setSelected(true);
        //valueText = (TextView)findViewById(R.id.textView2);
        //valueText.setSelected(true);
        this.setOnClickListener(this);

    }

    protected void inflateTheme(LayoutInflater inflater)
    {
        inflater.inflate(R.layout.image_button, this);
    }

    public void SetMenuItemListner(Interfaces.I_MenuItemClick menuItemClick)
    {
        this.onItemClick = menuItemClick;
    }

    public void SetParameter(AbstractModeParameter parameter)
    {
        if (parameter == null || !parameter.IsSupported())
        {
            onIsSupportedChanged(false);
            Log.d(TAG, "Paramters is null or Unsupported");
            if (parameter != null) {
                parameter.addEventListner(this);
                this.parameter = parameter;
            }
            return;
        }
        else {
            onIsSupportedChanged(true);
            if (parameter != null) {
                parameter.addEventListner(this);
                this.parameter = parameter;
            }
        }


        setTextToTextBox(parameter);
    }

    public void setTextToTextBox(AbstractModeParameter parameter)
    {
        if (parameter.IsSupported())
        {
            onIsSupportedChanged(true);
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
            if (settingsname != null && !settingsname.equals(""))
                appSettingsManager.setString(settingsname, value);
            try {
                parameter.SetValue(value, true);
            }
            catch (NullPointerException ex)
            {
                ex.printStackTrace();
            }
            onValueChanged(value);
        }
    }

    //AbstractModeParameter.I_ModeParameterEvent implementation
    @Override
    public void onValueChanged(String val)
    {
        Log.d(TAG, "Set Value to:" + val);
        //valueText.setText(val);
        //paramV.setBackground();
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
        if (parameter != null && parameter.IsSupported()) {
            setTextToTextBox(parameter);
            onIsSupportedChanged(true);
        }
        else
            onIsSupportedChanged(false);
    }

    @Override
    public void onClick(View v) {

       // if (onItemClick != null)
        //    onItemClick.onMenuItemClick(this, false);
    }
}