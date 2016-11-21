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

package freed.cam.ui.themesample.cameraui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.troop.freedcam.R;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import freed.ActivityInterface;
import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualParameter.I_ManualParameterEvent;
import freed.cam.apis.sonyremote.SonyCameraFragment;
import freed.cam.ui.themesample.AbstractFragment;
import freed.utils.AppSettingsManager;
import freed.utils.Logger;

/**
 * Created by troop on 08.12.2015.
 */
public class ManualFragment extends AbstractFragment implements OnSeekBarChangeListener, I_ManualParameterEvent
{
    private int currentValuePos;

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

    private AfBracketSettingsView afBracketSettingsView;


    private final String TAG = ManualFragment.class.getSimpleName();
    private ModuleChangedReciever moduleChangedReciever;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(layout.cameraui_manual_fragment_rotatingseekbar, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragment_activityInterface = (ActivityInterface)getActivity();
        seekbar = (RotatingSeekbar)view.findViewById(id.seekbar);
        seekbar.setOnSeekBarChangeListener(this);
        seekbar.setVisibility(View.GONE);

        mf = (ManualButton)view.findViewById(id.manual_mf);
        mf.SetStuff(fragment_activityInterface,AppSettingsManager.MF);
        mf.setOnClickListener(manualButtonClickListner);

        iso = (ManualButton)view.findViewById(id.manual_iso);
        iso.SetStuff(fragment_activityInterface, AppSettingsManager.MISO);
        iso.setOnClickListener(manualButtonClickListner);

        shutter = (ManualButton)view.findViewById(id.manual_shutter);
        shutter.SetStuff(fragment_activityInterface,AppSettingsManager.MSHUTTERSPEED);
        shutter.setOnClickListener(manualButtonClickListner);

        aperture = (ManualButton)view.findViewById(id.manual_aperture);
        aperture.SetStuff(fragment_activityInterface,"");
        aperture.setOnClickListener(manualButtonClickListner);

        exposure = (ManualButton)view.findViewById(id.manual_exposure);
        exposure.SetStuff(fragment_activityInterface,AppSettingsManager.MEXPOSURE);
        exposure.setOnClickListener(manualButtonClickListner);

        brightness = (ManualButton)view.findViewById(id.manual_brightness);
        brightness.SetStuff(fragment_activityInterface,AppSettingsManager.MBRIGHTNESS);
        brightness.setOnClickListener(manualButtonClickListner);

        burst = (ManualButton)view.findViewById(id.manual_burst);
        burst.SetStuff(fragment_activityInterface,AppSettingsManager.MBURST);
        burst.setOnClickListener(manualButtonClickListner);

        wb = (ManualButton)view.findViewById(id.manual_wb);
        wb.SetStuff(fragment_activityInterface,AppSettingsManager.MCCT);
        wb.setOnClickListener(manualButtonClickListner);

        contrast = (ManualButton)view.findViewById(id.manual_contrast);
        contrast.SetStuff(fragment_activityInterface,AppSettingsManager.MCONTRAST);
        contrast.setOnClickListener(manualButtonClickListner);

        saturation = (ManualButton)view.findViewById(id.manual_saturation);
        saturation.SetStuff(fragment_activityInterface,AppSettingsManager.MSATURATION);
        saturation.setOnClickListener(manualButtonClickListner);

        sharpness = (ManualButton)view.findViewById(id.manual_sharpness);
        sharpness.SetStuff(fragment_activityInterface,AppSettingsManager.MSHARPNESS);
        sharpness.setOnClickListener(manualButtonClickListner);

        programshift = (ManualButton)view.findViewById(id.manual_program_shift);
        programshift.SetStuff(fragment_activityInterface,"");
        programshift.setOnClickListener(manualButtonClickListner);

        zoom = (ManualButton)view.findViewById(id.manual_zoom);
        zoom.SetStuff(fragment_activityInterface,"");
        zoom.setOnClickListener(manualButtonClickListner);

        skintone = (ManualButton)view.findViewById(id.manual_skintone);
        skintone.SetStuff(fragment_activityInterface,"");
        skintone.setOnClickListener(manualButtonClickListner);

        fx = (ManualButton)view.findViewById(id.manual_fx);
        fx.SetStuff(fragment_activityInterface,"");
        fx.setOnClickListener(manualButtonClickListner);

        convergence = (ManualButton)view.findViewById(id.manual_convergence);
        convergence.SetStuff(fragment_activityInterface,AppSettingsManager.MCONVERGENCE);
        convergence.setOnClickListener(manualButtonClickListner);

        previewZoom = (ManualButton)view.findViewById(id.manual_zoom_preview);
        previewZoom.setOnClickListener(manualButtonClickListner);
        afBracketSettingsView = (AfBracketSettingsView)view.findViewById(id.manualFragment_afbsettings);

        moduleChangedReciever = new ModuleChangedReciever();
        ((ActivityInterface) getActivity()).RegisterLocalReciever(moduleChangedReciever, new IntentFilter("troop.com.freedcam.MODULE_CHANGED"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ActivityInterface) getActivity()).UnregisterLocalReciever(moduleChangedReciever);
    }

    @Override
    protected void setCameraUiWrapperToUi()
    {
        contrast.SetManualParameter(cameraUiWrapper.GetParameterHandler().ManualContrast);
        burst.SetManualParameter(cameraUiWrapper.GetParameterHandler().Burst);
        brightness.SetManualParameter(cameraUiWrapper.GetParameterHandler().ManualBrightness);
        wb.SetManualParameter(cameraUiWrapper.GetParameterHandler().CCT);
        convergence.SetManualParameter(cameraUiWrapper.GetParameterHandler().ManualConvergence);
        exposure.SetManualParameter(cameraUiWrapper.GetParameterHandler().ManualExposure);
        fx.SetManualParameter(cameraUiWrapper.GetParameterHandler().FX);
        mf.SetManualParameter(cameraUiWrapper.GetParameterHandler().ManualFocus);
        saturation.SetManualParameter(cameraUiWrapper.GetParameterHandler().ManualSaturation);
        sharpness.SetManualParameter(cameraUiWrapper.GetParameterHandler().ManualSharpness);
        shutter.SetManualParameter(cameraUiWrapper.GetParameterHandler().ManualShutter);
        iso.SetManualParameter(cameraUiWrapper.GetParameterHandler().ManualIso);
        zoom.SetManualParameter(cameraUiWrapper.GetParameterHandler().Zoom);
        aperture.SetManualParameter(cameraUiWrapper.GetParameterHandler().ManualFNumber);
        skintone.SetManualParameter(cameraUiWrapper.GetParameterHandler().Skintone);
        programshift.SetManualParameter(cameraUiWrapper.GetParameterHandler().ProgramShift);
        previewZoom.SetManualParameter(cameraUiWrapper.GetParameterHandler().PreviewZoom);
        seekbar.setVisibility(View.GONE);
        afBracketSettingsView.SetCameraWrapper(cameraUiWrapper);
        if (cameraUiWrapper.GetModuleHandler().GetCurrentModuleName().equals(KEYS.MODULE_AFBRACKET) && currentButton == mf && seekbar.getVisibility() == View.VISIBLE)
            afBracketSettingsView.setVisibility(View.VISIBLE);
        else
            afBracketSettingsView.setVisibility(View.GONE);

    }

    //######## ManualButton Stuff#####
    private final OnClickListener manualButtonClickListner = new OnClickListener() {
        @Override
        public void onClick(View v)
        {
            if (currentButton != null)
                currentButton.RemoveParameterListner(ManualFragment.this);
            //when same button gets clicked second time
            if(v == currentButton && seekbar.getVisibility() == View.VISIBLE)
            {
                //hideseekbar and set color back from button
                seekbar.setVisibility(View.GONE);
                currentButton.SetActive(false);
                afBracketSettingsView.setVisibility(View.GONE);
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
                currentButton.SetParameterListner(ManualFragment.this);
                if (currentButton == mf && cameraUiWrapper.GetModuleHandler().GetCurrentModuleName().equals(KEYS.MODULE_AFBRACKET))
                    afBracketSettingsView.setVisibility(View.VISIBLE);
                else
                    afBracketSettingsView.setVisibility(View.GONE);
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
        if (!(cameraUiWrapper instanceof SonyCameraFragment)) {
            currentButton.setValueToParameters(progress);
            currentButton.onCurrentValueChanged(progress);

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (cameraUiWrapper instanceof SonyCameraFragment) {
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
    public void onCurrentValueChanged(int current)
    {
       /* if(!seekbar.IsAutoScrolling()&& !seekbar.IsMoving())
        {
            seekbar.setProgress(current, false);
        }*/
    }

    @Override
    public void onValuesChanged(String[] values)
    {
        seekbar.SetStringValues(values);
    }

    @Override
    public void onCurrentStringValueChanged(String value) {

    }

    private class ModuleChangedReciever extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String module = intent.getStringExtra(context.getString(R.string.INTENT_EXTRA_MODULECHANGED));
            if (module.equals(KEYS.MODULE_AFBRACKET) && seekbar.getVisibility() == View.VISIBLE)
                afBracketSettingsView.setVisibility(View.VISIBLE);
            else
                afBracketSettingsView.setVisibility(View.GONE);
        }
    }

}
