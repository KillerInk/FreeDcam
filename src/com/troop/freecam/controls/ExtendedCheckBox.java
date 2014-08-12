package com.troop.freecam.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.troop.freecam.enums.E_ManualSeekbar;

/**
 * Created by troop on 12.08.2014.
 */
public class ExtendedCheckBox extends CheckBox
{
    private E_ManualSeekbar seekbarValue;
    public ExtendedCheckBox(Context context) {
        super(context);
    }

    public ExtendedCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public E_ManualSeekbar GetSeekBarValue()
    {
        return seekbarValue;
    }

    public void SetSeekbarValue(E_ManualSeekbar seekbarValue)
    {
        this.seekbarValue = seekbarValue;
    }
}
