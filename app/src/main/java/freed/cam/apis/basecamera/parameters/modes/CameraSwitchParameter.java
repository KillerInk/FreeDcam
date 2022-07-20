package freed.cam.apis.basecamera.parameters.modes;

import freed.cam.apis.basecamera.CameraThreadHandler;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.SettingKeys;

public class CameraSwitchParameter extends AbstractParameter {

    private int currentCamera;
    public CameraSwitchParameter() {
        super(null);
        currentCamera = settingsManager.GetCurrentCamera();
        setViewState(ViewState.Visible);
        fireStringValueChanged(getCamera(currentCamera));
    }

    public CameraSwitchParameter(CameraWrapperInterface cameraUiWrapper, SettingKeys.Key settingMode) {
        super(cameraUiWrapper, settingMode);
        currentCamera = settingsManager.GetCurrentCamera();
    }

    @Override
    protected void setValue(String value, boolean setToCamera) {
        String[] split = value.split(" ");
        currentCamera = Integer.parseInt(split[1]);
        settingsManager.SetCurrentCamera(currentCamera);
        CameraThreadHandler.restartCameraAsync();
        fireStringValueChanged(getCamera(currentCamera));
    }

    private String getCamera(int i)
    {
        if (settingsManager.getIsFrontCamera())
            return "Front " + i;
        else
            return "Back " + i;
    }

    @Override
    public String[] getStringValues() {
        int[] camids = settingsManager.getCameraIds();
        String[] retarr = new String[camids.length];
        for (int i = 0; i < camids.length; i++)
        {
            if (settingsManager.getCamIsFrontCamera(i))
                retarr[i] = "Front "+i;
            else
                retarr[i] = "Back "+i;
        }
        return retarr;
    }
}
