package com.freedcam.ui.themesample.subfragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.freedcam.apis.i_camera.AbstractCameraUiWrapper;
import com.freedcam.ui.AbstractFragment;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.ui.themesample.views.menu.MenuItem;
import com.freedcam.ui.themesample.views.uichilds.UiSettingsChild;
import com.freedcam.utils.Logger;
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

    private ScrollView scrollView;
    private FrameLayout settingsMenu;
    private final String KEY_SETTINGSOPEN = "key_settingsopen";
    private SharedPreferences sharedPref;
    private boolean settingsOpen;
    private LinearLayout leftholder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.view = inflater.inflate(R.layout.rightmenufragment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scene = (MenuItem)view.findViewById(R.id.MenuItemScene);
        scene.SetStuff(i_activity, AppSettingsManager.SETTING_SCENEMODE);

        color = (MenuItem)view.findViewById(R.id.MenuItemColor);
        color.SetStuff(i_activity, AppSettingsManager.SETTING_COLORMODE);

        cctMode = (MenuItem)view.findViewById(R.id.MenuItemCCTMode);
        cctMode.SetStuff(i_activity, AppSettingsManager.SETTING_COLORCORRECTION);

        objectTrackingMode = (MenuItem)view.findViewById(R.id.MenuItemObjectTracking);
        objectTrackingMode.SetStuff(i_activity, AppSettingsManager.SETTING_OBJECTTRACKING);

        toneMapMode = (MenuItem)view.findViewById(R.id.MenuItemTonemap);
        toneMapMode.SetStuff(i_activity, AppSettingsManager.SETTING_TONEMAP);

        postViewSize = (MenuItem)view.findViewById(R.id.MenuItemPostViewSize);
        postViewSize.SetStuff(i_activity, "");

        controleMode = (MenuItem)view.findViewById(R.id.MenuItemControlMode);
        controleMode.SetStuff(i_activity, AppSettingsManager.SETTING_CONTROLMODE);

        redeyeflash = (MenuItem)view.findViewById(R.id.MenuItemRedEye);
        redeyeflash.SetStuff(i_activity, AppSettingsManager.SETTING_REDEYE_MODE);

        antiBanding = (MenuItem)view.findViewById(R.id.MenuItemAntiBanding);
        antiBanding.SetStuff(i_activity, AppSettingsManager.SETTING_ANTIBANDINGMODE);

        ipp = (MenuItem)view.findViewById(R.id.MenuItemIpp);
        ipp.SetStuff(i_activity, AppSettingsManager.SETTING_IMAGEPOSTPROCESSINGMODE);

        lensShade = (MenuItem)view.findViewById(R.id.MenuItemLensShade);
        lensShade.SetStuff(i_activity, AppSettingsManager.SETTING_LENSSHADE_MODE);

        sceneDetectMode = (MenuItem)view.findViewById(R.id.MenuItemSceneDetection);
        sceneDetectMode.SetStuff(i_activity, AppSettingsManager.SETTING_SCENEDETECT_MODE);

        waveletdenoiseMode = (MenuItem)view.findViewById(R.id.MenuItemWaveletDenoise);
        waveletdenoiseMode.SetStuff(i_activity, AppSettingsManager.SETTING_DENOISE_MODE);

        digitalImageStabilization = (MenuItem)view.findViewById(R.id.MenuItemDigitalImageStab);
        digitalImageStabilization.SetStuff(i_activity, AppSettingsManager.SETTING_DIS_MODE);

        memoryColorEnhancement = (MenuItem)view.findViewById(R.id.MenuItemMemoryColorEnhanc);
        memoryColorEnhancement.SetStuff(i_activity, AppSettingsManager.SETTING_MCE_MODE);

        ZeroShutterLag = (MenuItem)view.findViewById(R.id.MenuItemZSL);
        ZeroShutterLag.SetStuff(i_activity, AppSettingsManager.SETTING_ZEROSHUTTERLAG_MODE);

        nonZSLmanualMode = (MenuItem)view.findViewById(R.id.MenuItemNonManualZSL);
        nonZSLmanualMode.SetStuff(i_activity, AppSettingsManager.SETTING_NONZSLMANUALMODE);

        correlatedDoubleSampling = (MenuItem)view.findViewById(R.id.MenuItemCorrelatedDoubleSampling);
        correlatedDoubleSampling.SetStuff(i_activity, AppSettingsManager.SETTING_CDS);

        temporalDenoise = (MenuItem)view.findViewById(R.id.MenuItemTemporalDenoise);
        temporalDenoise.SetStuff(i_activity, AppSettingsManager.SETTING_TNR);

        edgeMode = (MenuItem)view.findViewById(R.id.MenuItemEdgeMode);
        edgeMode.SetStuff(i_activity, AppSettingsManager.SETTING_EDGE);

        hotPixelMode = (MenuItem)view.findViewById(R.id.MenuItemHotPixelMode);
        hotPixelMode.SetStuff(i_activity, AppSettingsManager.SETTING_HOTPIXEL);

        opticalImageStabilization = (MenuItem)view.findViewById(R.id.MenuItemOIS);
        opticalImageStabilization.SetStuff(i_activity, AppSettingsManager.SETTING_OIS);

        LensFilter = (MenuItem)view.findViewById(R.id.LensFilter);
        LensFilter.SetStuff(i_activity, AppSettingsManager.SETTING_Filter);

        scrollView = (ScrollView) view.findViewById(R.id.scrollView2);
        settingsMenu =  (FrameLayout)getActivity().findViewById(R.id.settingsMenuHolder);

        zoomSetting = (MenuItem)view.findViewById(R.id.MenuItemZoomSetting);
        zoomSetting.SetStuff(i_activity, null);

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        settingsOpen = sharedPref.getBoolean(KEY_SETTINGSOPEN, false);
        leftholder = (LinearLayout) getActivity().findViewById(R.id.guideHolder);


    }

    @Override
    public void onResume() {
        super.onResume();
        setWrapper();
    }

    private void setWrapper()
    {
        Logger.d(TAG, "setWrapper");
        if (wrapper == null)
            return;
        scene.SetParameter(wrapper.camParametersHandler.SceneMode);
        scene.SetMenuItemListner(this);

        color.SetParameter(wrapper.camParametersHandler.ColorMode);
        color.SetMenuItemListner(this);

        cctMode.SetParameter(wrapper.camParametersHandler.ColorCorrectionMode);
        cctMode.SetMenuItemListner(this);

        objectTrackingMode.SetParameter(wrapper.camParametersHandler.ObjectTracking);
        objectTrackingMode.SetMenuItemListner(this);

        toneMapMode.SetParameter(wrapper.camParametersHandler.ToneMapMode);
        toneMapMode.SetMenuItemListner(this);

        postViewSize.SetParameter(wrapper.camParametersHandler.PostViewSize);
        postViewSize.SetMenuItemListner(this);

        controleMode.SetParameter(wrapper.camParametersHandler.ControlMode);
        controleMode.SetMenuItemListner(this);

        redeyeflash.SetParameter(wrapper.camParametersHandler.RedEye);
        redeyeflash.SetMenuItemListner(this);

        antiBanding.SetParameter(wrapper.camParametersHandler.AntiBandingMode);
        antiBanding.SetMenuItemListner(this);

        ipp.SetParameter(wrapper.camParametersHandler.ImagePostProcessing);
        ipp.SetMenuItemListner(this);

        lensShade.SetParameter(wrapper.camParametersHandler.LensShade);
        lensShade.SetMenuItemListner(this);

        sceneDetectMode.SetParameter(wrapper.camParametersHandler.SceneDetect);
        sceneDetectMode.SetMenuItemListner(this);

        waveletdenoiseMode.SetParameter(wrapper.camParametersHandler.Denoise);
        waveletdenoiseMode.SetMenuItemListner(this);

        LensFilter.SetParameter(wrapper.camParametersHandler.LensFilter);
        LensFilter.SetMenuItemListner(this);

        digitalImageStabilization.SetParameter(wrapper.camParametersHandler.DigitalImageStabilization);
        digitalImageStabilization.SetMenuItemListner(this);

        memoryColorEnhancement.SetParameter(wrapper.camParametersHandler.MemoryColorEnhancement);
        memoryColorEnhancement.SetMenuItemListner(this);

        ZeroShutterLag.SetParameter(wrapper.camParametersHandler.ZSL);
        ZeroShutterLag.SetMenuItemListner(this);

        nonZSLmanualMode.SetParameter(wrapper.camParametersHandler.NonZslManualMode);
        nonZSLmanualMode.SetMenuItemListner(this);

        correlatedDoubleSampling.SetParameter(wrapper.camParametersHandler.CDS_Mode);
        correlatedDoubleSampling.SetMenuItemListner(this);

        temporalDenoise.SetParameter(wrapper.camParametersHandler.TnrMode);
        temporalDenoise.SetMenuItemListner(this);

        edgeMode.SetParameter(wrapper.camParametersHandler.EdgeMode);
        edgeMode.SetMenuItemListner(this);

        hotPixelMode.SetParameter(wrapper.camParametersHandler.HotPixelMode);
        hotPixelMode.SetMenuItemListner(this);

        opticalImageStabilization.SetParameter(wrapper.camParametersHandler.oismode);
        opticalImageStabilization.SetMenuItemListner(this);

        zoomSetting.SetParameter(wrapper.camParametersHandler.ZoomSetting);
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

    @Override
    public void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper) {
        super.SetCameraUIWrapper(wrapper);
        if (view != null)
            setWrapper();
    }
}
