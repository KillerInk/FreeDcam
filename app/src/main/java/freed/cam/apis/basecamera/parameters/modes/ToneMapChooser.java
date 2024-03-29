package freed.cam.apis.basecamera.parameters.modes;

import android.text.TextUtils;

import com.troop.freedcam.R;

import java.util.HashMap;

import freed.FreedApplication;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.dng.ToneMapProfile;
import freed.settings.SettingKeys;

/**
 * Created by troop on 10.07.2017.
 */

public class ToneMapChooser extends AbstractParameter {
    private final HashMap<String, ToneMapProfile> toneMapProfileHashMap;
    public ToneMapChooser(HashMap<String, ToneMapProfile> toneMapProfileHashMap)
    {
        super(SettingKeys.TONEMAP_SET);
        this.toneMapProfileHashMap = toneMapProfileHashMap;
        setViewState(ViewState.Visible);
        currentString = settingsManager.get(SettingKeys.TONEMAP_SET).get();
        if (TextUtils.isEmpty(currentString))
            currentString = FreedApplication.getStringFromRessources(R.string.off_);
    }

    @Override
    public void setStringValue(String valueToSet, boolean setToCamera)
    {
        currentString = valueToSet;
        fireStringValueChanged(currentString);
        settingsManager.get(SettingKeys.TONEMAP_SET).set(valueToSet);
    }

    @Override
    public String[] getStringValues()
    {
        return toneMapProfileHashMap.keySet().toArray(new String[toneMapProfileHashMap.size()]);
    }

    public ToneMapProfile getToneMap()
    {
        return toneMapProfileHashMap.get(currentString);
    }
}
