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

    public static final String ISO100 = "ISO100";
    public static final String ISO_MANUAL = "manual";

    //Qualcomm

    public static final String AUTO_HDR_SUPPORTED = "auto-hdr-supported";
    public static final String AUTO_HDR_ENABLE = "auto-hdr-enable";

    public static final String SCENE_MODE_VALUES_HDR ="hdr";
    public static final String SCENE_MODE_VALUES_ASD ="asd";

    public static final String MORPHO_HDR = "morpho-hdr";
    public static final String MORPHO_HHT = "morpho-hht";

    public static final String HDR_MODE = "hdr-mode";

    public static final String BAYER_MIPI_10BGGR = "bayer-mipi-10bggr";
    public static final String BAYER_MIPI_10RGGB ="bayer-mipi-10rggb";
    public static final String BAYER_QCOM_10GRBG = "bayer-qcom-10grbg";
    public static final String BAYER_QCOM_10RGGB = "bayer-qcom-10rggb";

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

    //omap
    //seen on optimus3d to set 3d deepth
    public static final String MANUAL_CONVERGENCE =  "manual-convergence";
    public static final String SUPPORTED_MANUAL_CONVERGENCE_MAX = "supported-manual-convergence-max";
    public static final String SUPPORTED_MANUAL_CONVERGENCE_MIN = "supported-manual-convergence-min";

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
