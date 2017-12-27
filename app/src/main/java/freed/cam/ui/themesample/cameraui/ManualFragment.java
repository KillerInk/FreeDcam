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

import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.troop.freedcam.R;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleChangedEvent;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.cam.apis.basecamera.parameters.ParameterEvents;
import freed.cam.apis.camera2.parameters.manual.ManualToneMapCurveApi2;
import freed.cam.apis.sonyremote.SonyCameraRemoteFragment;
import freed.cam.ui.themesample.AbstractFragment;
import freed.cam.ui.themesample.cameraui.childs.ManualButtonToneCurve;
import freed.settings.Settings;
import freed.settings.SettingsManager;
import freed.utils.CurveView;
import freed.utils.CurveViewControl;
import freed.utils.Log;

/**
 * Created by troop on 08.12.2015.
 */
public class ManualFragment extends AbstractFragment implements OnSeekBarChangeListener, ParameterEvents, ModuleChangedEvent, CurveView.CurveChangedEvent
{
    private int currentValuePos;

    private RotatingSeekbar seekbar;

    private ManualButton currentButton;

    private CurveViewControl curveView;

    private AfBracketSettingsView afBracketSettingsView;

    private LinearLayout manualItemsHolder;

    private final String TAG = ManualFragment.class.getSimpleName();


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

        curveView = (CurveViewControl) view.findViewById(id.curveView);
        curveView.setVisibility(View.GONE);

        manualItemsHolder = (LinearLayout)view.findViewById(id.manualItemsHolder);

        afBracketSettingsView = (AfBracketSettingsView)view.findViewById(id.manualFragment_afbsettings);
        afBracketSettingsView.setVisibility(View.GONE);
    }

    @Override
    public void setCameraToUi(CameraWrapperInterface wrapper)
    {
        super.setCameraToUi(wrapper);
        if (manualItemsHolder == null)
            return;
        //rest views to init state
        manualItemsHolder.removeAllViews();
        seekbar.setVisibility(View.GONE);
        if (currentButton != null) {
            currentButton.SetActive(false);
            currentButton.SetParameterListner(null);
            currentButton = null;
        }
        afBracketSettingsView.setVisibility(View.GONE);


        if (cameraUiWrapper != null)
        {
            cameraUiWrapper.getModuleHandler().addListner(this);
            SettingsManager aps = SettingsManager.getInstance();
            AbstractParameterHandler parms = cameraUiWrapper.getParameterHandler();
            if (parms.get(Settings.M_Zoom) != null)
            {
                ManualButton btn = new ManualButton(getContext(), aps.get(Settings.M_Zoom), parms.get(Settings.M_Zoom), R.drawable.manual_zoom);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }

            if (parms.get(Settings.M_Focus) != null) {
                ManualButton btn = new ManualButton(getContext(), aps.get(Settings.M_Focus), parms.get(Settings.M_Focus), R.drawable.manual_focus);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(Settings.M_ManualIso) != null) {
                ManualButton btn = new ManualButton(getContext(), aps.get(Settings.M_ManualIso), parms.get(Settings.M_ManualIso), R.drawable.manual_iso);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(Settings.M_ExposureTime) != null) {
                ManualButton btn = new ManualButton(getContext(), aps.get(Settings.M_ExposureTime), parms.get(Settings.M_ExposureTime), R.drawable.manual_shutter);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(Settings.M_Fnumber) != null) {
                ManualButton btn = new ManualButton(getContext(), aps.get(Settings.M_Fnumber), parms.get(Settings.M_Fnumber), R.drawable.manual_fnum);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(Settings.M_ExposureCompensation) != null) {
                ManualButton btn = new ManualButton(getContext(), aps.get(Settings.M_ExposureCompensation), parms.get(Settings.M_ExposureCompensation), R.drawable.manual_exposure);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(Settings.M_Whitebalance) != null) {
                ManualButton btn = new ManualButton(getContext(), aps.get(Settings.M_Whitebalance), parms.get(Settings.M_Whitebalance), R.drawable.manual_wb);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }

            if (parms.get(Settings.M_Burst) != null) {
                ManualButton btn = new ManualButton(getContext(), aps.get(Settings.M_Burst), parms.get(Settings.M_Burst), R.drawable.manual_burst);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(Settings.M_Contrast) != null) {
                ManualButton btn = new ManualButton(getContext(), aps.get(Settings.M_Contrast), parms.get(Settings.M_Contrast), R.drawable.manual_contrast);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(Settings.M_Brightness) != null) {
                ManualButton btn = new ManualButton(getContext(), aps.get(Settings.M_Brightness), parms.get(Settings.M_Brightness), R.drawable.brightness);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(Settings.M_Saturation) != null) {
                ManualButton btn = new ManualButton(getContext(), aps.get(Settings.M_Saturation), parms.get(Settings.M_Saturation), R.drawable.manual_saturation);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(Settings.M_Sharpness) != null) {
                ManualButton btn = new ManualButton(getContext(), aps.get(Settings.M_Sharpness), parms.get(Settings.M_Sharpness), R.drawable.manual_sharpness);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(Settings.M_3D_Convergence) != null) {
                ManualButton btn = new ManualButton(getContext(), aps.get(Settings.M_3D_Convergence), parms.get(Settings.M_3D_Convergence), R.drawable.manual_convergence);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(Settings.M_FX) != null) {
                ManualButton btn = new ManualButton(getContext(), aps.get(Settings.M_FX), parms.get(Settings.M_FX), R.drawable.manual_fx);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(Settings.M_ProgramShift) != null) {
                ManualButton btn = new ManualButton(getContext(), aps.get(Settings.M_ProgramShift), parms.get(Settings.M_ProgramShift), R.drawable.manual_shift);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(Settings.scalePreview) != null) {
                ManualButton btn = new ManualButton(getContext(), aps.get(Settings.M_PreviewZoom), parms.get(Settings.M_PreviewZoom), R.drawable.manual_zoom);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            /*if (parms.black != null) {
                ManualButton btn = new ManualButton(getContext(), null, parms.black, R.drawable.manual_black);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.shadows != null) {
                ManualButton btn = new ManualButton(getContext(), null, parms.shadows, R.drawable.manual_shadows);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.midtones != null) {
                ManualButton btn = new ManualButton(getContext(), null, parms.midtones, R.drawable.manual_midtones);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.highlights != null) {
                ManualButton btn = new ManualButton(getContext(), null, parms.highlights, R.drawable.manual_highlights);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.white != null) {
                ManualButton btn = new ManualButton(getContext(), null, parms.white, R.drawable.manual_white);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }*/
            if (parms.get(Settings.M_ToneCurve) != null)
            {
                ManualButton btn = new ManualButton(getContext(), null, parms.get(Settings.M_ToneCurve), R.drawable.manual_midtones);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            curveView.setVisibility(View.GONE);
            curveView.setCurveChangedListner(this);

            seekbar.setVisibility(View.GONE);
            afBracketSettingsView.SetCameraWrapper(cameraUiWrapper);
            if (cameraUiWrapper.getModuleHandler().getCurrentModuleName().equals(cameraUiWrapper.getResString(R.string.module_afbracket)) /*&& currentButton == mf*/ && seekbar.getVisibility() == View.VISIBLE)
                afBracketSettingsView.setVisibility(View.VISIBLE);
            else
                afBracketSettingsView.setVisibility(View.GONE);
        }
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
                if (/*currentButton == mf &&*/ cameraUiWrapper.getModuleHandler().getCurrentModuleName().equals(cameraUiWrapper.getResString(R.string.module_afbracket)))
                    afBracketSettingsView.setVisibility(View.VISIBLE);
                else
                    afBracketSettingsView.setVisibility(View.GONE);

                if (currentButton instanceof ManualButtonToneCurve)
                {
                    seekbar.setVisibility(View.GONE);
                    if (curveView.getVisibility() == View.GONE)
                        curveView.setVisibility(View.VISIBLE);
                    else {
                        curveView.setVisibility(View.GONE);
                        currentButton.SetActive(false);
                    }
                }
                else {
                    curveView.setVisibility(View.GONE);
                    String[] vals = currentButton.getStringValues();
                    if (vals == null || vals.length == 0) {
                        currentButton.SetActive(false);
                        seekbar.setVisibility(View.GONE);
                        Log.e(TAG, "Values returned from currentButton are NULL!");
                        return;
                    }
                    seekbar.SetStringValues(vals);
                    seekbar.setProgress(currentButton.getCurrentItem(), false);
                    currentValuePos = currentButton.getCurrentItem();
                    Log.d(TAG, "CurrentvaluePos " + currentValuePos);
                }
            }

        }
    };

    //#########################SEEKBAR STUFF#############################


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        Log.d(TAG, "onProgressChanged:" + progress);
        currentValuePos = progress;
        if (!(cameraUiWrapper instanceof SonyCameraRemoteFragment)) {
            currentButton.setValueToParameters(progress);
            currentButton.onIntValueChanged(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (cameraUiWrapper instanceof SonyCameraRemoteFragment) {
            currentButton.setValueToParameters(currentValuePos);
            currentButton.onIntValueChanged(currentValuePos);
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
    public void onIsSetSupportedChanged(final boolean value)
    {
        seekbar.post(new Runnable() {
            @Override
            public void run() {
                if (value)
                    seekbar.setVisibility(View.VISIBLE);
                else
                    seekbar.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public void onIntValueChanged(int current)
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
    public void onStringValueChanged(String value) {

    }

    /**
     * Gets called when the module has changed
     *
     * @param module
     */
    @Override
    public void onModuleChanged(String module)
    {
        if (module.equals(cameraUiWrapper.getResString(R.string.module_afbracket)) && seekbar.getVisibility() == View.VISIBLE)
            afBracketSettingsView.setVisibility(View.VISIBLE);
        else
            afBracketSettingsView.setVisibility(View.GONE);
    }

    @Override
    public void onCurveChanged(PointF[] pointFs) {
        float[] ar = new float[pointFs.length*2];
        int count = 0;
        for (int i = 0; i< pointFs.length; i++)
        {
                ar[count++] = pointFs[i].x;
                ar[count++] = pointFs[i].y;
        }
        ((ManualToneMapCurveApi2.ToneCurveParameter)cameraUiWrapper.getParameterHandler().get(Settings.M_ToneCurve)).setCurveToCamera(ar);
    }

    private float[] pointFtoFloatArray(PointF[] pointFs)
    {
        float[] ar = new float[pointFs.length*2];
        int count = 0;
        for (int i = 0; i< pointFs.length; i++)
        {
            ar[count++] = pointFs[i].x;
            ar[count++] = pointFs[i].y;
        }
        return ar;
    }

    @Override
    public void onCurveChanged(PointF[] r, PointF[] g, PointF[] b) {
        ((ManualToneMapCurveApi2.ToneCurveParameter)cameraUiWrapper.getParameterHandler().get(Settings.M_ToneCurve)).setCurveToCamera(pointFtoFloatArray(r),pointFtoFloatArray(g),pointFtoFloatArray(b));
    }

    @Override
    public void onTouchStart() {
        cameraUiWrapper.getActivityInterface().DisablePagerTouch(true);
    }

    @Override
    public void onTouchEnd() {
        cameraUiWrapper.getActivityInterface().DisablePagerTouch(false);
    }

}
