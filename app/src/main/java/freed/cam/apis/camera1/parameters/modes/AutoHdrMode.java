package freed.cam.apis.camera1.parameters.modes;

import android.hardware.Camera;
import android.text.TextUtils;

import com.troop.freedcam.R;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.ParameterEvents;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.events.ValueChangedEvent;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.settings.mode.SettingMode;
import freed.utils.Log;

/**
 * Created by troop on 24.02.2017.
 */

public class AutoHdrMode extends BaseModeParameter {

    final String TAG = AutoHdrMode.class.getSimpleName();
    private boolean visible = true;
    private String state = "";
    private String format = "";
    private String curmodule = "";
    public AutoHdrMode(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper, SettingKeys.Key  settingMode) {
        super(parameters, cameraUiWrapper, settingMode);

        if (parameters.get(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto_hdr_supported))!=null)
            setViewState(ViewState.Hidden);
        String autohdr = parameters.get(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto_hdr_supported));
        if (autohdr != null && !TextUtils.isEmpty(autohdr) && autohdr.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.true_))
                && parameters.get(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto_hdr_enable)) != null) {

            List<String> Scenes = new ArrayList<>(Arrays.asList(parameters.get(SettingsManager.getInstance().getResString(R.string.scene_mode_values)).split(",")));
            if (Scenes.contains(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.scene_mode_hdr))) {
                setViewState(ViewState.Visible);
            }
            if (Scenes.contains(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.scene_mode_asd))) {
                setViewState(ViewState.Visible);
            }

        }
        else
            setViewState(ViewState.Hidden);

    }

    @Override
    public void setValue(String valueToSet, boolean setToCam) {
        if (valueToSet.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.on_)))
        {
            parameters.set(SettingsManager.getInstance().getResString(R.string.scene_mode), cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto));
            parameters.set(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto_hdr_enable), cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.disable_));

        }
        else if (valueToSet.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_)))
        {
            parameters.set(SettingsManager.getInstance().getResString(R.string.scene_mode), cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto));
            parameters.set(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto_hdr_enable), cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.disable_));
        }
        else if (valueToSet.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto_)))
        {
            parameters.set(SettingsManager.getInstance().getResString(R.string.scene_mode), cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.scene_mode_asd));
            parameters.set(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto_hdr_enable), cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.enable_));
        }
        Log.d(TAG, "set auto hdr");
        if(setToCam)
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        ((SettingMode)SettingsManager.get(key)).set(valueToSet);
        fireStringValueChanged(valueToSet);
    }

    @Override
    public String GetStringValue() {
        if(parameters.get(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto_hdr_enable))!= null)
        {
            if (parameters.get(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto_hdr_enable)).equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.enable_))
                    && parameters.get(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.scene_mode)).equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.scene_mode_hdr)))
                return cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.on_);
            else if (parameters.get(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto_hdr_enable)).equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.enable_))
                    && parameters.get(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.scene_mode)).equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.scene_mode_asd)))
                return cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto_);
            else
                return cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_);
        }
        else
            return cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_);
    }

    @Override
    public void onModuleChanged(String module)
    {
        curmodule = module;
        if (curmodule.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.module_video))|| curmodule.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.module_video)))
        {
            hide();
            SetValue(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_),true);
        }
        else
        {
            if (format.contains(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.jpeg_))) {
                show();
                setViewState(ViewState.Visible);
            }
            else
            {
                hide();
                SetValue(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_),true);
            }
        }
    }

    @Subscribe
    public void onPictureFormatChanged(ValueChangedEvent<String> valueChangedEvent)
    {
        if (valueChangedEvent.key == SettingKeys.PictureFormat) {
            format = valueChangedEvent.newValue;
            if (format.contains(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.jpeg_)) && !visible && !curmodule.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.module_hdr)))
                show();

            else if (!format.contains(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.jpeg_)) && visible) {
                hide();
            }
        }
    }

    private void hide()
    {
        state = GetStringValue();
        visible = false;
        SetValue(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_),true);
        fireStringValueChanged(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_));
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
