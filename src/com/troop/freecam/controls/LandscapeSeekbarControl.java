package com.troop.freecam.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.enums.E_ManualSeekbar;
import com.troop.freecam.interfaces.ILandscapeSeekbar;

/**
 * Created by troop on 11.08.2014.
 */
public class LandscapeSeekbarControl extends LinearLayout implements SeekBar.OnSeekBarChangeListener, ILandscapeSeekbar
{
    protected TextView textView_currentValue;
    protected SeekBar seekBar;

    protected int min;
    protected int max;
    protected int current;
    protected CameraManager cameraManager;
    protected E_ManualSeekbar e_manualSeekbar;

    public LandscapeSeekbarControl(Context context) {
        super(context);
    }

    public LandscapeSeekbarControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LandscapeSeekbarControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.landscape_seekbar_control, this);
        textView_currentValue = (TextView)findViewById(R.id.landscape_seekbar_textview);
        seekBar = (SeekBar)findViewById(R.id.landscape_seekBar);
        seekBar.setOnSeekBarChangeListener(this);
    }

    public E_ManualSeekbar GetManualSeekBarEnum()
    {
        return e_manualSeekbar;
    }

    public void SetCameraManager(CameraManager cameraManager)
    {
        this.cameraManager = cameraManager;
    }

    public void SetMinMaxValues(int min, int max)
    {
        this.min = min;
        this.max = max;
        seekBar.setMax(max);

    }

    public void SetCurrentValue(int current)
    {
        this.current = current;
        seekBar.setProgress(current);
    }

    public int GetCurrentValue()
    {
        return current;
    }

    public void SetText(String text)
    {
        textView_currentValue.setText(text);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
