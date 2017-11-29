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
import freed.settings.AppSettingsManager;

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
            AppSettingsManager apS = AppSettingsManager.getInstance();
            AbstractParameterHandler params = cameraUiWrapper.getParameterHandler();

            GroupChild settingsgroup = new GroupChild(getContext(), getString(R.string.setting_camera_));

            if (params.SceneMode != null)
            {
                SettingsChildMenu scene = new SettingsChildMenu(getContext(), apS.sceneMode, params.SceneMode, R.string.setting_scene_header, R.string.setting_scene_description);
                scene.SetUiItemClickListner(this);
                settingsgroup.addView(scene);
            }

            if (params.ColorMode != null)
            {
                SettingsChildMenu color = new SettingsChildMenu(getContext(), apS.colorMode, params.ColorMode, R.string.setting_color_header, R.string.setting_color_description);
                color.SetUiItemClickListner(this);
                settingsgroup.addView(color);
            }

            if (params.ColorCorrectionMode != null)
            {
                SettingsChildMenu cct = new SettingsChildMenu(getContext(), apS.colorCorrectionMode, params.ColorCorrectionMode, R.string.setting_colorcorrection_header, R.string.setting_colorcorrection_description);
                cct.SetUiItemClickListner(this);
                settingsgroup.addView(cct);
            }
            if (params.ObjectTracking != null)
            {
                SettingsChildMenu ot = new SettingsChildMenu(getContext(), apS.objectTracking, params.ObjectTracking, R.string.setting_objecttrack_header, R.string.setting_objecttrack_description);
                ot.SetUiItemClickListner(this);
                settingsgroup.addView(ot);
            }
            if (params.ToneMapMode != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.toneMapMode, params.ToneMapMode, R.string.setting_tonemap_header, R.string.setting_tonemap_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.PostViewSize != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.postviewSize, params.PostViewSize, R.string.setting_postview_header, R.string.setting_postview_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.ControlMode != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.controlMode, params.ControlMode, R.string.setting_controlmode_header, R.string.setting_controlmode_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.RedEye != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.redEyeMode, params.RedEye, R.string.setting_redeye_header, R.string.setting_redeye_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.AntiBandingMode != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.antiBandingMode, params.AntiBandingMode, R.string.setting_antiflicker_header, R.string.setting_antiflicker_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.ImagePostProcessing != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.imagePostProcessing, params.ImagePostProcessing, R.string.setting_ipp_header, R.string.setting_ipp_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }

            if (params.LensShade != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.lenshade, params.LensShade, R.string.setting_lensshade_header, R.string.setting_lensshade_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.SceneDetect != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.sceneDetectMode, params.SceneDetect, R.string.setting_scenedec_header, R.string.setting_scenedec_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.Denoise != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.denoiseMode, params.Denoise, R.string.setting_waveletdenoise_header, R.string.setting_waveletdenoise_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
/////////////////////////////////////////////////
            if (params.TNR != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.temporal_nr, params.TNR, R.string.setting_temporaldenoise_header, R.string.setting_temporaldenoise_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.TNR_V != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.temporal_video_nr, params.TNR_V, R.string.setting_temporaldenoiseV_header, R.string.setting_temporaldenoiseV_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.PDAF != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.pdafcontrol, params.PDAF, R.string.setting_pdaf_header, R.string.setting_pdaf_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.TruePotrait != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.truepotrait, params.TruePotrait, R.string.setting_truepotrait_header, R.string.setting_truepotrait_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.RDI != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.rawdumpinterface, params.RDI, R.string.setting_rdi_header, R.string.setting_rdi_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.ChromaFlash != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.chromaflash, params.ChromaFlash, R.string.setting_chroma_header, R.string.setting_chroma_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.OptiZoom != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.optizoom, params.OptiZoom, R.string.setting_optizoom_header, R.string.setting_optizoom_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.ReFocus != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.refocus, params.ReFocus, R.string.setting_refocus_header, R.string.setting_refous_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }

            if (params.SeeMore != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.seemore_tonemap, params.SeeMore, R.string.setting_seemore_header, R.string.setting_seemore_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
   ///////////////////////////////////////////////

            if (params.LensFilter != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.virtualLensfilter, params.LensFilter, R.string.setting_lensfilter_header, R.string.setting_lensfilter_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.DigitalImageStabilization != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.digitalImageStabilisationMode, params.DigitalImageStabilization, R.string.setting_dis_header, R.string.setting_dis_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.MemoryColorEnhancement != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.memoryColorEnhancement, params.MemoryColorEnhancement, R.string.setting_mce_header, R.string.setting_mce_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.ZSL != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.zeroshutterlag, params.ZSL, R.string.setting_zsl_header, R.string.setting_zsl_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.NonZslManualMode != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.nonZslManualMode, params.NonZslManualMode, R.string.setting_nonzsl_header, R.string.setting_nonzsl_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.CDS_Mode != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.correlatedDoubleSampling, params.CDS_Mode, R.string.setting_cds_header, R.string.setting_cds_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.EdgeMode != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.edgeMode, params.EdgeMode, R.string.setting_edge_header, R.string.setting_edge_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.HotPixelMode != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.hotpixelMode, params.HotPixelMode, R.string.setting_hotpixel_header, R.string.setting_hotpixel_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.oismode != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.opticalImageStabilisation, params.oismode, R.string.setting_ois_header, R.string.setting_ois_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.ZoomSetting != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.zoommode, params.ZoomSetting, R.string.setting_zoomsetting_header, R.string.setting_zoomsetting_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.scalePreview != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.scalePreview, params.scalePreview, R.string.setting_scalepreview_header, R.string.setting_scalepreview_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.dualPrimaryCameraMode != null && !apS.getIsFrontCamera())
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.dualPrimaryCameraMode, params.dualPrimaryCameraMode, R.string.setting_dualprimarycamera_header, R.string.setting_dualprimarycamera_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.ae_TargetFPS != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.ae_TagetFPS, params.ae_TargetFPS, R.string.setting_aetargetfps_header, R.string.setting_aetargetfps_description);
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
