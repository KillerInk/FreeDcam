package com.troop.freedcam.camera.basecamera.parameters.modes;

import com.troop.freedcam.camera.R;
import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.parameters.AbstractParameter;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.utils.ContextApplication;

public class VideoAudioSourceMode extends AbstractParameter {


    public VideoAudioSourceMode(CameraControllerInterface cameraUiWrapper, SettingKeys.Key settingMode) {
        super(cameraUiWrapper, settingMode);
    }

    @Override
    protected void setValue(String valueToSet, boolean setToCamera) {
        super.setValue(valueToSet, setToCamera);
        if (cameraUiWrapper.getModuleHandler().getCurrentModuleName() == ContextApplication.getStringFromRessources(R.string.module_video) && setToCamera)
        {
            cameraUiWrapper.getModuleHandler().getCurrentModule().DestroyModule();
            cameraUiWrapper.getModuleHandler().getCurrentModule().InitModule();
        }
    }
}
