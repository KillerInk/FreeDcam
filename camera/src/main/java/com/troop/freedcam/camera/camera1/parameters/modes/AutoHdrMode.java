package com.troop.freedcam.camera.camera1.parameters.modes;

import android.hardware.Camera;
import android.text.TextUtils;

import com.troop.freedcam.camera.R;
import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.camera1.parameters.ParametersHandler;
import com.troop.freedcam.eventbus.events.ValueChangedEvent;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.settings.mode.SettingMode;
import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.utils.Log;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by troop on 24.02.2017.
 */

public class AutoHdrMode extends BaseModeParameter {

    final String TAG = AutoHdrMode.class.getSimpleName();
    private boolean visible = true;
    private String state = "";
    private String format = "";
    private String curmodule = "";
    public AutoHdrMode(Camera.Parameters parameters, CameraControllerInterface cameraUiWrapper, SettingKeys.Key  settingMode) {
        super(parameters, cameraUiWrapper, settingMode);

        if (parameters.get(ContextApplication.getStringFromRessources(R.string.auto_hdr_supported))!=null)
            setViewState(ViewState.Hidden);
        String autohdr = parameters.get(ContextApplication.getStringFromRessources(R.string.auto_hdr_supported));
        if (autohdr != null && !TextUtils.isEmpty(autohdr) && autohdr.equals(ContextApplication.getStringFromRessources(R.string.true_))
                && parameters.get(ContextApplication.getStringFromRessources(R.string.auto_hdr_enable)) != null) {

            List<String> Scenes = new ArrayList<>(Arrays.asList(parameters.get(ContextApplication.getStringFromRessources(R.string.scene_mode_values)).split(",")));
            if (Scenes.contains(ContextApplication.getStringFromRessources(R.string.scene_mode_hdr))) {
                setViewState(ViewState.Visible);
            }
            if (Scenes.contains(ContextApplication.getStringFromRessources(R.string.scene_mode_asd))) {
                setViewState(ViewState.Visible);
            }

        }
        else
            setViewState(ViewState.Hidden);

    }

    @Override
    public void setValue(String valueToSet, boolean setToCam) {
        if (valueToSet.equals(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_)))
        {
            parameters.set(ContextApplication.getStringFromRessources(R.string.scene_mode), ContextApplication.getStringFromRessources(R.string.auto));
            parameters.set(ContextApplication.getStringFromRessources(R.string.auto_hdr_enable), ContextApplication.getStringFromRessources(R.string.disable_));

        }
        else if (valueToSet.equals(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_)))
        {
            parameters.set(ContextApplication.getStringFromRessources(R.string.scene_mode), ContextApplication.getStringFromRessources(R.string.auto));
            parameters.set(ContextApplication.getStringFromRessources(R.string.auto_hdr_enable), ContextApplication.getStringFromRessources(R.string.disable_));
        }
        else if (valueToSet.equals(ContextApplication.getStringFromRessources(R.string.auto_)))
        {
            parameters.set(ContextApplication.getStringFromRessources(R.string.scene_mode), ContextApplication.getStringFromRessources(R.string.scene_mode_asd));
            parameters.set(ContextApplication.getStringFromRessources(R.string.auto_hdr_enable), ContextApplication.getStringFromRessources(R.string.enable_));
        }
        Log.d(TAG, "set auto hdr");
        if(setToCam)
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        ((SettingMode)SettingsManager.get(key)).set(valueToSet);
        fireStringValueChanged(valueToSet);
    }

    @Override
    public String GetStringValue() {
        if(parameters.get(ContextApplication.getStringFromRessources(R.string.auto_hdr_enable))!= null)
        {
            if (parameters.get(ContextApplication.getStringFromRessources(R.string.auto_hdr_enable)).equals(ContextApplication.getStringFromRessources(R.string.enable_))
                    && parameters.get(ContextApplication.getStringFromRessources(R.string.scene_mode)).equals(ContextApplication.getStringFromRessources(R.string.scene_mode_hdr)))
                return ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_);
            else if (parameters.get(ContextApplication.getStringFromRessources(R.string.auto_hdr_enable)).equals(ContextApplication.getStringFromRessources(R.string.enable_))
                    && parameters.get(ContextApplication.getStringFromRessources(R.string.scene_mode)).equals(ContextApplication.getStringFromRessources(R.string.scene_mode_asd)))
                return ContextApplication.getStringFromRessources(R.string.auto_);
            else
                return ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_);
        }
        else
            return ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_);
    }

    @Override
    public void onModuleChanged(String module)
    {
        curmodule = module;
        if (curmodule.equals(ContextApplication.getStringFromRessources(R.string.module_video))|| curmodule.equals(ContextApplication.getStringFromRessources(R.string.module_video)))
        {
            hide();
            SetValue(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_),true);
        }
        else
        {
            if (format.contains(ContextApplication.getStringFromRessources(R.string.jpeg_))) {
                show();
                setViewState(ViewState.Visible);
            }
            else
            {
                hide();
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
                show();

            else if (!format.contains(ContextApplication.getStringFromRessources(R.string.jpeg_)) && visible) {
                hide();
            }
        }
    }

    private void hide()
    {
        state = GetStringValue();
        visible = false;
        SetValue(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_),true);
        fireStringValueChanged(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_));
        setViewState(ViewState.Hidden);
    }
    private void show()
    {
        visible = true;
        SetValue(state,true);
        fireStringValueChanged(state);
        setViewState(ViewState.Visible);
    }
}
