package troop.com.themesample.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.ui.AppSettingsManager;

import troop.com.themesample.R;

/**
 * Created by Ingo on 24.07.2015.
 */
public class ManualItem extends LinearLayout implements AbstractManualParameter.I_ManualParameterEvent, SeekBar.OnSeekBarChangeListener
{
    AbstractManualParameter parameter;
    AppSettingsManager appSettingsManager;
    String settingsname;
    VerticalSeekBar seekBar;
    TextView headerTextView;
    TextView valueTextView;

    String[] parameterValues;

    int realMin;
    int realMax;
    boolean userIsSeeking= false;

    HandlerThread thread;
    Handler handler;

    public ManualItem(Context context)
    {
        super(context);
        init(context);
    }

    public ManualItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ManualItem,
                0, 0
        );
        //try to set the attributs
        try
        {
            headerTextView.setText(a.getText(R.styleable.ManualItem_Header));

        }
        finally {
            a.recycle();
        }
    }

    public ManualItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.manual_item, this);
        this.seekBar = (VerticalSeekBar)findViewById(R.id.vertical_seekbar);
        seekBar.setOnSeekBarChangeListener(this);
        this.headerTextView = (TextView)findViewById(R.id.textView_mheader);
        this.valueTextView = (TextView)findViewById(R.id.textView_mvalue);

        thread = new HandlerThread("seekbarThread");
        thread.start();
        handler = new Handler(thread.getLooper());
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
                int min = parameter.GetMinValue();
                int max = parameter.GetMaxValue();
                if (max > 0)
                    setSeekbar_Min_Max(min, max);
                setSeekbarProgress(parameter.GetValue());
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
    public void onIsSupportedChanged(boolean value)
    {
        final String txt = headerTextView.getText().toString();
        Log.d(txt, "isSupported:" + value);
        if (value)
            this.setVisibility(VISIBLE);
        else
            this.setVisibility(GONE);
    }

    @Override
    public void onIsSetSupportedChanged(boolean value)
    {
        if (value) {
            this.setEnabled(true);
            seekBar.setVisibility(VISIBLE);
        }
        else {
            this.setEnabled(false);
            seekBar.setVisibility(GONE);
        }
    }

    @Override
    public void onMaxValueChanged(int max) {

    }

    @Override
    public void onMinValueChanged(int min) {

    }

    @Override
    public void onCurrentValueChanged(int current)
    {
        setTextValue(current);
        if (!userIsSeeking)
        {
            setSeekbarProgress(current);
        }
    }

    private void setTextValue(final int current)
    {
        valueTextView.post(new Runnable() {
            @Override
            public void run() {
                String txt = getStringValue(current);
                if (txt != null && !txt.equals(""))
                    valueTextView.setText(txt);
                else
                    valueTextView.setText(current+"");
            }
        });

    }

    @Override
    public void onValuesChanged(String[] values)
    {
        this.parameterValues = values;
    }

    @Override
    public void onCurrentStringValueChanged(String value)
    {
        this.valueTextView.setText(value);
    }


    public String getStringValue(int pos)
    {
        if(parameterValues == null)
            parameterValues = parameter.getStringValues();
        if (parameterValues != null && parameterValues.length > 0)
        {
            return parameterValues[pos-1];
        }
        else if (parameterValues == null)
            return parameter.GetStringValue();

        return null;
    }
    //##################################
    //              SEEKBAR
    //##################################

    private void setValueToParameters(int value)
    {
        if (realMin < 0)
            parameter.SetValue(value + realMin);
        else
            parameter.SetValue(value);
    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress, boolean fromUser) {
        if (userIsSeeking && parameter != null)
        {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    setValueToParameters(progress);
                }
            });
            if (realMin < 0)
                setTextValue(progress + realMin);
            else
                setTextValue(progress);
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        userIsSeeking = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        userIsSeeking = false;
    }

    private void setSeekbarProgress(int value)
    {
        if (realMin < 0)
        {
            seekBar.setProgress(value - realMin);

        }
        else
        {
            seekBar.setProgress(value);
        }
    }

    private void setSeekbar_Min_Max(int min, int max)
    {
        realMin = min;
        realMax = max;
        if (min <0)
        {
            int m = max + min * -1;
            seekBar.setMax(m);
        }
        else
            seekBar.setMax(realMax);

    }
}
