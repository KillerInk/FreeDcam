package freed.cam.ui.themenextgen.layoutconfig;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.List;

import freed.settings.SettingKeys;

public class SettingGroupConfig
{
    public List<SettingItemConfig> getVideoGroup()
    {
        ArrayList<SettingItemConfig> group = new ArrayList();
        group.add(new SettingItemConfig(SettingKeys.VIDEO_PROFILES, R.string.setting_videoprofile_header, R.string.setting_videoprofile_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.TIMELAPSE_FRAMES, 0, 0,true, SettingItemConfig.ViewType.Custom));
        group.add(new SettingItemConfig(null, R.string.setting_videoprofileeditor_header, R.string.setting_videoprofileeditor_description,false, SettingItemConfig.ViewType.Custom));
        group.add(new SettingItemConfig(SettingKeys.VIDEO_HDR, R.string.setting_videohdr_header, R.string.setting_videohdr_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.VIDEO_STABILIZATION, R.string.setting_vs_header, R.string.setting_vs_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.VIDEO_AUDIO_SOURCE, R.string.setting_videoaudiosource_header, R.string.setting_videoaudiosource_description,false, SettingItemConfig.ViewType.Text));
        return group;
    }

    public List<SettingItemConfig> getPictureGroup()
    {
        ArrayList<SettingItemConfig> group = new ArrayList();
        group.add(new SettingItemConfig(SettingKeys.PICTURE_SIZE, R.string.setting_picturesize_header, R.string.setting_picturesize_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.PICTURE_FORMAT, R.string.setting_pictureformat_header, R.string.setting_pictureformat_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.YUV_SIZE, R.string.setting_yuvsize_header, R.string.setting_yuvsize_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.SECONDARY_SENSOR_SIZE, R.string.setting_secondarypicturesize_header, R.string.setting_secondarypicturesize_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.JPEG_QUALITY, R.string.setting_jpegquality_header, R.string.setting_jpegquality_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.MFNR, R.string.setting_mfnr_header,R.string.setting_mfnr_description,false, SettingItemConfig.ViewType.Boolean));
        group.add(new SettingItemConfig(SettingKeys.XIAOMI_MFNR, R.string.setting_xiaomimfnr_header,R.string.setting_mfnr_description,false, SettingItemConfig.ViewType.Boolean));
        return group;
    }

    public List<SettingItemConfig> getIntervalGroup()
    {
        ArrayList<SettingItemConfig> group = new ArrayList();
        group.add(new SettingItemConfig(SettingKeys.INTERVAL_SHUTTER_SLEEP, R.string.setting_interval_header, R.string.setting_interval_texter,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.INTERVAL_DURATION, R.string.setting_interval_duration_header, R.string.setting_interval_duration_text,false, SettingItemConfig.ViewType.Text));
        return group;
    }

    public List<SettingItemConfig> getRawGroup()
    {
        ArrayList<SettingItemConfig> group = new ArrayList();
        group.add(new SettingItemConfig(SettingKeys.OPCODE, R.string.setting_opcode_header, R.string.setting_opcode_description,false, SettingItemConfig.ViewType.Custom));
        group.add(new SettingItemConfig(SettingKeys.BAYERFORMAT, R.string.setting_bayerformat_header, R.string.setting_bayerformat_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.MATRIX_SET, R.string.setting_matrixchooser_header, R.string.setting_matrixchooser_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.TONEMAP_SET, R.string.setting_tonemapchooser_header, R.string.setting_tonemapchooser_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.FORCE_RAW_TO_DNG,R.string.setting_forcerawtodng_header, R.string.setting_forcerawtodng_description, true, SettingItemConfig.ViewType.Boolean));
        group.add(new SettingItemConfig(SettingKeys.USE_CUSTOM_MATRIX_ON_CAMERA_2, R.string.setting_usecustomdngprofile_header, R.string.setting_usecustomdngprofile_description,true, SettingItemConfig.ViewType.Boolean));
        group.add(new SettingItemConfig(SettingKeys.RAW_SIZE, R.string.setting_rawsize_header, R.string.setting_rawsize_description,false, SettingItemConfig.ViewType.Text));
        return group;
    }

    public List<SettingItemConfig> getEtcGroup()
    {
        ArrayList<SettingItemConfig> group = new ArrayList();
        group.add(new SettingItemConfig(null, R.string.setting_api_header, R.string.setting_api_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.SD_SAVE_LOCATION, R.string.setting_sdcard_header, R.string.setting_sdcard_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.LOCATION_MODE,R.string.setting_location_header, R.string.setting_location_description,false, SettingItemConfig.ViewType.Custom));
        group.add(new SettingItemConfig(SettingKeys.NIGHT_OVERLAY,R.string.setting_nightoverlay_header, R.string.setting_nightoverlay_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.TOUCH_TO_CAPTURE,R.string.setting_touchtocapture_header, R.string.setting_touchtocapture_description,true, SettingItemConfig.ViewType.Boolean));
        group.add(new SettingItemConfig(SettingKeys.PLAY_SHUTTER_SOUND,R.string.setting_playshuttersound_header, R.string.setting_playshuttersound_description,true, SettingItemConfig.ViewType.Boolean));
        group.add(new SettingItemConfig(null,R.string.setting_savecamparams_header,R.string.setting_savecamparams_description,true, SettingItemConfig.ViewType.Custom));
        group.add(new SettingItemConfig(SettingKeys.OPEN_CAMERA_1_LEGACY,R.string.setting_opencameralegacy_header, R.string.setting_opencameralegacy_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(null,R.string.setting_featuredetector_header,R.string.setting_featuredetector_description,false, SettingItemConfig.ViewType.Custom));
        group.add(new SettingItemConfig(SettingKeys.CHECKFORUPDATES, R.string.setting_checkforupdate_header, R.string.setting_checkforupdate_description,true, SettingItemConfig.ViewType.Boolean));
        group.add(new SettingItemConfig(null, R.string.setting_dump_vendor_keys_header,R.string.setting_dump_vendor_keys_description,true, SettingItemConfig.ViewType.Custom));
        group.add(new SettingItemConfig(SettingKeys.THEME, R.string.setting_theme_header,R.string.setting_theme_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.PREVIEW_TEMPLATE, R.string.setting_previewtemplate_header,R.string.setting_previewtemplate_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.CAPTURE_TEMPLATE, R.string.setting_capturetemplate_header,R.string.setting_capturetemplate_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.HIDE_CAMERA_UI, R.string.setting_disablecameraui_header, R.string.setting_disablecameraui_description,true, SettingItemConfig.ViewType.Boolean));
        return group;
    }

    public List<SettingItemConfig> getPreviewGroup()
    {
        ArrayList<SettingItemConfig> group = new ArrayList();
        group.add(new SettingItemConfig(SettingKeys.PREVIEW_POST_PROCESSING_MODE, R.string.setting_enablerenderscript_header, R.string.setting_enablerenderscript_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.FOCUSPEAK_COLOR, R.string.setting_focuspeakcolor_header, R.string.setting_focuspeakcolor_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.ORIENTATION_HACK, R.string.setting_orientation_header, R.string.setting_orientation_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.SWITCH_ASPECT_RATIO,R.string.setting_switch_aspect_header, R.string.setting_switch_aspect_text,true, SettingItemConfig.ViewType.Custom));
        group.add(new SettingItemConfig(SettingKeys.USE_FREEDCAM_AE,R.string.setting_usefreedae_header, R.string.setting_use_freedae_text, true, SettingItemConfig.ViewType.Custom));
        group.add(new SettingItemConfig(SettingKeys.MAX_ISO,R.string.setting_maxiso_header, R.string.setting_maxiso_text,true, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.MIN_ISO,R.string.setting_miniso_header, R.string.setting_miniso_text,true, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.MIN_EXPOSURE,R.string.setting_minexpotime_header, R.string.setting_minexpotime_text,true, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.MAX_EXPOSURE,R.string.setting_maxexpotime_header, R.string.setting_maxexpotime_text,true, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.GUIDE_LIST, R.string.setting_guide_header, R.string.setting_guide_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.HORIZONT_LVL, R.string.setting_horizont_header, R.string.setting_horizont_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.ZOOM_ON_MANUALFOCUS,R.string.setting_zoom_on_mf_header, R.string.setting_zoom_on_mf_description, true, SettingItemConfig.ViewType.Boolean));
        group.add(new SettingItemConfig(SettingKeys.ZOOM_ON_MANUALFOCUS_ZOOMFACTOR,R.string.setting_zoom_on_mf_factor_header, R.string.setting_zoom_on_mf_factor_description, true, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.ZOOM_ON_MANUALFOCUS_ZOOMDURATION,R.string.setting_zoom_on_mf_duration_header, R.string.setting_zoom_on_mf_duration_description, true, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.FORCE_WIDE_PREVIEW,R.string.setting_force_wide_preview_header, R.string.setting_forcerawtodng_description, false, SettingItemConfig.ViewType.Boolean));

        return group;
    }

    public List<SettingItemConfig> getCameraGroup()
    {
        ArrayList<SettingItemConfig> group = new ArrayList();
        group.add(new SettingItemConfig(SettingKeys.SCENE_MODE, R.string.setting_scene_header,R.string.setting_scene_description ,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.WHITE_BALANCE_MODE, R.string.uisetting_wb_header,R.string.setting_distortion_description ,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.COLOR_MODE, R.string.setting_color_header, R.string.setting_color_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.COLOR_CORRECTION_MODE, R.string.setting_colorcorrection_header, R.string.setting_colorcorrection_description,false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.TONE_MAP_MODE, R.string.setting_tonemap_header, R.string.setting_tonemap_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.CONTROL_MODE, R.string.setting_controlmode_header, R.string.setting_controlmode_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.RED_EYE, R.string.setting_redeye_header, R.string.setting_redeye_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.ANTI_BANDING_MODE, R.string.setting_antiflicker_header, R.string.setting_antiflicker_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.IMAGE_POST_PROCESSING, R.string.setting_ipp_header, R.string.setting_ipp_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.LENS_SHADE, R.string.setting_lensshade_header, R.string.setting_lensshade_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.DENOISE, R.string.setting_waveletdenoise_header, R.string.setting_waveletdenoise_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.SCENE_DETECT, R.string.setting_scenedec_header, R.string.setting_scenedec_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.TNR, R.string.setting_temporaldenoise_header, R.string.setting_temporaldenoise_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.TNR_V, R.string.setting_temporaldenoiseV_header, R.string.setting_temporaldenoiseV_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.PDAF, R.string.setting_pdaf_header, R.string.setting_pdaf_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.TRUE_POTRAIT, R.string.setting_truepotrait_header, R.string.setting_truepotrait_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.RDI, R.string.setting_rdi_header, R.string.setting_rdi_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.CHROMA_FLASH, R.string.setting_chroma_header, R.string.setting_chroma_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.OPTI_ZOOM, R.string.setting_optizoom_header, R.string.setting_optizoom_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.RE_FOCUS, R.string.setting_refocus_header, R.string.setting_refous_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.SEE_MORE, R.string.setting_seemore_header, R.string.setting_seemore_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.LENS_FILTER, R.string.setting_lensfilter_header, R.string.setting_lensfilter_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.DIGITAL_IMAGE_STABILIZATION, R.string.setting_dis_header, R.string.setting_dis_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.MEMORY_COLOR_ENHANCEMENT, R.string.setting_mce_header, R.string.setting_mce_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.ZSL, R.string.setting_zsl_header, R.string.setting_zsl_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.HDR_MODE, R.string.setting_hdrmode_header, R.string.setting_hdrmode_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.NON_ZSL_MANUAL_MODE, R.string.setting_nonzsl_header, R.string.setting_nonzsl_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.CDS_MODE, R.string.setting_cds_header, R.string.setting_cds_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.EDGE_MODE, R.string.setting_edge_header, R.string.setting_edge_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.HOT_PIXEL_MODE, R.string.setting_hotpixel_header, R.string.setting_hotpixel_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.DISTORTION_CORRECTION_MODE, R.string.setting_distortion_header, R.string.setting_distortion_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.FACE_DETECTOR_MODE, R.string.setting_facemode_header, R.string.setting_facemode_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.OIS_MODE, R.string.setting_ois_header, R.string.setting_ois_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.DUAL_PRIMARY_CAMERA_MODE, R.string.setting_dualprimarycamera_header, R.string.setting_dualprimarycamera_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.EXPOSURE_MODE, R.string.setting_exposuremode_header, R.string.setting_exposuremode_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.AE_TARGET_FPS, R.string.setting_aetargetfps_header, R.string.setting_aetargetfps_description, false, SettingItemConfig.ViewType.Text));
        group.add(new SettingItemConfig(SettingKeys.AE_METERING, R.string.setting_aemetering_header, R.string.setting_aemetering_description, false, SettingItemConfig.ViewType.Text));

        return group;
    }
}
