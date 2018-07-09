package freed.cam.apis.camera1.parameters.modes;

import android.os.Build;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.mode.ApiBooleanSettingMode;
import freed.settings.mode.BooleanSettingModeInterface;

/**
 * Created by KillerInk on 22.02.2018.
 */

public class LegacyMode extends AbstractParameter implements BooleanSettingModeInterface {

    ApiBooleanSettingMode settingMode;

    public LegacyMode(CameraWrapperInterface cameraUiWrapper,  ApiBooleanSettingMode settingMode) {
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
