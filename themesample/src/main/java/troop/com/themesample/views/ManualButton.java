package troop.com.themesample.views;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.filelogger.Logger;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.sonyapi.parameters.manual.BaseManualParameterSony;
import com.troop.freedcam.ui.AppSettingsManager;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import troop.com.themesample.R;

/**
 * Created by troop on 08.12.2015.
 */
public class ManualButton extends LinearLayout implements AbstractManualParameter.I_ManualParameterEvent
{

    private final String TAG = ManualButton.class.getSimpleName();
    private String[] parameterValues;
    private AbstractManualParameter parameter;
    private String settingsname;
    private TextView headerTextView;
    private TextView valueTextView;
    private ImageView imageView;
    private Handler handler;
    private final int backgroundColorActive = Color.parseColor("#46FFFFFF");
    private final int backgroundColor = Color.parseColor("#00000000");
    private final int stringColor = Color.parseColor("#FFFFFFFF");
    private final int stringColorActive = Color.parseColor("#FF000000");
    private boolean imageusing = false;
    private int pos = 0;

    private final BlockingQueue<Integer> valueQueue = new ArrayBlockingQueue<>(3);

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
      /*  imageView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(appSettingsManager.context, "Reseted", Toast.LENGTH_SHORT).show();
                return true;
            }
        });*/
    }

    public void RemoveParameterListner( AbstractManualParameter.I_ManualParameterEvent t)
    {
        parameter.removeEventListner(t);
    }

    public void SetParameterListner( AbstractManualParameter.I_ManualParameterEvent t)
    {
        parameter.addEventListner(t);
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

                onIsSupportedChanged(parameter.IsVisible());
                onIsSetSupportedChanged(parameter.IsSetSupported());
                createStringParametersStrings(parameter);

            }
            else
                onIsSupportedChanged(false);
        }
        else
            onIsSupportedChanged(false);

    }

    private void createStringParametersStrings(AbstractManualParameter parameter) {
        parameterValues = parameter.getStringValues();

        /*if (parameterValues == null && realMax > 0)
        {
            ArrayList<String> list = new ArrayList<>();
            for (int i = realMin; i<= realMax; i++)
            {
                list.add(i+"");
            }
            parameterValues = new String[list.size()];
            list.toArray(parameterValues);
        }*/
    }

    public void SetStuff(String settingsName)
    {

        this.settingsname = settingsName;
    }

    @Override
    public void onIsSupportedChanged(final boolean value) {
        this.post(new Runnable() {
            @Override
            public void run() {
                final String txt = headerTextView.getText().toString();
                Logger.d(txt, "isSupported:" + value);
                if (value) {
                    ManualButton.this.setVisibility(VISIBLE);
                    ManualButton.this.animate().setListener(null).scaleX(1f).setDuration(300);
                }
                else
                {
                    ManualButton.this.animate().setListener(hideListner).scaleX(0f).setDuration(300);
                }
            }
        });
    }

    private Animator.AnimatorListener hideListner = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            ManualButton.this.setVisibility(GONE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    @Override
    public void onIsSetSupportedChanged(final boolean value) {
        post(new Runnable() {
            @Override
            public void run() {
                if (value) {
                    ManualButton.this.setEnabled(true);
                    imageView.getDrawable().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
                } else {
                    ManualButton.this.setEnabled(false);
                    imageView.getDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                }
            }
        });
    }


    @Override
    public void onCurrentValueChanged(int current)
    {

        this.pos = current;

        Logger.d(TAG, "onCurrentValueChanged current:"+current +" pos:" +pos);
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
                Logger.d(TAG, "setTextValue:" + valueTextView.getText());
            }
        });

    }

    private String getStringValue(int pos)
    {
        if (parameterValues != null && parameterValues.length > 0)
        {
            if (pos >= parameterValues.length)
                return parameterValues[parameterValues.length-1];
            else if (pos < 0)
                return parameterValues[0];
            else
                return parameterValues[pos];
        }

        return null;
    }

    public String[] getStringValues()
    {
        if (parameterValues == null)
            createStringParametersStrings(parameter);
        return parameterValues;
    }

    public int getCurrentItem()
    {
        return parameter.GetValue();
    }

    private boolean currentlysettingsparameter = false;
    public void setValueToParameters(final int value)
    {
        if (valueQueue.size() == 3)
            valueQueue.remove();
        Logger.d(TAG, "add to queue:" + value);
        valueQueue.add(value);

        handler.post(new Runnable() {
            @Override
            public void run() {
                //setparameter();
                while (valueQueue.size() >= 1) {
                    setparameter();

                }

            }
        });


    }

    private void setparameter()
    {
        currentlysettingsparameter = true;
        int runValue = 0;
        try {
            runValue = valueQueue.take();

        } catch (InterruptedException e) {
            Logger.exception(e);
            currentlysettingsparameter = false;
        }
        pos = runValue;
        if (runValue < 0 || runValue > parameterValues.length -1)
            return;
        parameter.SetValue(runValue);
        if (!(parameter instanceof BaseManualParameterSony) && settingsname != null) {
            AppSettingsManager.APPSETTINGSMANAGER.setString(settingsname, runValue + "");
        }
        currentlysettingsparameter = false;
    }

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
