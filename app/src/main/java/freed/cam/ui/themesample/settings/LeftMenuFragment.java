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

package freed.cam.ui.themesample.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.parameters.modes.ApiParameter;
import freed.cam.apis.basecamera.parameters.modes.ParameterExternalShutter;
import freed.cam.apis.camera1.Camera1Fragment;
import freed.cam.apis.camera1.ModuleHandler;
import freed.cam.ui.themesample.AbstractFragment;
import freed.cam.ui.themesample.SettingsChildAbstract.SettingsChildClick;
import freed.cam.ui.themesample.cameraui.childs.UiSettingsChild;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenu;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuAEB;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuGPS;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuInterval;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuIntervalDuration;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuOrientationHack;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuSDSave;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuSaveCamParams;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuTimer;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuVideoHDR;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuVideoProfile;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenu_VideoProfEditor;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuimeLapseFrames;
import freed.utils.AppSettingsManager;

/**
 * Created by troop on 15.06.2015.
 */
public class LeftMenuFragment extends AbstractFragment  implements SettingsChildClick
{
    private final String TAG = LeftMenuFragment.class.getSimpleName();
    private final boolean DEBUG = false;
    private SettingsChildMenu bayerFormatItem;
    private SettingsChildMenu opcode;
    private SettingsChildMenu pictureSize;
    private SettingsChildMenuSDSave sdSave;
    private SettingsChildMenuGPS menuItemGPS;

    private SettingsChildMenuInterval menuItemInterval;
    private SettingsChildMenuIntervalDuration menuItemIntervalDuration;
    private SettingsChildMenuTimer menuItemTimer;

    private SettingsChildMenu_VideoProfEditor videoProfileEditor;

    private SettingsChildMenu guide;
    private SettingsChildMenu api;
    private SettingsChildMenu externalShutter;
    private SettingsChildMenuOrientationHack orientationHack;

    private SettingsChildMenu jpegQuality;
    private SettingsChildMenuSaveCamParams saveCamParams;

    private SettingsChildMenuVideoProfile videoProfile;
    private SettingsChildMenuVideoHDR videoHDR;
    private SettingsChildMenuimeLapseFrames timeLapseFrames;

    private SettingsChildMenu VideoSize;

    private SettingsChildMenu PreviewSize;
    private SettingsChildMenu PreviewFormat;

    private SettingsChildMenu videoStabilization;

    private SettingsChildMenu horizont;
    private SettingsChildMenuAEB AEB1;
    private SettingsChildMenuAEB AEB2;
    private SettingsChildMenuAEB AEB3;

    private SettingsChildMenu matrixChooser;

    private SettingsChildMenu imageStackMode;

    private SettingsChildClick onMenuItemClick;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        fragment_activityInterface = (ActivityInterface)getActivity();
        View view = inflater.inflate(layout.settings_leftmenufragment, container, false);

        videoProfileEditor = (SettingsChildMenu_VideoProfEditor)view.findViewById(id.MenuItem_VideoProfileEditor);

        pictureSize = (SettingsChildMenu)view.findViewById(id.MenuItemPicSize);

        sdSave = (SettingsChildMenuSDSave)view.findViewById(id.MenuItemSDSave);

        menuItemInterval = (SettingsChildMenuInterval)view.findViewById(id.MenuIntervalmeter);

        menuItemIntervalDuration = (SettingsChildMenuIntervalDuration)view.findViewById(id.MenuIntervalmeterDuration);

        menuItemTimer = (SettingsChildMenuTimer)view.findViewById(id.MenuTimer);

        menuItemGPS = (SettingsChildMenuGPS)view.findViewById(id.MenuItemGPS);

        guide = (SettingsChildMenu)view.findViewById(id.MenuItemGuide);

        api = (SettingsChildMenu)view.findViewById(id.MenuItemApi);

        externalShutter = (SettingsChildMenu)view.findViewById(id.MenuItemExternalShutter);

        orientationHack = (SettingsChildMenuOrientationHack)view.findViewById(id.MenuItemOrientationHack);

        jpegQuality = (SettingsChildMenu)view.findViewById(id.MenuItemJpegQuality);

        videoProfile = (SettingsChildMenuVideoProfile)view.findViewById(id.MenuItemVideoProfile);

        videoHDR = (SettingsChildMenuVideoHDR)view.findViewById(id.MenuItemVideHDR);

        VideoSize = (SettingsChildMenu) view.findViewById(id.MenuItemVideoSize);

        videoStabilization =  (SettingsChildMenu)view.findViewById(id.MenuItemVideoStabilization);

        timeLapseFrames = (SettingsChildMenuimeLapseFrames) view.findViewById(id.MenuItemTimeLapseFrame);

        saveCamParams = (SettingsChildMenuSaveCamParams)view.findViewById(id.MenuItemSaveParams);
        PreviewFormat = (SettingsChildMenu)view.findViewById(id.MenuItemPreviewFormat);
        PreviewSize = (SettingsChildMenu)view.findViewById(id.MenuItemPreviewSize);
        horizont = (SettingsChildMenu)view.findViewById(id.MenuItemHorizont);

        AEB1 = (SettingsChildMenuAEB) view.findViewById(id.MenuItemAEB1);
        AEB2 = (SettingsChildMenuAEB) view.findViewById(id.MenuItemAEB2);
        AEB3 = (SettingsChildMenuAEB) view.findViewById(id.MenuItemAEB3);

        bayerFormatItem = (SettingsChildMenu)view.findViewById(id.MenuItemBayerFormat);

        opcode = (SettingsChildMenu)view.findViewById(id.MenuItemOpCode);

        matrixChooser = (SettingsChildMenu)view.findViewById(id.MenuItemMatrixChooser);

        imageStackMode = (SettingsChildMenu)view.findViewById(id.MenuItemImageStack);
        setCameraUiWrapperToUi();
        return view;
    }

    @Override
    protected void setCameraUiWrapperToUi()
    {
        if (cameraUiWrapper == null)
            return;
        pictureSize.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_PICTURESIZE);
        pictureSize.SetParameter(cameraUiWrapper.GetParameterHandler().PictureSize);
        pictureSize.SetUiItemClickListner(this);

        sdSave.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_EXTERNALSD);
        sdSave.SetCameraUiWrapper(cameraUiWrapper);
        sdSave.SetUiItemClickListner(this);

        menuItemInterval.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_INTERVAL);
        menuItemInterval.SetCameraUIWrapper(cameraUiWrapper);
        menuItemInterval.SetUiItemClickListner(this);

        menuItemIntervalDuration.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_INTERVAL_DURATION);
        menuItemIntervalDuration.SetCameraUIWrapper(cameraUiWrapper);
        menuItemIntervalDuration.SetUiItemClickListner(this);

        menuItemTimer.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_TIMER);
        menuItemTimer.SetCameraUIWrapper(cameraUiWrapper);
        menuItemTimer.SetUiItemClickListner(this);

        menuItemGPS.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_LOCATION);
        menuItemGPS.SetCameraUIWrapper(cameraUiWrapper);
        menuItemGPS.SetUiItemClickListner(this);

        guide.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_GUIDE);
        guide.SetParameter(cameraUiWrapper.GetParameterHandler().GuideList);
        guide.SetUiItemClickListner(this);

        api.SetStuff(fragment_activityInterface, null);
        api.SetParameter(new ApiParameter(fragment_activityInterface));
        api.SetUiItemClickListner(this);

        externalShutter.SetStuff(fragment_activityInterface, null);
        externalShutter.SetParameter(new ParameterExternalShutter(fragment_activityInterface.getAppSettings()));
        externalShutter.SetUiItemClickListner(this);

        orientationHack.SetStuff(fragment_activityInterface, null);
        orientationHack.SetCameraUIWrapper(cameraUiWrapper);
        orientationHack.SetUiItemClickListner(this);

        jpegQuality.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_JPEGQUALITY);
        jpegQuality.SetParameter(cameraUiWrapper.GetParameterHandler().JpegQuality);
        jpegQuality.SetUiItemClickListner(this);

        videoProfile.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_VIDEPROFILE);

        if (cameraUiWrapper.GetParameterHandler().VideoProfiles != null)
            videoProfile.SetParameter(cameraUiWrapper.GetParameterHandler().VideoProfiles);
        videoProfile.SetUiItemClickListner(this);

        videoHDR.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_VIDEOHDR);
        videoHDR.SetParameter(cameraUiWrapper.GetParameterHandler().VideoHDR);
        videoHDR.SetCameraInterface(cameraUiWrapper);
        videoHDR.SetUiItemClickListner(this);
        ///////////////////////////   Highspeed Recording //////////////////////////////////////////

        VideoSize.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_VIDEOSIZE);
        if (!(cameraUiWrapper instanceof Camera1Fragment) && cameraUiWrapper.GetParameterHandler().VideoSize != null && cameraUiWrapper.GetParameterHandler().VideoSize.IsSupported()) {
            VideoSize.SetParameter(cameraUiWrapper.GetParameterHandler().VideoSize);
            VideoSize.SetUiItemClickListner(this);
            VideoSize.setVisibility(View.VISIBLE);
        }
        else
            VideoSize.setVisibility(View.GONE);

        videoStabilization.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_VIDEOSTABILIZATION);
        videoStabilization.SetParameter(cameraUiWrapper.GetParameterHandler().VideoStabilization);
        videoStabilization.SetUiItemClickListner(this);

        ////////////////////////////////////////////////////////////////////////////////////////////

        if (cameraUiWrapper instanceof Camera1Fragment) {

            timeLapseFrames.setVisibility(View.VISIBLE);
            timeLapseFrames.SetStuff(fragment_activityInterface.getAppSettings());
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
            PreviewFormat.SetStuff(fragment_activityInterface, null);
            PreviewFormat.SetParameter(cameraUiWrapper.GetParameterHandler().PreviewFormat);
            PreviewFormat.SetUiItemClickListner(this);
            PreviewFormat.setVisibility(View.VISIBLE);
            PreviewSize.SetStuff(fragment_activityInterface, null);
            PreviewSize.SetParameter(cameraUiWrapper.GetParameterHandler().PreviewSize);
            PreviewSize.SetUiItemClickListner(this);
            PreviewSize.setVisibility(View.VISIBLE);
        }
        else {
            PreviewFormat.setVisibility(View.GONE);
            PreviewSize.setVisibility(View.GONE);
        }

        horizont.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_HORIZONT);
        horizont.SetParameter(cameraUiWrapper.GetParameterHandler().Horizont);
        horizont.SetUiItemClickListner(this);

        if(cameraUiWrapper instanceof Camera1Fragment)
        {
            AEB1.SetStuff(fragment_activityInterface.getAppSettings(), AppSettingsManager.SETTING_AEB1);
            AEB1.SetCameraUIWrapper(cameraUiWrapper);
            AEB2.SetStuff(fragment_activityInterface.getAppSettings(), AppSettingsManager.SETTING_AEB2);
            AEB2.SetCameraUIWrapper(cameraUiWrapper);
            AEB3.SetStuff(fragment_activityInterface.getAppSettings(), AppSettingsManager.SETTING_AEB3);
            AEB3.SetCameraUIWrapper(cameraUiWrapper);
        }
        else
        {
            AEB1.setVisibility(View.GONE);
            AEB2.setVisibility(View.GONE);
            AEB3.setVisibility(View.GONE);
        }

        opcode.SetStuff(fragment_activityInterface, "");
        opcode.SetParameter(cameraUiWrapper.GetParameterHandler().opcode);
        opcode.SetUiItemClickListner(this);

        bayerFormatItem.SetStuff(fragment_activityInterface, AppSettingsManager.SETTTING_BAYERFORMAT);
        bayerFormatItem.SetParameter(cameraUiWrapper.GetParameterHandler().bayerformat);
        bayerFormatItem.SetUiItemClickListner(this);

        matrixChooser.SetStuff(fragment_activityInterface, AppSettingsManager.SETTTING_CUSTOMMATRIX);
        matrixChooser.SetParameter(cameraUiWrapper.GetParameterHandler().matrixChooser);
        matrixChooser.SetUiItemClickListner(this);

        imageStackMode.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_STACKMODE);
        imageStackMode.SetParameter(cameraUiWrapper.GetParameterHandler().imageStackMode);
        imageStackMode.SetUiItemClickListner(this);
    }

    public void SetMenuItemClickListner(SettingsChildClick menuItemClick)
    {
        onMenuItemClick = menuItemClick;
    }

    @Override
    public void onSettingsChildClick(UiSettingsChild item, boolean fromLeftFragment) {
        onMenuItemClick.onSettingsChildClick(item, true);
    }

}
