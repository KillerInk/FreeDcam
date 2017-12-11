package freed.cam.apis.camera1.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.List;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.Settings;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 24.02.2017.
 */

public class MorphoHdrModeParameters extends BaseModeParameter {

    final String TAG = MorphoHdrModeParameters.class.getSimpleName();
    private boolean visible = true;
    private boolean supportauto;
    private boolean supporton;
    private String state = "";
    private String format = "";
    private String curmodule = "";

    public MorphoHdrModeParameters(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper, SettingsManager.SettingMode settingMode) {
        super(parameters, cameraUiWrapper, settingMode);
        isSupported = settingMode.isSupported();
        isVisible = isSupported;
        cameraUiWrapper.getModuleHandler().addListner(this);
        cameraUiWrapper.getParameterHandler().get(Settings.PictureFormat).addEventListner(this);
    }

    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        if (valueToSet.equals(cameraUiWrapper.getResString(R.string.on_))) {
            parameters.set(cameraUiWrapper.getResString(R.string.morpho_hht), cameraUiWrapper.getResString(R.string.false_));
            cameraUiWrapper.getParameterHandler().get(Settings.NightMode).fireStringValueChanged(cameraUiWrapper.getResString(R.string.off_));
            parameters.set("capture-burst-exposures","-10,0,10");
            cameraUiWrapper.getParameterHandler().get(Settings.AE_Bracket).SetValue(SettingsManager.getInstance().getResString(R.string.ae_bracket_hdr_values_aebracket), true);
            parameters.set(cameraUiWrapper.getResString(R.string.morpho_hdr), cameraUiWrapper.getResString(R.string.true_));
        } else {
            cameraUiWrapper.getParameterHandler().get(Settings.AE_Bracket).SetValue(SettingsManager.getInstance().getResString(R.string.ae_bracket_hdr_values_off), true);
            parameters.set(cameraUiWrapper.getResString(R.string.morpho_hdr), cameraUiWrapper.getResString(R.string.false_));
        }

        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        onStringValueChanged(valueToSet);
    }

    @Override
    public String GetStringValue() {

        if(cameraUiWrapper == null) {
            Log.d(TAG, "cameraUiWrapper null");
            isSupported =false;
            onIsSupportedChanged(false);
            return cameraUiWrapper.getResString(R.string.off_);
        }
        if (parameters == null) {
            Log.d(TAG, "Parameters are null");
            isSupported =false;
            onIsSupportedChanged(false);
            return cameraUiWrapper.getResString(R.string.off_);
        }
        if (parameters.get(cameraUiWrapper.getResString(R.string.morpho_hdr)) == null) {
            Log.d(TAG, "MorphoHdr is null");
            isSupported =false;
            onIsSupportedChanged(false);
            return cameraUiWrapper.getResString(R.string.off_);
        }
        if (parameters.get(SettingsManager.getInstance().getResString(R.string.ae_bracket_hdr)) == null){
            Log.d(TAG, "Ae bracket is null");
            isSupported =false;
            onIsSupportedChanged(false);
            return cameraUiWrapper.getResString(R.string.off_);
        }


        if (parameters.get(cameraUiWrapper.getResString(R.string.morpho_hdr)).equals(cameraUiWrapper.getResString(R.string.true_))
                && parameters.get(SettingsManager.getInstance().getResString(R.string.ae_bracket_hdr)).equals(SettingsManager.getInstance().getResString(R.string.ae_bracket_hdr_values_aebracket)))
            return cameraUiWrapper.getResString(R.string.on_);
        else
            return cameraUiWrapper.getResString(R.string.off_);
    }

    @Override
    public String[] getStringValues() {
        List<String> hdrVals =  new ArrayList<>();
        hdrVals.add(cameraUiWrapper.getResString(R.string.off_));
        hdrVals.add(cameraUiWrapper.getResString(R.string.on_));
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
