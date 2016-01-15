package troop.com.themesample.subfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.ApiParameter;
import com.troop.freedcam.i_camera.parameters.ParameterExternalShutter;
import com.troop.freedcam.ui.AbstractFragment;
import com.troop.freedcam.ui.AppSettingsManager;

import troop.com.themesample.R;
import troop.com.themesample.views.menu.MenuItem;
import troop.com.themesample.views.menu.MenuItemBayerFormat;
import troop.com.themesample.views.menu.MenuItemGPS;
import troop.com.themesample.views.menu.MenuItemInterval;
import troop.com.themesample.views.menu.MenuItemIntervalDuration;
import troop.com.themesample.views.menu.MenuItemOrientationHack;
import troop.com.themesample.views.menu.MenuItemSDSave;
import troop.com.themesample.views.menu.MenuItemSaveCamParams;
import troop.com.themesample.views.menu.MenuItemTheme;
import troop.com.themesample.views.menu.MenuItemTimeLapseFrames;
import troop.com.themesample.views.menu.MenuItemTimer;
import troop.com.themesample.views.menu.MenuItemVideoProfile;
import troop.com.themesample.views.uichilds.UiSettingsChild;

/**
 * Created by troop on 15.06.2015.
 */
public class LeftMenuFragment extends AbstractFragment  implements Interfaces.I_MenuItemClick
{

    final boolean DEBUG = false;
    MenuItemTheme themeItem;
    MenuItemBayerFormat bayerFormatItem;
    troop.com.themesample.views.menu.MenuItem pictureSize;
    MenuItemSDSave sdSave;
    MenuItemGPS menuItemGPS;

    MenuItemInterval menuItemInterval;
    MenuItemIntervalDuration menuItemIntervalDuration;
    MenuItemTimer menuItemTimer;

    troop.com.themesample.views.menu.MenuItem guide;
    troop.com.themesample.views.menu.MenuItem api;
    troop.com.themesample.views.menu.MenuItem externalShutter;
    MenuItemOrientationHack orientationHack;

    troop.com.themesample.views.menu.MenuItem jpegQuality;
    //troop.com.themesample.views.menu.MenuItem histogram;
    //troop.com.themesample.views.menu.MenuItem focuspeak;
    MenuItemSaveCamParams saveCamParams;

    troop.com.themesample.views.menu.MenuItem aeBracket;

    MenuItemVideoProfile videoProfile;
    troop.com.themesample.views.menu.MenuItemVideoHDR videoHDR;
    troop.com.themesample.views.menu.MenuItemHighFramerateVideo HighFramerateVideo;
    MenuItemTimeLapseFrames timeLapseFrames;

    troop.com.themesample.views.menu.MenuItem OverrideVideoProfile;
    troop.com.themesample.views.menu.MenuItem VideoSize;
    troop.com.themesample.views.menu.MenuItem VideoHeight;
    troop.com.themesample.views.menu.MenuItem VideoFps;
    troop.com.themesample.views.menu.MenuItem VideoBitrate;
    troop.com.themesample.views.menu.MenuItem VideoHFR;
    troop.com.themesample.views.menu.MenuItem VideoHSR;
    troop.com.themesample.views.menu.MenuItem VideoCodec;
    troop.com.themesample.views.menu.MenuItem AudioBitrate;
    troop.com.themesample.views.menu.MenuItem AudioSampleRate;
    troop.com.themesample.views.menu.MenuItem AudioCodec;

    troop.com.themesample.views.menu.MenuItem PreviewSize;
    troop.com.themesample.views.menu.MenuItem PreviewFormat;

    troop.com.themesample.views.menu.MenuItem videoStabilization;
    troop.com.themesample.views.menu.MenuItem HighSpeedVideo;

    troop.com.themesample.views.menu.MenuItem horizont;

    Interfaces.I_MenuItemClick onMenuItemClick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.leftmenufragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        themeItem = (MenuItemTheme)view.findViewById(R.id.MenuItemTheme);
        themeItem.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_Theme);

        bayerFormatItem = (MenuItemBayerFormat) view.findViewById(R.id.MenuItemBayerFormat);
        bayerFormatItem.SetStuff(i_activity, appSettingsManager, "");

        pictureSize = (troop.com.themesample.views.menu.MenuItem)view.findViewById(R.id.MenuItemPicSize);
        pictureSize.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_PICTURESIZE);

        sdSave = (MenuItemSDSave)view.findViewById(R.id.MenuItemSDSave);
        sdSave.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_EXTERNALSD);

        menuItemInterval = (MenuItemInterval)view.findViewById(R.id.MenuIntervalmeter);
        menuItemInterval.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_INTERVAL);

        menuItemIntervalDuration = (MenuItemIntervalDuration)view.findViewById(R.id.MenuIntervalmeterDuration);
        menuItemIntervalDuration.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_INTERVAL_DURATION);

        menuItemTimer = (MenuItemTimer)view.findViewById(R.id.MenuTimer);
        menuItemTimer.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_TIMER);

        menuItemGPS = (MenuItemGPS)view.findViewById(R.id.MenuItemGPS);
        menuItemGPS.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_LOCATION);

        guide = (troop.com.themesample.views.menu.MenuItem)view.findViewById(R.id.MenuItemGuide);
        guide.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_GUIDE);

        api = (troop.com.themesample.views.menu.MenuItem)view.findViewById(R.id.MenuItemApi);
        api.SetStuff(i_activity, appSettingsManager, null);

        externalShutter = (troop.com.themesample.views.menu.MenuItem)view.findViewById(R.id.MenuItemExternalShutter);
        externalShutter.SetStuff(i_activity, appSettingsManager, null);

        orientationHack = (MenuItemOrientationHack)view.findViewById(R.id.MenuItemOrientationHack);
        orientationHack.SetStuff(i_activity, appSettingsManager, null);

        jpegQuality = (troop.com.themesample.views.menu.MenuItem)view.findViewById(R.id.MenuItemJpegQuality);
        jpegQuality.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_JPEGQUALITY);

        aeBracket = (troop.com.themesample.views.menu.MenuItem)view.findViewById(R.id.MenuItemAeBracket);
        aeBracket.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_AEBRACKETACTIVE);

        videoProfile = (MenuItemVideoProfile)view.findViewById(R.id.MenuItemVideoProfile);
        videoProfile.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_VIDEPROFILE);

        videoHDR = (troop.com.themesample.views.menu.MenuItemVideoHDR)view.findViewById(R.id.MenuItemVideHDR);
        videoHDR.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_VIDEOHDR);

        HighFramerateVideo = (troop.com.themesample.views.menu.MenuItemHighFramerateVideo)view.findViewById(R.id.MenuItemHighFramerateVideo);
        HighFramerateVideo.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_HighFramerateVideo);

        HighSpeedVideo = (troop.com.themesample.views.menu.MenuItem)view.findViewById(R.id.MenuItemHighSpeedVideo);
        HighSpeedVideo.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_HighSpeedVideo);

        VideoSize = (MenuItem) view.findViewById(R.id.MenuItemVideoSize);
        VideoSize.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_VIDEOSIZE);

        videoStabilization =  (MenuItem)view.findViewById(R.id.MenuItemVideoStabilization);
        videoStabilization.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_VIDEOSTABILIZATION);

        timeLapseFrames = (MenuItemTimeLapseFrames) view.findViewById(troop.com.themesample.R.id.MenuItemTimeLapseFrame);
        timeLapseFrames.SetStuff(appSettingsManager, AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME);

        saveCamParams = (MenuItemSaveCamParams)view.findViewById(R.id.MenuItemSaveParams);

        PreviewFormat = (MenuItem)view.findViewById(R.id.MenuItemPreviewFormat);

        PreviewSize = (MenuItem)view.findViewById(R.id.MenuItemPreviewSize);

        horizont = (troop.com.themesample.views.menu.MenuItem)view.findViewById(R.id.MenuItemHorizont);
        horizont.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_HORIZONT);

        setWrapper();
    }

    private void setWrapper()
    {
        if (themeItem == null)
            return;
        themeItem.SetParameter(wrapper.camParametersHandler.ThemeList);
        themeItem.SetMenuItemListner(this);

        if(wrapper instanceof CameraUiWrapper) {
            bayerFormatItem.SetParameter(wrapper.camParametersHandler.PictureFormat);
            bayerFormatItem.SetMenuItemListner(this);
            bayerFormatItem.setVisibility(View.VISIBLE);
        }
        else
            bayerFormatItem.setVisibility(View.GONE);

        pictureSize.SetParameter(wrapper.camParametersHandler.PictureSize);
        pictureSize.SetMenuItemListner(this);

        sdSave.SetCameraUiWrapper(wrapper);
        sdSave.SetMenuItemListner(this);

        menuItemInterval.SetCameraUIWrapper(wrapper);
        menuItemInterval.SetMenuItemListner(this);

        menuItemIntervalDuration.SetCameraUIWrapper(wrapper);
        menuItemIntervalDuration.SetMenuItemListner(this);

        menuItemTimer.SetCameraUIWrapper(wrapper);
        menuItemTimer.SetMenuItemListner(this);

        menuItemGPS.SetCameraUIWrapper(wrapper);
        menuItemGPS.SetMenuItemListner(this);

        guide.SetParameter(wrapper.camParametersHandler.GuideList);
        guide.SetMenuItemListner(this);

        api.SetParameter(new ApiParameter(null, i_activity, appSettingsManager));
        api.SetMenuItemListner(this);

        externalShutter.SetParameter(new ParameterExternalShutter(appSettingsManager));
        externalShutter.SetMenuItemListner(this);

        orientationHack.SetCameraUIWrapper(wrapper);
        orientationHack.SetMenuItemListner(this);

        jpegQuality.SetParameter(wrapper.camParametersHandler.JpegQuality);
        jpegQuality.SetMenuItemListner(this);

        aeBracket.SetParameter(wrapper.camParametersHandler.AE_Bracket);
        aeBracket.SetMenuItemListner(this);

        if (wrapper.camParametersHandler.VideoProfiles != null)
            videoProfile.SetParameter(wrapper.camParametersHandler.VideoProfiles);
        else if (wrapper.camParametersHandler.VideoProfilesG3 != null)
            videoProfile.SetParameter(wrapper.camParametersHandler.VideoProfilesG3);
        else {
            videoProfile.SetParameter(null);

        }
        videoProfile.SetMenuItemListner(this);

        videoHDR.SetParameter(wrapper.camParametersHandler.VideoHDR);
        videoHDR.SetMenuItemListner(this);
        videoHDR.SetModulesToShow(wrapper.moduleHandler.VideoModules, wrapper.moduleHandler);
        ///////////////////////////   Highspeed Recording //////////////////////////////////////////

        HighFramerateVideo.SetParameter(wrapper.camParametersHandler.VideoHighFramerateVideo);
        HighFramerateVideo.SetMenuItemListner(this);
        HighFramerateVideo.SetModulesToShow(wrapper.moduleHandler.VideoModules, wrapper.moduleHandler);

        HighSpeedVideo.SetParameter(wrapper.camParametersHandler.VideoHighSpeedVideo);
        HighSpeedVideo.SetMenuItemListner(this);

        if (!(wrapper instanceof CameraUiWrapper) && wrapper.camParametersHandler.VideoSize != null && wrapper.camParametersHandler.VideoSize.IsSupported()) {

            VideoSize.SetParameter(wrapper.camParametersHandler.VideoSize);
            VideoSize.SetMenuItemListner(this);
            VideoSize.setVisibility(View.VISIBLE);
        }
        else
            VideoSize.setVisibility(View.GONE);

        videoStabilization.SetParameter(wrapper.camParametersHandler.VideoStabilization);
        videoStabilization.SetMenuItemListner(this);

        ////////////////////////////////////////////////////////////////////////////////////////////

        if (wrapper instanceof CameraUiWrapper) {

            timeLapseFrames.setVisibility(View.VISIBLE);
        }
        else
            timeLapseFrames.setVisibility(View.GONE);

        saveCamParams.setCameraUiWrapper(wrapper);

        if (DEBUG)
        {
            PreviewFormat.SetStuff(i_activity, appSettingsManager, null);
            PreviewFormat.SetParameter(wrapper.camParametersHandler.PreviewFormat);
            PreviewFormat.SetMenuItemListner(this);
            PreviewFormat.setVisibility(View.VISIBLE);
            PreviewSize.SetStuff(i_activity, appSettingsManager, null);
            PreviewSize.SetParameter(wrapper.camParametersHandler.PreviewSize);
            PreviewSize.SetMenuItemListner(this);
            PreviewSize.setVisibility(View.VISIBLE);
        }
        else {
            PreviewFormat.setVisibility(View.GONE);
            PreviewSize.setVisibility(View.GONE);
        }


        horizont.SetParameter(wrapper.camParametersHandler.Horizont);
        horizont.SetMenuItemListner(this);


    }

    public void SetMenuItemClickListner(Interfaces.I_MenuItemClick menuItemClick)
    {
        this.onMenuItemClick = menuItemClick;
    }



    @Override
    public void onMenuItemClick(UiSettingsChild item, boolean fromLeftFragment) {
        onMenuItemClick.onMenuItemClick(item, true);
    }

    @Override
    public void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper) {
        super.SetCameraUIWrapper(wrapper);
            setWrapper();
    }
}
