package com.freedcam.apis.camera1.camera.parameters;

/**
 * Created by GeorgeKiarie on 5/26/2016.
 */
public class KEYS {

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

    public final static String WBCURRENT = "wb-current-cct";
    public final static String WB_CCT = "wb-cct";
    public final static String WB_CT = "wb-ct";
    public final static String WB_MANUAL = "wb-manual-cct";
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


    //MediaTEk
    public static final String KEY_MIN_FOCUS_MEDIATEK_VCM = "afeng-min-focus-step";
    public static final String KEY_MAX_FOCUS_MEDIATEK_VCM = "afeng-max-focus-step";



    //Krillin
    public static final String KEY_MANUAL_FOCUS_HUAWEI_SUPPORTED = "hw-manual-focus-supported";
    public static final String KEY_MIN_FOCUS_HUAWEI_VCM = "hw-vcm-start-value";
    public static final String KEY_MAX_FOCUS_HUAWEI_VCM = "hw-vcm-end-value";


}
