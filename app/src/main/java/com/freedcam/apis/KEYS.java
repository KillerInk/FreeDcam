package com.freedcam.apis;

/**
 * Created by GeorgeKiarie on 5/26/2016.
 */
public final class KEYS {

    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String ON = "on";
    public static final String OFF = "off";
    public static final String AUTO = "auto";
    public static final String DISABLE = "disable";
    public static final String ENABLE = "enable";

    public static final String JPEG = "jpeg";
    public static final String BAYER = "bayer";
    public static final String DNG = "dng";

    public static final String VIDEO_STABILIZATION = "video-stabilization";
    public static final String VIDEO_STABILIZATION_SUPPORTED = "video-stabilization-supported";
    public static final String PICTURE_FORMAT_VALUES = "picture-format-values";
    public static final String PICTURE_FORMAT = "picture-format";

    public final static String ISO100 = "ISO100";
    public final static String CUR_ISO  = "cur-iso";

    //Qualcomm

    public static final String KEY_QUALCOMM_CAMERA = "qc-camera-features";

    public static final String FOCUS_MODE = "focus-mode";
    public static final String FOCUS_MODE_VALUES="focus-mode-values";

    public static final String WHITEBALANCE = "whitebalance";
    public static final String WHITEBALANCE_VALUES = "whitebalance-values";

    public static final String COLOR_EFFECT = "effect";
    public static final String COLOR_EFFECT_VALUES = "effect-values";

    public static final String FLASH_MODE = "flash-mode";
    public static final String FLASH_MODE_VALUES = "flash-mode-values";

    public static final String ANTIBANDING = "antibanding";
    public static final String ANTIBANDING_VALUES = "antibanding-values";

    public static final String PREVIEW_FRAME_RATE_VALUES = "preview-frame-rate-values";
    public static final String PREVIEW_FRAME_RATE ="preview-frame-rate";

    public final static String REDEYE_REDUCTION = "redeye-reduction";
    public final static String REDEYE_REDUCTION_VALUES = "redeye-reduction-values";

    public final static String LENSSHADE = "lensshade";
    public final static String LENSSHADE_VALUES = "lensshade-values";

    public final static String SCENE_DETECT = "scene-detect";
    public final static String SCENE_DETECT_VALUES= "scene-detect-values";

    public final static String DENOISE = "denoise";
    public final static String DENOISE_VALUES="denoise-values";

    public final static String DIGITALIMAGESTABILIZATION = "dis";
    public final static String DIGITALIMAGESTABILIZATION_VALUES = "dis-values";

    public final static String MEMORYCOLORENHANCEMENT_VALUES = "mce-values";
    public final static String MEMORYCOLORENHANCEMENT = "mce";

    public final static String SKINETONEENHANCEMENT = "skinToneEnhancement";
    public final static String SKINETONEENHANCEMENT_VALUES = "skinToneEnhancement-values";


    // Manual Focus Keys
    public static final String KEY_MANUAL_FOCUS_MODE_VALUE = "manual-focus-modes";
    public static final String KEY_FOCUS_MODE_MANUAL = "manual";

    public static final String KEY_FOCUS_TYPE_VCM_INDEX = "1";
    public static final String MIN_FOCUS_POS_INDEX = "min-focus-pos-index";
    public static final String MAX_FOCUS_POS_INDEX = "max-focus-pos-index";
    // Manual Focus Scale
    public static final String KEY_FOCUS_TYPE_SCALE = "2";
    public static final String MIN_FOCUS_POS_RATIO = "min-focus-pos-ratio";
    public static final String MAX_FOCUS_POS_RATIO = "max-focus-pos-ratio";
    // Manual Focus Diopter Value type Double
    public static final String KEY_FOCUS_TYPE_DIOPTER = "3";
    public static final String KEY_MIN_FOCUS_DIOPTER = "min-focus-pos-diopter";
    public static final String KEY_MAX_FOCUS_DIOPTER = "max-focus-pos-diopter";
    // Manual Focus Settable
    public static final String KEY_MANUAL_FOCUS_TYPE = "manual-focus-pos-type";
    public static final String KEY_MANUAL_FOCUS_POSITION = "manual-focus-position";
    // Manual Focus Gettable
    public static final String KEY_MANUAL_FOCUS_SCALE = "cur-focus-scale";
    public static final String KEY_MANUAL_FOCUS_DIOPTER = "cur-focus-diopter";

    public final static String MANUAL_FOCUS = "manual-focus";
    public final static String MANUAL_FOCUS_SCALE_MODE = "scale-mode";

    public final static String WB_CURRENT_CCT = "wb-current-cct";
    public final static String WB_CCT = "wb-cct";
    public final static String WB_CT = "wb-ct";
    public final static String WB_MANUAL_CCT = "wb-manual-cct";
    public final static String MANUAL_WB_VALUE = "manual-wb-value";
    public final static String MAX_WB_CCT = "max-wb-cct";
    public final static String MIN_WB_CCT = "min-wb-cct";
    public final static String MAX_WB_CT = "max-wb-ct";
    public final static String MIN_WB_CT = "min-wb-ct";
    public final static String WB_MODE_MANUAL = "manual";
    public final static String WB_MODE_MANUAL_CCT = "manual-cct";
    public final static String MANUAL_WB_TYPE = "manual-wb-type";
    public final static String MANUAL_WB_TYPE_COLOR_TEMPERATURE = "color-temperature";

    public final static String MAX_EXPOSURE_TIME = "max-exposure-time";
    public final static String MIN_EXPOSURE_TIME = "min-exposure-time";
    public final static String EXPOSURE_TIME = "exposure-time";
    public final static String MANUAL_EXPOSURE = "manual-exposure";
    public final static String MANUAL_EXPOSURE_MODES = "manual-exposure-modes";
    public final static String MANUAL_EXPOSURE_MODES_OFF = OFF;
    public final static String MANUAL_EXPOSURE_MODES_EXP_TIME_PRIORITY = "exp-time-priority";
    public final static String MANUAL_EXPOSURE_MODES_ISO_PRIORITY = "iso-priority";
    public final static String MANUAL_EXPOSURE_MODES_USER_SETTING = "user-setting";

    public final static String AUTO_HDR_SUPPORTED = "auto-hdr-supported";
    public final static String AUTO_HDR_ENABLE = "auto-hdr-enable";

    public final static String SCENE_MODE_VALUES ="scene-mode-values";
    public final static String SCENE_MODE = "scene-mode";
    public final static String SCENE_MODE_VALUES_HDR ="hdr";
    public final static String SCENE_MODE_VALUES_ASD ="asd";

    public final static String AE_BRACKET_HDR = "ae-bracket-hdr";
    public final static String AE_BRACKET_HDR_VALUES = "ae-bracket-hdr-values";
    public final static String AE_BRACKET_HDR_VALUES_AE_BRACKET = "AE-Bracket";
    public final static String AE_BRACKET_OFF = "Off";

    public final static String MORPHO_HDR = "morpho-hdr";

    public final static String HDR_MODE = "hdr-mode";

    public final static String BAYER_MIPI_10BGGR = "bayer-mipi-10bggr";
    public final static String BAYER_MIPI_10RGGB ="bayer-mipi-10rggb";
    public final static String BAYER_QCOM_10GRBG = "bayer-qcom-10grbg";

    //Burst
    public final static String NUM_SNAPS_PER_SHUTTER = "num-snaps-per-shutter";
    public final static String SNAPSHOT_BURST_NUM = "snapshot-burst-num";
    public final static String BURST_NUM = "burst-num";


    public final static String MORPHO_EFFECT_TYPE = "morpho_effect_type";

    public final static String ISO = "iso";
    public final static String MIN_ISO = "min-iso";
    public final static String MAX_ISO = "max-iso";

    //MediaTEk
    public static final String AFENG_MIN_FOCUS_STEP = "afeng-min-focus-step";
    public static final String AFENG_MAX_FOCUS_STEP = "afeng-max-focus-step";
    public static final String AFENG_POS = "afeng-pos";
    public static final String FOCUS_FS_FI = "focus-fs-fi";
    public static final String FOCUS_FS_FI_MAX = "focus-fs-fi-max";
    public static final String FOCUS_FS_FI_MIN = "focus-fs-fi-min";

    public final static String MTK_NOISE_REDUCTION_MODE = "3dnr-mode";
    public final static String MTK_NOISE_REDUCTION_MODE_VALUES = "3dnr-mode-values";



    //Krillin
    public static final String KEY_MANUAL_FOCUS_HUAWEI_SUPPORTED = "hw-manual-focus-supported";
    public static final String HW_VCM_START_VALUE = "hw-vcm-start-value";
    public static final String HW_VCM_END_VALUE = "hw-vcm-end-value";
    //focus
    public final static String HW_HWCAMERA_FLAG = "hw-hwcamera-flag";
    public final static String HW_MANUAL_FOCUS_MODE = "hw-manual-focus-mode";
    public final static String HW_MANUAL_FOCUS_STEP_VALUE = "hw-manual-focus-step-value";

    //LG
    public final static String LG_MANUAL_MODE_RESET = "lg-manual-mode-reset";
    public final static String LG_WB_SUPPORTED_MIN = "lg-wb-supported-min";
    public final static String LG_WB_SUPPORTED_MAX = "lg-wb-supported-max";
    public final static String LG_WB = "lg-wb";
    public final static String LG_ISO = "lg-iso";

    public final static String MANUALFOCUS_STEP = "manualfocus_step";
    public final static String FOCUS_MODE_NORMAL = "normal";

    //HTC
    public final static String MAX_FOCUS = "max-focus";
    public final static String MIN_FOCUS = "min-focus";
    public final static String FOCUS = "focus";
    public final static String ISO_ST =  "iso-st";


    //omap
    //seen on optimus3d to set 3d deepth
    public static final String MANUAL_CONVERGENCE =  "manual-convergence";
    public static final String SUPPORTED_MANUAL_CONVERGENCE_MAX = "supported-manual-convergence-max";
    public static final String SUPPORTED_MANUAL_CONVERGENCE_MIN = "supported-manual-convergence-min";

    public static final String IMAGEPOSTPROCESSING = "ipp";
    public static final String IMAGEPOSTPROCESSING_VALUES = "ipp-values";

    //sony
    //visual stabilization
    public static final String SONY_VS = "sony-vs";
    public static final String SONY_VS_VALUES = "sony-vs-values";



    //freedcam Module
    public static final String MODULE_VIDEO = "module_video";
    public static final String MODULE_PICTURE = "module_picture";
    public static final String MODULE_HDR = "module_hdr";
    public static final String MODULE_BURST = "module_burst";
    public static final String MODULE_LONGEXPO = "module_longexposure";
    public static final String MODULE_STACKING = "module_stacking";
    public static final String MODULE_FILM_SNAPSHOT = "module_film_snapshot";
    public static final String MODULE_FILM_VIDEO = "module_film_snapshot";
    public static final String MODULE_INTERVAL = "module_interval";
    public static final String MODULE_ALL = "module_all";
}
