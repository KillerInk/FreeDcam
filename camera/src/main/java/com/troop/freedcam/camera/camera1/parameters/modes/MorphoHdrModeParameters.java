package com.troop.freedcam.camera.camera1.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.camera.R;
import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.parameters.ParameterEvents;
import com.troop.freedcam.camera.camera1.parameters.ParametersHandler;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.settings.mode.SettingMode;
import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.utils.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 24.02.2017.
 */

public class MorphoHdrModeParameters extends BaseModeParameter implements ParameterEvents {

    final String TAG = MorphoHdrModeParameters.class.getSimpleName();
    private boolean visible = true;
    private boolean supportauto;
    private boolean supporton;
    private String state = "";
    private String format = "";
    private String curmodule = "";

    public MorphoHdrModeParameters(Camera.Parameters parameters, CameraControllerInterface cameraUiWrapper, SettingKeys.Key settingMode) {
        super(parameters, cameraUiWrapper, settingMode);

        //cameraUiWrapper.getParameterHandler().get(SettingKeys.PictureFormat).addEventListner(this);
    }


    @Override
    public void setValue(String valueToSet, boolean setToCam) {
        if (valueToSet.equals(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_))) {
            parameters.set(ContextApplication.getStringFromRessources(R.string.morpho_hht), ContextApplication.getStringFromRessources(R.string.false_));
            cameraUiWrapper.getParameterHandler().get(SettingKeys.NightMode).fireStringValueChanged(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_));
            parameters.set("capture-burst-exposures","-10,0,10");
            cameraUiWrapper.getParameterHandler().get(SettingKeys.AE_Bracket).SetValue(ContextApplication.getStringFromRessources(R.string.ae_bracket_hdr_values_aebracket), true);
            parameters.set(ContextApplication.getStringFromRessources(R.string.morpho_hdr), ContextApplication.getStringFromRessources(R.string.true_));
        } else {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.AE_Bracket).SetValue(ContextApplication.getStringFromRessources(R.string.ae_bracket_hdr_values_off), true);
            parameters.set(ContextApplication.getStringFromRessources(R.string.morpho_hdr), ContextApplication.getStringFromRessources(R.string.false_));
        }
        if (setToCam)
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        ((SettingMode)SettingsManager.get(key)).set(valueToSet);
        fireStringValueChanged(valueToSet);
    }

    @Override
    public String GetStringValue() {

        if(cameraUiWrapper == null) {
            Log.d(TAG, "cameraUiWrapper null");
            setViewState(ViewState.Hidden);
            return ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_);
        }
        if (parameters == null) {
            Log.d(TAG, "Parameters are null");
            setViewState(ViewState.Hidden);
            return ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_);
        }
        if (parameters.get(ContextApplication.getStringFromRessources(R.string.morpho_hdr)) == null) {
            Log.d(TAG, "MorphoHdr is null");
            setViewState(ViewState.Hidden);
            return ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_);
        }
        if (parameters.get(ContextApplication.getStringFromRessources(R.string.ae_bracket_hdr)) == null){
            Log.d(TAG, "Ae bracket is null");
            setViewState(ViewState.Hidden);
            return ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_);
        }


        if (parameters.get(ContextApplication.getStringFromRessources(R.string.morpho_hdr)).equals(ContextApplication.getStringFromRessources(R.string.true_))
                && parameters.get(ContextApplication.getStringFromRessources(R.string.ae_bracket_hdr)).equals(ContextApplication.getStringFromRessources(R.string.ae_bracket_hdr_values_aebracket)))
            return ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_);
        else
            return ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_);
    }

    @Override
    public String[] getStringValues() {
        List<String> hdrVals =  new ArrayList<>();
        hdrVals.add(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_));
        hdrVals.add(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_));
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

    @Override
    public void onViewStateChanged(ViewState value) {

    }

    @Override
    public void onIntValueChanged(int current) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public void onStringValueChanged(String val) {
        format = val;
        if (val.contains(ContextApplication.getStringFromRessources(R.string.jpeg_))&&!visible &&!curmodule.equals(ContextApplication.getStringFromRessources(R.string.module_hdr)))
            Show();

        else if (!val.contains(ContextApplication.getStringFromRessources(R.string.jpeg_))&& visible) {
            Hide();
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
