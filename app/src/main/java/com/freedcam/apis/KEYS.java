package com.freedcam.apis;

import com.freedcam.utils.StringUtils;

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

    //Qualcomm

    public static final String KEY_QUALCOMM_CAMERA = "qc-camera-features";

    // Manual Focus Keys
    public static final String KEY_MANUAL_FOCUS_MODE_VALUE = "manual-focus-modes";
    public static final String KEY_FOCUS_MODE_MANUAL = "manual";

    public static final String KEY_FOCUS_TYPE_VCM_INDEX = "1";
    public static final String KEY_MIN_FOCUS_VCM_INDEX = "min-focus-pos-index";
    public static final String KEY_MAX_FOCUS_VCM_INDEX = "max-focus-pos-index";
    // Manual Focus Scale
    public static final String KEY_FOCUS_TYPE_SCALE = "2";
    public static final String KEY_MIN_FOCUS_SCALE = "min-focus-pos-ratio";
    public static final String KEY_MAX_FOCUS_SCALE = "max-focus-pos-ratio";
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

    public final static String WB_CURRENT_CCT = "wb-current-cct";
    public final static String WB_CCT = "wb-cct";
    public final static String WB_CT = "wb-ct";
    public final static String WB_MANUAL_CCT = "wb-manual-cct";
    public final static String MANUAL_WB_VALUE = "manual-wb-value";
    public final static String MAX_WB_CCT = "max-wb-cct";
    public final static String MIN_WB_CCT = "min-wb-cct";
    public final static String MAX_WB_CT = "max-wb-ct";
    public final static String MIN_WB_CT = "min-wb-ct";
    public final static String LG_Min = "lg-wb-supported-min";
    public final static String LG_Max = "lg-wb-supported-max";
    public final static String LG_WB = "lg-wb";
    public final static String WB_MODE_MANUAL = "manual";
    public final static String WB_MODE_MANUAL_CCT = "manual-cct";

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

    //MediaTEk
    public static final String KEY_MIN_FOCUS_MEDIATEK_VCM = "afeng-min-focus-step";
    public static final String KEY_MAX_FOCUS_MEDIATEK_VCM = "afeng-max-focus-step";



    //Krillin
    public static final String KEY_MANUAL_FOCUS_HUAWEI_SUPPORTED = "hw-manual-focus-supported";
    public static final String KEY_MIN_FOCUS_HUAWEI_VCM = "hw-vcm-start-value";
    public static final String KEY_MAX_FOCUS_HUAWEI_VCM = "hw-vcm-end-value";


}
