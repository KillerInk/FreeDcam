package freed.cam.apis.basecamera.parameters.modes;

import androidx.databinding.Observable;

import com.troop.freedcam.R;

import org.greenrobot.eventbus.Subscribe;

import freed.FreedApplication;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.events.ValueChangedEvent;
import freed.cam.previewpostprocessing.Preview;
import freed.settings.SettingKeys;
import freed.utils.Log;

/**
 * Created by KillerInk on 17.01.2018.
 */

public class FocusPeakColorMode extends AbstractParameter {

    private Preview focuspeakProcessor;
    public FocusPeakColorMode(Preview renderScriptManager, SettingKeys.Key settingMode) {
        super(settingMode);
        this.focuspeakProcessor = renderScriptManager;
        setStringValue(getStringValue(),false);
    }

    @Override
    public void setStringValue(String valueToSet, boolean setToCamera) {
        if (focuspeakProcessor == null)
            return;
        try {
            settingsManager.getGlobal(SettingKeys.FOCUSPEAK_COLOR).set(valueToSet);
            if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.fcolor_red))) {
                focuspeakProcessor.setRed(true);
                focuspeakProcessor.setGreen(false);
                focuspeakProcessor.setBlue(false);
            } else if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.fcolor_green))) {
                focuspeakProcessor.setRed(false);
                focuspeakProcessor.setGreen(true);
                focuspeakProcessor.setBlue(false);
            } else if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.fcolor_blue))) {
                focuspeakProcessor.setRed(false);
                focuspeakProcessor.setGreen(false);
                focuspeakProcessor.setBlue(true);
            } else if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.fcolor_white))) {
                focuspeakProcessor.setRed(true);
                focuspeakProcessor.setGreen(true);
                focuspeakProcessor.setBlue(true);
            } else if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.fcolor_yellow))) {
                focuspeakProcessor.setRed(true);
                focuspeakProcessor.setGreen(true);
                focuspeakProcessor.setBlue(false);
            } else if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.fcolor_magenta))) {
                focuspeakProcessor.setRed(true);
                focuspeakProcessor.setGreen(false);
                focuspeakProcessor.setBlue(true);
            } else if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.fcolor_cyan))) {
                focuspeakProcessor.setRed(false);
                focuspeakProcessor.setGreen(true);
                focuspeakProcessor.setBlue(true);
            }
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }
        fireStringValueChanged(valueToSet);
    }

    @Override
    public String[] getStringValues() {
        return settingsManager.getGlobal(SettingKeys.FOCUSPEAK_COLOR).getValues();
    }

    @Override
    public String getStringValue() {
        return settingsManager.getGlobal(SettingKeys.FOCUSPEAK_COLOR).get();
    }

}
