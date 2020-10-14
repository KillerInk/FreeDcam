package freed.cam.apis.basecamera.parameters.modes;

import com.troop.freedcam.R;

import org.greenrobot.eventbus.Subscribe;

import freed.FreedApplication;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.events.ValueChangedEvent;
import freed.renderscript.RenderScriptProcessorInterface;
import freed.settings.SettingKeys;
import com.troop.freedcam.logger.Log;

/**
 * Created by KillerInk on 17.01.2018.
 */

public class FocusPeakColorMode extends AbstractParameter {

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

    @Subscribe
    public void onStringValueChanged(ValueChangedEvent<String> valueob)
    {
        if (valueob.key == SettingKeys.EnableRenderScript) {
            String value = valueob.newValue;
            if (value.equals(FreedApplication.getStringFromRessources(R.string.off_)))
                setViewState(ViewState.Hidden);
            else
                setViewState(ViewState.Visible);
        }
    }
}
