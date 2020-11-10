package com.troop.freedcam.cameraui.viewmodels;

import android.view.MotionEvent;
import android.view.View;

import androidx.lifecycle.ViewModel;

import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.parameters.AbstractParameter;
import com.troop.freedcam.camera.basecamera.parameters.ParameterInterface;
import com.troop.freedcam.cameraui.models.ButtonModel;
import com.troop.freedcam.cameraui.models.CloseAppButtonModel;
import com.troop.freedcam.cameraui.models.ManualButtonModel;
import com.troop.freedcam.cameraui.models.ManualControlsHolderModel;
import com.troop.freedcam.cameraui.models.RotatingSeekbarModel;
import com.troop.freedcam.cameraui.models.ShutterButtonModel;
import com.troop.freedcam.cameraui.service.I_swipe;
import com.troop.freedcam.cameraui.service.SwipeMenuListner;
import com.troop.freedcam.camera.events.CameraStateEvents;
import com.troop.freedcam.eventbus.EventBusHelper;
import com.troop.freedcam.settings.SettingKeys;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

public class CameraUiViewModel extends ViewModel implements I_swipe {



    public enum ManualButtons
    {
        zoom,
        focus,
        iso,
        shutter,
        fnum,
        shift,
        ev,
        saturation,
        sharpness,
        tonecurve,
        wb,
        contrast,
        fx,
        burst,
    }

    public enum LeftbarButtons
    {
        histogram,
        clipping,
        wb,
        iso,
        flash,
        focus,
        exposure,
        aepriority,
        contshot,
        hdr,
        night,
        imageformat,
    }

    public enum RightbarButtons
    {
        close,
        module,
        focuspeak,
        selftimer,
        aelock,
        cameraswitch,
    }

    @Subscribe
    public void onCameraOpenFinishEvent(CameraStateEvents.CameraOpenFinishEvent cameraOpenFinishEvent)
    {
        /*
        zoom,
        focus,
        iso,
        shutter,
        fnum,
        shift,
        ev,
        saturation,
        sharpness,
        tonecurve,
        wb,
        contrast,
        fx,
        burst,
         */
        CameraControllerInterface cameraControllerInterface = cameraOpenFinishEvent.cameraControllerInterface;
        setParameterToManualButton(cameraControllerInterface,SettingKeys.M_Zoom,ManualButtons.zoom);
        setParameterToManualButton(cameraControllerInterface,SettingKeys.M_Focus,ManualButtons.focus);
        setParameterToManualButton(cameraControllerInterface,SettingKeys.M_ManualIso,ManualButtons.iso);
        setParameterToManualButton(cameraControllerInterface,SettingKeys.M_ExposureTime,ManualButtons.shutter);
        setParameterToManualButton(cameraControllerInterface,SettingKeys.M_Fnumber,ManualButtons.fnum);
        setParameterToManualButton(cameraControllerInterface,SettingKeys.M_ProgramShift,ManualButtons.shift);
        setParameterToManualButton(cameraControllerInterface,SettingKeys.M_ExposureCompensation,ManualButtons.ev);
        setParameterToManualButton(cameraControllerInterface,SettingKeys.M_Saturation,ManualButtons.saturation);
        setParameterToManualButton(cameraControllerInterface,SettingKeys.M_Sharpness,ManualButtons.sharpness);
        setParameterToManualButton(cameraControllerInterface,SettingKeys.TONE_MAP_MODE,ManualButtons.tonecurve);
        setParameterToManualButton(cameraControllerInterface,SettingKeys.M_Whitebalance,ManualButtons.wb);
        setParameterToManualButton(cameraControllerInterface,SettingKeys.M_Contrast,ManualButtons.contrast);
        setParameterToManualButton(cameraControllerInterface,SettingKeys.M_FX,ManualButtons.fx);
        setParameterToManualButton(cameraControllerInterface,SettingKeys.M_Burst,ManualButtons.burst);

        setParameterToLeftBarButtons(cameraControllerInterface,SettingKeys.HISTOGRAM, LeftbarButtons.histogram);
        setParameterToLeftBarButtons(cameraControllerInterface,SettingKeys.CLIPPING, LeftbarButtons.clipping);
        setParameterToLeftBarButtons(cameraControllerInterface,SettingKeys.WhiteBalanceMode, LeftbarButtons.wb);
        setParameterToLeftBarButtons(cameraControllerInterface,SettingKeys.IsoMode, LeftbarButtons.iso);
        setParameterToLeftBarButtons(cameraControllerInterface,SettingKeys.FlashMode, LeftbarButtons.flash);
        setParameterToLeftBarButtons(cameraControllerInterface,SettingKeys.FocusMode, LeftbarButtons.focus);
        setParameterToLeftBarButtons(cameraControllerInterface,SettingKeys.ExposureMode, LeftbarButtons.exposure);
        setParameterToLeftBarButtons(cameraControllerInterface,SettingKeys.AE_PriorityMode, LeftbarButtons.aepriority);
        setParameterToLeftBarButtons(cameraControllerInterface,SettingKeys.ContShootMode, LeftbarButtons.contshot);
        setParameterToLeftBarButtons(cameraControllerInterface,SettingKeys.HDRMode, LeftbarButtons.hdr);
        setParameterToLeftBarButtons(cameraControllerInterface,SettingKeys.NightMode, LeftbarButtons.night);
        setParameterToLeftBarButtons(cameraControllerInterface,SettingKeys.PictureFormat, LeftbarButtons.imageformat);

    }

    private void setParameterToManualButton(CameraControllerInterface cameraControllerInterface, SettingKeys.Key key, ManualButtons buttons)
    {
        ParameterInterface parameterInterface = cameraControllerInterface.getParameterHandler().get(key);
        if (parameterInterface != null)
            manualButtonModelHashMap.get(buttons).setParameterInterface(parameterInterface);
    }

    private void setParameterToLeftBarButtons(CameraControllerInterface cameraControllerInterface, SettingKeys.Key key, LeftbarButtons buttons)
    {
        ParameterInterface parameterInterface = cameraControllerInterface.getParameterHandler().get(key);
        if (parameterInterface != null)
            leftbarButtonsManualButtonModelHashMap.get(buttons).setParameterInterface(parameterInterface);
    }


    public View.OnTouchListener onTouchListener = new View.OnTouchListener()
    {
        public boolean onTouch(View v, MotionEvent event)
        {
            return touchHandler.onTouchEvent(event);
        }

    };

    private HashMap<ManualButtons, ManualButtonModel> manualButtonModelHashMap;
    private HashMap<LeftbarButtons, ButtonModel> leftbarButtonsManualButtonModelHashMap;
    private HashMap<RightbarButtons, ButtonModel> rightbarButtonsManualButtonModelHashMap;
    private ManualControlsHolderModel manualControlsHolder;
    private RotatingSeekbarModel seekBarModel;
    private ShutterButtonModel shutterButtonModel;
    private SwipeMenuListner touchHandler;

    public CameraUiViewModel() {
        seekBarModel = new RotatingSeekbarModel();
        manualControlsHolder = new ManualControlsHolderModel(seekBarModel);


        manualButtonModelHashMap = new HashMap<>();
        ManualButtons buttons[] = ManualButtons.values();
        for (ManualButtons b : buttons) {
            ManualButtonModel m =  new ManualButtonModel(manualControlsHolder);
            EventBusHelper.register(this);
            manualButtonModelHashMap.put(b, m);
        }
        leftbarButtonsManualButtonModelHashMap = new HashMap<>();
        LeftbarButtons leftbarButtons[] = LeftbarButtons.values();
        for (LeftbarButtons buttons1: leftbarButtons)
            leftbarButtonsManualButtonModelHashMap.put(buttons1, new ManualButtonModel(manualControlsHolder));

        rightbarButtonsManualButtonModelHashMap = new HashMap<>();
        RightbarButtons rightbarButtons[] = RightbarButtons.values();
        for (RightbarButtons buttons1: rightbarButtons) {
            if (buttons1 != RightbarButtons.close)
                rightbarButtonsManualButtonModelHashMap.put(buttons1, new ManualButtonModel(manualControlsHolder));
        }
        rightbarButtonsManualButtonModelHashMap.put(RightbarButtons.close,new CloseAppButtonModel());

        shutterButtonModel = new ShutterButtonModel();
        touchHandler = new SwipeMenuListner(this);
    }

    public ManualButtonModel getManualButtonModel(ManualButtons manualButtons)
    {
        return manualButtonModelHashMap.get(manualButtons);
    }

    public ButtonModel getManualButtonModel(LeftbarButtons manualButtons)
    {
        return leftbarButtonsManualButtonModelHashMap.get(manualButtons);
    }

    public ButtonModel getManualButtonModel(RightbarButtons manualButtons)
    {
        return rightbarButtonsManualButtonModelHashMap.get(manualButtons);
    }



    public ManualControlsHolderModel getManualControlsHolderModel() {
        return manualControlsHolder;
    }

    public RotatingSeekbarModel getSeekBarModel() {
        return seekBarModel;
    }

    public ShutterButtonModel getShutterButtonModel() {
        return shutterButtonModel;
    }

    @Override
    public void doLeftToRightSwipe() {

    }

    @Override
    public void doRightToLeftSwipe() {

    }

    @Override
    public void doTopToBottomSwipe() {
        manualControlsHolder.setVisibility(View.GONE);
        seekBarModel.setVisibility(View.GONE);
    }

    @Override
    public void doBottomToTopSwipe() {
        manualControlsHolder.setVisibility(View.VISIBLE);
        if (seekBarModel.getManualButtonModel() != null)
            seekBarModel.setVisibility(View.VISIBLE);
        else
            seekBarModel.setVisibility(View.GONE);
    }

    @Override
    public void onClick(int x, int y) {

    }

    @Override
    public void onMotionEvent(MotionEvent event) {

    }

}