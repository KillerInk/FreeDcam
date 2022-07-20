package freed.cam.ui.themenextgen.adapter.customclicks;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.VendorKeyParser;
import camera2_hidden_keys.VendorKeyTestLog;
import freed.cam.apis.camera2.Camera2;
import freed.cam.ui.themenextgen.view.button.NextGenSettingButton;

public class Camera2VendorButtonClick implements NextGenSettingButton.NextGenSettingButtonClick {


    private final Camera2 camera2;
    public Camera2VendorButtonClick(Camera2 camera2)
    {
        this.camera2 = camera2;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSettingButtonClick() {
        CaptureResult captureResult = camera2.cameraBackroundValuesChangedListner.getCaptureResult();
        CaptureRequest captureRequest = camera2.captureSessionHandler.getCaptureRequest();
        CameraCharacteristics characteristics = camera2.getCameraHolder().characteristics;
        VendorKeyParser vendorKeyParser = new VendorKeyParser();
        VendorKeyTestLog vendorKeyTestLog = new VendorKeyTestLog(vendorKeyParser,characteristics,captureResult,captureRequest);
        vendorKeyTestLog.testKeys();
    }
}
