package freed.cam.apis.camera1.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.R;


import java.util.ArrayList;
import java.util.List;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.settings.SettingKeys;
import freed.settings.mode.SettingMode;

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
    public void setValue(String valueToSet, boolean setToCam) {

        if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.on_)))
            parameters.set(FreedApplication.getStringFromRessources(R.string.hdr_mode), 1);
        else if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.off_)))
            parameters.set(FreedApplication.getStringFromRessources(R.string.hdr_mode), 0);
        else if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.auto_)))
            parameters.set(FreedApplication.getStringFromRessources(R.string.hdr_mode), 2);
        if (setToCam)
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        ((SettingMode)settingsManager.get(key)).set(valueToSet);
        fireStringValueChanged(valueToSet);
    }

    @Override
    public String getStringValue() {
            if (parameters.get(FreedApplication.getStringFromRessources(R.string.hdr_mode))== null)
                parameters.set(FreedApplication.getStringFromRessources(R.string.hdr_mode), "0");
            if (parameters.get(FreedApplication.getStringFromRessources(R.string.hdr_mode)).equals("0"))
                return FreedApplication.getStringFromRessources(R.string.off_);
            else if (parameters.get(FreedApplication.getStringFromRessources(R.string.hdr_mode)).equals("1"))
                return FreedApplication.getStringFromRessources(R.string.on_);
            else
                return FreedApplication.getStringFromRessources(R.string.auto_);

    }

    @Override
    public String[] getStringValues() {
        List<String> hdrVals =  new ArrayList<>();
        hdrVals.add(FreedApplication.getStringFromRessources(R.string.off_));
        hdrVals.add(FreedApplication.getStringFromRessources(R.string.on_));
        hdrVals.add(FreedApplication.getStringFromRessources(R.string.auto_));
        return hdrVals.toArray(new String[hdrVals.size()]);
    }

    @Override
    public void onModuleChanged(String module)
    {
        curmodule = module;
        if (curmodule.equals(FreedApplication.getStringFromRessources(R.string.module_video))|| curmodule.equals(FreedApplication.getStringFromRessources(R.string.module_video)))
        {
            Hide();
            setStringValue(FreedApplication.getStringFromRessources(R.string.off_),true);
        }
        else
        {
            if (format.contains(FreedApplication.getStringFromRessources(R.string.jpeg_))) {
                Show();
                setViewState(ViewState.Visible);
            }
            else
            {
                Hide();
                setStringValue(FreedApplication.getStringFromRessources(R.string.off_),true);
            }
        }
    }

    private void Hide()
    {
        state = getStringValue();
        visible = false;
        setStringValue(FreedApplication.getStringFromRessources(R.string.off_),true);
        fireStringValueChanged(FreedApplication.getStringFromRessources(R.string.off_));
        setViewState(ViewState.Hidden);
    }
    private void Show()
    {
        visible = true;
        setStringValue(state,true);
        fireStringValueChanged(state);
        setViewState(ViewState.Visible);
    }

}
