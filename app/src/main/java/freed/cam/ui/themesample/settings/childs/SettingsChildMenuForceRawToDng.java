package freed.cam.ui.themesample.settings.childs;

import android.content.Context;

import com.troop.freedcam.R;

import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

/**
 * Created by troop on 26.06.2017.
 */

public class SettingsChildMenuForceRawToDng extends SettingsChildMenu {

    public SettingsChildMenuForceRawToDng(Context context, int headerid, int descriptionid) {
        super(context, headerid, descriptionid);
        if (SettingsManager.get(SettingKeys.forceRawToDng).get())
            SetValue(getContext().getResources().getString(R.string.on));
        else
            SetValue(getContext().getResources().getString(R.string.off));
    }

    @Override
    public String[] GetValues() {
        return new String[]{getContext().getResources().getString(R.string.off), getContext().getResources().getString(R.string.on)};
    }

    @Override
    public void SetValue(String value) {
        if (value.equals(getContext().getResources().getString(R.string.off)))
            SettingsManager.get(SettingKeys.forceRawToDng).set(false);
        else
            SettingsManager.get(SettingKeys.forceRawToDng).set(true);
        valueText.setText(value);
    }


}
