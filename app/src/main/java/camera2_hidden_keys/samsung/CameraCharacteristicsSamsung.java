package camera2_hidden_keys.samsung;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.AbstractCameraCharacteristics;
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraCharacteristicsSamsung extends AbstractCameraCharacteristics {
    public static final CameraCharacteristics.Key<String> LOGICAL_MULTI_CAMERA_MASTER_PHYSICAL_ID;
    public static final CameraCharacteristics.Key<byte[]> LOGICAL_MULTI_CAMERA_DUAL_CALIBRATION;

    static {
        LOGICAL_MULTI_CAMERA_MASTER_PHYSICAL_ID = getKeyType("samsung.android.logicalMultiCamera.masterPhysicalId",String.class);
        LOGICAL_MULTI_CAMERA_DUAL_CALIBRATION = getKeyType("samsung.android.logicalMultiCamera.dualCalibration",byte[].class);
    }

}
