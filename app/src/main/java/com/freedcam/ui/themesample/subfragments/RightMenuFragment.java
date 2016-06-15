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

import com.freedcam.ui.AbstractFragment;
import com.freedcam.ui.themesample.subfragments.Interfaces.I_MenuItemClick;
import com.freedcam.ui.themesample.views.menu.MenuItem;
import com.freedcam.ui.themesample.views.uichilds.UiSettingsChild;
import com.freedcam.utils.AppSettingsManager;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

/**
 * Created by troop on 15.06.2015.
 */
public class RightMenuFragment extends AbstractFragment implements I_MenuItemClick
{
    private static final String TAG = RightMenuFragment.class.getSimpleName();
    private I_MenuItemClick onMenuItemClick;
    private MenuItem scene;
    private MenuItem color;
    private MenuItem cctMode;
    private MenuItem objectTrackingMode;
    private MenuItem toneMapMode;
    private MenuItem postViewSize;
    private MenuItem controleMode;

    private MenuItem antiBanding;
    private MenuItem ipp;
    private MenuItem lensShade;
    private MenuItem sceneDetectMode;
    private MenuItem waveletdenoiseMode;
    private MenuItem digitalImageStabilization;
    private MenuItem memoryColorEnhancement;
    private MenuItem ZeroShutterLag;
    private MenuItem nonZSLmanualMode;
    private MenuItem correlatedDoubleSampling;
    private MenuItem temporalDenoise;
    private MenuItem edgeMode;
    private MenuItem hotPixelMode;
    private MenuItem opticalImageStabilization;
    private MenuItem zoomSetting;
    private MenuItem redeyeflash;

    private MenuItem LensFilter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        view = inflater.inflate(layout.rightmenufragment, container, false);
        scene = (MenuItem) view.findViewById(id.MenuItemScene);
        color = (MenuItem) view.findViewById(id.MenuItemColor);
        cctMode = (MenuItem) view.findViewById(id.MenuItemCCTMode);
        objectTrackingMode = (MenuItem) view.findViewById(id.MenuItemObjectTracking);
        toneMapMode = (MenuItem) view.findViewById(id.MenuItemTonemap);
        postViewSize = (MenuItem) view.findViewById(id.MenuItemPostViewSize);
        controleMode = (MenuItem) view.findViewById(id.MenuItemControlMode);
        redeyeflash = (MenuItem) view.findViewById(id.MenuItemRedEye);
        antiBanding = (MenuItem) view.findViewById(id.MenuItemAntiBanding);
        ipp = (MenuItem) view.findViewById(id.MenuItemIpp);
        lensShade = (MenuItem) view.findViewById(id.MenuItemLensShade);
        sceneDetectMode = (MenuItem) view.findViewById(id.MenuItemSceneDetection);
        waveletdenoiseMode = (MenuItem) view.findViewById(id.MenuItemWaveletDenoise);
        digitalImageStabilization = (MenuItem) view.findViewById(id.MenuItemDigitalImageStab);
        memoryColorEnhancement = (MenuItem) view.findViewById(id.MenuItemMemoryColorEnhanc);
        ZeroShutterLag = (MenuItem) view.findViewById(id.MenuItemZSL);
        nonZSLmanualMode = (MenuItem) view.findViewById(id.MenuItemNonManualZSL);
        correlatedDoubleSampling = (MenuItem) view.findViewById(id.MenuItemCorrelatedDoubleSampling);
        temporalDenoise = (MenuItem) view.findViewById(id.MenuItemTemporalDenoise);
        edgeMode = (MenuItem) view.findViewById(id.MenuItemEdgeMode);
        hotPixelMode = (MenuItem) view.findViewById(id.MenuItemHotPixelMode);
        opticalImageStabilization = (MenuItem) view.findViewById(id.MenuItemOIS);
        LensFilter = (MenuItem) view.findViewById(id.LensFilter);
        zoomSetting = (MenuItem) view.findViewById(id.MenuItemZoomSetting);
        setCameraUiWrapperToUi();
        return view;
    }


    @Override
    protected void setCameraUiWrapperToUi()
    {
        if (cameraUiWrapper == null)
            return;
        scene.SetStuff(i_activity, AppSettingsManager.SETTING_SCENEMODE);
        scene.SetParameter(cameraUiWrapper.GetParameterHandler().SceneMode);
        scene.SetMenuItemListner(this);

        color.SetStuff(i_activity, AppSettingsManager.SETTING_COLORMODE);
        color.SetParameter(cameraUiWrapper.GetParameterHandler().ColorMode);
        color.SetMenuItemListner(this);

        cctMode.SetStuff(i_activity, AppSettingsManager.SETTING_COLORCORRECTION);
        cctMode.SetParameter(cameraUiWrapper.GetParameterHandler().ColorCorrectionMode);
        cctMode.SetMenuItemListner(this);

        objectTrackingMode.SetStuff(i_activity, AppSettingsManager.SETTING_OBJECTTRACKING);
        objectTrackingMode.SetParameter(cameraUiWrapper.GetParameterHandler().ObjectTracking);
        objectTrackingMode.SetMenuItemListner(this);

        toneMapMode.SetStuff(i_activity, AppSettingsManager.SETTING_TONEMAP);
        toneMapMode.SetParameter(cameraUiWrapper.GetParameterHandler().ToneMapMode);
        toneMapMode.SetMenuItemListner(this);

        postViewSize.SetStuff(i_activity, "");
        postViewSize.SetParameter(cameraUiWrapper.GetParameterHandler().PostViewSize);
        postViewSize.SetMenuItemListner(this);

        controleMode.SetStuff(i_activity, AppSettingsManager.SETTING_CONTROLMODE);
        controleMode.SetParameter(cameraUiWrapper.GetParameterHandler().ControlMode);
        controleMode.SetMenuItemListner(this);

        redeyeflash.SetStuff(i_activity, AppSettingsManager.SETTING_REDEYE_MODE);
        redeyeflash.SetParameter(cameraUiWrapper.GetParameterHandler().RedEye);
        redeyeflash.SetMenuItemListner(this);

        antiBanding.SetStuff(i_activity, AppSettingsManager.SETTING_ANTIBANDINGMODE);
        antiBanding.SetParameter(cameraUiWrapper.GetParameterHandler().AntiBandingMode);
        antiBanding.SetMenuItemListner(this);

        ipp.SetStuff(i_activity, AppSettingsManager.SETTING_IMAGEPOSTPROCESSINGMODE);
        ipp.SetParameter(cameraUiWrapper.GetParameterHandler().ImagePostProcessing);
        ipp.SetMenuItemListner(this);

        lensShade.SetStuff(i_activity, AppSettingsManager.SETTING_LENSSHADE_MODE);
        lensShade.SetParameter(cameraUiWrapper.GetParameterHandler().LensShade);
        lensShade.SetMenuItemListner(this);

        sceneDetectMode.SetStuff(i_activity, AppSettingsManager.SETTING_SCENEDETECT_MODE);
        sceneDetectMode.SetParameter(cameraUiWrapper.GetParameterHandler().SceneDetect);
        sceneDetectMode.SetMenuItemListner(this);

        waveletdenoiseMode.SetStuff(i_activity, AppSettingsManager.SETTING_DENOISE_MODE);
        waveletdenoiseMode.SetParameter(cameraUiWrapper.GetParameterHandler().Denoise);
        waveletdenoiseMode.SetMenuItemListner(this);

        LensFilter.SetStuff(i_activity, AppSettingsManager.SETTING_Filter);
        LensFilter.SetParameter(cameraUiWrapper.GetParameterHandler().LensFilter);
        LensFilter.SetMenuItemListner(this);

        digitalImageStabilization.SetStuff(i_activity, AppSettingsManager.SETTING_DIS_MODE);
        digitalImageStabilization.SetParameter(cameraUiWrapper.GetParameterHandler().DigitalImageStabilization);
        digitalImageStabilization.SetMenuItemListner(this);

        memoryColorEnhancement.SetStuff(i_activity, AppSettingsManager.SETTING_MCE_MODE);
        memoryColorEnhancement.SetParameter(cameraUiWrapper.GetParameterHandler().MemoryColorEnhancement);
        memoryColorEnhancement.SetMenuItemListner(this);

        ZeroShutterLag.SetStuff(i_activity, AppSettingsManager.SETTING_ZEROSHUTTERLAG_MODE);
        ZeroShutterLag.SetParameter(cameraUiWrapper.GetParameterHandler().ZSL);
        ZeroShutterLag.SetMenuItemListner(this);

        nonZSLmanualMode.SetStuff(i_activity, AppSettingsManager.SETTING_NONZSLMANUALMODE);
        nonZSLmanualMode.SetParameter(cameraUiWrapper.GetParameterHandler().NonZslManualMode);
        nonZSLmanualMode.SetMenuItemListner(this);

        correlatedDoubleSampling.SetStuff(i_activity, AppSettingsManager.SETTING_CDS);
        correlatedDoubleSampling.SetParameter(cameraUiWrapper.GetParameterHandler().CDS_Mode);
        correlatedDoubleSampling.SetMenuItemListner(this);

        temporalDenoise.SetStuff(i_activity, AppSettingsManager.SETTING_TNR);
        temporalDenoise.SetParameter(cameraUiWrapper.GetParameterHandler().TnrMode);
        temporalDenoise.SetMenuItemListner(this);

        edgeMode.SetStuff(i_activity, AppSettingsManager.SETTING_EDGE);
        edgeMode.SetParameter(cameraUiWrapper.GetParameterHandler().EdgeMode);
        edgeMode.SetMenuItemListner(this);

        hotPixelMode.SetStuff(i_activity, AppSettingsManager.SETTING_HOTPIXEL);
        hotPixelMode.SetParameter(cameraUiWrapper.GetParameterHandler().HotPixelMode);
        hotPixelMode.SetMenuItemListner(this);

        opticalImageStabilization.SetStuff(i_activity, AppSettingsManager.SETTING_OIS);
        opticalImageStabilization.SetParameter(cameraUiWrapper.GetParameterHandler().oismode);
        opticalImageStabilization.SetMenuItemListner(this);

        zoomSetting.SetStuff(i_activity, null);
        zoomSetting.SetParameter(cameraUiWrapper.GetParameterHandler().ZoomSetting);
        zoomSetting.SetMenuItemListner(this);
    }

    public void SetMenuItemClickListner(I_MenuItemClick menuItemClick)
    {
        onMenuItemClick = menuItemClick;
    }

    @Override
    public void onMenuItemClick(UiSettingsChild item, boolean fromLeftFragment) {
        onMenuItemClick.onMenuItemClick(item, false);
    }
}
