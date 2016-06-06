/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedcam.ui.themesample.subfragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.sonyremote.camera.CameraUiWrapper;
import com.freedcam.ui.AbstractFragment;
import com.freedcam.ui.I_Activity;
import com.freedcam.ui.themesample.views.ManualButton;
import com.freedcam.ui.views.RotatingSeekbar;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;
import com.troop.freedcam.R;

/**
 * Created by troop on 08.12.2015.
 */
public class ManualFragmentRotatingSeekbar extends AbstractFragment implements SeekBar.OnSeekBarChangeListener, AbstractManualParameter.I_ManualParameterEvent
{
    private int currentValuePos = 0;

    private RotatingSeekbar seekbar;
    private ManualButton mf;
    private ManualButton iso;
    private ManualButton shutter;
    private ManualButton aperture;
    private ManualButton exposure;
    private ManualButton brightness;
    private ManualButton burst;
    private ManualButton wb;
    private ManualButton contrast;
    private ManualButton saturation;
    private ManualButton sharpness;
    private ManualButton programshift;
    private ManualButton zoom;
    private ManualButton skintone;
    private ManualButton fx;
    private ManualButton convergence;

    private ManualButton currentButton;

    private ManualButton previewZoom;

    private final String TAG = ManualFragmentRotatingSeekbar.class.getSimpleName();

    public static ManualFragmentRotatingSeekbar GetInstance(AppSettingsManager appSettingsManager, I_Activity activity)
    {
        ManualFragmentRotatingSeekbar mf = new ManualFragmentRotatingSeekbar();
        mf.appSettingsManager = appSettingsManager;
        mf.i_activity = activity;
        return mf;
    }

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
        mf.SetStuff(AppSettingsManager.MF,appSettingsManager);
        mf.setOnClickListener(manualButtonClickListner);

        iso = (ManualButton)view.findViewById(R.id.manual_iso);
        iso.SetStuff( AppSettingsManager.MISO,appSettingsManager);
        iso.setOnClickListener(manualButtonClickListner);

        shutter = (ManualButton)view.findViewById(R.id.manual_shutter);
        shutter.SetStuff(AppSettingsManager.MSHUTTERSPEED,appSettingsManager);
        shutter.setOnClickListener(manualButtonClickListner);

        aperture = (ManualButton)view.findViewById(R.id.manual_aperture);
        aperture.SetStuff("",appSettingsManager);
        aperture.setOnClickListener(manualButtonClickListner);

        exposure = (ManualButton)view.findViewById(R.id.manual_exposure);
        exposure.SetStuff(AppSettingsManager.MEXPOSURE,appSettingsManager);
        exposure.setOnClickListener(manualButtonClickListner);

        brightness = (ManualButton)view.findViewById(R.id.manual_brightness);
        brightness.SetStuff(AppSettingsManager.MBRIGHTNESS,appSettingsManager);
        brightness.setOnClickListener(manualButtonClickListner);

        burst = (ManualButton)view.findViewById(R.id.manual_burst);
        burst.SetStuff(AppSettingsManager.MBURST,appSettingsManager);
        burst.setOnClickListener(manualButtonClickListner);

        wb = (ManualButton)view.findViewById(R.id.manual_wb);
        wb.SetStuff(AppSettingsManager.MCCT,appSettingsManager);
        wb.setOnClickListener(manualButtonClickListner);

        contrast = (ManualButton)view.findViewById(R.id.manual_contrast);
        contrast.SetStuff(AppSettingsManager.MCONTRAST,appSettingsManager);
        contrast.setOnClickListener(manualButtonClickListner);

        saturation = (ManualButton)view.findViewById(R.id.manual_saturation);
        saturation.SetStuff(AppSettingsManager.MSATURATION,appSettingsManager);
        saturation.setOnClickListener(manualButtonClickListner);

        sharpness = (ManualButton)view.findViewById(R.id.manual_sharpness);
        sharpness.SetStuff(AppSettingsManager.MSHARPNESS,appSettingsManager);
        sharpness.setOnClickListener(manualButtonClickListner);

        programshift = (ManualButton)view.findViewById(R.id.manual_program_shift);
        programshift.SetStuff("",appSettingsManager);
        programshift.setOnClickListener(manualButtonClickListner);

        zoom = (ManualButton)view.findViewById(R.id.manual_zoom);
        zoom.SetStuff("",appSettingsManager);
        zoom.setOnClickListener(manualButtonClickListner);

        skintone = (ManualButton)view.findViewById(R.id.manual_skintone);
        skintone.SetStuff("",appSettingsManager);
        skintone.setOnClickListener(manualButtonClickListner);

        fx  = (ManualButton)view.findViewById(R.id.manual_fx);
        fx.SetStuff("",appSettingsManager);
        fx.setOnClickListener(manualButtonClickListner);

        convergence = (ManualButton)view.findViewById(R.id.manual_convergence);
        convergence.SetStuff(AppSettingsManager.MCONVERGENCE,appSettingsManager);
        convergence.setOnClickListener(manualButtonClickListner);

        previewZoom = (ManualButton)view.findViewById(R.id.manual_zoom_preview);
        previewZoom.setOnClickListener(manualButtonClickListner);
    }

    @Override
    protected void setCameraUiWrapperToUi() {
        contrast.SetAbstractManualParameter(cameraUiWrapper.parametersHandler.ManualContrast);
        burst.SetAbstractManualParameter(cameraUiWrapper.parametersHandler.Burst);
        brightness.SetAbstractManualParameter(cameraUiWrapper.parametersHandler.ManualBrightness);
        wb.SetAbstractManualParameter(cameraUiWrapper.parametersHandler.CCT);
        convergence.SetAbstractManualParameter(cameraUiWrapper.parametersHandler.ManualConvergence);
        exposure.SetAbstractManualParameter(cameraUiWrapper.parametersHandler.ManualExposure);
        fx.SetAbstractManualParameter(cameraUiWrapper.parametersHandler.FX);
        mf.SetAbstractManualParameter(cameraUiWrapper.parametersHandler.ManualFocus);
        saturation.SetAbstractManualParameter(cameraUiWrapper.parametersHandler.ManualSaturation);
        sharpness.SetAbstractManualParameter(cameraUiWrapper.parametersHandler.ManualSharpness);
        shutter.SetAbstractManualParameter(cameraUiWrapper.parametersHandler.ManualShutter);
        iso.SetAbstractManualParameter(cameraUiWrapper.parametersHandler.ManualIso);
        zoom.SetAbstractManualParameter(cameraUiWrapper.parametersHandler.Zoom);
        aperture.SetAbstractManualParameter(cameraUiWrapper.parametersHandler.ManualFNumber);
        skintone.SetAbstractManualParameter(cameraUiWrapper.parametersHandler.Skintone);
        programshift.SetAbstractManualParameter(cameraUiWrapper.parametersHandler.ProgramShift);
        previewZoom.SetAbstractManualParameter(cameraUiWrapper.parametersHandler.PreviewZoom);
    }

    //######## ManualButton Stuff#####
    private View.OnClickListener manualButtonClickListner = new View.OnClickListener() {
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
                    Logger.e(TAG, "Values returned from currentButton are NULL!");
                    return;
                }
                seekbar.SetStringValues(vals);
                seekbar.setProgress(currentButton.getCurrentItem(),false);
                currentValuePos = currentButton.getCurrentItem();
                Logger.d(TAG, "CurrentvaluePos " + currentValuePos);
            }

        }
    };

    //#########################SEEKBAR STUFF#############################


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        Logger.d(TAG, "onProgressChanged:" + progress);
        currentValuePos = progress;
        if (!(cameraUiWrapper instanceof CameraUiWrapper)) {
            currentButton.setValueToParameters(progress);
            currentButton.onCurrentValueChanged(progress);

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (cameraUiWrapper instanceof CameraUiWrapper) {
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
    public void onIsSetSupportedChanged(boolean value)
    {
        if (value)
            seekbar.setVisibility(View.VISIBLE);
        else
            seekbar.setVisibility(View.GONE);
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
