package troop.com.themesample.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;

import troop.com.themesample.R;

/**
 * Created by troop on 11.06.2015.
 */
public class UiSettingsChild extends LinearLayout implements I_ModuleEvent, AbstractModeParameter.I_ModeParameterEvent
{
    Context context;
    TextView headerText;
    TextView valueText;
    AbstractModeParameter parameter;
    public UiSettingsChild(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public UiSettingsChild(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.UiSettingsChild,
                0, 0
        );

        try {
            headerText.setText(a.getText(R.styleable.UiSettingsChild_HeaderText));
            valueText.setText(a.getText(R.styleable.UiSettingsChild_ValueText));
        }
        finally {
            a.recycle();
        }

    }

    public UiSettingsChild(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context);
    }

    private void init(Context context)
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


    public void SetParameter( AbstractModeParameter parameter)
    {
        if (parameter == null)
            return;
        this.parameter = parameter;
        parameter.addEventListner(this);
    }

    //AbstractModeParameter.I_ModeParameterEvent implementation
    @Override
    public void onValueChanged(String val)
    {
        valueText.setText(val);
    }

    @Override
    public void onIsSupportedChanged(boolean isSupported)
    {
        if (isSupported)
            this.setVisibility(VISIBLE);
        else
            this.setVisibility(GONE);
    }

    @Override
    public void onIsSetSupportedChanged(boolean isSupported)
    {
        this.setEnabled(isSupported);


    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public String ModuleChanged(String module) {
        return null;
    }
}
