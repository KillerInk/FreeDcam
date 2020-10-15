package freed.cam.apis.basecamera.parameters.modes;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import com.troop.freedcam.settings.SettingKeys;

public class VideoAudioSourceMode extends AbstractParameter {


    public VideoAudioSourceMode(CameraWrapperInterface cameraUiWrapper, SettingKeys.Key settingMode) {
        super(cameraUiWrapper, settingMode);
    }

    @Override
    protected void setValue(String valueToSet, boolean setToCamera) {
        super.setValue(valueToSet, setToCamera);
        if (cameraUiWrapper.getModuleHandler().getCurrentModuleName() == FreedApplication.getStringFromRessources(R.string.module_video) && setToCamera)
        {
            cameraUiWrapper.getModuleHandler().getCurrentModule().DestroyModule();
            cameraUiWrapper.getModuleHandler().getCurrentModule().InitModule();
        }
    }
}
