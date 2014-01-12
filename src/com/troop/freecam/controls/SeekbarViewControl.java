package com.troop.freecam.controls;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.fragments.BaseFragment;
import com.troop.freecam.manager.ManualSaturationManager;

/**
 * Created by troop on 02.01.14.
 */
public class SeekbarViewControl extends LinearLayout
{
    public TableRow exposureRow;
    public SeekBar exposureSeekbar;
    public TextView exposureTextView;

    public TableRow sharpnessRow;
    public SeekBar sharpnessSeekBar;
    public TextView sharpnessTextView;

    public  TextView contrastTextView;
    public TableRow contrastRow;
    public SeekBar contrastSeekBar;

    public TextView brightnessTextView;
    public SeekBar brightnessSeekBar;
    public TableRow brightnessRow;

    public TextView saturationTextView;
    public SeekBar saturationSeekBar;
    public TableRow saturationRow;

    public TableRow focusRow;
    public SeekBar focusSeekBar;

    CameraManager camMan;
    MainActivity activity;

    /*public SeekbarViewControl(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.seekbarviewfragment, container, false);
        init();
        return view;
    }*/

    public SeekbarViewControl(Context context) {
        super(context);
    }

    public SeekbarViewControl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeekbarViewControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void SetCameraManger(CameraManager camMan, MainActivity activity)
    {
        this.camMan = camMan;
        this.activity = activity;
        init();
    }

    private void init()
    {
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.seekbarviewfragment, this);

        exposureTextView = (TextView) findViewById(R.id.textViewexposure);
        exposureSeekbar  = (SeekBar)  findViewById(R.id.seekBar_exposure);
        //exposureSeekbar.setProgress(camMan.parametersManager.getParameters().getMaxExposureCompensation() - camMan.parametersManager.getParameters().getMinExposureCompensation());
        exposureSeekbar.setOnSeekBarChangeListener(camMan.manualExposureManager);
        exposureRow = (TableRow)  findViewById(R.id.tableRowExposure);
        exposureRow.setVisibility(View.GONE);

        sharpnessTextView = (TextView) findViewById(R.id.textView_sharpness);
        sharpnessSeekBar = (SeekBar) findViewById(R.id.seekBar_sharpness);
        sharpnessSeekBar.setProgress(100);
        sharpnessSeekBar.setOnSeekBarChangeListener(camMan.manualSharpnessManager);
        sharpnessRow = (TableRow)  findViewById(R.id.tableRowSharpness);
        sharpnessRow.setVisibility(View.GONE);

        focusRow = (TableRow) findViewById(R.id.tableRowFocus);
        focusSeekBar = (SeekBar) findViewById(R.id.seekBarFocus);
        focusSeekBar.setMax(60);
        //TODO something wrong here?
        brightnessTextView = (TextView)( findViewById(R.id.textViewFocus));
        focusSeekBar.setOnSeekBarChangeListener(camMan.manualFocus);
        focusRow.setVisibility(View.GONE);

        contrastRow = (TableRow) findViewById(R.id.tableRowContrast);
        contrastSeekBar = (SeekBar)  findViewById(R.id.seekBar_contrast);
        contrastSeekBar.setProgress(100);
        contrastSeekBar.setOnSeekBarChangeListener(camMan.manualContrastManager);
        contrastTextView = (TextView)  findViewById(R.id.textView_contrast);
        contrastRow.setVisibility(View.GONE);

        //TODO setProgress not added
        brightnessRow = (TableRow) findViewById(R.id.tableRowBrightness);
        brightnessSeekBar = (SeekBar) findViewById(R.id.seekBar_brightness);
        brightnessTextView = (TextView)findViewById(R.id.textView_brightness);
        brightnessSeekBar.setOnSeekBarChangeListener(camMan.manualBrightnessManager);
        brightnessRow.setVisibility(View.GONE);

        saturationRow = (TableRow) findViewById(R.id.tableRowsaturation);
        saturationTextView = (TextView) findViewById(R.id.textViewSaturation);
        saturationSeekBar = (SeekBar) findViewById(R.id.seekBarSaturation);
        saturationSeekBar.setProgress(100);
        saturationSeekBar.setOnSeekBarChangeListener(new ManualSaturationManager(camMan));
        saturationRow.setVisibility(View.GONE);
    }

    public void UpdateValues(boolean initValues)
    {
        if (initValues)
            setBaseValues();
        updateValues();
    }

    private void updateValues()
    {
        exposureTextView.setText("Exposure: " + camMan.parametersManager.getParameters().getExposureCompensation());

        if (camMan.parametersManager.getSupportSharpness())
        {
            sharpnessTextView.setText("Sharpness: " + camMan.parametersManager.getParameters().getInt("sharpness"));
        }
        if (camMan.parametersManager.getSupportContrast())
        {
            contrastTextView.setText("Contrast: " + camMan.parametersManager.getParameters().get("contrast"));
        }
        //Brightness
        if (camMan.parametersManager.getSupportBrightness())
        {
            brightnessTextView.setText("Brightness: " + camMan.parametersManager.Brightness.Get());
        }
        if (camMan.parametersManager.getSupportSaturation())
        {
            saturationTextView.setText("Saturation: " + camMan.parametersManager.getParameters().get("saturation"));
        }
    }

    private void setBaseValues()
    {
        int min = camMan.parametersManager.getParameters().getMinExposureCompensation();
        if (min < 0)
            min *= -1;
        int max = camMan.parametersManager.getParameters().getMaxExposureCompensation() + min;
        exposureSeekbar.setMax(max);
        camMan.manualExposureManager.ExternalSet = true;
        exposureSeekbar.setProgress(camMan.parametersManager.getParameters().getMaxExposureCompensation());
        camMan.manualExposureManager.SetMinMax(camMan.parametersManager.getParameters().getMinExposureCompensation(), camMan.parametersManager.getParameters().getMaxExposureCompensation());
        //camMan.manualExposureManager.ExternalSet = true;
        //camMan.manualExposureManager.SetCurrentValue(camMan.parametersManager.getParameters().getExposureCompensation());

        if (camMan.parametersManager.getSupportSharpness())
        {
            sharpnessSeekBar.setMax(180);
            sharpnessSeekBar.setProgress(camMan.parametersManager.getParameters().getInt("sharpness"));
        }

        //Contrast
        if (camMan.parametersManager.getSupportContrast())
        {
            contrastSeekBar.setMax(180);
            camMan.manualContrastManager.ExternalSet = true;
            contrastSeekBar.setProgress(camMan.parametersManager.getParameters().getInt("contrast"));
        }
        //Brightness
        if (camMan.parametersManager.getSupportBrightness())
        {
            brightnessSeekBar.setMax(100);
            brightnessSeekBar.setProgress(camMan.parametersManager.Brightness.Get());
        }
        //Saturation
        if (camMan.parametersManager.getSupportSaturation())
        {
            saturationSeekBar.setMax(180);
            saturationSeekBar.setProgress(camMan.parametersManager.getParameters().getInt("saturation"));
        }
    }
}
