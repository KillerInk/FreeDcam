package com.troop.freecam.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.troop.freecam.R;
import com.troop.freecam.enums.E_ManualSeekbar;

/**
 * Created by troop on 13.08.2014.
 */
public class ToggleControl extends LinearLayout
{
    private E_ManualSeekbar seekbarValue;
    TextView textView;
    ToggleButton toggleButton;

    public ToggleControl(Context context) {
        super(context);
    }

    public ToggleControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ToggleControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.toggle_control, this);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ToggleControl,
                0, 0);
        String tmp = a.getString(R.styleable.ToggleControl_ToggleTextViewText);

        textView = (TextView)findViewById(R.id.toggle_textView);
        textView.setText(tmp);
        toggleButton = (ToggleButton)findViewById(R.id.toggleButton);
        a.recycle();
    }

    public E_ManualSeekbar GetSeekBarValue()
    {
        return seekbarValue;
    }

    public void SetSeekbarValue(E_ManualSeekbar seekbarValue)
    {
        this.seekbarValue = seekbarValue;
    }

    public boolean isChecked()
    {
        return toggleButton.isChecked();
    }
    public void setChecked(boolean value)
    {
        toggleButton.setChecked(value);
    }

    public void SetText(String text)
    {
        textView.setText(text);
    }

    public void setOnClickListener(OnClickListener onClickListener)
    {
        toggleButton.setOnClickListener(onClickListener);
    }
}
