package freed.cam.apis.basecamera.parameters.modes;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterEvents;
import freed.renderscript.RenderScriptProcessorInterface;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by KillerInk on 17.01.2018.
 */

public class FocusPeakColorMode extends AbstractParameter implements ParameterEvents {

    private RenderScriptProcessorInterface focuspeakProcessor;
    public FocusPeakColorMode(RenderScriptProcessorInterface renderScriptManager, SettingKeys.Key settingMode) {
        super(settingMode);
        this.focuspeakProcessor = renderScriptManager;

    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {
        if (focuspeakProcessor == null)
            return;
        try {
            settingMode.set(valueToSet);
            if (valueToSet.equals(SettingsManager.getInstance().getResString(R.string.fcolor_red))) {
                focuspeakProcessor.setRed(true);
                focuspeakProcessor.setGreen(false);
                focuspeakProcessor.setBlue(false);
            } else if (valueToSet.equals(SettingsManager.getInstance().getResString(R.string.fcolor_green))) {
                focuspeakProcessor.setRed(false);
                focuspeakProcessor.setGreen(true);
                focuspeakProcessor.setBlue(false);
            } else if (valueToSet.equals(SettingsManager.getInstance().getResString(R.string.fcolor_blue))) {
                focuspeakProcessor.setRed(false);
                focuspeakProcessor.setGreen(false);
                focuspeakProcessor.setBlue(true);
            } else if (valueToSet.equals(SettingsManager.getInstance().getResString(R.string.fcolor_white))) {
                focuspeakProcessor.setRed(true);
                focuspeakProcessor.setGreen(true);
                focuspeakProcessor.setBlue(true);
            } else if (valueToSet.equals(SettingsManager.getInstance().getResString(R.string.fcolor_yellow))) {
                focuspeakProcessor.setRed(true);
                focuspeakProcessor.setGreen(true);
                focuspeakProcessor.setBlue(false);
            } else if (valueToSet.equals(SettingsManager.getInstance().getResString(R.string.fcolor_magenta))) {
                focuspeakProcessor.setRed(true);
                focuspeakProcessor.setGreen(false);
                focuspeakProcessor.setBlue(true);
            } else if (valueToSet.equals(SettingsManager.getInstance().getResString(R.string.fcolor_cyan))) {
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
    public void onViewStateChanged(ViewState value) {

    }

    @Override
    public void onIntValueChanged(int current) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public void onStringValueChanged(String value) {
        if (value.equals(SettingsManager.getInstance().getResString(R.string.off_)))
            setViewState(ViewState.Hidden);
        else
            setViewState(ViewState.Visible);
    }
}
