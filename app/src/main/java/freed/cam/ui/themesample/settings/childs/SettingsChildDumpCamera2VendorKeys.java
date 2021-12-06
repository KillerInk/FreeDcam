package freed.cam.ui.themesample.settings.childs;

import android.content.Context;
import android.graphics.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.VendorKeyParser;
import camera2_hidden_keys.VendorKeyTestLog;
import freed.cam.apis.CameraApiManager;
import freed.cam.apis.camera2.Camera2;

public class SettingsChildDumpCamera2VendorKeys extends SettingsChildMenu {

    private Camera2 cameraManager;

    public SettingsChildDumpCamera2VendorKeys(Context context, int headerid, int descriptionid, Camera2 cameraManager) {
        super(context, headerid, descriptionid);
        binding.textviewMenuitemHeaderValue.setText("");
        this.cameraManager = cameraManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
            Camera2 camera2 = cameraManager;
            CaptureResult captureResult = camera2.cameraBackroundValuesChangedListner.getCaptureResult();
            CaptureRequest captureRequest = camera2.captureSessionHandler.getCaptureRequest();
            CameraCharacteristics characteristics = camera2.getCameraHolder().characteristics;
            VendorKeyParser vendorKeyParser = new VendorKeyParser();
            VendorKeyTestLog vendorKeyTestLog = new VendorKeyTestLog(vendorKeyParser,characteristics,captureResult,captureRequest);
            vendorKeyTestLog.testKeys();
    }
}
