package freed.cam.apis.camera1.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.List;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.settings.SettingsManager;

/**
 * Created by GeorgeKiarie on 5/11/2017.
 */

public class MotoHDR extends BaseModeParameter
{
    final String TAG = MotoHDR.class.getSimpleName();
    private boolean visible = true;
    private boolean supportauto;
    private boolean supporton;
    private String state = "";
    private String format = "";
    private String curmodule = "";

    public MotoHDR(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper, SettingsManager.SettingMode settingMode) {
        super(parameters, cameraUiWrapper, settingMode);
    }

    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {

        if (valueToSet.equals(cameraUiWrapper.getResString(R.string.on_)))
            parameters.set(SettingsManager.getInstance().getResString(R.string.scene_mode), cameraUiWrapper.getResString(R.string.scene_mode_hdr));
        else if (valueToSet.equals(cameraUiWrapper.getResString(R.string.off_)))
            parameters.set(SettingsManager.getInstance().getResString(R.string.scene_mode), cameraUiWrapper.getResString(R.string.auto_));
        else if (valueToSet.equals(cameraUiWrapper.getResString(R.string.auto_)))
            parameters.set(SettingsManager.getInstance().getResString(R.string.scene_mode), cameraUiWrapper.getResString(R.string.auto_hdr));
        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        onStringValueChanged(valueToSet);
    }

    @Override
    public String GetStringValue() {
        if (parameters.get(cameraUiWrapper.getResString(R.string.scene_mode)) == null)
            return cameraUiWrapper.getResString(R.string.off_);

        if (parameters.get(cameraUiWrapper.getResString(R.string.scene_mode)).equals(cameraUiWrapper.getResString(R.string.auto_)))
            return cameraUiWrapper.getResString(R.string.off_);
        else if (parameters.get(cameraUiWrapper.getResString(R.string.scene_mode)).equals(cameraUiWrapper.getResString(R.string.scene_mode_hdr)))
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
        onStringValueChanged(cameraUiWrapper.getResString(R.string.off_));
        onIsSupportedChanged(visible);
    }
    private void Show()
    {
        visible = true;
        SetValue(state,true);
        onStringValueChanged(state);
        onIsSupportedChanged(visible);
    }

}
