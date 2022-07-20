package freed.cam.apis.basecamera.parameters.modes;

import java.util.Arrays;

import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.SettingKeys;
import freed.utils.Log;

public class ThemeMode extends AbstractParameter {

    private static final String TAG = ThemeMode.class.getSimpleName();

    public ThemeMode(SettingKeys.Key key) {
        super(key);

        stringvalues = settingsManager.getGlobal(SettingKeys.THEME).getValues();
        stringvalues = settingsManager.get(SettingKeys.THEME).getValues();
        Log.d(TAG,"values:" + Arrays.toString(stringvalues));
    }

    @Override
    protected void setValue(String valueToSet, boolean setToCamera) {
        super.setValue(valueToSet, setToCamera);
        ActivityFreeDcamMain.themeManager().changeTheme(valueToSet);
    }

    @Override
    public ViewState getViewState() {
        return ViewState.Visible;
    }
}
