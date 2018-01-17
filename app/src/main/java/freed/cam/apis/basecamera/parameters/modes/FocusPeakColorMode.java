package freed.cam.apis.basecamera.parameters.modes;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.FocuspeakProcessor;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

/**
 * Created by KillerInk on 17.01.2018.
 */

public class FocusPeakColorMode extends AbstractParameter {

    private FocuspeakProcessor focuspeakProcessor;
    public FocusPeakColorMode(FocuspeakProcessor renderScriptManager, SettingKeys.Key settingMode) {
        super(settingMode);
        this.focuspeakProcessor = renderScriptManager;

    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {
        //super.setValue(valueToSet, setToCamera);
        settingMode.set(valueToSet);
        if (valueToSet.equals(SettingsManager.getInstance().getResString(R.string.fcolor_red)))
        {
            focuspeakProcessor.setRed(true);
            focuspeakProcessor.setGreen(false);
            focuspeakProcessor.setBlue(false);
        }
        else if (valueToSet.equals(SettingsManager.getInstance().getResString(R.string.fcolor_green)))
        {
            focuspeakProcessor.setRed(false);
            focuspeakProcessor.setGreen(true);
            focuspeakProcessor.setBlue(false);
        }
        else if (valueToSet.equals(SettingsManager.getInstance().getResString(R.string.fcolor_blue)))
        {
            focuspeakProcessor.setRed(false);
            focuspeakProcessor.setGreen(false);
            focuspeakProcessor.setBlue(true);
        }
        else if (valueToSet.equals(SettingsManager.getInstance().getResString(R.string.fcolor_white)))
        {
            focuspeakProcessor.setRed(true);
            focuspeakProcessor.setGreen(true);
            focuspeakProcessor.setBlue(true);
        }
        else if (valueToSet.equals(SettingsManager.getInstance().getResString(R.string.fcolor_yellow)))
        {
            focuspeakProcessor.setRed(true);
            focuspeakProcessor.setGreen(true);
            focuspeakProcessor.setBlue(false);
        }
        else if (valueToSet.equals(SettingsManager.getInstance().getResString(R.string.fcolor_magenta)))
        {
            focuspeakProcessor.setRed(true);
            focuspeakProcessor.setGreen(false);
            focuspeakProcessor.setBlue(true);
        }
        else if (valueToSet.equals(SettingsManager.getInstance().getResString(R.string.fcolor_cyan)))
        {
            focuspeakProcessor.setRed(false);
            focuspeakProcessor.setGreen(true);
            focuspeakProcessor.setBlue(true);
        }
        fireStringValueChanged(valueToSet);
    }
}
