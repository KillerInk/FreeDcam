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
import freed.cam.apis.basecamera.parameters.Parameters;
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

            if (params.get(Parameters.SceneMode) != null)
            {
                SettingsChildMenu scene = new SettingsChildMenu(getContext(), apS.sceneMode, params.get(Parameters.SceneMode), R.string.setting_scene_header, R.string.setting_scene_description);
                scene.SetUiItemClickListner(this);
                settingsgroup.addView(scene);
            }

            if (params.get(Parameters.ColorMode) != null)
            {
                SettingsChildMenu color = new SettingsChildMenu(getContext(), apS.colorMode, params.get(Parameters.ColorMode), R.string.setting_color_header, R.string.setting_color_description);
                color.SetUiItemClickListner(this);
                settingsgroup.addView(color);
            }

            if (params.get(Parameters.ColorCorrectionMode) != null)
            {
                SettingsChildMenu cct = new SettingsChildMenu(getContext(), apS.colorCorrectionMode, params.get(Parameters.ColorCorrectionMode), R.string.setting_colorcorrection_header, R.string.setting_colorcorrection_description);
                cct.SetUiItemClickListner(this);
                settingsgroup.addView(cct);
            }
            if (params.get(Parameters.ObjectTracking) != null)
            {
                SettingsChildMenu ot = new SettingsChildMenu(getContext(), apS.objectTracking, params.get(Parameters.ObjectTracking), R.string.setting_objecttrack_header, R.string.setting_objecttrack_description);
                ot.SetUiItemClickListner(this);
                settingsgroup.addView(ot);
            }
            if (params.get(Parameters.ToneMapMode) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.toneMapMode, params.get(Parameters.ToneMapMode), R.string.setting_tonemap_header, R.string.setting_tonemap_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.PostViewSize) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.postviewSize, params.get(Parameters.PostViewSize), R.string.setting_postview_header, R.string.setting_postview_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.ControlMode) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.controlMode, params.get(Parameters.ControlMode), R.string.setting_controlmode_header, R.string.setting_controlmode_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.RedEye) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.redEyeMode, params.get(Parameters.RedEye), R.string.setting_redeye_header, R.string.setting_redeye_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.AntiBandingMode) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.antiBandingMode, params.get(Parameters.AntiBandingMode), R.string.setting_antiflicker_header, R.string.setting_antiflicker_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.ImagePostProcessing) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.imagePostProcessing, params.get(Parameters.ImagePostProcessing), R.string.setting_ipp_header, R.string.setting_ipp_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }

            if (params.get(Parameters.LensShade) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.lenshade, params.get(Parameters.LensShade), R.string.setting_lensshade_header, R.string.setting_lensshade_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.SceneDetect) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.sceneDetectMode, params.get(Parameters.SceneDetect), R.string.setting_scenedec_header, R.string.setting_scenedec_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.Denoise) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.denoiseMode, params.get(Parameters.Denoise), R.string.setting_waveletdenoise_header, R.string.setting_waveletdenoise_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
/////////////////////////////////////////////////
            if (params.get(Parameters.TNR) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.temporal_nr, params.get(Parameters.TNR), R.string.setting_temporaldenoise_header, R.string.setting_temporaldenoise_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.TNR_V) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.temporal_video_nr, params.get(Parameters.TNR_V), R.string.setting_temporaldenoiseV_header, R.string.setting_temporaldenoiseV_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.PDAF) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.pdafcontrol, params.get(Parameters.PDAF), R.string.setting_pdaf_header, R.string.setting_pdaf_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.TruePotrait) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.truepotrait, params.get(Parameters.TruePotrait), R.string.setting_truepotrait_header, R.string.setting_truepotrait_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.RDI) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.rawdumpinterface, params.get(Parameters.RDI), R.string.setting_rdi_header, R.string.setting_rdi_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.ChromaFlash) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.chromaflash, params.get(Parameters.ChromaFlash), R.string.setting_chroma_header, R.string.setting_chroma_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.OptiZoom) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.optizoom, params.get(Parameters.OptiZoom), R.string.setting_optizoom_header, R.string.setting_optizoom_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.ReFocus) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.refocus, params.get(Parameters.ReFocus), R.string.setting_refocus_header, R.string.setting_refous_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }

            if (params.get(Parameters.SeeMore) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.seemore_tonemap, params.get(Parameters.SeeMore), R.string.setting_seemore_header, R.string.setting_seemore_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
   ///////////////////////////////////////////////

            if (params.get(Parameters.LensFilter) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.virtualLensfilter, params.get(Parameters.LensFilter), R.string.setting_lensfilter_header, R.string.setting_lensfilter_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.DigitalImageStabilization) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.digitalImageStabilisationMode, params.get(Parameters.DigitalImageStabilization), R.string.setting_dis_header, R.string.setting_dis_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.MemoryColorEnhancement) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.memoryColorEnhancement, params.get(Parameters.MemoryColorEnhancement), R.string.setting_mce_header, R.string.setting_mce_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.ZSL) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.zeroshutterlag, params.get(Parameters.ZSL), R.string.setting_zsl_header, R.string.setting_zsl_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.NonZslManualMode) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.nonZslManualMode, params.get(Parameters.NonZslManualMode), R.string.setting_nonzsl_header, R.string.setting_nonzsl_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.CDS_Mode) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.correlatedDoubleSampling, params.get(Parameters.CDS_Mode), R.string.setting_cds_header, R.string.setting_cds_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.EdgeMode) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.edgeMode, params.get(Parameters.EdgeMode), R.string.setting_edge_header, R.string.setting_edge_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.HotPixelMode) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.hotpixelMode, params.get(Parameters.HotPixelMode), R.string.setting_hotpixel_header, R.string.setting_hotpixel_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.oismode) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.opticalImageStabilisation, params.get(Parameters.oismode), R.string.setting_ois_header, R.string.setting_ois_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.ZoomSetting) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.zoommode, params.get(Parameters.ZoomSetting), R.string.setting_zoomsetting_header, R.string.setting_zoomsetting_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.scalePreview) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.scalePreview, params.get(Parameters.scalePreview), R.string.setting_scalepreview_header, R.string.setting_scalepreview_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.dualPrimaryCameraMode) != null && !apS.getIsFrontCamera())
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.dualPrimaryCameraMode, params.get(Parameters.dualPrimaryCameraMode), R.string.setting_dualprimarycamera_header, R.string.setting_dualprimarycamera_description);
                ton.SetUiItemClickListner(this);
                settingsgroup.addView(ton);
            }
            if (params.get(Parameters.Ae_TargetFPS) != null)
            {
                SettingsChildMenu ton = new SettingsChildMenu(getContext(), apS.ae_TagetFPS, params.get(Parameters.Ae_TargetFPS), R.string.setting_aetargetfps_header, R.string.setting_aetargetfps_description);
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
