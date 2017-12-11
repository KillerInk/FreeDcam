package freed.cam.ui.themesample.cameraui.childs;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.settings.SettingsManager;

public class UiSettingsChildSelfTimer extends UiSettingsChild {
    public UiSettingsChildSelfTimer(Context context) {
        super(context);
    }

    public UiSettingsChildSelfTimer(Context context, SettingsManager.SettingMode settingsMode, ParameterInterface parameter) {
        super(context, settingsMode, parameter);
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

    @Override
    public void SetStuff(SettingsManager.SettingMode settingMode) {
        super.SetStuff(settingMode);
        onStringValueChanged(settingMode.get());
    }

    @Override
    public String[] GetValues() {
        return settingMode.getValues();
    }

    @Override
    public void SetValue(String value) {
        settingMode.set(value);
        onStringValueChanged(value);
    }
}
