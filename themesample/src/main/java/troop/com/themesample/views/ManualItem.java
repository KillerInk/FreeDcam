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

import com.troop.freedcam.camera2.parameters.manual.BurstApi2;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.sonyapi.parameters.manual.BaseManualParameterSony;
import com.troop.freedcam.ui.AppSettingsManager;

import troop.com.themesample.R;
import troop.com.views.FreeVerticalSeekbar;

/**
 * Created by Ingo on 24.07.2015.
 */
public class ManualItem extends LinearLayout implements AbstractManualParameter.I_ManualParameterEvent, SeekBar.OnSeekBarChangeListener
{
    AbstractManualParameter parameter;
    AppSettingsManager appSettingsManager;
    String settingsname;
    FreeVerticalSeekbar seekBar;
    TextView headerTextView;
    TextView valueTextView;
    TextView minTextView;
    TextView maxTextView;


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
        this.seekBar = (FreeVerticalSeekbar)findViewById(R.id.vertical_seekbar);

        seekBar.setOnSeekBarChangeListener(this);
        this.headerTextView = (TextView)findViewById(R.id.textView_mheader);
        headerTextView.setSelected(true);
        this.valueTextView = (TextView)findViewById(R.id.textView_mvalue);
        valueTextView.setSelected(true);

        minTextView = (TextView)findViewById(R.id.textView_min);
        maxTextView = (TextView)findViewById(R.id.textView_max);

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
                realMax = parameter.GetMaxValue();
                realMin = parameter.GetMinValue();
                parameterValues = parameter.getStringValues();
                updateMinMaxTextViewAndSeekbarMax();
                setSeekbarProgress(parameter.GetValue());
            }
            else
                onIsSupportedChanged(false);
        }
        else
            onIsSupportedChanged(false);

    }

    private void updateMinMaxTextViewAndSeekbarMax()
    {
        if (parameterValues != null && parameterValues.length > 0)
        {
            minTextView.setText(parameterValues[0]);
            maxTextView.setText(parameterValues[parameterValues.length-1]);
            setSeekbar_Min_Max(0, parameterValues.length-1);
        }
        else
        {
            minTextView.setText(realMin+"");
            maxTextView.setText(realMax+"");
            setSeekbar_Min_Max(realMin, realMax);
        }
    }

    public void SetStuff(AppSettingsManager appSettingsManager, String settingsName)
    {
        this.appSettingsManager = appSettingsManager;
        this.settingsname = settingsName;
    }

    @Override
    public void onIsSupportedChanged(final boolean value)
    {
        this.post(new Runnable() {
            @Override
            public void run() {
                final String txt = headerTextView.getText().toString();
                Log.d(txt, "isSupported:" + value);
                if (value)
                    ManualItem.this.setVisibility(VISIBLE);
                else
                    ManualItem.this.setVisibility(GONE);
            }
        });

    }

    @Override
    public void onIsSetSupportedChanged(final boolean value)
    {
        post(new Runnable() {
            @Override
            public void run() {
                if (value) {
                    ManualItem.this.setEnabled(true);
                    seekBar.setVisibility(VISIBLE);
                    minTextView.setVisibility(VISIBLE);
                    maxTextView.setVisibility(VISIBLE);
                }
                else {
                    ManualItem.this.setEnabled(false);
                    seekBar.setVisibility(GONE);
                    minTextView.setVisibility(GONE);
                    maxTextView.setVisibility(GONE);
                }
            }
        });

    }

    @Override
    public void onMaxValueChanged(int max)
    {
        realMax = max;
        updateMinMaxTextViewAndSeekbarMax();
    }

    @Override
    public void onMinValueChanged(int min) {
        realMin = min;
        updateMinMaxTextViewAndSeekbarMax();
    }

    @Override
    public void onCurrentValueChanged(int current)
    {
        setTextValue(current);
        if (!userIsSeeking)
            setSeekbarProgress(current);
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

    @Override
    public void onValuesChanged(String[] values)
    {
        this.parameterValues = values;
        updateMinMaxTextViewAndSeekbarMax();
    }

    @Override
    public void onCurrentStringValueChanged(final String value)
    {
        valueTextView.post(new Runnable() {
            @Override
            public void run() {
                valueTextView.setText(value);
            }
        });
    }


    public String getStringValue(int pos)
    {
        if(parameterValues == null)
            parameterValues = parameter.getStringValues();
        if (parameterValues != null && parameterValues.length > 0)
        {
            if (pos > parameterValues.length)
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
    //##################################
    //              SEEKBAR
    //##################################

    private void setValueToParameters(final int value)
    {
        handler.post(new Runnable() {
            @Override
            public void run()
            {
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
            }
        });

    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress, boolean fromUser) {
        //Log.d(headerTextView.getText().toString(), "Seekbar onProgressChanged fromUser:" + userIsSeeking + "Progress:" + progress);
        if (userIsSeeking && parameter != null)
        {
            if (!(parameter instanceof BaseManualParameterSony) && !(parameter instanceof BurstApi2))
            {
                setValueToParameters(progress);
                if (realMin < 0)
                    setTextValue(progress + realMin);
                else
                    setTextValue(progress);
            }
            else
            {
                if (realMin < 0)
                    setTextValue(progress + realMin);
                else
                    setTextValue(progress);
            }
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        userIsSeeking = true;
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
        userIsSeeking = false;
        if (parameter instanceof BaseManualParameterSony || parameter instanceof BurstApi2)
            handler.post(new Runnable() {
                @Override
                public void run()
                {
                    setValueToParameters(ManualItem.this.seekBar.getProgress());
                }
            });
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
