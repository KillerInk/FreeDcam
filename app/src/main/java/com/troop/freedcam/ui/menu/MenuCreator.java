package com.troop.freedcam.ui.menu;

import android.view.SurfaceView;

import com.troop.freedcam.R;

import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.MainActivity_v2;
import com.troop.freedcam.ui.TextureView.ExtendedSurfaceView;
import com.troop.freedcam.ui.menu.childs.SwitchApiExpandableChild;
import com.troop.freedcam.ui.menu.childs.ExpandableChild;
import com.troop.freedcam.ui.menu.childs.ExpandableChildNumber;
import com.troop.freedcam.ui.menu.childs.LongExposureChild;
import com.troop.freedcam.ui.menu.childs.PictureFormatExpandableChild;
import com.troop.freedcam.ui.menu.childs.PreviewExpandableChild;
import com.troop.freedcam.ui.menu.childs.SaveCamParasExpandableChild;
import com.troop.freedcam.ui.menu.childs.VideoProfileExpandableChild;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.ArrayList;

/**
 * Created by troop on 19.08.2014.
 */
public class MenuCreator
{
    MainActivity_v2 context;
    AbstractCameraUiWrapper cameraUiWrapper;
    AppSettingsManager appSettingsManager;
    AbstractParameterHandler parameterHandler;
    public MenuCreator(MainActivity_v2 context, AbstractCameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.context = context;
        this.appSettingsManager = appSettingsManager;
        this.parameterHandler = cameraUiWrapper.camParametersHandler;
    }

    public ExpandableGroup CreatePictureSettings(SurfaceView surfaceView)
    {
        ExpandableGroup picGroup = new ExpandableGroup(context);
        picGroup.setName(context.getString(R.string.picture_settings));
        picGroup.modulesToShow = cameraUiWrapper.moduleHandler.PictureModules;
        //cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(picGroup);
        createPictureSettingsChilds(picGroup, surfaceView);
        return picGroup;
    }

    private void createPictureSettingsChilds(ExpandableGroup group, SurfaceView surfaceView)
    {
        ArrayList<ExpandableChild> piclist = new ArrayList<ExpandableChild>();
        if (parameterHandler.PictureSize != null)
        {
            PreviewExpandableChild picSize;
            if (surfaceView instanceof ExtendedSurfaceView)
            {
                picSize = new PreviewExpandableChild(context, (ExtendedSurfaceView)surfaceView);
            }
            else
            {
                picSize = new PreviewExpandableChild(context);
            }
            picSize.setName(context.getString(R.string.picture_size));
            //cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(picSize);
            picSize.setParameterHolder(parameterHandler.PictureSize, appSettingsManager, AppSettingsManager.SETTING_PICTURESIZE, cameraUiWrapper.moduleHandler.PictureModules, cameraUiWrapper);
            piclist.add(picSize);
        }

        if (parameterHandler.PictureFormat != null) {
            PictureFormatExpandableChild picformat = new PictureFormatExpandableChild(context);
            picformat.setName(context.getString(R.string.picture_format));
            picformat.setParameterHolder(parameterHandler.PictureFormat, appSettingsManager, AppSettingsManager.SETTING_PICTUREFORMAT, cameraUiWrapper.moduleHandler.PictureModules, cameraUiWrapper);
         /*getNewChild(parameterHandler.PictureFormat,
                AppSettingsManager.SETTING_PICTUREFORMAT,
                context.getString(R.string.picture_format),
                cameraUiWrapper.moduleHandler.PictureModules);*/


            piclist.add(picformat);
        }

        if (parameterHandler.JpegQuality != null) {
            ExpandableChild jpegquality = getNewChild(parameterHandler.JpegQuality,
                    AppSettingsManager.SETTING_JPEGQUALITY, context.getString(R.string.jpeg_quality), cameraUiWrapper.moduleHandler.PictureModules);
            piclist.add(jpegquality);
        }

        /*if (parameterHandler.AE_Bracket.IsSupported()) {
            ExpandableChild ae_bracket = getNewChild(parameterHandler.AE_Bracket, AppSettingsManager.SETTING_AEBRACKET, context.getString(R.string.picture_aebracket), cameraUiWrapper.moduleHandler.PictureModules);
            piclist.add(ae_bracket);
        }*/

        if (parameterHandler.RedEye != null && parameterHandler.RedEye.IsSupported())
        {
            ExpandableChild redeye = getNewChild(parameterHandler.RedEye, AppSettingsManager.SETTING_REDEYE_MODE, context.getString(R.string.picture_redeyereduction), cameraUiWrapper.moduleHandler.PictureModules);
            piclist.add(redeye);
        }

        group.setItems(piclist);
    }

    public ExpandableGroup CreateModeSettings()
    {
        ExpandableGroup modesGroup = new ExpandableGroup(context);
        modesGroup.setName(context.getString(R.string.mode_settings));
        //modesGroup.modulesToShow = cameraUiWrapper.moduleHandler.AllModules;
        createModesSettingsChilds(modesGroup);
        return modesGroup;
    }

    private void createModesSettingsChilds(ExpandableGroup group)
    {
        ArrayList<ExpandableChild> childlist = new ArrayList<ExpandableChild>();

        if (parameterHandler.ColorMode != null && parameterHandler.ColorMode.IsSupported()) {
            ExpandableChild color = new ExpandableChild(context);
            color.setName(context.getString(R.string.mode_color));
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(color);
            color.setParameterHolder(parameterHandler.ColorMode, appSettingsManager, AppSettingsManager.SETTING_COLORMODE, cameraUiWrapper.moduleHandler.AllModules, cameraUiWrapper);
            childlist.add(color);
        }

        if (parameterHandler.IsoMode != null && parameterHandler.IsoMode.IsSupported()) {
            ExpandableChild iso = new ExpandableChild(context);
            iso.setName(context.getString(R.string.mode_iso));
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(iso);
            iso.setParameterHolder(parameterHandler.IsoMode, appSettingsManager, AppSettingsManager.SETTING_ISOMODE, cameraUiWrapper.moduleHandler.AllModules, cameraUiWrapper);
            childlist.add(iso);
        }

        if (parameterHandler.ExposureMode != null && parameterHandler.ExposureMode.IsSupported())
        {
            ExpandableChild exposure = new ExpandableChild(context);
            exposure.setName(context.getString(R.string.mode_exposure));
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(exposure);
            exposure.setParameterHolder(parameterHandler.ExposureMode,
                    appSettingsManager,
                    AppSettingsManager.SETTING_EXPOSUREMODE,
                    cameraUiWrapper.moduleHandler.AllModules, cameraUiWrapper);
            childlist.add(exposure);
        }

        if (parameterHandler.WhiteBalanceMode != null && parameterHandler.WhiteBalanceMode.IsSupported())
        {
            ExpandableChild wb = getNewChild(parameterHandler.WhiteBalanceMode,
                    AppSettingsManager.SETTING_WHITEBALANCEMODE,
                    context.getString(R.string.mode_whitebalance), cameraUiWrapper.moduleHandler.AllModules);
            wb.setName(context.getString(R.string.mode_whitebalance));
            childlist.add(wb);
        }
        if (parameterHandler.SceneMode != null && parameterHandler.SceneMode.IsSupported())
        {
            ExpandableChild scen = getNewChild(parameterHandler.SceneMode, AppSettingsManager.SETTING_SCENEMODE, context.getString(R.string.mode_scene), cameraUiWrapper.moduleHandler.AllModules);
            scen.setName(context.getString(R.string.mode_scene));
            childlist.add(scen);
        }
        if (parameterHandler.FocusMode != null) {
            ExpandableChild focus = getNewChild(parameterHandler.FocusMode, AppSettingsManager.SETTING_FOCUSMODE, context.getString(R.string.mode_focus), cameraUiWrapper.moduleHandler.AllModules);
            focus.setName(context.getString(R.string.mode_focus));
            childlist.add(focus);
        }

        group.setItems(childlist);
    }

    public ExpandableGroup CreateQualitySettings()
    {
        ExpandableGroup qualityGroup = new ExpandableGroup(context);
        qualityGroup.setName(context.getString(R.string.quality_settings));
        //qualityGroup.modulesToShow = cameraUiWrapper.moduleHandler.AllModules;
        createQualitySettingsChilds(qualityGroup);
        return qualityGroup;
    }

    private void  createQualitySettingsChilds(ExpandableGroup group)
    {
        ArrayList<ExpandableChild> childlist = new ArrayList<ExpandableChild>();

        if (parameterHandler.AntiBandingMode != null && parameterHandler.AntiBandingMode.IsSupported())
        {
            ExpandableChild antibanding = new ExpandableChild(context);
            antibanding.setName(context.getString(R.string.antibanding));
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(antibanding);
            antibanding.setParameterHolder(parameterHandler.AntiBandingMode, appSettingsManager, AppSettingsManager.SETTING_ANTIBANDINGMODE, cameraUiWrapper.moduleHandler.AllModules, cameraUiWrapper);
            childlist.add(antibanding);
        }
        if (parameterHandler.ImagePostProcessing != null && parameterHandler.ImagePostProcessing.IsSupported())
        {
            ExpandableChild ipp = new ExpandableChild(context);
            ipp.setName(context.getString(R.string.image_post_processing));
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(ipp);
            ipp.setParameterHolder(parameterHandler.ImagePostProcessing, appSettingsManager, AppSettingsManager.SETTING_IMAGEPOSTPROCESSINGMODE, cameraUiWrapper.moduleHandler.AllModules, cameraUiWrapper);
            childlist.add(ipp);
        }
        if (parameterHandler.LensShade != null && parameterHandler.LensShade.IsSupported())
        {
            ExpandableChild lens = getNewChild(parameterHandler.LensShade, AppSettingsManager.SETTING_LENSSHADE_MODE, context.getString(R.string.quality_lensshade), cameraUiWrapper.moduleHandler.AllModules);
            childlist.add(lens);
        }
        /*if (parameterHandler.ZSL.IsSupported())
        {
            ExpandableChild zsl = getNewChild(parameterHandler.ZSL, AppSettingsManager.SETTING_ZEROSHUTTERLAG_MODE, context.getString(R.string.quality_zsl), cameraUiWrapper.moduleHandler.AllModules);
            childlist.add(zsl);
        }*/
        if (parameterHandler.SceneDetect != null && parameterHandler.SceneDetect.IsSupported())
        {
            ExpandableChild sd = getNewChild(parameterHandler.SceneDetect, AppSettingsManager.SETTING_SCENEDETECT_MODE, context.getString(R.string.quality_scenedetect), cameraUiWrapper.moduleHandler.AllModules);
            childlist.add(sd);
        }
        if (parameterHandler.Denoise != null && parameterHandler.Denoise.IsSupported())
        {
            ExpandableChild sd = getNewChild(parameterHandler.Denoise, AppSettingsManager.SETTING_DENOISE_MODE, context.getString(R.string.quality_denoise), cameraUiWrapper.moduleHandler.AllModules);
            childlist.add(sd);
        }
        /*if (parameterHandler.DigitalImageStabilization.IsSupported())
        {
            ExpandableChild sd = getNewChild(parameterHandler.DigitalImageStabilization, AppSettingsManager.SETTING_DIS_MODE, context.getString(R.string.quality_digitalimagestab), cameraUiWrapper.moduleHandler.AllModules);
            childlist.add(sd);
        }*/
        /*if (parameterHandler.MemoryColorEnhancement.IsSupported())
        {
            ExpandableChild sd = getNewChild(parameterHandler.MemoryColorEnhancement, AppSettingsManager.SETTING_MCE_MODE, context.getString(R.string.quality_mce), cameraUiWrapper.moduleHandler.AllModules);
            childlist.add(sd);
        }*/

        /*if (parameterHandler.SkinToneEnhancment.IsSupported())
        {
            ExpandableChild sd = getNewChild(parameterHandler.SkinToneEnhancment, AppSettingsManager.SETTING_SKINTONE_MODE, context.getString(R.string.quality_skintone), cameraUiWrapper.moduleHandler.AllModules);
            childlist.add(sd);
        }*/
        if (parameterHandler.NonZslManualMode != null && parameterHandler.NonZslManualMode.IsSupported())
        {
            ExpandableChild nzm = getNewChild(
                    parameterHandler.NonZslManualMode,
                    AppSettingsManager.SETTING_NONZSLMANUALMODE,
                    context.getString(R.string.quality_nonmanualzsl),
                    cameraUiWrapper.moduleHandler.AllModules);
            childlist.add(nzm);
        }
        /*if(parameterHandler.Histogram.IsSupported())
        {
            ExpandableChild his = getNewChild(
                    parameterHandler.Histogram,
                    AppSettingsManager.SETTING_HISTOGRAM,
                    "Histogram",
                    cameraUiWrapper.moduleHandler.AllModules );
            childlist.add(his);
        }*/




        group.setItems(childlist);
    }

    public ExpandableGroup CreatePreviewSettings(SurfaceView surfaceView)
    {
        ExpandableGroup preview = getNewGroup("Long Exposure");
        //preview.modulesToShow = cameraUiWrapper.moduleHandler.LongeExpoModules;
        createPreviewSettingsChilds(preview, surfaceView);
        return preview;
    }

    private void createPreviewSettingsChilds(ExpandableGroup preview, SurfaceView surfaceView)
    {
        ArrayList<ExpandableChild> childlist = new ArrayList<ExpandableChild>();
        if (parameterHandler.PreviewSize != null)
        {
            PreviewExpandableChild size;
            if (surfaceView instanceof ExtendedSurfaceView) {
                size = new PreviewExpandableChild(context, (ExtendedSurfaceView)surfaceView);
            }
            else
            {
                size = new PreviewExpandableChild(context);
            }
            size.setName("Picture Size");
            //cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(size);
            size.setParameterHolder(parameterHandler.PreviewSize, appSettingsManager, AppSettingsManager.SETTING_PREVIEWSIZE, cameraUiWrapper.moduleHandler.LongeExpoModules, cameraUiWrapper);
            childlist.add(size);
        }

        //ExpandableChild size = getNewChild(parameterHandler.PreviewSize, AppSettingsManager.SETTING_PREVIEWSIZE, context.getString(R.string.preview_size), cameraUiWrapper.moduleHandler.AllModules);


        /*ExpandableChild fps = getNewChild(parameterHandler.PreviewFPS, AppSettingsManager.SETTING_PREVIEWFPS, context.getString(R.string.preview_fps), cameraUiWrapper.moduleHandler.AllModules);
        childlist.add(fps);

        ExpandableChild format = getNewChild(parameterHandler.PreviewFormat, AppSettingsManager.SETTING_PREVIEWFORMAT, context.getString(R.string.preview_fromat), cameraUiWrapper.moduleHandler.AllModules);
        childlist.add(format);*/

        //ExpandableChild expotime = getNewChild(new LongExposureSetting(null,null,"",""),AppSettingsManager.SETTING_EXPOSURELONGTIME, "ExposureTime", cameraUiWrapper.moduleHandler.LongeExpoModules);

        LongExposureChild child = new LongExposureChild(context);
        child.setName("ExposureTime");
        child.setParameterHolder(
                new LongExposureSetting(null,null,"",""),
                appSettingsManager,
                AppSettingsManager.SETTING_EXPOSURELONGTIME,
                cameraUiWrapper.moduleHandler.LongeExpoModules,
                cameraUiWrapper);
        childlist.add(child);

        preview.setItems(childlist);
    }

    private ExpandableGroup getNewGroup(String name)
    {
        ExpandableGroup group = new ExpandableGroup(context);
        //cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(group);
        group.setName(name);
        return group;
    }

    /**
     *
     * @param mode The camera parameter wich handels the input
     * @param appsettingName THe name of the appsetting to get stored
     * @param settingName the name wich gets displayed in menu
     * @param modules on wich module event the child is shown.
     * @return
     */
    private ExpandableChild getNewChild(I_ModeParameter mode, String appsettingName, String settingName, ArrayList<String> modules)
    {
        ExpandableChild child = new ExpandableChild(context);
        child.setName(settingName);
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(child);
        child.setParameterHolder(mode,appSettingsManager,appsettingName, modules, cameraUiWrapper);
        return child;
    }

    public ExpandableGroup CreateVideoSettings(SurfaceView surfaceView)
    {
        ExpandableGroup preview = getNewGroup("Video Settings");
        createVideoSettingsChilds(preview, surfaceView);
        return preview;
    }

    private void createVideoSettingsChilds(ExpandableGroup video, SurfaceView surfaceView)
    {
        ArrayList<ExpandableChild> childlist = new ArrayList<ExpandableChild>();

        /*VideoSizeExpandableChild videoSizeExpandableChild = new VideoSizeExpandableChild(context);
        videoSizeExpandableChild.setName("Video Size");
        videoSizeExpandableChild.setParameterHolder(
                parameterHandler.VideoSize,
                appSettingsManager,AppSettingsManager.SETTING_VIDEOSIZE,
                cameraUiWrapper.moduleHandler.VideoModules,
                cameraUiWrapper);
        childlist.add(videoSizeExpandableChild);*/

        if (parameterHandler.VideoProfiles != null) {
            VideoProfileExpandableChild videoProfile = new VideoProfileExpandableChild(context, cameraUiWrapper);
            videoProfile.setName("Video Profile");

            videoProfile.setParameterHolder(
                    parameterHandler.VideoProfiles,
                    appSettingsManager,
                    AppSettingsManager.SETTING_VIDEPROFILE,
                    cameraUiWrapper.moduleHandler.VideoModules,
                    cameraUiWrapper);
            childlist.add(videoProfile);

            ExpandableChildNumber timelapseframes = new ExpandableChildNumber(context,appSettingsManager,AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME, cameraUiWrapper.moduleHandler.VideoModules, cameraUiWrapper);
            timelapseframes.setMinMax(0.01f, 30);
            timelapseframes.setName("Timelapse FPS");
            childlist.add(timelapseframes);
            videoProfile.videoProfileChanged = timelapseframes;


            video.setItems(childlist);
        }
        if (parameterHandler.VideoProfilesG3 != null && DeviceUtils.isLGADV())
        {
            VideoProfileExpandableChild videoProfile = new VideoProfileExpandableChild(context, cameraUiWrapper);
            videoProfile.setName("Video Profile");
            videoProfile.setParameterHolder(
                    parameterHandler.VideoProfilesG3,
                    appSettingsManager,
                    AppSettingsManager.SETTING_VIDEPROFILE,
                    cameraUiWrapper.moduleHandler.VideoModules,
                    cameraUiWrapper);
            childlist.add(videoProfile);

            ExpandableChildNumber timelapseframes = new ExpandableChildNumber(context,appSettingsManager,AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME, cameraUiWrapper.moduleHandler.VideoModules, cameraUiWrapper);
            timelapseframes.setMinMax(0.01f, 30);
            timelapseframes.setName("Timelapse FPS");
            childlist.add(timelapseframes);
            videoProfile.videoProfileChanged = timelapseframes;


            video.setItems(childlist);
        }



        if (parameterHandler.VideoHDR != null && parameterHandler.VideoHDR.IsSupported()) {
            ExpandableChild videoHdr = new ExpandableChild(context);
            videoHdr.setName("Video HDR");
            videoHdr.setParameterHolder(
                    parameterHandler.VideoHDR,
                    appSettingsManager,
                    AppSettingsManager.SETTING_VIDEOHDR,
                    cameraUiWrapper.moduleHandler.VideoModules,
                    cameraUiWrapper
            );

            childlist.add(videoHdr);
        }



    }

    public ExpandableGroup CreateSettings()
    {
        ExpandableGroup preview = getNewGroup("Settings");
        ArrayList<ExpandableChild> childlist = new ArrayList<ExpandableChild>();

        if (appSettingsManager.getCamApi().equals(AppSettingsManager.API_1)) {
            SaveCamParasExpandableChild saveCamparas = new SaveCamParasExpandableChild(context);
            saveCamparas.setParameterHolder(null, null, "Save Camparas", cameraUiWrapper.moduleHandler.AllModules, cameraUiWrapper);
            childlist.add(saveCamparas);
        }

        SwitchApiExpandableChild sonyExpandableChild = new SwitchApiExpandableChild(context);
        sonyExpandableChild.setParameterHolder(null, appSettingsManager, AppSettingsManager.SETTING_SONYAPI, cameraUiWrapper.moduleHandler.AllModules, cameraUiWrapper);
        childlist.add(sonyExpandableChild);

        preview.setItems(childlist);

        return preview;
    }


}
