package freed.cam.ui.themesample.settings;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.troop.freedcam.R;

import javax.inject.Inject;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterHandler;
import freed.cam.apis.basecamera.parameters.modes.ApiParameter;
import freed.cam.apis.basecamera.parameters.modes.SDModeParameter;
import freed.cam.apis.basecamera.parameters.modes.SettingModeParamter;
import freed.cam.apis.camera2.Camera2;
import freed.cam.previewpostprocessing.PreviewPostProcessingModes;
import freed.cam.ui.themesample.SettingsChildAbstract;
import freed.cam.ui.themesample.settings.childs.GroupChild;
import freed.cam.ui.themesample.settings.childs.SettingsChildDumpCamera2VendorKeys;
import freed.cam.ui.themesample.settings.childs.SettingsChildFeatureDetect;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenu;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuSaveCamParams;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuTimeLapseFrames;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuVideoHDR;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuVideoProfile;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenu_VideoProfEditor;
import freed.cam.ui.themesample.settings.childs.SettingsChild_BooleanSetting;
import freed.cam.ui.themesample.settings.childs.SettingsChild_FreedAe;
import freed.cam.ui.themesample.settings.childs.SettingsChild_SwitchAspectRatio;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.settings.mode.BooleanSettingModeInterface;
import freed.update.ReleaseChecker;
import freed.utils.Log;

public class SettingsMenuItemFactory
{

    private static final String TAG = SettingsMenuItemFactory.class.getSimpleName();

    private final ApiParameter apiParameter;

    @Inject
    public SettingsMenuItemFactory(ApiParameter apiParameter)
    {
        this.apiParameter = apiParameter;
    }

    public void fillLeftSettingsMenu(CameraWrapperInterface cameraUiWrapper, Context context, SettingsChildAbstract.SettingsChildClick click, LinearLayout settingsChildHolder)
    {
        SettingsManager apS = FreedApplication.settingsManager();
        if (cameraUiWrapper != null) {


            ParameterHandler params = cameraUiWrapper.getParameterHandler();
            if (params != null) {
        /*
            VIDEOGROUP
         */
                GroupChild videoGroup = new GroupChild(context, context.getResources().getString(R.string.setting_video_group_header));

                if (params.get(SettingKeys.VIDEO_PROFILES) != null) {
                    SettingsChildMenuVideoProfile videoProfile = new SettingsChildMenuVideoProfile(context,
                            params.get(SettingKeys.VIDEO_PROFILES), R.string.setting_videoprofile_header, R.string.setting_videoprofile_description);
                    videoProfile.SetUiItemClickListner(click);
                    videoGroup.addView(videoProfile);

                    SettingsChildMenuTimeLapseFrames timeLapseFrames = new SettingsChildMenuTimeLapseFrames(context);
                    timeLapseFrames.setVisibility(View.VISIBLE);
                    videoGroup.addView(timeLapseFrames);


                    SettingsChildMenu_VideoProfEditor videoProfileEditor = new SettingsChildMenu_VideoProfEditor(context, R.string.setting_videoprofileeditor_header, R.string.setting_videoprofileeditor_description);
                    videoGroup.addView(videoProfileEditor);
                }
                if (params.get(SettingKeys.VIDEO_HDR) != null) {
                    SettingsChildMenuVideoHDR videoHDR = new SettingsChildMenuVideoHDR(context, params.get(SettingKeys.VIDEO_HDR), R.string.setting_videohdr_header, R.string.setting_videohdr_description);
                    videoHDR.SetCameraInterface(cameraUiWrapper);
                    videoHDR.SetUiItemClickListner(click);
                    videoGroup.addView(videoHDR);
                }

                if (params.get(SettingKeys.VIDEO_STABILIZATION) != null) {
                    SettingsChildMenu videoStabilization = new SettingsChildMenu(context, params.get(SettingKeys.VIDEO_STABILIZATION), R.string.setting_vs_header, R.string.setting_vs_description);
                    videoStabilization.SetUiItemClickListner(click);
                    videoGroup.addView(videoStabilization);
                }
                if (params.get(SettingKeys.VIDEO_AUDIO_SOURCE) != null) {
                    SettingsChildMenu videoaudio = new SettingsChildMenu(context, params.get(SettingKeys.VIDEO_AUDIO_SOURCE), R.string.setting_videoaudiosource_header, R.string.setting_videoaudiosource_description);
                    videoaudio.SetUiItemClickListner(click);
                    videoGroup.addView(videoaudio);
                }

                if (videoGroup.childSize() > 0)
                    settingsChildHolder.addView(videoGroup);

        /*
            PictureGroup
         */
                GroupChild picGroup = new GroupChild(context, context.getResources().getString(R.string.setting_picture_group_header));

                if (params.get(SettingKeys.PICTURE_SIZE) != null) {
                    SettingsChildMenu pictureSize = new SettingsChildMenu(context, params.get(SettingKeys.PICTURE_SIZE), R.string.setting_picturesize_header, R.string.setting_picturesize_description);
                    pictureSize.SetUiItemClickListner(click);
                    picGroup.addView(pictureSize);
                }

                if (params.get(SettingKeys.YUV_SIZE) != null) {
                    SettingsChildMenu pictureSize = new SettingsChildMenu(context, params.get(SettingKeys.YUV_SIZE), R.string.setting_yuvsize_header, R.string.setting_yuvsize_description);
                    pictureSize.SetUiItemClickListner(click);
                    picGroup.addView(pictureSize);
                }

                if (params.get(SettingKeys.SECONDARY_SENSOR_SIZE) != null) {
                    SettingsChildMenu pictureSize = new SettingsChildMenu(context, params.get(SettingKeys.SECONDARY_SENSOR_SIZE), R.string.setting_secondarypicturesize_header, R.string.setting_secondarypicturesize_description);
                    pictureSize.SetUiItemClickListner(click);
                    picGroup.addView(pictureSize);
                }

                if (params.get(SettingKeys.JPEG_QUALITY) != null) {
                    SettingsChildMenu jpegQuality = new SettingsChildMenu(context, params.get(SettingKeys.JPEG_QUALITY), R.string.setting_jpegquality_header, R.string.setting_jpegquality_description);
                    jpegQuality.SetUiItemClickListner(click);
                    picGroup.addView(jpegQuality);
                }


                if (params.get(SettingKeys.MFNR) != null) {
                    SettingsChild_BooleanSetting mfnr = new SettingsChild_BooleanSetting(context, (BooleanSettingModeInterface) params.get(SettingKeys.MFNR), R.string.setting_mfnr_header,R.string.setting_mfnr_description);
                    picGroup.addView(mfnr);
                }

                if (params.get(SettingKeys.XIAOMI_MFNR) != null) {
                    SettingsChild_BooleanSetting mfnr = new SettingsChild_BooleanSetting(context, (BooleanSettingModeInterface) params.get(SettingKeys.XIAOMI_MFNR), R.string.setting_xiaomimfnr_header,R.string.setting_mfnr_description);
                    picGroup.addView(mfnr);
                }
                settingsChildHolder.addView(picGroup);

                GroupChild intervalGroup = new GroupChild(context, context.getResources().getString(R.string.setting_Automation));

                SettingsChildMenu menuInterval = new SettingsChildMenu(context, params.get(SettingKeys.INTERVAL_SHUTTER_SLEEP), R.string.setting_interval_header, R.string.setting_interval_texter);
                menuInterval.SetUiItemClickListner(click);
                intervalGroup.addView(menuInterval);

                SettingsChildMenu menuIntervalDuration = new SettingsChildMenu(context, params.get(SettingKeys.INTERVAL_DURATION), R.string.setting_interval_duration_header, R.string.setting_interval_duration_text);
                menuIntervalDuration.SetUiItemClickListner(click);
                intervalGroup.addView(menuIntervalDuration);

                settingsChildHolder.addView(intervalGroup);

                GroupChild dngGroup = new GroupChild(context, context.getResources().getString(R.string.setting_raw_group_header));

                if (params.get(SettingKeys.OPCODE) != null) {
                    SettingsChildMenu opcode = new SettingsChildMenu(context, params.get(SettingKeys.OPCODE), R.string.setting_opcode_header, R.string.setting_opcode_description);
                    opcode.SetUiItemClickListner(click);
                    dngGroup.addView(opcode);
                }

                if (params.get(SettingKeys.BAYERFORMAT) != null && params.get(SettingKeys.BAYERFORMAT).getViewState() == AbstractParameter.ViewState.Visible && apS.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).isSupported()) {
                    SettingsChildMenu bayerFormatItem = new SettingsChildMenu(context, params.get(SettingKeys.BAYERFORMAT), R.string.setting_bayerformat_header, R.string.setting_bayerformat_description);
                    bayerFormatItem.SetUiItemClickListner(click);
                    dngGroup.addView(bayerFormatItem);
                }
                if (params.get(SettingKeys.MATRIX_SET) != null && params.get(SettingKeys.MATRIX_SET).getViewState() == AbstractParameter.ViewState.Visible) {
                    SettingsChildMenu matrixChooser = new SettingsChildMenu(context, params.get(SettingKeys.MATRIX_SET), R.string.setting_matrixchooser_header, R.string.setting_matrixchooser_description);
                    matrixChooser.SetUiItemClickListner(click);
                    dngGroup.addView(matrixChooser);
                }
                if (params.get(SettingKeys.TONEMAP_SET) != null && params.get(SettingKeys.TONEMAP_SET).getViewState() == AbstractParameter.ViewState.Visible) {
                    SettingsChildMenu matrixChooser = new SettingsChildMenu(context, params.get(SettingKeys.TONEMAP_SET), R.string.setting_tonemapchooser_header, R.string.setting_tonemapchooser_description);
                    matrixChooser.SetUiItemClickListner(click);
                    dngGroup.addView(matrixChooser);
                }
                if (cameraUiWrapper instanceof Camera2) {
                    SettingsChild_BooleanSetting rawToDng = new SettingsChild_BooleanSetting(context, apS.get(SettingKeys.FORCE_RAW_TO_DNG),R.string.setting_forcerawtodng_header, R.string.setting_forcerawtodng_description);
                    dngGroup.addView(rawToDng);

                    SettingsChild_BooleanSetting useCustomMatrix = new SettingsChild_BooleanSetting(context, apS.get(SettingKeys.USE_CUSTOM_MATRIX_ON_CAMERA_2), R.string.setting_usecustomdngprofile_header, R.string.setting_usecustomdngprofile_description);
                    dngGroup.addView(useCustomMatrix);
                    if (params.get(SettingKeys.RAW_SIZE) != null && params.get(SettingKeys.RAW_SIZE).getViewState() == AbstractParameter.ViewState.Visible) {
                        SettingsChildMenu rawsize = new SettingsChildMenu(context, params.get(SettingKeys.RAW_SIZE), R.string.setting_rawsize_header, R.string.setting_rawsize_description);
                        rawsize.SetUiItemClickListner(click);
                        dngGroup.addView(rawsize);
                    }
                }
                if (dngGroup.childSize() > 0)
                    settingsChildHolder.addView(dngGroup);


            }
         /*
            Gobal settings
         */
        }

        GroupChild globalSettingGroup = new GroupChild(context,context.getResources().getString(R.string.setting_freedcam_));


        SettingsChildMenu api = new SettingsChildMenu(context,R.string.setting_api_header, R.string.setting_api_description);
        api.SetParameter(apiParameter);
        api.SetUiItemClickListner(click);
        globalSettingGroup.addView(api);



        if (cameraUiWrapper != null && cameraUiWrapper.getParameterHandler() != null) {

            SettingsChildMenu sdSave = new SettingsChildMenu(context, cameraUiWrapper.getParameterHandler().get(SettingKeys.SD_SAVE_LOCATION), R.string.setting_sdcard_header, R.string.setting_sdcard_description);
            sdSave.SetUiItemClickListner(click);
            ((SDModeParameter)cameraUiWrapper.getParameterHandler().get(SettingKeys.SD_SAVE_LOCATION)).setContext(context);
            globalSettingGroup.addView(sdSave);

            SettingsChild_BooleanSetting menuItemGPS = new SettingsChild_BooleanSetting(context, (BooleanSettingModeInterface) cameraUiWrapper.getParameterHandler().get(SettingKeys.LOCATION_MODE),R.string.setting_location_header, R.string.setting_location_description );
            globalSettingGroup.addView(menuItemGPS);

            SettingsChildMenu guide = new SettingsChildMenu(context,cameraUiWrapper.getParameterHandler().get(SettingKeys.GUIDE_LIST), R.string.setting_guide_header, R.string.setting_guide_description);
            guide.SetUiItemClickListner(click);
            globalSettingGroup.addView(guide);

            SettingsChildMenu horizont = new SettingsChildMenu(context, R.string.setting_horizont_header, R.string.setting_horizont_description);
            horizont.SetParameter(cameraUiWrapper.getParameterHandler().get(SettingKeys.HORIZONT_LVL));
            horizont.SetUiItemClickListner(click);
            globalSettingGroup.addView(horizont);

            SettingsChildMenu nightoverlay = new SettingsChildMenu(context, R.string.setting_nightoverlay_header, R.string.setting_nightoverlay_description);
            nightoverlay.SetUiItemClickListner(click);
            nightoverlay.SetParameter(cameraUiWrapper.getParameterHandler().get(SettingKeys.NIGHT_OVERLAY));
            globalSettingGroup.addView(nightoverlay);

            SettingsChild_BooleanSetting booleanSetting = new SettingsChild_BooleanSetting(context,apS.getGlobal(SettingKeys.TOUCH_TO_CAPTURE),R.string.setting_touchtocapture_header, R.string.setting_touchtocapture_description);
            globalSettingGroup.addView(booleanSetting);

            SettingsChild_BooleanSetting playshutter = new SettingsChild_BooleanSetting(context,apS.getGlobal(SettingKeys.PLAY_SHUTTER_SOUND),R.string.setting_playshuttersound_header, R.string.setting_playshuttersound_description);
            globalSettingGroup.addView(playshutter);


            SettingsChildMenuSaveCamParams saveCamParams = new SettingsChildMenuSaveCamParams(context,R.string.setting_savecamparams_header,R.string.setting_savecamparams_description,cameraUiWrapper);
            saveCamParams.setCameraUiWrapper(cameraUiWrapper);
            globalSettingGroup.addView(saveCamParams);

            if (cameraUiWrapper.getParameterHandler().get(SettingKeys.OPEN_CAMERA_1_LEGACY) != null)
            {
                SettingsChild_BooleanSetting ers = new SettingsChild_BooleanSetting(context,(BooleanSettingModeInterface) cameraUiWrapper.getParameterHandler().get(SettingKeys.OPEN_CAMERA_1_LEGACY),R.string.setting_opencameralegacy_header, R.string.setting_opencameralegacy_description);
                globalSettingGroup.addView(ers);
            }

            if (cameraUiWrapper.getParameterHandler().get(SettingKeys.THEME) != null) {
                SettingsChildMenu theme = new SettingsChildMenu(context, R.string.setting_theme_header, R.string.setting_theme_description);
                theme.SetParameter(cameraUiWrapper.getParameterHandler().get(SettingKeys.THEME));
                theme.SetUiItemClickListner(click);
                globalSettingGroup.addView(theme);
            }


        }

        GroupChild etc = new GroupChild(context,"Etc");




        SettingsChildFeatureDetect fd = new SettingsChildFeatureDetect(context,R.string.setting_featuredetector_header,R.string.setting_featuredetector_description);
        etc.addView(fd);

        if (ReleaseChecker.isGithubRelease) {
            SettingsChild_BooleanSetting booleanSetting = new SettingsChild_BooleanSetting(context, apS.getGlobal(SettingKeys.CHECKFORUPDATES), R.string.setting_checkforupdate_header, R.string.setting_checkforupdate_description);
            etc.addView(booleanSetting);
        }

        SettingsChild_BooleanSetting disablecameraui = new SettingsChild_BooleanSetting(context, apS.getGlobal(SettingKeys.HIDE_CAMERA_UI), R.string.setting_disablecameraui_header, R.string.setting_disablecameraui_description);
        etc.addView(disablecameraui);

        if (cameraUiWrapper instanceof Camera2)
        {
            SettingsChildDumpCamera2VendorKeys dumpCamera2VendorKeys = new SettingsChildDumpCamera2VendorKeys(context,R.string.setting_dump_vendor_keys_header,R.string.setting_dump_vendor_keys_description,(Camera2) cameraUiWrapper);
            etc.addView(dumpCamera2VendorKeys);
        }
        if (etc.childSize() > 0)
            globalSettingGroup.addView(etc);

        settingsChildHolder.addView(globalSettingGroup);
    }


    public void fillRightSettingsMenu(CameraWrapperInterface cameraUiWrapper, Context context, LinearLayout settingchildholder, SettingsChildAbstract.SettingsChildClick click)
    {
        if (cameraUiWrapper != null) {
            SettingsManager apS = FreedApplication.settingsManager();
            ParameterHandler params = cameraUiWrapper.getParameterHandler();

            GroupChild settingsgroup = new GroupChild(context,  context.getResources().getString(R.string.setting_camera_));
            if (params == null)
            {
                Log.d(TAG, "ParameterHandler is null");
                return;
            }

            GroupChild previewgroup = new GroupChild(context,context.getResources().getString(R.string.setting_preview_));
            if (params.get(SettingKeys.PREVIEW_POST_PROCESSING_MODE) != null) {
                SettingsChildMenu ers = new SettingsChildMenu(context, params.get(SettingKeys.PREVIEW_POST_PROCESSING_MODE), R.string.setting_enablerenderscript_header, R.string.setting_enablerenderscript_description);
                ers.SetUiItemClickListner(click);
                previewgroup.addView(ers);
            }
            if (params.get(SettingKeys.FOCUSPEAK_COLOR) != null) {
                SettingsChildMenu fpc = new SettingsChildMenu(context, params.get(SettingKeys.FOCUSPEAK_COLOR), R.string.setting_focuspeakcolor_header, R.string.setting_focuspeakcolor_description);
                fpc.SetUiItemClickListner(click);
                previewgroup.addView(fpc);
            }
            if (cameraUiWrapper.getParameterHandler().get(SettingKeys.ORIENTATION_HACK) != null) {
                SettingsChildMenu orientationHack = new SettingsChildMenu(context, cameraUiWrapper.getParameterHandler().get(SettingKeys.ORIENTATION_HACK), R.string.setting_orientation_header, R.string.setting_orientation_description);
                orientationHack.SetUiItemClickListner(click);
                previewgroup.addView(orientationHack);
            }
            SettingsChild_SwitchAspectRatio aspectRatio = new SettingsChild_SwitchAspectRatio(context,apS.get(SettingKeys.SWITCH_ASPECT_RATIO),R.string.setting_switch_aspect_header, R.string.setting_switch_aspect_text);
            previewgroup.addView(aspectRatio);

            if (params.get(SettingKeys.PREVIEW_POST_PROCESSING_MODE) != null)
            {
                if (params.get(SettingKeys.PREVIEW_POST_PROCESSING_MODE).getStringValue() != null) {
                    if (params.get(SettingKeys.PREVIEW_POST_PROCESSING_MODE).getStringValue().equals(PreviewPostProcessingModes.OpenGL.name())) {
                        GroupChild aegroup = new GroupChild(context, "Custom AE");
                        SettingsChild_FreedAe freedae = new SettingsChild_FreedAe(context, apS.getGlobal(SettingKeys.USE_FREEDCAM_AE), R.string.setting_usefreedae_header, R.string.setting_use_freedae_text);
                        aegroup.addView(freedae);

                        SettingsChildMenu maxiso = new SettingsChildMenu(context, new SettingModeParamter(SettingKeys.MAX_ISO), R.string.setting_maxiso_header, R.string.setting_maxiso_text);
                        maxiso.SetUiItemClickListner(click);
                        aegroup.addView(maxiso);

                        SettingsChildMenu miniso = new SettingsChildMenu(context, new SettingModeParamter(SettingKeys.MIN_ISO), R.string.setting_miniso_header, R.string.setting_miniso_text);
                        miniso.SetUiItemClickListner(click);
                        aegroup.addView(miniso);

                        SettingsChildMenu minexpotime = new SettingsChildMenu(context, new SettingModeParamter(SettingKeys.MIN_EXPOSURE), R.string.setting_minexpotime_header, R.string.setting_minexpotime_text);
                        minexpotime.SetUiItemClickListner(click);
                        aegroup.addView(minexpotime);

                        SettingsChildMenu maxexpotime = new SettingsChildMenu(context, new SettingModeParamter(SettingKeys.MAX_EXPOSURE), R.string.setting_maxexpotime_header, R.string.setting_maxexpotime_text);
                        maxexpotime.SetUiItemClickListner(click);
                        aegroup.addView(maxexpotime);
                        previewgroup.addView(aegroup);
                    }
                }
            }

            settingchildholder.addView(previewgroup);



            if (params.get(SettingKeys.SCENE_MODE) != null) {
                SettingsChildMenu scene = new SettingsChildMenu(context, params.get(SettingKeys.SCENE_MODE), R.string.setting_scene_header, R.string.setting_scene_description);
                scene.SetUiItemClickListner(click);
                settingsgroup.addView(scene);
            }

            if (params.get(SettingKeys.COLOR_MODE) != null) {
                SettingsChildMenu color = new SettingsChildMenu(context, params.get(SettingKeys.COLOR_MODE), R.string.setting_color_header, R.string.setting_color_description);
                color.SetUiItemClickListner(click);
                settingsgroup.addView(color);
            }

            if (params.get(SettingKeys.COLOR_CORRECTION_MODE) != null) {
                SettingsChildMenu cct = new SettingsChildMenu(context, params.get(SettingKeys.COLOR_CORRECTION_MODE), R.string.setting_colorcorrection_header, R.string.setting_colorcorrection_description);
                cct.SetUiItemClickListner(click);
                settingsgroup.addView(cct);
            }
            if (params.get(SettingKeys.TONE_MAP_MODE) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.TONE_MAP_MODE), R.string.setting_tonemap_header, R.string.setting_tonemap_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }

            if (params.get(SettingKeys.CONTROL_MODE) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.CONTROL_MODE), R.string.setting_controlmode_header, R.string.setting_controlmode_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.RED_EYE) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.RED_EYE), R.string.setting_redeye_header, R.string.setting_redeye_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.ANTI_BANDING_MODE) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.ANTI_BANDING_MODE), R.string.setting_antiflicker_header, R.string.setting_antiflicker_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.IMAGE_POST_PROCESSING) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.IMAGE_POST_PROCESSING), R.string.setting_ipp_header, R.string.setting_ipp_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }

            if (params.get(SettingKeys.LENS_SHADE) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.LENS_SHADE), R.string.setting_lensshade_header, R.string.setting_lensshade_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.SCENE_DETECT) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.SCENE_DETECT), R.string.setting_scenedec_header, R.string.setting_scenedec_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.DENOISE) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.DENOISE), R.string.setting_waveletdenoise_header, R.string.setting_waveletdenoise_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
/////////////////////////////////////////////////
            if (params.get(SettingKeys.TNR) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.TNR), R.string.setting_temporaldenoise_header, R.string.setting_temporaldenoise_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.TNR_V) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.TNR_V), R.string.setting_temporaldenoiseV_header, R.string.setting_temporaldenoiseV_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.PDAF) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.PDAF), R.string.setting_pdaf_header, R.string.setting_pdaf_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.TRUE_POTRAIT) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.TRUE_POTRAIT), R.string.setting_truepotrait_header, R.string.setting_truepotrait_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.RDI) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.RDI), R.string.setting_rdi_header, R.string.setting_rdi_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.CHROMA_FLASH) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.CHROMA_FLASH), R.string.setting_chroma_header, R.string.setting_chroma_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.OPTI_ZOOM) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.OPTI_ZOOM), R.string.setting_optizoom_header, R.string.setting_optizoom_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.RE_FOCUS) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.RE_FOCUS), R.string.setting_refocus_header, R.string.setting_refous_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }

            if (params.get(SettingKeys.SEE_MORE) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.SEE_MORE), R.string.setting_seemore_header, R.string.setting_seemore_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            ///////////////////////////////////////////////

            if (params.get(SettingKeys.LENS_FILTER) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.LENS_FILTER), R.string.setting_lensfilter_header, R.string.setting_lensfilter_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.DIGITAL_IMAGE_STABILIZATION) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.DIGITAL_IMAGE_STABILIZATION), R.string.setting_dis_header, R.string.setting_dis_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.MEMORY_COLOR_ENHANCEMENT) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.MEMORY_COLOR_ENHANCEMENT), R.string.setting_mce_header, R.string.setting_mce_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.ZSL) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.ZSL), R.string.setting_zsl_header, R.string.setting_zsl_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.NON_ZSL_MANUAL_MODE) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.NON_ZSL_MANUAL_MODE), R.string.setting_nonzsl_header, R.string.setting_nonzsl_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.CDS_MODE) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.CDS_MODE), R.string.setting_cds_header, R.string.setting_cds_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.EDGE_MODE) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.EDGE_MODE), R.string.setting_edge_header, R.string.setting_edge_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.HOT_PIXEL_MODE) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.HOT_PIXEL_MODE), R.string.setting_hotpixel_header, R.string.setting_hotpixel_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.DISTORTION_CORRECTION_MODE) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.DISTORTION_CORRECTION_MODE), R.string.setting_distortion_header, R.string.setting_distortion_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }

            if (params.get(SettingKeys.FACE_DETECTOR_MODE) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.FACE_DETECTOR_MODE), R.string.setting_facemode_header, R.string.setting_facemode_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }


            if (params.get(SettingKeys.OIS_MODE) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.OIS_MODE), R.string.setting_ois_header, R.string.setting_ois_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.DUAL_PRIMARY_CAMERA_MODE) != null && !apS.getIsFrontCamera()) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.DUAL_PRIMARY_CAMERA_MODE), R.string.setting_dualprimarycamera_header, R.string.setting_dualprimarycamera_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.AE_TARGET_FPS) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.AE_TARGET_FPS), R.string.setting_aetargetfps_header, R.string.setting_aetargetfps_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }

            GroupChild mf = new GroupChild(context,"Manual Focus");
            if (apS.get(SettingKeys.ZOOM_ON_MANUALFOCUS).isSupported()) {
                SettingsChild_BooleanSetting ton = new SettingsChild_BooleanSetting(context,apS.get(SettingKeys.ZOOM_ON_MANUALFOCUS),R.string.setting_zoom_on_mf_header, R.string.setting_zoom_on_mf_description);
                mf.addView(ton);
            }

            if (apS.get(SettingKeys.ZOOM_ON_MANUALFOCUS_ZOOMFACTOR).isSupported()) {
                SettingsChildMenu ton = new SettingsChildMenu(context, new SettingModeParamter(SettingKeys.ZOOM_ON_MANUALFOCUS_ZOOMFACTOR),R.string.setting_zoom_on_mf_factor_header, R.string.setting_zoom_on_mf_factor_description);
                ton.SetUiItemClickListner(click);
                mf.addView(ton);
            }

            if (apS.get(SettingKeys.ZOOM_ON_MANUALFOCUS_ZOOMDURATION).isSupported()) {
                SettingsChildMenu ton = new SettingsChildMenu(context, new SettingModeParamter(SettingKeys.ZOOM_ON_MANUALFOCUS_ZOOMDURATION),R.string.setting_zoom_on_mf_duration_header, R.string.setting_zoom_on_mf_duration_description);
                ton.SetUiItemClickListner(click);
                mf.addView(ton);
            }
            if (mf.childSize() > 0)
                settingsgroup.addView(mf);
            settingchildholder.addView(settingsgroup);
        }
    }
}
