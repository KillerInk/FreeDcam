package freed.cam.apis.basecamera.parameters.modes;

import java.util.HashMap;

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.dng.ToneMapProfile;
import freed.utils.AppSettingsManager;

/**
 * Created by troop on 10.07.2017.
 */

public class ToneMapChooser extends AbstractParameter {
    private AppSettingsManager appSettingsManager;
    private HashMap<String, ToneMapProfile> toneMapProfileHashMap;
    public ToneMapChooser(HashMap<String, ToneMapProfile> toneMapProfileHashMap, AppSettingsManager appSettingsManager)
    {
        this.appSettingsManager = appSettingsManager;
        this.toneMapProfileHashMap = toneMapProfileHashMap;
        isSupported = true;
        currentString = appSettingsManager.tonemapProfilesSettings.get();
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
        appSettingsManager.tonemapProfilesSettings.set(valueToSet);
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
