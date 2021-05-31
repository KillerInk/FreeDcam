package freed.cam.apis.basecamera.parameters.modes;

import freed.cam.apis.basecamera.CameraThreadHandler;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera1.Camera1Fragment;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera2.Camera2Fragment;
import freed.settings.SettingKeys;

public class OrientationHackParameter extends AbstractParameter {
    public OrientationHackParameter(SettingKeys.Key key) {
        super(key);
    }

    public OrientationHackParameter(CameraWrapperInterface cameraUiWrapper, SettingKeys.Key settingMode) {
        super(cameraUiWrapper, settingMode);
    }

    @Override
    protected void setValue(String valueToSet, boolean setToCamera) {
        super.setValue(valueToSet,setToCamera);
        settingsManager.get(SettingKeys.orientationHack).set(valueToSet);
        if (cameraUiWrapper instanceof Camera1Fragment) {
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetCameraRotation();
            cameraUiWrapper.getParameterHandler().SetPictureOrientation(0);
        }
        else if(cameraUiWrapper instanceof Camera2Fragment)
        {
            CameraThreadHandler.restartCameraAsync();
        }
    }

    @Override
    public String getStringValue() {
        return settingsManager.get(SettingKeys.orientationHack).get();
    }
}
