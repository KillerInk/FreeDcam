package com.freedcam.ui.themesample.subfragments;

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

import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera2.camera.CameraUiWrapperApi2;
import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.apis.basecamera.camera.parameters.modes.ApiParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.ParameterExternalShutter;
import com.freedcam.ui.AbstractFragment;
import com.freedcam.ui.I_Activity;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.ui.I_swipe;
import com.freedcam.ui.themesample.views.menu.MenuItem;
import com.freedcam.ui.themesample.views.menu.MenuItemAEB;
import com.freedcam.ui.themesample.views.menu.MenuItemGPS;
import com.freedcam.ui.themesample.views.menu.MenuItemInterval;
import com.freedcam.ui.themesample.views.menu.MenuItemIntervalDuration;
import com.freedcam.ui.themesample.views.menu.MenuItemOrientationHack;
import com.freedcam.ui.themesample.views.menu.MenuItemSDSave;
import com.freedcam.ui.themesample.views.menu.MenuItemSaveCamParams;
import com.freedcam.ui.themesample.views.menu.MenuItemTheme;
import com.freedcam.ui.themesample.views.menu.MenuItemTimeLapseFrames;
import com.freedcam.ui.themesample.views.menu.MenuItemTimer;
import com.freedcam.ui.themesample.views.menu.MenuItemVideoHDR;
import com.freedcam.ui.themesample.views.menu.MenuItemVideoProfile;
import com.freedcam.ui.themesample.views.menu.MenuItem_VideoProfEditor;
import com.freedcam.ui.themesample.views.uichilds.UiSettingsChild;
import com.troop.freedcam.R;

/**
 * Created by troop on 15.06.2015.
 */
public class LeftMenuFragment extends AbstractFragment  implements Interfaces.I_MenuItemClick, I_swipe
{

    private final boolean DEBUG = false;
    private MenuItemTheme themeItem;
    private MenuItem bayerFormatItem;
    private MenuItem opcode;
    private MenuItem pictureSize;
    private MenuItemSDSave sdSave;
    private MenuItemGPS menuItemGPS;

    private MenuItemInterval menuItemInterval;
    private MenuItemIntervalDuration menuItemIntervalDuration;
    private MenuItemTimer menuItemTimer;

    private MenuItem_VideoProfEditor videoProfileEditor;

    private MenuItem guide;
    private MenuItem api;
    private MenuItem externalShutter;
    private MenuItemOrientationHack orientationHack;

    private MenuItem jpegQuality;
    private MenuItemSaveCamParams saveCamParams;

    private MenuItemVideoProfile videoProfile;
    private MenuItemVideoHDR videoHDR;
    private MenuItemTimeLapseFrames timeLapseFrames;

    private MenuItem VideoSize;

    private MenuItem PreviewSize;
    private MenuItem PreviewFormat;

    private MenuItem videoStabilization;

    private MenuItem horizont;
    private MenuItemAEB AEB1;
    private MenuItemAEB AEB2;
    private MenuItemAEB AEB3;

    private MenuItem matrixChooser;

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
    public void SetStuff(I_Activity i_activity, AppSettingsManager appSettingsManager) {
        super.SetStuff(i_activity, appSettingsManager);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        themeItem = (MenuItemTheme)view.findViewById(R.id.MenuItemTheme);

        videoProfileEditor = (MenuItem_VideoProfEditor)view.findViewById(R.id.MenuItem_VideoProfileEditor);

        pictureSize = (MenuItem)view.findViewById(R.id.MenuItemPicSize);

        sdSave = (MenuItemSDSave)view.findViewById(R.id.MenuItemSDSave);

        menuItemInterval = (MenuItemInterval)view.findViewById(R.id.MenuIntervalmeter);

        menuItemIntervalDuration = (MenuItemIntervalDuration)view.findViewById(R.id.MenuIntervalmeterDuration);

        menuItemTimer = (MenuItemTimer)view.findViewById(R.id.MenuTimer);

        menuItemGPS = (MenuItemGPS)view.findViewById(R.id.MenuItemGPS);

        guide = (MenuItem)view.findViewById(R.id.MenuItemGuide);

        api = (MenuItem)view.findViewById(R.id.MenuItemApi);

        externalShutter = (MenuItem)view.findViewById(R.id.MenuItemExternalShutter);

        orientationHack = (MenuItemOrientationHack)view.findViewById(R.id.MenuItemOrientationHack);

        jpegQuality = (MenuItem)view.findViewById(R.id.MenuItemJpegQuality);

        videoProfile = (MenuItemVideoProfile)view.findViewById(R.id.MenuItemVideoProfile);

        videoHDR = (MenuItemVideoHDR)view.findViewById(R.id.MenuItemVideHDR);

        VideoSize = (MenuItem) view.findViewById(R.id.MenuItemVideoSize);

        videoStabilization =  (MenuItem)view.findViewById(R.id.MenuItemVideoStabilization);

        timeLapseFrames = (MenuItemTimeLapseFrames) view.findViewById(R.id.MenuItemTimeLapseFrame);

        saveCamParams = (MenuItemSaveCamParams)view.findViewById(R.id.MenuItemSaveParams);
        PreviewFormat = (MenuItem)view.findViewById(R.id.MenuItemPreviewFormat);
        PreviewSize = (MenuItem)view.findViewById(R.id.MenuItemPreviewSize);
        horizont = (MenuItem)view.findViewById(R.id.MenuItemHorizont);

        AEB1 = (MenuItemAEB) view.findViewById(R.id.MenuItemAEB1);
        AEB2 = (MenuItemAEB) view.findViewById(R.id.MenuItemAEB2);
        AEB3 = (MenuItemAEB) view.findViewById(R.id.MenuItemAEB3);

        bayerFormatItem = (MenuItem)view.findViewById(R.id.MenuItemBayerFormat);

        opcode = (MenuItem)view.findViewById(R.id.MenuItemOpCode);

        matrixChooser = (MenuItem)view.findViewById(R.id.MenuItemMatrixChooser);

        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        settingsMenu =  (FrameLayout)getActivity().findViewById(R.id.settingsMenuHolder);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        settingsOpen = sharedPref.getBoolean(KEY_SETTINGSOPEN, false);
        leftholder = (LinearLayout) getActivity().findViewById(R.id.guideHolder);

    }

    @Override
    public void onResume() {
        super.onResume();
        setWrapper();
    }

    private void setWrapper()
    {
        if (themeItem == null || cameraUiWrapper == null)
            return;
        themeItem.SetStuff(i_activity,AppSettingsManager.SETTING_Theme,appSettingsManager);
        themeItem.SetParameter(cameraUiWrapper.camParametersHandler.ThemeList);
        themeItem.SetMenuItemListner(this);

        pictureSize.SetStuff(i_activity, AppSettingsManager.SETTING_PICTURESIZE,appSettingsManager);
        pictureSize.SetParameter(cameraUiWrapper.camParametersHandler.PictureSize);
        pictureSize.SetMenuItemListner(this);

        sdSave.SetStuff(i_activity, AppSettingsManager.SETTING_EXTERNALSD,appSettingsManager);
        sdSave.SetCameraUiWrapper(cameraUiWrapper);
        sdSave.SetMenuItemListner(this);

        menuItemInterval.SetStuff(i_activity, AppSettingsManager.SETTING_INTERVAL,appSettingsManager);
        menuItemInterval.SetCameraUIWrapper(cameraUiWrapper);
        menuItemInterval.SetMenuItemListner(this);

        menuItemIntervalDuration.SetStuff(i_activity, AppSettingsManager.SETTING_INTERVAL_DURATION,appSettingsManager);
        menuItemIntervalDuration.SetCameraUIWrapper(cameraUiWrapper);
        menuItemIntervalDuration.SetMenuItemListner(this);

        menuItemTimer.SetStuff(i_activity, AppSettingsManager.SETTING_TIMER,appSettingsManager);
        menuItemTimer.SetCameraUIWrapper(cameraUiWrapper);
        menuItemTimer.SetMenuItemListner(this);

        menuItemGPS.SetStuff(i_activity, AppSettingsManager.SETTING_LOCATION,appSettingsManager);
        menuItemGPS.SetCameraUIWrapper(cameraUiWrapper);
        menuItemGPS.SetMenuItemListner(this);

        guide.SetStuff(i_activity, AppSettingsManager.SETTING_GUIDE,appSettingsManager);
        guide.SetParameter(cameraUiWrapper.camParametersHandler.GuideList);
        guide.SetMenuItemListner(this);

        api.SetStuff(i_activity, null,appSettingsManager);
        api.SetParameter(new ApiParameter(i_activity,appSettingsManager));
        api.SetMenuItemListner(this);

        externalShutter.SetStuff(i_activity, null,appSettingsManager);
        externalShutter.SetParameter(new ParameterExternalShutter(appSettingsManager));
        externalShutter.SetMenuItemListner(this);

        orientationHack.SetStuff(i_activity, null,appSettingsManager);
        orientationHack.SetCameraUIWrapper(cameraUiWrapper);
        orientationHack.SetMenuItemListner(this);

        jpegQuality.SetStuff(i_activity, AppSettingsManager.SETTING_JPEGQUALITY,appSettingsManager);
        jpegQuality.SetParameter(cameraUiWrapper.camParametersHandler.JpegQuality);
        jpegQuality.SetMenuItemListner(this);

        videoProfile.SetStuff(i_activity, AppSettingsManager.SETTING_VIDEPROFILE,appSettingsManager);
        if (cameraUiWrapper.camParametersHandler.VideoProfiles != null)
            videoProfile.SetParameter(cameraUiWrapper.camParametersHandler.VideoProfiles);
        else if (cameraUiWrapper.camParametersHandler.VideoProfilesG3 != null)
            videoProfile.SetParameter(cameraUiWrapper.camParametersHandler.VideoProfilesG3);
        else {
            videoProfile.SetParameter(null);
        }
        videoProfile.SetMenuItemListner(this);

        videoHDR.SetStuff(i_activity, AppSettingsManager.SETTING_VIDEOHDR,appSettingsManager);
        videoHDR.SetParameter(cameraUiWrapper.camParametersHandler.VideoHDR);
        videoHDR.SetMenuItemListner(this);
        videoHDR.SetModulesToShow(cameraUiWrapper.moduleHandler.VideoModules, cameraUiWrapper.moduleHandler);
        ///////////////////////////   Highspeed Recording //////////////////////////////////////////

        VideoSize.SetStuff(i_activity, AppSettingsManager.SETTING_VIDEOSIZE,appSettingsManager);
        if (!(cameraUiWrapper instanceof CameraUiWrapper) && cameraUiWrapper.camParametersHandler.VideoSize != null && cameraUiWrapper.camParametersHandler.VideoSize.IsSupported()) {
            VideoSize.SetParameter(cameraUiWrapper.camParametersHandler.VideoSize);
            VideoSize.SetMenuItemListner(this);
            VideoSize.setVisibility(View.VISIBLE);
        }
        else
            VideoSize.setVisibility(View.GONE);

        videoStabilization.SetStuff(i_activity, AppSettingsManager.SETTING_VIDEOSTABILIZATION,appSettingsManager);
        videoStabilization.SetParameter(cameraUiWrapper.camParametersHandler.VideoStabilization);
        videoStabilization.SetMenuItemListner(this);

        ////////////////////////////////////////////////////////////////////////////////////////////

        if (cameraUiWrapper instanceof CameraUiWrapper) {

            timeLapseFrames.setVisibility(View.VISIBLE);
            timeLapseFrames.SetStuff(appSettingsManager);
            videoProfileEditor.setVisibility(View.VISIBLE);
        }
        else if (cameraUiWrapper instanceof CameraUiWrapperApi2)
        {
            timeLapseFrames.setVisibility(View.GONE);
            videoProfileEditor.setVisibility(View.VISIBLE);
        }
        else
        {
            timeLapseFrames.setVisibility(View.GONE);
            videoProfileEditor.setVisibility(View.GONE);
        }


        saveCamParams.setCameraUiWrapper(cameraUiWrapper);

        if (DEBUG)
        {
            PreviewFormat.SetStuff(i_activity, null,appSettingsManager);
            PreviewFormat.SetParameter(cameraUiWrapper.camParametersHandler.PreviewFormat);
            PreviewFormat.SetMenuItemListner(this);
            PreviewFormat.setVisibility(View.VISIBLE);
            PreviewSize.SetStuff(i_activity, null,appSettingsManager);
            PreviewSize.SetParameter(cameraUiWrapper.camParametersHandler.PreviewSize);
            PreviewSize.SetMenuItemListner(this);
            PreviewSize.setVisibility(View.VISIBLE);
        }
        else {
            PreviewFormat.setVisibility(View.GONE);
            PreviewSize.setVisibility(View.GONE);
        }

        horizont.SetStuff(i_activity, AppSettingsManager.SETTING_HORIZONT,appSettingsManager);
        horizont.SetParameter(cameraUiWrapper.camParametersHandler.Horizont);
        horizont.SetMenuItemListner(this);

        if(cameraUiWrapper instanceof CameraUiWrapper)
        {
            AEB1.SetStuff(appSettingsManager, AppSettingsManager.SETTING_AEB1);
            AEB1.SetCameraUIWrapper(cameraUiWrapper);
            AEB2.SetStuff(appSettingsManager, AppSettingsManager.SETTING_AEB2);
            AEB2.SetCameraUIWrapper(cameraUiWrapper);
            AEB3.SetStuff(appSettingsManager, AppSettingsManager.SETTING_AEB3);
            AEB3.SetCameraUIWrapper(cameraUiWrapper);
        }
        else
        {
            AEB1.setVisibility(View.GONE);
            AEB2.setVisibility(View.GONE);
            AEB3.setVisibility(View.GONE);
        }

        opcode.SetStuff(i_activity, "",appSettingsManager);
        opcode.SetParameter(cameraUiWrapper.camParametersHandler.opcode);
        opcode.SetMenuItemListner(this);

        bayerFormatItem.SetStuff(i_activity, AppSettingsManager.SETTTING_BAYERFORMAT,appSettingsManager);
        bayerFormatItem.SetParameter(cameraUiWrapper.camParametersHandler.bayerformat);
        bayerFormatItem.SetMenuItemListner(this);

        matrixChooser.SetStuff(i_activity, AppSettingsManager.SETTTING_CUSTOMMATRIX,appSettingsManager);
        matrixChooser.SetParameter(cameraUiWrapper.camParametersHandler.matrixChooser);
        matrixChooser.SetMenuItemListner(this);

        /*previewZoom.SetParameter(cameraUiWrapper.camParametersHandler.PreviewZoom);
        previewZoom.SetMenuItemListner(this);*/

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
