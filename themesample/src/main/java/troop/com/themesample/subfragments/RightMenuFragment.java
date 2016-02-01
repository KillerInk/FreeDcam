package troop.com.themesample.subfragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AbstractFragment;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_swipe;
import com.troop.freedcam.ui.SwipeMenuListner;

import troop.com.themesample.R;
import troop.com.themesample.views.menu.MenuItem;
import troop.com.themesample.views.uichilds.UiSettingsChild;

/**
 * Created by troop on 15.06.2015.
 */
public class RightMenuFragment extends AbstractFragment implements Interfaces.I_MenuItemClick, I_swipe
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
    MenuItem chromaFlash;
    MenuItem sceneDetectMode;
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

    MenuItem LensFilter;

    public SwipeMenuListner touchHandler;
    ScrollView scrollView;
    FrameLayout settingsMenu;
    final String KEY_SETTINGSOPEN = "key_settingsopen";
    SharedPreferences sharedPref;
    boolean settingsOpen;
    LinearLayout leftholder;

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
        scene.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_SCENEMODE, touchHandler);

        color = (MenuItem)view.findViewById(R.id.MenuItemColor);
        color.SetStuff(i_activity,appSettingsManager, AppSettingsManager.SETTING_COLORMODE,touchHandler);

        cctMode = (MenuItem)view.findViewById(R.id.MenuItemCCTMode);
        cctMode.SetStuff(i_activity,appSettingsManager, AppSettingsManager.SETTING_COLORCORRECTION,touchHandler);

        objectTrackingMode = (MenuItem)view.findViewById(R.id.MenuItemObjectTracking);
        objectTrackingMode.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_OBJECTTRACKING,touchHandler);

        toneMapMode = (MenuItem)view.findViewById(R.id.MenuItemTonemap);
        toneMapMode.SetStuff(i_activity,appSettingsManager, AppSettingsManager.SETTING_TONEMAP,touchHandler);

        postViewSize = (MenuItem)view.findViewById(R.id.MenuItemPostViewSize);
        postViewSize.SetStuff(i_activity,appSettingsManager, "",touchHandler);

        controleMode = (MenuItem)view.findViewById(R.id.MenuItemControlMode);
        controleMode.SetStuff(i_activity,appSettingsManager, AppSettingsManager.SETTING_CONTROLMODE,touchHandler);

        redeyeflash = (troop.com.themesample.views.menu.MenuItem)view.findViewById(R.id.MenuItemRedEye);
        redeyeflash.SetStuff(i_activity,appSettingsManager, AppSettingsManager.SETTING_REDEYE_MODE,touchHandler);

        antiBanding = (MenuItem)view.findViewById(R.id.MenuItemAntiBanding);
        antiBanding.SetStuff(i_activity,appSettingsManager, AppSettingsManager.SETTING_ANTIBANDINGMODE,touchHandler);

        ipp = (MenuItem)view.findViewById(R.id.MenuItemIpp);
        ipp.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_IMAGEPOSTPROCESSINGMODE,touchHandler);

        lensShade = (MenuItem)view.findViewById(R.id.MenuItemLensShade);
        lensShade.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_LENSSHADE_MODE,touchHandler);

        chromaFlash = (MenuItem)view.findViewById(R.id.MenuItemChromaFlash);
        chromaFlash.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_CHROMAFLASH_MODE,touchHandler);

        sceneDetectMode = (MenuItem)view.findViewById(R.id.MenuItemSceneDetection);
        sceneDetectMode.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_SCENEDETECT_MODE,touchHandler);

        waveletdenoiseMode = (MenuItem)view.findViewById(R.id.MenuItemWaveletDenoise);
        waveletdenoiseMode.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_DENOISE_MODE,touchHandler);

        digitalImageStabilization = (MenuItem)view.findViewById(R.id.MenuItemDigitalImageStab);
        digitalImageStabilization.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_DIS_MODE,touchHandler);

        memoryColorEnhancement = (MenuItem)view.findViewById(R.id.MenuItemMemoryColorEnhanc);
        memoryColorEnhancement.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_MCE_MODE,touchHandler);

        ZeroShutterLag = (MenuItem)view.findViewById(R.id.MenuItemZSL);
        ZeroShutterLag.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_ZEROSHUTTERLAG_MODE,touchHandler);

        nonZSLmanualMode = (MenuItem)view.findViewById(R.id.MenuItemNonManualZSL);
        nonZSLmanualMode.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_NONZSLMANUALMODE,touchHandler);

        correlatedDoubleSampling = (MenuItem)view.findViewById(R.id.MenuItemCorrelatedDoubleSampling);
        correlatedDoubleSampling.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_CDS,touchHandler);

        temporalDenoise = (MenuItem)view.findViewById(R.id.MenuItemTemporalDenoise);
        temporalDenoise.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_TNR,touchHandler);

        edgeMode = (MenuItem)view.findViewById(R.id.MenuItemEdgeMode);
        edgeMode.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_EDGE,touchHandler);

        hotPixelMode = (MenuItem)view.findViewById(R.id.MenuItemHotPixelMode);
        hotPixelMode.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_HOTPIXEL,touchHandler);

        opticalImageStabilization = (MenuItem)view.findViewById(R.id.MenuItemOIS);
        opticalImageStabilization.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_OIS,touchHandler);

        LensFilter = (MenuItem)view.findViewById(R.id.LensFilter);
        LensFilter.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_Filter,touchHandler);

        scrollView = (ScrollView) view.findViewById(R.id.scrollView2);
        settingsMenu =  (FrameLayout)getActivity().findViewById(R.id.settingsMenuHolder);

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        settingsOpen = sharedPref.getBoolean(KEY_SETTINGSOPEN, false);
        leftholder = (LinearLayout) getActivity().findViewById(R.id.guideHolder);


        setWrapper();
    }

    private void setWrapper()
    {
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

        chromaFlash.SetParameter(wrapper.camParametersHandler.ChromaFlash);
        chromaFlash.SetMenuItemListner(this);

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

    @Override
    public void doLeftToRightSwipe() {

    }

    @Override
    public void doRightToLeftSwipe() {
        settingsOpen = false;
        sharedPref.edit().putBoolean(KEY_SETTINGSOPEN, settingsOpen).commit();
        float width = leftholder.getWidth();
        settingsMenu.animate().translationX(-width).setDuration(300);
    }

    @Override
    public void doTopToBottomSwipe() {

    }

    @Override
    public void doBottomToTopSwipe() {

    }

    @Override
    public void onClick(int x, int y) {

    }

}
