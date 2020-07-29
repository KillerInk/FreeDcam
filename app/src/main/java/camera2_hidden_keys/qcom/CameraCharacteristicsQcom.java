package camera2_hidden_keys.qcom;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.AbstractCameraCharacteristics;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraCharacteristicsQcom extends AbstractCameraCharacteristics
{
    public static final CameraCharacteristics.Key<int[]> sharpness_range;
    public static final CameraCharacteristics.Key<int[]> saturation_range;
    public static final CameraCharacteristics.Key availableStreamConfigurationsQuadra;
    public static final CameraCharacteristics.Key<Byte> is_qcfa_sensor;
    public static final CameraCharacteristics.Key<Byte> is_logical_camera;
    public static final CameraCharacteristics.Key<Byte[]> sensor_sync_mode_config;
    public static final CameraCharacteristics.Key<Integer[]> qcfa_dimension;
    public static final CameraCharacteristics.Key<Integer[]> qcfa_availableStreamConfigurations;
    public static final CameraCharacteristics.Key<Integer[]> qcfa_activeArraySize;
    public static final CameraCharacteristics.Key<Integer> customhw;


    static {
        sharpness_range = getKeyType("org.codeaurora.qcamera3.sharpness.range", int[].class);
        saturation_range = getKeyType("org.codeaurora.qcamera3.saturation.range", int[].class);
        availableStreamConfigurationsQuadra = getKeyType("org.codeaurora.qcamera3.quadra_cfa.availableStreamConfigurations", int[].class);
        is_qcfa_sensor = getKeyType("org.codeaurora.qcamera3.quadra_cfa.is_qcfa_sensor", Byte.class);
        is_logical_camera = getKeyType("org.codeaurora.qcamera3.logicalCameraType.logical_camera_type", Byte.class);
        sensor_sync_mode_config = getKeyType("com.qti.chi.multicamerasensorconfig.sensorsyncmodeconfig", Byte[].class);
        qcfa_dimension = getKeyType("org.codeaurora.qcamera3.quadra_cfa.qcfa_dimension", Integer[].class);
        qcfa_availableStreamConfigurations = getKeyType("org.codeaurora.qcamera3.quadra_cfa.availableStreamConfigurations", Integer[].class);
        qcfa_activeArraySize = getKeyType("org.codeaurora.qcamera3.quadra_cfa.activeArraySize", Integer[].class);
        customhw = getKeyType("com.qti.node.customhw", Integer.class);
    }


}
