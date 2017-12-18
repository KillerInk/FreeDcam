package com.huawei.camera2ex;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import java.lang.reflect.Type;

/**
 * Created by troop on 29.03.2017.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CameraCharacteristicsEx
{
    
    public static final CameraCharacteristics.Key<byte[]> ANDROID_HW_SUPPORTED_COLOR_MODES;
    
    public static final CameraCharacteristics.Key<byte[]> ANDROID_HW_SUPPORTED_ISO_VALUE;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_3D_MODEL_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_AF_TRIGGER_LOCK_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_ALLFOCUS_MODE_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_APERTURE_MONO_SUPPORTED;
    
    public static final CameraCharacteristics.Key<byte[]> HUAWEI_APERTURE_VALUE_SUPPORTED;
    
    public static final CameraCharacteristics.Key<byte[]> HUAWEI_AVAILABLE_BRIGHTNESS;
    
    public static final CameraCharacteristics.Key<byte[]> HUAWEI_AVAILABLE_COLOR_EFFECT_MODES;
    
    public static final CameraCharacteristics.Key<int[]> HUAWEI_AVAILABLE_COLOR_EFFECT_RANGE;
    
    public static final CameraCharacteristics.Key<byte[]> HUAWEI_AVAILABLE_CONTRAST;
    
    public static final CameraCharacteristics.Key<int[]> HUAWEI_AVAILABLE_DEPTH_STREAM_CONFIGURATIONS;
    
    public static final CameraCharacteristics.Key<byte[]> HUAWEI_AVAILABLE_DUAL_PRIMARY;
    
    public static final CameraCharacteristics.Key<byte[]> HUAWEI_AVAILABLE_EXPOSURE_MODES;
    
    public static final CameraCharacteristics.Key<int[]> HUAWEI_AVAILABLE_FRONT_FLASH_LEVEL;
    
    public static final CameraCharacteristics.Key<byte[]> HUAWEI_AVAILABLE_METERING;
    
    public static final CameraCharacteristics.Key<byte[]> HUAWEI_AVAILABLE_MONO_FILTER_MODES;
    
    public static final CameraCharacteristics.Key<byte[]> HUAWEI_AVAILABLE_RELAYOUT_MODES;
    
    public static final CameraCharacteristics.Key<byte[]> HUAWEI_AVAILABLE_SATURATION;
    
    public static final CameraCharacteristics.Key<byte[]> HUAWEI_AVAILABLE_SUPERNIGHT_EXPOSURE_VALUE;
    
    public static final CameraCharacteristics.Key<byte[]> HUAWEI_AVAILABLE_SUPERNIGHT_ISO_VALUE;
    
    public static final CameraCharacteristics.Key<int[]> HUAWEI_AVAILABLE_VIDEO_FPS;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_BEAUTY_1080P_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_BEST_MODE_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_BIG_APERTURE_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_BIG_APERTURE_ZOOM_THRESHOLD;
    
    public static final CameraCharacteristics.Key<Integer> HUAWEI_BURST_COUNT_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Integer> HUAWEI_BURST_JPEG_QUALITY;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_BURST_MODE_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_CAPTURE_MIRROR_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_DARK_RAIDER_MODE_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_DENOISE_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_DM_WATERMARK_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_DONOT_SUPPORT_HWCAMERA;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_DUAL_PRIMARY_SINGLE_REPROCESS;
    
    public static final CameraCharacteristics.Key<Float> HUAWEI_EXPOSURE_COMPENSATION_STEP;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_EXPOSURE_COMPENSATION_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_EXPOSURE_HINT_SUPPORTED;
    
    public static final CameraCharacteristics.Key<int[]> HUAWEI_EXPOSURE_LEVEL_RANGE;
    
    public static final CameraCharacteristics.Key<int[]> HUAWEI_FACE_BEAUTY_RANGE;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_FACE_BEAUTY_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_FAST_NOTIFY_SUPPORTED;
    
    public static final CameraCharacteristics.Key<byte[]> HUAWEI_FLASH_ASSIST_FOCUS_SUPPORTED;
    
    public static final CameraCharacteristics.Key<byte[]> HUAWEI_FRONT_VIRTUAL_EFFECT_SUPPORTED_RANGE;
    
    public static final CameraCharacteristics.Key<byte[]> HUAWEI_HDC_CALIBRATE_DATA;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_HDR_MOVIE_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_LCD_COMPENSATE_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_MAKEUP_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_MANUAL_FOCUS_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Integer> HUAWEI_MANUAL_VCM_END_VALUE;
    
    public static final CameraCharacteristics.Key<Integer> HUAWEI_MANUAL_VCM_START_VALUE;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_MONO_MODE_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_NICE_FOOD_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_OPTICAL_MAX_ZOOM_VALUE;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_OPTICAL_ZOOM_THRESHOLD;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_PORTRAIT_MODE_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_POST_PROCESS_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_PRE_CAPTURE_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_PRE_LCD_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_PROFESSIONAL_MODE_SUPPORTED;
    
    public static final CameraCharacteristics.Key<int[]> HUAWEI_QUICKTHUMBNAIL_SUPPORTED_SIZE;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_RAW_IMAGE_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_SCOPE_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_SEAMLESS_SUPPORTED;
    
    public static final CameraCharacteristics.Key<int[]> HUAWEI_SENCONDARY_SENSOR_PIXEL_ARRAY_SIZE;
    
    public static final CameraCharacteristics.Key<int[]> HUAWEI_SENCONDARY_SENSOR_SUPPORTED_SIZE;
    
    public static final CameraCharacteristics.Key<int[]> HUAWEI_SENSOR_EXPOSURETIME_RANGE;
    
    public static final CameraCharacteristics.Key<int[]> HUAWEI_SENSOR_ISO_RANGE;
    
    public static final CameraCharacteristics.Key<int[]> HUAWEI_SENSOR_WB_RANGE;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_SMILE_DETECTION_SUPPORTED;
    
    public static final CameraCharacteristics.Key<int[]> HUAWEI_SUPER_RESOLUTION_PICTURE_SIZE;
    
    public static final CameraCharacteristics.Key<byte[]> HUAWEI_SUPPORTED_MAKEUP_MODES;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_VDR_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_VIDEO_BEAUTY_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_VIDEO_CALLBACK_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_VIDEO_CALLBACK_THRESHOLD;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_VIDEO_STABILIZATION_DEFAULT_ON;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_VIDEO_STATUS_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_WATCH_SUPPORTED;
    
    public static final CameraCharacteristics.Key<Byte> HUAWEI_ZSL_SUPPORTED;

    public static final CameraCharacteristics.Key<int[]> HUAWEI_PROFESSIONAL_RAW12_SUPPORTED;

    public static final CameraCharacteristics.Key<int[]>HUAWEI_MULTICAP;
    public static final CameraCharacteristics.Key<int[]>HUAWEI_AVAILIBLE_DEPTH_SIZES;
    public static final CameraCharacteristics.Key<int[]>HUAWEI_AVAILIBLE_PREVIEW_DEPTH_SIZES;

    static {

        HUAWEI_FACE_BEAUTY_RANGE = getKeyClass("com.huawei.device.capabilities.faceBeautyRange", int[].class);
        HUAWEI_LCD_COMPENSATE_SUPPORTED = getKeyType("com.huawei.device.capabilities.lcdCompensateSupported", Byte.TYPE);
        HUAWEI_AVAILABLE_COLOR_EFFECT_MODES = getKeyClass("com.huawei.device.capabilities.availbaleEffectModes", byte[].class);
        HUAWEI_AVAILABLE_COLOR_EFFECT_RANGE = getKeyClass("com.huawei.device.capabilities.availbaleEffectRange", int[].class);
        HUAWEI_VIDEO_BEAUTY_SUPPORTED = getKeyType("com.huawei.device.capabilities.videoBeatySupported", Byte.TYPE);
        HUAWEI_AVAILABLE_CONTRAST = getKeyClass("com.huawei.device.capabilities.availbaleContrast", byte[].class);
        HUAWEI_AVAILABLE_SATURATION = getKeyClass("com.huawei.device.capabilities.availbaleSaturation", byte[].class);
        HUAWEI_AVAILABLE_BRIGHTNESS = getKeyClass("com.huawei.device.capabilities.availbaleBrightness", byte[].class);
        HUAWEI_AVAILABLE_METERING = getKeyClass("com.huawei.device.capabilities.availbaleMetering", byte[].class);
        HUAWEI_BURST_MODE_SUPPORTED = getKeyType("com.huawei.device.capabilities.burstModeSupported", Byte.TYPE);
        HUAWEI_BURST_COUNT_SUPPORTED = getKeyType("com.huawei.device.capabilities.burstCountSupported", Integer.TYPE);
        HUAWEI_BURST_JPEG_QUALITY = getKeyType("com.huawei.device.capabilities.burstJpegQuality", Integer.TYPE);
        HUAWEI_ZSL_SUPPORTED = getKeyType("com.huawei.device.capabilities.zslSupported", Byte.TYPE);
        HUAWEI_BEST_MODE_SUPPORTED = getKeyType("com.huawei.device.capabilities.bestModeSupported", Byte.TYPE);
        HUAWEI_HDR_MOVIE_SUPPORTED = getKeyType("com.huawei.device.capabilities.hdrMovieSupported", Byte.TYPE);
        HUAWEI_MANUAL_FOCUS_SUPPORTED = getKeyType("com.huawei.device.capabilities.manualFocusSupported", Byte.TYPE);
        HUAWEI_MANUAL_VCM_START_VALUE = getKeyType("com.huawei.device.capabilities.manualVcmStartValue", Integer.TYPE);
        HUAWEI_MANUAL_VCM_END_VALUE = getKeyType("com.huawei.device.capabilities.manualVcmEndValue", Integer.TYPE);
        HUAWEI_AVAILABLE_DUAL_PRIMARY = getKeyClass("com.huawei.device.capabilities.availbaleDualPrimary", byte[].class);
        HUAWEI_SMILE_DETECTION_SUPPORTED = getKeyType("com.huawei.device.capabilities.smileDetectionSupported", Byte.TYPE);
        HUAWEI_SCOPE_SUPPORTED = getKeyType("com.huawei.device.capabilities.hwScopeSupported", Byte.TYPE);
        HUAWEI_POST_PROCESS_SUPPORTED = getKeyType("com.huawei.device.capabilities.postProcessSupported", Byte.TYPE);
        HUAWEI_DENOISE_SUPPORTED = getKeyType("com.huawei.device.capabilities.denoiseSupported", Byte.TYPE);
        HUAWEI_BIG_APERTURE_SUPPORTED = getKeyType("com.huawei.device.capabilities.bigApertureSupported", Byte.TYPE);
        HUAWEI_APERTURE_VALUE_SUPPORTED = getKeyClass("com.huawei.device.capabilities.apertureValueSupported", byte[].class);
        HUAWEI_NICE_FOOD_SUPPORTED = getKeyType("com.huawei.device.capabilities.niceFoodSupported", Byte.TYPE);
        HUAWEI_BEAUTY_1080P_SUPPORTED = getKeyType("com.huawei.device.capabilities.beauty1080pSupported", Byte.TYPE);
        HUAWEI_VIDEO_CALLBACK_SUPPORTED = getKeyType("com.huawei.device.capabilities.videoCallbackSupported", Byte.TYPE);
        HUAWEI_AVAILABLE_EXPOSURE_MODES = getKeyClass("com.huawei.device.capabilities.availbaleLightPaintingModes", byte[].class);
        HUAWEI_AVAILABLE_RELAYOUT_MODES = getKeyClass("com.huawei.device.capabilities.availbaleRelayoutModes", byte[].class);
        HUAWEI_EXPOSURE_LEVEL_RANGE = getKeyClass("com.huawei.device.capabilities.exposureLevelRange", int[].class);
        HUAWEI_AVAILABLE_SUPERNIGHT_ISO_VALUE = getKeyClass("com.huawei.device.capabilities.availbaleSupernightIso", byte[].class);
        HUAWEI_AVAILABLE_SUPERNIGHT_EXPOSURE_VALUE = getKeyClass("com.huawei.device.capabilities.availbaleSupernightExposureTime", byte[].class);
        HUAWEI_EXPOSURE_COMPENSATION_SUPPORTED = getKeyType("com.huawei.device.capabilities.hwExposureCompensationSupported", Byte.TYPE);
        HUAWEI_EXPOSURE_COMPENSATION_STEP = getKeyType("com.huawei.device.capabilities.hwExposureCompensationStep", Float.TYPE);
        HUAWEI_ALLFOCUS_MODE_SUPPORTED = getKeyType("com.huawei.device.capabilities.allfocusModeSupported", Byte.TYPE);
        HUAWEI_AVAILABLE_VIDEO_FPS = getKeyClass("com.huawei.device.capabilities.videoFpsSupported", int[].class);
        HUAWEI_AVAILABLE_FRONT_FLASH_LEVEL = getKeyClass("com.huawei.device.capabilities.availbaleFrontFlashLevel", int[].class);
        HUAWEI_PROFESSIONAL_MODE_SUPPORTED = getKeyType("com.huawei.device.capabilities.professionalModeSupported", Byte.TYPE);
        HUAWEI_SENSOR_WB_RANGE = getKeyClass("com.huawei.device.capabilities.sensorWbRange", int[].class);
        HUAWEI_SENSOR_EXPOSURETIME_RANGE = getKeyClass("com.huawei.device.capabilities.hw-sensor-exposure-range", int[].class);
        HUAWEI_SENSOR_ISO_RANGE = getKeyClass("com.huawei.device.capabilities.hw-sensor-iso-range", int[].class);
        HUAWEI_RAW_IMAGE_SUPPORTED = getKeyType("com.huawei.device.capabilities.rawImgSupported", Byte.TYPE);
        HUAWEI_EXPOSURE_HINT_SUPPORTED = getKeyType("com.huawei.device.capabilities.exposureHintSupported", Byte.TYPE);
        HUAWEI_FLASH_ASSIST_FOCUS_SUPPORTED = getKeyClass("com.huawei.device.capabilities.flashAssistFocusSupported", byte[].class);
        HUAWEI_MONO_MODE_SUPPORTED = getKeyType("com.huawei.device.capabilities.monoModeSupported", Byte.TYPE);
        HUAWEI_AVAILABLE_MONO_FILTER_MODES = getKeyClass("com.huawei.device.capabilities.availableMonoFilterModes", byte[].class);
        HUAWEI_DARK_RAIDER_MODE_SUPPORTED = getKeyType("com.huawei.device.capabilities.darkRaiderModeSupported", Byte.TYPE);
        HUAWEI_VIDEO_STATUS_SUPPORTED = getKeyType("com.huawei.device.capabilities.hwRequireVideoStatus", Byte.TYPE);
        HUAWEI_PRE_CAPTURE_SUPPORTED = getKeyType("com.huawei.device.capabilities.prepareCaptureSupported", Byte.TYPE);
        ANDROID_HW_SUPPORTED_COLOR_MODES = getKeyClass("com.huawei.device.capabilities.supportedColorModes", byte[].class);
        ANDROID_HW_SUPPORTED_ISO_VALUE = getKeyClass("com.huawei.device.capabilities.supported_iso_values", byte[].class);
        HUAWEI_PRE_LCD_SUPPORTED = getKeyType("com.huawei.device.capabilities.lcdFlashStatusSupported", Byte.TYPE);
        HUAWEI_AF_TRIGGER_LOCK_SUPPORTED = getKeyType("com.huawei.device.capabilities.afTriggerLockSupported", Byte.TYPE);
        HUAWEI_MAKEUP_SUPPORTED = getKeyType("com.huawei.device.capabilities.makeUpSupported", Byte.TYPE);
        HUAWEI_SUPPORTED_MAKEUP_MODES = getKeyClass("com.huawei.device.capabilities.makeUpEffectSupported", byte[].class);
        HUAWEI_DM_WATERMARK_SUPPORTED = getKeyType("com.huawei.device.capabilities.dmWaterMarkSupported", Byte.TYPE);
        HUAWEI_FRONT_VIRTUAL_EFFECT_SUPPORTED_RANGE = getKeyClass("com.huawei.device.capabilities.frontVirtualEffectSupportedValue", byte[].class);
        HUAWEI_DUAL_PRIMARY_SINGLE_REPROCESS = getKeyType("com.huawei.device.capabilities.dualPrimarySingleReprocess", Byte.TYPE);
        HUAWEI_VDR_SUPPORTED = getKeyType("com.huawei.device.capabilities.vdrSupported", Byte.TYPE);
        HUAWEI_SUPER_RESOLUTION_PICTURE_SIZE = getKeyClass("com.huawei.device.capabilities.superResolutionPictureSize", int[].class);
        HUAWEI_SENCONDARY_SENSOR_PIXEL_ARRAY_SIZE = getKeyClass("com.huawei.device.capabilities.hwSubActiveArraySize", int[].class);
        HUAWEI_SENCONDARY_SENSOR_SUPPORTED_SIZE = getKeyClass("com.huawei.device.capabilities.hwSubSensorJpegSize", int[].class);
        HUAWEI_OPTICAL_ZOOM_THRESHOLD = getKeyType("com.huawei.device.capabilities.opticalZoomThreshold", Byte.TYPE);
        HUAWEI_VIDEO_CALLBACK_THRESHOLD = getKeyType("com.huawei.device.capabilities.highVideoCallbackThresholdSupported", Byte.TYPE);
        HUAWEI_DONOT_SUPPORT_HWCAMERA = getKeyType("com.huawei.device.capabilities.donotSupportHwCamera", Byte.TYPE);
        HUAWEI_WATCH_SUPPORTED = getKeyType("com.huawei.device.capabilities.hwWatchSupported", Byte.TYPE);
        HUAWEI_QUICKTHUMBNAIL_SUPPORTED_SIZE = getKeyClass("com.huawei.device.capabilities.quickthumbnailResolutionSupported", int[].class);
        HUAWEI_FAST_NOTIFY_SUPPORTED = getKeyType("com.huawei.device.capabilities.fastBinderSupported", Byte.TYPE);
        HUAWEI_BIG_APERTURE_ZOOM_THRESHOLD = getKeyType("com.huawei.device.capabilities.bigApertureZoomThreshold", Byte.TYPE);
        HUAWEI_VIDEO_STABILIZATION_DEFAULT_ON = getKeyType("com.huawei.device.capabilities.hwVideoStabilizationDefaultOn", Byte.TYPE);
        HUAWEI_APERTURE_MONO_SUPPORTED = getKeyType("com.huawei.device.capabilities.bigApertureMonoSupported", Byte.TYPE);
        HUAWEI_SEAMLESS_SUPPORTED = getKeyType("com.huawei.device.capabilities.seamlessSupported", Byte.TYPE);
        HUAWEI_PORTRAIT_MODE_SUPPORTED = getKeyType("com.huawei.device.capabilities.portraitModeSupported", Byte.TYPE);
        HUAWEI_3D_MODEL_SUPPORTED = getKeyType("com.huawei.device.capabilities.hw3DModelSupported", Byte.TYPE);
        HUAWEI_HDC_CALIBRATE_DATA = getKeyClass("com.huawei.device.capabilities.hwHdcCalibrateData", byte[].class);
        HUAWEI_CAPTURE_MIRROR_SUPPORTED = getKeyType("com.huawei.device.capabilities.captureMirrorSupported", Byte.TYPE);
        HUAWEI_FACE_BEAUTY_SUPPORTED = getKeyType("com.huawei.device.capabilities.faceBeautySupported", Byte.TYPE);
        HUAWEI_AVAILABLE_DEPTH_STREAM_CONFIGURATIONS = getKeyClass("com.huawei.device.capabilities.hwAvailableDepthStreamConfigurations", int[].class);
        HUAWEI_OPTICAL_MAX_ZOOM_VALUE = getKeyType("com.huawei.device.capabilities.opticalMaxZoomValue", Byte.TYPE);
        HUAWEI_PROFESSIONAL_RAW12_SUPPORTED = getKeyClass("com.huawei.device.capabilities.hw-professional-raw12-supported", int[].class);
        HUAWEI_MULTICAP = getKeyClass("com.huawei.device.capabilities.multiCameraCap",int[].class);
        HUAWEI_AVAILIBLE_DEPTH_SIZES = getKeyClass("com.huawei.device.capabilities.availableCaptureDepthSizes",int[].class);
        HUAWEI_AVAILIBLE_PREVIEW_DEPTH_SIZES = getKeyClass("com.huawei.device.capabilities.availablePreviewDepthSizes",int[].class);
    }


    //public android.hardware.camera2.CameraCharacteristics$Key(java.lang.String,android.hardware.camera2.utils.TypeReference)
    private static CameraCharacteristics.Key getKeyType(String string,  Type type)
    {
        return (CameraCharacteristics.Key) ReflectionHelper.getKeyType(string,type,CameraCharacteristics.Key.class);
    }

    //public android.hardware.camera2.CameraCharacteristics$Key(java.lang.String,java.lang.Class)
    private static <T> CameraCharacteristics.Key getKeyClass(String string, Class<T> type)
    {

        return (CameraCharacteristics.Key) ReflectionHelper.getKeyClass(string,type,CameraCharacteristics.Key.class);
    }

}
