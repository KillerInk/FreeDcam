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

    public MorphoHdrModeParameters(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper,SettingKeys.Key settingMode) {
        super(parameters, cameraUiWrapper, settingMode);

        //cameraUiWrapper.getParameterHandler().get(SettingKeys.PictureFormat).addEventListner(this);
    }


    @Override
    public void setValue(String valueToSet, boolean setToCam) {
        if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.on_))) {
            parameters.set(FreedApplication.getStringFromRessources(R.string.morpho_hht), FreedApplication.getStringFromRessources(R.string.false_));
            cameraUiWrapper.getParameterHandler().get(SettingKeys.NightMode).fireStringValueChanged(FreedApplication.getStringFromRessources(R.string.off_));
            parameters.set("capture-burst-exposures","-10,0,10");
            cameraUiWrapper.getParameterHandler().get(SettingKeys.AE_Bracket).setStringValue(FreedApplication.getStringFromRessources(R.string.ae_bracket_hdr_values_aebracket), true);
            parameters.set(FreedApplication.getStringFromRessources(R.string.morpho_hdr), FreedApplication.getStringFromRessources(R.string.true_));
        } else {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.AE_Bracket).setStringValue(FreedApplication.getStringFromRessources(R.string.ae_bracket_hdr_values_off), true);
            parameters.set(FreedApplication.getStringFromRessources(R.string.morpho_hdr), FreedApplication.getStringFromRessources(R.string.false_));
        }
        if (setToCam)
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        ((SettingMode)settingsManager.get(key)).set(valueToSet);
        fireStringValueChanged(valueToSet);
    }

    @Override
    public String getStringValue() {

        if(cameraUiWrapper == null) {
            Log.d(TAG, "cameraUiWrapper null");
            setViewState(ViewState.Hidden);
            return FreedApplication.getStringFromRessources(R.string.off_);
        }
        if (parameters == null) {
            Log.d(TAG, "Parameters are null");
            setViewState(ViewState.Hidden);
            return FreedApplication.getStringFromRessources(R.string.off_);
        }
        if (parameters.get(FreedApplication.getStringFromRessources(R.string.morpho_hdr)) == null) {
            Log.d(TAG, "MorphoHdr is null");
            setViewState(ViewState.Hidden);
            return FreedApplication.getStringFromRessources(R.string.off_);
        }
        if (parameters.get(FreedApplication.getStringFromRessources(R.string.ae_bracket_hdr)) == null){
            Log.d(TAG, "Ae bracket is null");
            setViewState(ViewState.Hidden);
            return FreedApplication.getStringFromRessources(R.string.off_);
        }


        if (parameters.get(FreedApplication.getStringFromRessources(R.string.morpho_hdr)).equals(FreedApplication.getStringFromRessources(R.string.true_))
                && parameters.get(FreedApplication.getStringFromRessources(R.string.ae_bracket_hdr)).equals(FreedApplication.getStringFromRessources(R.string.ae_bracket_hdr_values_aebracket)))
            return FreedApplication.getStringFromRessources(R.string.on_);
        else
            return FreedApplication.getStringFromRessources(R.string.off_);
    }

    @Override
    public String[] getStringValues() {
        List<String> hdrVals =  new ArrayList<>();
        hdrVals.add(FreedApplication.getStringFromRessources(R.string.off_));
        hdrVals.add(FreedApplication.getStringFromRessources(R.string.on_));
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

    /*@Override
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
        if (val.contains(FreedApplication.getStringFromRessources(R.string.jpeg_))&&!visible &&!curmodule.equals(FreedApplication.getStringFromRessources(R.string.module_hdr)))
            Show();

        else if (!val.contains(FreedApplication.getStringFromRessources(R.string.jpeg_))&& visible) {
            Hide();
        }
    }*/

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
