package freed.cam.ui.themesample.settings.childs;

import android.content.Context;
import android.widget.CompoundButton;

import freed.cam.apis.basecamera.CameraThreadHandler;
import freed.settings.mode.BooleanSettingModeInterface;

public class SettingsChild_FreedAe extends SettingsChild_BooleanSetting {
    public SettingsChild_FreedAe(Context context, BooleanSettingModeInterface booleanSettingMode, int headerid, int descriptionid) {
        super(context, booleanSettingMode, headerid, descriptionid);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        super.onCheckedChanged(buttonView, isChecked);
        if(!buttonView.isPressed()) {
            return;
        }
        CameraThreadHandler.restartCameraAsync();
    }
}
