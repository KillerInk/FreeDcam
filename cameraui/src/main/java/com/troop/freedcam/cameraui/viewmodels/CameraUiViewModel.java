package com.troop.freedcam.cameraui.viewmodels;

import android.view.MotionEvent;
import android.view.View;

import androidx.lifecycle.ViewModel;

import com.troop.freedcam.cameraui.models.CloseAppButtonModel;
import com.troop.freedcam.cameraui.models.ManualButtonModel;
import com.troop.freedcam.cameraui.models.ManualControlsHolderModel;
import com.troop.freedcam.cameraui.models.RotatingSeekbarModel;
import com.troop.freedcam.cameraui.models.ShutterButtonModel;
import com.troop.freedcam.cameraui.models.VisibilityEnableModel;
import com.troop.freedcam.cameraui.service.I_swipe;
import com.troop.freedcam.cameraui.service.SwipeMenuListner;
import com.troop.freedcam.eventbus.events.CameraStateEvents;

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

    public View.OnTouchListener onTouchListener = new View.OnTouchListener()
    {
        public boolean onTouch(View v, MotionEvent event)
        {
            return touchHandler.onTouchEvent(event);
        }

    };

    private HashMap<ManualButtons, ManualButtonModel> manualButtonModelHashMap;
    private HashMap<LeftbarButtons, ManualButtonModel> leftbarButtonsManualButtonModelHashMap;
    private HashMap<RightbarButtons, ManualButtonModel> rightbarButtonsManualButtonModelHashMap;
    private ManualControlsHolderModel manualControlsHolder;
    private RotatingSeekbarModel seekBarModel;
    private ShutterButtonModel shutterButtonModel;
    private SwipeMenuListner touchHandler;

    public CameraUiViewModel() {
        seekBarModel = new RotatingSeekbarModel();
        manualControlsHolder = new ManualControlsHolderModel(seekBarModel);


        manualButtonModelHashMap = new HashMap<>();
        ManualButtons buttons[] = ManualButtons.values();
        for (ManualButtons b : buttons)
            manualButtonModelHashMap.put(b, new ManualButtonModel(manualControlsHolder));
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

    public ManualButtonModel getManualButtonModel(LeftbarButtons manualButtons)
    {
        return leftbarButtonsManualButtonModelHashMap.get(manualButtons);
    }

    public ManualButtonModel getManualButtonModel(RightbarButtons manualButtons)
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

    @Subscribe
    public void onCameraOpen(CameraStateEvents.CameraOpenFinishEvent cameraStateEvents)
    {

    }
}