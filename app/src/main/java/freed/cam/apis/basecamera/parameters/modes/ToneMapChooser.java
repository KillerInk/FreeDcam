package freed.cam.apis.basecamera.parameters.modes;

import android.text.TextUtils;

import com.troop.freedcam.R;

import java.util.HashMap;

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.dng.ToneMapProfile;
import freed.settings.SettingsManager;
import freed.settings.Settings;

/**
 * Created by troop on 10.07.2017.
 */

public class ToneMapChooser extends AbstractParameter {
    private HashMap<String, ToneMapProfile> toneMapProfileHashMap;
    public ToneMapChooser(HashMap<String, ToneMapProfile> toneMapProfileHashMap)
    {
        this.toneMapProfileHashMap = toneMapProfileHashMap;
        isSupported = true;
        currentString = SettingsManager.get(Settings.tonemapChooser).get();
        if (TextUtils.isEmpty(currentString))
            currentString = SettingsManager.getInstance().getResString(R.string.off_);
    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        currentString = valueToSet;
        fireStringValueChanged(currentString);
        SettingsManager.get(Settings.tonemapChooser).set(valueToSet);
    }

    @Override
    public String[] getStringValues()
    {
        return toneMapProfileHashMap.keySet().toArray(new String[toneMapProfileHashMap.size()]);
    }

    @Override
    public boolean IsVisible() {
        return isSupported;
    }

    public ToneMapProfile getToneMap()
    {
        return toneMapProfileHashMap.get(currentString);
    }
}
