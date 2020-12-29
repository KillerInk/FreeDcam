package freed.cam.ui.themesample.settings.childs;

import android.content.Context;
import android.widget.CompoundButton;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.mode.BooleanSettingModeInterface;

public class SettingsChild_SwitchAspectRatio extends SettingsChild_BooleanSetting {

    private CameraWrapperInterface cameraWrapperInterface;

    public SettingsChild_SwitchAspectRatio(Context context, CameraWrapperInterface cameraWrapperInterface, BooleanSettingModeInterface booleanSettingMode, int headerid, int descriptionid) {
        super(context, booleanSettingMode, headerid, descriptionid);
        this.cameraWrapperInterface = cameraWrapperInterface;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        super.onCheckedChanged(buttonView, isChecked);
        cameraWrapperInterface.restartPreviewAsync();
    }
}
