/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedcam.ui.themesample.subfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freedcam.apis.basecamera.parameters.modes.ApiParameter;
import com.freedcam.apis.basecamera.parameters.modes.ParameterExternalShutter;
import com.freedcam.apis.camera1.Camera1Fragment;
import com.freedcam.ui.AbstractFragment;
import com.freedcam.ui.I_Activity;
import com.freedcam.ui.themesample.subfragments.Interfaces.I_MenuItemClick;
import com.freedcam.ui.themesample.views.menu.MenuItem;
import com.freedcam.ui.themesample.views.menu.MenuItemAEB;
import com.freedcam.ui.themesample.views.menu.MenuItemGPS;
import com.freedcam.ui.themesample.views.menu.MenuItemInterval;
import com.freedcam.ui.themesample.views.menu.MenuItemIntervalDuration;
import com.freedcam.ui.themesample.views.menu.MenuItemOrientationHack;
import com.freedcam.ui.themesample.views.menu.MenuItemSDSave;
import com.freedcam.ui.themesample.views.menu.MenuItemSaveCamParams;
import com.freedcam.ui.themesample.views.menu.MenuItemTimeLapseFrames;
import com.freedcam.ui.themesample.views.menu.MenuItemTimer;
import com.freedcam.ui.themesample.views.menu.MenuItemVideoHDR;
import com.freedcam.ui.themesample.views.menu.MenuItemVideoProfile;
import com.freedcam.ui.themesample.views.menu.MenuItem_VideoProfEditor;
import com.freedcam.ui.themesample.views.uichilds.UiSettingsChild;
import com.freedcam.utils.AppSettingsManager;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

/**
 * Created by troop on 15.06.2015.
 */
public class LeftMenuFragment extends AbstractFragment  implements I_MenuItemClick
{
    private final String TAG = LeftMenuFragment.class.getSimpleName();
    private final boolean DEBUG = false;
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

    private MenuItem imageStackMode;

    private I_MenuItemClick onMenuItemClick;

    public static LeftMenuFragment GetInstance(I_Activity i_activity, AppSettingsManager appSettingsManager)
    {
        LeftMenuFragment s = new LeftMenuFragment();
        s.i_activity = i_activity;
        s.appSettingsManager = appSettingsManager;
        return s;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(layout.leftmenufragment, container, false);

        videoProfileEditor = (MenuItem_VideoProfEditor)view.findViewById(id.MenuItem_VideoProfileEditor);

        pictureSize = (MenuItem)view.findViewById(id.MenuItemPicSize);

        sdSave = (MenuItemSDSave)view.findViewById(id.MenuItemSDSave);

        menuItemInterval = (MenuItemInterval)view.findViewById(id.MenuIntervalmeter);

        menuItemIntervalDuration = (MenuItemIntervalDuration)view.findViewById(id.MenuIntervalmeterDuration);

        menuItemTimer = (MenuItemTimer)view.findViewById(id.MenuTimer);

        menuItemGPS = (MenuItemGPS)view.findViewById(id.MenuItemGPS);

        guide = (MenuItem)view.findViewById(id.MenuItemGuide);

        api = (MenuItem)view.findViewById(id.MenuItemApi);

        externalShutter = (MenuItem)view.findViewById(id.MenuItemExternalShutter);

        orientationHack = (MenuItemOrientationHack)view.findViewById(id.MenuItemOrientationHack);

        jpegQuality = (MenuItem)view.findViewById(id.MenuItemJpegQuality);

        videoProfile = (MenuItemVideoProfile)view.findViewById(id.MenuItemVideoProfile);

        videoHDR = (MenuItemVideoHDR)view.findViewById(id.MenuItemVideHDR);

        VideoSize = (MenuItem) view.findViewById(id.MenuItemVideoSize);

        videoStabilization =  (MenuItem)view.findViewById(id.MenuItemVideoStabilization);

        timeLapseFrames = (MenuItemTimeLapseFrames) view.findViewById(id.MenuItemTimeLapseFrame);

        saveCamParams = (MenuItemSaveCamParams)view.findViewById(id.MenuItemSaveParams);
        PreviewFormat = (MenuItem)view.findViewById(id.MenuItemPreviewFormat);
        PreviewSize = (MenuItem)view.findViewById(id.MenuItemPreviewSize);
        horizont = (MenuItem)view.findViewById(id.MenuItemHorizont);

        AEB1 = (MenuItemAEB) view.findViewById(id.MenuItemAEB1);
        AEB2 = (MenuItemAEB) view.findViewById(id.MenuItemAEB2);
        AEB3 = (MenuItemAEB) view.findViewById(id.MenuItemAEB3);

        bayerFormatItem = (MenuItem)view.findViewById(id.MenuItemBayerFormat);

        opcode = (MenuItem)view.findViewById(id.MenuItemOpCode);

        matrixChooser = (MenuItem)view.findViewById(id.MenuItemMatrixChooser);

        imageStackMode = (MenuItem)view.findViewById(id.MenuItemImageStack);
        setCameraUiWrapperToUi();
        return view;
    }

    @Override
    protected void setCameraUiWrapperToUi()
    {
        if (cameraUiWrapper == null)
            return;
        pictureSize.SetStuff(i_activity, AppSettingsManager.SETTING_PICTURESIZE, appSettingsManager);
        pictureSize.SetParameter(cameraUiWrapper.GetParameterHandler().PictureSize);
        pictureSize.SetMenuItemListner(this);

        sdSave.SetStuff(i_activity, AppSettingsManager.SETTING_EXTERNALSD, appSettingsManager);
        sdSave.SetCameraUiWrapper(cameraUiWrapper);
        sdSave.SetMenuItemListner(this);

        menuItemInterval.SetStuff(i_activity, AppSettingsManager.SETTING_INTERVAL, appSettingsManager);
        menuItemInterval.SetCameraUIWrapper(cameraUiWrapper);
        menuItemInterval.SetMenuItemListner(this);

        menuItemIntervalDuration.SetStuff(i_activity, AppSettingsManager.SETTING_INTERVAL_DURATION, appSettingsManager);
        menuItemIntervalDuration.SetCameraUIWrapper(cameraUiWrapper);
        menuItemIntervalDuration.SetMenuItemListner(this);

        menuItemTimer.SetStuff(i_activity, AppSettingsManager.SETTING_TIMER, appSettingsManager);
        menuItemTimer.SetCameraUIWrapper(cameraUiWrapper);
        menuItemTimer.SetMenuItemListner(this);

        menuItemGPS.SetStuff(i_activity, AppSettingsManager.SETTING_LOCATION, appSettingsManager);
        menuItemGPS.SetCameraUIWrapper(cameraUiWrapper);
        menuItemGPS.SetMenuItemListner(this);

        guide.SetStuff(i_activity, AppSettingsManager.SETTING_GUIDE, appSettingsManager);
        guide.SetParameter(cameraUiWrapper.GetParameterHandler().GuideList);
        guide.SetMenuItemListner(this);

        api.SetStuff(i_activity, null, appSettingsManager);
        api.SetParameter(new ApiParameter(i_activity, appSettingsManager));
        api.SetMenuItemListner(this);

        externalShutter.SetStuff(i_activity, null, appSettingsManager);
        externalShutter.SetParameter(new ParameterExternalShutter(appSettingsManager));
        externalShutter.SetMenuItemListner(this);

        orientationHack.SetStuff(i_activity, null, appSettingsManager);
        orientationHack.SetCameraUIWrapper(cameraUiWrapper);
        orientationHack.SetMenuItemListner(this);

        jpegQuality.SetStuff(i_activity, AppSettingsManager.SETTING_JPEGQUALITY, appSettingsManager);
        jpegQuality.SetParameter(cameraUiWrapper.GetParameterHandler().JpegQuality);
        jpegQuality.SetMenuItemListner(this);

        videoProfile.SetStuff(i_activity, AppSettingsManager.SETTING_VIDEPROFILE, appSettingsManager);

        if (cameraUiWrapper.GetParameterHandler().VideoProfiles != null)
            videoProfile.SetParameter(cameraUiWrapper.GetParameterHandler().VideoProfiles);
        videoProfile.SetMenuItemListner(this);

        videoHDR.SetStuff(i_activity, AppSettingsManager.SETTING_VIDEOHDR, appSettingsManager);
        videoHDR.SetParameter(cameraUiWrapper.GetParameterHandler().VideoHDR);
        videoHDR.SetMenuItemListner(this);
        ///////////////////////////   Highspeed Recording //////////////////////////////////////////

        VideoSize.SetStuff(i_activity, AppSettingsManager.SETTING_VIDEOSIZE, appSettingsManager);
        if (!(cameraUiWrapper instanceof Camera1Fragment) && cameraUiWrapper.GetParameterHandler().VideoSize != null && cameraUiWrapper.GetParameterHandler().VideoSize.IsSupported()) {
            VideoSize.SetParameter(cameraUiWrapper.GetParameterHandler().VideoSize);
            VideoSize.SetMenuItemListner(this);
            VideoSize.setVisibility(View.VISIBLE);
        }
        else
            VideoSize.setVisibility(View.GONE);

        videoStabilization.SetStuff(i_activity, AppSettingsManager.SETTING_VIDEOSTABILIZATION, appSettingsManager);
        videoStabilization.SetParameter(cameraUiWrapper.GetParameterHandler().VideoStabilization);
        videoStabilization.SetMenuItemListner(this);

        ////////////////////////////////////////////////////////////////////////////////////////////

        if (cameraUiWrapper instanceof Camera1Fragment) {

            timeLapseFrames.setVisibility(View.VISIBLE);
            timeLapseFrames.SetStuff(appSettingsManager);
            videoProfileEditor.setVisibility(View.VISIBLE);
        }
        else if (cameraUiWrapper instanceof Camera1Fragment)
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
            PreviewFormat.SetStuff(i_activity, null, appSettingsManager);
            PreviewFormat.SetParameter(cameraUiWrapper.GetParameterHandler().PreviewFormat);
            PreviewFormat.SetMenuItemListner(this);
            PreviewFormat.setVisibility(View.VISIBLE);
            PreviewSize.SetStuff(i_activity, null, appSettingsManager);
            PreviewSize.SetParameter(cameraUiWrapper.GetParameterHandler().PreviewSize);
            PreviewSize.SetMenuItemListner(this);
            PreviewSize.setVisibility(View.VISIBLE);
        }
        else {
            PreviewFormat.setVisibility(View.GONE);
            PreviewSize.setVisibility(View.GONE);
        }

        horizont.SetStuff(i_activity, AppSettingsManager.SETTING_HORIZONT, appSettingsManager);
        horizont.SetParameter(cameraUiWrapper.GetParameterHandler().Horizont);
        horizont.SetMenuItemListner(this);

        if(cameraUiWrapper instanceof Camera1Fragment)
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

        opcode.SetStuff(i_activity, "", appSettingsManager);
        opcode.SetParameter(cameraUiWrapper.GetParameterHandler().opcode);
        opcode.SetMenuItemListner(this);

        bayerFormatItem.SetStuff(i_activity, AppSettingsManager.SETTTING_BAYERFORMAT, appSettingsManager);
        bayerFormatItem.SetParameter(cameraUiWrapper.GetParameterHandler().bayerformat);
        bayerFormatItem.SetMenuItemListner(this);

        matrixChooser.SetStuff(i_activity, AppSettingsManager.SETTTING_CUSTOMMATRIX, appSettingsManager);
        matrixChooser.SetParameter(cameraUiWrapper.GetParameterHandler().matrixChooser);
        matrixChooser.SetMenuItemListner(this);

        imageStackMode.SetStuff(i_activity, AppSettingsManager.SETTING_STACKMODE, appSettingsManager);
        imageStackMode.SetParameter(cameraUiWrapper.GetParameterHandler().imageStackMode);
        imageStackMode.SetMenuItemListner(this);
    }

    public void SetMenuItemClickListner(I_MenuItemClick menuItemClick)
    {
        onMenuItemClick = menuItemClick;
    }

    @Override
    public void onMenuItemClick(UiSettingsChild item, boolean fromLeftFragment) {
        onMenuItemClick.onMenuItemClick(item, true);
    }

}
