package com.troop.freecam.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.interfaces.INumericUpDownValueCHanged;

/**
 * Created by troop on 20.01.14.
 */
public class NumericUpDownControl extends LinearLayout
{
    Button minus;
    Button plus;
    EditText text;
    int min;
    int max;
    int current;

    private INumericUpDownValueCHanged valueCHanged;
    public void setOnValueCHanged(INumericUpDownValueCHanged valueCHanged){ this.valueCHanged = valueCHanged;}

    private void valuehasChanged()
    {
        if (valueCHanged != null)
            valueCHanged.ValueHasCHanged(current);
    }

    public NumericUpDownControl(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.numeric_updown_control, this);
    }

    public NumericUpDownControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.numeric_updown_control, this);
        init();
    }

    public NumericUpDownControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init()
    {
        minus = (Button)findViewById(R.id.button_minus);
        plus = (Button)findViewById(R.id.button_plus);
        text = (EditText)findViewById(R.id.editText_number);
        minus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current > min)
                {
                    current--;
                    text.setText(current + "");
                    valuehasChanged();
                }
            }
        });
        plus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current < max)
                {
                    current++;
                    text.setText(current +"");
                    valuehasChanged();
                }
            }
        });
    }

    public void setMinMax(int min, int max)
    {
        this.min = min;
        this.max = max;
    }

    public void setCurrent(int current)
    {
        this.current = current;
        text.setText(current +"");
    }

    public int GetCurrent()
    {
        return current;
    }
}
