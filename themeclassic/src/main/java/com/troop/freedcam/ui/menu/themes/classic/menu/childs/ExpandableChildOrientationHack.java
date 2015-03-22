package com.troop.freedcam.ui.menu.themes.classic.menu.childs;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.camera.parameters.modes.SimpleModeParameter;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.ExpandableGroup;

import java.util.ArrayList;

/**
 * Created by troop on 26.02.2015.
 */
public class ExpandableChildOrientationHack extends ExpandableChild implements CompoundButton.OnCheckedChangeListener
{
    protected Switch aSwitch;
    AbstractCameraUiWrapper cameraUiWrapper;

    public ExpandableChildOrientationHack(Context context, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname) {
        super(context, group, name, appSettingsManager, settingsname);
    }

    @Override
    protected void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expandablechildboolean_on_off, this);
        aSwitch = (Switch)findViewById(R.id.switch1);
        aSwitch.setText(Name);
        parameterHolder = new SimpleModeParameter();
        aSwitch.setOnCheckedChangeListener(this);
    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        if (cameraUiWrapper instanceof CameraUiWrapper) {
            ((SimpleModeParameter) parameterHolder).setIsSupported(true);
            if (appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals("true"))
                aSwitch.setChecked(true);
        }
        else
            ((SimpleModeParameter)parameterHolder).setIsSupported(false);
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String Value() {
        return null;
    }

    @Override
    public void setValue(String value) {

    }

    @Override
    public I_ModeParameter getParameterHolder() {
        return super.getParameterHolder();
    }

    @Override
    public void setParameterHolder(AbstractModeParameter parameterHolder, ArrayList<String> modulesToShow)
    {


    }

    @Override
    public String ModuleChanged(String module) {
        return null;
    }

    @Override
    protected String getTAG() {
        return ExpandableChildOrientationHack.class.getSimpleName();
    }

    @Override
    public void onValueChanged(String val) {

    }

    @Override
    public void onIsSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onIsSetSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        final String check = aSwitch.isChecked() +"";
        appSettingsManager.setString(settingsname,  check);
        ((CamParametersHandler)cameraUiWrapper.camParametersHandler).SetCameraRotation();
        ((CamParametersHandler)cameraUiWrapper.camParametersHandler).SetPictureOrientation(0);

    }
}
