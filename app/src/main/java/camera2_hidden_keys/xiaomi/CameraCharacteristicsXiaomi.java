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
    public final static CameraCharacteristics.Key<Byte> teleois_supported;
    public final static CameraCharacteristics.Key<Integer> camera_role_id;
    public final static CameraCharacteristics.Key<Integer[]> EIS_QUALITY_SUPPORTED;
    public final static CameraCharacteristics.Key<Integer> SUPPORT_VIDEO_HDR10;



    static {
        availableStreamConfigurations = getKeyClass("xiaomi.scaler.availableStreamConfigurations", int[].class);
        availableSuperResolutionStreamConfigurations = getKeyClass("xiaomi.scaler.availableSuperResolutionStreamConfigurations", int[].class);
        availableHeicStreamConfigurations = getKeyClass("xiaomi.scaler.availableHeicStreamConfigurations", int[].class);
        availableRawSizes = getKeyClass("android.scaler.availableRawSizes", int[].class);
        qcfa_supported = getKeyClass("xiaomi.quadcfa.supported", Byte.class);
        qcfa_enabled = getKeyClass("xiaomi.quadcfa.enabled", Byte.class);
        teleois_supported = getKeyClass("com.xiaomi.camera.supportedfeatures.TeleOisSupported", Byte.class);
        camera_role_id = getKeyClass("com.xiaomi.cameraid.role.cameraId", Integer.class);
        EIS_QUALITY_SUPPORTED = getKeyClass("xiaomi.capabilities.videoStabilization.quality", int[].class);
        SUPPORT_VIDEO_HDR10 = getKeyClass("xiaomi.videohdrmode.value", Integer.class);
    }
}
