package camera2_hidden_keys.devices.s6tab;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.AbstractCameraCharacteristics;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraCharacteristicsDump extends AbstractCameraCharacteristics
{
//[0, 1, 2]
public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_exposure_metering_available_modes;
//[256]
public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_histogram_buckets;
//[256]
public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_histogram_max_count;
//[0, 1, 2]
public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_instant_aec_instant_aec_available_modes;
//[26490, 158052000]
public static final CameraCharacteristics.Key<long[]> org_codeaurora_qcamera3_iso_exp_priority_exposure_time_range;
//[0, 1, 2, 3, 4, 5, 6, 7]
public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_iso_exp_priority_iso_available_modes;
//[0]
public static final CameraCharacteristics.Key<byte[]> org_codeaurora_qcamera3_logicalCameraType_logical_camera_type;
//[0, 10000]
public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_manualWB_color_temperature_range;
//[1.0, 31.99]
public static final CameraCharacteristics.Key<float[]> org_codeaurora_qcamera3_manualWB_gains_range;
//[0, 0]
public static final CameraCharacteristics.Key<byte[]> org_codeaurora_qcamera3_platformCapabilities_IPEICACapabilities;
//[0, 10, 5, 1]
public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_saturation_range;
//[0, 6]
public static final CameraCharacteristics.Key<int[]> org_codeaurora_qcamera3_sharpness_range;
//[9, 3, 2608, 1956, 30, 2608, 1630, 30, 1968, 1968, 30, 2608, 1468, 30, 3264, 2448, 30, 800, 600, 119, 3264, 2040, 30, 3264, 1836, 30, 2448, 2448, 30]
public static final CameraCharacteristics.Key<int[]> org_quic_camera2_sensormode_info_SensorModeTable;
//[0, 1, 101, 102]
public static final CameraCharacteristics.Key<byte[]> samsung_android_control_aeAvailableModes;
//[0]
public static final CameraCharacteristics.Key<byte[]> samsung_android_control_afAvailableModes;
//[10, 20]
public static final CameraCharacteristics.Key<int[]> samsung_android_control_availableBurstShotFps;
//[0, 1, 2, 3, 4, 5, 6, 7, 8, 101]
public static final CameraCharacteristics.Key<byte[]> samsung_android_control_availableEffects;
//[0, 6, 7, 18, 8, 9, 11, 27, 15, 14, 21, 17, 201, 202, 49]
public static final CameraCharacteristics.Key<int[]> samsung_android_control_availableFeatures;
//[0, 1, 2, 3, 4, 5, 6, 7, 8, 101]
public static final CameraCharacteristics.Key<int[]> samsung_android_control_awbAvailableModes;
//[0]
public static final CameraCharacteristics.Key<int[]> samsung_android_control_beautyFaceRetouchLevel;
//[0]
public static final CameraCharacteristics.Key<int[]> samsung_android_control_beautyFaceSkinColor;
//[0]
public static final CameraCharacteristics.Key<int[]> samsung_android_control_cameraClient;
//[0]
public static final CameraCharacteristics.Key<int[]> samsung_android_control_cameraType;
//[2300, 10000]
public static final CameraCharacteristics.Key<int[]> samsung_android_control_colorTemperatureRange;
//[10, 30, 30, 30]
public static final CameraCharacteristics.Key<int[]> samsung_android_control_effectAeAvailableTargetFpsRanges;
//[0, 1]
public static final CameraCharacteristics.Key<int[]> samsung_android_control_handGestureAvailableTypes;
//[0, 2]
public static final CameraCharacteristics.Key<int[]> samsung_android_control_liveHdrAvailableModes;
//[0, 1]
public static final CameraCharacteristics.Key<int[]> samsung_android_control_liveHdrLevelRange;
//[0.0, 0.0, 0.0, -2.0]
public static final CameraCharacteristics.Key<float[]> samsung_android_control_llHdrEvCompensationList;
//[0]
public static final CameraCharacteristics.Key<int[]> samsung_android_control_llsValue;
//[0, 1, 2, 3, 4, 5, 6]
public static final CameraCharacteristics.Key<int[]> samsung_android_control_meteringAvailableMode;
//[0.0, -2.0, -3.0]
public static final CameraCharacteristics.Key<float[]> samsung_android_control_mfHdrEvCompensationList;
//[50462976, 117835012]
public static final CameraCharacteristics.Key<int[]> samsung_android_control_multiFrame;
//[0]
public static final CameraCharacteristics.Key<byte[]> samsung_android_control_pafAvailableMode;
//[0]
public static final CameraCharacteristics.Key<byte[]> samsung_android_control_previousAe;
//[0]
public static final CameraCharacteristics.Key<byte[]> samsung_android_control_productColorInfo;
//[0]
public static final CameraCharacteristics.Key<int[]> samsung_android_control_recordingSuggestedMotionSpeedMode;
//[0]
public static final CameraCharacteristics.Key<byte[]> samsung_android_control_samsungSDK;
//[1280, 720, 368, 1920, 1080, 160]
public static final CameraCharacteristics.Key<int[]> samsung_android_control_ssmRecordableImageCount;
//[0]
public static final CameraCharacteristics.Key<int[]> samsung_android_control_strBvOffset;
//[0]
public static final CameraCharacteristics.Key<byte[]> samsung_android_control_swSuperVideoStabilization;
//[0, 0]
public static final CameraCharacteristics.Key<int[]> samsung_android_control_wbLevelRange;
//[512, 320, 512, 248, 496, 240, 480, 288, 256, 154, 432, 288, 410, 168, 240, 240, 380, 180, 320, 180, 352, 288, 320, 240]
public static final CameraCharacteristics.Key<int[]> samsung_android_jpeg_availableThumbnailSizes;
//[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
public static final CameraCharacteristics.Key<byte[]> samsung_android_jpeg_imageUniqueId;
//[26]
public static final CameraCharacteristics.Key<int[]> samsung_android_lens_info_focalLengthIn35mmFilm;
//[68.0, 68.0, 54.0, 0.0, 0.0, 68.0]
public static final CameraCharacteristics.Key<float[]> samsung_android_lens_info_horizontalViewAngles;
//[54.0]
public static final CameraCharacteristics.Key<float[]> samsung_android_lens_info_verticalViewAngle;
//[1234.0, 1234.0, 1234.0]
public static final CameraCharacteristics.Key<float[]> samsung_android_listener_gyro_gyroValues;
//[-2141519792, -2141519791, -2141519790, -2141519789, -2141519788, -2141519787, -2141126647, -2141519859, -2141519780, -2141519778, -2139619327, -2139619326]
public static final CameraCharacteristics.Key<int[]> samsung_android_request_availableSessionKeys;
//[33, 1968, 1968, 0, 33, 2608, 1956, 0, 33, 2608, 1468, 0, 33, 2608, 1630, 0, 33, 1920, 1080, 0, 33, 1440, 1080, 0, 33, 1280, 960, 0, 33, 1280, 720, 0, 33, 640, 480, 0, 33, 320, 240, 0, 33, 176, 144, 0, 33, 1920, 1440, 0, 33, 1072, 1072, 0, 33, 720, 720, 0]
public static final CameraCharacteristics.Key<int[]> samsung_android_scaler_availableCropPictureStreamConfigurations;
//[34, 1920, 1080, 0, 34, 1728, 1080, 0, 34, 1440, 1080, 0, 34, 1088, 1088, 0, 34, 1280, 960, 0, 34, 1280, 720, 0, 34, 960, 720, 0, 34, 720, 720, 0, 34, 720, 480, 0, 34, 640, 480, 0, 34, 352, 288, 0, 34, 320, 240, 0, 34, 176, 144, 0, 34, 2288, 1080, 0, 34, 1920, 1440, 0, 34, 1072, 1072, 0, 34, 496, 340, 0]
public static final CameraCharacteristics.Key<int[]> samsung_android_scaler_availableCropPreviewStreamConfigurations;
//[33, 3264, 2448, 0, 33, 3264, 2040, 0, 33, 3264, 1836, 0, 33, 2448, 2448, 0, 33, 2288, 1080, 0, 33, 2736, 2736, 0, 33, 2560, 1080, 0, 33, 1920, 1080, 0, 33, 1440, 1080, 0, 33, 1280, 960, 0, 33, 1280, 720, 0, 33, 640, 480, 0, 33, 320, 240, 0, 33, 176, 144, 0, 33, 1264, 2232, 0, 33, 1920, 1440, 0, 33, 1072, 1072, 0, 33, 720, 720, 0]
public static final CameraCharacteristics.Key<int[]> samsung_android_scaler_availableFullPictureStreamConfigurations;
//[34, 1920, 1080, 0, 34, 1728, 1080, 0, 34, 1440, 1080, 0, 34, 1088, 1088, 0, 34, 1280, 960, 0, 34, 1280, 720, 0, 34, 960, 720, 0, 34, 720, 720, 0, 34, 720, 480, 0, 34, 640, 480, 0, 34, 352, 288, 0, 34, 320, 240, 0, 34, 176, 144, 0, 34, 3840, 2160, 0, 34, 2560, 1440, 0, 34, 2288, 1080, 0, 34, 1920, 1440, 0, 34, 1072, 1072, 0, 34, 496, 340, 0]
public static final CameraCharacteristics.Key<int[]> samsung_android_scaler_availableFullPreviewStreamConfigurations;
//[538982489, 2400, 2400, 0]
public static final CameraCharacteristics.Key<int[]> samsung_android_scaler_availableIrisStreamConfigurations;
//[8.0]
public static final CameraCharacteristics.Key<float[]> samsung_android_scaler_availableMaxDigitalZoom;
//[8.0, 8.0, 0.0, 8.0, 8.0, 8.0, 8.0]
public static final CameraCharacteristics.Key<float[]> samsung_android_scaler_availableMaxDigitalZoomList;
//[1.0]
public static final CameraCharacteristics.Key<float[]> samsung_android_scaler_availableMinDigitalZoom;
//[1.0, 1.0, 0.0, 1.0, 1.0, 1.0, 1.0]
public static final CameraCharacteristics.Key<float[]> samsung_android_scaler_availableMinDigitalZoomList;
//[32, 3264, 2448, 0, 33, 1968, 1968, 0, 33, 2608, 1956, 0, 33, 2608, 1468, 0, 33, 2608, 1630, 0, 33, 2560, 1080, 0, 33, 1920, 1080, 0, 33, 1440, 1080, 0, 33, 1280, 960, 0, 33, 1280, 720, 0, 33, 640, 480, 0, 33, 320, 240, 0, 33, 176, 144, 0, 33, 1920, 1440, 0, 33, 1072, 1072, 0, 33, 720, 720, 0]
public static final CameraCharacteristics.Key<int[]> samsung_android_scaler_availablePictureStreamConfigurations;
//[34, 1920, 1080, 0, 34, 1728, 1080, 0, 34, 1440, 1080, 0, 34, 1088, 1088, 0, 34, 1280, 960, 0, 34, 1280, 720, 0, 34, 960, 720, 0, 34, 720, 720, 0, 34, 720, 480, 0, 34, 640, 480, 0, 34, 352, 288, 0, 34, 320, 240, 0, 34, 176, 144, 0, 34, 3840, 2160, 0, 34, 2560, 1440, 0, 34, 2288, 1080, 0, 34, 1920, 1440, 0, 34, 1072, 1072, 0, 34, 496, 340, 0]
public static final CameraCharacteristics.Key<int[]> samsung_android_scaler_availablePreviewStreamConfigurations;
//[35, 512, 320, 0, 35, 512, 248, 0, 35, 496, 240, 0, 35, 480, 288, 0, 35, 256, 154, 0, 35, 432, 288, 0, 35, 410, 168, 0, 35, 240, 240, 0, 35, 380, 180, 0, 35, 320, 180, 0, 35, 352, 288, 0, 35, 320, 240, 0]
public static final CameraCharacteristics.Key<int[]> samsung_android_scaler_availableThumbnailStreamConfigurations;
//[1920, 1440, 30, 30, 0, 0, 1920, 1080, 30, 30, 0, 0, 1728, 1080, 30, 30, 0, 0, 1280, 720, 30, 30, 0, 0, 1440, 1440, 30, 30, 0, 0, 2288, 1080, 30, 30, 20, 0]
public static final CameraCharacteristics.Key<int[]> samsung_android_scaler_availableVideoBeautyConfigurations;
//[1920, 1440, 30, 30, 20, 0, 1920, 1080, 30, 30, 20, 0, 1728, 1080, 30, 30, 20, 0, 1280, 720, 30, 30, 20, 0, 1440, 1440, 30, 30, 0, 0, 640, 480, 30, 30, 0, 0, 320, 240, 30, 30, 0, 0, 176, 144, 30, 30, 0, 0, 3840, 2160, 30, 30, 20, 0, 2560, 1440, 30, 30, 0, 0, 2288, 1080, 30, 30, 20, 0]
public static final CameraCharacteristics.Key<int[]> samsung_android_scaler_availableVideoConfigurations;
//[0, 1, 2]
public static final CameraCharacteristics.Key<int[]> samsung_android_scaler_flipAvailableModes;
//[0, 0, 0, 0]
public static final CameraCharacteristics.Key<int[]> samsung_android_sensor_blackLevelPattern;
//[1024]
public static final CameraCharacteristics.Key<int[]> samsung_android_sensor_captureTotalGain;
//[0]
public static final CameraCharacteristics.Key<int[]> samsung_android_sensor_drcRatio;
//[-1]
public static final CameraCharacteristics.Key<int[]> samsung_android_sensor_gyroState;
//[0, 0, 2608, 1956]
public static final CameraCharacteristics.Key<int[]> samsung_android_sensor_info_cropActiveArraySize;
//[0, 0, 3264, 2448]
public static final CameraCharacteristics.Key<int[]> samsung_android_sensor_info_fullActiveArraySize;
//[0]
public static final CameraCharacteristics.Key<int[]> samsung_android_sensor_info_physicalType;
//[50, 6400]
public static final CameraCharacteristics.Key<int[]> samsung_android_sensor_info_sensitivityRange;
//[83, 53, 75, 52, 72, 65, 0]
public static final CameraCharacteristics.Key<byte[]> samsung_android_sensor_info_sensorName;
//[0, 0]
public static final CameraCharacteristics.Key<long[]> samsung_android_sensor_wdrExposureTime;
//[0, 0]
public static final CameraCharacteristics.Key<int[]> samsung_android_sensor_wdrSensitivity;
//[49, 46, 48, 50, 0]
public static final CameraCharacteristics.Key<byte[]> samsung_android_unihal_versionInfo;
//[30, 93]
public static final CameraCharacteristics.Key<int[]> samsung_android_unihal_videoAvailableModes;
//[0]
public static final CameraCharacteristics.Key<long[]> samsung_android_uniplugin_DebugInfoData;
//[0]
public static final CameraCharacteristics.Key<int[]> samsung_android_uniplugin_DebugInfoSize;
//[0, 0]
public static final CameraCharacteristics.Key<int[]> samsung_android_uniplugin_ShiftInfo;
//[0]
public static final CameraCharacteristics.Key<byte[]> samsung_android_uniplugin_bokehRecordingHint;
//[0]
public static final CameraCharacteristics.Key<int[]> samsung_android_uniplugin_fps;
//[0]
public static final CameraCharacteristics.Key<byte[]> samsung_android_uniplugin_isProbeTOFSensor;
//[0, 1]
public static final CameraCharacteristics.Key<byte[]> samsung_android_uniplugin_llhdr;
//[0, 1]
public static final CameraCharacteristics.Key<byte[]> samsung_android_uniplugin_mfhdr;
//[0]
public static final CameraCharacteristics.Key<int[]> samsung_android_uniplugin_oisGain;
//[0, 0, 0, 0]
public static final CameraCharacteristics.Key<int[]> samsung_android_uniplugin_predictedAfRegions;
//[0, 0]
public static final CameraCharacteristics.Key<int[]> samsung_android_uniplugin_sensorDimension;
//[0]
public static final CameraCharacteristics.Key<int[]> samsung_android_uniplugin_shootingMode;
//[0]
public static final CameraCharacteristics.Key<int[]> samsung_android_uniplugin_shotMode;
//[0, 1]
public static final CameraCharacteristics.Key<byte[]> samsung_android_uniplugin_superResolution;
//[0]
public static final CameraCharacteristics.Key<int[]> samsung_android_uniplugin_vdis;
//[1.0]
public static final CameraCharacteristics.Key<float[]> samsung_android_uniplugin_zoomRatio;
static {
org_codeaurora_qcamera3_exposure_metering_available_modes= getKeyType("org.codeaurora.qcamera3.exposure_metering.available_modes", int[].class);
org_codeaurora_qcamera3_histogram_buckets= getKeyType("org.codeaurora.qcamera3.histogram.buckets", int[].class);
org_codeaurora_qcamera3_histogram_max_count= getKeyType("org.codeaurora.qcamera3.histogram.max_count", int[].class);
org_codeaurora_qcamera3_instant_aec_instant_aec_available_modes= getKeyType("org.codeaurora.qcamera3.instant_aec.instant_aec_available_modes", int[].class);
org_codeaurora_qcamera3_iso_exp_priority_exposure_time_range= getKeyType("org.codeaurora.qcamera3.iso_exp_priority.exposure_time_range", long[].class);
org_codeaurora_qcamera3_iso_exp_priority_iso_available_modes= getKeyType("org.codeaurora.qcamera3.iso_exp_priority.iso_available_modes", int[].class);
org_codeaurora_qcamera3_logicalCameraType_logical_camera_type= getKeyType("org.codeaurora.qcamera3.logicalCameraType.logical_camera_type", byte[].class);
org_codeaurora_qcamera3_manualWB_color_temperature_range= getKeyType("org.codeaurora.qcamera3.manualWB.color_temperature_range", int[].class);
org_codeaurora_qcamera3_manualWB_gains_range= getKeyType("org.codeaurora.qcamera3.manualWB.gains_range", float[].class);
org_codeaurora_qcamera3_platformCapabilities_IPEICACapabilities= getKeyType("org.codeaurora.qcamera3.platformCapabilities.IPEICACapabilities", byte[].class);
org_codeaurora_qcamera3_saturation_range= getKeyType("org.codeaurora.qcamera3.saturation.range", int[].class);
org_codeaurora_qcamera3_sharpness_range= getKeyType("org.codeaurora.qcamera3.sharpness.range", int[].class);
org_quic_camera2_sensormode_info_SensorModeTable= getKeyType("org.quic.camera2.sensormode.info.SensorModeTable", int[].class);
samsung_android_control_aeAvailableModes= getKeyType("samsung.android.control.aeAvailableModes", byte[].class);
samsung_android_control_afAvailableModes= getKeyType("samsung.android.control.afAvailableModes", byte[].class);
samsung_android_control_availableBurstShotFps= getKeyType("samsung.android.control.availableBurstShotFps", int[].class);
samsung_android_control_availableEffects= getKeyType("samsung.android.control.availableEffects", byte[].class);
samsung_android_control_availableFeatures= getKeyType("samsung.android.control.availableFeatures", int[].class);
samsung_android_control_awbAvailableModes= getKeyType("samsung.android.control.awbAvailableModes", int[].class);
samsung_android_control_beautyFaceRetouchLevel= getKeyType("samsung.android.control.beautyFaceRetouchLevel", int[].class);
samsung_android_control_beautyFaceSkinColor= getKeyType("samsung.android.control.beautyFaceSkinColor", int[].class);
samsung_android_control_cameraClient= getKeyType("samsung.android.control.cameraClient", int[].class);
samsung_android_control_cameraType= getKeyType("samsung.android.control.cameraType", int[].class);
samsung_android_control_colorTemperatureRange= getKeyType("samsung.android.control.colorTemperatureRange", int[].class);
samsung_android_control_effectAeAvailableTargetFpsRanges= getKeyType("samsung.android.control.effectAeAvailableTargetFpsRanges", int[].class);
samsung_android_control_handGestureAvailableTypes= getKeyType("samsung.android.control.handGestureAvailableTypes", int[].class);
samsung_android_control_liveHdrAvailableModes= getKeyType("samsung.android.control.liveHdrAvailableModes", int[].class);
samsung_android_control_liveHdrLevelRange= getKeyType("samsung.android.control.liveHdrLevelRange", int[].class);
samsung_android_control_llHdrEvCompensationList= getKeyType("samsung.android.control.llHdrEvCompensationList", float[].class);
samsung_android_control_llsValue= getKeyType("samsung.android.control.llsValue", int[].class);
samsung_android_control_meteringAvailableMode= getKeyType("samsung.android.control.meteringAvailableMode", int[].class);
samsung_android_control_mfHdrEvCompensationList= getKeyType("samsung.android.control.mfHdrEvCompensationList", float[].class);
samsung_android_control_multiFrame= getKeyType("samsung.android.control.multiFrame", int[].class);
samsung_android_control_pafAvailableMode= getKeyType("samsung.android.control.pafAvailableMode", byte[].class);
samsung_android_control_previousAe= getKeyType("samsung.android.control.previousAe", byte[].class);
samsung_android_control_productColorInfo= getKeyType("samsung.android.control.productColorInfo", byte[].class);
samsung_android_control_recordingSuggestedMotionSpeedMode= getKeyType("samsung.android.control.recordingSuggestedMotionSpeedMode", int[].class);
samsung_android_control_samsungSDK= getKeyType("samsung.android.control.samsungSDK", byte[].class);
samsung_android_control_ssmRecordableImageCount= getKeyType("samsung.android.control.ssmRecordableImageCount", int[].class);
samsung_android_control_strBvOffset= getKeyType("samsung.android.control.strBvOffset", int[].class);
samsung_android_control_swSuperVideoStabilization= getKeyType("samsung.android.control.swSuperVideoStabilization", byte[].class);
samsung_android_control_wbLevelRange= getKeyType("samsung.android.control.wbLevelRange", int[].class);
samsung_android_jpeg_availableThumbnailSizes= getKeyType("samsung.android.jpeg.availableThumbnailSizes", int[].class);
samsung_android_jpeg_imageUniqueId= getKeyType("samsung.android.jpeg.imageUniqueId", byte[].class);
samsung_android_lens_info_focalLengthIn35mmFilm= getKeyType("samsung.android.lens.info.focalLengthIn35mmFilm", int[].class);
samsung_android_lens_info_horizontalViewAngles= getKeyType("samsung.android.lens.info.horizontalViewAngles", float[].class);
samsung_android_lens_info_verticalViewAngle= getKeyType("samsung.android.lens.info.verticalViewAngle", float[].class);
samsung_android_listener_gyro_gyroValues= getKeyType("samsung.android.listener.gyro.gyroValues", float[].class);
samsung_android_request_availableSessionKeys= getKeyType("samsung.android.request.availableSessionKeys", int[].class);
samsung_android_scaler_availableCropPictureStreamConfigurations= getKeyType("samsung.android.scaler.availableCropPictureStreamConfigurations", int[].class);
samsung_android_scaler_availableCropPreviewStreamConfigurations= getKeyType("samsung.android.scaler.availableCropPreviewStreamConfigurations", int[].class);
samsung_android_scaler_availableFullPictureStreamConfigurations= getKeyType("samsung.android.scaler.availableFullPictureStreamConfigurations", int[].class);
samsung_android_scaler_availableFullPreviewStreamConfigurations= getKeyType("samsung.android.scaler.availableFullPreviewStreamConfigurations", int[].class);
samsung_android_scaler_availableIrisStreamConfigurations= getKeyType("samsung.android.scaler.availableIrisStreamConfigurations", int[].class);
samsung_android_scaler_availableMaxDigitalZoom= getKeyType("samsung.android.scaler.availableMaxDigitalZoom", float[].class);
samsung_android_scaler_availableMaxDigitalZoomList= getKeyType("samsung.android.scaler.availableMaxDigitalZoomList", float[].class);
samsung_android_scaler_availableMinDigitalZoom= getKeyType("samsung.android.scaler.availableMinDigitalZoom", float[].class);
samsung_android_scaler_availableMinDigitalZoomList= getKeyType("samsung.android.scaler.availableMinDigitalZoomList", float[].class);
samsung_android_scaler_availablePictureStreamConfigurations= getKeyType("samsung.android.scaler.availablePictureStreamConfigurations", int[].class);
samsung_android_scaler_availablePreviewStreamConfigurations= getKeyType("samsung.android.scaler.availablePreviewStreamConfigurations", int[].class);
samsung_android_scaler_availableThumbnailStreamConfigurations= getKeyType("samsung.android.scaler.availableThumbnailStreamConfigurations", int[].class);
samsung_android_scaler_availableVideoBeautyConfigurations= getKeyType("samsung.android.scaler.availableVideoBeautyConfigurations", int[].class);
samsung_android_scaler_availableVideoConfigurations= getKeyType("samsung.android.scaler.availableVideoConfigurations", int[].class);
samsung_android_scaler_flipAvailableModes= getKeyType("samsung.android.scaler.flipAvailableModes", int[].class);
samsung_android_sensor_blackLevelPattern= getKeyType("samsung.android.sensor.blackLevelPattern", int[].class);
samsung_android_sensor_captureTotalGain= getKeyType("samsung.android.sensor.captureTotalGain", int[].class);
samsung_android_sensor_drcRatio= getKeyType("samsung.android.sensor.drcRatio", int[].class);
samsung_android_sensor_gyroState= getKeyType("samsung.android.sensor.gyroState", int[].class);
samsung_android_sensor_info_cropActiveArraySize= getKeyType("samsung.android.sensor.info.cropActiveArraySize", int[].class);
samsung_android_sensor_info_fullActiveArraySize= getKeyType("samsung.android.sensor.info.fullActiveArraySize", int[].class);
samsung_android_sensor_info_physicalType= getKeyType("samsung.android.sensor.info.physicalType", int[].class);
samsung_android_sensor_info_sensitivityRange= getKeyType("samsung.android.sensor.info.sensitivityRange", int[].class);
samsung_android_sensor_info_sensorName= getKeyType("samsung.android.sensor.info.sensorName", byte[].class);
samsung_android_sensor_wdrExposureTime= getKeyType("samsung.android.sensor.wdrExposureTime", long[].class);
samsung_android_sensor_wdrSensitivity= getKeyType("samsung.android.sensor.wdrSensitivity", int[].class);
samsung_android_unihal_versionInfo= getKeyType("samsung.android.unihal.versionInfo", byte[].class);
samsung_android_unihal_videoAvailableModes= getKeyType("samsung.android.unihal.videoAvailableModes", int[].class);
samsung_android_uniplugin_DebugInfoData= getKeyType("samsung.android.uniplugin.DebugInfoData", long[].class);
samsung_android_uniplugin_DebugInfoSize= getKeyType("samsung.android.uniplugin.DebugInfoSize", int[].class);
samsung_android_uniplugin_ShiftInfo= getKeyType("samsung.android.uniplugin.ShiftInfo", int[].class);
samsung_android_uniplugin_bokehRecordingHint= getKeyType("samsung.android.uniplugin.bokehRecordingHint", byte[].class);
samsung_android_uniplugin_fps= getKeyType("samsung.android.uniplugin.fps", int[].class);
samsung_android_uniplugin_isProbeTOFSensor= getKeyType("samsung.android.uniplugin.isProbeTOFSensor", byte[].class);
samsung_android_uniplugin_llhdr= getKeyType("samsung.android.uniplugin.llhdr", byte[].class);
samsung_android_uniplugin_mfhdr= getKeyType("samsung.android.uniplugin.mfhdr", byte[].class);
samsung_android_uniplugin_oisGain= getKeyType("samsung.android.uniplugin.oisGain", int[].class);
samsung_android_uniplugin_predictedAfRegions= getKeyType("samsung.android.uniplugin.predictedAfRegions", int[].class);
samsung_android_uniplugin_sensorDimension= getKeyType("samsung.android.uniplugin.sensorDimension", int[].class);
samsung_android_uniplugin_shootingMode= getKeyType("samsung.android.uniplugin.shootingMode", int[].class);
samsung_android_uniplugin_shotMode= getKeyType("samsung.android.uniplugin.shotMode", int[].class);
samsung_android_uniplugin_superResolution= getKeyType("samsung.android.uniplugin.superResolution", byte[].class);
samsung_android_uniplugin_vdis= getKeyType("samsung.android.uniplugin.vdis", int[].class);
samsung_android_uniplugin_zoomRatio= getKeyType("samsung.android.uniplugin.zoomRatio", float[].class);
}
}
