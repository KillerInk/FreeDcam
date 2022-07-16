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

import android.graphics.Color;
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
import androidx.databinding.library.baseAdapters.BR;


import com.troop.freedcam.R;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import freed.FreedApplication;
import freed.cam.apis.CameraApiManager;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.Size;
import freed.cam.event.module.ModuleChangedEvent;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterHandler;
import freed.cam.apis.camera2.parameters.manual.ManualToneMapCurveApi2;
import freed.cam.event.camera.CameraHolderEvent;
import freed.cam.ui.KeyPressedController;
import freed.cam.ui.themenextgen.view.button.ManualButtonInterface;
import freed.cam.ui.themenextgen.view.button.NextGenMfItem;
import freed.cam.ui.themenextgen.view.button.NextGenTextItem;
import freed.cam.ui.themesample.AbstractFragment;
import freed.cam.ui.themesample.PagingViewTouchState;
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

    private ManualButtonInterface currentButton;

    private CurveViewControl curveView;

    private AfBracketSettingsView afBracketSettingsView;

    private LinearLayout manualItemsHolder;

    private Handler handler = new Handler();

    private final String TAG = ManualFragment.class.getSimpleName();
    @Inject
    CameraApiManager cameraApiManager;
    @Inject
    PagingViewTouchState pagingViewTouchState;
    @Inject
    KeyPressedController keyPressedController;

    private HashMap<SettingKeys.Key, ManualButtonInterface> buttonHashMap;
    private List<SettingKeys.Key> supportedManuals;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(layout.cameraui_manual_fragment_rotatingseekbar, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonHashMap = new HashMap<>();
        supportedManuals = new ArrayList<>();
        keyPressedController.setManualModeChangedEventListner(manualModeChangedEvent);
        seekbar = view.findViewById(id.seekbar);
        seekbar.setOnSeekBarChangeListener(this);
        seekbar.setVisibility(View.GONE);

        curveView = view.findViewById(id.curveView);
        curveView.setVisibility(View.GONE);

        manualItemsHolder = view.findViewById(id.manualItemsHolder);

        afBracketSettingsView = view.findViewById(id.manualFragment_afbsettings);
        afBracketSettingsView.setVisibility(View.GONE);
        cameraApiManager.addEventListner(this);
        cameraApiManager.addModuleChangedEventListner(this);
        onCameraOpenFinished();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cameraApiManager.removeEventListner(this);
        cameraApiManager.removeModuleChangedEventListner(this);
    }

    private void setCameraToUi(CameraWrapperInterface cameraUiWrapper)
    {
        if (manualItemsHolder == null)
            return;
        //rest views to init state
        manualItemsHolder.removeAllViews();
        buttonHashMap.clear();
        supportedManuals.clear();
        seekbar.setVisibility(View.GONE);
        if (currentButton != null) {
            currentButton.SetActive(false);
            currentButton = null;
        }
        afBracketSettingsView.setVisibility(View.GONE);


        if (cameraUiWrapper != null)
        {
            ParameterHandler parms = cameraUiWrapper.getParameterHandler();
            addNextGenButton(parms,SettingKeys.M_Zoom, getContext().getString(R.string.font_zoom_plus));
            //addManualButton(parms,SettingKeys.M_Zoom,R.drawable.manual_zoom);
            //addManualButton(parms,SettingKeys.M_Focus,R.drawable.manual_focus);
            //used to simple check if its mf and then activate near/far limits in afbracket module
            //need a rework that modules can have their own specific settings
            /*if (parms.get(SettingKeys.M_Focus) != null) {
                ManualButtonMF btn = new ManualButtonMF(getContext(), parms.get(SettingKeys.M_Focus), R.drawable.manual_focus);
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
                buttonHashMap.put(SettingKeys.M_Focus,btn);
                supportedManuals.add(SettingKeys.M_Focus);
            }*/

            if (parms.get(SettingKeys.M_Focus) != null) {
                NextGenMfItem btn = NextGenMfItem.getInstance(getContext(),getContext().getString(R.string.font_manual_focus), (AbstractParameter) parms.get(SettingKeys.M_Focus));
                btn.setOnClickListener(manualButtonClickListner);
                manualItemsHolder.addView(btn);
                buttonHashMap.put(SettingKeys.M_Focus,btn);
                supportedManuals.add(SettingKeys.M_Focus);
            }
            addNextGenButton(parms,SettingKeys.M_ManualIso, getContext().getString(R.string.font_iso));
            //addManualButton(parms,SettingKeys.M_ManualIso,R.drawable.manual_iso);
            addNextGenButton(parms,SettingKeys.M_ExposureTime, getContext().getString(R.string.font_exposuretime));
            //addManualButton(parms,SettingKeys.M_ExposureTime,R.drawable.manual_shutter);
            //addManualButton(parms,SettingKeys.M_Fnumber,R.drawable.manual_fnum);
            addNextGenButton(parms,SettingKeys.M_Aperture, getContext().getString(R.string.font_aperture));
            //addManualButton(parms,SettingKeys.M_Aperture,R.drawable.manual_fnum);
            addNextGenButton(parms,SettingKeys.M_ExposureCompensation, getContext().getString(R.string.font_ev));
            //addManualButton(parms,SettingKeys.M_ExposureCompensation,R.drawable.manual_exposure);
            addManualButton(parms,SettingKeys.M_Whitebalance,R.drawable.manual_wb);
            addNextGenButton(parms,SettingKeys.M_Whitebalance, getContext().getString(R.string.font_wb));
            addNextGenButton(parms,SettingKeys.M_Burst, getContext().getString(R.string.font_burst));
            //addManualButton(parms,SettingKeys.M_Burst,R.drawable.manual_burst);
            addNextGenButton(parms,SettingKeys.M_Contrast, getContext().getString(R.string.font_contrast));
            //addManualButton(parms,SettingKeys.M_Contrast,R.drawable.manual_contrast);
            addNextGenButton(parms,SettingKeys.M_Brightness, getContext().getString(R.string.font_brightness));
            //addManualButton(parms,SettingKeys.M_Brightness,R.drawable.brightness);
            //addManualButton(parms,SettingKeys.M_Saturation,R.drawable.manual_saturation);
            addNextGenButton(parms,SettingKeys.M_Saturation, getContext().getString(R.string.font_saturation));
            addNextGenButton(parms,SettingKeys.M_Sharpness, getContext().getString(R.string.font_sharpness));
            //addManualButton(parms,SettingKeys.M_Sharpness,R.drawable.manual_sharpness);
            /*addManualButton(parms,SettingKeys.M_FX,R.drawable.manual_fx);
            addManualButton(parms,SettingKeys.M_ProgramShift,R.drawable.manual_shift);
            addManualButton(parms,SettingKeys.SCALE_PREVIEW,R.drawable.manual_zoom);*/

            if (parms.get(SettingKeys.TONE_CURVE_PARAMETER) != null)
            {
                ManualButtonToneCurve btn = new ManualButtonToneCurve(getContext(), parms.get(SettingKeys.TONE_CURVE_PARAMETER), R.drawable.manual_midtones);
                btn.setOnClickListener(manualButtonClickListner);
                //btn.onStringValueChanged("");
                manualItemsHolder.addView(btn);
            }
            curveView.setVisibility(View.GONE);
            curveView.setCurveChangedListner(this);

            addNextGenButton(parms,SettingKeys.M_ZEBRA_HIGH,getContext().getString(R.string.font_clipping), Color.RED);
            addNextGenButton(parms,SettingKeys.M_ZEBRA_LOW,getContext().getString(R.string.font_clipping),Color.BLUE);
            //addManualButton(parms,SettingKeys.M_ZEBRA_HIGH,R.drawable.clipping);
            //addManualButton(parms,SettingKeys.M_ZEBRA_LOW,R.drawable.clipping);

            seekbar.setVisibility(View.GONE);
            if (cameraUiWrapper.getModuleHandler().getCurrentModuleName().equals(FreedApplication.getStringFromRessources(R.string.module_afbracket))
                    && currentButton instanceof ManualButtonMF
                    && seekbar.getVisibility() == View.VISIBLE)
                afBracketSettingsView.setVisibility(View.VISIBLE);
            else
                afBracketSettingsView.setVisibility(View.GONE);
            keyPressedController.setSupportedManualsModes(supportedManuals);
            if (currentButton != null)
                keyPressedController.setActiveKey(getKeyFromButton(currentButton));
            else if(supportedManuals.size() > 0)
                keyPressedController.setActiveKey(supportedManuals.get(0));
        }
    }

    private void addManualButton(ParameterHandler parms,SettingKeys.Key key, int drawable)
    {
        if (parms.get(key) != null) {
            ManualButton btn = new ManualButton(getContext(), parms.get(key), drawable);
            btn.setOnClickListener(manualButtonClickListner);
            manualItemsHolder.addView(btn);
            buttonHashMap.put(key,btn);
            supportedManuals.add(key);
        }
    }

    private void addNextGenButton(ParameterHandler parms,SettingKeys.Key key, String stringid)
    {
        if (parms.get(key) != null) {
            NextGenTextItem btn = NextGenTextItem.getInstance(getContext(),stringid, (AbstractParameter) parms.get(key));
            btn.setOnClickListener(manualButtonClickListner);
            manualItemsHolder.addView(btn);
            buttonHashMap.put(key,btn);
            supportedManuals.add(key);
        }
    }

    private void addNextGenButton(ParameterHandler parms,SettingKeys.Key key, String stringid,int color)
    {
        if (parms.get(key) != null) {
            NextGenTextItem btn = NextGenTextItem.getInstance(getContext(),stringid, (AbstractParameter) parms.get(key),color);
            btn.setOnClickListener(manualButtonClickListner);
            manualItemsHolder.addView(btn);
            buttonHashMap.put(key,btn);
            supportedManuals.add(key);
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
                    currentButton.getParameter().removeOnPropertyChangedCallback(selectedParameterObserver);
                    currentButton.SetActive(false);
                }
                //set the returned view as active and fill seekbar
                currentButton = (ManualButtonInterface) v;
                currentButton.SetActive(true);
                keyPressedController.setActiveKey(getKeyFromButton(currentButton));
                currentButton.getParameter().addOnPropertyChangedCallback(selectedParameterObserver);

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

    private SettingKeys.Key getKeyFromButton(ManualButtonInterface button)
    {
        for (Map.Entry entry : buttonHashMap.entrySet())
        {
            if (entry.getValue() == button)
                return (SettingKeys.Key) entry.getKey();
        }
        return SettingKeys.M_Zoom;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        Log.d(TAG, "onProgressChanged:" + progress);
        currentValuePos = progress;
        try {
            currentButton.setValueToParameters(progress);
        }
        catch (NullPointerException ex)
        {}

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        currentButton.setValueToParameters(currentValuePos);
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
        pagingViewTouchState.setTouchEnable(false);
    }

    @Override
    public void onTouchEnd() {
        pagingViewTouchState.setTouchEnable(true);
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

    private KeyPressedController.ManualModeChangedEvent manualModeChangedEvent = new KeyPressedController.ManualModeChangedEvent() {
        @Override
        public void onManualModeChanged(SettingKeys.Key key) {
            ManualButtonInterface button = buttonHashMap.get(key);
            if (button != null)
                manualButtonClickListner.onClick((View) button);
        }
    };
}
