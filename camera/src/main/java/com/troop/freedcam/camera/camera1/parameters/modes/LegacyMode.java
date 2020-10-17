package com.troop.freedcam.camera.camera1.parameters.modes;

import android.os.Build;

import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.parameters.AbstractParameter;
import com.troop.freedcam.settings.mode.ApiBooleanSettingMode;
import com.troop.freedcam.settings.mode.BooleanSettingModeInterface;

/**
 * Created by KillerInk on 22.02.2018.
 */

public class LegacyMode extends AbstractParameter implements BooleanSettingModeInterface {

    ApiBooleanSettingMode settingMode;

    public LegacyMode(CameraControllerInterface cameraUiWrapper, ApiBooleanSettingMode settingMode) {
        super(cameraUiWrapper,null);
        this.settingMode = settingMode;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            setViewState(ViewState.Visible);
    }

    @Override
    public boolean get() {
        return settingMode.get();
    }

    @Override
    public void set(boolean bool) {
        settingMode.set(bool);
        cameraUiWrapper.getActivityInterface().runFeatureDetector();
    }
}
