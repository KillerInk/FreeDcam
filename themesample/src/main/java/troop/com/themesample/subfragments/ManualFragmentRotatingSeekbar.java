package troop.com.themesample.subfragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.i_camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.sonyapi.CameraUiWrapperSony;
import com.troop.freedcam.ui.AbstractFragment;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;

import troop.com.themesample.R;
import troop.com.themesample.views.ManualButton;
import troop.com.views.RotatingSeekbar;

/**
 * Created by troop on 08.12.2015.
 */
public class ManualFragmentRotatingSeekbar extends AbstractFragment implements I_ParametersLoaded, SeekBar.OnSeekBarChangeListener, AbstractManualParameter.I_ManualParameterEvent
{
    private int currentValuePos = 0;

    RotatingSeekbar seekbar;
    ManualButton mf;
    ManualButton iso;
    ManualButton shutter;
    ManualButton aperture;
    ManualButton exposure;
    ManualButton brightness;
    ManualButton burst;
    ManualButton wb;
    ManualButton contrast;
    ManualButton saturation;
    ManualButton sharpness;
    ManualButton programshift;
    ManualButton zoom;
    ManualButton skintone;
    ManualButton fx;
    ManualButton convergence;

    ManualButton currentButton;

    final String TAG = ManualFragmentRotatingSeekbar.class.getSimpleName();
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.manual_fragment_rotatingseekbar, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        seekbar = (RotatingSeekbar)view.findViewById(R.id.seekbar);
        seekbar.setOnSeekBarChangeListener(this);
        seekbar.setVisibility(View.GONE);

        mf = (ManualButton)view.findViewById(R.id.manual_mf);
        mf.SetStuff(appSettingsManager, AppSettingsManager.MF);
        mf.setOnClickListener(manualButtonClickListner);

        iso = (ManualButton)view.findViewById(R.id.manual_iso);
        iso.SetStuff(appSettingsManager, AppSettingsManager.MISO);
        iso.setOnClickListener(manualButtonClickListner);

        shutter = (ManualButton)view.findViewById(R.id.manual_shutter);
        shutter.SetStuff(appSettingsManager, AppSettingsManager.MSHUTTERSPEED);
        shutter.setOnClickListener(manualButtonClickListner);

        aperture = (ManualButton)view.findViewById(R.id.manual_aperture);
        aperture.SetStuff(appSettingsManager, "");
        aperture.setOnClickListener(manualButtonClickListner);

        exposure = (ManualButton)view.findViewById(R.id.manual_exposure);
        exposure.SetStuff(appSettingsManager, AppSettingsManager.MEXPOSURE);
        exposure.setOnClickListener(manualButtonClickListner);

        brightness = (ManualButton)view.findViewById(R.id.manual_brightness);
        brightness.SetStuff(appSettingsManager, AppSettingsManager.MBRIGHTNESS);
        brightness.setOnClickListener(manualButtonClickListner);

        burst = (ManualButton)view.findViewById(R.id.manual_burst);
        burst.SetStuff(appSettingsManager, "");
        burst.setOnClickListener(manualButtonClickListner);

        wb = (ManualButton)view.findViewById(R.id.manual_wb);
        wb.SetStuff(appSettingsManager, AppSettingsManager.MCCT);
        wb.setOnClickListener(manualButtonClickListner);

        contrast = (ManualButton)view.findViewById(R.id.manual_contrast);
        contrast.SetStuff(appSettingsManager, AppSettingsManager.MCONTRAST);
        contrast.setOnClickListener(manualButtonClickListner);

        saturation = (ManualButton)view.findViewById(R.id.manual_saturation);
        saturation.SetStuff(appSettingsManager, AppSettingsManager.MSATURATION);
        saturation.setOnClickListener(manualButtonClickListner);

        sharpness = (ManualButton)view.findViewById(R.id.manual_sharpness);
        sharpness.SetStuff(appSettingsManager, AppSettingsManager.MSHARPNESS);
        sharpness.setOnClickListener(manualButtonClickListner);

        programshift = (ManualButton)view.findViewById(R.id.manual_program_shift);
        programshift.SetStuff(appSettingsManager, "");
        programshift.setOnClickListener(manualButtonClickListner);

        zoom = (ManualButton)view.findViewById(R.id.manual_zoom);
        zoom.SetStuff(appSettingsManager, "");
        zoom.setOnClickListener(manualButtonClickListner);

        skintone = (ManualButton)view.findViewById(R.id.manual_skintone);
        skintone.SetStuff(appSettingsManager, "");
        skintone.setOnClickListener(manualButtonClickListner);

        fx  = (ManualButton)view.findViewById(R.id.manual_fx);
        fx.SetStuff(appSettingsManager, "");
        fx.setOnClickListener(manualButtonClickListner);

        convergence = (ManualButton)view.findViewById(R.id.manual_convergence);
        convergence.SetStuff(appSettingsManager, AppSettingsManager.MCONVERGENCE);
        convergence.setOnClickListener(manualButtonClickListner);
        if (wrapper != null)
            setWrapper();
    }



    @Override
    public void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper)
    {
        super.SetCameraUIWrapper(wrapper);
        //at this point a nullpointer could happen because the fragemnt is possible not added to the activity
        //but if its added notify it about the change
        try {
            wrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
            setWrapper();
        }
        catch (NullPointerException ex)
        {

        }


    }

    @Override
    public void SetStuff(AppSettingsManager appSettingsManager, I_Activity i_activity) {
        super.SetStuff(appSettingsManager, i_activity);
    }

    private void setWrapper() {
        contrast.SetAbstractManualParameter(wrapper.camParametersHandler.ManualContrast);
        burst.SetAbstractManualParameter(wrapper.camParametersHandler.Burst);
        brightness.SetAbstractManualParameter(wrapper.camParametersHandler.ManualBrightness);
        wb.SetAbstractManualParameter(wrapper.camParametersHandler.CCT);
        convergence.SetAbstractManualParameter(wrapper.camParametersHandler.ManualConvergence);
        exposure.SetAbstractManualParameter(wrapper.camParametersHandler.ManualExposure);
        fx.SetAbstractManualParameter(wrapper.camParametersHandler.FX);
        mf.SetAbstractManualParameter(wrapper.camParametersHandler.ManualFocus);
        saturation.SetAbstractManualParameter(wrapper.camParametersHandler.ManualSaturation);
        sharpness.SetAbstractManualParameter(wrapper.camParametersHandler.ManualSharpness);
        shutter.SetAbstractManualParameter(wrapper.camParametersHandler.ManualShutter);
        iso.SetAbstractManualParameter(wrapper.camParametersHandler.ISOManual);
        zoom.SetAbstractManualParameter(wrapper.camParametersHandler.Zoom);
        aperture.SetAbstractManualParameter(wrapper.camParametersHandler.ManualFNumber);
        skintone.SetAbstractManualParameter(wrapper.camParametersHandler.Skintone);
        programshift.SetAbstractManualParameter(wrapper.camParametersHandler.ProgramShift);
    }

    @Override
    public void ParametersLoaded() {
        if (wrapper != null)
            setWrapper();
    }

    //######## ManualButton Stuff#####
    View.OnClickListener manualButtonClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            if (currentButton != null)
                currentButton.RemoveParameterListner(ManualFragmentRotatingSeekbar.this);
            //when same button gets clicked second time
            if(v == currentButton && seekbar.getVisibility() == View.VISIBLE)
            {
                //hideseekbar and set color back from button
                seekbar.setVisibility(View.GONE);
                currentButton.SetActive(false);
            }
            //if no button was active or a different was clicked
            else
            {
                if (seekbar.getVisibility() == View.GONE)
                    seekbar.setVisibility(View.VISIBLE);
                //when already a button is active disable it
                if (currentButton != null)
                    currentButton.SetActive(false);
                //set the returned view as active and fill seekbar
                currentButton = (ManualButton) v;
                currentButton.SetActive(true);
                currentButton.SetParameterListner(ManualFragmentRotatingSeekbar.this);
                String[]vals = currentButton.getStringValues();
                if (vals == null || vals.length == 0) {
                    currentButton.SetActive(false);
                    seekbar.setVisibility(View.GONE);
                    Log.e(TAG, "Values returned from currentButton are NULL!");
                    return;
                }
                seekbar.SetStringValues(vals);
                seekbar.setProgress(currentButton.getCurrentItem());
                currentValuePos = currentButton.getCurrentItem();
                Log.d(TAG, "CurrentvaluePos " + currentValuePos);
            }

        }
    };

    //#########################SEEKBAR STUFF#############################


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        Log.d(TAG, "onProgressChanged:" + progress);
        currentValuePos = progress;
        if (!(wrapper instanceof CameraUiWrapperSony)) {
            currentButton.setValueToParameters(progress);
            currentButton.onCurrentValueChanged(progress);

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (wrapper instanceof CameraUiWrapperSony) {
            currentButton.setValueToParameters(currentValuePos);
            currentButton.onCurrentValueChanged(currentValuePos);
        }
    }

    @Override
    public void onIsSupportedChanged(boolean value)
    {
        if (!value) {
            seekbar.setVisibility(View.GONE);
            currentButton.SetActive(false);
        }
    }

    @Override
    public void onIsSetSupportedChanged(boolean value) {

    }

    @Override
    public void onMaxValueChanged(int max) {

    }

    @Override
    public void onMinValueChanged(int min) {

    }

    @Override
    public void onCurrentValueChanged(int current) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public void onCurrentStringValueChanged(String value) {

    }
}
