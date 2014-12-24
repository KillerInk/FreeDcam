package com.troop.freedcam.ui.menu;

import android.view.SurfaceView;

import com.troop.freedcam.R;

import com.troop.freedcam.camera.CameraUiWrapper;
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

    ExpandableGroup settingsGroup;

    PreviewExpandableChild picSize;
    PictureFormatExpandableChild picformat;
    ExpandableChild jpegquality;
    ExpandableChild redeye;
    ExpandableChild color;
    ExpandableChild iso;
    ExpandableChild exposureMode;
    ExpandableChild whitebalanceMode;
    ExpandableChild sceneMode;
    ExpandableChild focusMode;
    ExpandableChild antibandingMode;
    ExpandableChild ippMode;
    ExpandableChild lensShadeMode;
    ExpandableChild sceneDectecMode;
    ExpandableChild denoiseMode;
    ExpandableChild nonZSLMode;
    PreviewExpandableChild previewSize;
    LongExposureChild longExposureTime;
    VideoProfileExpandableChild videoProfile;
    ExpandableChild videoHdr;
    ExpandableChildNumber timelapseframes;
    SaveCamParasExpandableChild saveCamparas;
    SwitchApiExpandableChild sonyExpandableChild;

    public MenuCreator(MainActivity_v2 context, AbstractCameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.context = context;
        this.appSettingsManager = appSettingsManager;

    }

    public void setCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.parameterHandler = cameraUiWrapper.camParametersHandler;
        if (parameterHandler.PictureSize != null)
        {
            picSize.setParameterHolder(parameterHandler.PictureSize,cameraUiWrapper.moduleHandler.PictureModules);
        }
        if (parameterHandler.PictureFormat != null)
        {
            picformat.setParameterHolder(parameterHandler.PictureFormat,cameraUiWrapper.moduleHandler.PictureModules);
        }
        if (parameterHandler.JpegQuality != null) {
            jpegquality.setParameterHolder(parameterHandler.JpegQuality,cameraUiWrapper.moduleHandler.PictureModules);
        }
        if (parameterHandler.RedEye != null && parameterHandler.RedEye.IsSupported())
        {
            redeye.setParameterHolder(parameterHandler.RedEye,cameraUiWrapper.moduleHandler.PictureModules);
        }
        if (parameterHandler.ColorMode != null && parameterHandler.ColorMode.IsSupported()) {
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(color);
            color.setParameterHolder(parameterHandler.ColorMode, cameraUiWrapper.moduleHandler.AllModules);
        }
        if (parameterHandler.IsoMode != null && parameterHandler.IsoMode.IsSupported()) {

            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(iso);
            iso.setParameterHolder(parameterHandler.IsoMode, cameraUiWrapper.moduleHandler.AllModules);
        }
        if (parameterHandler.ExposureMode != null && parameterHandler.ExposureMode.IsSupported())
        {
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(exposureMode);
            exposureMode.setParameterHolder(parameterHandler.ExposureMode, cameraUiWrapper.moduleHandler.AllModules);
        }
        if (parameterHandler.WhiteBalanceMode != null && parameterHandler.WhiteBalanceMode.IsSupported())
        {
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(whitebalanceMode);
            whitebalanceMode.setParameterHolder(parameterHandler.WhiteBalanceMode, cameraUiWrapper.moduleHandler.AllModules);
        }
        if (parameterHandler.SceneMode != null && parameterHandler.SceneMode.IsSupported())
        {
            //TODO look why they arent added to moduleeventhandler
            sceneMode.setParameterHolder(parameterHandler.SceneMode, cameraUiWrapper.moduleHandler.AllModules);
        }
        if (parameterHandler.FocusMode != null) {

            focusMode.setParameterHolder(parameterHandler.FocusMode, cameraUiWrapper.moduleHandler.AllModules);
        }
        if (parameterHandler.AntiBandingMode != null && parameterHandler.AntiBandingMode.IsSupported())
        {
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(antibandingMode);
            antibandingMode.setParameterHolder(parameterHandler.AntiBandingMode, cameraUiWrapper.moduleHandler.AllModules);
        }
        if (parameterHandler.ImagePostProcessing != null && parameterHandler.ImagePostProcessing.IsSupported())
        {
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(ippMode);
            ippMode.setParameterHolder(parameterHandler.ImagePostProcessing, cameraUiWrapper.moduleHandler.AllModules);
        }
        if (parameterHandler.LensShade != null && parameterHandler.LensShade.IsSupported())
        {
            lensShadeMode.setParameterHolder(parameterHandler.LensShade, cameraUiWrapper.moduleHandler.AllModules);
        }
        if (parameterHandler.SceneDetect != null && parameterHandler.SceneDetect.IsSupported())
        {
            sceneDectecMode.setParameterHolder(parameterHandler.SceneDetect, cameraUiWrapper.moduleHandler.AllModules);
        }
        if (parameterHandler.Denoise != null && parameterHandler.Denoise.IsSupported())
        {
            denoiseMode.setParameterHolder(parameterHandler.Denoise, cameraUiWrapper.moduleHandler.AllModules);
        }
        if (parameterHandler.NonZslManualMode != null && parameterHandler.NonZslManualMode.IsSupported())
        {
            nonZSLMode.setParameterHolder(parameterHandler.NonZslManualMode, cameraUiWrapper.moduleHandler.AllModules);
        }
        //used for longexposuremodule
        if (parameterHandler.PreviewSize != null)
        {
            previewSize.setParameterHolder(parameterHandler.PreviewSize, cameraUiWrapper.moduleHandler.LongeExpoModules);
        }

        longExposureTime.setParameterHolder(new LongExposureSetting(null,null,"",""), cameraUiWrapper.moduleHandler.LongeExpoModules);

        if (parameterHandler.VideoProfiles != null)
        {
            videoProfile.setParameterHolder(parameterHandler.VideoProfiles, cameraUiWrapper.moduleHandler.VideoModules);
        }
        if (parameterHandler.VideoProfilesG3 != null && DeviceUtils.isLGADV())
        {
            videoProfile.setParameterHolder(parameterHandler.VideoProfilesG3,cameraUiWrapper.moduleHandler.VideoModules);
        }
        if (parameterHandler.VideoHDR != null && parameterHandler.VideoHDR.IsSupported()) {
            videoHdr.setParameterHolder(parameterHandler.VideoHDR,cameraUiWrapper.moduleHandler.VideoModules);
        }
        if (saveCamparas.getParameterHolder() != null && saveCamparas.IsSupported())
            saveCamparas.setParameterHolder(null, cameraUiWrapper.moduleHandler.AllModules, cameraUiWrapper);
        if (sonyExpandableChild.getParameterHolder() != null && sonyExpandableChild.IsSupported())
            sonyExpandableChild.setParameterHolder(null, cameraUiWrapper.moduleHandler.AllModules);
    }

    public ExpandableGroup CreatePictureSettings(SurfaceView surfaceView)
    {
        ExpandableGroup picGroup = new ExpandableGroup(context);
        picGroup.setName(context.getString(R.string.picture_settings));
        //picGroup.modulesToShow = cameraUiWrapper.moduleHandler.PictureModules;

        createPictureSettingsChilds(picGroup, surfaceView);
        return picGroup;
    }

    private void createPictureSettingsChilds(ExpandableGroup group, SurfaceView surfaceView)
    {
        ArrayList<ExpandableChild> piclist = new ArrayList<ExpandableChild>();

        if (surfaceView instanceof ExtendedSurfaceView)
        {
            picSize = new PreviewExpandableChild(context, (ExtendedSurfaceView)surfaceView, group, context.getString(R.string.picture_size), appSettingsManager , AppSettingsManager.SETTING_PICTURESIZE);
        }
        else
        {
            picSize = new PreviewExpandableChild(context, group, context.getString(R.string.picture_size),appSettingsManager, AppSettingsManager.SETTING_PICTURESIZE);
        }
        piclist.add(picSize);

        picformat = new PictureFormatExpandableChild(context, group, context.getString(R.string.picture_format), appSettingsManager, AppSettingsManager.SETTING_PICTUREFORMAT);
        piclist.add(picformat);

        jpegquality= new ExpandableChild(context, group, context.getString(R.string.jpeg_quality), appSettingsManager, AppSettingsManager.SETTING_JPEGQUALITY);
        piclist.add(jpegquality);

        /*if (parameterHandler.AE_Bracket.IsSupported()) {
            ExpandableChild ae_bracket = getNewChild(parameterHandler.AE_Bracket, AppSettingsManager.SETTING_AEBRACKET, context.getString(R.string.picture_aebracket), cameraUiWrapper.moduleHandler.PictureModules);
            piclist.add(ae_bracket);
        }*/

        redeye= new ExpandableChild(context, group, context.getString(R.string.picture_redeyereduction),appSettingsManager, AppSettingsManager.SETTING_REDEYE_MODE);
        piclist.add(redeye);

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

        color = new ExpandableChild(context, group,context.getString(R.string.mode_color), appSettingsManager,AppSettingsManager.SETTING_COLORMODE);
        childlist.add(color);

        iso = new ExpandableChild(context, group, context.getString(R.string.mode_iso),appSettingsManager, AppSettingsManager.SETTING_ISOMODE);
        childlist.add(iso);

        exposureMode = new ExpandableChild(context, group, context.getString(R.string.mode_exposure), appSettingsManager, AppSettingsManager.SETTING_EXPOSUREMODE);
        childlist.add(exposureMode);

        whitebalanceMode =  new ExpandableChild(context, group,context.getString(R.string.mode_whitebalance),appSettingsManager, AppSettingsManager.SETTING_WHITEBALANCEMODE);
        childlist.add(whitebalanceMode);

        sceneMode = new ExpandableChild(context, group , context.getString(R.string.mode_scene), appSettingsManager, AppSettingsManager.SETTING_SCENEMODE);
        childlist.add(sceneMode);

        focusMode = new ExpandableChild(context, group, context.getString(R.string.mode_focus), appSettingsManager, AppSettingsManager.SETTING_FOCUSMODE);
        childlist.add(focusMode);

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

        antibandingMode = new ExpandableChild(context, group, context.getString(R.string.antibanding), appSettingsManager, AppSettingsManager.SETTING_ANTIBANDINGMODE);
        childlist.add(antibandingMode);

        ippMode = new ExpandableChild(context, group, context.getString(R.string.image_post_processing), appSettingsManager, AppSettingsManager.SETTING_IMAGEPOSTPROCESSINGMODE);
        childlist.add(ippMode);

        lensShadeMode = new ExpandableChild(context, group, context.getString(R.string.quality_lensshade), appSettingsManager, AppSettingsManager.SETTING_LENSSHADE_MODE);
        childlist.add(lensShadeMode);

        /*if (parameterHandler.ZSL.IsSupported())
        {
            ExpandableChild zsl = getNewChild(parameterHandler.ZSL, AppSettingsManager.SETTING_ZEROSHUTTERLAG_MODE, context.getString(R.string.quality_zsl), cameraUiWrapper.moduleHandler.AllModules);
            childlist.add(zsl);
        }*/

        sceneDectecMode = new ExpandableChild(context, group, context.getString(R.string.quality_scenedetect), appSettingsManager, AppSettingsManager.SETTING_SCENEDETECT_MODE);
        childlist.add(sceneDectecMode);

        denoiseMode = new ExpandableChild(context, group, context.getString(R.string.quality_denoise), appSettingsManager, AppSettingsManager.SETTING_DENOISE_MODE);
        childlist.add(denoiseMode);

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

        nonZSLMode = new ExpandableChild(context, group, context.getString(R.string.quality_nonmanualzsl), appSettingsManager,AppSettingsManager.SETTING_NONZSLMANUALMODE);
        childlist.add(nonZSLMode);

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

        if (surfaceView instanceof ExtendedSurfaceView) {
            previewSize = new PreviewExpandableChild(context, (ExtendedSurfaceView)surfaceView, preview, "Picture Size", appSettingsManager, AppSettingsManager.SETTING_PREVIEWSIZE);
        }
        else
        {
            previewSize = new PreviewExpandableChild(context, preview, "Picture Size", appSettingsManager,  AppSettingsManager.SETTING_PREVIEWSIZE);
        }
        childlist.add(previewSize);

        //ExpandableChild size = getNewChild(parameterHandler.PreviewSize, AppSettingsManager.SETTING_PREVIEWSIZE, context.getString(R.string.preview_size), cameraUiWrapper.moduleHandler.AllModules);

        /*ExpandableChild fps = getNewChild(parameterHandler.PreviewFPS, AppSettingsManager.SETTING_PREVIEWFPS, context.getString(R.string.preview_fps), cameraUiWrapper.moduleHandler.AllModules);
        childlist.add(fps);

        ExpandableChild format = getNewChild(parameterHandler.PreviewFormat, AppSettingsManager.SETTING_PREVIEWFORMAT, context.getString(R.string.preview_fromat), cameraUiWrapper.moduleHandler.AllModules);
        childlist.add(format);*/

        //ExpandableChild expotime = getNewChild(new LongExposureSetting(null,null,"",""),AppSettingsManager.SETTING_EXPOSURELONGTIME, "ExposureTime", cameraUiWrapper.moduleHandler.LongeExpoModules);

        longExposureTime =  new LongExposureChild(context, preview, "ExposureTime", appSettingsManager, AppSettingsManager.SETTING_EXPOSURELONGTIME);
        childlist.add(longExposureTime);

        preview.setItems(childlist);
    }

    private ExpandableGroup getNewGroup(String name)
    {
        ExpandableGroup group = new ExpandableGroup(context);
        //cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(group);
        group.setName(name);
        return group;
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

        if (surfaceView instanceof ExtendedSurfaceView)
        {
            videoProfile = new VideoProfileExpandableChild(context,(ExtendedSurfaceView)surfaceView, video, "Video Profile", appSettingsManager, AppSettingsManager.SETTING_VIDEPROFILE);
        }
        else
        {
            videoProfile = new VideoProfileExpandableChild(context, null, video, "Video Profile", appSettingsManager, AppSettingsManager.SETTING_VIDEPROFILE);
        }
        childlist.add(videoProfile);

        timelapseframes = new ExpandableChildNumber(context, video,appSettingsManager,"Timelapse FPS",AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME);
        timelapseframes.setMinMax(0.01f, 30);

        childlist.add(timelapseframes);
        videoProfile.videoProfileChanged = timelapseframes;

        video.setItems(childlist);

        videoHdr = new ExpandableChild(context, video, "Video HDR", appSettingsManager, AppSettingsManager.SETTING_VIDEOHDR);
        childlist.add(videoHdr);

    }

    public ExpandableGroup CreateSettings()
    {
        settingsGroup = getNewGroup("Settings");
        ArrayList<ExpandableChild> childlist = new ArrayList<ExpandableChild>();

        sonyExpandableChild = new SwitchApiExpandableChild(context, settingsGroup,"Switch Api" ,appSettingsManager, AppSettingsManager.SETTING_SONYAPI);
        childlist.add(sonyExpandableChild);

        saveCamparas = new SaveCamParasExpandableChild(context, settingsGroup, "Save Camparas",appSettingsManager, null);
        childlist.add(saveCamparas);

        settingsGroup.setItems(childlist);

        return settingsGroup;
    }


}
