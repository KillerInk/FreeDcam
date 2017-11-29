package freed.cam.ui.themesample.settings.childs;

import android.content.Context;

import com.troop.freedcam.R;

import freed.settings.AppSettingsManager;

/**
 * Created by troop on 26.06.2017.
 */

public class SettingsChildMenuForceRawToDng extends SettingsChildMenu {

    public SettingsChildMenuForceRawToDng(Context context, int headerid, int descriptionid) {
        super(context, headerid, descriptionid);
        if (AppSettingsManager.getInstance().forceRawToDng.getBoolean())
            SetValue(AppSettingsManager.getInstance().getResString(R.string.on));
        else
            SetValue(AppSettingsManager.getInstance().getResString(R.string.off));
    }

    @Override
    public String[] GetValues() {
        return new String[]{AppSettingsManager.getInstance().getResString(R.string.off), AppSettingsManager.getInstance().getResString(R.string.on)};
    }

    @Override
    public void SetValue(String value) {
        if (value.equals(AppSettingsManager.getInstance().getResString(R.string.off)))
            AppSettingsManager.getInstance().forceRawToDng.setBoolean(false);
        else
            AppSettingsManager.getInstance().forceRawToDng.setBoolean(true);
        valueText.setText(value);
    }


}
