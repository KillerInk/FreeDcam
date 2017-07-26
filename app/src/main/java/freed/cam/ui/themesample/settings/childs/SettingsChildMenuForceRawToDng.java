package freed.cam.ui.themesample.settings.childs;

import android.content.Context;

import com.troop.freedcam.R;

import freed.utils.AppSettingsManager;

/**
 * Created by troop on 26.06.2017.
 */

public class SettingsChildMenuForceRawToDng extends SettingsChildMenu {
    private AppSettingsManager appSettingsManager;

    public SettingsChildMenuForceRawToDng(Context context, int headerid, int descriptionid, AppSettingsManager appSettingsManager) {
        super(context, headerid, descriptionid);
        this.appSettingsManager = appSettingsManager;
        if (appSettingsManager.forceRawToDng.getBoolean())
            SetValue(appSettingsManager.getResString(R.string.on));
        else
            SetValue(appSettingsManager.getResString(R.string.off));
    }

    @Override
    public String[] GetValues() {
        return new String[]{appSettingsManager.getResString(R.string.off), appSettingsManager.getResString(R.string.on)};
    }

    @Override
    public void SetValue(String value) {
        if (value.equals(appSettingsManager.getResString(R.string.off)))
            appSettingsManager.forceRawToDng.setBoolean(false);
        else
            appSettingsManager.forceRawToDng.setBoolean(true);
        valueText.setText(value);
    }


}
