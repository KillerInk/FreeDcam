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
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.cam.apis.basecamera.parameters.modes.ApiParameter;
import freed.cam.apis.basecamera.parameters.modes.ParameterExternalShutter;
import freed.cam.ui.themesample.AbstractFragment;
import freed.cam.ui.themesample.SettingsChildAbstract.SettingsChildClick;
import freed.cam.ui.themesample.cameraui.childs.UiSettingsChild;
import freed.cam.ui.themesample.settings.childs.GroupChild;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenu;
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
import freed.utils.AppSettingsManager;

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
        setCameraUiWrapperToUi();
        return view;
    }

    @Override
    protected void setCameraUiWrapperToUi() {

        settingsChildHolder.removeAllViews();
        if (cameraUiWrapper != null) {

            AppSettingsManager apS = cameraUiWrapper.getAppSettingsManager();
            AbstractParameterHandler params = cameraUiWrapper.getParameterHandler();
        /*
            VIDEOGROUP
         */
            GroupChild videoGroup = new GroupChild(getContext(), getString(R.string.setting_video_group_header));

            if (params.VideoProfiles != null) {
                SettingsChildMenuVideoProfile videoProfile = new SettingsChildMenuVideoProfile(getContext(), apS.videoProfile,
                        params.VideoProfiles, R.string.setting_videoprofile_header, R.string.setting_videoprofile_description);
                videoProfile.SetUiItemClickListner(this);
                videoGroup.addView(videoProfile);

                SettingsChildMenuTimeLapseFrames timeLapseFrames = new SettingsChildMenuTimeLapseFrames(getContext(), apS);
                timeLapseFrames.setVisibility(View.VISIBLE);
                videoGroup.addView(timeLapseFrames);

                SettingsChildMenu_VideoProfEditor videoProfileEditor = new SettingsChildMenu_VideoProfEditor(getContext(), R.string.setting_videoprofileeditor_header, R.string.setting_videoprofileeditor_description);
                videoGroup.addView(videoProfileEditor);
            }
            if (params.VideoHDR != null) {
                SettingsChildMenuVideoHDR videoHDR = new SettingsChildMenuVideoHDR(getContext(), apS.videoHDR, params.VideoHDR, R.string.setting_videohdr_header, R.string.setting_videohdr_description);
                videoHDR.SetCameraInterface(cameraUiWrapper);
                videoHDR.SetUiItemClickListner(this);
                videoGroup.addView(videoHDR);
            }

            if (params.VideoSize != null) {

                SettingsChildMenu VideoSize = new SettingsChildMenu(getContext(), apS.videoSize, params.VideoSize, R.string.setting_videoprofile_header, R.string.setting_videoprofile_description);
                VideoSize.SetUiItemClickListner(this);
                videoGroup.addView(VideoSize);
            }

            if (params.VideoStabilization != null) {
                SettingsChildMenu videoStabilization = new SettingsChildMenu(getContext(), apS.videoStabilisation, params.VideoStabilization, R.string.setting_vs_header, R.string.setting_vs_description);
                videoStabilization.SetUiItemClickListner(this);
                videoGroup.addView(videoStabilization);
            }

            if (videoGroup.childSize() > 0)
                settingsChildHolder.addView(videoGroup);

        /*
            PictureGroup
         */
            GroupChild picGroup = new GroupChild(getContext(), getString(R.string.setting_picture_group_header));

            if (params.PictureSize != null) {
                SettingsChildMenu pictureSize = new SettingsChildMenu(getContext(), apS.pictureSize, params.PictureSize, R.string.setting_picturesize_header, R.string.setting_picturesize_description);
                pictureSize.SetUiItemClickListner(this);
                picGroup.addView(pictureSize);
            }

            if (params.JpegQuality != null) {
                SettingsChildMenu jpegQuality = new SettingsChildMenu(getContext(), apS.jpegQuality, params.JpegQuality, R.string.setting_jpegquality_header, R.string.setting_jpegquality_description);
                jpegQuality.SetUiItemClickListner(this);
                picGroup.addView(jpegQuality);
            }

            GroupChild intervalGroup = new GroupChild(getContext(), getString(R.string.setting_Automation));

            SettingsChildMenuInterval menuInterval = new SettingsChildMenuInterval(getContext(), apS.interval, params.IntervalShutterSleep, R.string.setting_interval_header, R.string.setting_interval_texter);
            menuInterval.SetUiItemClickListner(this);
            intervalGroup.addView(menuInterval);

            SettingsChildMenuIntervalDuration menuIntervalDuration = new SettingsChildMenuIntervalDuration(getContext(), apS.intervalDuration, params.IntervalDuration, R.string.setting_interval_duration_header, R.string.setting_interval_duration_text);
            menuIntervalDuration.SetUiItemClickListner(this);
            intervalGroup.addView(menuIntervalDuration);

            picGroup.addView(intervalGroup);

            GroupChild dngGroup = new GroupChild(getContext(), getString(R.string.setting_raw_group_header));

            if (params.opcode != null) {
                SettingsChildMenu opcode = new SettingsChildMenu(getContext(), apS.opcode, params.opcode, R.string.setting_opcode_header, R.string.setting_opcode_description);
                opcode.SetUiItemClickListner(this);
                dngGroup.addView(opcode);
            }

            if (params.bayerformat != null) {
                SettingsChildMenu bayerFormatItem = new SettingsChildMenu(getContext(), apS.rawPictureFormat, params.bayerformat, R.string.setting_bayerformat_header, R.string.setting_bayerformat_description);
                bayerFormatItem.SetUiItemClickListner(this);
                dngGroup.addView(bayerFormatItem);
            }
            if (params.matrixChooser != null) {
                SettingsChildMenu matrixChooser = new SettingsChildMenu(getContext(), apS.matrixset, params.matrixChooser, R.string.setting_matrixchooser_header, R.string.setting_matrixchooser_description);
                matrixChooser.SetUiItemClickListner(this);
                dngGroup.addView(matrixChooser);
            }
            if (dngGroup.childSize() > 0)
                picGroup.addView(dngGroup);

            settingsChildHolder.addView(picGroup);

        /*menuItemTimer.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_TIMER);
        menuItemTimer.SetCameraUIWrapper(cameraUiWrapper);
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
            externalShutter.SetParameter(new ParameterExternalShutter(fragment_activityInterface.getAppSettings()));
            externalShutter.SetUiItemClickListner(this);
            globalSettingGroup.addView(externalShutter);

            SettingsChildMenuOrientationHack orientationHack = new SettingsChildMenuOrientationHack(getContext(),R.string.setting_orientation_header, R.string.setting_orientation_description);
            orientationHack.SetStuff(fragment_activityInterface, "");
            orientationHack.SetCameraUIWrapper(cameraUiWrapper);
            orientationHack.SetUiItemClickListner(this);
            globalSettingGroup.addView(orientationHack);

            SettingsChildMenuSDSave sdSave = new SettingsChildMenuSDSave(getContext(), R.string.setting_sdcard_header, R.string.setting_sdcard_description);
            sdSave.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_EXTERNALSD);
            sdSave.SetCameraUiWrapper(cameraUiWrapper);
            sdSave.SetUiItemClickListner(this);
            globalSettingGroup.addView(sdSave);

            SettingsChildMenuGPS menuItemGPS = new SettingsChildMenuGPS(getContext(),R.string.setting_location_header, R.string.setting_location_description );
            menuItemGPS.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_LOCATION);
            menuItemGPS.SetCameraUIWrapper(cameraUiWrapper);
            menuItemGPS.SetUiItemClickListner(this);
            globalSettingGroup.addView(menuItemGPS);

            SettingsChildMenu guide = new SettingsChildMenu(getContext(),cameraUiWrapper.getAppSettingsManager().guide,cameraUiWrapper.getParameterHandler().GuideList, R.string.setting_guide_header, R.string.setting_guide_description);
            guide.SetUiItemClickListner(this);
            globalSettingGroup.addView(guide);

            SettingsChildMenuSaveCamParams saveCamParams = new SettingsChildMenuSaveCamParams(getContext(),R.string.setting_savecamparams_header,R.string.setting_savecamparams_description,cameraUiWrapper);
            saveCamParams.setCameraUiWrapper(cameraUiWrapper);
            globalSettingGroup.addView(saveCamParams);

            SettingsChildMenu horizont = new SettingsChildMenu(getContext(), R.string.setting_horizont_header, R.string.setting_horizont_description);
            horizont.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_HORIZONT);
            horizont.SetParameter(cameraUiWrapper.getParameterHandler().Horizont);
            horizont.SetUiItemClickListner(this);
            globalSettingGroup.addView(horizont);

            SettingsChildMenu nightoverlay = new SettingsChildMenu(getContext(), R.string.setting_nightoverlay_header, R.string.setting_nightoverlay_description);
            nightoverlay.SetUiItemClickListner(this);
            nightoverlay.SetParameter(cameraUiWrapper.getParameterHandler().NightOverlay);
            globalSettingGroup.addView(nightoverlay);
        }

        settingsChildHolder.addView(globalSettingGroup);

        ////////////////////////////////////////////////////////////////////////////////////////////

        /*if (cameraUiWrapper instanceof Camera1Fragment) {

            if (!(cameraUiWrapper.getCameraHolder() instanceof CameraHolderMTK)) {
                AEB1.setVisibility(View.VISIBLE);
                AEB1.SetStuff(fragment_activityInterface.getAppSettings(), AppSettingsManager.SETTING_AEB1);
                AEB1.SetCameraUIWrapper(cameraUiWrapper);

                AEB2.setVisibility(View.VISIBLE);
                AEB2.SetStuff(fragment_activityInterface.getAppSettings(), AppSettingsManager.SETTING_AEB2);
                AEB2.SetCameraUIWrapper(cameraUiWrapper);

                AEB3.setVisibility(View.VISIBLE);
                AEB3.SetStuff(fragment_activityInterface.getAppSettings(), AppSettingsManager.SETTING_AEB3);
                AEB3.SetCameraUIWrapper(cameraUiWrapper);

                AEB4.setVisibility(View.VISIBLE);
                AEB4.SetStuff(fragment_activityInterface.getAppSettings(), AppSettingsManager.SETTING_AEB4);
                AEB4.SetCameraUIWrapper(cameraUiWrapper);

                AEB5.setVisibility(View.VISIBLE);
                AEB5.SetStuff(fragment_activityInterface.getAppSettings(), AppSettingsManager.SETTING_AEB5);
                AEB5.SetCameraUIWrapper(cameraUiWrapper);

                AEB6.setVisibility(View.VISIBLE);
                AEB6.SetStuff(fragment_activityInterface.getAppSettings(), AppSettingsManager.SETTING_AEB6);
                AEB6.SetCameraUIWrapper(cameraUiWrapper);

                AEB7.setVisibility(View.VISIBLE);
                AEB7.SetStuff(fragment_activityInterface.getAppSettings(), AppSettingsManager.SETTING_AEB7);
                AEB7.SetCameraUIWrapper(cameraUiWrapper);
            }
        }
        else if (cameraUiWrapper instanceof Camera2Fragment) {

            AEB1.setVisibility(View.GONE);
            AEB2.setVisibility(View.GONE);
            AEB3.setVisibility(View.GONE);
            AEB4.setVisibility(View.GONE);
            AEB5.setVisibility(View.GONE);
            AEB6.setVisibility(View.GONE);
            AEB7.setVisibility(View.GONE);
        } else {

            AEB1.setVisibility(View.GONE);
            AEB2.setVisibility(View.GONE);
            AEB3.setVisibility(View.GONE);
            AEB4.setVisibility(View.GONE);
            AEB5.setVisibility(View.GONE);
            AEB6.setVisibility(View.GONE);
            AEB7.setVisibility(View.GONE);
        }*/




        /*if (DEBUG) {
            PreviewFormat.SetStuff(fragment_activityInterface, "");
            PreviewFormat.SetParameter(cameraUiWrapper.getParameterHandler().PreviewFormat);
            PreviewFormat.SetUiItemClickListner(this);
            PreviewFormat.setVisibility(View.VISIBLE);
            PreviewSize.SetStuff(fragment_activityInterface, "");
            PreviewSize.SetParameter(cameraUiWrapper.getParameterHandler().PreviewSize);
            PreviewSize.SetUiItemClickListner(this);
            PreviewSize.setVisibility(View.VISIBLE);
        } else {
            PreviewFormat.setVisibility(View.GONE);
            PreviewSize.setVisibility(View.GONE);
        }
*/

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
