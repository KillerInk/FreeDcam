package freed.cam.ui.themesample.settings.childs;

import android.content.Context;

import com.troop.freedcam.R;

import freed.settings.Settings;
import freed.settings.SettingsManager;

/**
 * Created by troop on 26.06.2017.
 */

public class SettingsChildMenuForceRawToDng extends SettingsChildMenu {

    public SettingsChildMenuForceRawToDng(Context context, int headerid, int descriptionid) {
        super(context, headerid, descriptionid);
        if (SettingsManager.get(Settings.forceRawToDng).getBoolean())
            SetValue(SettingsManager.getInstance().getResString(R.string.on));
        else
            SetValue(SettingsManager.getInstance().getResString(R.string.off));
    }

    @Override
    public String[] GetValues() {
        return new String[]{SettingsManager.getInstance().getResString(R.string.off), SettingsManager.getInstance().getResString(R.string.on)};
    }

    @Override
    public void SetValue(String value) {
        if (value.equals(SettingsManager.getInstance().getResString(R.string.off)))
            SettingsManager.get(Settings.forceRawToDng).setBoolean(false);
        else
            SettingsManager.get(Settings.forceRawToDng).setBoolean(true);
        valueText.setText(value);
    }


}
