package troop.com.themesample.subfragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera2.CameraUiWrapperApi2;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.ApiParameter;
import com.troop.freedcam.i_camera.parameters.ParameterExternalShutter;
import com.troop.freedcam.ui.AbstractFragment;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_swipe;
import com.troop.freedcam.ui.SwipeMenuListner;

import troop.com.themesample.R;
import troop.com.themesample.views.menu.MenuItem;
import troop.com.themesample.views.menu.MenuItemAEB;
import troop.com.themesample.views.menu.MenuItemGPS;
import troop.com.themesample.views.menu.MenuItemInterval;
import troop.com.themesample.views.menu.MenuItemIntervalDuration;
import troop.com.themesample.views.menu.MenuItemOrientationHack;
import troop.com.themesample.views.menu.MenuItemSDSave;
import troop.com.themesample.views.menu.MenuItemSaveCamParams;
import troop.com.themesample.views.menu.MenuItemTheme;
import troop.com.themesample.views.menu.MenuItemTimeLapseFrames;
import troop.com.themesample.views.menu.MenuItemTimer;
import troop.com.themesample.views.menu.MenuItemVideoBitrate;
import troop.com.themesample.views.menu.MenuItemVideoProfile;
import troop.com.themesample.views.menu.MenuItem_VideoProfEditor;
import troop.com.themesample.views.uichilds.UiSettingsChild;

/**
 * Created by troop on 15.06.2015.
 */
public class LeftMenuFragment extends AbstractFragment  implements Interfaces.I_MenuItemClick, I_swipe
{

    final boolean DEBUG = false;
    private MenuItemTheme themeItem;
    //MenuItemBayerFormat bayerFormatItem;
    private troop.com.themesample.views.menu.MenuItem pictureSize;
    private MenuItemSDSave sdSave;
    private MenuItemGPS menuItemGPS;

    private MenuItemInterval menuItemInterval;
    private MenuItemIntervalDuration menuItemIntervalDuration;
    private MenuItemTimer menuItemTimer;

    private MenuItem_VideoProfEditor videoProfileEditor;

    private troop.com.themesample.views.menu.MenuItem guide;
    private troop.com.themesample.views.menu.MenuItem api;
    private troop.com.themesample.views.menu.MenuItem externalShutter;
    private MenuItemOrientationHack orientationHack;

    private troop.com.themesample.views.menu.MenuItem jpegQuality;
    private MenuItemSaveCamParams saveCamParams;

    private MenuItemVideoProfile videoProfile;
    private troop.com.themesample.views.menu.MenuItemVideoHDR videoHDR;
    private MenuItemTimeLapseFrames timeLapseFrames;

    private troop.com.themesample.views.menu.MenuItem VideoSize;

    private troop.com.themesample.views.menu.MenuItem PreviewSize;
    private troop.com.themesample.views.menu.MenuItem PreviewFormat;

    private troop.com.themesample.views.menu.MenuItem videoStabilization;

    private troop.com.themesample.views.menu.MenuItem horizont;
    private troop.com.themesample.views.menu.MenuItemAEB AEB1;
    private troop.com.themesample.views.menu.MenuItemAEB AEB2;
    private troop.com.themesample.views.menu.MenuItemAEB AEB3;

    private MenuItem previewZoom;

    private Interfaces.I_MenuItemClick onMenuItemClick;

    private ScrollView scrollView;
    private FrameLayout settingsMenu;
    private final String KEY_SETTINGSOPEN = "key_settingsopen";
    private SharedPreferences sharedPref;
    private boolean settingsOpen;
    private LinearLayout leftholder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.leftmenufragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        themeItem = (MenuItemTheme)view.findViewById(R.id.MenuItemTheme);
        themeItem.SetStuff(i_activity,AppSettingsManager.SETTING_Theme);

        videoProfileEditor = (MenuItem_VideoProfEditor)view.findViewById(R.id.MenuItem_VideoProfileEditor);

        pictureSize = (troop.com.themesample.views.menu.MenuItem)view.findViewById(R.id.MenuItemPicSize);
        pictureSize.SetStuff(i_activity, AppSettingsManager.SETTING_PICTURESIZE);

        sdSave = (MenuItemSDSave)view.findViewById(R.id.MenuItemSDSave);
        sdSave.SetStuff(i_activity, AppSettingsManager.SETTING_EXTERNALSD);

        menuItemInterval = (MenuItemInterval)view.findViewById(R.id.MenuIntervalmeter);
        menuItemInterval.SetStuff(i_activity, AppSettingsManager.SETTING_INTERVAL);

        menuItemIntervalDuration = (MenuItemIntervalDuration)view.findViewById(R.id.MenuIntervalmeterDuration);
        menuItemIntervalDuration.SetStuff(i_activity, AppSettingsManager.SETTING_INTERVAL_DURATION);

        menuItemTimer = (MenuItemTimer)view.findViewById(R.id.MenuTimer);
        menuItemTimer.SetStuff(i_activity, AppSettingsManager.SETTING_TIMER);

        menuItemGPS = (MenuItemGPS)view.findViewById(R.id.MenuItemGPS);
        menuItemGPS.SetStuff(i_activity, AppSettingsManager.SETTING_LOCATION);

        guide = (troop.com.themesample.views.menu.MenuItem)view.findViewById(R.id.MenuItemGuide);
        guide.SetStuff(i_activity, AppSettingsManager.SETTING_GUIDE);

        api = (troop.com.themesample.views.menu.MenuItem)view.findViewById(R.id.MenuItemApi);
        api.SetStuff(i_activity, null);

        externalShutter = (troop.com.themesample.views.menu.MenuItem)view.findViewById(R.id.MenuItemExternalShutter);
        externalShutter.SetStuff(i_activity, null);

        orientationHack = (MenuItemOrientationHack)view.findViewById(R.id.MenuItemOrientationHack);
        orientationHack.SetStuff(i_activity, null);

        jpegQuality = (troop.com.themesample.views.menu.MenuItem)view.findViewById(R.id.MenuItemJpegQuality);
        jpegQuality.SetStuff(i_activity, AppSettingsManager.SETTING_JPEGQUALITY);

        videoProfile = (MenuItemVideoProfile)view.findViewById(R.id.MenuItemVideoProfile);
        videoProfile.SetStuff(i_activity, AppSettingsManager.SETTING_VIDEPROFILE);

        videoHDR = (troop.com.themesample.views.menu.MenuItemVideoHDR)view.findViewById(R.id.MenuItemVideHDR);
        videoHDR.SetStuff(i_activity, AppSettingsManager.SETTING_VIDEOHDR);

        VideoSize = (MenuItem) view.findViewById(R.id.MenuItemVideoSize);
        VideoSize.SetStuff(i_activity, AppSettingsManager.SETTING_VIDEOSIZE);

        videoStabilization =  (MenuItem)view.findViewById(R.id.MenuItemVideoStabilization);
        videoStabilization.SetStuff(i_activity, AppSettingsManager.SETTING_VIDEOSTABILIZATION);

        timeLapseFrames = (MenuItemTimeLapseFrames) view.findViewById(troop.com.themesample.R.id.MenuItemTimeLapseFrame);
        timeLapseFrames.SetStuff(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME);

        saveCamParams = (MenuItemSaveCamParams)view.findViewById(R.id.MenuItemSaveParams);

        PreviewFormat = (MenuItem)view.findViewById(R.id.MenuItemPreviewFormat);

        PreviewSize = (MenuItem)view.findViewById(R.id.MenuItemPreviewSize);

        horizont = (troop.com.themesample.views.menu.MenuItem)view.findViewById(R.id.MenuItemHorizont);
        horizont.SetStuff(i_activity, AppSettingsManager.SETTING_HORIZONT);

        AEB1 = (MenuItemAEB) view.findViewById(R.id.MenuItemAEB1);
        AEB1.SetStuff(AppSettingsManager.APPSETTINGSMANAGER, AppSettingsManager.SETTING_AEB1);
        AEB2 = (MenuItemAEB) view.findViewById(R.id.MenuItemAEB2);
        AEB2.SetStuff(AppSettingsManager.APPSETTINGSMANAGER, AppSettingsManager.SETTING_AEB2);
        AEB3 = (MenuItemAEB) view.findViewById(R.id.MenuItemAEB3);
        AEB3.SetStuff(AppSettingsManager.APPSETTINGSMANAGER, AppSettingsManager.SETTING_AEB3);


        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        settingsMenu =  (FrameLayout)getActivity().findViewById(R.id.settingsMenuHolder);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        settingsOpen = sharedPref.getBoolean(KEY_SETTINGSOPEN, false);
        leftholder = (LinearLayout) getActivity().findViewById(R.id.guideHolder);

        previewZoom = (MenuItem)view.findViewById(R.id.MenuItemPreviewZoom);
        previewZoom.SetStuff(i_activity,AppSettingsManager.SETTINGS_PREVIEWZOOM);

        setWrapper();
    }

    private void setWrapper()
    {
        if (themeItem == null ||wrapper == null)
            return;
        themeItem.SetParameter(wrapper.camParametersHandler.ThemeList);
        themeItem.SetMenuItemListner(this);

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

        api.SetParameter(new ApiParameter(null, i_activity));
        api.SetMenuItemListner(this);

        externalShutter.SetParameter(new ParameterExternalShutter());
        externalShutter.SetMenuItemListner(this);

        orientationHack.SetCameraUIWrapper(wrapper);
        orientationHack.SetMenuItemListner(this);

        jpegQuality.SetParameter(wrapper.camParametersHandler.JpegQuality);
        jpegQuality.SetMenuItemListner(this);

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
            videoProfileEditor.setVisibility(View.VISIBLE);
        }
        else if (wrapper instanceof CameraUiWrapperApi2)
        {
            timeLapseFrames.setVisibility(View.GONE);
            videoProfileEditor.setVisibility(View.VISIBLE);
        }
        else
        {

        }

        saveCamParams.setCameraUiWrapper(wrapper);

        if (DEBUG)
        {
            PreviewFormat.SetStuff(i_activity, null);
            PreviewFormat.SetParameter(wrapper.camParametersHandler.PreviewFormat);
            PreviewFormat.SetMenuItemListner(this);
            PreviewFormat.setVisibility(View.VISIBLE);
            PreviewSize.SetStuff(i_activity, null);
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

        if(wrapper instanceof CameraUiWrapper) {
            AEB1.SetCameraUIWrapper(wrapper);
            AEB2.SetCameraUIWrapper(wrapper);
            AEB3.SetCameraUIWrapper(wrapper);
        }
        else {
            AEB1.setVisibility(View.GONE);
            AEB2.setVisibility(View.GONE);
            AEB3.setVisibility(View.GONE);
        }

        previewZoom.SetParameter(wrapper.camParametersHandler.PreviewZoom);
        previewZoom.SetMenuItemListner(this);

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

    @Override
    public void doLeftToRightSwipe() {

    }

    @Override
    public void doRightToLeftSwipe() {
        settingsOpen = false;
        sharedPref.edit().putBoolean(KEY_SETTINGSOPEN, settingsOpen).commit();
        float width = leftholder.getWidth();
        settingsMenu.animate().translationX(-width).setDuration(300);
    }

    @Override
    public void doTopToBottomSwipe() {

    }

    @Override
    public void doBottomToTopSwipe() {

    }

    @Override
    public void onClick(int x, int y) {

    }

    @Override
    public void onMotionEvent(MotionEvent event) {

    }
}
