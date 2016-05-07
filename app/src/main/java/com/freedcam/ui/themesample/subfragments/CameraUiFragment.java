package com.freedcam.ui.themesample.subfragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.apis.basecamera.camera.parameters.I_ParametersLoaded;
import com.freedcam.ui.AbstractFragment;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.ui.I_Activity;
import com.freedcam.ui.I_swipe;
import com.freedcam.ui.SwipeMenuListner;
import com.freedcam.ui.guide.GuideHandler;
import com.freedcam.ui.themesample.handler.FocusImageHandler;
import com.freedcam.ui.themesample.handler.SampleInfoOverlayHandler;
import com.freedcam.ui.themesample.handler.UserMessageHandler;
import com.freedcam.ui.themesample.views.ShutterButton;
import com.freedcam.ui.themesample.views.ThumbView;
import com.freedcam.ui.themesample.views.uichilds.UiSettingsChild;
import com.freedcam.ui.themesample.views.uichilds.UiSettingsChildCameraSwitch;
import com.freedcam.ui.themesample.views.uichilds.UiSettingsChildExit;
import com.freedcam.ui.themesample.views.uichilds.UiSettingsChildModuleSwitch;
import com.freedcam.ui.themesample.views.uichilds.UiSettingsFocusPeak;
import com.freedcam.ui.themesample.views.uichilds.UiSettingsMenu;
import com.freedcam.utils.Logger;
import com.freedviewer.screenslide.ScreenSlideFragment;
import com.troop.freedcam.R;

import java.io.File;

/**
 * Created by troop on 14.06.2015.
 */
public class CameraUiFragment extends AbstractFragment implements Interfaces.I_MenuItemClick, Interfaces.I_CloseNotice, I_swipe, View.OnClickListener
{
    final String TAG = CameraUiFragment.class.getSimpleName();
    private UiSettingsChild flash;
    private UiSettingsChild iso;
    private UiSettingsChild autoexposure;
    private UiSettingsChild whitebalance;
    private UiSettingsChild focus;
    private UiSettingsChild night;
    private UiSettingsChild format;
    private UiSettingsChildCameraSwitch cameraSwitch;
    private UiSettingsChildModuleSwitch modeSwitch;
    private UiSettingsMenu menu;
    private UiSettingsChild contShot;
    private UiSettingsChild currentOpendChild;
    private UiSettingsChild aepriority;
    private HorizontalValuesFragment horizontalValuesFragment;
    private SwipeMenuListner touchHandler;
    private ShutterButton shutterButton;
    private UiSettingsFocusPeak focuspeak;
    private UiSettingsChild hdr_switch;
    private ThumbView thumbView;
    private ManualFragmentRotatingSeekbar manualModesFragment;
    private FrameLayout manualModes_holder;
    private boolean manualsettingsIsOpen = false;
    private FocusImageHandler focusImageHandler;
    private View view;
    private I_Activity i_activity;
    private SampleInfoOverlayHandler infoOverlayHandler;
    private GuideHandler guideHandler;
    private final String KEY_MANUALMENUOPEN = "key_manualmenuopen";
    private SharedPreferences sharedPref;
    private ScreenSlideFragment.I_ThumbClick thumbClick;
    private File lastFile;
    private AppSettingsManager appSettingsManager;

    private HorizontLineFragment horizontLineFragment;

    public static CameraUiFragment GetInstance(I_Activity i_activity, ScreenSlideFragment.I_ThumbClick thumbClick, AppSettingsManager appSettingsManager,AbstractCameraUiWrapper cameraUiWrapper)
    {
        CameraUiFragment cameraUiFragment = new CameraUiFragment();
        cameraUiFragment.i_activity = i_activity;
        cameraUiFragment.thumbClick = thumbClick;
        cameraUiFragment.appSettingsManager = appSettingsManager;
        cameraUiFragment.cameraUiWrapper = cameraUiWrapper;
        return cameraUiFragment;
    }

    @Override
    protected void setCameraUiWrapperToUi() {
        if (cameraUiWrapper == null || cameraUiWrapper.camParametersHandler == null)
        {
            Logger.d(TAG, "failed to set cameraUiWrapper");
            return;
        }
        flash.SetParameter(cameraUiWrapper.camParametersHandler.FlashMode);
        iso.SetParameter(cameraUiWrapper.camParametersHandler.IsoMode);
        autoexposure.SetParameter(cameraUiWrapper.camParametersHandler.ExposureMode);
        whitebalance.SetParameter(cameraUiWrapper.camParametersHandler.WhiteBalanceMode);
        focus.SetParameter(cameraUiWrapper.camParametersHandler.FocusMode);
        night.SetParameter(cameraUiWrapper.camParametersHandler.NightMode);
        aepriority.SetParameter(cameraUiWrapper.camParametersHandler.AE_PriorityMode);
        thumbView.INIT(cameraUiWrapper);

        cameraSwitch.SetCameraUiWrapper(cameraUiWrapper);
        focusImageHandler.SetCamerUIWrapper(cameraUiWrapper);
        UserMessageHandler messageHandler = new UserMessageHandler(view);
        messageHandler.SetCameraUiWrapper(cameraUiWrapper);
        shutterButton.SetCameraUIWrapper(cameraUiWrapper, messageHandler);
        format.SetParameter(cameraUiWrapper.camParametersHandler.PictureFormat);
        contShot.SetParameter(cameraUiWrapper.camParametersHandler.ContShootMode);
        if (manualModesFragment != null)
            manualModesFragment.SetCameraUIWrapper(cameraUiWrapper);
        if (cameraUiWrapper.camParametersHandler.Focuspeak != null) {
            focuspeak.SetParameter(cameraUiWrapper.camParametersHandler.Focuspeak);
            cameraUiWrapper.camParametersHandler.AddParametersLoadedListner(focuspeak);
        }
        guideHandler.setCameraUiWrapper(cameraUiWrapper);
        focuspeak.SetCameraUiWrapper(cameraUiWrapper);
        modeSwitch.SetCameraUiWrapper(cameraUiWrapper);
        hdr_switch.SetParameter(cameraUiWrapper.camParametersHandler.HDRMode);
        horizontLineFragment.setCameraUiWrapper(cameraUiWrapper);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d(TAG, "####################ONCREATEDVIEW####################");

        touchHandler = new SwipeMenuListner(this);

        return inflater.inflate(R.layout.cameraui, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        Logger.d(TAG, "####################VIEW CREATED####################");
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        manualsettingsIsOpen = sharedPref.getBoolean(KEY_MANUALMENUOPEN, false);
        LinearLayout left_cameraUI_holder = (LinearLayout) view.findViewById(R.id.left_ui_holder);
        RelativeLayout right_camerUI_holder = (RelativeLayout) view.findViewById(R.id.right_ui_holder);
        this.manualModes_holder = (FrameLayout)view.findViewById(R.id.manualModesHolder);
        LinearLayout LC = (LinearLayout) view.findViewById(R.id.LCover);

        this.flash = (UiSettingsChild)view.findViewById(R.id.Flash);
        flash.SetStuff(i_activity, AppSettingsManager.SETTING_FLASHMODE,appSettingsManager);
        flash.SetMenuItemListner(this, true);

        this.iso = (UiSettingsChild)view.findViewById(R.id.ui_settings_iso);
        iso.SetStuff(i_activity, AppSettingsManager.SETTING_ISOMODE,appSettingsManager);
        iso.SetMenuItemListner(this,true);

        this.autoexposure =(UiSettingsChild)view.findViewById(R.id.Ae);
        autoexposure.SetStuff(i_activity,AppSettingsManager.SETTING_EXPOSUREMODE,appSettingsManager);
        autoexposure.SetMenuItemListner(this,true);

        this.aepriority = (UiSettingsChild)view.findViewById(R.id.AePriority);
        aepriority.SetStuff(i_activity,AppSettingsManager.SETTTING_AE_PRIORITY,appSettingsManager);
        aepriority.SetMenuItemListner(this,true);

        this.whitebalance = (UiSettingsChild)view.findViewById(R.id.wb);
        whitebalance.SetStuff(i_activity, AppSettingsManager.SETTING_WHITEBALANCEMODE,appSettingsManager);
        whitebalance.SetMenuItemListner(this,true);

        this.focus = (UiSettingsChild)view.findViewById(R.id.focus_uisetting);
        focus.SetStuff(i_activity, AppSettingsManager.SETTING_FOCUSMODE,appSettingsManager);
        focus.SetMenuItemListner(this,true);

        this.contShot = (UiSettingsChild)view.findViewById(R.id.continousShot);
        contShot.SetStuff(i_activity, null,appSettingsManager);
        contShot.SetMenuItemListner(this,true);

        this.night = (UiSettingsChild)view.findViewById(R.id.night);
        night.SetStuff(i_activity, AppSettingsManager.SETTING_NIGHTEMODE,appSettingsManager);
        night.SetMenuItemListner(this,true);

        this.format = (UiSettingsChild)view.findViewById(R.id.format);
        format.SetStuff(i_activity, AppSettingsManager.SETTING_PICTUREFORMAT,appSettingsManager);
        format.SetMenuItemListner(this,true);

        this.thumbView = (ThumbView)view.findViewById(R.id.thumbview);
        this.thumbView.SetOnThumbClickListener(thumbClick);

        this.modeSwitch = (UiSettingsChildModuleSwitch)view.findViewById(R.id.mode_switch);
        modeSwitch.SetStuff(i_activity, AppSettingsManager.SETTING_CURRENTMODULE,appSettingsManager);
        modeSwitch.SetMenuItemListner(this,false);

        UiSettingsChildExit exit = (UiSettingsChildExit) view.findViewById(R.id.exit);
        exit.SetStuff(i_activity, "",appSettingsManager);

        cameraSwitch = (UiSettingsChildCameraSwitch)view.findViewById(R.id.camera_switch);
        cameraSwitch.SetStuff(i_activity, AppSettingsManager.SETTING_CURRENTCAMERA,appSettingsManager);

        infoOverlayHandler = new SampleInfoOverlayHandler(view,appSettingsManager);
        infoOverlayHandler.setCameraUIWrapper(cameraUiWrapper);

        focusImageHandler = new FocusImageHandler(view, this);

        shutterButton = (ShutterButton)view.findViewById(R.id.shutter_button);
        view.setOnTouchListener(onTouchListener);

        focuspeak = (UiSettingsFocusPeak)view.findViewById(R.id.ui_focuspeak);

        focuspeak.SetStuff(i_activity, AppSettingsManager.SETTING_FOCUSPEAK,appSettingsManager);
        focuspeak.SetMenuItemListner(this);

        //adding hdr switch log test v1.0 1-29-2016 6:13 - Defcomk
        this.hdr_switch = (UiSettingsChild)view.findViewById(R.id.hdr_toggle);
        hdr_switch.SetStuff(i_activity, AppSettingsManager.SETTING_HDRMODE,appSettingsManager);
        hdr_switch.SetMenuItemListner(this,true);

        manualModesFragment = ManualFragmentRotatingSeekbar.GetInstance(appSettingsManager,i_activity);

        horizontLineFragment = HorizontLineFragment.GetInstance(i_activity,appSettingsManager);

        guideHandler =GuideHandler.GetInstance(appSettingsManager);
        android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.guideHolder, guideHandler, "Guide");
        transaction.commitAllowingStateLoss();

        transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.bottom_to_top_enter, R.anim.empty);
        transaction.replace(R.id.manualModesHolder, manualModesFragment);
        transaction.commitAllowingStateLoss();

        transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.empty, R.anim.empty);
        transaction.replace(R.id.horHolder, horizontLineFragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();

        boolean showhelp = appSettingsManager.getShowHelpOverlay();
        if (showhelp) {
            transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.empty, R.anim.empty);
            transaction.replace(R.id.helpfragment_container, HelpFragment.getFragment(helpfragmentCloser,appSettingsManager));
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }

        if(!manualsettingsIsOpen)
            manualModes_holder.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        setCameraUiWrapperToUi();
        infoOverlayHandler.StartUpdating();
    }

    @Override
    public void onPause()
    {
        infoOverlayHandler.StopUpdating();
        sharedPref.edit().putBoolean(KEY_MANUALMENUOPEN,manualsettingsIsOpen).commit();
        boolean settingsOpen = false;
        String KEY_SETTINGSOPEN = "key_settingsopen";
        sharedPref.edit().putBoolean(KEY_SETTINGSOPEN, settingsOpen).commit();
        super.onPause();

    }

    private void hide_ManualSettings()
    {
        manualsettingsIsOpen = false;
        Logger.d(TAG, "HideSettings");
        manualModes_holder.animate().translationY(manualModes_holder.getHeight()).setDuration(300);
        //manualModes_holder.setVisibility(View.GONE);
    }

    private void showManualSettings()
    {
        Logger.d(TAG, "ShowSettings");
        manualsettingsIsOpen = true;
        manualModes_holder.animate().translationY(0).setDuration(300);
        manualModes_holder.setVisibility(View.VISIBLE);
    }


    @Override
    public void onMenuItemClick(UiSettingsChild item, boolean fromLeftFragment)
    {

        if (currentOpendChild == item)
        {
            removeHorizontalFragment();
            currentOpendChild = null;
            return;
        }
        if (currentOpendChild != null)
        {
            removeHorizontalFragment();
            currentOpendChild = null;
        }
        if (horizontalValuesFragment != null)
            horizontalValuesFragment.Clear();
        View l = (View)view.findViewById(R.id.cameraui_values_fragment_holder);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.manualitemwidth);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.shuttericon_size);
        //params.addRule(RelativeLayout.CENTER_VERTICAL);

        if (manualsettingsIsOpen)
            params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.manualSettingsHeight);

        if (fromLeftFragment)
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        else  params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        l.setLayoutParams(params);

        currentOpendChild = item;
        horizontalValuesFragment = new HorizontalValuesFragment();
        String[] tmo = item.GetValues();
        if (tmo != null && tmo.length >0)
            horizontalValuesFragment.SetStringValues(tmo, this);
        else
           horizontalValuesFragment.ListenToParameter(item.GetParameter());
        infalteIntoHolder(R.id.cameraui_values_fragment_holder, horizontalValuesFragment);

    }

    private void infalteIntoHolder(int id, HorizontalValuesFragment fragment)
    {

        android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.left_to_right_enter, 0);
        transaction.replace(id, fragment);
        transaction.commitAllowingStateLoss();
    }

    private void removeHorizontalFragment()
    {
        getChildFragmentManager().beginTransaction().remove(horizontalValuesFragment).setCustomAnimations(0, R.anim.right_to_left_exit).commit();
    }


    @Override
    public void onClose(String value) {
        currentOpendChild.SetValue(value);
        removeHorizontalFragment();
        currentOpendChild = null;
    }



    @Override
    public void  doRightToLeftSwipe() {

    }

    @Override
    public void doLeftToRightSwipe(){
    }

    @Override
    public void doTopToBottomSwipe(){
        hide_ManualSettings();
    }

    @Override
    public void doBottomToTopSwipe(){
        showManualSettings();

    }

    @Override
    public void onClick(int x, int y) {
        if (focusImageHandler != null)
            focusImageHandler.OnClick(x,y);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        if (focusImageHandler != null)
            focusImageHandler.onTouchEvent(event);
    }

    View.OnTouchListener onTouchListener = new View.OnTouchListener()
    {
        public boolean onTouch(View v, MotionEvent event)
        {
            return touchHandler.onTouchEvent(event);
        }

    };

    //On Settings Menu Click
    @Override
    public void onClick(View v) {

    }

    interface i_HelpFragment
    {
        void Close(android.support.v4.app.Fragment fragment);
    }

    private i_HelpFragment helpfragmentCloser = new i_HelpFragment() {
        @Override
        public void Close(android.support.v4.app.Fragment fragment) {
            android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.remove(fragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
    };
}
