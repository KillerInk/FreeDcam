package freed.cam.apis.camera1.parameters.modes;

import android.hardware.Camera;
import android.text.TextUtils;

import com.troop.freedcam.R;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
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

        if (parameters.get(FreedApplication.getStringFromRessources(R.string.auto_hdr_supported))!=null)
            setViewState(ViewState.Hidden);
        String autohdr = parameters.get(FreedApplication.getStringFromRessources(R.string.auto_hdr_supported));
        if (autohdr != null && !TextUtils.isEmpty(autohdr) && autohdr.equals(FreedApplication.getStringFromRessources(R.string.true_))
                && parameters.get(FreedApplication.getStringFromRessources(R.string.auto_hdr_enable)) != null) {

            List<String> Scenes = new ArrayList<>(Arrays.asList(parameters.get(FreedApplication.getStringFromRessources(R.string.scene_mode_values)).split(",")));
            if (Scenes.contains(FreedApplication.getStringFromRessources(R.string.scene_mode_hdr))) {
                setViewState(ViewState.Visible);
            }
            if (Scenes.contains(FreedApplication.getStringFromRessources(R.string.scene_mode_asd))) {
                setViewState(ViewState.Visible);
            }

        }
        else
            setViewState(ViewState.Hidden);

    }

    @Override
    public void setValue(String valueToSet, boolean setToCam) {
        if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.on_)))
        {
            parameters.set(FreedApplication.getStringFromRessources(R.string.scene_mode), FreedApplication.getStringFromRessources(R.string.auto));
            parameters.set(FreedApplication.getStringFromRessources(R.string.auto_hdr_enable), FreedApplication.getStringFromRessources(R.string.disable_));

        }
        else if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.off_)))
        {
            parameters.set(FreedApplication.getStringFromRessources(R.string.scene_mode), FreedApplication.getStringFromRessources(R.string.auto));
            parameters.set(FreedApplication.getStringFromRessources(R.string.auto_hdr_enable), FreedApplication.getStringFromRessources(R.string.disable_));
        }
        else if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.auto_)))
        {
            parameters.set(FreedApplication.getStringFromRessources(R.string.scene_mode), FreedApplication.getStringFromRessources(R.string.scene_mode_asd));
            parameters.set(FreedApplication.getStringFromRessources(R.string.auto_hdr_enable), FreedApplication.getStringFromRessources(R.string.enable_));
        }
        Log.d(TAG, "set auto hdr");
        if(setToCam)
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        ((SettingMode)SettingsManager.get(key)).set(valueToSet);
        fireStringValueChanged(valueToSet);
    }

    @Override
    public String GetStringValue() {
        if(parameters.get(FreedApplication.getStringFromRessources(R.string.auto_hdr_enable))!= null)
        {
            if (parameters.get(FreedApplication.getStringFromRessources(R.string.auto_hdr_enable)).equals(FreedApplication.getStringFromRessources(R.string.enable_))
                    && parameters.get(FreedApplication.getStringFromRessources(R.string.scene_mode)).equals(FreedApplication.getStringFromRessources(R.string.scene_mode_hdr)))
                return FreedApplication.getStringFromRessources(R.string.on_);
            else if (parameters.get(FreedApplication.getStringFromRessources(R.string.auto_hdr_enable)).equals(FreedApplication.getStringFromRessources(R.string.enable_))
                    && parameters.get(FreedApplication.getStringFromRessources(R.string.scene_mode)).equals(FreedApplication.getStringFromRessources(R.string.scene_mode_asd)))
                return FreedApplication.getStringFromRessources(R.string.auto_);
            else
                return FreedApplication.getStringFromRessources(R.string.off_);
        }
        else
            return FreedApplication.getStringFromRessources(R.string.off_);
    }

    @Override
    public void onModuleChanged(String module)
    {
        curmodule = module;
        if (curmodule.equals(FreedApplication.getStringFromRessources(R.string.module_video))|| curmodule.equals(FreedApplication.getStringFromRessources(R.string.module_video)))
        {
            hide();
            SetValue(FreedApplication.getStringFromRessources(R.string.off_),true);
        }
        else
        {
            if (format.contains(FreedApplication.getStringFromRessources(R.string.jpeg_))) {
                show();
                setViewState(ViewState.Visible);
            }
            else
            {
                hide();
                SetValue(FreedApplication.getStringFromRessources(R.string.off_),true);
            }
        }
    }

    @Subscribe
    public void onPictureFormatChanged(ValueChangedEvent<String> valueChangedEvent)
    {
        if (valueChangedEvent.key == SettingKeys.PictureFormat) {
            format = valueChangedEvent.newValue;
            if (format.contains(FreedApplication.getStringFromRessources(R.string.jpeg_)) && !visible && !curmodule.equals(FreedApplication.getStringFromRessources(R.string.module_hdr)))
                show();

            else if (!format.contains(FreedApplication.getStringFromRessources(R.string.jpeg_)) && visible) {
                hide();
            }
        }
    }

    private void hide()
    {
        state = GetStringValue();
        visible = false;
        SetValue(FreedApplication.getStringFromRessources(R.string.off_),true);
        fireStringValueChanged(FreedApplication.getStringFromRessources(R.string.off_));
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
