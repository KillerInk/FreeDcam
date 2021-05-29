package freed.cam.ui.themesample.settings.childs;

import android.content.Context;

import com.troop.freedcam.R;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

/**
 * Created by troop on 26.06.2017.
 */

@AndroidEntryPoint
public class SettingsChildMenuForceRawToDng extends SettingsChildMenu {

    @Inject
    SettingsManager settingsManager;

    public SettingsChildMenuForceRawToDng(Context context, int headerid, int descriptionid) {
        super(context, headerid, descriptionid);
        if (settingsManager.get(SettingKeys.forceRawToDng).get())
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
            settingsManager.get(SettingKeys.forceRawToDng).set(false);
        else
            settingsManager.get(SettingKeys.forceRawToDng).set(true);
        binding.textviewMenuitemHeaderValue.setText(value);
    }


}
