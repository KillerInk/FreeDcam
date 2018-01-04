package freed.cam.apis.camera1.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.List;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.settings.SettingKeys;
import freed.settings.mode.SettingMode;
import freed.settings.SettingsManager;

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

    public LgHdrMode(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper, SettingKeys.Key settingMode) {
        super(parameters, cameraUiWrapper, settingMode);
    }

    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public void setValue(String valueToSet, boolean setToCam) {

        if (valueToSet.equals(cameraUiWrapper.getResString(R.string.on_)))
            parameters.set(cameraUiWrapper.getResString(R.string.hdr_mode), 1);
        else if (valueToSet.equals(cameraUiWrapper.getResString(R.string.off_)))
            parameters.set(cameraUiWrapper.getResString(R.string.hdr_mode), 0);
        else if (valueToSet.equals(cameraUiWrapper.getResString(R.string.auto_)))
            parameters.set(cameraUiWrapper.getResString(R.string.hdr_mode), 2);
        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        ((SettingMode)SettingsManager.get(key)).set(valueToSet);
        fireStringValueChanged(valueToSet);
    }

    @Override
    public String GetStringValue() {
            if (parameters.get(cameraUiWrapper.getResString(R.string.hdr_mode))== null)
                parameters.set(cameraUiWrapper.getResString(R.string.hdr_mode), "0");
            if (parameters.get(cameraUiWrapper.getResString(R.string.hdr_mode)).equals("0"))
                return cameraUiWrapper.getResString(R.string.off_);
            else if (parameters.get(cameraUiWrapper.getResString(R.string.hdr_mode)).equals("1"))
                return cameraUiWrapper.getResString(R.string.on_);
            else
                return cameraUiWrapper.getResString(R.string.auto_);

    }

    @Override
    public String[] getStringValues() {
        List<String> hdrVals =  new ArrayList<>();
        hdrVals.add(cameraUiWrapper.getResString(R.string.off_));
        hdrVals.add(cameraUiWrapper.getResString(R.string.on_));
        hdrVals.add(cameraUiWrapper.getResString(R.string.auto_));
        return hdrVals.toArray(new String[hdrVals.size()]);
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
                fireIsSupportedChanged(true);
            }
            else
            {
                Hide();
                SetValue(cameraUiWrapper.getResString(R.string.off_),true);
            }
        }
    }

    @Override
    public void onStringValueChanged(String val) {
        format = val;
        if (val.contains(cameraUiWrapper.getResString(R.string.jpeg_))&&!visible &&!curmodule.equals(cameraUiWrapper.getResString(R.string.module_hdr)))
            Show();

        else if (!val.contains(cameraUiWrapper.getResString(R.string.jpeg_))&& visible) {
            Hide();
        }
    }

    private void Hide()
    {
        state = GetStringValue();
        visible = false;
        SetValue(cameraUiWrapper.getResString(R.string.off_),true);
        fireStringValueChanged(cameraUiWrapper.getResString(R.string.off_));
        fireIsSupportedChanged(visible);
    }
    private void Show()
    {
        visible = true;
        SetValue(state,true);
        fireStringValueChanged(state);
        fireIsSupportedChanged(visible);
    }

}
