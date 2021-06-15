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
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import androidx.databinding.Observable;

import com.troop.freedcam.BR;
import com.troop.freedcam.R;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import freed.FreedApplication;
import freed.cam.apis.CameraApiManager;
import freed.cam.apis.basecamera.CameraHolderEvent;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.Size;
import freed.cam.apis.basecamera.modules.ModuleChangedEvent;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterHandler;
import freed.cam.apis.camera2.parameters.manual.ManualToneMapCurveApi2;
import freed.cam.apis.sonyremote.SonyRemoteCamera;
import freed.cam.events.DisableViewPagerTouchEvent;
import freed.cam.events.EventBusHelper;
import freed.cam.events.ModuleHasChangedEvent;
import freed.cam.ui.themesample.AbstractFragment;
import freed.cam.ui.themesample.cameraui.childs.ManualButtonMF;
import freed.cam.ui.themesample.cameraui.childs.ManualButtonToneCurve;
import freed.settings.SettingKeys;
import freed.utils.Log;
import freed.views.CurveView;
import freed.views.CurveViewControl;

/**
 * Created by troop on 08.12.2015.
 */
@AndroidEntryPoint
public class ManualFragment extends AbstractFragment implements OnSeekBarChangeListener, ModuleChangedEvent, CurveView.CurveChangedEvent, CameraHolderEvent
{
    private int currentValuePos;

    private RotatingSeekbar seekbar;

    private ManualButton currentButton;

    private CurveViewControl curveView;

    private AfBracketSettingsView afBracketSettingsView;

    private LinearLayout manualItemsHolder;

    private Handler handler = new Handler();

    private final String TAG = ManualFragment.class.getSimpleName();
    @Inject
    CameraApiManager cameraApiManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(layout.cameraui_manual_fragment_rotatingseekbar, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        seekbar = view.findViewById(id.seekbar);
        seekbar.setOnSeekBarChangeListener(this);
        seekbar.setVisibility(View.GONE);

        curveView = view.findViewById(id.curveView);
        curveView.setVisibility(View.GONE);

        manualItemsHolder = view.findViewById(id.manualItemsHolder);

        afBracketSettingsView = view.findViewById(id.manualFragment_afbsettings);
        afBracketSettingsView.setVisibility(View.GONE);
        cameraApiManager.addEventListner(this);
        onCameraOpenFinished();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cameraApiManager.removeEventListner(this);
    }

    private void setCameraToUi(CameraWrapperInterface cameraUiWrapper)
    {
        if (manualItemsHolder == null)
            return;
        //rest views to init state
        manualItemsHolder.removeAllViews();
        seekbar.setVisibility(View.GONE);
        if (currentButton != null) {
            currentButton.SetActive(false);
            currentButton = null;
        }
        afBracketSettingsView.setVisibility(View.GONE);


        if (cameraUiWrapper != null)
        {
            ParameterHandler parms = cameraUiWrapper.getParameterHandler();
            if (parms.get(SettingKeys.M_Zoom) != null)
            {
                ManualButton btn = new ManualButton(getContext(), parms.get(SettingKeys.M_Zoom), R.drawable.manual_zoom);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }

            if (parms.get(SettingKeys.M_Focus) != null) {
                ManualButton btn = new ManualButtonMF(getContext(), parms.get(SettingKeys.M_Focus), R.drawable.manual_focus);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(SettingKeys.M_ManualIso) != null) {
                ManualButton btn = new ManualButton(getContext(), parms.get(SettingKeys.M_ManualIso), R.drawable.manual_iso);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(SettingKeys.M_ExposureTime) != null) {
                ManualButton btn = new ManualButton(getContext(), parms.get(SettingKeys.M_ExposureTime), R.drawable.manual_shutter);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(SettingKeys.M_Fnumber) != null) {
                ManualButton btn = new ManualButton(getContext(), parms.get(SettingKeys.M_Fnumber), R.drawable.manual_fnum);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(SettingKeys.M_Aperture) != null) {
                ManualButton btn = new ManualButton(getContext(), parms.get(SettingKeys.M_Aperture), R.drawable.manual_fnum);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(SettingKeys.M_ExposureCompensation) != null) {
                ManualButton btn = new ManualButton(getContext(), parms.get(SettingKeys.M_ExposureCompensation), R.drawable.manual_exposure);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(SettingKeys.M_Whitebalance) != null) {
                ManualButton btn = new ManualButton(getContext(), parms.get(SettingKeys.M_Whitebalance), R.drawable.manual_wb);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }

            if (parms.get(SettingKeys.M_Burst) != null) {
                ManualButton btn = new ManualButton(getContext(), parms.get(SettingKeys.M_Burst), R.drawable.manual_burst);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(SettingKeys.M_Contrast) != null) {
                ManualButton btn = new ManualButton(getContext(), parms.get(SettingKeys.M_Contrast), R.drawable.manual_contrast);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(SettingKeys.M_Brightness) != null) {
                ManualButton btn = new ManualButton(getContext(), parms.get(SettingKeys.M_Brightness), R.drawable.brightness);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(SettingKeys.M_Saturation) != null) {
                ManualButton btn = new ManualButton(getContext(), parms.get(SettingKeys.M_Saturation), R.drawable.manual_saturation);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(SettingKeys.M_Sharpness) != null) {
                ManualButton btn = new ManualButton(getContext(), parms.get(SettingKeys.M_Sharpness), R.drawable.manual_sharpness);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }

            if (parms.get(SettingKeys.M_FX) != null) {
                ManualButton btn = new ManualButton(getContext(), parms.get(SettingKeys.M_FX), R.drawable.manual_fx);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(SettingKeys.M_ProgramShift) != null) {
                ManualButton btn = new ManualButton(getContext(), parms.get(SettingKeys.M_ProgramShift), R.drawable.manual_shift);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
            }
            if (parms.get(SettingKeys.SCALE_PREVIEW) != null) {
                ManualButton btn = new ManualButton(getContext(), parms.get(SettingKeys.M_PreviewZoom), R.drawable.manual_zoom);
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
            if (parms.get(SettingKeys.TONE_CURVE_PARAMETER) != null)
            {
                ManualButtonToneCurve btn = new ManualButtonToneCurve(getContext(), parms.get(SettingKeys.TONE_CURVE_PARAMETER), R.drawable.manual_midtones);
                btn.setOnClickListener(manualButtonClickListner);
                //btn.onStringValueChanged("");
                manualItemsHolder.addView(btn);
            }
            curveView.setVisibility(View.GONE);
            curveView.setCurveChangedListner(this);

            seekbar.setVisibility(View.GONE);
            afBracketSettingsView.SetCameraWrapper(cameraUiWrapper);
            if (cameraUiWrapper.getModuleHandler().getCurrentModuleName().equals(FreedApplication.getStringFromRessources(R.string.module_afbracket))
                    && currentButton instanceof ManualButtonMF
                    && seekbar.getVisibility() == View.VISIBLE)
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
            if (v instanceof ManualButton && ((ManualButton)v).parameter.getViewState() == AbstractParameter.ViewState.Disabled)
                return;
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
                if (currentButton != null) {
                    ((AbstractParameter)currentButton.parameter).removeOnPropertyChangedCallback(selectedParameterObserver);
                    currentButton.SetActive(false);
                }
                //set the returned view as active and fill seekbar
                currentButton = (ManualButton) v;
                currentButton.SetActive(true);
                ((AbstractParameter)currentButton.parameter).addOnPropertyChangedCallback(selectedParameterObserver);

                if (currentButton instanceof ManualButtonMF && cameraApiManager.getCamera().getModuleHandler().getCurrentModuleName().equals(FreedApplication.getStringFromRessources(R.string.module_afbracket)))
                    afBracketSettingsView.setVisibility(View.VISIBLE);
                else
                    afBracketSettingsView.setVisibility(View.GONE);

                if (currentButton instanceof ManualButtonToneCurve)
                {
                    seekbar.setVisibility(View.GONE);
                    if (curveView.getVisibility() == View.GONE) {
                        curveView.setVisibility(View.VISIBLE);
                        curveView.bringToFront();
                    }
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
        if (!(cameraApiManager.getCamera() instanceof SonyRemoteCamera)) {
            currentButton.setValueToParameters(progress);

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (cameraApiManager.getCamera() instanceof SonyRemoteCamera) {
            currentButton.setValueToParameters(currentValuePos);

        }
    }

    Observable.OnPropertyChangedCallback selectedParameterObserver = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (propertyId == BR.viewState)
                onViewStateChanged(((AbstractParameter)sender).getViewState());
            if (propertyId == BR.intValue)
                onIntValueChanged(((AbstractParameter)sender).getIntValue());
            if (propertyId == BR.stringValues)
                onValuesChanged(((AbstractParameter)sender).getStringValues());
        }
    };

    private void onViewStateChanged(AbstractParameter.ViewState value) {
        switch (value)
        {
            case Visible:
                break;
            case Hidden:
                seekbar.setVisibility(View.GONE);
                currentButton.SetActive(false);
                break;
            case Disabled:
                seekbar.post(() -> {
                        seekbar.setVisibility(View.GONE);
                });
                break;
            case Enabled:
                seekbar.post(() -> {
                    seekbar.setVisibility(View.VISIBLE);
                });
                break;
        }
    }

    private void onIntValueChanged(int current)
    {
        if(!seekbar.IsAutoScrolling()&& !seekbar.IsMoving())
        {
            seekbar.setProgress(current, false);
        }
    }

    private void onValuesChanged(String[] values)
    {
        seekbar.SetStringValues(values);
    }

    @Subscribe
    public void onModuleHasChangedEvent(ModuleHasChangedEvent event)
    {
        onModuleChanged(event.NewModuleName);
    }

    /**
     * Gets called when the module has changed
     *
     * @param module
     */
    @Override
    public void onModuleChanged(String module)
    {
        if (cameraApiManager.getCamera() == null || FreedApplication.getContext() == null)
            return;
        if (module.equals(FreedApplication.getStringFromRessources(R.string.module_afbracket)) && seekbar.getVisibility() == View.VISIBLE)
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
        ((ManualToneMapCurveApi2.ToneCurveParameter) cameraApiManager.getCamera().getParameterHandler().get(SettingKeys.TONE_CURVE_PARAMETER)).setCurveToCamera(ar);
    }

    public static float[] pointFtoFloatArray(PointF[] pointFs)
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
        ((ManualToneMapCurveApi2.ToneCurveParameter) cameraApiManager.getCamera().getParameterHandler().get(SettingKeys.TONE_CURVE_PARAMETER)).setCurveToCamera(pointFtoFloatArray(r),pointFtoFloatArray(g),pointFtoFloatArray(b));
    }

    @Override
    public void onTouchStart() {
        EventBusHelper.post(new DisableViewPagerTouchEvent(true));
    }

    @Override
    public void onTouchEnd() {
        EventBusHelper.post(new DisableViewPagerTouchEvent(false));
    }

    @Override
    public void onClick(PointF pointF) {

    }

    @Override
    public void onCameraOpen() {

    }

    @Override
    public void onCameraOpenFinished() {
        handler.post(() -> setCameraToUi(cameraApiManager.getCamera()));
    }

    @Override
    public void onCameraClose() {
        handler.post(() -> setCameraToUi(null));

    }

    @Override
    public void onCameraError(String error) {

    }

    @Override
    public void onCameraChangedAspectRatioEvent(Size size) {

    }
}
