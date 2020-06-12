package camera2_hidden_keys.xiaomi;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.AbstractCameraCharacteristics;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraCharacteristicsXiaomi extends AbstractCameraCharacteristics {
    public final static CameraCharacteristics.Key<int[]> availableStreamConfigurations;
    public final static CameraCharacteristics.Key<int[]> availableSuperResolutionStreamConfigurations;
    public final static CameraCharacteristics.Key<int[]> availableHeicStreamConfigurations;
    public final static CameraCharacteristics.Key<int[]> availableRawSizes;
    public final static CameraCharacteristics.Key<Byte> qcfa_supported;
    public final static CameraCharacteristics.Key<Byte> qcfa_enabled;

    static {
        availableStreamConfigurations = getKeyClass("xiaomi.scaler.availableStreamConfigurations", int[].class);
        availableSuperResolutionStreamConfigurations = getKeyClass("xiaomi.scaler.availableSuperResolutionStreamConfigurations", int[].class);
        availableHeicStreamConfigurations = getKeyClass("xiaomi.scaler.availableHeicStreamConfigurations", int[].class);
        availableRawSizes = getKeyClass("android.scaler.availableRawSizes", int[].class);
        qcfa_supported = getKeyClass("xiaomi.quadcfa.supported", Byte.class);
        qcfa_enabled = getKeyClass("xiaomi.quadcfa.enabled", Byte.class);
    }
}
