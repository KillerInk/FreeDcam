package freed.cam.apis.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera2.Camera2Fragment;

/**
 * Created by Ingo on 03.10.2016.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AeLockModeApi2 extends BaseModeApi2 {
    public AeLockModeApi2(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper, null);
        setViewState(ViewState.Visible);
    }


    @Override
    public String GetStringValue() {
        if (((Camera2Fragment)cameraUiWrapper).captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AE_LOCK))
            return cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.true_);
        else
            return cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.false_);
    }

    @Override
    public String[] getStringValues() {
        return new String[]{cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.false_), cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.true_)};
    }

    @Override
    public void setValue(String valueToSet, boolean setToCamera) {

        ((Camera2Fragment) cameraUiWrapper).captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AE_LOCK, valueToSet.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.true_)), setToCamera);
    }
}
