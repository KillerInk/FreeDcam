package com.troop.freedcam.ui.menu.themes.classic.menu;

import android.content.Context;
import android.view.SurfaceView;
import android.widget.LinearLayout;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.ExtendedSurfaceView;
import com.troop.freedcam.camera.parameters.modes.LongExposureSetting;
import com.troop.freedcam.camera.parameters.modes.SimpleModeParameter;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.menu.themes.R;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.ExpandAbleChildHistogram;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.ExpandableChild;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.ExpandableChildDngSupport;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.ExpandableChildExternalShutter;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.ExpandableChildGps;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.ExpandableChildGuide;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.ExpandableChildOrientationHack;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.ExpandableChildSaveSD;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.ExpandableChildTheme;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.ExpandableChildTimelapseFps;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.ExpandbleChildAeBracket;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.LongExposureChild;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.PictureFormatExpandableChild;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.PreviewExpandableChild;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.SaveCamParasExpandableChild;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.SwitchApiExpandableChild;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.VideoProfileExpandableChild;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.ArrayList;

/**
 * Created by troop on 19.08.2014.
 */
public class MenuCreator
{
    protected Context context;
    MenuFragment fragment;
    protected AbstractCameraUiWrapper cameraUiWrapper;
    protected AppSettingsManager appSettingsManager;
    protected AbstractParameterHandler parameterHandler;

    protected ExpandableGroup settingsGroup;

    protected PreviewExpandableChild picSize;
    protected PictureFormatExpandableChild picformat;
    protected ExpandableChild jpegquality;
    protected ExpandableChild redeye;
    protected ExpandableChild color;
    protected ExpandableChild iso;
    protected ExpandableChild exposureMode;
    protected ExpandableChild whitebalanceMode;
    protected ExpandableChild sceneMode;
    protected ExpandableChild focusMode;
    protected ExpandableChild objectTrackingMode;
    protected ExpandableChild antibandingMode;
    protected ExpandableChild ippMode;
    protected ExpandableChild lensShadeMode;
    protected ExpandableChild sceneDectecMode;
    protected ExpandableChild denoiseMode;
    protected ExpandableChild nonZSLMode;
    protected ExpandableChild digitalImageStabilization;
    protected ExpandableChild mce;
    protected ExpandableChild zsl;
    protected ExpandableChild guide;
    protected ExpandableChild contShootMode;
    protected ExpandableChild contShootModeSpeed;
    protected PreviewExpandableChild previewSize;
    protected LongExposureChild longExposureTime;
    protected VideoProfileExpandableChild videoProfile;
    protected ExpandableChild videoHdr;
    protected ExpandableChildTimelapseFps timelapseframes;
    protected SaveCamParasExpandableChild saveCamparas;
    protected SwitchApiExpandableChild sonyExpandableChild;
    protected ExpandableChildDngSupport dngSwitch;
    protected ExpandbleChildAeBracket aeBracketSwitch;
    protected ExpandableChildGps gps;
    protected ExpandableChild externalShutter;
    protected ExpandableChildOrientationHack rotationHack;
    protected ExpandableChildTheme Theme;
    protected ExpandAbleChildHistogram Histogram;
    protected ExpandableChild CDS;
    /*protected ExpandableChild SecureMode;
    protected ExpandableChild RdiMode;*/
    protected ExpandableChild TnrMode;

    protected ExpandableChild EdgeMode;
    protected ExpandableChild ColorCorrectionMode;
    protected ExpandableChild HotPixelMode;
    protected ExpandableChild ToneMapMode;

    protected ExpandableChild PostViewSize;
    protected ExpandableChildSaveSD SaveToSD;

    protected ExpandableChild OisChild;
    protected ExpandableChild ControlModeChild;
    protected ExpandableChild PreviewFPS;

    protected LinearLayout submenu;

    protected I_Activity activityV2;

    public MenuCreator(MenuFragment context, AppSettingsManager appSettingsManager, I_Activity activityV2)
    {
        this.fragment = context;
        this.context = activityV2.GetActivityContext();
        this.appSettingsManager = appSettingsManager;
        this.activityV2 = activityV2;
        //this.submenu = (LinearLayout)context.settingsLayoutHolder.findViewById(R.id.GroupSubMenu);

    }

    public void setCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {

        this.cameraUiWrapper = cameraUiWrapper;
        this.parameterHandler = cameraUiWrapper.camParametersHandler;
        if (parameterHandler.PictureSize != null)
        {
            picSize.setParameterHolder(parameterHandler.PictureSize,cameraUiWrapper.moduleHandler.PictureModules);
        }
        if (dngSwitch != null)
            dngSwitch.setParameterHolder(new SimpleModeParameter(null), cameraUiWrapper.moduleHandler.PictureModules, parameterHandler);
        if (parameterHandler.PictureFormat != null)
        {
            picformat.PictureFormatChangedHandler = dngSwitch;
            picformat.setParameterHolder(parameterHandler.PictureFormat,cameraUiWrapper.moduleHandler.PictureModules);
        }
        if (parameterHandler.JpegQuality != null) {
            jpegquality.setParameterHolder(parameterHandler.JpegQuality,cameraUiWrapper.moduleHandler.PictureModules);
        }
        //defcomg was here
        if(parameterHandler.GuideList != null)
        {
            guide.setParameterHolder(parameterHandler.GuideList,cameraUiWrapper.moduleHandler.AllModules);
        }
        //
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
        if (parameterHandler.ExposureMode != null)
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
        if (parameterHandler.DigitalImageStabilization !=null && parameterHandler.DigitalImageStabilization.IsSupported())
        {
            digitalImageStabilization.setParameterHolder(parameterHandler.DigitalImageStabilization, cameraUiWrapper.moduleHandler.AllModules);
        }
        if (parameterHandler.MemoryColorEnhancement != null && parameterHandler.MemoryColorEnhancement.IsSupported())
        {
            mce.setParameterHolder(parameterHandler.MemoryColorEnhancement, cameraUiWrapper.moduleHandler.AllModules);
        }
        if (parameterHandler.ZSL != null && parameterHandler.ZSL.IsSupported())
        {
            zsl.setParameterHolder(parameterHandler.ZSL, cameraUiWrapper.moduleHandler.AllModules);
        }
        if (parameterHandler.CDS_Mode != null && parameterHandler.CDS_Mode.IsSupported() && CDS != null)
            CDS.setParameterHolder(parameterHandler.CDS_Mode, cameraUiWrapper.moduleHandler.AllModules);


        if (parameterHandler.oismode != null && parameterHandler.oismode.IsSupported())
            OisChild.setParameterHolder(parameterHandler.oismode, cameraUiWrapper.moduleHandler.AllModules);

        //used for longexposuremodule
        if (parameterHandler.PreviewSize != null)
        {
            previewSize.setParameterHolder(parameterHandler.PreviewSize, cameraUiWrapper.moduleHandler.LongeExpoModules);
        }

        longExposureTime.setParameterHolder(new LongExposureSetting(null,null,null,"",""), cameraUiWrapper.moduleHandler.LongeExpoModules);
        timelapseframes.setParameterHolder(new SimpleModeParameter(null), cameraUiWrapper.moduleHandler.VideoModules);


        if (parameterHandler.VideoProfiles != null)
        {
            videoProfile.videoProfileChanged = timelapseframes;
            videoProfile.setParameterHolder(parameterHandler.VideoProfiles, cameraUiWrapper.moduleHandler.VideoModules);
        }
        if (parameterHandler.VideoProfilesG3 != null && (DeviceUtils.isLG_G3() || DeviceUtils.isG2()))
        {
            videoProfile.videoProfileChanged = timelapseframes;
            videoProfile.setParameterHolder(parameterHandler.VideoProfilesG3,cameraUiWrapper.moduleHandler.VideoModules);
        }
        if (parameterHandler.VideoHDR != null && parameterHandler.VideoHDR.IsSupported()) {
            videoHdr.setParameterHolder(parameterHandler.VideoHDR,cameraUiWrapper.moduleHandler.VideoModules);
        }
        if(cameraUiWrapper instanceof CameraUiWrapper)
            saveCamparas.setParameterHolder(new SimpleModeParameter(null), cameraUiWrapper.moduleHandler.AllModules, cameraUiWrapper);
        if (sonyExpandableChild != null && sonyExpandableChild.getParameterHolder() != null && sonyExpandableChild.getParameterHolder().IsSupported())
            sonyExpandableChild.setParameterHolder(null, cameraUiWrapper.moduleHandler.AllModules);

        if (parameterHandler.AE_Bracket != null && parameterHandler.AE_Bracket.IsSupported())
        {
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(aeBracketSwitch);
            aeBracketSwitch.setParameterHolder(parameterHandler.AE_Bracket, cameraUiWrapper.moduleHandler.HDRModule, parameterHandler);
        }
        if (parameterHandler.TnrMode != null && parameterHandler.TnrMode.IsSupported())
            TnrMode.setParameterHolder(parameterHandler.TnrMode, cameraUiWrapper.moduleHandler.AllModules);
        /*if (parameterHandler.RdiMode != null && parameterHandler.RdiMode.IsSupported())
            RdiMode.setParameterHolder(parameterHandler.RdiMode, cameraUiWrapper.moduleHandler.AllModules);
        if (parameterHandler.SecureMode != null && parameterHandler.SecureMode.IsSupported())
            SecureMode.setParameterHolder(parameterHandler.SecureMode, cameraUiWrapper.moduleHandler.AllModules);*/
        if (parameterHandler.HotPixelMode != null && parameterHandler.HotPixelMode.IsSupported())
            HotPixelMode.setParameterHolder(parameterHandler.HotPixelMode, cameraUiWrapper.moduleHandler.AllModules);
        if (parameterHandler.ToneMapMode != null && parameterHandler.ToneMapMode.IsSupported())
            ToneMapMode.setParameterHolder(parameterHandler.ToneMapMode, cameraUiWrapper.moduleHandler.AllModules);
        if (parameterHandler.ContShootMode != null)
        {
            contShootMode.setParameterHolder(parameterHandler.ContShootMode, cameraUiWrapper.moduleHandler.PictureModules);
        }
        if (parameterHandler.ContShootModeSpeed != null)
        {
            contShootModeSpeed.setParameterHolder(parameterHandler.ContShootModeSpeed, cameraUiWrapper.moduleHandler.PictureModules);
        }

        if (parameterHandler.ObjectTracking != null)
        {
            objectTrackingMode.setParameterHolder(parameterHandler.ObjectTracking, cameraUiWrapper.moduleHandler.AllModules);
        }
        if (parameterHandler.ControlMode != null && parameterHandler.ControlMode.IsSupported())
            ControlModeChild.setParameterHolder(parameterHandler.ControlMode, cameraUiWrapper.moduleHandler.AllModules);

        gps.SetCameraUIWrapper(cameraUiWrapper);

        if (parameterHandler.EdgeMode != null && parameterHandler.EdgeMode.IsSupported())
            EdgeMode.setParameterHolder(parameterHandler.EdgeMode, cameraUiWrapper.moduleHandler.AllModules);
        if (parameterHandler.ColorCorrectionMode != null && parameterHandler.ColorCorrectionMode.IsSupported())
            ColorCorrectionMode.setParameterHolder(parameterHandler.ColorCorrectionMode, cameraUiWrapper.moduleHandler.AllModules);

        if (parameterHandler.PostViewSize != null)
            PostViewSize.setParameterHolder(parameterHandler.PostViewSize, cameraUiWrapper.moduleHandler.AllModules);

        externalShutter.setParameterHolder(null, cameraUiWrapper.moduleHandler.AllModules);
        rotationHack.SetCameraUIWrapper(cameraUiWrapper);
        if (Histogram != null)
            Histogram.SetCameraUIWrapper(cameraUiWrapper);

        SaveToSD.SetCameraUIWrapper(cameraUiWrapper);

        if(parameterHandler.ThemeList != null)
        {
            Theme.setParameterHolder(parameterHandler.ThemeList,cameraUiWrapper.moduleHandler.AllModules);
        }

        if (parameterHandler.PreviewFPS != null && parameterHandler.PreviewFPS.IsSupported())
            PreviewFPS.setParameterHolder(parameterHandler.PreviewFPS, cameraUiWrapper.moduleHandler.PictureModules);
        //cameraUiWrapper.moduleHandler.moduleEventHandler.ModuleHasChanged(cameraUiWrapper.moduleHandler.GetCurrentModuleName());

    }

    public ExpandableGroup CreatePictureSettings(SurfaceView surfaceView)
    {
        ExpandableGroup picGroup = new ExpandableGroup(context, submenu,appSettingsManager);
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


        contShootMode = new ExpandableChild(context,group,context.getString(R.string.picture_contshootmode), appSettingsManager, "");
        piclist.add(contShootMode);
        contShootModeSpeed = new ExpandableChild(context,group,context.getString(R.string.picture_contshootmodespeed), appSettingsManager, "");
        piclist.add(contShootModeSpeed);


        Histogram = new ExpandAbleChildHistogram(context,group, "Histogram", appSettingsManager,AppSettingsManager.SETTING_HISTOGRAM, activityV2);
        piclist.add(Histogram);

        /*if (parameterHandler.AE_Bracket.IsSupported()) {
            ExpandableChild ae_bracket = getNewChild(parameterHandler.AE_Bracket, AppSettingsManager.SETTING_AEBRACKET, context.getString(R.string.picture_aebracket), cameraUiWrapper.moduleHandler.PictureModules);
            piclist.add(ae_bracket);
        }*/

        redeye= new ExpandableChild(context, group, context.getString(R.string.picture_redeyereduction),appSettingsManager, AppSettingsManager.SETTING_REDEYE_MODE);
        piclist.add(redeye);

        dngSwitch = new ExpandableChildDngSupport(context,group, appSettingsManager,context.getString(R.string.picture_dng_convert), AppSettingsManager.SETTING_DNG);


        aeBracketSwitch = new ExpandbleChildAeBracket(context, group, appSettingsManager, context.getString(R.string.picture_hdr_aebracket), AppSettingsManager.SETTING_AEBRACKETACTIVE);

        piclist.add(aeBracketSwitch);
        piclist.add(dngSwitch);

        PreviewFPS = new ExpandableChild(context,group,"FPS", appSettingsManager, AppSettingsManager.SETTING_PREVIEWFPS);
        piclist.add(PreviewFPS);
        group.setItems(piclist);
    }

    public ExpandableGroup CreateModeSettings()
    {
        ExpandableGroup modesGroup = new ExpandableGroup(context, submenu,appSettingsManager);
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

        ColorCorrectionMode = new ExpandableChild(context,group,"ColorCorrection", appSettingsManager, AppSettingsManager.SETTING_COLORCORRECTION);
        childlist.add(ColorCorrectionMode);

        sceneMode = new ExpandableChild(context, group , context.getString(R.string.mode_scene), appSettingsManager, AppSettingsManager.SETTING_SCENEMODE);
        childlist.add(sceneMode);

        focusMode = new ExpandableChild(context, group, context.getString(R.string.mode_focus), appSettingsManager, AppSettingsManager.SETTING_FOCUSMODE);
        childlist.add(focusMode);

        objectTrackingMode = new ExpandableChild(context, group, context.getString(R.string.mode_objecttracking), appSettingsManager,AppSettingsManager.SETTING_OBJECTTRACKING);
        childlist.add(objectTrackingMode);

        ToneMapMode = new ExpandableChild(context,group,"ToneMap", appSettingsManager, AppSettingsManager.SETTING_TONEMAP);
        childlist.add(ToneMapMode);

        PostViewSize = new ExpandableChild(context,group,"PostViewSize", appSettingsManager, "");
        childlist.add(PostViewSize);

        ControlModeChild = new ExpandableChild(context,group,"ControlMode", appSettingsManager, AppSettingsManager.SETTING_CONTROLMODE);
        childlist.add(ControlModeChild);

        group.setItems(childlist);
    }

    public ExpandableGroup CreateQualitySettings()
    {
        ExpandableGroup qualityGroup = new ExpandableGroup(context, submenu,appSettingsManager);
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

        sceneDectecMode = new ExpandableChild(context, group, context.getString(R.string.quality_scenedetect), appSettingsManager, AppSettingsManager.SETTING_SCENEDETECT_MODE);
        childlist.add(sceneDectecMode);

        denoiseMode = new ExpandableChild(context, group, "WaveletDenoise", appSettingsManager, AppSettingsManager.SETTING_DENOISE_MODE);
        childlist.add(denoiseMode);

        digitalImageStabilization = new ExpandableChild(context,group,context.getString(R.string.quality_digitalimagestab),appSettingsManager, AppSettingsManager.SETTING_DIS_MODE);
        childlist.add(digitalImageStabilization);

        mce = new ExpandableChild(context,group,context.getString(R.string.quality_mce), appSettingsManager, AppSettingsManager.SETTING_MCE_MODE);
        childlist.add(mce);

        zsl = new ExpandableChild(context, group, context.getString(R.string.quality_zsl), appSettingsManager, AppSettingsManager.SETTING_ZEROSHUTTERLAG_MODE);
        childlist.add(zsl);

        /*if (parameterHandler.SkinToneEnhancment.IsSupported())
        {
            ExpandableChild sd = getNewChild(parameterHandler.SkinToneEnhancment, AppSettingsManager.SETTING_SKINTONE_MODE, context.getString(R.string.quality_skintone), cameraUiWrapper.moduleHandler.AllModules);
            childlist.add(sd);
        }*/

        nonZSLMode = new ExpandableChild(context, group, context.getString(R.string.quality_nonmanualzsl), appSettingsManager,AppSettingsManager.SETTING_NONZSLMANUALMODE);
        childlist.add(nonZSLMode);

        //http://en.m.wikipedia.org/wiki/Correlated_double_sampling
        CDS = new ExpandableChild(context,group, "CorrelatedDoubleSampling", appSettingsManager, AppSettingsManager.SETTING_CDS);
        childlist.add(CDS);

        TnrMode = new ExpandableChild(context,group,"TemporalNoiseReduction",appSettingsManager,AppSettingsManager.SETTING_TNR);
        childlist.add(TnrMode);

        /*SecureMode = new ExpandableChild(context,group,"SecureMode", appSettingsManager, AppSettingsManager.SETTING_SECUREMODE);
        childlist.add(SecureMode);

        RdiMode = new ExpandableChild(context,group, "RdiMode", appSettingsManager, AppSettingsManager.SETTING_RDI);
        childlist.add(RdiMode);

        */

        EdgeMode = new ExpandableChild(context,group,"Edge Mode", appSettingsManager, AppSettingsManager.SETTING_EDGE);
        childlist.add(EdgeMode);


        HotPixelMode = new ExpandableChild(context,group,"HotPixelCorrection", appSettingsManager, AppSettingsManager.SETTING_HOTPIXEL);
        childlist.add(HotPixelMode);

        OisChild = new ExpandableChild(context,group, "OIS", appSettingsManager, AppSettingsManager.SETTING_OIS);
        childlist.add(OisChild);

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
            previewSize = new PreviewExpandableChild(context, (ExtendedSurfaceView)surfaceView, preview, context.getString(R.string.picture_size), appSettingsManager, AppSettingsManager.SETTING_PREVIEWSIZE);
        }
        else
        {
            previewSize = new PreviewExpandableChild(context, preview,  context.getString(R.string.picture_size), appSettingsManager,  AppSettingsManager.SETTING_PREVIEWSIZE);
        }
        childlist.add(previewSize);

        //ExpandableChild size = getNewChild(parameterHandler.PreviewSize, AppSettingsManager.SETTING_PREVIEWSIZE, context.getString(R.string.preview_size), cameraUiWrapper.moduleHandler.AllModules);

        /*ExpandableChild fps = getNewChild(parameterHandler.PreviewFPS, AppSettingsManager.SETTING_PREVIEWFPS, context.getString(R.string.preview_fps), cameraUiWrapper.moduleHandler.AllModules);
        childlist.add(fps);

        ExpandableChild format = getNewChild(parameterHandler.PreviewFormat, AppSettingsManager.SETTING_PREVIEWFORMAT, context.getString(R.string.preview_fromat), cameraUiWrapper.moduleHandler.AllModules);
        childlist.add(format);*/

        //ExpandableChild expotime = getNewChild(new LongExposureSetting(null,null,"",""),AppSettingsManager.SETTING_EXPOSURELONGTIME, "ExposureTime", cameraUiWrapper.moduleHandler.LongeExpoModules);

        longExposureTime =  new LongExposureChild(context, preview, context.getString(R.string.picture_exposuretime), appSettingsManager, AppSettingsManager.SETTING_EXPOSURELONGTIME);
        childlist.add(longExposureTime);

        preview.setItems(childlist);
    }

    private ExpandableGroup getNewGroup(String name)
    {
        ExpandableGroup group = new ExpandableGroup(context, submenu,appSettingsManager);
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
            videoProfile = new VideoProfileExpandableChild(context,(ExtendedSurfaceView)surfaceView, video, context.getString(R.string.video_profile), appSettingsManager, AppSettingsManager.SETTING_VIDEPROFILE);
        }
        else
        {
            videoProfile = new VideoProfileExpandableChild(context, null, video, context.getString(R.string.video_profile), appSettingsManager, AppSettingsManager.SETTING_VIDEPROFILE);
        }
        childlist.add(videoProfile);

        timelapseframes = new ExpandableChildTimelapseFps(context, video,appSettingsManager,context.getString(R.string.video_timelapsefps),AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME);
        timelapseframes.setMinMax(0.01f, 30);

        childlist.add(timelapseframes);


        video.setItems(childlist);

        videoHdr = new ExpandableChild(context, video, context.getString(R.string.video_hdr), appSettingsManager, AppSettingsManager.SETTING_VIDEOHDR);
        childlist.add(videoHdr);

    }

    public ExpandableGroup CreateSettings()
    {
        settingsGroup = getNewGroup(context.getString(R.string.settings));
        ArrayList<ExpandableChild> childlist = new ArrayList<ExpandableChild>();

        sonyExpandableChild = new SwitchApiExpandableChild(context,activityV2, settingsGroup,context.getString(R.string.settings_switchapi) ,appSettingsManager, AppSettingsManager.SETTING_SONYAPI);
        childlist.add(sonyExpandableChild);

        //defcomg was here



        saveCamparas = new SaveCamParasExpandableChild(context, settingsGroup, context.getString(R.string.settings_savecampara),appSettingsManager, null);
        childlist.add(saveCamparas);

        guide = new ExpandableChildGuide(context, settingsGroup, context.getString(R.string.picture_composit), appSettingsManager, AppSettingsManager.SETTING_GUIDE);
        childlist.add(guide);

        gps = new ExpandableChildGps(context, settingsGroup, context.getString(R.string.settings_gps), appSettingsManager, AppSettingsManager.SETTING_LOCATION);
        childlist.add(gps);

        externalShutter = new ExpandableChildExternalShutter(context, settingsGroup, context.getString(R.string.settings_externalshutter), appSettingsManager, AppSettingsManager.SETTING_EXTERNALSHUTTER);
        childlist.add(externalShutter);

        rotationHack = new ExpandableChildOrientationHack(context, settingsGroup, context.getString(R.string.settings_orientatiohack), appSettingsManager, AppSettingsManager.SETTING_OrientationHack);
        childlist.add(rotationHack);

        Theme = new ExpandableChildTheme(context,activityV2, settingsGroup, context.getString(R.string.settings_theme), appSettingsManager, AppSettingsManager.SETTING_Theme);
        childlist.add(Theme);

        SaveToSD = new ExpandableChildSaveSD(context, settingsGroup, "Save to", appSettingsManager, AppSettingsManager.SETTING_EXTERNALSD);
        childlist.add(SaveToSD);

        settingsGroup.setItems(childlist);

        return settingsGroup;
    }


}
