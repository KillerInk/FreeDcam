package freed.cam.ui.themesample.cameraui.childs;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.troop.freedcam.R;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.settings.mode.SettingMode;

@AndroidEntryPoint
public class UiSettingsChildSelfTimer extends UiSettingsChild {

    @Inject
    SettingsManager settingsManager;

    public UiSettingsChildSelfTimer(Context context) {
        super(context);
    }

    public UiSettingsChildSelfTimer(Context context, SettingMode settingsMode, AbstractParameter parameter) {
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

        //onStringValueChanged(settingMode.get());
    }

    @Override
    public String[] GetValues() {
        return settingsManager.get(SettingKeys.selfTimer).getValues();
    }

    @Override
    public void SetValue(String value) {
        settingsManager.get(SettingKeys.selfTimer).set(value);
        //onStringValueChanged(value);
    }
}
