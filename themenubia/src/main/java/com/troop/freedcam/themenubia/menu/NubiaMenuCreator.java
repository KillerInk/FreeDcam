package com.troop.freedcam.themenubia.menu;

import android.view.SurfaceView;

import com.troop.freedcam.camera.ExtendedSurfaceView;
import com.troop.freedcam.themenubia.R;
import com.troop.freedcam.themenubia.menu.childs.NubiaExpandableChild;
import com.troop.freedcam.themenubia.menu.childs.NubiaExternalShutterChild;
import com.troop.freedcam.themenubia.menu.childs.NubiaGpsChild;
import com.troop.freedcam.themenubia.menu.childs.NubiaGuideChild;
import com.troop.freedcam.themenubia.menu.childs.NubiaLongExposure;
import com.troop.freedcam.themenubia.menu.childs.NubiaPictureFormatExpandableChild;
import com.troop.freedcam.themenubia.menu.childs.NubiaPreviewExpandableChild;
import com.troop.freedcam.themenubia.menu.childs.NubiaPreviewSizeChild;
import com.troop.freedcam.themenubia.menu.childs.NubiaSavCamParamsChild;
import com.troop.freedcam.themenubia.menu.childs.NubiaSwitchApiChild;
import com.troop.freedcam.themenubia.menu.childs.NubiaThemeChild;
import com.troop.freedcam.themenubia.menu.childs.NubiaTimelapseFramesChild;
import com.troop.freedcam.themenubia.menu.childs.NubiaVideoProfilChild;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.menu.themes.classic.menu.ExpandableGroup;
import com.troop.freedcam.ui.menu.themes.classic.menu.MenuCreator;
import com.troop.freedcam.ui.menu.themes.classic.menu.MenuFragment;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.ExpandableChild;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.ExpandableChildDngSupport;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.ExpandableChildOrientationHack;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.ExpandbleChildAeBracket;

import java.util.ArrayList;

/**
 * Created by troop on 23.03.2015.
 */
public class NubiaMenuCreator extends MenuCreator
{

    public NubiaMenuCreator(MenuFragment context, AppSettingsManager appSettingsManager, I_Activity activityV2) {
        super(context, appSettingsManager, activityV2);
    }

    public ExpandableGroup CreatePictureSettings(SurfaceView surfaceView)
    {
        ExpandableGroup picGroup = new NubiaExpandableGroup(context, submenu,appSettingsManager);
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
            picSize = new NubiaPreviewExpandableChild(context, (ExtendedSurfaceView)surfaceView, group, context.getString(R.string.picture_size), appSettingsManager , AppSettingsManager.SETTING_PICTURESIZE);
        }
        else
        {
            picSize = new NubiaPreviewExpandableChild(context, group, context.getString(R.string.picture_size),appSettingsManager, AppSettingsManager.SETTING_PICTURESIZE);
        }
        piclist.add(picSize);


        picformat = new NubiaPictureFormatExpandableChild(context, group, context.getString(R.string.picture_format), appSettingsManager, AppSettingsManager.SETTING_PICTUREFORMAT);
        piclist.add(picformat);

        jpegquality= new NubiaExpandableChild(context, group, context.getString(R.string.jpeg_quality), appSettingsManager, AppSettingsManager.SETTING_JPEGQUALITY);
        piclist.add(jpegquality);


        contShootMode = new NubiaExpandableChild(context,group,context.getString(R.string.picture_contshootmode), appSettingsManager, "");
        piclist.add(contShootMode);
        contShootModeSpeed = new NubiaExpandableChild(context,group,context.getString(R.string.picture_contshootmodespeed), appSettingsManager, "");
        piclist.add(contShootModeSpeed);




        /*if (parameterHandler.AE_Bracket.IsSupported()) {
            ExpandableChild ae_bracket = getNewChild(parameterHandler.AE_Bracket, AppSettingsManager.SETTING_AEBRACKET, context.getString(R.string.picture_aebracket), cameraUiWrapper.moduleHandler.PictureModules);
            piclist.add(ae_bracket);
        }*/

        redeye= new NubiaExpandableChild(context, group, context.getString(R.string.picture_redeyereduction),appSettingsManager, AppSettingsManager.SETTING_REDEYE_MODE);
        piclist.add(redeye);

        dngSwitch = new ExpandableChildDngSupport(context,group, appSettingsManager,context.getString(R.string.picture_dng_convert), AppSettingsManager.SETTING_DNG);


        aeBracketSwitch = new ExpandbleChildAeBracket(context, group, appSettingsManager, context.getString(R.string.picture_hdr_aebracket), AppSettingsManager.SETTING_AEBRACKETACTIVE);
        piclist.add(aeBracketSwitch);
        piclist.add(dngSwitch);
        group.setItems(piclist);
    }

    public ExpandableGroup CreateModeSettings()
    {
        ExpandableGroup modesGroup = new NubiaExpandableGroup(context, submenu,appSettingsManager);
        modesGroup.setName(context.getString(R.string.mode_settings));
        //modesGroup.modulesToShow = cameraUiWrapper.moduleHandler.AllModules;
        createModesSettingsChilds(modesGroup);
        return modesGroup;
    }

    private void createModesSettingsChilds(ExpandableGroup group)
    {
        ArrayList<ExpandableChild> childlist = new ArrayList<ExpandableChild>();

        color = new NubiaExpandableChild(context, group,context.getString(R.string.mode_color), appSettingsManager,AppSettingsManager.SETTING_COLORMODE);
        childlist.add(color);

        iso = new NubiaExpandableChild(context, group, context.getString(R.string.mode_iso),appSettingsManager, AppSettingsManager.SETTING_ISOMODE);
        childlist.add(iso);

        exposureMode = new NubiaExpandableChild(context, group, context.getString(R.string.mode_exposure), appSettingsManager, AppSettingsManager.SETTING_EXPOSUREMODE);
        childlist.add(exposureMode);

        whitebalanceMode =  new NubiaExpandableChild(context, group,context.getString(R.string.mode_whitebalance),appSettingsManager, AppSettingsManager.SETTING_WHITEBALANCEMODE);
        childlist.add(whitebalanceMode);

        sceneMode = new NubiaExpandableChild(context, group , context.getString(R.string.mode_scene), appSettingsManager, AppSettingsManager.SETTING_SCENEMODE);
        childlist.add(sceneMode);

        focusMode = new NubiaExpandableChild(context, group, context.getString(R.string.mode_focus), appSettingsManager, AppSettingsManager.SETTING_FOCUSMODE);
        childlist.add(focusMode);

        objectTrackingMode = new NubiaExpandableChild(context, group, context.getString(R.string.mode_objecttracking), appSettingsManager,AppSettingsManager.SETTING_OBJECTTRACKING);
        childlist.add(objectTrackingMode);

        group.setItems(childlist);
    }

    public ExpandableGroup CreateQualitySettings()
    {
        ExpandableGroup qualityGroup = new NubiaExpandableGroup(context, submenu,appSettingsManager);
        qualityGroup.setName(context.getString(R.string.quality_settings));
        //qualityGroup.modulesToShow = cameraUiWrapper.moduleHandler.AllModules;
        createQualitySettingsChilds(qualityGroup);
        return qualityGroup;
    }

    private void  createQualitySettingsChilds(ExpandableGroup group)
    {
        ArrayList<ExpandableChild> childlist = new ArrayList<ExpandableChild>();

        antibandingMode = new NubiaExpandableChild(context, group, context.getString(R.string.antibanding), appSettingsManager, AppSettingsManager.SETTING_ANTIBANDINGMODE);
        childlist.add(antibandingMode);

        ippMode = new NubiaExpandableChild(context, group, context.getString(R.string.image_post_processing), appSettingsManager, AppSettingsManager.SETTING_IMAGEPOSTPROCESSINGMODE);
        childlist.add(ippMode);

        lensShadeMode = new NubiaExpandableChild(context, group, context.getString(R.string.quality_lensshade), appSettingsManager, AppSettingsManager.SETTING_LENSSHADE_MODE);
        childlist.add(lensShadeMode);

        sceneDectecMode = new NubiaExpandableChild(context, group, context.getString(R.string.quality_scenedetect), appSettingsManager, AppSettingsManager.SETTING_SCENEDETECT_MODE);
        childlist.add(sceneDectecMode);

        denoiseMode = new NubiaExpandableChild(context, group, context.getString(R.string.quality_denoise), appSettingsManager, AppSettingsManager.SETTING_DENOISE_MODE);
        childlist.add(denoiseMode);

        digitalImageStabilization = new NubiaExpandableChild(context,group,context.getString(R.string.quality_digitalimagestab),appSettingsManager, AppSettingsManager.SETTING_DIS_MODE);
        childlist.add(digitalImageStabilization);

        mce = new NubiaExpandableChild(context,group,context.getString(R.string.quality_mce), appSettingsManager, AppSettingsManager.SETTING_MCE_MODE);
        childlist.add(mce);

        zsl = new NubiaExpandableChild(context, group, context.getString(R.string.quality_zsl), appSettingsManager, AppSettingsManager.SETTING_ZEROSHUTTERLAG_MODE);
        childlist.add(zsl);

        /*if (parameterHandler.SkinToneEnhancment.IsSupported())
        {
            ExpandableChild sd = getNewChild(parameterHandler.SkinToneEnhancment, AppSettingsManager.SETTING_SKINTONE_MODE, context.getString(R.string.quality_skintone), cameraUiWrapper.moduleHandler.AllModules);
            childlist.add(sd);
        }*/

        nonZSLMode = new NubiaExpandableChild(context, group, context.getString(R.string.quality_nonmanualzsl), appSettingsManager,AppSettingsManager.SETTING_NONZSLMANUALMODE);
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
        ExpandableGroup preview = getNewGroup(context.getString(R.string.picture_longexposure));
        //preview.modulesToShow = cameraUiWrapper.moduleHandler.LongeExpoModules;
        createPreviewSettingsChilds(preview, surfaceView);
        return preview;
    }

    private void createPreviewSettingsChilds(ExpandableGroup preview, SurfaceView surfaceView)
    {
        ArrayList<ExpandableChild> childlist = new ArrayList<ExpandableChild>();

        if (surfaceView instanceof ExtendedSurfaceView) {
            previewSize = new NubiaPreviewSizeChild(context, (ExtendedSurfaceView)surfaceView, preview, context.getString(R.string.picture_size), appSettingsManager, AppSettingsManager.SETTING_PREVIEWSIZE);
        }
        else
        {
            previewSize = new NubiaPreviewSizeChild(context, preview,  context.getString(R.string.picture_size), appSettingsManager,  AppSettingsManager.SETTING_PREVIEWSIZE);
        }
        childlist.add(previewSize);

        //ExpandableChild size = getNewChild(parameterHandler.PreviewSize, AppSettingsManager.SETTING_PREVIEWSIZE, context.getString(R.string.preview_size), cameraUiWrapper.moduleHandler.AllModules);

        /*ExpandableChild fps = getNewChild(parameterHandler.PreviewFPS, AppSettingsManager.SETTING_PREVIEWFPS, context.getString(R.string.preview_fps), cameraUiWrapper.moduleHandler.AllModules);
        childlist.add(fps);

        ExpandableChild format = getNewChild(parameterHandler.PreviewFormat, AppSettingsManager.SETTING_PREVIEWFORMAT, context.getString(R.string.preview_fromat), cameraUiWrapper.moduleHandler.AllModules);
        childlist.add(format);*/

        //ExpandableChild expotime = getNewChild(new LongExposureSetting(null,null,"",""),AppSettingsManager.SETTING_EXPOSURELONGTIME, "ExposureTime", cameraUiWrapper.moduleHandler.LongeExpoModules);

        longExposureTime =  new NubiaLongExposure(context, preview, context.getString(R.string.picture_exposuretime), appSettingsManager, AppSettingsManager.SETTING_EXPOSURELONGTIME);
        childlist.add(longExposureTime);

        preview.setItems(childlist);
    }

    private ExpandableGroup getNewGroup(String name)
    {
        ExpandableGroup group = new NubiaExpandableGroup(context, submenu,appSettingsManager);
        //cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(group);
        group.setName(name);
        return group;
    }

    public ExpandableGroup CreateVideoSettings(SurfaceView surfaceView)
    {
        ExpandableGroup preview = getNewGroup(context.getString(R.string.video_settings));
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
            videoProfile = new NubiaVideoProfilChild(context,(ExtendedSurfaceView)surfaceView, video, context.getString(R.string.video_profile), appSettingsManager, AppSettingsManager.SETTING_VIDEPROFILE);
        }
        else
        {
            videoProfile = new NubiaVideoProfilChild(context, null, video, context.getString(R.string.video_profile), appSettingsManager, AppSettingsManager.SETTING_VIDEPROFILE);
        }
        childlist.add(videoProfile);

        timelapseframes = new NubiaTimelapseFramesChild(context, video,appSettingsManager,context.getString(R.string.video_timelapsefps),AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME);
        timelapseframes.setMinMax(0.01f, 30);

        childlist.add(timelapseframes);


        video.setItems(childlist);

        videoHdr = new NubiaExpandableChild(context, video, context.getString(R.string.video_hdr), appSettingsManager, AppSettingsManager.SETTING_VIDEOHDR);
        childlist.add(videoHdr);

    }

    public ExpandableGroup CreateSettings()
    {
        settingsGroup = getNewGroup(context.getString(R.string.settings));
        ArrayList<ExpandableChild> childlist = new ArrayList<ExpandableChild>();

        sonyExpandableChild = new NubiaSwitchApiChild(context,activityV2, settingsGroup,context.getString(R.string.settings_switchapi) ,appSettingsManager, AppSettingsManager.SETTING_SONYAPI);
        childlist.add(sonyExpandableChild);

        //defcomg was here



        saveCamparas = new NubiaSavCamParamsChild(context, settingsGroup, context.getString(R.string.settings_savecampara),appSettingsManager, null);
        childlist.add(saveCamparas);

        guide = new NubiaGuideChild(context, settingsGroup, context.getString(R.string.picture_composit), appSettingsManager, AppSettingsManager.SETTING_GUIDE);
        childlist.add(guide);

        gps = new NubiaGpsChild(context, settingsGroup, context.getString(R.string.settings_gps), appSettingsManager, AppSettingsManager.SETTING_LOCATION);
        childlist.add(gps);

        externalShutter = new NubiaExternalShutterChild(context, settingsGroup, context.getString(R.string.settings_externalshutter), appSettingsManager, AppSettingsManager.SETTING_EXTERNALSHUTTER);
        childlist.add(externalShutter);

        rotationHack = new ExpandableChildOrientationHack(context, settingsGroup, context.getString(R.string.settings_orientatiohack), appSettingsManager, AppSettingsManager.SETTING_OrientationHack);
        childlist.add(rotationHack);

        Theme = new NubiaThemeChild(context,activityV2, settingsGroup, context.getString(R.string.settings_theme), appSettingsManager, AppSettingsManager.SETTING_Theme);
        childlist.add(Theme);

        settingsGroup.setItems(childlist);

        return settingsGroup;
    }
}
