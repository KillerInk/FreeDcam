package freed.cam.apis.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera2.CameraHolderApi2;

/**
 * Created by Ingo on 03.10.2016.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JpegQualityModeApi2 extends BaseModeApi2 {
    public JpegQualityModeApi2(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
    }

    @Override
    public boolean IsSupported() {
        return true;
    }


    @Override
    public String GetStringValue()
    {
        int r;
        try {
            r  = (int) ((CameraHolderApi2) cameraUiWrapper.getCameraHolder()).captureSessionHandler.getPreviewParameter(CaptureRequest.JPEG_QUALITY);
        }
        catch (NullPointerException ex)
        {
            r = 85;
        }

        return r+"";
    }

    @Override
    public String[] getStringValues() {
        List<String> values = new ArrayList<>();
        for (int i= 10; i <= 100; i+=10)
        {
            values.add(i+"");
        }
        String[] strings = new String[values.size()];
        values.toArray(strings);
        return strings;
    }

    @Override
    public void setValue(String valueToSet, boolean setToCamera) {
        ((CameraHolderApi2) cameraUiWrapper.getCameraHolder()).captureSessionHandler.SetParameterRepeating(CaptureRequest.JPEG_QUALITY, (byte)Integer.parseInt(valueToSet),setToCamera);
        fireStringValueChanged(valueToSet);
    }
}
