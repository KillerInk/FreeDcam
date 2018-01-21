package freed.cam.apis.basecamera.parameters.modes;


import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.renderscript.RenderScriptManager;
import freed.settings.mode.ApiBooleanSettingMode;
import freed.settings.mode.BooleanSettingModeInterface;

public class EnableRenderScriptMode extends FocusPeakMode implements BooleanSettingModeInterface {

    private ApiBooleanSettingMode settingMode;

    public EnableRenderScriptMode(CameraWrapperInterface cameraUiWrapper, ApiBooleanSettingMode settingMode) {
        super(cameraUiWrapper);
        this.settingMode = settingMode;
    }

    @Override
    public boolean IsSupported()
    {
        return RenderScriptManager.isSupported() && cameraUiWrapper.getRenderScriptManager().isSucessfullLoaded();
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.equals(cameraUiWrapper.getResString(R.string.on_)))
        {
            settingMode.set(true);
            fireStringValueChanged(cameraUiWrapper.getResString(R.string.on_));
        }
        else {
            settingMode.set(false);
            fireStringValueChanged(cameraUiWrapper.getResString(R.string.off_));
        }
        cameraUiWrapper.restartCameraAsync();

    }

    @Override
    public boolean get() {
        return settingMode.get();
    }

    @Override
    public void set(boolean bool) {
        if (bool)
        {
            fireStringValueChanged(cameraUiWrapper.getResString(R.string.on_));
        }
        else
            fireStringValueChanged(cameraUiWrapper.getResString(R.string.off_));
        settingMode.set(bool);
        cameraUiWrapper.restartCameraAsync();
    }
}
