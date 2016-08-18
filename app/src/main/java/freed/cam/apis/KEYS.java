/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.apis;

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

    public static final String ZOOM = "zoom";
    public static final String ZOOM_SUPPORTED = "zoom-supported";
    public static final String MAX_ZOOM = "max-zoom";

    public static final String VIDEO_STABILIZATION = "video-stabilization";
    public static final String VIDEO_STABILIZATION_SUPPORTED = "video-stabilization-supported";
    public static final String PICTURE_FORMAT_VALUES = "picture-format-values";
    public static final String PICTURE_FORMAT = "picture-format";

    public static final String JPEG_QUALITY = "jpeg-quality";

    public static final String ISO100 = "ISO100";
    public static final String ISO_MANUAL = "manual";
    public static final String CUR_ISO  = "cur-iso";
    public static final String CUR_EXPOSURE_TIME = "cur-exposure-time";

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

    public static final String REDEYE_REDUCTION = "redeye-reduction";
    public static final String REDEYE_REDUCTION_VALUES = "redeye-reduction-values";

    public static final String LENSSHADE = "lensshade";
    public static final String LENSSHADE_VALUES = "lensshade-values";

    public static final String SCENE_DETECT = "scene-detect";
    public static final String SCENE_DETECT_VALUES= "scene-detect-values";

    public static final String DENOISE = "denoise";
    public static final String DENOISE_VALUES="denoise-values";

    public static final String DIGITALIMAGESTABILIZATION = "dis";
    public static final String DIGITALIMAGESTABILIZATION_VALUES = "dis-values";

    public static final String MEMORYCOLORENHANCEMENT_VALUES = "mce-values";
    public static final String MEMORYCOLORENHANCEMENT = "mce";

    public static final String SKINETONEENHANCEMENT = "skinToneEnhancement";
    public static final String SKINETONEENHANCEMENT_VALUES = "skinToneEnhancement-values";

    public static final int MAGIC_NUM0 = 0;
    public static final int MAGIC_NUM100 = 100;

    public static final String MAX_SHARPNESS = "max-sharpness";
    public static final String MIN_SHARPNESS = "min-sharpness";
    public static final String SHARPNESS_MAX = "sharpness-max";
    public static final String SHARPNESS_MIN = "sharpness-min";
    public static final String SHARPNESS = "sharpness";
    public static final String SHARPNESS_VALUES = "sharpness-values";
    public static final String SHARPNESS_STEP = "sharpness-step";

    public static final String MAX_SATURATION = "max-saturation";
    public static final String MIN_SATURATION = "min-saturation";
    public static final String SATURATION_MAX = "saturation-max";
    public static final String SATURATION_MIN = "saturation-min";
    public static final String SATURATION = "saturation";
    public static final String SATURATION_VALUES = "saturation-values";


    // Manual Focus Keys
    public static final String KEY_MANUAL_FOCUS_MODE_VALUE = "manual-focus-modes";
    public static final String KEY_FOCUS_MODE_MANUAL = "manual";

    public static final int KEY_FOCUS_TYPE_VCM_INDEX = 1;
    public static final String MIN_FOCUS_POS_INDEX = "min-focus-pos-index";
    public static final String MAX_FOCUS_POS_INDEX = "max-focus-pos-index";
    // Manual Focus Scale
    public static final int KEY_FOCUS_TYPE_SCALE = 1;
    public static final String MIN_FOCUS_POS_RATIO = "min-focus-pos-ratio";
    public static final String MAX_FOCUS_POS_RATIO = "max-focus-pos-ratio";
    // Manual Focus Diopter Value type Double
    public static final int KEY_FOCUS_TYPE_DIOPTER = 3;
    public static final String KEY_MIN_FOCUS_DIOPTER = "min-focus-pos-diopter";
    public static final String KEY_MAX_FOCUS_DIOPTER = "max-focus-pos-diopter";
    // Manual Focus Settable
    public static final String KEY_MANUAL_FOCUS_TYPE = "manual-focus-pos-type";
    public static final String KEY_MANUAL_FOCUS_POSITION = "manual-focus-position";
    // Manual Focus Gettable
    public static final String KEY_MANUAL_FOCUS_SCALE = "cur-focus-scale";
    public static final String KEY_MANUAL_FOCUS_DIOPTER = "cur-focus-diopter";

    public static final String MANUAL_FOCUS = "manual-focus";
    public static final String MANUAL_FOCUS_SCALE_MODE = "scale-mode";

    public static final String WB_CURRENT_CCT = "wb-current-cct";
    public static final String WB_CCT = "wb-cct";
    public static final String WB_CT = "wb-ct";
    public static final String WB_MANUAL_CCT = "wb-manual-cct";
    public static final String MANUAL_WB_VALUE = "manual-wb-value";
    public static final String MAX_WB_CCT = "max-wb-cct";
    public static final String MIN_WB_CCT = "min-wb-cct";
    public static final String MAX_WB_CT = "max-wb-ct";
    public static final String MIN_WB_CT = "min-wb-ct";
    public static final String WB_MODE_MANUAL = "manual";
    public static final String WB_MODE_MANUAL_CCT = "manual-cct";
    public static final String MANUAL_WB_TYPE = "manual-wb-type";
    public static final String MANUAL_WB_TYPE_COLOR_TEMPERATURE = "color-temperature";

    public static final String MAX_EXPOSURE_TIME = "max-exposure-time";
    public static final String MIN_EXPOSURE_TIME = "min-exposure-time";
    public static final String EXPOSURE_TIME = "exposure-time";
    public static final String MANUAL_EXPOSURE = "manual-exposure";
    public static final String MANUAL_EXPOSURE_MODES = "manual-exposure-modes";
    public static final String MANUAL_EXPOSURE_MODES_OFF = OFF;
    public static final String MANUAL_EXPOSURE_MODES_EXP_TIME_PRIORITY = "exp-time-priority";
    public static final String MANUAL_EXPOSURE_MODES_ISO_PRIORITY = "iso-priority";
    public static final String MANUAL_EXPOSURE_MODES_USER_SETTING = "user-setting";

    public static final String AUTO_HDR_SUPPORTED = "auto-hdr-supported";
    public static final String AUTO_HDR_ENABLE = "auto-hdr-enable";

    public static final String SCENE_MODE_VALUES ="scene-mode-values";
    public static final String SCENE_MODE = "scene-mode";
    public static final String SCENE_MODE_VALUES_HDR ="hdr";
    public static final String SCENE_MODE_VALUES_ASD ="asd";

    public static final String AE_BRACKET_HDR = "ae-bracket-hdr";
    public static final String AE_BRACKET_HDR_VALUES = "ae-bracket-hdr-values";
    public static final String AE_BRACKET_HDR_VALUES_AE_BRACKET = "AE-Bracket";
    public static final String AE_BRACKET_HDR_VALUES_OFF = "Off";

    public static final String MORPHO_HDR = "morpho-hdr";
    public static final String MORPHO_HHT = "morpho-hht";

    public static final String HDR_MODE = "hdr-mode";

    public static final String BAYER_MIPI_10BGGR = "bayer-mipi-10bggr";
    public static final String BAYER_MIPI_10RGGB ="bayer-mipi-10rggb";
    public static final String BAYER_QCOM_10GRBG = "bayer-qcom-10grbg";

    //Burst
    public static final String NUM_SNAPS_PER_SHUTTER = "num-snaps-per-shutter";
    public static final String SNAPSHOT_BURST_NUM = "snapshot-burst-num";
    public static final String BURST_NUM = "burst-num";


    public static final String MORPHO_EFFECT_TYPE = "morpho_effect_type";

    public static final String ISO = "iso";
    public static final String CONTINUOUS_ISO = "continuous-iso";
    public static final String MIN_ISO = "min-iso";
    public static final String MAX_ISO = "max-iso";

    //MediaTEk
    public static final String AFENG_MIN_FOCUS_STEP = "afeng-min-focus-step";
    public static final String AFENG_MAX_FOCUS_STEP = "afeng-max-focus-step";
    public static final String AFENG_POS = "afeng-pos";
    public static final String FOCUS_FS_FI = "focus-fs-fi";
    public static final String FOCUS_FS_FI_MAX = "focus-fs-fi-max";
    public static final String FOCUS_FS_FI_MIN = "focus-fs-fi-min";

    public static final String MTK_NOISE_REDUCTION_MODE = "3dnr-mode";
    public static final String MTK_NOISE_REDUCTION_MODE_VALUES = "3dnr-mode-values";

    public static final String CUR_ISO_MTK  = "eng-capture-sensor-gain";
    public static final String CUR_ISO_MTK2  = "cap-isp-g";
    public static final String CUR_EXPOSURE_TIME_MTK  = "eng-capture-shutter-speed";
    public static final String CUR_EXPOSURE_TIME_MTK1  = "cap-ss";



    //Krillin
    public static final String KEY_MANUAL_FOCUS_HUAWEI_SUPPORTED = "hw-manual-focus-supported";
    public static final String HW_VCM_START_VALUE = "hw-vcm-start-value";
    public static final String HW_VCM_END_VALUE = "hw-vcm-end-value";
    //focus
    public static final String HW_HWCAMERA_FLAG = "hw-hwcamera-flag";
    public static final String HW_MANUAL_FOCUS_MODE = "hw-manual-focus-mode";
    public static final String HW_MANUAL_FOCUS_STEP_VALUE = "hw-manual-focus-step-value";

    //LG
    public static final String LG_MANUAL_MODE_RESET = "lg-manual-mode-reset";
    public static final String LG_WB_SUPPORTED_MIN = "lg-wb-supported-min";
    public static final String LG_WB_SUPPORTED_MAX = "lg-wb-supported-max";
    public static final String LG_WB = "lg-wb";
    public static final String LG_ISO = "lg-iso";
    public static final String LG_COLOR_ADJUST = "lg-color-adjust";
    public static final String LG_COLOR_ADJUST_MAX = "lg-color-adjust-max";
    public static final String LG_COLOR_ADJUST_MIN = "lg-color-adjust-min";

    public static final String MANUALFOCUS_STEP = "manualfocus_step";
    public static final String FOCUS_MODE_NORMAL = "normal";

    public static final String LG_OIS = "ois-ctrl";
    public static final String LG_OIS_PREVIEW_CAPTURE = "preview-capture";
    public static final String LG_OIS_CAPTURE = "capture";
    public static final String LG_OIS_VIDEO = "video";
    public static final String LG_OIS_CENTERING_ONLY = "centering-only";
    public static final String LG_OIS_CENTERING_OFF = "centering-off";
    public static final String LG_SHUTTER_SPEED_VALUES = "shutter-speed-values";
    public static final String LG_SHUTTER_SPEED = "shutter-speed";

    //ZTE
    public static final String NIGHT_KEY = "night_key";
    public static final String NIGHT_MODE_TRIPOD = "tripod";

    //HTC
    public static final String MAX_FOCUS = "max-focus";
    public static final String MIN_FOCUS = "min-focus";
    public static final String FOCUS = "focus";
    public static final String ISO_ST =  "iso-st";


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

    //cam Module
    public static final String MODULE_VIDEO = "module_video";
    public static final String MODULE_PICTURE = "module_picture";
    public static final String MODULE_HDR = "module_hdr";
    public static final String MODULE_BURST = "module_burst";
    public static final String MODULE_STACKING = "module_stacking";
    public static final String MODULE_INTERVAL = "module_interval";
    public static final String MODULE_AFBRACKET = "module_afbracket";
}
