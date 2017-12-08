package com.huawei.camera2ex;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.util.Rational;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

/**
 * Created by troop on 29.03.2017.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CaptureRequestEx {

    public static final CaptureRequest.Key<Byte> ANDROID_HW_COLOR_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_3D_MODEL_MODE;
    
    public static final CaptureRequest.Key<Integer> HUAWEI_4K_VIDEO_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_AF_TRIGGER_LOCK;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_ALL_FOCUS_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_APERTURE_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_APERTURE_MONO_MODE;
    
    public static final CaptureRequest.Key<Integer> HUAWEI_APERTURE_VALUE;
    
    public static final CaptureRequest.Key<Integer> HUAWEI_API_VERSION;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_BEST_SHOT_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_BLINK_DETECTION;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_BRIGHTNESS_VALUE;
    
    public static final CaptureRequest.Key<Integer> HUAWEI_BURST_COUNT;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_BURST_SNAPSHOT_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_CAMERA_FLAG;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_CAPTURE_MIRROR;
    
    public static final CaptureRequest.Key<Integer> HUAWEI_COLOR_EFFECT_LEVEL;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_COLOR_EFFECT_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_CONTRAST_VALUE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_DARK_RAIDER_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_DMAP_FORMAT;
    
    public static final CaptureRequest.Key<int[]> HUAWEI_DMAP_SIZE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_DM_WATERMARK_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_DUAL_SENSOR_MODE;
    
    public static final CaptureRequest.Key<Float> HUAWEI_EXPOSURE_COMP_VALUE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_EXPOSURE_HINT_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_EXT_SCENE_MODE;
    
    public static final CaptureRequest.Key<Integer> HUAWEI_FACE_BEAUTY_LEVEL;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_FACE_BEAUTY_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_FACE_MEIWO;
    
    public static final CaptureRequest.Key<Integer> HUAWEI_FACE_ORIENTATION;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_FAST_SHOT_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_FOCUS_METERING_SEPERATE_MODE;
    
    public static final CaptureRequest.Key<Integer> HUAWEI_FRONT_FLASH_LEVEL;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_FRONT_VIRTUAL_EFFECT_LEVEL;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_FRONT_VIRTUAL_EFFECT_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_HDR_MOVIE_MODE;
    public static final CaptureRequest.Key<Integer> HUAWEI_HIGH_VIDEO_FPS;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_IMAGE_POST_PROCESS_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_ISO;
    
    public static final CaptureRequest.Key<int[]> HUAWEI_JPEG_THUMBNAIL_SIZE;
    
    public static final CaptureRequest.Key<Integer> HUAWEI_LCD_COMPENSATE_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_LIGHT_PAINTING_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_LIGHT_PAINTING_RELAYOUT_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_LIGHT_PAINTING_TRYAE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_MAKEUP_EFFECT;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_MAKEUP_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_MANUAL_EXPOSURE_VALUE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_MANUAL_FLASH_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_MANUAL_FOCUS_MODE;
    
    public static final CaptureRequest.Key<Integer> HUAWEI_MANUAL_FOCUS_VALUE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_MANUAL_ISO_VALUE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_METERING_MODE;
    
    public static final CaptureRequest.Key<Integer> HUAWEI_MIRROR_MODE;
    
    public static final CaptureRequest.Key<Integer> HUAWEI_MIRROR_POINT;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_MONO_MODE;
    
    public static final CaptureRequest.Key<Integer> HUAWEI_MULTICAMERA_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_NICE_FOOD_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_PANORAMA_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_PORTRAIT_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_PREPARE_CAPTURE_FLAG;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_PRE_LCD_FLASH;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_PROFESSIONAL_ASSIST_FLASH_MODE;

    public static final Byte HUAWEI_PROFESSIONAL_FOCUS_MODE_AFS = (byte)1;
    public static final Byte HUAWEI_PROFESSIONAL_FOCUS_MODE_AFC = (byte)2;
    public static final Byte HUAWEI_PROFESSIONAL_FOCUS_MODE_MF = (byte)3;

    public static final CaptureRequest.Key<Byte> HUAWEI_PROFESSIONAL_FOCUS_MODE;


    public static final Byte HUAWEI_PROFESSIONAL_MODE_ENABLED = (byte) 1;
    public static final Byte HUAWEI_PROFESSIONAL_MODE_DISABLED = (byte)0;
    //Controls the aestate
    public static final CaptureRequest.Key<Byte> HUAWEI_PROFESSIONAL_MODE;

    // Set the exposuretime as rational (1/33) for use case enable HUAWEI_PROFESSIONAL_MODE
    public static final CaptureRequest.Key<Rational> HUAWEI_PROF_EXPOSURE_TIME;

    //gets used to set ExposureTime, need set with PROF_EXPOSURE_TIME as millisec!
    public static final CaptureRequest.Key<Integer> HUAWEI_SENSOR_EXPOSURE_TIME;
    
    public static final CaptureRequest.Key<int[]> HUAWEI_QUICKTHUMBNAIL_RESOLUTION;
    
    public static final CaptureRequest.Key<int[]> HUAWEI_REAL_JPEG_SIZE;
    public static final CaptureRequest.Key<Integer> HUAWEI_ROTATION_VALUE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_SATURATION_VALUE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_SCOPE_MODE;

    
    public static final CaptureRequest.Key<Integer> HUAWEI_SENSOR_ISO_VALUE;
    
    public static final CaptureRequest.Key<Integer> HUAWEI_SENSOR_WB_VALUE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_SHARPNESS_VALUE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_SMILE_DETECTION;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_STOP_BURST;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_TARGET_TRACKING_MODE;
    
    public static final CaptureRequest.Key<Byte> HUAWEI_VIDEO_STATUS;

    static {
        HUAWEI_FACE_BEAUTY_LEVEL = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", Integer.TYPE);
        HUAWEI_LCD_COMPENSATE_MODE = getKeyType("com.huawei.capture.metadata.lcdCompensateMode", Integer.TYPE);
        HUAWEI_COLOR_EFFECT_MODE = getKeyType("com.huawei.capture.metadata.colorEffectMode", Byte.TYPE);
        HUAWEI_COLOR_EFFECT_LEVEL = getKeyType("com.huawei.capture.metadata.colorEffectLevel", Integer.TYPE);
        HUAWEI_CONTRAST_VALUE = getKeyType("com.huawei.capture.metadata.constrastValue", Byte.TYPE);
        HUAWEI_SATURATION_VALUE = getKeyType("com.huawei.capture.metadata.saturationValue", Byte.TYPE);
        HUAWEI_BRIGHTNESS_VALUE = getKeyType("com.huawei.capture.metadata.brightnessValue", Byte.TYPE);
        HUAWEI_SHARPNESS_VALUE = getKeyType("com.huawei.capture.metadata.sharpnessValue", Byte.TYPE);
        HUAWEI_METERING_MODE = getKeyType("com.huawei.capture.metadata.meteringMode", Byte.TYPE);
        HUAWEI_BURST_SNAPSHOT_MODE = getKeyType("com.huawei.capture.metadata.burstSnapshotMode", Byte.TYPE);
        HUAWEI_BURST_COUNT = getKeyType("com.huawei.capture.metadata.burstCount", Integer.TYPE);
        HUAWEI_STOP_BURST = getKeyType("com.huawei.capture.metadata.stopBurst", Byte.TYPE);
        HUAWEI_FACE_ORIENTATION = getKeyType("com.huawei.capture.metadata.faceOrientation", Integer.TYPE);
        HUAWEI_SMILE_DETECTION = getKeyType("com.huawei.capture.metadata.smileDetectionMode", Byte.TYPE);
        HUAWEI_BLINK_DETECTION = getKeyType("com.huawei.capture.metadata.blinkDetectionMode", Byte.TYPE);
        HUAWEI_FACE_MEIWO = getKeyType("com.huawei.capture.metadata.faceMeiwoMode", Byte.TYPE);
        HUAWEI_NICE_FOOD_MODE = getKeyType("com.huawei.capture.metadata.niceFoodMode", Byte.TYPE);
        HUAWEI_CAMERA_FLAG = getKeyType("com.huawei.capture.metadata.hwCamera2Flag", Byte.TYPE);
        HUAWEI_MANUAL_FLASH_MODE = getKeyType("com.huawei.capture.metadata.manualFlashMode", Byte.TYPE);
        HUAWEI_IMAGE_POST_PROCESS_MODE = getKeyType("com.huawei.capture.metadata.imagePostProcessMode", Byte.TYPE);
        HUAWEI_SCOPE_MODE = getKeyType("com.huawei.capture.metadata.scopeMode", Byte.TYPE);
        HUAWEI_BEST_SHOT_MODE = getKeyType("com.huawei.capture.metadata.bestShotMode", Byte.TYPE);
        HUAWEI_HDR_MOVIE_MODE = getKeyType("com.huawei.capture.metadata.hdrMovieMode", Byte.TYPE);
        HUAWEI_PANORAMA_MODE = getKeyType("com.huawei.capture.metadata.panoramaMode", Byte.TYPE);
        HUAWEI_MANUAL_FOCUS_MODE = getKeyType("com.huawei.capture.metadata.manualFocusMode", Byte.TYPE);
        HUAWEI_MANUAL_FOCUS_VALUE = getKeyType("com.huawei.capture.metadata.manualFocusValue", Integer.TYPE);
        HUAWEI_MIRROR_MODE = getKeyType("com.huawei.capture.metadata.mirrorMode", Integer.TYPE);
        HUAWEI_MIRROR_POINT = getKeyType("com.huawei.capture.metadata.mirrorPoint", Integer.TYPE);
        HUAWEI_DUAL_SENSOR_MODE = getKeyType("com.huawei.capture.metadata.dualSensorMode", Byte.TYPE);
        HUAWEI_EXT_SCENE_MODE = getKeyType("com.huawei.capture.metadata.extSceneMode", Byte.TYPE);
        HUAWEI_FAST_SHOT_MODE = getKeyType("com.huawei.capture.metadata.fastShotMode", Byte.TYPE);
        HUAWEI_APERTURE_MODE = getKeyType("com.huawei.capture.metadata.apertureMode", Byte.TYPE);
        HUAWEI_APERTURE_VALUE = getKeyType("com.huawei.capture.metadata.apertureValue", Integer.TYPE);
        HUAWEI_LIGHT_PAINTING_MODE = getKeyType("com.huawei.capture.metadata.lightPaintingMode", Byte.TYPE);
        HUAWEI_LIGHT_PAINTING_TRYAE = getKeyType("com.huawei.capture.metadata.lightPaintingTryAe", Byte.TYPE);
        HUAWEI_LIGHT_PAINTING_RELAYOUT_MODE = getKeyType("com.huawei.capture.metadata.lightPaintingRelayoutMode", Byte.TYPE);
        HUAWEI_FOCUS_METERING_SEPERATE_MODE = getKeyType("com.huawei.capture.metadata.focusMeteringSeperateMode", Byte.TYPE);
        HUAWEI_EXPOSURE_COMP_VALUE = getKeyType("com.huawei.capture.metadata.exposureCompValue", Float.TYPE);
        HUAWEI_MANUAL_ISO_VALUE = getKeyType("com.huawei.capture.metadata.manualIsoValue", Byte.TYPE);
        HUAWEI_MANUAL_EXPOSURE_VALUE = getKeyType("com.huawei.capture.metadata.manualExposureValue", Byte.TYPE);
        HUAWEI_ALL_FOCUS_MODE = getKeyType("com.huawei.capture.metadata.allFocusMode", Byte.TYPE);
        HUAWEI_FRONT_FLASH_LEVEL = getKeyType("com.huawei.capture.metadata.frontFlashLevel", Integer.TYPE);
        HUAWEI_SENSOR_WB_VALUE = getKeyType("com.huawei.capture.metadata.sensorWbValue", Integer.TYPE);
        HUAWEI_SENSOR_EXPOSURE_TIME = getKeyType("com.huawei.capture.metadata.sensorExposureTime", Integer.TYPE);
        HUAWEI_SENSOR_ISO_VALUE = getKeyType("com.huawei.capture.metadata.sensorIso", Integer.TYPE);
        HUAWEI_PROFESSIONAL_MODE = getKeyType("com.huawei.capture.metadata.professionalMode", Byte.TYPE);
        HUAWEI_PROFESSIONAL_FOCUS_MODE = getKeyType("com.huawei.capture.metadata.professionalFocusMode", Byte.TYPE);
        HUAWEI_PROFESSIONAL_ASSIST_FLASH_MODE = getKeyType("com.huawei.capture.metadata.profFocusAssistFlashMode", Byte.TYPE);
        HUAWEI_EXPOSURE_HINT_MODE = getKeyType("com.huawei.capture.metadata.exposureHintMode", Byte.TYPE);
        HUAWEI_API_VERSION = getKeyType("com.huawei.capture.metadata.apiVersion", Integer.TYPE);
        HUAWEI_MONO_MODE = getKeyType("com.huawei.capture.metadata.monoMode", Byte.TYPE);
        HUAWEI_DARK_RAIDER_MODE = getKeyType("com.huawei.capture.metadata.darkRaiderMode", Byte.TYPE);
        HUAWEI_PREPARE_CAPTURE_FLAG = getKeyType("com.huawei.capture.metadata.prepareCaptureFlag", Byte.TYPE);
        HUAWEI_VIDEO_STATUS = getKeyType("com.huawei.capture.metadata.hwVideoStatus", Byte.TYPE);
        ANDROID_HW_COLOR_MODE = getKeyType("com.huawei.capture.metadata.colorMode", Byte.TYPE);
        HUAWEI_ISO = getKeyType("com.huawei.capture.metadata.iso", Byte.TYPE);
        HUAWEI_TARGET_TRACKING_MODE = getKeyType("com.huawei.capture.metadata.hw-device-target-tracking", Byte.TYPE);
        HUAWEI_QUICKTHUMBNAIL_RESOLUTION = getKeyType("com.huawei.capture.metadata.hwQuickThumbnailResolution", int[].class);
        HUAWEI_PRE_LCD_FLASH = getKeyType("com.huawei.capture.metadata.lcdFlashStatus", Byte.TYPE);
        HUAWEI_HIGH_VIDEO_FPS = getKeyType("com.huawei.capture.metadata.hw-video-fps", Integer.TYPE);
        HUAWEI_AF_TRIGGER_LOCK = getKeyType("com.huawei.capture.metadata.afTriggerLock", Byte.TYPE);
        HUAWEI_DM_WATERMARK_MODE = getKeyType("com.huawei.capture.metadata.dmWaterMarkMode", Byte.TYPE);
        HUAWEI_FRONT_VIRTUAL_EFFECT_MODE = getKeyType("com.huawei.capture.metadata.frontVirtualEffectMode", Byte.TYPE);
        HUAWEI_FRONT_VIRTUAL_EFFECT_LEVEL = getKeyType("com.huawei.capture.metadata.frontVirtualEffectLevel", Byte.TYPE);
        HUAWEI_ROTATION_VALUE = getKeyType("com.huawei.capture.metadata.hw-rotation-value", Integer.TYPE);
        HUAWEI_JPEG_THUMBNAIL_SIZE = getKeyClass("com.huawei.capture.metadata.hw-jpeg-thumbnail-size", int[].class);
        HUAWEI_4K_VIDEO_MODE = getKeyType("com.huawei.capture.metadata.hwVideo4kMode", Integer.TYPE);
        HUAWEI_PROF_EXPOSURE_TIME = getKeyClass("com.huawei.capture.metadata.proExposureTime", Rational.class);
        HUAWEI_REAL_JPEG_SIZE = getKeyClass("com.huawei.capture.metadata.captureStreamResolution", int[].class);
        HUAWEI_APERTURE_MONO_MODE = getKeyType("com.huawei.capture.metadata.apertureMonoMode", Byte.TYPE);
        HUAWEI_PORTRAIT_MODE = getKeyType("com.huawei.capture.metadata.portraitMode", Byte.TYPE);
        HUAWEI_3D_MODEL_MODE = getKeyType("com.huawei.capture.metadata.hw3DModelMode", Byte.TYPE);
        HUAWEI_MULTICAMERA_MODE = getKeyType("com.huawei.capture.metadata.multiCameraMode", Integer.TYPE);
        HUAWEI_DMAP_FORMAT = getKeyType("com.huawei.capture.metadata.hwDmapFormat", Byte.TYPE);
        HUAWEI_DMAP_SIZE = getKeyClass("com.huawei.capture.metadata.hwDmapSize", int[].class);
        HUAWEI_MAKEUP_MODE = getKeyType("com.huawei.capture.metadata.makeUpMode", Byte.TYPE);
        HUAWEI_CAPTURE_MIRROR = getKeyType("com.huawei.capture.metadata.captureMirrorMode", Byte.TYPE);
        HUAWEI_MAKEUP_EFFECT = getKeyType("com.huawei.capture.metadata.makeUpEffect", Byte.TYPE);
        HUAWEI_FACE_BEAUTY_MODE = getKeyType("com.huawei.capture.metadata.faceBeautyMode", Byte.TYPE);
    }
//0 = {Constructor@5072} "protected android.hardware.camera2.utils.TypeReference()"
//1 = {Constructor@5073} "private android.hardware.camera2.utils.TypeReference(java.lang.reflect.Type)"
//            2 = {Constructor@5061} "android.hardware.camera2.utils.TypeReference(java.lang.reflect.Type,android.hardware.camera2.utils.TypeReference)"
    private static CaptureRequest.Key getKeyType(String string, Type type)
    {
        return (CaptureRequest.Key) ReflectionHelper.getKeyType(string,type, CaptureRequest.Key.class);
    }

    private static <T> CaptureRequest.Key getKeyClass(String string, Class<T> type)
    {
        return (CaptureRequest.Key) ReflectionHelper.getKeyClass(string,type, CaptureRequest.Key.class);
    }
}
