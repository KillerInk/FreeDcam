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
import freed.cam.ui.themesample.AbstractFragment;
import freed.cam.ui.themesample.SettingsChildAbstract.SettingsChildClick;
import freed.cam.ui.themesample.cameraui.childs.UiSettingsChild;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenu;
import freed.utils.AppSettingsManager;

/**
 * Created by troop on 15.06.2015.
 */
public class RightMenuFragment extends AbstractFragment implements SettingsChildClick
{
    private static final String TAG = RightMenuFragment.class.getSimpleName();
    private SettingsChildClick onMenuItemClick;
    private SettingsChildMenu scene;
    private SettingsChildMenu color;
    private SettingsChildMenu cctMode;
    private SettingsChildMenu objectTrackingMode;
    private SettingsChildMenu toneMapMode;
    private SettingsChildMenu postViewSize;
    private SettingsChildMenu controleMode;

    private SettingsChildMenu antiBanding;
    private SettingsChildMenu ipp;
    private SettingsChildMenu lensShade;
    private SettingsChildMenu sceneDetectMode;
    private SettingsChildMenu waveletdenoiseMode;
    private SettingsChildMenu digitalImageStabilization;
    private SettingsChildMenu memoryColorEnhancement;
    private SettingsChildMenu ZeroShutterLag;
    private SettingsChildMenu nonZSLmanualMode;
    private SettingsChildMenu correlatedDoubleSampling;
    private SettingsChildMenu temporalDenoise;
    private SettingsChildMenu edgeMode;
    private SettingsChildMenu hotPixelMode;
    private SettingsChildMenu opticalImageStabilization;
    private SettingsChildMenu zoomSetting;
    private SettingsChildMenu redeyeflash;

    private SettingsChildMenu LensFilter;

    private SettingsChildMenu scalePreview;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        fragment_activityInterface = (ActivityInterface)getActivity();
        view = inflater.inflate(layout.settings_rightmenufragment, container, false);
        scene = (SettingsChildMenu) view.findViewById(id.MenuItemScene);
        color = (SettingsChildMenu) view.findViewById(id.MenuItemColor);
        cctMode = (SettingsChildMenu) view.findViewById(id.MenuItemCCTMode);
        objectTrackingMode = (SettingsChildMenu) view.findViewById(id.MenuItemObjectTracking);
        toneMapMode = (SettingsChildMenu) view.findViewById(id.MenuItemTonemap);
        postViewSize = (SettingsChildMenu) view.findViewById(id.MenuItemPostViewSize);
        controleMode = (SettingsChildMenu) view.findViewById(id.MenuItemControlMode);
        redeyeflash = (SettingsChildMenu) view.findViewById(id.MenuItemRedEye);
        antiBanding = (SettingsChildMenu) view.findViewById(id.MenuItemAntiBanding);
        ipp = (SettingsChildMenu) view.findViewById(id.MenuItemIpp);
        lensShade = (SettingsChildMenu) view.findViewById(id.MenuItemLensShade);
        sceneDetectMode = (SettingsChildMenu) view.findViewById(id.MenuItemSceneDetection);
        waveletdenoiseMode = (SettingsChildMenu) view.findViewById(id.MenuItemWaveletDenoise);
        digitalImageStabilization = (SettingsChildMenu) view.findViewById(id.MenuItemDigitalImageStab);
        memoryColorEnhancement = (SettingsChildMenu) view.findViewById(id.MenuItemMemoryColorEnhanc);
        ZeroShutterLag = (SettingsChildMenu) view.findViewById(id.MenuItemZSL);
        nonZSLmanualMode = (SettingsChildMenu) view.findViewById(id.MenuItemNonManualZSL);
        correlatedDoubleSampling = (SettingsChildMenu) view.findViewById(id.MenuItemCorrelatedDoubleSampling);
        temporalDenoise = (SettingsChildMenu) view.findViewById(id.MenuItemTemporalDenoise);
        edgeMode = (SettingsChildMenu) view.findViewById(id.MenuItemEdgeMode);
        hotPixelMode = (SettingsChildMenu) view.findViewById(id.MenuItemHotPixelMode);
        opticalImageStabilization = (SettingsChildMenu) view.findViewById(id.MenuItemOIS);
        LensFilter = (SettingsChildMenu) view.findViewById(id.LensFilter);
        zoomSetting = (SettingsChildMenu) view.findViewById(id.MenuItemZoomSetting);
        scalePreview = (SettingsChildMenu)view.findViewById(id.MenuItemScalePreview);
        setCameraUiWrapperToUi();
        return view;
    }


    @Override
    protected void setCameraUiWrapperToUi()
    {
        if (cameraUiWrapper == null)
            return;
        scene.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_SCENEMODE);
        scene.SetParameter(cameraUiWrapper.GetParameterHandler().SceneMode);
        scene.SetUiItemClickListner(this);

        color.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_COLORMODE);
        color.SetParameter(cameraUiWrapper.GetParameterHandler().ColorMode);
        color.SetUiItemClickListner(this);

        cctMode.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_COLORCORRECTION);
        cctMode.SetParameter(cameraUiWrapper.GetParameterHandler().ColorCorrectionMode);
        cctMode.SetUiItemClickListner(this);

        objectTrackingMode.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_OBJECTTRACKING);
        objectTrackingMode.SetParameter(cameraUiWrapper.GetParameterHandler().ObjectTracking);
        objectTrackingMode.SetUiItemClickListner(this);

        toneMapMode.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_TONEMAP);
        toneMapMode.SetParameter(cameraUiWrapper.GetParameterHandler().ToneMapMode);
        toneMapMode.SetUiItemClickListner(this);

        postViewSize.SetStuff(fragment_activityInterface, "");
        postViewSize.SetParameter(cameraUiWrapper.GetParameterHandler().PostViewSize);
        postViewSize.SetUiItemClickListner(this);

        controleMode.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_CONTROLMODE);
        controleMode.SetParameter(cameraUiWrapper.GetParameterHandler().ControlMode);
        controleMode.SetUiItemClickListner(this);

        redeyeflash.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_REDEYE_MODE);
        redeyeflash.SetParameter(cameraUiWrapper.GetParameterHandler().RedEye);
        redeyeflash.SetUiItemClickListner(this);

        antiBanding.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_ANTIBANDINGMODE);
        antiBanding.SetParameter(cameraUiWrapper.GetParameterHandler().AntiBandingMode);
        antiBanding.SetUiItemClickListner(this);

        ipp.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_IMAGEPOSTPROCESSINGMODE);
        ipp.SetParameter(cameraUiWrapper.GetParameterHandler().ImagePostProcessing);
        ipp.SetUiItemClickListner(this);

        lensShade.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_LENSSHADE_MODE);
        lensShade.SetParameter(cameraUiWrapper.GetParameterHandler().LensShade);
        lensShade.SetUiItemClickListner(this);

        sceneDetectMode.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_SCENEDETECT_MODE);
        sceneDetectMode.SetParameter(cameraUiWrapper.GetParameterHandler().SceneDetect);
        sceneDetectMode.SetUiItemClickListner(this);

        waveletdenoiseMode.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_DENOISE_MODE);
        waveletdenoiseMode.SetParameter(cameraUiWrapper.GetParameterHandler().Denoise);
        waveletdenoiseMode.SetUiItemClickListner(this);

        LensFilter.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_Filter);
        LensFilter.SetParameter(cameraUiWrapper.GetParameterHandler().LensFilter);
        LensFilter.SetUiItemClickListner(this);

        digitalImageStabilization.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_DIS_MODE);
        digitalImageStabilization.SetParameter(cameraUiWrapper.GetParameterHandler().DigitalImageStabilization);
        digitalImageStabilization.SetUiItemClickListner(this);

        memoryColorEnhancement.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_MCE_MODE);
        memoryColorEnhancement.SetParameter(cameraUiWrapper.GetParameterHandler().MemoryColorEnhancement);
        memoryColorEnhancement.SetUiItemClickListner(this);

        ZeroShutterLag.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_ZEROSHUTTERLAG_MODE);
        ZeroShutterLag.SetParameter(cameraUiWrapper.GetParameterHandler().ZSL);
        ZeroShutterLag.SetUiItemClickListner(this);

        nonZSLmanualMode.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_NONZSLMANUALMODE);
        nonZSLmanualMode.SetParameter(cameraUiWrapper.GetParameterHandler().NonZslManualMode);
        nonZSLmanualMode.SetUiItemClickListner(this);

        correlatedDoubleSampling.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_CDS);
        correlatedDoubleSampling.SetParameter(cameraUiWrapper.GetParameterHandler().CDS_Mode);
        correlatedDoubleSampling.SetUiItemClickListner(this);

        temporalDenoise.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_TNR);
        temporalDenoise.SetParameter(cameraUiWrapper.GetParameterHandler().TnrMode);
        temporalDenoise.SetUiItemClickListner(this);

        edgeMode.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_EDGE);
        edgeMode.SetParameter(cameraUiWrapper.GetParameterHandler().EdgeMode);
        edgeMode.SetUiItemClickListner(this);

        hotPixelMode.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_HOTPIXEL);
        hotPixelMode.SetParameter(cameraUiWrapper.GetParameterHandler().HotPixelMode);
        hotPixelMode.SetUiItemClickListner(this);

        opticalImageStabilization.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_OIS);
        opticalImageStabilization.SetParameter(cameraUiWrapper.GetParameterHandler().oismode);
        opticalImageStabilization.SetUiItemClickListner(this);

        zoomSetting.SetStuff(fragment_activityInterface, null);
        zoomSetting.SetParameter(cameraUiWrapper.GetParameterHandler().ZoomSetting);
        zoomSetting.SetUiItemClickListner(this);

        scalePreview.SetStuff(fragment_activityInterface, null);
        scalePreview.SetParameter(cameraUiWrapper.GetParameterHandler().scalePreview);
        scalePreview.SetUiItemClickListner(this);
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
