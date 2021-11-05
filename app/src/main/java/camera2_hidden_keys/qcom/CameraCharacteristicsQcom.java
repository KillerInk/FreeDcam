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
    public static final CameraCharacteristics.Key<int[]> sensorModeTable;
    public static final CameraCharacteristics.Key<int[]> support_video_hdr_modes;

    //[9470, 32110118400]
    public static final CameraCharacteristics.Key<long[]> org_codeaurora_qcamera3_iso_exp_priority_exposure_time_range;
    //[0, 1, 2, 3, 4, 5, 6, 7]
    public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_iso_exp_priority_iso_available_modes;

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
        sensorModeTable = getKeyType("org.quic.camera2.sensormode.info.SensorModeTable", int[].class);
        support_video_hdr_modes = getKeyType("org.codeaurora.qcamera3.available_video_hdr_modes.video_hdr_modes", int[].class);

        org_codeaurora_qcamera3_iso_exp_priority_exposure_time_range= getKeyType("org.codeaurora.qcamera3.iso_exp_priority.exposure_time_range", long[].class);
        org_codeaurora_qcamera3_iso_exp_priority_iso_available_modes= getKeyType("org.codeaurora.qcamera3.iso_exp_priority.iso_available_modes", int[].class);
    }


}
