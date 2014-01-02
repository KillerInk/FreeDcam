package com.troop.freecam.activitys;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.troop.freecam.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.manager.ManualSaturationManager;

/**
 * Created by troop on 02.01.14.
 */
public class SeekbarViewFragment extends  BaseFragment
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

    public SeekbarViewFragment(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.seekbarviewfragment, container, false);
        init();
        return view;
    }

    private void init()
    {
        exposureTextView = (TextView) view.findViewById(R.id.textViewexposure);
        exposureSeekbar  = (SeekBar)  view.findViewById(R.id.seekBar_exposure);
        exposureSeekbar.setProgress(30);
        exposureSeekbar.setOnSeekBarChangeListener(camMan.manualExposureManager);
        exposureRow = (TableRow)  view.findViewById(R.id.tableRowExposure);
        exposureRow.setVisibility(View.GONE);

        sharpnessTextView = (TextView) view.findViewById(R.id.textView_sharpness);
        sharpnessSeekBar = (SeekBar) view.findViewById(R.id.seekBar_sharpness);
        sharpnessSeekBar.setProgress(100);
        sharpnessSeekBar.setOnSeekBarChangeListener(camMan.manualSharpnessManager);
        sharpnessRow = (TableRow)  view.findViewById(R.id.tableRowSharpness);
        sharpnessRow.setVisibility(View.GONE);

        focusRow = (TableRow) view.findViewById(R.id.tableRowFocus);
        focusSeekBar = (SeekBar) view.findViewById(R.id.seekBarFocus);
        focusSeekBar.setMax(60);
        //TODO something wrong here?
        brightnessTextView = (TextView)( view.findViewById(R.id.textViewFocus));
        focusSeekBar.setOnSeekBarChangeListener(camMan.manualFocus);
        focusRow.setVisibility(View.GONE);

        contrastRow = (TableRow) view.findViewById(R.id.tableRowContrast);
        contrastSeekBar = (SeekBar)  view.findViewById(R.id.seekBar_contrast);
        contrastSeekBar.setProgress(100);
        contrastSeekBar.setOnSeekBarChangeListener(camMan.manualContrastManager);
        contrastTextView = (TextView)  view.findViewById(R.id.textView_contrast);
        contrastRow.setVisibility(View.GONE);

        //TODO setProgress not added
        brightnessRow = (TableRow) view.findViewById(R.id.tableRowBrightness);
        brightnessSeekBar = (SeekBar) view.findViewById(R.id.seekBar_brightness);
        brightnessTextView = (TextView)view.findViewById(R.id.textView_brightness);
        brightnessSeekBar.setOnSeekBarChangeListener(camMan.manualBrightnessManager);
        brightnessRow.setVisibility(View.GONE);

        saturationRow = (TableRow) view.findViewById(R.id.tableRowsaturation);
        saturationTextView = (TextView) view.findViewById(R.id.textViewSaturation);
        saturationSeekBar = (SeekBar) view.findViewById(R.id.seekBarSaturation);
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
        exposureSeekbar.setProgress(camMan.parametersManager.getParameters().getExposureCompensation() + camMan.parametersManager.getParameters().getMaxExposureCompensation());
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
