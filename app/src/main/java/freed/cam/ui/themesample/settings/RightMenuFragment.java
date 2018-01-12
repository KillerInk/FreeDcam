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
import android.support.annotation.Nullable;
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
import freed.cam.ui.themesample.AbstractFragment;
import freed.cam.ui.themesample.SettingsChildAbstract.SettingsChildClick;
import freed.cam.ui.themesample.cameraui.childs.UiSettingsChild;
import freed.cam.ui.themesample.settings.childs.GroupChild;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenu;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

/**
 * Created by troop on 15.06.2015.
 */
public class RightMenuFragment extends AbstractFragment implements SettingsChildClick
{
    private static final String TAG = RightMenuFragment.class.getSimpleName();
    private SettingsChildClick onMenuItemClick;

    private LinearLayout settingchildholder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        fragment_activityInterface = (ActivityInterface)getActivity();
        return inflater.inflate(layout.settings_rightmenufragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingchildholder = (LinearLayout)view.findViewById(id.SettingChildHolder);
        setCameraToUi(cameraUiWrapper);
    }

    @Override
    public void setCameraToUi(CameraWrapperInterface wrapper)
    {
        super.setCameraToUi(wrapper);
        settingchildholder.removeAllViews();
        if (cameraUiWrapper != null)
        {
            SettingsManager apS = SettingsManager.getInstance();
            AbstractParameterHandler params = cameraUiWrapper.getParameterHandler();

            GroupChild settingsgroup = new GroupChild(getContext(), getString(R.string.setting_camera_));

            if (params.get(SettingKeys.SceneMode) != null)
            {
                SettingsChildMenu scene = new SettingsChildMenu(getContext(), params.get(SettingKeys.SceneMode), R.string.setting_scene_header, R.string.setting_scene_description);
                scene.SetUiItemClickListner(this);
                settingsgroup.addView(scene);
            }

            if (params.get(SettingKeys.ColorMode) != null)
            {
                SettingsChildMenu color = new SettingsChildMenu(getContext(), params.get(SettingKeys.ColorMode), R.string.setting_color_header, R.string.setting_color_description);
                color.SetUiItemClickListner(this);
                settingsgroup.addView(color);
            }

            if (params.get(SettingKeys.COLOR_CORRECTION_MODE) != null)
            {
                SettingsChildMenu cct = new SettingsChildMenu(getContext(), params.get(SettingKeys.COLOR_CORRECTION_MODE), R.string.setting_colorcorrection_header, R.string.setting_colorcorrection_description);
                cct.SetUiItemClickListner(this);
                settingsgroup.addView(cct);
            }
            if (params.get(SettingKeys.ObjectTracking) != null)
            {
                SettingsChildMenu ot = new SettingsChildMenu(getContext(), params.get(SettingKeys.ObjectTracking), R.string.setting_objecttrack_header, R.string.setting_objecttrack_description);
                ot.SetUiItemClickListner(this);
                settingsgroup.addView(ot);
            }
            if (params.get(SettingKeys.TONE_MAP_MODE) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.TONE_MAP_MODE), R.string.setting_tonemap_header, R.string.setting_tonemap_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.PostViewSize) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.PostViewSize), R.string.setting_postview_header, R.string.setting_postview_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.CONTROL_MODE) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.CONTROL_MODE), R.string.setting_controlmode_header, R.string.setting_controlmode_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.RedEye) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.RedEye), R.string.setting_redeye_header, R.string.setting_redeye_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.AntiBandingMode) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.AntiBandingMode), R.string.setting_antiflicker_header, R.string.setting_antiflicker_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.ImagePostProcessing) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.ImagePostProcessing), R.string.setting_ipp_header, R.string.setting_ipp_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }

            if (params.get(SettingKeys.LensShade) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.LensShade), R.string.setting_lensshade_header, R.string.setting_lensshade_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.SceneDetect) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.SceneDetect), R.string.setting_scenedec_header, R.string.setting_scenedec_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.Denoise) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.Denoise), R.string.setting_waveletdenoise_header, R.string.setting_waveletdenoise_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
/////////////////////////////////////////////////
            if (params.get(SettingKeys.TNR) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.TNR), R.string.setting_temporaldenoise_header, R.string.setting_temporaldenoise_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.TNR_V) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.TNR_V), R.string.setting_temporaldenoiseV_header, R.string.setting_temporaldenoiseV_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.PDAF) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.PDAF), R.string.setting_pdaf_header, R.string.setting_pdaf_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.TruePotrait) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.TruePotrait), R.string.setting_truepotrait_header, R.string.setting_truepotrait_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.RDI) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.RDI), R.string.setting_rdi_header, R.string.setting_rdi_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.ChromaFlash) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.ChromaFlash), R.string.setting_chroma_header, R.string.setting_chroma_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.OptiZoom) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.OptiZoom), R.string.setting_optizoom_header, R.string.setting_optizoom_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.ReFocus) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.ReFocus), R.string.setting_refocus_header, R.string.setting_refous_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }

            if (params.get(SettingKeys.SeeMore) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.SeeMore), R.string.setting_seemore_header, R.string.setting_seemore_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
   ///////////////////////////////////////////////

            if (params.get(SettingKeys.LensFilter) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.LensFilter), R.string.setting_lensfilter_header, R.string.setting_lensfilter_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.DigitalImageStabilization) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.DigitalImageStabilization), R.string.setting_dis_header, R.string.setting_dis_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.MemoryColorEnhancement) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.MemoryColorEnhancement), R.string.setting_mce_header, R.string.setting_mce_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.ZSL) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(),params.get(SettingKeys.ZSL), R.string.setting_zsl_header, R.string.setting_zsl_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.NonZslManualMode) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.NonZslManualMode), R.string.setting_nonzsl_header, R.string.setting_nonzsl_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.CDS_Mode) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.CDS_Mode), R.string.setting_cds_header, R.string.setting_cds_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.EDGE_MODE) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.EDGE_MODE), R.string.setting_edge_header, R.string.setting_edge_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.HOT_PIXEL_MODE) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.HOT_PIXEL_MODE), R.string.setting_hotpixel_header, R.string.setting_hotpixel_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.OIS_MODE) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.OIS_MODE), R.string.setting_ois_header, R.string.setting_ois_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.ZoomSetting) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.ZoomSetting), R.string.setting_zoomsetting_header, R.string.setting_zoomsetting_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.SCALE_PREVIEW) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.SCALE_PREVIEW), R.string.setting_scalepreview_header, R.string.setting_scalepreview_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.dualPrimaryCameraMode) != null && !apS.getIsFrontCamera())
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.dualPrimaryCameraMode), R.string.setting_dualprimarycamera_header, R.string.setting_dualprimarycamera_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(SettingKeys.Ae_TargetFPS) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), params.get(SettingKeys.Ae_TargetFPS), R.string.setting_aetargetfps_header, R.string.setting_aetargetfps_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }



            settingchildholder.addView(settingsgroup);
        }

       /* temporalDenoise.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_TNR);
        temporalDenoise.SetParameter(cameraUiWrapper.getParameterHandler().TnrMode);
        temporalDenoise.SetUiItemClickListner(this);*/
    }

    public void SetMenuItemClickListner(SettingsChildClick menuItemClick)
    {
        onMenuItemClick = menuItemClick;
    }

    @Override
    public void onSettingsChildClick(UiSettingsChild item, boolean fromLeftFragment) {
        onMenuItemClick.onSettingsChildClick(item, false);
    }
}
