package com.troop.freedcam.camera.basecamera.parameters.modes;


import com.troop.freedcam.camera.basecamera.parameters.AbstractParameter;
import com.troop.freedcam.eventbus.events.ValueChangedEvent;
import com.troop.freedcam.language.R;
import com.troop.freedcam.processor.RenderScriptProcessorInterface;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.utils.Log;

import org.greenrobot.eventbus.Subscribe;

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
            if (valueToSet.equals(ContextApplication.getStringFromRessources(R.string.fcolor_red))) {
                focuspeakProcessor.setRed(true);
                focuspeakProcessor.setGreen(false);
                focuspeakProcessor.setBlue(false);
            } else if (valueToSet.equals(ContextApplication.getStringFromRessources(R.string.fcolor_green))) {
                focuspeakProcessor.setRed(false);
                focuspeakProcessor.setGreen(true);
                focuspeakProcessor.setBlue(false);
            } else if (valueToSet.equals(ContextApplication.getStringFromRessources(R.string.fcolor_blue))) {
                focuspeakProcessor.setRed(false);
                focuspeakProcessor.setGreen(false);
                focuspeakProcessor.setBlue(true);
            } else if (valueToSet.equals(ContextApplication.getStringFromRessources(R.string.fcolor_white))) {
                focuspeakProcessor.setRed(true);
                focuspeakProcessor.setGreen(true);
                focuspeakProcessor.setBlue(true);
            } else if (valueToSet.equals(ContextApplication.getStringFromRessources(R.string.fcolor_yellow))) {
                focuspeakProcessor.setRed(true);
                focuspeakProcessor.setGreen(true);
                focuspeakProcessor.setBlue(false);
            } else if (valueToSet.equals(ContextApplication.getStringFromRessources(R.string.fcolor_magenta))) {
                focuspeakProcessor.setRed(true);
                focuspeakProcessor.setGreen(false);
                focuspeakProcessor.setBlue(true);
            } else if (valueToSet.equals(ContextApplication.getStringFromRessources(R.string.fcolor_cyan))) {
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

    /*@Subscribe
    public void onStringValueChanged(ValueChangedEvent<String> valueob)
    {
        if (valueob.key == SettingKeys.EnableRenderScript) {
            String value = valueob.newValue;
            if (value.equals(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_)))
                setViewState(ViewState.Hidden);
            else
                setViewState(ViewState.Visible);
        }
    }*/
}
