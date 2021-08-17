package freed.cam.ui.themesample.settings;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.troop.freedcam.R;

import javax.inject.Inject;

import freed.ActivityInterface;
import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterHandler;
import freed.cam.apis.basecamera.parameters.modes.ApiParameter;
import freed.cam.apis.basecamera.parameters.modes.SettingModeParamter;
import freed.cam.apis.camera2.Camera2;
import freed.cam.apis.sonyremote.SonyRemoteCamera;
import freed.cam.previewpostprocessing.PreviewPostProcessingModes;
import freed.cam.ui.themesample.SettingsChildAbstract;
import freed.cam.ui.themesample.settings.childs.GroupChild;
import freed.cam.ui.themesample.settings.childs.SettingsChildFeatureDetect;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenu;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuSDSave;
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

    private ApiParameter apiParameter;

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

                if (params.get(SettingKeys.VideoProfiles) != null) {
                    SettingsChildMenuVideoProfile videoProfile = new SettingsChildMenuVideoProfile(context,
                            params.get(SettingKeys.VideoProfiles), R.string.setting_videoprofile_header, R.string.setting_videoprofile_description);
                    videoProfile.SetUiItemClickListner(click);
                    videoGroup.addView(videoProfile);

                    SettingsChildMenuTimeLapseFrames timeLapseFrames = new SettingsChildMenuTimeLapseFrames(context);
                    timeLapseFrames.setVisibility(View.VISIBLE);
                    videoGroup.addView(timeLapseFrames);


                    SettingsChildMenu_VideoProfEditor videoProfileEditor = new SettingsChildMenu_VideoProfEditor(context, R.string.setting_videoprofileeditor_header, R.string.setting_videoprofileeditor_description);
                    videoGroup.addView(videoProfileEditor);
                }
                if (params.get(SettingKeys.VideoHDR) != null) {
                    SettingsChildMenuVideoHDR videoHDR = new SettingsChildMenuVideoHDR(context, params.get(SettingKeys.VideoHDR), R.string.setting_videohdr_header, R.string.setting_videohdr_description);
                    videoHDR.SetCameraInterface(cameraUiWrapper);
                    videoHDR.SetUiItemClickListner(click);
                    videoGroup.addView(videoHDR);
                }

                if (params.get(SettingKeys.VideoSize) != null && (cameraUiWrapper instanceof SonyRemoteCamera)) {

                    SettingsChildMenu VideoSize = new SettingsChildMenu(context, params.get(SettingKeys.VideoSize), R.string.setting_videoprofile_header, R.string.setting_videoprofile_description);
                    VideoSize.SetUiItemClickListner(click);
                    videoGroup.addView(VideoSize);
                }

                if (params.get(SettingKeys.VideoStabilization) != null) {
                    SettingsChildMenu videoStabilization = new SettingsChildMenu(context, params.get(SettingKeys.VideoStabilization), R.string.setting_vs_header, R.string.setting_vs_description);
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

                if (params.get(SettingKeys.PictureSize) != null) {
                    SettingsChildMenu pictureSize = new SettingsChildMenu(context, params.get(SettingKeys.PictureSize), R.string.setting_picturesize_header, R.string.setting_picturesize_description);
                    pictureSize.SetUiItemClickListner(click);
                    picGroup.addView(pictureSize);
                }

                if (params.get(SettingKeys.YuvSize) != null) {
                    SettingsChildMenu pictureSize = new SettingsChildMenu(context, params.get(SettingKeys.YuvSize), R.string.setting_yuvsize_header, R.string.setting_yuvsize_description);
                    pictureSize.SetUiItemClickListner(click);
                    picGroup.addView(pictureSize);
                }

                if (params.get(SettingKeys.secondarySensorSize) != null) {
                    SettingsChildMenu pictureSize = new SettingsChildMenu(context, params.get(SettingKeys.secondarySensorSize), R.string.setting_secondarypicturesize_header, R.string.setting_secondarypicturesize_description);
                    pictureSize.SetUiItemClickListner(click);
                    picGroup.addView(pictureSize);
                }

                if (params.get(SettingKeys.JpegQuality) != null) {
                    SettingsChildMenu jpegQuality = new SettingsChildMenu(context, params.get(SettingKeys.JpegQuality), R.string.setting_jpegquality_header, R.string.setting_jpegquality_description);
                    jpegQuality.SetUiItemClickListner(click);
                    picGroup.addView(jpegQuality);
                }


                if (params.get(SettingKeys.MFNR) != null) {
                    SettingsChildMenu mfnr = new SettingsChildMenu(context, params.get(SettingKeys.MFNR), R.string.setting_mfnr_header,R.string.setting_mfnr_description);
                    mfnr.SetUiItemClickListner(click);
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
                    SettingsChild_BooleanSetting rawToDng = new SettingsChild_BooleanSetting(context, apS.get(SettingKeys.forceRawToDng),R.string.setting_forcerawtodng_header, R.string.setting_forcerawtodng_description);
                    dngGroup.addView(rawToDng);

                    SettingsChild_BooleanSetting useCustomMatrix = new SettingsChild_BooleanSetting(context, apS.get(SettingKeys.useCustomMatrixOnCamera2), R.string.setting_usecustomdngprofile_header, R.string.setting_usecustomdngprofile_description);
                    dngGroup.addView(useCustomMatrix);
                    if (params.get(SettingKeys.RawSize) != null && params.get(SettingKeys.RawSize).getViewState() == AbstractParameter.ViewState.Visible) {
                        SettingsChildMenu rawsize = new SettingsChildMenu(context, params.get(SettingKeys.RawSize), R.string.setting_rawsize_header, R.string.setting_rawsize_description);
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

            SettingsChildMenuSDSave sdSave = new SettingsChildMenuSDSave(context, R.string.setting_sdcard_header, R.string.setting_sdcard_description);
            sdSave.SetCameraUiWrapper(cameraUiWrapper);
            sdSave.SetUiItemClickListner(click);
            globalSettingGroup.addView(sdSave);

            SettingsChildMenu menuItemGPS = new SettingsChildMenu(context,cameraUiWrapper.getParameterHandler().get(SettingKeys.LOCATION_MODE),R.string.setting_location_header, R.string.setting_location_description );
            menuItemGPS.SetUiItemClickListner(click);
            globalSettingGroup.addView(menuItemGPS);

            SettingsChildMenu guide = new SettingsChildMenu(context,cameraUiWrapper.getParameterHandler().get(SettingKeys.GuideList), R.string.setting_guide_header, R.string.setting_guide_description);
            guide.SetUiItemClickListner(click);
            globalSettingGroup.addView(guide);

            SettingsChildMenu horizont = new SettingsChildMenu(context, R.string.setting_horizont_header, R.string.setting_horizont_description);
            horizont.SetParameter(cameraUiWrapper.getParameterHandler().get(SettingKeys.HorizontLvl));
            horizont.SetUiItemClickListner(click);
            globalSettingGroup.addView(horizont);

            SettingsChildMenu nightoverlay = new SettingsChildMenu(context, R.string.setting_nightoverlay_header, R.string.setting_nightoverlay_description);
            nightoverlay.SetUiItemClickListner(click);
            nightoverlay.SetParameter(cameraUiWrapper.getParameterHandler().get(SettingKeys.NightOverlay));
            globalSettingGroup.addView(nightoverlay);

            SettingsChild_BooleanSetting booleanSetting = new SettingsChild_BooleanSetting(context,apS.getGlobal(SettingKeys.TouchToCapture),R.string.setting_touchtocapture_header, R.string.setting_touchtocapture_description);
            globalSettingGroup.addView(booleanSetting);

            SettingsChild_BooleanSetting playshutter = new SettingsChild_BooleanSetting(context,apS.getGlobal(SettingKeys.PLAY_SHUTTER_SOUND),R.string.setting_playshuttersound_header, R.string.setting_playshuttersound_description);
            globalSettingGroup.addView(playshutter);


            SettingsChildMenuSaveCamParams saveCamParams = new SettingsChildMenuSaveCamParams(context,R.string.setting_savecamparams_header,R.string.setting_savecamparams_description,cameraUiWrapper);
            saveCamParams.setCameraUiWrapper(cameraUiWrapper);
            globalSettingGroup.addView(saveCamParams);

            if (cameraUiWrapper.getParameterHandler().get(SettingKeys.openCamera1Legacy) != null)
            {
                SettingsChild_BooleanSetting ers = new SettingsChild_BooleanSetting(context,(BooleanSettingModeInterface) cameraUiWrapper.getParameterHandler().get(SettingKeys.openCamera1Legacy),R.string.setting_opencameralegacy_header, R.string.setting_opencameralegacy_description);
                globalSettingGroup.addView(ers);
            }

            if (!(cameraUiWrapper instanceof SonyRemoteCamera))
            {
                SettingsChildFeatureDetect fd = new SettingsChildFeatureDetect(context,R.string.setting_featuredetector_header,R.string.setting_featuredetector_description);
                globalSettingGroup.addView(fd);
            }
        }

        if (ReleaseChecker.isGithubRelease) {
            SettingsChild_BooleanSetting booleanSetting = new SettingsChild_BooleanSetting(context, apS.getGlobal(SettingKeys.CHECKFORUPDATES), R.string.setting_checkforupdate_header, R.string.setting_checkforupdate_description);
            globalSettingGroup.addView(booleanSetting);
        }

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
            if (cameraUiWrapper.getParameterHandler().get(SettingKeys.orientationHack) != null) {
                SettingsChildMenu orientationHack = new SettingsChildMenu(context, cameraUiWrapper.getParameterHandler().get(SettingKeys.orientationHack), R.string.setting_orientation_header, R.string.setting_orientation_description);
                orientationHack.SetUiItemClickListner(click);
                previewgroup.addView(orientationHack);
            }
            SettingsChild_SwitchAspectRatio aspectRatio = new SettingsChild_SwitchAspectRatio(context,apS.get(SettingKeys.SWITCH_ASPECT_RATIO),R.string.setting_switch_aspect_header, R.string.setting_switch_aspect_text);
            previewgroup.addView(aspectRatio);

            if (params.get(SettingKeys.PREVIEW_POST_PROCESSING_MODE) != null && params.get(SettingKeys.PREVIEW_POST_PROCESSING_MODE).getStringValue().equals(PreviewPostProcessingModes.OpenGL.name()))
            {
                SettingsChild_FreedAe freedae = new SettingsChild_FreedAe(context,apS.getGlobal(SettingKeys.USE_FREEDCAM_AE),R.string.setting_usefreedae_header, R.string.setting_use_freedae_text);
                previewgroup.addView(freedae);

                SettingsChildMenu maxiso = new SettingsChildMenu(context,new SettingModeParamter(SettingKeys.MAX_ISO),R.string.setting_maxiso_header, R.string.setting_maxiso_text);
                maxiso.SetUiItemClickListner(click);
                previewgroup.addView(maxiso);

                SettingsChildMenu miniso = new SettingsChildMenu(context,new SettingModeParamter(SettingKeys.MIN_ISO),R.string.setting_miniso_header, R.string.setting_miniso_text);
                miniso.SetUiItemClickListner(click);
                previewgroup.addView(miniso);

                SettingsChildMenu minexpotime = new SettingsChildMenu(context,new SettingModeParamter(SettingKeys.MIN_EXPOSURE),R.string.setting_minexpotime_header, R.string.setting_minexpotime_text);
                minexpotime.SetUiItemClickListner(click);
                previewgroup.addView(minexpotime);

                SettingsChildMenu maxexpotime = new SettingsChildMenu(context,new SettingModeParamter(SettingKeys.MAX_EXPOSURE),R.string.setting_maxexpotime_header, R.string.setting_maxexpotime_text);
                maxexpotime.SetUiItemClickListner(click);
                previewgroup.addView(maxexpotime);
            }

            settingchildholder.addView(previewgroup);



            if (params.get(SettingKeys.SceneMode) != null) {
                SettingsChildMenu scene = new SettingsChildMenu(context, params.get(SettingKeys.SceneMode), R.string.setting_scene_header, R.string.setting_scene_description);
                scene.SetUiItemClickListner(click);
                settingsgroup.addView(scene);
            }

            if (params.get(SettingKeys.ColorMode) != null) {
                SettingsChildMenu color = new SettingsChildMenu(context, params.get(SettingKeys.ColorMode), R.string.setting_color_header, R.string.setting_color_description);
                color.SetUiItemClickListner(click);
                settingsgroup.addView(color);
            }

            if (params.get(SettingKeys.COLOR_CORRECTION_MODE) != null) {
                SettingsChildMenu cct = new SettingsChildMenu(context, params.get(SettingKeys.COLOR_CORRECTION_MODE), R.string.setting_colorcorrection_header, R.string.setting_colorcorrection_description);
                cct.SetUiItemClickListner(click);
                settingsgroup.addView(cct);
            }
            if (params.get(SettingKeys.ObjectTracking) != null) {
                SettingsChildMenu ot = new SettingsChildMenu(context, params.get(SettingKeys.ObjectTracking), R.string.setting_objecttrack_header, R.string.setting_objecttrack_description);
                ot.SetUiItemClickListner(click);
                settingsgroup.addView(ot);
            }
            if (params.get(SettingKeys.TONE_MAP_MODE) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.TONE_MAP_MODE), R.string.setting_tonemap_header, R.string.setting_tonemap_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.PostViewSize) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.PostViewSize), R.string.setting_postview_header, R.string.setting_postview_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.CONTROL_MODE) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.CONTROL_MODE), R.string.setting_controlmode_header, R.string.setting_controlmode_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.RedEye) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.RedEye), R.string.setting_redeye_header, R.string.setting_redeye_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.AntiBandingMode) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.AntiBandingMode), R.string.setting_antiflicker_header, R.string.setting_antiflicker_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.ImagePostProcessing) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.ImagePostProcessing), R.string.setting_ipp_header, R.string.setting_ipp_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }

            if (params.get(SettingKeys.LensShade) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.LensShade), R.string.setting_lensshade_header, R.string.setting_lensshade_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.SceneDetect) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.SceneDetect), R.string.setting_scenedec_header, R.string.setting_scenedec_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.Denoise) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.Denoise), R.string.setting_waveletdenoise_header, R.string.setting_waveletdenoise_description);
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
            if (params.get(SettingKeys.TruePotrait) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.TruePotrait), R.string.setting_truepotrait_header, R.string.setting_truepotrait_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.RDI) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.RDI), R.string.setting_rdi_header, R.string.setting_rdi_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.ChromaFlash) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.ChromaFlash), R.string.setting_chroma_header, R.string.setting_chroma_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.OptiZoom) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.OptiZoom), R.string.setting_optizoom_header, R.string.setting_optizoom_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.ReFocus) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.ReFocus), R.string.setting_refocus_header, R.string.setting_refous_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }

            if (params.get(SettingKeys.SeeMore) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.SeeMore), R.string.setting_seemore_header, R.string.setting_seemore_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            ///////////////////////////////////////////////

            if (params.get(SettingKeys.LensFilter) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.LensFilter), R.string.setting_lensfilter_header, R.string.setting_lensfilter_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.DigitalImageStabilization) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.DigitalImageStabilization), R.string.setting_dis_header, R.string.setting_dis_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.MemoryColorEnhancement) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.MemoryColorEnhancement), R.string.setting_mce_header, R.string.setting_mce_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.ZSL) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.ZSL), R.string.setting_zsl_header, R.string.setting_zsl_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.NonZslManualMode) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.NonZslManualMode), R.string.setting_nonzsl_header, R.string.setting_nonzsl_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.CDS_Mode) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.CDS_Mode), R.string.setting_cds_header, R.string.setting_cds_description);
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
            if (params.get(SettingKeys.ZoomSetting) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.ZoomSetting), R.string.setting_zoomsetting_header, R.string.setting_zoomsetting_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.SCALE_PREVIEW) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.SCALE_PREVIEW), R.string.setting_scalepreview_header, R.string.setting_scalepreview_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.dualPrimaryCameraMode) != null && !apS.getIsFrontCamera()) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.dualPrimaryCameraMode), R.string.setting_dualprimarycamera_header, R.string.setting_dualprimarycamera_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.Ae_TargetFPS) != null) {
                SettingsChildMenu ton = new SettingsChildMenu(context, params.get(SettingKeys.Ae_TargetFPS), R.string.setting_aetargetfps_header, R.string.setting_aetargetfps_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }

            if (apS.get(SettingKeys.ZOOM_ON_MANUALFOCUS).isSupported()) {
                SettingsChild_BooleanSetting ton = new SettingsChild_BooleanSetting(context,apS.get(SettingKeys.ZOOM_ON_MANUALFOCUS),R.string.setting_zoom_on_mf_header, R.string.setting_zoom_on_mf_description);
                settingsgroup.addView(ton);
            }

            if (apS.get(SettingKeys.ZOOM_ON_MANUALFOCUS_ZOOMFACTOR).isSupported()) {
                SettingsChildMenu ton = new SettingsChildMenu(context, new SettingModeParamter(SettingKeys.ZOOM_ON_MANUALFOCUS_ZOOMFACTOR),R.string.setting_zoom_on_mf_factor_header, R.string.setting_zoom_on_mf_factor_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }

            if (apS.get(SettingKeys.ZOOM_ON_MANUALFOCUS_ZOOMDURATION).isSupported()) {
                SettingsChildMenu ton = new SettingsChildMenu(context, new SettingModeParamter(SettingKeys.ZOOM_ON_MANUALFOCUS_ZOOMDURATION),R.string.setting_zoom_on_mf_duration_header, R.string.setting_zoom_on_mf_duration_description);
                ton.SetUiItemClickListner(click);
                settingsgroup.addView(ton);
            }
            settingchildholder.addView(settingsgroup);
        }
    }
}
