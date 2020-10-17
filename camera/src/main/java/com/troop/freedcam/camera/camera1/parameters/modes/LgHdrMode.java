package com.troop.freedcam.camera.camera1.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.R;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.camera1.parameters.ParametersHandler;
import freed.cam.events.ValueChangedEvent;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.settings.mode.SettingMode;

/**
 * Created by troop on 24.02.2017.
 */

public class LgHdrMode extends BaseModeParameter
{
    final String TAG = LgHdrMode.class.getSimpleName();
    private boolean visible = true;
    private boolean supportauto;
    private boolean supporton;
    private String state = "";
    private String format = "";
    private String curmodule = "";

    public LgHdrMode(Camera.Parameters parameters, CameraControllerInterface cameraUiWrapper, SettingKeys.Key settingMode) {
        super(parameters, cameraUiWrapper, settingMode);
    }

    @Override
    public void setValue(String valueToSet, boolean setToCam) {

        if (valueToSet.equals(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_)))
            parameters.set(ContextApplication.getStringFromRessources(R.string.hdr_mode), 1);
        else if (valueToSet.equals(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_)))
            parameters.set(ContextApplication.getStringFromRessources(R.string.hdr_mode), 0);
        else if (valueToSet.equals(ContextApplication.getStringFromRessources(R.string.auto_)))
            parameters.set(ContextApplication.getStringFromRessources(R.string.hdr_mode), 2);
        if (setToCam)
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        ((SettingMode)SettingsManager.get(key)).set(valueToSet);
        fireStringValueChanged(valueToSet);
    }

    @Override
    public String GetStringValue() {
            if (parameters.get(ContextApplication.getStringFromRessources(R.string.hdr_mode))== null)
                parameters.set(ContextApplication.getStringFromRessources(R.string.hdr_mode), "0");
            if (parameters.get(ContextApplication.getStringFromRessources(R.string.hdr_mode)).equals("0"))
                return ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_);
            else if (parameters.get(ContextApplication.getStringFromRessources(R.string.hdr_mode)).equals("1"))
                return ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_);
            else
                return ContextApplication.getStringFromRessources(R.string.auto_);

    }

    @Override
    public String[] getStringValues() {
        List<String> hdrVals =  new ArrayList<>();
        hdrVals.add(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_));
        hdrVals.add(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_));
        hdrVals.add(ContextApplication.getStringFromRessources(R.string.auto_));
        return hdrVals.toArray(new String[hdrVals.size()]);
    }

    @Override
    public void onModuleChanged(String module)
    {
        curmodule = module;
        if (curmodule.equals(ContextApplication.getStringFromRessources(R.string.module_video))|| curmodule.equals(ContextApplication.getStringFromRessources(R.string.module_video)))
        {
            Hide();
            SetValue(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_),true);
        }
        else
        {
            if (format.contains(ContextApplication.getStringFromRessources(R.string.jpeg_))) {
                Show();
                setViewState(ViewState.Visible);
            }
            else
            {
                Hide();
                SetValue(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_),true);
            }
        }
    }

    @Subscribe
    public void onPictureFormatChanged(ValueChangedEvent<String> valueChangedEvent)
    {
        if (valueChangedEvent.key == SettingKeys.PictureFormat) {
            format = valueChangedEvent.newValue;
            if (format.contains(ContextApplication.getStringFromRessources(R.string.jpeg_)) && !visible && !curmodule.equals(ContextApplication.getStringFromRessources(R.string.module_hdr)))
                Show();

            else if (!format.contains(ContextApplication.getStringFromRessources(R.string.jpeg_)) && visible) {
                Hide();
            }
        }
    }

    private void Hide()
    {
        state = GetStringValue();
        visible = false;
        SetValue(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_),true);
        fireStringValueChanged(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_));
        setViewState(ViewState.Hidden);
    }
    private void Show()
    {
        visible = true;
        SetValue(state,true);
        fireStringValueChanged(state);
        setViewState(ViewState.Visible);
    }

}
