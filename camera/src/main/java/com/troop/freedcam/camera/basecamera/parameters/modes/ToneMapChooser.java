package com.troop.freedcam.camera.basecamera.parameters.modes;

import android.text.TextUtils;

import com.troop.freedcam.camera.basecamera.parameters.AbstractParameter;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.utils.ContextApplication;

import java.util.HashMap;

import freed.dng.ToneMapProfile;

/**
 * Created by troop on 10.07.2017.
 */

public class ToneMapChooser extends AbstractParameter {
    private HashMap<String, ToneMapProfile> toneMapProfileHashMap;
    public ToneMapChooser(HashMap<String, ToneMapProfile> toneMapProfileHashMap)
    {
        super(SettingKeys.TONEMAP_SET);
        this.toneMapProfileHashMap = toneMapProfileHashMap;
        setViewState(ViewState.Visible);
        currentString = SettingsManager.get(SettingKeys.TONEMAP_SET).get();
        if (TextUtils.isEmpty(currentString))
            currentString = ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        currentString = valueToSet;
        fireStringValueChanged(currentString);
        SettingsManager.get(SettingKeys.TONEMAP_SET).set(valueToSet);
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
