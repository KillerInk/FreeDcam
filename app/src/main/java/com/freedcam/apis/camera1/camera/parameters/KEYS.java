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


    //MediaTEk
    public static final String KEY_MIN_FOCUS_MEDIATEK_VCM = "afeng-min-focus-step";
    public static final String KEY_MAX_FOCUS_MEDIATEK_VCM = "afeng-max-focus-step";



    //Krillin
    public static final String KEY_MANUAL_FOCUS_HUAWEI_SUPPORTED = "hw-manual-focus-supported";
    public static final String KEY_MIN_FOCUS_HUAWEI_VCM = "hw-vcm-start-value";
    public static final String KEY_MAX_FOCUS_HUAWEI_VCM = "hw-vcm-end-value";


}
