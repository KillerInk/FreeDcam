package freed.cam.apis.camera1.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.utils.AppSettingsManager;

/**
 * Created by troop on 24.02.2017.
 */

public class AutoHdrMode extends BaseModeParameter {

    final String TAG = AutoHdrMode.class.getSimpleName();
    private boolean visible = true;
    private String state = "";
    private String format = "";
    private String curmodule = "";
    public AutoHdrMode(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper, AppSettingsManager.SettingMode settingMode) {
        super(parameters, cameraUiWrapper, settingMode);

        if (parameters.get(cameraUiWrapper.getResString(R.string.auto_hdr_supported))!=null)
            isSupported = false;
        String autohdr = parameters.get(cameraUiWrapper.getResString(R.string.auto_hdr_supported));
        if (autohdr != null && !autohdr.equals("") && autohdr.equals(cameraUiWrapper.getResString(R.string.true_)) && parameters.get(cameraUiWrapper.getResString(R.string.auto_hdr_enable)) != null) {

            List<String> Scenes = new ArrayList<>(Arrays.asList(parameters.get(cameraUiWrapper.GetAppSettingsManager().getResString(R.string.scene_mode_values)).split(",")));
            if (Scenes.contains(cameraUiWrapper.getResString(R.string.scene_mode_hdr))) {
                boolean supporton = true;
                isSupported = true;
            }
            if (Scenes.contains(cameraUiWrapper.getResString(R.string.scene_mode_asd))) {
                boolean supportauto = true;
                isSupported = true;
            }

        }
        else
            isSupported = false;

        if (isSupported) {
            cameraUiWrapper.GetModuleHandler().addListner(this);
            cameraUiWrapper.GetParameterHandler().PictureFormat.addEventListner(this);
        }
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        if (valueToSet.equals(cameraUiWrapper.getResString(R.string.on_)))
        {
            parameters.set(cameraUiWrapper.GetAppSettingsManager().getResString(R.string.scene_mode), cameraUiWrapper.getResString(R.string.auto));
            parameters.set(cameraUiWrapper.getResString(R.string.auto_hdr_enable), cameraUiWrapper.getResString(R.string.disable_));

        }
        else if (valueToSet.equals(cameraUiWrapper.getResString(R.string.off_)))
        {
            parameters.set(cameraUiWrapper.GetAppSettingsManager().getResString(R.string.scene_mode), cameraUiWrapper.getResString(R.string.auto));
            parameters.set(cameraUiWrapper.getResString(R.string.auto_hdr_enable), cameraUiWrapper.getResString(R.string.disable_));
        }
        else if (valueToSet.equals(cameraUiWrapper.getResString(R.string.auto_)))
        {
            parameters.set(cameraUiWrapper.GetAppSettingsManager().getResString(R.string.scene_mode), cameraUiWrapper.getResString(R.string.scene_mode_asd));
            parameters.set(cameraUiWrapper.getResString(R.string.auto_hdr_enable), cameraUiWrapper.getResString(R.string.enable_));
        }
        ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
        onValueHasChanged(valueToSet);
    }

    @Override
    public String GetValue() {
        if(parameters.get(cameraUiWrapper.getResString(R.string.auto_hdr_enable))!= null)
        {
            if (parameters.get(cameraUiWrapper.getResString(R.string.auto_hdr_enable)).equals(cameraUiWrapper.getResString(R.string.enable_))
                    && parameters.get(cameraUiWrapper.getResString(R.string.scene_mode)).equals(cameraUiWrapper.getResString(R.string.scene_mode_hdr)))
                return cameraUiWrapper.getResString(R.string.on_);
            else if (parameters.get(cameraUiWrapper.getResString(R.string.auto_hdr_enable)).equals(cameraUiWrapper.getResString(R.string.enable_))
                    && parameters.get(cameraUiWrapper.getResString(R.string.scene_mode)).equals(cameraUiWrapper.getResString(R.string.scene_mode_asd)))
                return cameraUiWrapper.getResString(R.string.auto_);
            else
                return cameraUiWrapper.getResString(R.string.off_);
        }
        else
            return cameraUiWrapper.getResString(R.string.off_);
    }

    @Override
    public String[] GetValues() {
        return valuesArray;
    }

    @Override
    public void onModuleChanged(String module)
    {
        curmodule = module;
        if (curmodule.equals(cameraUiWrapper.getResString(R.string.module_video))|| curmodule.equals(cameraUiWrapper.getResString(R.string.module_video)))
        {
            Hide();
            SetValue(cameraUiWrapper.getResString(R.string.off_),true);
        }
        else
        {
            if (format.contains(cameraUiWrapper.getResString(R.string.jpeg_))) {
                Show();
                onIsSupportedChanged(true);
            }
            else
            {
                Hide();
                SetValue(cameraUiWrapper.getResString(R.string.off_),true);
            }
        }
    }

    @Override
    public void onParameterValueChanged(String val)
    {
        format = val;
        if (val.contains(cameraUiWrapper.getResString(R.string.jpeg_))&&!visible &&!curmodule.equals(cameraUiWrapper.getResString(R.string.module_hdr)))
            Show();

        else if (!val.contains(cameraUiWrapper.getResString(R.string.jpeg_))&& visible) {
            Hide();
        }
    }

    private void Hide()
    {
        state = GetValue();
        visible = false;
        SetValue(cameraUiWrapper.getResString(R.string.off_),true);
        onValueHasChanged(cameraUiWrapper.getResString(R.string.off_));
        onIsSupportedChanged(visible);
    }
    private void Show()
    {
        visible = true;
        SetValue(state,true);
        onValueHasChanged(state);
        onIsSupportedChanged(visible);
    }
}
