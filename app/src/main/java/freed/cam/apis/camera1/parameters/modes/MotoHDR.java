package freed.cam.apis.camera1.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.R;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.events.ValueChangedEvent;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.settings.mode.SettingMode;

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

    public MotoHDR(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper,SettingKeys.Key settingMode) {
        super(parameters, cameraUiWrapper, settingMode);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {

        if (valueToSet.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.on_)))
            parameters.set(SettingsManager.getInstance().getResString(R.string.scene_mode), cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.scene_mode_hdr));
        else if (valueToSet.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_)))
            parameters.set(SettingsManager.getInstance().getResString(R.string.scene_mode), cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto_));
        else if (valueToSet.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto_)))
            parameters.set(SettingsManager.getInstance().getResString(R.string.scene_mode), cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto_hdr));
        if (setToCam)
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        fireStringValueChanged(valueToSet);
        ((SettingMode)SettingsManager.get(key)).set(valueToSet);
    }

    @Override
    public String GetStringValue() {
        if (parameters.get(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.scene_mode)) == null)
            return cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_);

        if (parameters.get(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.scene_mode)).equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto_)))
            return cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_);
        else if (parameters.get(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.scene_mode)).equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.scene_mode_hdr)))
            return cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.on_);
        else
            return cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto_);

    }

    @Override
    public String[] getStringValues() {
        List<String> hdrVals =  new ArrayList<>();
        hdrVals.add(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_));
        hdrVals.add(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.on_));
        hdrVals.add(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto_));
        return hdrVals.toArray(new String[hdrVals.size()]);
    }

    @Override
    public void onModuleChanged(String module)
    {
        curmodule = module;
        if (curmodule.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.module_video))|| curmodule.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.module_video)))
        {
            Hide();
            SetValue(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_),true);
        }
        else
        {
            if (format.contains(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.jpeg_))) {
                Show();
                setViewState(ViewState.Visible);
            }
            else
            {
                Hide();
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
                Show();

            else if (!format.contains(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.jpeg_)) && visible) {
                Hide();
            }
        }
    }

    private void Hide()
    {
        state = GetStringValue();
        visible = false;
        SetValue(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_),true);
        fireStringValueChanged(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_));
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
