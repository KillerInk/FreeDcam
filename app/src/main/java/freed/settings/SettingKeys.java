package freed.settings;

import com.troop.freedcam.R;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import freed.FreedApplication;
import freed.settings.mode.ApiBooleanSettingMode;
import freed.settings.mode.GlobalBooleanSettingMode;
import freed.settings.mode.SettingMode;
import freed.settings.mode.TypedSettingMode;

/*
    This class is used to design the settings.
    with adding a new key it can get extended.
 */
public class SettingKeys{

    public final static Key<SettingMode> M_Brightness = new Key(SettingMode.class, R.string.aps_manualbrightness);
    public final static Key<SettingMode> M_SHARPNESS = new Key(SettingMode.class, R.string.aps_manualsharpness);
    public final static Key<SettingMode> M_CONTRAST = new Key(SettingMode.class, R.string.aps_manualcontrast);
    public final static Key<SettingMode> M_SATURATION = new Key(SettingMode.class, R.string.aps_manualsaturation);
    public final static Key<SettingMode> M_EXPOSURE_COMPENSATION = new Key(SettingMode.class, R.string.aps_manualexpocomp);
    public final static Key<SettingMode> M_FNUMBER = new Key(SettingMode.class, R.string.aps_manualfnum);
    public final static Key<SettingMode> M_BURST = new Key(SettingMode.class, R.string.aps_manualburst);
    public final static Key<SettingMode> M_FX = new Key(SettingMode.class, R.string.aps_manualfx);
    public final static Key<SettingMode> M_ZOOM = new Key(SettingMode.class, R.string.aps_manualzoom);
    public final static Key<SettingMode> M_PROGRAM_SHIFT = new Key(SettingMode.class, R.string.aps_manualprogramshift);
    public final static Key<SettingMode> M_APERTURE = new Key(SettingMode.class, R.string.aps_manualaperture);
    public final static ApiKey<SettingMode> M_ZEBRA_HIGH = new ApiKey(SettingMode.class, R.string.aps_zebrahigh);
    public final static ApiKey<SettingMode> M_ZEBRA_LOW = new ApiKey(SettingMode.class, R.string.aps_zebralow);
    public final static Key<TypedSettingMode> M_MANUAL_ISO = new Key(TypedSettingMode.class, R.string.aps_manualiso);
    public final static Key<TypedSettingMode> M_WHITEBALANCE = new Key(TypedSettingMode.class, R.string.aps_manualwb);
    public final static Key<TypedSettingMode> M_FOCUS = new Key(TypedSettingMode.class, R.string.aps_manualfocus);
    public final static Key<TypedSettingMode> M_EXPOSURE_TIME = new Key(TypedSettingMode.class, R.string.aps_manualexpotime);

    public final static Key<SettingMode> COLOR_MODE = new Key(SettingMode.class, R.string.aps_colormode);
    public final static Key<SettingMode> EXPOSURE_MODE = new Key(SettingMode.class, R.string.aps_exposuremode);
    public final static Key<SettingMode> FLASH_MODE = new Key(SettingMode.class, R.string.aps_flashmode);
    public final static Key<SettingMode> ISO_MODE = new Key(SettingMode.class, R.string.aps_isomode);
    public final static Key<SettingMode> ANTI_BANDING_MODE = new Key(SettingMode.class, R.string.aps_antibandingmode);
    public final static Key<SettingMode> WHITE_BALANCE_MODE = new Key(SettingMode.class, R.string.aps_whitebalancemode);
    public final static Key<SettingMode> PICTURE_SIZE = new Key(SettingMode.class, R.string.aps_picturesize);
    public final static Key<SettingMode> YUV_SIZE = new Key(SettingMode.class, R.string.aps_yuvsize);
    public final static Key<SettingMode> RAW_SIZE = new Key(SettingMode.class, R.string.aps_rawsize);
    public final static Key<SettingMode> PICTURE_FORMAT = new Key(SettingMode.class, R.string.aps_pictureformat);

    public final static Key<SettingMode> JPEG_QUALITY = new Key(SettingMode.class, R.string.aps_jpegquality);
    public final static Key<ApiBooleanSettingMode> MFNR = new Key(ApiBooleanSettingMode.class, R.string.aps_mfnr);

    public final static ApiKey<SettingMode> IMAGE_POST_PROCESSING = new ApiKey(SettingMode.class, R.string.aps_ippmode);
    public final static ApiKey<SettingMode> CAMERA_SWITCH = new ApiKey(SettingMode.class, R.string.aps_cameraswitch);
    public final static Key<SettingMode> PREVIEW_SIZE = new Key(SettingMode.class, R.string.aps_previewsize);
    public final static Key<SettingMode> PREVIEW_FPS = new Key(SettingMode.class, R.string.aps_previewfps);
    public final static Key<SettingMode> PREVIEW_FORMAT = new Key(SettingMode.class, R.string.aps_previewformat);
    public final static Key<SettingMode> PREVIEW_FPS_RANGE = new Key(SettingMode.class, R.string.aps_previewfpsrange);
    public final static Key<SettingMode> SCENE_MODE = new Key(SettingMode.class, R.string.aps_scenemode);
    public final static Key<SettingMode> FOCUS_MODE = new Key(SettingMode.class, R.string.aps_focusmode);
    public final static Key<SettingMode> RED_EYE = new Key(SettingMode.class, R.string.aps_redeyemode);
    public final static Key<SettingMode> LENS_SHADE = new Key(SettingMode.class, R.string.aps_lenshademode);
    public final static Key<SettingMode> ZSL = new Key(SettingMode.class, R.string.aps_zslmode);
    public final static Key<SettingMode> SCENE_DETECT = new Key(SettingMode.class, R.string.aps_scenedetectmode);
    public final static Key<SettingMode> DENOISE = new Key(SettingMode.class, R.string.aps_denoisemode);

    public final static Key<SettingMode> PDAF = new Key(SettingMode.class, R.string.aps_pdaf);
    public final static Key<SettingMode> TNR = new Key(SettingMode.class, R.string.aps_tnr);//temporal noise reduction
    public final static Key<SettingMode> TNR_V = new Key(SettingMode.class, R.string.aps_tnr_v);//temporal noise reduction video
    public final static Key<SettingMode> RDI = new Key(SettingMode.class, R.string.aps_rdi);
    public final static Key<SettingMode> TRUE_POTRAIT = new Key(SettingMode.class, R.string.aps_truepotrait);
    public final static Key<SettingMode> RE_FOCUS = new Key(SettingMode.class, R.string.aps_refocus);
    public final static Key<SettingMode> SEE_MORE = new Key(SettingMode.class, R.string.aps_seemore);// advanced wb correction?
    public final static Key<SettingMode> OPTI_ZOOM = new Key(SettingMode.class, R.string.aps_optizoom);
    public final static Key<SettingMode> CHROMA_FLASH = new Key(SettingMode.class, R.string.aps_chroma_flash);

    public final static Key<SettingMode> DIGITAL_IMAGE_STABILIZATION = new Key(SettingMode.class, R.string.aps_digitalimagestabmode);
    public final static Key<SettingMode> VIDEO_STABILIZATION = new Key(SettingMode.class, R.string.aps_videoStabilisation);
    public final static Key<SettingMode> MEMORY_COLOR_ENHANCEMENT = new Key(SettingMode.class, R.string.aps_memorycolorenhancementmode);

    public final static Key<SettingMode> NON_ZSL_MANUAL_MODE = new Key(SettingMode.class, R.string.aps_nonzslmanualmode); //used on htc devices to enable q3a on raw capture
    public final static Key<SettingMode> AE_BRACKET = new Key(SettingMode.class, R.string.aps_aebrackethdr);
    public final static Key<ApiBooleanSettingMode> EXPOSURE_LOCK = new Key(ApiBooleanSettingMode.class, R.string.aps_aps_exposurelock);
    public final static Key<SettingMode> CDS_MODE = new Key(SettingMode.class, R.string.aps_cds);
    public final static Key<SettingMode> HTC_VIDEO_MODE = new Key(SettingMode.class, R.string.aps_htcvideoMode);
    public final static Key<SettingMode> HTC_VIDEO_MODE_HSR = new Key(SettingMode.class, R.string.aps_htcvideoModehsr);

    public final static Key<SettingMode> VIDEO_PROFILES = new Key(SettingMode.class, R.string.aps_videoProfile);
    public final static Key<SettingMode> VIDEO_SIZE = new Key(SettingMode.class, R.string.aps_videoSize);
    public final static Key<SettingMode> VIDEO_HDR = new Key(SettingMode.class, R.string.aps_videohdr);
    public final static Key<SettingMode> VIDEO_HIGH_FRAMERATE = new Key(SettingMode.class, R.string.aps_videohfr);
    public final static Key<SettingMode> LENS_FILTER = new Key(SettingMode.class, R.string.aps_lensfilter);

    public final static Key<SettingMode> AE_TARGET_FPS = new Key(SettingMode.class, R.string.aps_ae_targetFPS);
    public final static ApiKey<SettingMode> MODULE = new ApiKey(SettingMode.class, R.string.aps_module);
    public final static Key<SettingMode> DUAL_PRIMARY_CAMERA_MODE = new Key(SettingMode.class, R.string.aps_dualprimarycameramode);
    public final static Key<SettingMode> SECONDARY_SENSOR_SIZE = new Key(SettingMode.class, R.string.aps_secondarySensorSize);

    public final static Key<SettingMode> EDGE_MODE = new Key(SettingMode.class, R.string.aps_edgemode);
    public final static Key<SettingMode> DISTORTION_CORRECTION_MODE = new Key(SettingMode.class, R.string.aps_distortionmode);
    public final static Key<SettingMode> FACE_DETECTOR_MODE = new Key(SettingMode.class, R.string.aps_facemode);
    public final static Key<SettingMode> COLOR_CORRECTION_MODE = new Key(SettingMode.class, R.string.aps_colorcorrectionmode);
    public final static Key<SettingMode> HOT_PIXEL_MODE = new Key(SettingMode.class, R.string.aps_hotpixel);
    public final static Key<SettingMode> TONE_MAP_MODE = new Key(SettingMode.class, R.string.aps_tonemapmode);
    public final static Key<SettingMode> TONE_CURVE_PARAMETER = new Key(SettingMode.class, R.string.aps_tonecurve);
    public final static Key<SettingMode> CONTROL_MODE = new Key(SettingMode.class, R.string.aps_controlmode);
    public final static Key<SettingMode> OIS_MODE = new Key(SettingMode.class, R.string.aps_ois);
    public final static Key<SettingMode> SD_SAVE_LOCATION = new Key(SettingMode.class, R.string.aps_sdcard);


    public final static Key<SettingMode> INTERVAL_DURATION = new Key(SettingMode.class, R.string.aps_interval_duration);
    public final static Key<SettingMode> INTERVAL_SHUTTER_SLEEP = new Key(SettingMode.class, R.string.aps_interval);
    public final static Key<SettingMode> OPCODE = new Key(SettingMode.class, R.string.aps_opcode);
    public final static Key<SettingMode> BAYERFORMAT = new Key(SettingMode.class, R.string.aps_bayformat);
    public final static Key<SettingMode> MATRIX_SET = new Key(SettingMode.class, R.string.aps_matrixset);
    public final static Key<SettingMode> TONEMAP_SET = new Key(SettingMode.class, R.string.aps_tonemapProfile);
    public final static Key<SettingMode> RAW_PICTURE_FORMAT_SETTING = new Key(SettingMode.class, R.string.aps_rawpictureformat);
    public final static Key<SettingMode> SELF_TIMER = new Key(SettingMode.class, R.string.aps_selftimer);


    public final static Key<SettingMode> VIDEO_AUDIO_SOURCE = new Key(SettingMode.class, R.string.aps_video_audio_source);
    public final static Key<SettingMode> AF_BRACKET_MIN = new Key(SettingMode.class, R.string.aps_afbracketmin);
    public final static Key<SettingMode> AF_BRACKET_MAX = new Key(SettingMode.class, R.string.aps_afbracketmax);
    public final static Key<SettingMode> TIMELAPSE_FRAMES = new Key(SettingMode.class, R.string.aps_timelapseframes);


    public final static Key<TypedSettingMode> NIGHT_MODE = new Key(TypedSettingMode.class, R.string.aps_nightmode);
    public final static Key<TypedSettingMode> HDR_MODE = new Key(TypedSettingMode.class, R.string.aps_hdrmode);
    public final static Key<GlobalBooleanSettingMode> NEED_RESTART_AFTER_CAPTURE = new Key(GlobalBooleanSettingMode.class, R.string.aps_needrestartaftercapture);
    public final static Key<GlobalBooleanSettingMode> FORCE_RAW_TO_DNG = new Key(GlobalBooleanSettingMode.class, R.string.aps_forcerawtondng);

    public final static Key<ApiBooleanSettingMode> USE_QCOM_FOCUS = new Key(ApiBooleanSettingMode.class, R.string.aps_qcomfocus);
    public final static Key<ApiBooleanSettingMode> SUPPORT_12_BIT_RAW = new Key(ApiBooleanSettingMode.class, R.string.aps_support12bitraw);
    public final static Key<SettingMode> ORIENTATION_HACK = new Key(SettingMode.class, R.string.aps_orientationHack);
    public final static Key<ApiBooleanSettingMode> OPEN_CAMERA_1_LEGACY = new Key(ApiBooleanSettingMode.class, R.string.aps_opencamera1legacy);
    public final static Key<ApiBooleanSettingMode> USE_HUAWEI_WHITE_BALANCE = new Key(ApiBooleanSettingMode.class, R.string.aps_usehuawei_wb);
    public final static Key<ApiBooleanSettingMode> SWITCH_ASPECT_RATIO = new Key(ApiBooleanSettingMode.class, R.string.aps_switch_aspect_ratio);
    public final static Key<ApiBooleanSettingMode> XIAOMI_VIDEO_RECORD_CONTROL = new Key(ApiBooleanSettingMode.class, R.string.aps_xiaomi_video_record_control);
    public final static Key<ApiBooleanSettingMode> XIAOMI_PRO_VIDEO_LOG = new Key(ApiBooleanSettingMode.class, R.string.aps_xiaomi_pro_video_log);
    public final static Key<ApiBooleanSettingMode> QCOM_VIDEO_HDR10 = new Key(ApiBooleanSettingMode.class, R.string.aps_qcom_video_hdr);
    public final static Key<ApiBooleanSettingMode> XIAOMI_MFNR = new Key(ApiBooleanSettingMode.class, R.string.aps_xiaomi_mfnr);

    public final static Key<ApiBooleanSettingMode> ZOOM_ON_MANUALFOCUS = new Key(ApiBooleanSettingMode.class, R.string.aps_zoom_on_mf);
    public final static Key<SettingMode> ZOOM_ON_MANUALFOCUS_ZOOMFACTOR = new Key(SettingMode.class, R.string.aps_zoom_on_mf_zoomfactor);
    public final static Key<SettingMode> ZOOM_ON_MANUALFOCUS_ZOOMDURATION = new Key(SettingMode.class, R.string.aps_zoom_on_mf_zoomduration);
    public final static Key<ApiBooleanSettingMode> HISTOGRAM_STATS_QCOM = new Key(ApiBooleanSettingMode.class, R.string.aps_histogram_stats_qcom);
    public final static Key<ApiBooleanSettingMode> USE_QCOM_AE = new Key(ApiBooleanSettingMode.class, R.string.aps_use_qcom_ae);

    public final static Key<GlobalBooleanSettingMode> USE_CUSTOM_MATRIX_ON_CAMERA_2 = new Key(GlobalBooleanSettingMode.class, R.string.aps_usecustom_matrix_oncamera2);

    public final static Key<ApiBooleanSettingMode> FOCUSPEAK = new Key(ApiBooleanSettingMode.class, R.string.aps_focuspeak);
    public final static Key<ApiBooleanSettingMode> HISTOGRAM = new Key(ApiBooleanSettingMode.class, R.string.aps_histogram);
    public final static Key<ApiBooleanSettingMode> CLIPPING = new Key(ApiBooleanSettingMode.class, R.string.aps_clipping);
    public final static Key<ApiBooleanSettingMode> FORCE_WIDE_PREVIEW = new Key(ApiBooleanSettingMode.class, R.string.aps_force_wide_preview);


    public final static GlobalKey<GlobalBooleanSettingMode> CHECKFORUPDATES = new GlobalKey<>(GlobalBooleanSettingMode.class, R.string.aps_checkforupdates);
    public final static GlobalKey<SettingMode> THEME = new GlobalKey<>(SettingMode.class, R.string.aps_theme);
    public final static GlobalKey<GlobalBooleanSettingMode> SHOWMANUALSETTINGS = new GlobalKey<>(GlobalBooleanSettingMode.class, R.string.aps_showmanualsettings);
    public final static GlobalKey<SettingMode> PREVIEW_POST_PROCESSING_MODE = new GlobalKey<>(SettingMode.class, R.string.aps_preview_post_processing_mode);
    public final static GlobalKey<GlobalBooleanSettingMode> NIGHT_OVERLAY = new GlobalKey<>(GlobalBooleanSettingMode.class, R.string.aps_nightoverlay);
    public final static GlobalKey<GlobalBooleanSettingMode> LOCATION_MODE = new GlobalKey(GlobalBooleanSettingMode.class, R.string.aps_location);
    public final static GlobalKey<GlobalBooleanSettingMode> TOUCH_TO_CAPTURE = new GlobalKey(GlobalBooleanSettingMode.class, R.string.aps_touchtocapture);
    public final static GlobalKey<SettingMode> GUIDE_LIST = new GlobalKey(SettingMode.class, R.string.aps_guide);
    public final static GlobalKey<SettingMode> HORIZONT_LVL = new GlobalKey(SettingMode.class, R.string.aps_horizontlvl);
    public final static GlobalKey<SettingMode> FOCUSPEAK_COLOR = new GlobalKey(SettingMode.class, R.string.aps_focuspeakcolor);
    public final static GlobalKey<GlobalBooleanSettingMode> PLAY_SHUTTER_SOUND = new GlobalKey(GlobalBooleanSettingMode.class, R.string.aps_playshuttersound);
    public final static GlobalKey<GlobalBooleanSettingMode> USE_FREEDCAM_AE = new GlobalKey(GlobalBooleanSettingMode.class, R.string.aps_use_freedcam_ae);
    public final static Key<SettingMode> MIN_ISO = new Key(SettingMode.class, R.string.aps_min_iso);
    public final static Key<SettingMode> MAX_ISO = new Key(SettingMode.class, R.string.aps_max_iso);
    public final static Key<SettingMode> MAX_EXPOSURE = new Key(SettingMode.class, R.string.aps_max_exposure);
    public final static Key<SettingMode> MIN_EXPOSURE = new Key(SettingMode.class, R.string.aps_min_exposure);


    public final static ApiKey<SettingMode> PREVIEW_TEMPLATE = new ApiKey(SettingMode.class, R.string.aps_preview_template_mode);
    public final static ApiKey<SettingMode> CAPTURE_TEMPLATE = new ApiKey(SettingMode.class, R.string.aps_capture_template_mode);
    public final static ApiKey<GlobalBooleanSettingMode> SUPPORT_POST_RAW_SENSITIVITY_BOOST = new ApiKey(GlobalBooleanSettingMode.class, R.string.aps_support_postrawsensitivityboost);
    public final static ApiKey<SettingMode> AE_METERING = new ApiKey(SettingMode.class, R.string.aps_support_aemetering);
    public final static ApiKey<GlobalBooleanSettingMode> HIDE_CAMERA_UI = new ApiKey(GlobalBooleanSettingMode.class, R.string.aps_support_hidecameraui);

    /**
     *
     * @return a list with all statics keys in this class
     */
    public static Key[] getKeyList()
    {
        List<Key> keys = new ArrayList<>();
        Field[] fields = SettingKeys.class.getDeclaredFields();
        for(Field f : fields)
        {
            int mod = f.getModifiers();
            if (Modifier.isPublic(mod)
                    && Modifier.isStatic(mod)
                    && Modifier.isFinal(mod)
                    && (f.getType() == Key.class || f.getType() == GlobalKey.class || f.getType() == ApiKey.class))
                try {
                    keys.add((Key)f.get(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
        }
        return keys.toArray(new Key[keys.size()]);
    }



    public static class Key<T>
    {
        //class reference that is represented by that key
        private final Class<T> type;
        //string id that get used by this by the sharedpreference
        private final int ressourcesStringID;

        private Key(Class<T> type, int ressourcesStringID)
        {
            this.type = type;
            this.ressourcesStringID = ressourcesStringID;
        }

        public Class<T> getType()
        {
            return type;
        }

        public int getRessourcesStringID()
        {
            return ressourcesStringID;
        }

        @Override
        public String toString() {
            return FreedApplication.getStringFromRessources(ressourcesStringID);
        }
    }

    public static class GlobalKey<T> extends Key<T>
    {
        private GlobalKey(Class type, int ressourcesStringID) {
            super(type, ressourcesStringID);
        }
    }

    public static class ApiKey<T> extends Key<T>
    {
        private ApiKey(Class type, int ressourcesStringID) {
            super(type, ressourcesStringID);
        }
    }
}
