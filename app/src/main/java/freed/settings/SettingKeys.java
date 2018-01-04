package freed.settings;

import com.troop.freedcam.R;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

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
    public final static Key<SettingMode> M_Sharpness = new Key(SettingMode.class, R.string.aps_manualsharpness);
    public final static Key<SettingMode> M_Contrast = new Key(SettingMode.class, R.string.aps_manualcontrast);
    public final static Key<SettingMode> M_Saturation = new Key(SettingMode.class, R.string.aps_manualsaturation);
    public final static Key<SettingMode> M_ExposureCompensation = new Key(SettingMode.class, R.string.aps_manualexpocomp);
    public final static Key<SettingMode> M_3D_Convergence = new Key(SettingMode.class, R.string.aps_manualconvergence);
    public final static Key<SettingMode> M_Fnumber = new Key(SettingMode.class, R.string.aps_manualfnum);
    public final static Key<SettingMode> M_Burst = new Key(SettingMode.class, R.string.aps_manualburst);
    public final static Key<SettingMode> M_FX = new Key(SettingMode.class, R.string.aps_manualfx);
    public final static Key<SettingMode> M_Zoom = new Key(SettingMode.class, R.string.aps_manualzoom);
    public final static Key<SettingMode> M_ProgramShift = new Key(SettingMode.class, R.string.aps_manualprogramshift);
    public final static Key<SettingMode> M_PreviewZoom = new Key(SettingMode.class, R.string.aps_manualpreviewzoom);
    public final static Key<SettingMode> M_Aperture = new Key(SettingMode.class, R.string.aps_manualaperture);

    public final static Key<SettingMode> ColorMode = new Key(SettingMode.class, R.string.aps_colormode);
    public final static Key<SettingMode> ExposureMode = new Key(SettingMode.class, R.string.aps_exposuremode);
    public final static Key<SettingMode> AE_PriorityMode = new Key(SettingMode.class, R.string.aps_ae_priortiy);
    public final static Key<SettingMode> FlashMode = new Key(SettingMode.class, R.string.aps_flashmode);
    public final static Key<SettingMode> IsoMode = new Key(SettingMode.class, R.string.aps_isomode);
    public final static Key<SettingMode> AntiBandingMode = new Key(SettingMode.class, R.string.aps_antibandingmode);
    public final static Key<SettingMode> WhiteBalanceMode = new Key(SettingMode.class, R.string.aps_whitebalancemode);
    public final static Key<SettingMode> PictureSize = new Key(SettingMode.class, R.string.aps_picturesize);
    public final static Key<SettingMode> PictureFormat = new Key(SettingMode.class, R.string.aps_pictureformat);

    public final static Key<SettingMode> JpegQuality = new Key(SettingMode.class, R.string.aps_jpegquality);
    public final static Key<SettingMode> GuideList = new Key(SettingMode.class, R.string.aps_guide);
    public final static Key<SettingMode> ImagePostProcessing = new Key(SettingMode.class, R.string.aps_ippmode);
    public final static Key<SettingMode> PreviewSize = new Key(SettingMode.class, R.string.aps_previewsize);
    public final static Key<SettingMode> PreviewFPS = new Key(SettingMode.class, R.string.aps_previewfps);
    public final static Key<SettingMode> PreviewFormat = new Key(SettingMode.class, R.string.aps_previewformat);
    public final static Key<SettingMode> PreviewFpsRange = new Key(SettingMode.class, R.string.aps_previewfpsrange);
    public final static Key<SettingMode> SceneMode = new Key(SettingMode.class, R.string.aps_scenemode);
    public final static Key<SettingMode> FocusMode = new Key(SettingMode.class, R.string.aps_focusmode);
    public final static Key<SettingMode> RedEye = new Key(SettingMode.class, R.string.aps_redeyemode);
    public final static Key<SettingMode> LensShade = new Key(SettingMode.class, R.string.aps_lenshademode);
    public final static Key<SettingMode> ZSL = new Key(SettingMode.class, R.string.aps_zslmode);
    public final static Key<SettingMode> SceneDetect = new Key(SettingMode.class, R.string.aps_scenedetectmode);
    public final static Key<SettingMode> Denoise = new Key(SettingMode.class, R.string.aps_denoisemode);

    public final static Key<SettingMode> PDAF = new Key(SettingMode.class, R.string.aps_pdaf);
    public final static Key<SettingMode> TNR = new Key(SettingMode.class, R.string.aps_tnr);//temporal noise reduction
    public final static Key<SettingMode> TNR_V = new Key(SettingMode.class, R.string.aps_tnr_v);//temporal noise reduction video
    public final static Key<SettingMode> RDI = new Key(SettingMode.class, R.string.aps_rdi);
    public final static Key<SettingMode> TruePotrait = new Key(SettingMode.class, R.string.aps_truepotrait);
    public final static Key<SettingMode> ReFocus = new Key(SettingMode.class, R.string.aps_refocus);
    public final static Key<SettingMode> SeeMore = new Key(SettingMode.class, R.string.aps_seemore);// advanced wb correction?
    public final static Key<SettingMode> OptiZoom = new Key(SettingMode.class, R.string.aps_optizoom);
    public final static Key<SettingMode> ChromaFlash = new Key(SettingMode.class, R.string.aps_chroma_flash);

    public final static Key<SettingMode> DigitalImageStabilization = new Key(SettingMode.class, R.string.aps_digitalimagestabmode);
    public final static Key<SettingMode> VideoStabilization = new Key(SettingMode.class, R.string.aps_videoStabilisation);
    public final static Key<SettingMode> MemoryColorEnhancement = new Key(SettingMode.class, R.string.aps_memorycolorenhancementmode);

    public final static Key<SettingMode> NonZslManualMode = new Key(SettingMode.class, R.string.aps_nonzslmanualmode); //used on htc devices to enable q3a on raw capture
    public final static Key<SettingMode> AE_Bracket = new Key(SettingMode.class, R.string.aps_aebrackethdr);
    public final static Key<SettingMode> ExposureLock = new Key(SettingMode.class, R.string.aps_exposuremode);
    public final static Key<SettingMode> CDS_Mode = new Key(SettingMode.class, R.string.aps_cds);
    public final static Key<SettingMode> HTCVideoMode = new Key(SettingMode.class, R.string.aps_htcvideoMode);
    public final static Key<SettingMode> HTCVideoModeHSR = new Key(SettingMode.class, R.string.aps_htcvideoModehsr);

    public final static Key<SettingMode> VideoProfiles = new Key(SettingMode.class, R.string.aps_videoProfile);
    public final static Key<SettingMode> VideoSize = new Key(SettingMode.class, R.string.aps_videoSize);
    public final static Key<SettingMode> VideoHDR = new Key(SettingMode.class, R.string.aps_videohdr);
    public final static Key<SettingMode> VideoHighFramerate = new Key(SettingMode.class, R.string.aps_videohfr);
    public final static Key<SettingMode> LensFilter = new Key(SettingMode.class, R.string.aps_lensfilter);
    public final static Key<SettingMode> HorizontLvl = new Key(SettingMode.class, R.string.aps_horizontlvl);
    public final static Key<SettingMode> Ae_TargetFPS = new Key(SettingMode.class, R.string.aps_ae_targetFPS);

    public final static Key<SettingMode> ContShootMode = new Key(SettingMode.class, R.string.aps_contshootmode);
    public final static Key<SettingMode> ContShootModeSpeed = new Key(SettingMode.class, R.string.aps_contshootmodespeed);
    public final static Key<SettingMode> ObjectTracking = new Key(SettingMode.class, R.string.aps_objecttracking);
    public final static Key<SettingMode> PostViewSize = new Key(SettingMode.class, R.string.aps_postviewsize);
    public final static Key<SettingMode> Focuspeak = new Key(SettingMode.class, R.string.aps_focuspeak);
    public final static Key<SettingMode> Module = new Key(SettingMode.class, R.string.aps_module);
    public final static Key<SettingMode> ZoomSetting = new Key(SettingMode.class, R.string.aps_zoommode);
    public final static Key<SettingMode> dualPrimaryCameraMode = new Key(SettingMode.class, R.string.aps_dualprimarycameramode);

    public final static Key<SettingMode> EDGE_MODE = new Key(SettingMode.class, R.string.aps_edgemode);
    public final static Key<SettingMode> COLOR_CORRECTION_MODE = new Key(SettingMode.class, R.string.aps_colorcorrectionmode);
    public final static Key<SettingMode> HOT_PIXEL_MODE = new Key(SettingMode.class, R.string.aps_hotpixel);
    public final static Key<SettingMode> TONE_MAP_MODE = new Key(SettingMode.class, R.string.aps_tonemapmode);
    public final static Key<SettingMode> TONE_CURVE_PARAMETER = new Key(SettingMode.class, R.string.aps_tonecurve);
    public final static Key<SettingMode> CONTROL_MODE = new Key(SettingMode.class, R.string.aps_controlmode);
    public final static Key<SettingMode> OIS_MODE = new Key(SettingMode.class, R.string.aps_ois);
    public final static Key<SettingMode> SD_SAVE_LOCATION = new Key(SettingMode.class, R.string.aps_sdcard);
    public final static Key<SettingMode> LOCATION_MODE = new Key(SettingMode.class, R.string.aps_location);
    public final static Key<SettingMode> INTERVAL_DURATION = new Key(SettingMode.class, R.string.aps_interval_duration);
    public final static Key<SettingMode> INTERVAL_SHUTTER_SLEEP = new Key(SettingMode.class, R.string.aps_interval);
    public final static Key<SettingMode> OPCODE = new Key(SettingMode.class, R.string.aps_opcode);
    public final static Key<SettingMode> BAYERFORMAT = new Key(SettingMode.class, R.string.aps_bayformat);
    public final static Key<SettingMode> MATRIX_SET = new Key(SettingMode.class, R.string.aps_matrixset);
    public final static Key<SettingMode> TONEMAP_SET = new Key(SettingMode.class, R.string.aps_tonemapProfile);
    public final static Key<SettingMode> SCALE_PREVIEW = new Key(SettingMode.class, R.string.aps_scalePreview);
    public final static Key<SettingMode> RAW_PICTURE_FORMAT_SETTING = new Key(SettingMode.class, R.string.aps_rawpictureformat);
    public final static Key<SettingMode> selfTimer = new Key(SettingMode.class, R.string.aps_selftimer);

    public final static Key<TypedSettingMode> M_ManualIso = new Key(TypedSettingMode.class, R.string.aps_manualiso);
    public final static Key<TypedSettingMode> M_Whitebalance = new Key(TypedSettingMode.class, R.string.aps_manualwb);
    public final static Key<TypedSettingMode> M_Focus = new Key(TypedSettingMode.class, R.string.aps_manualfocus);
    public final static Key<TypedSettingMode> M_ExposureTime = new Key(TypedSettingMode.class, R.string.aps_manualexpotime);
    public final static Key<TypedSettingMode> NightMode = new Key(TypedSettingMode.class, R.string.aps_nightmode);
    public final static Key<TypedSettingMode> HDRMode = new Key(TypedSettingMode.class, R.string.aps_hdrmode);
    //public final static Key<SettingMode> M_ToneCurve = new Key(SettingMode.class, R.string.aps_manual);

    public final static Key<GlobalBooleanSettingMode> useHuaweiCamera2Extension = new Key(GlobalBooleanSettingMode.class, R.string.aps_usehuaweicam2);
    public final static Key<GlobalBooleanSettingMode> needRestartAfterCapture = new Key(GlobalBooleanSettingMode.class, R.string.aps_needrestartaftercapture);
    public final static Key<GlobalBooleanSettingMode> forceRawToDng = new Key(GlobalBooleanSettingMode.class, R.string.aps_forcerawtondng);
    public final static Key<GlobalBooleanSettingMode> areFeaturesDetected = new Key(GlobalBooleanSettingMode.class, R.string.aps_arefeaturesdetected);
    public final static Key<GlobalBooleanSettingMode> NightOverlay = new Key(GlobalBooleanSettingMode.class, R.string.aps_nightoverlay);

    public final static Key<ApiBooleanSettingMode> useQcomFocus = new Key(ApiBooleanSettingMode.class, R.string.aps_qcomfocus);
    public final static Key<ApiBooleanSettingMode> support12bitRaw = new Key(ApiBooleanSettingMode.class, R.string.aps_support12bitraw);
    public final static Key<ApiBooleanSettingMode> orientationHack = new Key(ApiBooleanSettingMode.class, R.string.aps_orientationHack);
    public final static Key<ApiBooleanSettingMode> openCamera1Legacy = new Key(ApiBooleanSettingMode.class, R.string.aps_opencamera1legacy);

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
                    && f.getType() == Key.class)
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
        private Class<T> type;
        //string id that get used by this by the sharedpreference
        private int ressourcesStringID;

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
    }
}
