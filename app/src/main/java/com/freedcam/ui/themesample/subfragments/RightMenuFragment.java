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
import com.freedcam.ui.I_Activity;
import com.freedcam.ui.themesample.views.menu.MenuItem;
import com.freedcam.ui.themesample.views.uichilds.UiSettingsChild;
import com.freedcam.utils.AppSettingsManager;
import com.troop.freedcam.R;

/**
 * Created by troop on 15.06.2015.
 */
public class RightMenuFragment extends AbstractFragment implements Interfaces.I_MenuItemClick
{
    private final static String TAG = RightMenuFragment.class.getSimpleName();
    private Interfaces.I_MenuItemClick onMenuItemClick;
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

    public static RightMenuFragment GetInstance(I_Activity i_activity, AppSettingsManager appSettingsManager)
    {
        RightMenuFragment s = new RightMenuFragment();
        s.i_activity = i_activity;
        s.appSettingsManager = appSettingsManager;
        return s;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        this.view = inflater.inflate(R.layout.rightmenufragment, container, false);
        scene = (MenuItem)view.findViewById(R.id.MenuItemScene);
        color = (MenuItem)view.findViewById(R.id.MenuItemColor);
        cctMode = (MenuItem)view.findViewById(R.id.MenuItemCCTMode);
        objectTrackingMode = (MenuItem)view.findViewById(R.id.MenuItemObjectTracking);
        toneMapMode = (MenuItem)view.findViewById(R.id.MenuItemTonemap);
        postViewSize = (MenuItem)view.findViewById(R.id.MenuItemPostViewSize);
        controleMode = (MenuItem)view.findViewById(R.id.MenuItemControlMode);
        redeyeflash = (MenuItem)view.findViewById(R.id.MenuItemRedEye);
        antiBanding = (MenuItem)view.findViewById(R.id.MenuItemAntiBanding);
        ipp = (MenuItem)view.findViewById(R.id.MenuItemIpp);
        lensShade = (MenuItem)view.findViewById(R.id.MenuItemLensShade);
        sceneDetectMode = (MenuItem)view.findViewById(R.id.MenuItemSceneDetection);
        waveletdenoiseMode = (MenuItem)view.findViewById(R.id.MenuItemWaveletDenoise);
        digitalImageStabilization = (MenuItem)view.findViewById(R.id.MenuItemDigitalImageStab);
        memoryColorEnhancement = (MenuItem)view.findViewById(R.id.MenuItemMemoryColorEnhanc);
        ZeroShutterLag = (MenuItem)view.findViewById(R.id.MenuItemZSL);
        nonZSLmanualMode = (MenuItem)view.findViewById(R.id.MenuItemNonManualZSL);
        correlatedDoubleSampling = (MenuItem)view.findViewById(R.id.MenuItemCorrelatedDoubleSampling);
        temporalDenoise = (MenuItem)view.findViewById(R.id.MenuItemTemporalDenoise);
        edgeMode = (MenuItem)view.findViewById(R.id.MenuItemEdgeMode);
        hotPixelMode = (MenuItem)view.findViewById(R.id.MenuItemHotPixelMode);
        opticalImageStabilization = (MenuItem)view.findViewById(R.id.MenuItemOIS);
        LensFilter = (MenuItem)view.findViewById(R.id.LensFilter);
        zoomSetting = (MenuItem)view.findViewById(R.id.MenuItemZoomSetting);
        setCameraUiWrapperToUi();
        return view;
    }


    @Override
    protected void setCameraUiWrapperToUi()
    {
        if (cameraUiWrapper == null)
            return;
        scene.SetStuff(i_activity, AppSettingsManager.SETTING_SCENEMODE,appSettingsManager);
        scene.SetParameter(cameraUiWrapper.parametersHandler.SceneMode);
        scene.SetMenuItemListner(this);

        color.SetStuff(i_activity, AppSettingsManager.SETTING_COLORMODE,appSettingsManager);
        color.SetParameter(cameraUiWrapper.parametersHandler.ColorMode);
        color.SetMenuItemListner(this);

        cctMode.SetStuff(i_activity, AppSettingsManager.SETTING_COLORCORRECTION,appSettingsManager);
        cctMode.SetParameter(cameraUiWrapper.parametersHandler.ColorCorrectionMode);
        cctMode.SetMenuItemListner(this);

        objectTrackingMode.SetStuff(i_activity, AppSettingsManager.SETTING_OBJECTTRACKING,appSettingsManager);
        objectTrackingMode.SetParameter(cameraUiWrapper.parametersHandler.ObjectTracking);
        objectTrackingMode.SetMenuItemListner(this);

        toneMapMode.SetStuff(i_activity, AppSettingsManager.SETTING_TONEMAP,appSettingsManager);
        toneMapMode.SetParameter(cameraUiWrapper.parametersHandler.ToneMapMode);
        toneMapMode.SetMenuItemListner(this);

        postViewSize.SetStuff(i_activity, "",appSettingsManager);
        postViewSize.SetParameter(cameraUiWrapper.parametersHandler.PostViewSize);
        postViewSize.SetMenuItemListner(this);

        controleMode.SetStuff(i_activity, AppSettingsManager.SETTING_CONTROLMODE,appSettingsManager);
        controleMode.SetParameter(cameraUiWrapper.parametersHandler.ControlMode);
        controleMode.SetMenuItemListner(this);

        redeyeflash.SetStuff(i_activity, AppSettingsManager.SETTING_REDEYE_MODE,appSettingsManager);
        redeyeflash.SetParameter(cameraUiWrapper.parametersHandler.RedEye);
        redeyeflash.SetMenuItemListner(this);

        antiBanding.SetStuff(i_activity, AppSettingsManager.SETTING_ANTIBANDINGMODE,appSettingsManager);
        antiBanding.SetParameter(cameraUiWrapper.parametersHandler.AntiBandingMode);
        antiBanding.SetMenuItemListner(this);

        ipp.SetStuff(i_activity, AppSettingsManager.SETTING_IMAGEPOSTPROCESSINGMODE,appSettingsManager);
        ipp.SetParameter(cameraUiWrapper.parametersHandler.ImagePostProcessing);
        ipp.SetMenuItemListner(this);

        lensShade.SetStuff(i_activity, AppSettingsManager.SETTING_LENSSHADE_MODE,appSettingsManager);
        lensShade.SetParameter(cameraUiWrapper.parametersHandler.LensShade);
        lensShade.SetMenuItemListner(this);

        sceneDetectMode.SetStuff(i_activity, AppSettingsManager.SETTING_SCENEDETECT_MODE,appSettingsManager);
        sceneDetectMode.SetParameter(cameraUiWrapper.parametersHandler.SceneDetect);
        sceneDetectMode.SetMenuItemListner(this);

        waveletdenoiseMode.SetStuff(i_activity, AppSettingsManager.SETTING_DENOISE_MODE,appSettingsManager);
        waveletdenoiseMode.SetParameter(cameraUiWrapper.parametersHandler.Denoise);
        waveletdenoiseMode.SetMenuItemListner(this);

        LensFilter.SetStuff(i_activity, AppSettingsManager.SETTING_Filter,appSettingsManager);
        LensFilter.SetParameter(cameraUiWrapper.parametersHandler.LensFilter);
        LensFilter.SetMenuItemListner(this);

        digitalImageStabilization.SetStuff(i_activity, AppSettingsManager.SETTING_DIS_MODE,appSettingsManager);
        digitalImageStabilization.SetParameter(cameraUiWrapper.parametersHandler.DigitalImageStabilization);
        digitalImageStabilization.SetMenuItemListner(this);

        memoryColorEnhancement.SetStuff(i_activity, AppSettingsManager.SETTING_MCE_MODE,appSettingsManager);
        memoryColorEnhancement.SetParameter(cameraUiWrapper.parametersHandler.MemoryColorEnhancement);
        memoryColorEnhancement.SetMenuItemListner(this);

        ZeroShutterLag.SetStuff(i_activity, AppSettingsManager.SETTING_ZEROSHUTTERLAG_MODE,appSettingsManager);
        ZeroShutterLag.SetParameter(cameraUiWrapper.parametersHandler.ZSL);
        ZeroShutterLag.SetMenuItemListner(this);

        nonZSLmanualMode.SetStuff(i_activity, AppSettingsManager.SETTING_NONZSLMANUALMODE,appSettingsManager);
        nonZSLmanualMode.SetParameter(cameraUiWrapper.parametersHandler.NonZslManualMode);
        nonZSLmanualMode.SetMenuItemListner(this);

        correlatedDoubleSampling.SetStuff(i_activity, AppSettingsManager.SETTING_CDS,appSettingsManager);
        correlatedDoubleSampling.SetParameter(cameraUiWrapper.parametersHandler.CDS_Mode);
        correlatedDoubleSampling.SetMenuItemListner(this);

        temporalDenoise.SetStuff(i_activity, AppSettingsManager.SETTING_TNR,appSettingsManager);
        temporalDenoise.SetParameter(cameraUiWrapper.parametersHandler.TnrMode);
        temporalDenoise.SetMenuItemListner(this);

        edgeMode.SetStuff(i_activity, AppSettingsManager.SETTING_EDGE,appSettingsManager);
        edgeMode.SetParameter(cameraUiWrapper.parametersHandler.EdgeMode);
        edgeMode.SetMenuItemListner(this);

        hotPixelMode.SetStuff(i_activity, AppSettingsManager.SETTING_HOTPIXEL,appSettingsManager);
        hotPixelMode.SetParameter(cameraUiWrapper.parametersHandler.HotPixelMode);
        hotPixelMode.SetMenuItemListner(this);

        opticalImageStabilization.SetStuff(i_activity, AppSettingsManager.SETTING_OIS,appSettingsManager);
        opticalImageStabilization.SetParameter(cameraUiWrapper.parametersHandler.oismode);
        opticalImageStabilization.SetMenuItemListner(this);

        zoomSetting.SetStuff(i_activity, null,appSettingsManager);
        zoomSetting.SetParameter(cameraUiWrapper.parametersHandler.ZoomSetting);
        zoomSetting.SetMenuItemListner(this);
    }

    public void SetMenuItemClickListner(Interfaces.I_MenuItemClick menuItemClick)
    {
        this.onMenuItemClick = menuItemClick;
    }

    @Override
    public void onMenuItemClick(UiSettingsChild item, boolean fromLeftFragment) {
        onMenuItemClick.onMenuItemClick(item, false);
    }
}
