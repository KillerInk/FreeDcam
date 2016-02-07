package troop.com.themesample.views.uichilds;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
 * Created by troop on 11.06.2015.
 */
public class UiSettingsChild extends LinearLayout implements I_ModuleEvent, AbstractModeParameter.I_ModeParameterEvent ,I_ParametersLoaded ,View.OnClickListener
{
    protected Context context;
    private String headerText;
    protected LinearLayout laybg;
    protected TextView valueText;
    protected AbstractModeParameter parameter;
    protected I_Activity i_activity;
    protected String TAG;
    protected AppSettingsManager appSettingsManager;
    protected String settingsname;
    protected Interfaces.I_MenuItemClick onItemClick;
    final protected boolean logging =false;
    private boolean fromleft = false;

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

            headerText = String.valueOf(a.getText(R.styleable.UiSettingsChild_HeaderText));

            valueText.setText(a.getText(R.styleable.UiSettingsChild_ValueText));
        }
        finally {
            a.recycle();
        }
        sendLog("Ctor done");
    }

    protected void sendLog(String log)
    {
        if (logging)
            Log.d(TAG,log);
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

        //headerText = (TextView)findViewById(R.id.textView);
        //headerText.setSelected(true);

        laybg = (LinearLayout)findViewById(R.id.LAYbg);
       // laybg.setBackgroundDrawable(switchICOn(headerText));

        if(context.getResources().getString(R.string.uisetting_wb_header) == headerText)
        {
            laybg.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.quck_set_wb));
        }

        valueText = (TextView)findViewById(R.id.textView2);
        valueText.setSelected(true);
        this.setOnClickListener(this);

    }

    private Drawable switchICOn(String param)
    {
        Drawable CurrentICon;

        switch (param)
        {
            case "WB":
                //CurrentICon = context.getDrawable(R.drawable.quck_set_focus);
                break;

        }

        return context.getResources().getDrawable(R.drawable.quck_set_focus);

    }

    protected void inflateTheme(LayoutInflater inflater)
    {
        inflater.inflate(R.layout.ui_settingschild, this);
    }

    public void SetMenuItemListner(Interfaces.I_MenuItemClick menuItemClick, boolean fromleft)
    {
        this.onItemClick = menuItemClick;
        this.fromleft = fromleft;
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
            sendLog("Paramters is null or Unsupported");
            if (parameter != null) {
                parameter.addEventListner(this);
                this.parameter = parameter;
            }
            return;
        }
        else
        {
            onIsSupportedChanged(parameter.IsVisible());
            if (parameter != null) {
                parameter.addEventListner(this);
                this.parameter = parameter;
            }
        }


        setTextToTextBox(parameter);
    }

    public AbstractModeParameter GetParameter()
    {
        return parameter;
    }

    public void setTextToTextBox(AbstractModeParameter parameter)
    {
        if (parameter != null && parameter.IsSupported())
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
        sendLog("Set Value to:" + val);
        valueText.setText(val);
    }

    @Override
    public void onIsSupportedChanged(boolean isSupported)
    {
        sendLog("isSupported:" + isSupported);
        if (isSupported) {
            this.setVisibility(VISIBLE);
            this.animate().setListener(null).scaleY(1f).setDuration(300);
        }
        else
            this.animate().setListener(hideListner).scaleY(0f).setDuration(300);
    }

    private Animator.AnimatorListener hideListner = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            UiSettingsChild.this.setVisibility(GONE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    @Override
    public void onIsSetSupportedChanged(boolean isSupported)
    {
        sendLog("isSetSupported:" + isSupported);
        this.setEnabled(isSupported);
    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public void onVisibilityChanged(boolean visible) {

    }

    @Override
    public String ModuleChanged(String module) {
        return null;
    }

    @Override
    public void ParametersLoaded()
    {
        sendLog("Parameters Loaded");
        if (parameter != null && parameter.IsSupported()) {
            setTextToTextBox(parameter);
            onIsSupportedChanged(true);
        }
        else
            onIsSupportedChanged(false);
    }

    @Override
    public void onClick(View v) {
        if (onItemClick != null)
            onItemClick.onMenuItemClick(this, fromleft);
    }
}
