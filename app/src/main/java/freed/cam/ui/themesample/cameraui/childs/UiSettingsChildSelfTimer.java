package freed.cam.ui.themesample.cameraui.childs;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.basecamera.parameters.ParameterInterface;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.settings.mode.SettingMode;

public class UiSettingsChildSelfTimer extends UiSettingsChild {
    public UiSettingsChildSelfTimer(Context context) {
        super(context);
    }

    public UiSettingsChildSelfTimer(Context context, SettingMode settingsMode, ParameterInterface parameter) {
        super(context, parameter);
    }

    public UiSettingsChildSelfTimer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onModuleChanged(String module)
    {
        if ((module.equals(getResources().getString(R.string.module_picture))
                || module.equals(getResources().getString(R.string.module_hdr))
                || module.equals(getResources().getString(R.string.module_interval))
                || module.equals(getResources().getString(R.string.module_afbracket)))
                )
            setVisibility(View.VISIBLE);
        else
            setVisibility(View.GONE);
    }

    public void SetStuff(SettingMode settingMode) {

        onStringValueChanged(settingMode.get());
    }

    @Override
    public String[] GetValues() {
        return SettingsManager.get(SettingKeys.selfTimer).getValues();
    }

    @Override
    public void SetValue(String value) {
        SettingsManager.get(SettingKeys.selfTimer).set(value);
        onStringValueChanged(value);
    }
}
