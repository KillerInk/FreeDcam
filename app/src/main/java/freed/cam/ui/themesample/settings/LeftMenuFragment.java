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
import android.widget.LinearLayout;

import com.troop.freedcam.R;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.settings.Settings;
import freed.cam.apis.basecamera.parameters.modes.ApiParameter;
import freed.cam.apis.basecamera.parameters.modes.ParameterExternalShutter;
import freed.cam.apis.camera2.Camera2Fragment;
import freed.cam.apis.sonyremote.SonyCameraRemoteFragment;
import freed.cam.ui.themesample.AbstractFragment;
import freed.cam.ui.themesample.SettingsChildAbstract.SettingsChildClick;
import freed.cam.ui.themesample.cameraui.childs.UiSettingsChild;
import freed.cam.ui.themesample.settings.childs.GroupChild;
import freed.cam.ui.themesample.settings.childs.SettingsChildFeatureDetect;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenu;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuForceRawToDng;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuGPS;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuInterval;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuIntervalDuration;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuOrientationHack;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuSDSave;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuSaveCamParams;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuTimeLapseFrames;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuVideoHDR;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuVideoProfile;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenu_VideoProfEditor;
import freed.settings.SettingsManager;

/**
 * Created by troop on 15.06.2015.
 */
public class LeftMenuFragment extends AbstractFragment  implements SettingsChildClick
{
    private final String TAG = LeftMenuFragment.class.getSimpleName();

    private SettingsChildClick onMenuItemClick;

    private LinearLayout settingsChildHolder;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        fragment_activityInterface = (ActivityInterface)getActivity();
        View view = inflater.inflate(layout.settings_leftmenufragment, container, false);

        settingsChildHolder = (LinearLayout)view.findViewById(id.SettingChildHolder);
        setCameraToUi(cameraUiWrapper);
        return view;
    }

    @Override
    public void setCameraToUi(CameraWrapperInterface wrapper) {

        this.cameraUiWrapper = wrapper;
        settingsChildHolder.removeAllViews();
        if (cameraUiWrapper != null) {

            SettingsManager apS = SettingsManager.getInstance();
            AbstractParameterHandler params = cameraUiWrapper.getParameterHandler();
        /*
            VIDEOGROUP
         */
            GroupChild videoGroup = new GroupChild(getContext(), getString(R.string.setting_video_group_header));

            if (params.get(Settings.VideoProfiles) != null) {
                SettingsChildMenuVideoProfile videoProfile = new SettingsChildMenuVideoProfile(getContext(), apS.get(Settings.VideoProfiles),
                        params.get(Settings.VideoProfiles), R.string.setting_videoprofile_header, R.string.setting_videoprofile_description);
                videoProfile.SetUiItemClickListner(this);
                videoGroup.addView(videoProfile);

                SettingsChildMenuTimeLapseFrames timeLapseFrames = new SettingsChildMenuTimeLapseFrames(getContext());
                timeLapseFrames.setVisibility(View.VISIBLE);
                videoGroup.addView(timeLapseFrames);


                SettingsChildMenu_VideoProfEditor videoProfileEditor = new SettingsChildMenu_VideoProfEditor(getContext(), R.string.setting_videoprofileeditor_header, R.string.setting_videoprofileeditor_description);
                videoGroup.addView(videoProfileEditor);
            }
            if (params.get(Settings.VideoHDR) != null) {
                SettingsChildMenuVideoHDR videoHDR = new SettingsChildMenuVideoHDR(getContext(), apS.get(Settings.VideoHDR), params.get(Settings.VideoHDR), R.string.setting_videohdr_header, R.string.setting_videohdr_description);
                videoHDR.SetCameraInterface(cameraUiWrapper);
                videoHDR.SetUiItemClickListner(this);
                videoGroup.addView(videoHDR);
            }

            if (params.get(Settings.VideoSize) != null && (cameraUiWrapper instanceof SonyCameraRemoteFragment)) {

                SettingsChildMenu VideoSize = new SettingsChildMenu(getContext(), apS.get(Settings.VideoSize), params.get(Settings.VideoSize), R.string.setting_videoprofile_header, R.string.setting_videoprofile_description);
                VideoSize.SetUiItemClickListner(this);
                videoGroup.addView(VideoSize);
            }

            if (params.get(Settings.VideoStabilization) != null) {
                SettingsChildMenu videoStabilization = new SettingsChildMenu(getContext(), apS.get(Settings.VideoStabilization), params.get(Settings.VideoStabilization), R.string.setting_vs_header, R.string.setting_vs_description);
                videoStabilization.SetUiItemClickListner(this);
                videoGroup.addView(videoStabilization);
            }

            if (videoGroup.childSize() > 0)
                settingsChildHolder.addView(videoGroup);

        /*
            PictureGroup
         */
            GroupChild picGroup = new GroupChild(getContext(), getString(R.string.setting_picture_group_header));

            if (params.get(Settings.PictureSize) != null) {
                SettingsChildMenu pictureSize = new SettingsChildMenu(getContext(), apS.get(Settings.PictureSize), params.get(Settings.PictureSize), R.string.setting_picturesize_header, R.string.setting_picturesize_description);
                pictureSize.SetUiItemClickListner(this);
                picGroup.addView(pictureSize);
            }

            if (params.get(Settings.JpegQuality) != null) {
                SettingsChildMenu jpegQuality = new SettingsChildMenu(getContext(), apS.get(Settings.JpegQuality), params.get(Settings.JpegQuality), R.string.setting_jpegquality_header, R.string.setting_jpegquality_description);
                jpegQuality.SetUiItemClickListner(this);
                picGroup.addView(jpegQuality);
            }

            GroupChild intervalGroup = new GroupChild(getContext(), getString(R.string.setting_Automation));

            SettingsChildMenuInterval menuInterval = new SettingsChildMenuInterval(getContext(), apS.get(Settings.IntervalShutterSleep), params.get(Settings.IntervalShutterSleep), R.string.setting_interval_header, R.string.setting_interval_texter);
            menuInterval.SetUiItemClickListner(this);
            intervalGroup.addView(menuInterval);

            SettingsChildMenuIntervalDuration menuIntervalDuration = new SettingsChildMenuIntervalDuration(getContext(), apS.get(Settings.IntervalDuration), params.get(Settings.IntervalDuration), R.string.setting_interval_duration_header, R.string.setting_interval_duration_text);
            menuIntervalDuration.SetUiItemClickListner(this);
            intervalGroup.addView(menuIntervalDuration);

            picGroup.addView(intervalGroup);

            GroupChild dngGroup = new GroupChild(getContext(), getString(R.string.setting_raw_group_header));

            if (params.get(Settings.opcode) != null) {
                SettingsChildMenu opcode = new SettingsChildMenu(getContext(), apS.get(Settings.opcode), params.get(Settings.opcode), R.string.setting_opcode_header, R.string.setting_opcode_description);
                opcode.SetUiItemClickListner(this);
                dngGroup.addView(opcode);
            }

            if (params.get(Settings.bayerformat) != null && params.get(Settings.bayerformat).IsSupported() && apS.get(Settings.rawPictureFormatSetting).isSupported()) {
                SettingsChildMenu bayerFormatItem = new SettingsChildMenu(getContext(), apS.get(Settings.rawPictureFormatSetting), params.get(Settings.bayerformat), R.string.setting_bayerformat_header, R.string.setting_bayerformat_description);
                bayerFormatItem.SetUiItemClickListner(this);
                dngGroup.addView(bayerFormatItem);
            }
            if (params.get(Settings.matrixChooser) != null && params.get(Settings.matrixChooser).IsSupported()) {
                SettingsChildMenu matrixChooser = new SettingsChildMenu(getContext(), apS.get(Settings.matrixChooser), params.get(Settings.matrixChooser), R.string.setting_matrixchooser_header, R.string.setting_matrixchooser_description);
                matrixChooser.SetUiItemClickListner(this);
                dngGroup.addView(matrixChooser);
            }
            if (params.get(Settings.tonemapChooser) != null && params.get(Settings.tonemapChooser).IsSupported()) {
                SettingsChildMenu matrixChooser = new SettingsChildMenu(getContext(), apS.get(Settings.tonemapChooser), params.get(Settings.tonemapChooser), R.string.setting_tonemapchooser_header, R.string.setting_tonemapchooser_description);
                matrixChooser.SetUiItemClickListner(this);
                dngGroup.addView(matrixChooser);
            }
            if (cameraUiWrapper instanceof Camera2Fragment)
            {
                SettingsChildMenuForceRawToDng rawToDng = new SettingsChildMenuForceRawToDng(getContext(), R.string.setting_forcerawtodng_header, R.string.setting_forcerawtodng_description);
                rawToDng.SetUiItemClickListner(this);
                dngGroup.addView(rawToDng);
            }
            if (dngGroup.childSize() > 0)
                picGroup.addView(dngGroup);

            settingsChildHolder.addView(picGroup);

        /*menuItemTimer.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_TIMER);
        menuItemTimer.setCameraToUi(cameraUiWrapper);
        menuItemTimer.SetUiItemClickListner(this);*/
        }

        /*
            Gobal settings
         */

        GroupChild globalSettingGroup = new GroupChild(getContext(),getString(R.string.setting_freedcam_));


        SettingsChildMenu api = new SettingsChildMenu(getContext(),R.string.setting_api_header, R.string.setting_api_description);
        api.SetStuff(fragment_activityInterface, "");
        api.SetParameter(new ApiParameter(fragment_activityInterface));
        api.SetUiItemClickListner(this);
        globalSettingGroup.addView(api);



        if (cameraUiWrapper != null) {


            SettingsChildMenu externalShutter = new SettingsChildMenu(getContext(),R.string.setting_externalshutter_header, R.string.setting_externalshutter_description);
            externalShutter.SetStuff(fragment_activityInterface, "");
            externalShutter.SetParameter(new ParameterExternalShutter());
            externalShutter.SetUiItemClickListner(this);
            globalSettingGroup.addView(externalShutter);

            SettingsChildMenuOrientationHack orientationHack = new SettingsChildMenuOrientationHack(getContext(),R.string.setting_orientation_header, R.string.setting_orientation_description);
            orientationHack.SetStuff(fragment_activityInterface, "");
            orientationHack.SetCameraUIWrapper(cameraUiWrapper);
            orientationHack.SetUiItemClickListner(this);
            globalSettingGroup.addView(orientationHack);

            SettingsChildMenuSDSave sdSave = new SettingsChildMenuSDSave(getContext(), R.string.setting_sdcard_header, R.string.setting_sdcard_description);
            sdSave.SetStuff(fragment_activityInterface, SettingsManager.SETTING_EXTERNALSD);
            sdSave.SetCameraUiWrapper(cameraUiWrapper);
            sdSave.SetUiItemClickListner(this);
            globalSettingGroup.addView(sdSave);

            SettingsChildMenuGPS menuItemGPS = new SettingsChildMenuGPS(getContext(),R.string.setting_location_header, R.string.setting_location_description );
            menuItemGPS.SetStuff(fragment_activityInterface, SettingsManager.SETTING_LOCATION);
            menuItemGPS.SetCameraUIWrapper(cameraUiWrapper);
            menuItemGPS.SetUiItemClickListner(this);
            globalSettingGroup.addView(menuItemGPS);

            SettingsChildMenu guide = new SettingsChildMenu(getContext(), SettingsManager.get(Settings.GuideList),cameraUiWrapper.getParameterHandler().get(Settings.GuideList), R.string.setting_guide_header, R.string.setting_guide_description);
            guide.SetUiItemClickListner(this);
            globalSettingGroup.addView(guide);

            SettingsChildMenuSaveCamParams saveCamParams = new SettingsChildMenuSaveCamParams(getContext(),R.string.setting_savecamparams_header,R.string.setting_savecamparams_description,cameraUiWrapper);
            saveCamParams.setCameraUiWrapper(cameraUiWrapper);
            globalSettingGroup.addView(saveCamParams);

            SettingsChildMenu horizont = new SettingsChildMenu(getContext(), R.string.setting_horizont_header, R.string.setting_horizont_description);
            horizont.SetStuff(fragment_activityInterface, SettingsManager.SETTING_HORIZONT);
            horizont.SetParameter(cameraUiWrapper.getParameterHandler().get(Settings.HorizontLvl));
            horizont.SetUiItemClickListner(this);
            globalSettingGroup.addView(horizont);

            SettingsChildMenu nightoverlay = new SettingsChildMenu(getContext(), R.string.setting_nightoverlay_header, R.string.setting_nightoverlay_description);
            nightoverlay.SetUiItemClickListner(this);
            nightoverlay.SetParameter(cameraUiWrapper.getParameterHandler().get(Settings.NightOverlay));
            globalSettingGroup.addView(nightoverlay);

            if (!(cameraUiWrapper instanceof SonyCameraRemoteFragment))
            {
                SettingsChildFeatureDetect fd = new SettingsChildFeatureDetect(getContext(),R.string.setting_featuredetector_header,R.string.setting_featuredetector_description, fragment_activityInterface);
                globalSettingGroup.addView(fd);
            }
        }

        settingsChildHolder.addView(globalSettingGroup);



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
