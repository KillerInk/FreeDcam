package troop.com.themesample.subfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.ui.AbstractFragment;
import com.troop.freedcam.ui.AppSettingsManager;

import troop.com.themesample.R;
import troop.com.themesample.views.menu.MenuItem;
import troop.com.themesample.views.uichilds.UiSettingsChild;

/**
 * Created by troop on 15.06.2015.
 */
public class RightMenuFragment extends AbstractFragment implements Interfaces.I_MenuItemClick
{

    Interfaces.I_MenuItemClick onMenuItemClick;
    MenuItem scene;
    MenuItem color;
    MenuItem cctMode;
    MenuItem objectTrackingMode;
    MenuItem toneMapMode;
    MenuItem postViewSize;
    MenuItem controleMode;

    MenuItem antiBanding;
    MenuItem ipp;
    MenuItem lensShade;
    MenuItem sceneDectecMode;
    MenuItem waveletdenoiseMode;
    MenuItem digitalImageStabilization;
    MenuItem memoryColorEnhancement;
    MenuItem ZeroShutterLag;
    MenuItem nonZSLmanualMode;
    MenuItem correlatedDoubleSampling;
    MenuItem temporalDenoise;
    MenuItem edgeMode;
    MenuItem hotPixelMode;
    MenuItem opticalImageStabilization;
    troop.com.themesample.views.menu.MenuItem redeyeflash;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.view = inflater.inflate(R.layout.rightmenufragment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(wrapper != null)
            setWrapper();
    }

    private void setWrapper()
    {
        scene = (MenuItem)view.findViewById(R.id.MenuItemScene);
        scene.SetStuff(i_activity,appSettingsManager, AppSettingsManager.SETTING_SCENEMODE);
        scene.SetParameter(wrapper.camParametersHandler.SceneMode);
        scene.SetMenuItemListner(this);

        color = (MenuItem)view.findViewById(R.id.MenuItemColor);
        color.SetStuff(i_activity,appSettingsManager, AppSettingsManager.SETTING_COLORMODE);
        color.SetParameter(wrapper.camParametersHandler.ColorMode);
        color.SetMenuItemListner(this);

        cctMode = (MenuItem)view.findViewById(R.id.MenuItemCCTMode);
        cctMode.SetStuff(i_activity,appSettingsManager, AppSettingsManager.SETTING_COLORCORRECTION);
        cctMode.SetParameter(wrapper.camParametersHandler.ColorCorrectionMode);
        cctMode.SetMenuItemListner(this);

        objectTrackingMode = (MenuItem)view.findViewById(R.id.MenuItemObjectTracking);
        objectTrackingMode.SetStuff(i_activity,appSettingsManager, AppSettingsManager.SETTING_OBJECTTRACKING);
        objectTrackingMode.SetParameter(wrapper.camParametersHandler.ObjectTracking);
        objectTrackingMode.SetMenuItemListner(this);

        toneMapMode = (MenuItem)view.findViewById(R.id.MenuItemTonemap);
        toneMapMode.SetStuff(i_activity,appSettingsManager, AppSettingsManager.SETTING_TONEMAP);
        toneMapMode.SetParameter(wrapper.camParametersHandler.ToneMapMode);
        toneMapMode.SetMenuItemListner(this);

        postViewSize = (MenuItem)view.findViewById(R.id.MenuItemPostViewSize);
        postViewSize.SetStuff(i_activity,appSettingsManager, "");
        postViewSize.SetParameter(wrapper.camParametersHandler.PostViewSize);
        postViewSize.SetMenuItemListner(this);

        controleMode = (MenuItem)view.findViewById(R.id.MenuItemControlMode);
        controleMode.SetStuff(i_activity,appSettingsManager, AppSettingsManager.SETTING_CONTROLMODE);
        controleMode.SetParameter(wrapper.camParametersHandler.ControlMode);
        controleMode.SetMenuItemListner(this);

        redeyeflash = (troop.com.themesample.views.menu.MenuItem)view.findViewById(R.id.MenuItemRedEye);
        redeyeflash.SetStuff(i_activity,appSettingsManager, AppSettingsManager.SETTING_REDEYE_MODE);
        redeyeflash.SetParameter(wrapper.camParametersHandler.RedEye);
        redeyeflash.SetMenuItemListner(this);

        antiBanding = (MenuItem)view.findViewById(R.id.MenuItemAntiBanding);
        antiBanding.SetStuff(i_activity,appSettingsManager, AppSettingsManager.SETTING_ANTIBANDINGMODE);
        antiBanding.SetParameter(wrapper.camParametersHandler.AntiBandingMode);
        antiBanding.SetMenuItemListner(this);

        ipp = (MenuItem)view.findViewById(R.id.MenuItemIpp);
        ipp.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_IMAGEPOSTPROCESSINGMODE);
        ipp.SetParameter(wrapper.camParametersHandler.ImagePostProcessing);
        ipp.SetMenuItemListner(this);

        lensShade = (MenuItem)view.findViewById(R.id.MenuItemLensShade);
        lensShade.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_LENSSHADE_MODE);
        lensShade.SetParameter(wrapper.camParametersHandler.LensShade);
        lensShade.SetMenuItemListner(this);

        sceneDectecMode = (MenuItem)view.findViewById(R.id.MenuItemSceneDetection);
        sceneDectecMode.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_SCENEDETECT_MODE);
        sceneDectecMode.SetParameter(wrapper.camParametersHandler.SceneDetect);
        sceneDectecMode.SetMenuItemListner(this);

        waveletdenoiseMode = (MenuItem)view.findViewById(R.id.MenuItemWaveletDenoise);
        waveletdenoiseMode.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_DENOISE_MODE);
        waveletdenoiseMode.SetParameter(wrapper.camParametersHandler.Denoise);
        waveletdenoiseMode.SetMenuItemListner(this);

        digitalImageStabilization = (MenuItem)view.findViewById(R.id.MenuItemDigitalImageStab);
        digitalImageStabilization.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_DIS_MODE);
        digitalImageStabilization.SetParameter(wrapper.camParametersHandler.DigitalImageStabilization);
        digitalImageStabilization.SetMenuItemListner(this);

        memoryColorEnhancement = (MenuItem)view.findViewById(R.id.MenuItemMemoryColorEnhanc);
        memoryColorEnhancement.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_MCE_MODE);
        memoryColorEnhancement.SetParameter(wrapper.camParametersHandler.MemoryColorEnhancement);
        memoryColorEnhancement.SetMenuItemListner(this);

        ZeroShutterLag = (MenuItem)view.findViewById(R.id.MenuItemZSL);
        ZeroShutterLag.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_ZEROSHUTTERLAG_MODE);
        ZeroShutterLag.SetParameter(wrapper.camParametersHandler.ZSL);
        ZeroShutterLag.SetMenuItemListner(this);

        nonZSLmanualMode = (MenuItem)view.findViewById(R.id.MenuItemNonManualZSL);
        nonZSLmanualMode.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_NONZSLMANUALMODE);
        nonZSLmanualMode.SetParameter(wrapper.camParametersHandler.NonZslManualMode);
        nonZSLmanualMode.SetMenuItemListner(this);

        correlatedDoubleSampling = (MenuItem)view.findViewById(R.id.MenuItemCorrelatedDoubleSampling);
        correlatedDoubleSampling.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_CDS);
        correlatedDoubleSampling.SetParameter(wrapper.camParametersHandler.CDS_Mode);
        correlatedDoubleSampling.SetMenuItemListner(this);

        temporalDenoise = (MenuItem)view.findViewById(R.id.MenuItemTemporalDenoise);
        temporalDenoise.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_TNR);
        temporalDenoise.SetParameter(wrapper.camParametersHandler.TnrMode);
        temporalDenoise.SetMenuItemListner(this);

        edgeMode = (MenuItem)view.findViewById(R.id.MenuItemEdgeMode);
        edgeMode.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_EDGE);
        edgeMode.SetParameter(wrapper.camParametersHandler.EdgeMode);
        edgeMode.SetMenuItemListner(this);

        hotPixelMode = (MenuItem)view.findViewById(R.id.MenuItemHotPixelMode);
        hotPixelMode.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_HOTPIXEL);
        hotPixelMode.SetParameter(wrapper.camParametersHandler.HotPixelMode);
        hotPixelMode.SetMenuItemListner(this);

        opticalImageStabilization = (MenuItem)view.findViewById(R.id.MenuItemOIS);
        opticalImageStabilization.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_OIS);
        opticalImageStabilization.SetParameter(wrapper.camParametersHandler.oismode);
        opticalImageStabilization.SetMenuItemListner(this);
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
