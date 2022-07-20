package camera2_hidden_keys.devices.pocof2;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.AbstractCameraCharacteristics;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraCharacteristicsDump extends AbstractCameraCharacteristics
{
    //[1, 0, 0, 0, 1, 0, 0, 0]
    public static final CameraCharacteristics.Key<byte[]> com_qti_chi_multicamerasensorconfig_sensorsyncmodeconfig;
    //[1]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_camera_supportedfeatures_3rdLightWeightSupported;
    //[2]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_camera_supportedfeatures_AIEnhancementVersion;
    //[1]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_camera_supportedfeatures_HdrBokeh;
    //[1]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_camera_supportedfeatures_TeleOisSupported;
    //[1]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_camera_supportedfeatures_beautyMakeup;
    //[3]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_camera_supportedfeatures_beautyVersion;
    //[0]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_camera_supportedfeatures_colorBokehVersion;
    //[1]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_camera_supportedfeatures_fovcEnable;
    //[1]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_camera_supportedfeatures_isMacroMutexWithHdr;
    //[0]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_camera_supportedfeatures_parallelCameraDevice;
    //[0]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_camera_supportedfeatures_superportraitSupported;
    //[1]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_camera_supportedfeatures_videoBeauty;
    //[1]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_camera_supportedfeatures_videoBokeh;
    //[1]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_camera_supportedfeatures_videoBokehFront;
    //[1]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_camera_supportedfeatures_videoColorRetentionBack;
    //[1]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_camera_supportedfeatures_videoColorRetentionFront;
    //[0]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_camera_supportedfeatures_videobeautyscreenshot;
    //[1]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_camera_supportedfeatures_videofilter;
    //[1]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_camera_supportedfeatures_videologformat;
    //[1]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_camera_supportedfeatures_videomimovie;
    //[0]
    public static final CameraCharacteristics.Key<int[]> com_xiaomi_cameraid_role_cameraId;
    //[0]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_capabilities_algoCameraXEnabled;
    //[4624, 3472, 60, 4624, 2600, 60, 4208, 3120, 60, 4000, 3000, 60, 4096, 2160, 60, 3840, 2160, 60, 3264, 2448, 60, 2592, 1944, 60, 2592, 1940, 60, 2560, 1440, 60, 1920, 1440, 60, 1920, 1080, 60, 1600, 1200, 60, 1560, 720, 60, 1440, 1080, 60, 1280, 960, 60, 1280, 720, 60, 864, 480, 60, 800, 600, 60, 720, 480, 60, 640, 480, 60, 352, 288, 60, 320, 240, 60, 176, 144, 60]
    public static final CameraCharacteristics.Key<int[]> com_xiaomi_capabilities_algoCustomHFRFpsTable;
    //[0]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_capabilities_algoSDKEnabled;
    //[49, 58, 48, 124, 71, 58, 48, 124, 48, 58, 48, 124, 50, 49, 58, 48, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 49, 58, 48, 124, 71, 58, 48, 124, 48, 58, 48, 124, 50, 49, 58, 48, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 49, 58, 48, 124, 71, 58, 48, 124, 48, 58, 48, 124, 50, 49, 58, 48, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 49, 58, 48, 124, 71, 58, 48, 124, 48, 58, 48, 124, 50, 49, 58, 48, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 49, 58, 48, 124, 71, 58, 48, 124, 48, 58, 48, 124, 50, 49, 58, 48, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 49, 58, 48, 124, 71, 58, 48, 124, 48, 58, 48, 124, 50, 51, 58, 48, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_capabilities_algoSupport;
    //[49, 46, 48, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_capabilities_algoVersion;
    //[0]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_gpu_enableGPURotation;
    //[0]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_sessionparams_cameraxConnection;
    //[100, 101, 102, 97, 117, 108, 116, 32, 67, 108, 105, 101, 110, 116, 0]
    public static final CameraCharacteristics.Key<byte[]> com_xiaomi_sessionparams_clientName;
    //[0]
    public static final CameraCharacteristics.Key<int[]> com_xiaomi_sessionparams_operation;
    //[0, 1, 2]
    public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_exposure_metering_available_modes;
    //[256]
    public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_histogram_buckets;
    //[256]
    public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_histogram_buckets_I;
    //[256]
    public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_histogram_buckets_hdr;
    //[256]
    public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_histogram_max_count;
    //[256]
    public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_histogram_max_count_I;
    //[256]
    public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_histogram_max_count_hdr;
    //[0, 1, 2]
    public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_instant_aec_instant_aec_available_modes;
    //[9470, 32110118400]
    public static final CameraCharacteristics.Key<long[]> org_codeaurora_qcamera3_iso_exp_priority_exposure_time_range;
    //[0, 1, 2, 3, 4, 5, 6, 7]
    public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_iso_exp_priority_iso_available_modes;
    //[0]
    public static final CameraCharacteristics.Key<byte[]> org_codeaurora_qcamera3_logicalCameraType_logical_camera_type;
    //[0, 10000]
    public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_manualWB_color_temperature_range;
    //[1.0, 31.99]
    public static final CameraCharacteristics.Key<float[]> org_codeaurora_qcamera3_manualWB_gains_range;
    //[0, 0, 0, 0, 89, 13, 0, 0]
    public static final CameraCharacteristics.Key<byte[]> org_codeaurora_qcamera3_platformCapabilities_IPEICACapabilities;
    //[0, 0, 4624, 3472]
    public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_quadra_cfa_activeArraySize;
    //[33, 9248, 6944, 0, 34, 9248, 6944, 0, 35, 9248, 6944, 0, 35, 9248, 6944, 1, 33, 7680, 4320, 0, 34, 7680, 4320, 0, 35, 7680, 4320, 0, 35, 7680, 4320, 1]
    public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_quadra_cfa_availableStreamConfigurations;
    //[1]
    public static final CameraCharacteristics.Key<byte[]> org_codeaurora_qcamera3_quadra_cfa_is_qcfa_sensor;
    //[9248, 6944]
    public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_quadra_cfa_qcfa_dimension;
    //[0, 10, 5, 1]
    public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_saturation_range;
    //[0, 2]
    public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_sharpness_range;
    //[0]
    public static final CameraCharacteristics.Key<byte[]> org_codeaurora_qcamera3_stats_bsgc_available;
    //[0]
    public static final CameraCharacteristics.Key<byte[]> org_quic_camera_SensorModeFS_isFastShutterModeSupported;
    //[-100.0, 100.0]
    public static final CameraCharacteristics.Key<float[]> org_quic_camera_ltmDynamicContrast_ltmBrightSupressStrengthRange;
    //[-100.0, 100.0]
    public static final CameraCharacteristics.Key<float[]> org_quic_camera_ltmDynamicContrast_ltmDarkBoostStrengthRange;
    //[-100.0, 100.0]
    public static final CameraCharacteristics.Key<float[]> org_quic_camera_ltmDynamicContrast_ltmDynamicContrastStrengthRange;
    //[4624, 3472, 60, 4624, 2600, 60, 4208, 3120, 60, 4000, 3000, 60, 4096, 2160, 60, 3840, 2160, 60, 3264, 2448, 60, 2592, 1944, 60, 2592, 1940, 60, 2560, 1440, 60, 1920, 1440, 60, 1920, 1080, 60, 1600, 1200, 60, 1560, 720, 60, 1440, 1080, 60, 1280, 960, 60, 1280, 720, 60, 864, 480, 60, 800, 600, 60, 720, 480, 60, 640, 480, 60, 352, 288, 60, 320, 240, 60, 176, 144, 60]
    public static final CameraCharacteristics.Key<int[]> org_quic_camera2_customhfrfps_info_CustomHFRFpsTable;
    //[7, 3, 9248, 6944, 30, 4624, 3472, 30, 4624, 2600, 60, 2312, 1300, 120, 2312, 1300, 240, 3296, 2472, 30, 4624, 3472, 60]
    public static final CameraCharacteristics.Key<int[]> org_quic_camera2_sensormode_info_SensorModeTable;
    //[4624, 3472, 30, 60, 4624, 3472, 60, 60, 4624, 2600, 30, 60, 4624, 2600, 60, 60, 4208, 3120, 30, 60, 4208, 3120, 60, 60, 4000, 3000, 30, 60, 4000, 3000, 60, 60, 4096, 2160, 30, 60, 4096, 2160, 60, 60, 3840, 2160, 30, 60, 3840, 2160, 60, 60, 3264, 2448, 30, 60, 3264, 2448, 60, 60, 2592, 1944, 30, 60, 2592, 1944, 60, 60, 2592, 1940, 30, 60, 2592, 1940, 60, 60, 2560, 1440, 30, 60, 2560, 1440, 60, 60, 1920, 1440, 30, 60, 1920, 1440, 60, 60, 1920, 1080, 30, 60, 1920, 1080, 60, 60, 1600, 1200, 30, 60, 1600, 1200, 60, 60, 1560, 720, 30, 60, 1560, 720, 60, 60, 1440, 1080, 30, 60, 1440, 1080, 60, 60, 1280, 960, 30, 60, 1280, 960, 60, 60, 1280, 720, 30, 60, 1280, 720, 60, 60, 864, 480, 30, 60, 864, 480, 60, 60, 800, 600, 30, 60, 800, 600, 60, 60, 720, 480, 30, 60, 720, 480, 60, 60, 640, 480, 30, 60, 640, 480, 60, 60, 352, 288, 30, 60, 352, 288, 60, 60, 320, 240, 30, 60, 320, 240, 60, 60, 176, 144, 30, 60, 176, 144, 60, 60]
    public static final CameraCharacteristics.Key<int[]> org_quic_camera2_streamBasedFPS_info_StreamBasedFPSTable;
    //[3.0]
    public static final CameraCharacteristics.Key<float[]> xiaomi_ai_misd_MiAlgoAsdVersion;
    //[0]
    public static final CameraCharacteristics.Key<byte[]> xiaomi_ai_supportedMoonAutoFocus;
    //[1]
    public static final CameraCharacteristics.Key<byte[]> xiaomi_capabilities_macro_zoom_feature;
    //[1]
    public static final CameraCharacteristics.Key<byte[]> xiaomi_capabilities_mfnr_bokeh_supported;
    //[0]
    public static final CameraCharacteristics.Key<byte[]> xiaomi_capabilities_quick_view_support;
    //[0]
    public static final CameraCharacteristics.Key<byte[]> xiaomi_capabilities_videoStabilization_60fpsDynamicSupported;
    //[0]
    public static final CameraCharacteristics.Key<byte[]> xiaomi_capabilities_videoStabilization_60fpsSupported;
    //[1]
    public static final CameraCharacteristics.Key<byte[]> xiaomi_capabilities_videoStabilization_previewSupported;
    //[1]
    public static final CameraCharacteristics.Key<byte[]> xiaomi_hdr_supportedFlashHdr;
    //[0]
    public static final CameraCharacteristics.Key<byte[]> xiaomi_imageQuality_available;
    //[0]
    public static final CameraCharacteristics.Key<byte[]> xiaomi_pro_video_movie_enabled;
    //[33, 9248, 6944, 0, 34, 9248, 6944, 0, 35, 9248, 6944, 0, 35, 9248, 6944, 1, 33, 7680, 4320, 0, 34, 7680, 4320, 0, 35, 7680, 4320, 0, 35, 7680, 4320, 1, 34, 4624, 3472, 0, 34, 4624, 3472, 1, 35, 4624, 3472, 0, 35, 4624, 3472, 1, 33, 4624, 3472, 0, 34, 4208, 3120, 0, 34, 4208, 3120, 1, 35, 4208, 3120, 0, 35, 4208, 3120, 1, 33, 4208, 3120, 0, 34, 3472, 3472, 0, 34, 3472, 3472, 1, 35, 3472, 3472, 0, 35, 3472, 3472, 1, 33, 3472, 3472, 0, 34, 4624, 2600, 0, 34, 4624, 2600, 1, 35, 4624, 2600, 0, 35, 4624, 2600, 1, 33, 4624, 2600, 0, 34, 4000, 3000, 0, 34, 4000, 3000, 1, 35, 4000, 3000, 0, 35, 4000, 3000, 1, 33, 4000, 3000, 0, 34, 4208, 2368, 0, 34, 4208, 2368, 1, 35, 4208, 2368, 0, 35, 4208, 2368, 1, 33, 4208, 2368, 0, 34, 3120, 3120, 0, 34, 3120, 3120, 1, 35, 3120, 3120, 0, 35, 3120, 3120, 1, 33, 3120, 3120, 0, 34, 4624, 2080, 0, 34, 4624, 2080, 1, 35, 4624, 2080, 0, 35, 4624, 2080, 1, 33, 4624, 2080, 0, 34, 4096, 2160, 0, 34, 4096, 2160, 1, 35, 4096, 2160, 0, 35, 4096, 2160, 1, 33, 4096, 2160, 0, 34, 3840, 2160, 0, 34, 3840, 2160, 1, 35, 3840, 2160, 0, 35, 3840, 2160, 1, 33, 3840, 2160, 0, 34, 3296, 2472, 0, 34, 3296, 2472, 1, 35, 3296, 2472, 0, 35, 3296, 2472, 1, 33, 3296, 2472, 0, 34, 3264, 2448, 0, 34, 3264, 2448, 1, 35, 3264, 2448, 0, 35, 3264, 2448, 1, 33, 3264, 2448, 0, 34, 4208, 1888, 0, 34, 4208, 1888, 1, 35, 4208, 1888, 0, 35, 4208, 1888, 1, 33, 4208, 1888, 0, 34, 3296, 1856, 0, 34, 3296, 1856, 1, 35, 3296, 1856, 0, 35, 3296, 1856, 1, 33, 3296, 1856, 0, 34, 3264, 1836, 0, 34, 3264, 1836, 1, 35, 3264, 1836, 0, 35, 3264, 1836, 1, 33, 3264, 1836, 0, 34, 2448, 2448, 0, 34, 2448, 2448, 1, 35, 2448, 2448, 0, 35, 2448, 2448, 1, 33, 2448, 2448, 0, 34, 2592, 1944, 0, 34, 2592, 1944, 1, 35, 2592, 1944, 0, 35, 2592, 1944, 1, 33, 2592, 1944, 0, 34, 2592, 1940, 0, 34, 2592, 1940, 1, 35, 2592, 1940, 0, 35, 2592, 1940, 1, 33, 2592, 1940, 0, 34, 3296, 1488, 0, 34, 3296, 1488, 1, 35, 3296, 1488, 0, 35, 3296, 1488, 1, 33, 3296, 1488, 0, 34, 3264, 1468, 0, 34, 3264, 1468, 1, 35, 3264, 1468, 0, 35, 3264, 1468, 1, 33, 3264, 1468, 0, 34, 1940, 1940, 0, 34, 1940, 1940, 1, 35, 1940, 1940, 0, 35, 1940, 1940, 1, 33, 1940, 1940, 0, 34, 2560, 1440, 0, 34, 2560, 1440, 1, 35, 2560, 1440, 0, 35, 2560, 1440, 1, 33, 2560, 1440, 0, 34, 2560, 1152, 0, 34, 2560, 1152, 1, 35, 2560, 1152, 0, 35, 2560, 1152, 1, 33, 2560, 1152, 0, 34, 1920, 1440, 0, 34, 1920, 1440, 1, 35, 1920, 1440, 0, 35, 1920, 1440, 1, 33, 1920, 1440, 0, 34, 2400, 1080, 0, 34, 2400, 1080, 1, 35, 2400, 1080, 0, 35, 2400, 1080, 1, 33, 2400, 1080, 0, 34, 2160, 1080, 0, 34, 2160, 1080, 1, 35, 2160, 1080, 0, 35, 2160, 1080, 1, 33, 2160, 1080, 0, 34, 1920, 1080, 0, 34, 1920, 1080, 1, 35, 1920, 1080, 0, 35, 1920, 1080, 1, 33, 1920, 1080, 0, 34, 2080, 960, 0, 34, 2080, 960, 1, 35, 2080, 960, 0, 35, 2080, 960, 1, 33, 2080, 960, 0, 34, 1600, 1200, 0, 34, 1600, 1200, 1, 35, 1600, 1200, 0, 35, 1600, 1200, 1, 33, 1600, 1200, 0, 34, 1440, 1080, 0, 34, 1440, 1080, 1, 35, 1440, 1080, 0, 35, 1440, 1080, 1, 33, 1440, 1080, 0, 34, 1600, 900, 0, 34, 1600, 900, 1, 35, 1600, 900, 0, 35, 1600, 900, 1, 33, 1600, 900, 0, 34, 1280, 960, 0, 34, 1280, 960, 1, 35, 1280, 960, 0, 35, 1280, 960, 1, 33, 1280, 960, 0, 34, 1600, 720, 0, 34, 1600, 720, 1, 35, 1600, 720, 0, 35, 1600, 720, 1, 33, 1600, 720, 0, 34, 1560, 720, 0, 34, 1560, 720, 1, 35, 1560, 720, 0, 35, 1560, 720, 1, 33, 1560, 720, 0, 34, 1440, 720, 0, 34, 1440, 720, 1, 35, 1440, 720, 0, 35, 1440, 720, 1, 33, 1440, 720, 0, 34, 1280, 720, 0, 34, 1280, 720, 1, 35, 1280, 720, 0, 35, 1280, 720, 1, 33, 1280, 720, 0, 34, 800, 600, 0, 34, 800, 600, 1, 35, 800, 600, 0, 35, 800, 600, 1, 33, 800, 600, 0, 34, 864, 480, 0, 34, 864, 480, 1, 35, 864, 480, 0, 35, 864, 480, 1, 33, 864, 480, 0, 34, 720, 480, 0, 34, 720, 480, 1, 35, 720, 480, 0, 35, 720, 480, 1, 33, 720, 480, 0, 34, 640, 480, 0, 34, 640, 480, 1, 35, 640, 480, 0, 35, 640, 480, 1, 33, 640, 480, 0, 34, 352, 288, 0, 34, 352, 288, 1, 35, 352, 288, 0, 35, 352, 288, 1, 34, 320, 240, 0, 34, 320, 240, 1, 35, 320, 240, 0, 35, 320, 240, 1, 34, 176, 144, 0, 34, 176, 144, 1, 35, 176, 144, 0, 35, 176, 144, 1, 37, 4624, 3472, 0, 32, 4624, 3472, 0, 36, 4624, 3472, 0]
    public static final CameraCharacteristics.Key<int[]> xiaomi_scaler_availableHeicStreamConfigurations;
    //[33, 7680, 4320, 0, 34, 7680, 4320, 0, 34, 4624, 3472, 0, 34, 4624, 3472, 1, 35, 4624, 3472, 0, 35, 4624, 3472, 1, 33, 4624, 3472, 0, 34, 4208, 3120, 0, 34, 4208, 3120, 1, 35, 4208, 3120, 0, 35, 4208, 3120, 1, 33, 4208, 3120, 0, 34, 3472, 3472, 0, 34, 3472, 3472, 1, 35, 3472, 3472, 0, 35, 3472, 3472, 1, 33, 3472, 3472, 0, 34, 4624, 2600, 0, 34, 4624, 2600, 1, 35, 4624, 2600, 0, 35, 4624, 2600, 1, 33, 4624, 2600, 0, 34, 4000, 3000, 0, 34, 4000, 3000, 1, 35, 4000, 3000, 0, 35, 4000, 3000, 1, 33, 4000, 3000, 0, 34, 4208, 2368, 0, 34, 4208, 2368, 1, 35, 4208, 2368, 0, 35, 4208, 2368, 1, 33, 4208, 2368, 0, 34, 3120, 3120, 0, 34, 3120, 3120, 1, 35, 3120, 3120, 0, 35, 3120, 3120, 1, 33, 3120, 3120, 0, 34, 4624, 2080, 0, 34, 4624, 2080, 1, 35, 4624, 2080, 0, 35, 4624, 2080, 1, 33, 4624, 2080, 0, 34, 4096, 2160, 0, 34, 4096, 2160, 1, 35, 4096, 2160, 0, 35, 4096, 2160, 1, 33, 4096, 2160, 0, 34, 3840, 2160, 0, 34, 3840, 2160, 1, 35, 3840, 2160, 0, 35, 3840, 2160, 1, 33, 3840, 2160, 0, 34, 3296, 2472, 0, 34, 3296, 2472, 1, 35, 3296, 2472, 0, 35, 3296, 2472, 1, 33, 3296, 2472, 0, 34, 3264, 2448, 0, 34, 3264, 2448, 1, 35, 3264, 2448, 0, 35, 3264, 2448, 1, 33, 3264, 2448, 0, 34, 4208, 1888, 0, 34, 4208, 1888, 1, 35, 4208, 1888, 0, 35, 4208, 1888, 1, 33, 4208, 1888, 0, 34, 3296, 1856, 0, 34, 3296, 1856, 1, 35, 3296, 1856, 0, 35, 3296, 1856, 1, 33, 3296, 1856, 0, 34, 3264, 1836, 0, 34, 3264, 1836, 1, 35, 3264, 1836, 0, 35, 3264, 1836, 1, 33, 3264, 1836, 0, 34, 2448, 2448, 0, 34, 2448, 2448, 1, 35, 2448, 2448, 0, 35, 2448, 2448, 1, 33, 2448, 2448, 0, 34, 2592, 1944, 0, 34, 2592, 1944, 1, 35, 2592, 1944, 0, 35, 2592, 1944, 1, 33, 2592, 1944, 0, 34, 2592, 1940, 0, 34, 2592, 1940, 1, 35, 2592, 1940, 0, 35, 2592, 1940, 1, 33, 2592, 1940, 0, 34, 3296, 1488, 0, 34, 3296, 1488, 1, 35, 3296, 1488, 0, 35, 3296, 1488, 1, 33, 3296, 1488, 0, 34, 3264, 1468, 0, 34, 3264, 1468, 1, 35, 3264, 1468, 0, 35, 3264, 1468, 1, 33, 3264, 1468, 0, 34, 1940, 1940, 0, 34, 1940, 1940, 1, 35, 1940, 1940, 0, 35, 1940, 1940, 1, 33, 1940, 1940, 0, 34, 2560, 1440, 0, 34, 2560, 1440, 1, 35, 2560, 1440, 0, 35, 2560, 1440, 1, 33, 2560, 1440, 0, 34, 2560, 1152, 0, 34, 2560, 1152, 1, 35, 2560, 1152, 0, 35, 2560, 1152, 1, 33, 2560, 1152, 0, 34, 1920, 1440, 0, 34, 1920, 1440, 1, 35, 1920, 1440, 0, 35, 1920, 1440, 1, 33, 1920, 1440, 0, 34, 2400, 1080, 0, 34, 2400, 1080, 1, 35, 2400, 1080, 0, 35, 2400, 1080, 1, 33, 2400, 1080, 0, 34, 2160, 1080, 0, 34, 2160, 1080, 1, 35, 2160, 1080, 0, 35, 2160, 1080, 1, 33, 2160, 1080, 0, 34, 1920, 1080, 0, 34, 1920, 1080, 1, 35, 1920, 1080, 0, 35, 1920, 1080, 1, 33, 1920, 1080, 0, 34, 2080, 960, 0, 34, 2080, 960, 1, 35, 2080, 960, 0, 35, 2080, 960, 1, 33, 2080, 960, 0, 34, 1600, 1200, 0, 34, 1600, 1200, 1, 35, 1600, 1200, 0, 35, 1600, 1200, 1, 33, 1600, 1200, 0, 34, 1440, 1080, 0, 34, 1440, 1080, 1, 35, 1440, 1080, 0, 35, 1440, 1080, 1, 33, 1440, 1080, 0, 34, 1600, 900, 0, 34, 1600, 900, 1, 35, 1600, 900, 0, 35, 1600, 900, 1, 33, 1600, 900, 0, 34, 1280, 960, 0, 34, 1280, 960, 1, 35, 1280, 960, 0, 35, 1280, 960, 1, 33, 1280, 960, 0, 34, 1600, 720, 0, 34, 1600, 720, 1, 35, 1600, 720, 0, 35, 1600, 720, 1, 33, 1600, 720, 0, 34, 1560, 720, 0, 34, 1560, 720, 1, 35, 1560, 720, 0, 35, 1560, 720, 1, 33, 1560, 720, 0, 34, 1440, 720, 0, 34, 1440, 720, 1, 35, 1440, 720, 0, 35, 1440, 720, 1, 33, 1440, 720, 0, 34, 1280, 720, 0, 34, 1280, 720, 1, 35, 1280, 720, 0, 35, 1280, 720, 1, 33, 1280, 720, 0, 34, 800, 600, 0, 34, 800, 600, 1, 35, 800, 600, 0, 35, 800, 600, 1, 33, 800, 600, 0, 34, 864, 480, 0, 34, 864, 480, 1, 35, 864, 480, 0, 35, 864, 480, 1, 33, 864, 480, 0, 34, 720, 480, 0, 34, 720, 480, 1, 35, 720, 480, 0, 35, 720, 480, 1, 33, 720, 480, 0, 34, 640, 480, 0, 34, 640, 480, 1, 35, 640, 480, 0, 35, 640, 480, 1, 33, 640, 480, 0, 34, 352, 288, 0, 34, 352, 288, 1, 35, 352, 288, 0, 35, 352, 288, 1, 33, 352, 288, 0, 34, 320, 240, 0, 34, 320, 240, 1, 35, 320, 240, 0, 35, 320, 240, 1, 33, 320, 240, 0, 34, 176, 144, 0, 34, 176, 144, 1, 35, 176, 144, 0, 35, 176, 144, 1, 37, 4624, 3472, 0, 32, 4624, 3472, 0, 36, 4624, 3472, 0]
    public static final CameraCharacteristics.Key<int[]> xiaomi_scaler_availableLimitStreamConfigurations;
    //[33, 9248, 6944, 0, 34, 9248, 6944, 0, 35, 9248, 6944, 0, 35, 9248, 6944, 1, 33, 7680, 4320, 0, 34, 7680, 4320, 0, 35, 7680, 4320, 0, 35, 7680, 4320, 1, 34, 4624, 3472, 0, 34, 4624, 3472, 1, 35, 4624, 3472, 0, 35, 4624, 3472, 1, 33, 4624, 3472, 0, 34, 4208, 3120, 0, 34, 4208, 3120, 1, 35, 4208, 3120, 0, 35, 4208, 3120, 1, 33, 4208, 3120, 0, 34, 3472, 3472, 0, 34, 3472, 3472, 1, 35, 3472, 3472, 0, 35, 3472, 3472, 1, 33, 3472, 3472, 0, 34, 4624, 2600, 0, 34, 4624, 2600, 1, 35, 4624, 2600, 0, 35, 4624, 2600, 1, 33, 4624, 2600, 0, 34, 4000, 3000, 0, 34, 4000, 3000, 1, 35, 4000, 3000, 0, 35, 4000, 3000, 1, 33, 4000, 3000, 0, 34, 4208, 2368, 0, 34, 4208, 2368, 1, 35, 4208, 2368, 0, 35, 4208, 2368, 1, 33, 4208, 2368, 0, 34, 3120, 3120, 0, 34, 3120, 3120, 1, 35, 3120, 3120, 0, 35, 3120, 3120, 1, 33, 3120, 3120, 0, 34, 4624, 2080, 0, 34, 4624, 2080, 1, 35, 4624, 2080, 0, 35, 4624, 2080, 1, 33, 4624, 2080, 0, 34, 4096, 2160, 0, 34, 4096, 2160, 1, 35, 4096, 2160, 0, 35, 4096, 2160, 1, 33, 4096, 2160, 0, 34, 3840, 2160, 0, 34, 3840, 2160, 1, 35, 3840, 2160, 0, 35, 3840, 2160, 1, 33, 3840, 2160, 0, 34, 3296, 2472, 0, 34, 3296, 2472, 1, 35, 3296, 2472, 0, 35, 3296, 2472, 1, 33, 3296, 2472, 0, 34, 3264, 2448, 0, 34, 3264, 2448, 1, 35, 3264, 2448, 0, 35, 3264, 2448, 1, 33, 3264, 2448, 0, 34, 4208, 1888, 0, 34, 4208, 1888, 1, 35, 4208, 1888, 0, 35, 4208, 1888, 1, 33, 4208, 1888, 0, 34, 3296, 1856, 0, 34, 3296, 1856, 1, 35, 3296, 1856, 0, 35, 3296, 1856, 1, 33, 3296, 1856, 0, 34, 3264, 1836, 0, 34, 3264, 1836, 1, 35, 3264, 1836, 0, 35, 3264, 1836, 1, 33, 3264, 1836, 0, 34, 2448, 2448, 0, 34, 2448, 2448, 1, 35, 2448, 2448, 0, 35, 2448, 2448, 1, 33, 2448, 2448, 0, 34, 2592, 1944, 0, 34, 2592, 1944, 1, 35, 2592, 1944, 0, 35, 2592, 1944, 1, 33, 2592, 1944, 0, 34, 2592, 1940, 0, 34, 2592, 1940, 1, 35, 2592, 1940, 0, 35, 2592, 1940, 1, 33, 2592, 1940, 0, 34, 3296, 1488, 0, 34, 3296, 1488, 1, 35, 3296, 1488, 0, 35, 3296, 1488, 1, 33, 3296, 1488, 0, 34, 3264, 1468, 0, 34, 3264, 1468, 1, 35, 3264, 1468, 0, 35, 3264, 1468, 1, 33, 3264, 1468, 0, 34, 1940, 1940, 0, 34, 1940, 1940, 1, 35, 1940, 1940, 0, 35, 1940, 1940, 1, 33, 1940, 1940, 0, 34, 2560, 1440, 0, 34, 2560, 1440, 1, 35, 2560, 1440, 0, 35, 2560, 1440, 1, 33, 2560, 1440, 0, 34, 2560, 1152, 0, 34, 2560, 1152, 1, 35, 2560, 1152, 0, 35, 2560, 1152, 1, 33, 2560, 1152, 0, 34, 1920, 1440, 0, 34, 1920, 1440, 1, 35, 1920, 1440, 0, 35, 1920, 1440, 1, 33, 1920, 1440, 0, 34, 2400, 1080, 0, 34, 2400, 1080, 1, 35, 2400, 1080, 0, 35, 2400, 1080, 1, 33, 2400, 1080, 0, 34, 2160, 1080, 0, 34, 2160, 1080, 1, 35, 2160, 1080, 0, 35, 2160, 1080, 1, 33, 2160, 1080, 0, 34, 1920, 1080, 0, 34, 1920, 1080, 1, 35, 1920, 1080, 0, 35, 1920, 1080, 1, 33, 1920, 1080, 0, 34, 2080, 960, 0, 34, 2080, 960, 1, 35, 2080, 960, 0, 35, 2080, 960, 1, 33, 2080, 960, 0, 34, 1600, 1200, 0, 34, 1600, 1200, 1, 35, 1600, 1200, 0, 35, 1600, 1200, 1, 33, 1600, 1200, 0, 34, 1440, 1080, 0, 34, 1440, 1080, 1, 35, 1440, 1080, 0, 35, 1440, 1080, 1, 33, 1440, 1080, 0, 34, 1600, 900, 0, 34, 1600, 900, 1, 35, 1600, 900, 0, 35, 1600, 900, 1, 33, 1600, 900, 0, 34, 1280, 960, 0, 34, 1280, 960, 1, 35, 1280, 960, 0, 35, 1280, 960, 1, 33, 1280, 960, 0, 34, 1600, 720, 0, 34, 1600, 720, 1, 35, 1600, 720, 0, 35, 1600, 720, 1, 33, 1600, 720, 0, 34, 1560, 720, 0, 34, 1560, 720, 1, 35, 1560, 720, 0, 35, 1560, 720, 1, 33, 1560, 720, 0, 34, 1440, 720, 0, 34, 1440, 720, 1, 35, 1440, 720, 0, 35, 1440, 720, 1, 33, 1440, 720, 0, 34, 1280, 720, 0, 34, 1280, 720, 1, 35, 1280, 720, 0, 35, 1280, 720, 1, 33, 1280, 720, 0, 34, 800, 600, 0, 34, 800, 600, 1, 35, 800, 600, 0, 35, 800, 600, 1, 33, 800, 600, 0, 34, 864, 480, 0, 34, 864, 480, 1, 35, 864, 480, 0, 35, 864, 480, 1, 33, 864, 480, 0, 34, 720, 480, 0, 34, 720, 480, 1, 35, 720, 480, 0, 35, 720, 480, 1, 33, 720, 480, 0, 34, 640, 480, 0, 34, 640, 480, 1, 35, 640, 480, 0, 35, 640, 480, 1, 33, 640, 480, 0, 34, 352, 288, 0, 34, 352, 288, 1, 35, 352, 288, 0, 35, 352, 288, 1, 34, 320, 240, 0, 34, 320, 240, 1, 35, 320, 240, 0, 35, 320, 240, 1, 34, 176, 144, 0, 34, 176, 144, 1, 35, 176, 144, 0, 35, 176, 144, 1, 37, 4624, 3472, 0, 32, 4624, 3472, 0, 36, 4624, 3472, 0]
    public static final CameraCharacteristics.Key<int[]> xiaomi_scaler_availableStreamConfigurations;
    //[34, 9248, 6944, 0, 34, 9248, 6944, 1, 35, 9248, 6944, 0, 35, 9248, 6944, 1, 33, 9248, 6944, 0, 34, 1920, 1440, 0, 34, 1920, 1440, 1, 35, 1920, 1440, 0, 35, 1920, 1440, 1, 33, 1920, 1440, 0]
    public static final CameraCharacteristics.Key<int[]> xiaomi_scaler_availableSuperResolutionStreamConfigurations;
    //[0]
    public static final CameraCharacteristics.Key<byte[]> xiaomi_smoothTransition_nearRangeMode;
    //[0]
    public static final CameraCharacteristics.Key<int[]> xiaomi_videohdrmode_value;
    static {
        com_qti_chi_multicamerasensorconfig_sensorsyncmodeconfig= getKeyType("com.qti.chi.multicamerasensorconfig.sensorsyncmodeconfig", byte[].class);
        com_xiaomi_camera_supportedfeatures_3rdLightWeightSupported= getKeyType("com.xiaomi.camera.supportedfeatures.3rdLightWeightSupported", byte[].class);
        com_xiaomi_camera_supportedfeatures_AIEnhancementVersion= getKeyType("com.xiaomi.camera.supportedfeatures.AIEnhancementVersion", byte[].class);
        com_xiaomi_camera_supportedfeatures_HdrBokeh= getKeyType("com.xiaomi.camera.supportedfeatures.HdrBokeh", byte[].class);
        com_xiaomi_camera_supportedfeatures_TeleOisSupported= getKeyType("com.xiaomi.camera.supportedfeatures.TeleOisSupported", byte[].class);
        com_xiaomi_camera_supportedfeatures_beautyMakeup= getKeyType("com.xiaomi.camera.supportedfeatures.beautyMakeup", byte[].class);
        com_xiaomi_camera_supportedfeatures_beautyVersion= getKeyType("com.xiaomi.camera.supportedfeatures.beautyVersion", byte[].class);
        com_xiaomi_camera_supportedfeatures_colorBokehVersion= getKeyType("com.xiaomi.camera.supportedfeatures.colorBokehVersion", byte[].class);
        com_xiaomi_camera_supportedfeatures_fovcEnable= getKeyType("com.xiaomi.camera.supportedfeatures.fovcEnable", byte[].class);
        com_xiaomi_camera_supportedfeatures_isMacroMutexWithHdr= getKeyType("com.xiaomi.camera.supportedfeatures.isMacroMutexWithHdr", byte[].class);
        com_xiaomi_camera_supportedfeatures_parallelCameraDevice= getKeyType("com.xiaomi.camera.supportedfeatures.parallelCameraDevice", byte[].class);
        com_xiaomi_camera_supportedfeatures_superportraitSupported= getKeyType("com.xiaomi.camera.supportedfeatures.superportraitSupported", byte[].class);
        com_xiaomi_camera_supportedfeatures_videoBeauty= getKeyType("com.xiaomi.camera.supportedfeatures.videoBeauty", byte[].class);
        com_xiaomi_camera_supportedfeatures_videoBokeh= getKeyType("com.xiaomi.camera.supportedfeatures.videoBokeh", byte[].class);
        com_xiaomi_camera_supportedfeatures_videoBokehFront= getKeyType("com.xiaomi.camera.supportedfeatures.videoBokehFront", byte[].class);
        com_xiaomi_camera_supportedfeatures_videoColorRetentionBack= getKeyType("com.xiaomi.camera.supportedfeatures.videoColorRetentionBack", byte[].class);
        com_xiaomi_camera_supportedfeatures_videoColorRetentionFront= getKeyType("com.xiaomi.camera.supportedfeatures.videoColorRetentionFront", byte[].class);
        com_xiaomi_camera_supportedfeatures_videobeautyscreenshot= getKeyType("com.xiaomi.camera.supportedfeatures.videobeautyscreenshot", byte[].class);
        com_xiaomi_camera_supportedfeatures_videofilter= getKeyType("com.xiaomi.camera.supportedfeatures.videofilter", byte[].class);
        com_xiaomi_camera_supportedfeatures_videologformat= getKeyType("com.xiaomi.camera.supportedfeatures.videologformat", byte[].class);
        com_xiaomi_camera_supportedfeatures_videomimovie= getKeyType("com.xiaomi.camera.supportedfeatures.videomimovie", byte[].class);
        com_xiaomi_cameraid_role_cameraId= getKeyType("com.xiaomi.cameraid.role.cameraId", int[].class);
        com_xiaomi_capabilities_algoCameraXEnabled= getKeyType("com.xiaomi.capabilities.algoCameraXEnabled", byte[].class);
        com_xiaomi_capabilities_algoCustomHFRFpsTable= getKeyType("com.xiaomi.capabilities.algoCustomHFRFpsTable", int[].class);
        com_xiaomi_capabilities_algoSDKEnabled= getKeyType("com.xiaomi.capabilities.algoSDKEnabled", byte[].class);
        com_xiaomi_capabilities_algoSupport= getKeyType("com.xiaomi.capabilities.algoSupport", byte[].class);
        com_xiaomi_capabilities_algoVersion= getKeyType("com.xiaomi.capabilities.algoVersion", byte[].class);
        com_xiaomi_gpu_enableGPURotation= getKeyType("com.xiaomi.gpu.enableGPURotation", byte[].class);
        com_xiaomi_sessionparams_cameraxConnection= getKeyType("com.xiaomi.sessionparams.cameraxConnection", byte[].class);
        com_xiaomi_sessionparams_clientName= getKeyType("com.xiaomi.sessionparams.clientName", byte[].class);
        com_xiaomi_sessionparams_operation= getKeyType("com.xiaomi.sessionparams.operation", int[].class);
        org_codeaurora_qcamera3_exposure_metering_available_modes= getKeyType("org.codeaurora.qcamera3.exposure_metering.available_modes", int[].class);
        org_codeaurora_qcamera3_histogram_buckets= getKeyType("org.codeaurora.qcamera3.histogram.buckets", int[].class);
        org_codeaurora_qcamera3_histogram_buckets_I= getKeyType("org.codeaurora.qcamera3.histogram.buckets_I", int[].class);
        org_codeaurora_qcamera3_histogram_buckets_hdr= getKeyType("org.codeaurora.qcamera3.histogram.buckets_hdr", int[].class);
        org_codeaurora_qcamera3_histogram_max_count= getKeyType("org.codeaurora.qcamera3.histogram.max_count", int[].class);
        org_codeaurora_qcamera3_histogram_max_count_I= getKeyType("org.codeaurora.qcamera3.histogram.max_count_I", int[].class);
        org_codeaurora_qcamera3_histogram_max_count_hdr= getKeyType("org.codeaurora.qcamera3.histogram.max_count_hdr", int[].class);
        org_codeaurora_qcamera3_instant_aec_instant_aec_available_modes= getKeyType("org.codeaurora.qcamera3.instant_aec.instant_aec_available_modes", int[].class);
        org_codeaurora_qcamera3_iso_exp_priority_exposure_time_range= getKeyType("org.codeaurora.qcamera3.iso_exp_priority.exposure_time_range", long[].class);
        org_codeaurora_qcamera3_iso_exp_priority_iso_available_modes= getKeyType("org.codeaurora.qcamera3.iso_exp_priority.iso_available_modes", int[].class);
        org_codeaurora_qcamera3_logicalCameraType_logical_camera_type= getKeyType("org.codeaurora.qcamera3.logicalCameraType.logical_camera_type", byte[].class);
        org_codeaurora_qcamera3_manualWB_color_temperature_range= getKeyType("org.codeaurora.qcamera3.manualWB.color_temperature_range", int[].class);
        org_codeaurora_qcamera3_manualWB_gains_range= getKeyType("org.codeaurora.qcamera3.manualWB.gains_range", float[].class);
        org_codeaurora_qcamera3_platformCapabilities_IPEICACapabilities= getKeyType("org.codeaurora.qcamera3.platformCapabilities.IPEICACapabilities", byte[].class);
        org_codeaurora_qcamera3_quadra_cfa_activeArraySize= getKeyType("org.codeaurora.qcamera3.quadra_cfa.activeArraySize", int[].class);
        org_codeaurora_qcamera3_quadra_cfa_availableStreamConfigurations= getKeyType("org.codeaurora.qcamera3.quadra_cfa.availableStreamConfigurations", int[].class);
        org_codeaurora_qcamera3_quadra_cfa_is_qcfa_sensor= getKeyType("org.codeaurora.qcamera3.quadra_cfa.is_qcfa_sensor", byte[].class);
        org_codeaurora_qcamera3_quadra_cfa_qcfa_dimension= getKeyType("org.codeaurora.qcamera3.quadra_cfa.qcfa_dimension", int[].class);
        org_codeaurora_qcamera3_saturation_range= getKeyType("org.codeaurora.qcamera3.saturation.range", int[].class);
        org_codeaurora_qcamera3_sharpness_range= getKeyType("org.codeaurora.qcamera3.sharpness.range", int[].class);
        org_codeaurora_qcamera3_stats_bsgc_available= getKeyType("org.codeaurora.qcamera3.stats.bsgc_available", byte[].class);
        org_quic_camera_SensorModeFS_isFastShutterModeSupported= getKeyType("org.quic.camera.SensorModeFS.isFastShutterModeSupported", byte[].class);
        org_quic_camera_ltmDynamicContrast_ltmBrightSupressStrengthRange= getKeyType("org.quic.camera.ltmDynamicContrast.ltmBrightSupressStrengthRange", float[].class);
        org_quic_camera_ltmDynamicContrast_ltmDarkBoostStrengthRange= getKeyType("org.quic.camera.ltmDynamicContrast.ltmDarkBoostStrengthRange", float[].class);
        org_quic_camera_ltmDynamicContrast_ltmDynamicContrastStrengthRange= getKeyType("org.quic.camera.ltmDynamicContrast.ltmDynamicContrastStrengthRange", float[].class);
        org_quic_camera2_customhfrfps_info_CustomHFRFpsTable= getKeyType("org.quic.camera2.customhfrfps.info.CustomHFRFpsTable", int[].class);
        org_quic_camera2_sensormode_info_SensorModeTable= getKeyType("org.quic.camera2.sensormode.info.SensorModeTable", int[].class);
        org_quic_camera2_streamBasedFPS_info_StreamBasedFPSTable= getKeyType("org.quic.camera2.streamBasedFPS.info.StreamBasedFPSTable", int[].class);
        xiaomi_ai_misd_MiAlgoAsdVersion= getKeyType("xiaomi.ai.misd.MiAlgoAsdVersion", float[].class);
        xiaomi_ai_supportedMoonAutoFocus= getKeyType("xiaomi.ai.supportedMoonAutoFocus", byte[].class);
        xiaomi_capabilities_macro_zoom_feature= getKeyType("xiaomi.capabilities.macro_zoom_feature", byte[].class);
        xiaomi_capabilities_mfnr_bokeh_supported= getKeyType("xiaomi.capabilities.mfnr_bokeh_supported", byte[].class);
        xiaomi_capabilities_quick_view_support= getKeyType("xiaomi.capabilities.quick_view_support", byte[].class);
        xiaomi_capabilities_videoStabilization_60fpsDynamicSupported= getKeyType("xiaomi.capabilities.videoStabilization.60fpsDynamicSupported", byte[].class);
        xiaomi_capabilities_videoStabilization_60fpsSupported= getKeyType("xiaomi.capabilities.videoStabilization.60fpsSupported", byte[].class);
        xiaomi_capabilities_videoStabilization_previewSupported= getKeyType("xiaomi.capabilities.videoStabilization.previewSupported", byte[].class);
        xiaomi_hdr_supportedFlashHdr= getKeyType("xiaomi.hdr.supportedFlashHdr", byte[].class);
        xiaomi_imageQuality_available= getKeyType("xiaomi.imageQuality.available", byte[].class);
        xiaomi_pro_video_movie_enabled= getKeyType("xiaomi.pro.video.movie.enabled", byte[].class);
        xiaomi_scaler_availableHeicStreamConfigurations= getKeyType("xiaomi.scaler.availableHeicStreamConfigurations", int[].class);
        xiaomi_scaler_availableLimitStreamConfigurations= getKeyType("xiaomi.scaler.availableLimitStreamConfigurations", int[].class);
        xiaomi_scaler_availableStreamConfigurations= getKeyType("xiaomi.scaler.availableStreamConfigurations", int[].class);
        xiaomi_scaler_availableSuperResolutionStreamConfigurations= getKeyType("xiaomi.scaler.availableSuperResolutionStreamConfigurations", int[].class);
        xiaomi_smoothTransition_nearRangeMode= getKeyType("xiaomi.smoothTransition.nearRangeMode", byte[].class);
        xiaomi_videohdrmode_value= getKeyType("xiaomi.videohdrmode.value", int[].class);
    }
}
