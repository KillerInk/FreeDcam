package troop.com.themesample.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.sonyapi.parameters.manual.BaseManualParameterSony;
import com.troop.freedcam.ui.AppSettingsManager;

import java.util.ArrayList;

import troop.com.themesample.R;

/**
 * Created by troop on 08.12.2015.
 */
public class ManualButton extends LinearLayout implements AbstractManualParameter.I_ManualParameterEvent
{

    String[] parameterValues;
    AbstractManualParameter parameter;
    AppSettingsManager appSettingsManager;
    String settingsname;
    TextView headerTextView;
    TextView valueTextView;
    private ImageView imageView;
    Handler handler;
    int realMin;
    int realMax;
    final int backgroundColorActive = Color.parseColor("#46FFFFFF");
    final int backgroundColor = Color.parseColor("#00000000");
    final int stringColor = Color.parseColor("#FFFFFFFF");
    final int stringColorActive = Color.parseColor("#FF000000");
    boolean imageusing = false;

    public ManualButton(Context context) {
        super(context);
        init(context);
    }

    public ManualButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ManualButton,
                0, 0
        );
        //try to set the attributs
        try
        {
            headerTextView.setText(a.getText(R.styleable.ManualButton_Header));
            imageView.setImageDrawable(a.getDrawable(R.styleable.ManualButton_Image));
            if (imageView.getDrawable() != null) {
                headerTextView.setVisibility(GONE);
                this.imageusing = true;
            }

        }
        finally {
            a.recycle();
        }
    }

    public ManualButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        handler = new Handler();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.manualbutton, this);
        this.headerTextView = (TextView)findViewById(R.id.manualbutton_headertext);
        headerTextView.setSelected(true);
        this.valueTextView = (TextView)findViewById(R.id.manualbutton_valuetext);
        valueTextView.setSelected(true);
        imageView = (ImageView)findViewById(R.id.imageView_ManualButton);
    }

    public void SetAbstractManualParameter(AbstractManualParameter parameter)
    {
        this.parameter = parameter;
        if (parameter != null) {
            parameter.addEventListner(this);
            if (parameter.IsSupported())
            {
                String txt = parameter.GetStringValue();
                if (txt != null && !txt.equals(""))
                    valueTextView.setText(txt);
                else
                    valueTextView.setText(parameter.GetValue()+"");

                onIsSupportedChanged(parameter.IsSupported());
                onIsSetSupportedChanged(parameter.IsSetSupported());
                realMax = parameter.GetMaxValue();
                realMin = parameter.GetMinValue();
                parameterValues = parameter.getStringValues();
                if (parameterValues == null)
                {
                    ArrayList<String> list = new ArrayList<>();
                    for (int i = realMin; i<= realMax; i++)
                    {
                        list.add(i+"");
                    }
                    parameterValues = new String[list.size()];
                    list.toArray(parameterValues);
                }
            }
            else
                onIsSupportedChanged(false);
        }
        else
            onIsSupportedChanged(false);

    }

    public void SetStuff(AppSettingsManager appSettingsManager, String settingsName)
    {
        this.appSettingsManager = appSettingsManager;
        this.settingsname = settingsName;
    }

    @Override
    public void onIsSupportedChanged(final boolean value) {
        this.post(new Runnable() {
            @Override
            public void run() {
                final String txt = headerTextView.getText().toString();
                Log.d(txt, "isSupported:" + value);
                if (value)
                    ManualButton.this.setVisibility(VISIBLE);
                else
                    ManualButton.this.setVisibility(GONE);
            }
        });
    }

    @Override
    public void onIsSetSupportedChanged(final boolean value) {
        post(new Runnable() {
            @Override
            public void run() {
                if (value) {
                    ManualButton.this.setEnabled(true);

                } else {
                    ManualButton.this.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onMaxValueChanged(int max)
    {
        this.realMax = max;
    }

    @Override
    public void onMinValueChanged(int min) {
        this.realMin = min;
    }

    @Override
    public void onCurrentValueChanged(int current) {
        setTextValue(current);
    }

    @Override
    public void onValuesChanged(String[] values) {
        this.parameterValues = values;
    }

    @Override
    public void onCurrentStringValueChanged(final String value) {
        valueTextView.post(new Runnable() {
            @Override
            public void run() {
                valueTextView.setText(value);
            }
        });
    }

    private void setTextValue(final int current)
    {
        valueTextView.post(new Runnable() {
            @Override
            public void run() {
                String txt = getStringValue(current);
                if (txt != null && !txt.equals("") && !txt.equals("null"))
                    valueTextView.setText(txt);
                else
                    valueTextView.setText(current+"");
            }
        });

    }

    public String getStringValue(int pos)
    {
        if(parameterValues == null)
            parameterValues = parameter.getStringValues();
        if (parameterValues != null && parameterValues.length > 0)
        {
            if (pos > parameterValues.length -1)
                return parameterValues[parameterValues.length-1];
            else if (pos < 0)
                return parameterValues[0];
            else
                return parameterValues[pos];
        }
        else if (parameterValues == null)
            return parameter.GetStringValue();

        return null;
    }

    public String[] getStringValues()
    {
        return parameterValues;
    }

    public int getCurrentItem(){return  parameter.GetValue() + (realMin*-1);}

    boolean currentlysettingsparameter = false;
    public void setValueToParameters(final int value)
    {
        if (currentlysettingsparameter)
            return;
        handler.post(new Runnable() {
            @Override
            public void run()
            {
                currentlysettingsparameter = true;
                int runValue = value;
                if (!(parameter instanceof BaseManualParameterSony) && settingsname != null) {
                    if (realMin < 0)
                        appSettingsManager.setString(settingsname, (runValue + realMin) + "");
                    else
                        appSettingsManager.setString(settingsname, runValue + "");
                }
                if (realMin < 0)
                    runValue += realMin;
                if (runValue > realMax) {
                    Log.e(headerTextView.getText().toString(), "value bigger then max");
                    return;
                }
                if (runValue < realMin)
                    runValue = realMin;
                if (runValue > realMax)
                    runValue = realMax;
                parameter.SetValue(runValue);
                currentlysettingsparameter = false;
            }
        });

    }
    public int getRealMin() {return realMin; }

    public void SetActive(boolean active)
    {
        if (!imageusing) {
            if (active) {
                setBackgroundColor(backgroundColorActive);
                headerTextView.setTextColor(stringColorActive);
                valueTextView.setTextColor(stringColorActive);
            } else {
                setBackgroundColor(backgroundColor);
                headerTextView.setTextColor(stringColor);
                valueTextView.setTextColor(stringColor);
            }
        }
        else
        {
            if (active) {
                setBackgroundColor(backgroundColorActive);
            } else {
                setBackgroundColor(backgroundColor);
            }
        }
    }

}
