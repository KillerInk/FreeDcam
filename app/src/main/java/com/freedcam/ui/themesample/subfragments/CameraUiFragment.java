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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.ui.AbstractFragment;
import com.freedcam.ui.I_Activity;
import com.freedcam.ui.I_swipe;
import com.freedcam.ui.SwipeMenuListner;
import com.freedcam.ui.guide.GuideHandler;
import com.freedcam.ui.themesample.handler.FocusImageHandler;
import com.freedcam.ui.themesample.handler.SampleInfoOverlayHandler;
import com.freedcam.ui.themesample.handler.UserMessageHandler;
import com.freedcam.ui.themesample.subfragments.Interfaces.I_CloseNotice;
import com.freedcam.ui.themesample.subfragments.Interfaces.I_MenuItemClick;
import com.freedcam.ui.themesample.views.ShutterButton;
import com.freedcam.ui.themesample.views.ThumbView;
import com.freedcam.ui.themesample.views.uichilds.UiSettingsChild;
import com.freedcam.ui.themesample.views.uichilds.UiSettingsChildCameraSwitch;
import com.freedcam.ui.themesample.views.uichilds.UiSettingsChildExit;
import com.freedcam.ui.themesample.views.uichilds.UiSettingsChildModuleSwitch;
import com.freedcam.ui.themesample.views.uichilds.UiSettingsFocusPeak;
import com.freedcam.ui.themesample.views.uichilds.UiSettingsMenu;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;
import com.freedviewer.helper.BitmapHelper;
import com.freedviewer.screenslide.ScreenSlideFragment.I_ThumbClick;
import com.troop.freedcam.R.anim;
import com.troop.freedcam.R.dimen;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import java.io.File;

/**
 * Created by troop on 14.06.2015.
 */
public class CameraUiFragment extends AbstractFragment implements I_MenuItemClick, I_CloseNotice, I_swipe, OnClickListener
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
    private I_ThumbClick thumbClick;
    private File lastFile;
    private AppSettingsManager appSettingsManager;

    private HorizontLineFragment horizontLineFragment;
    private int LeftWidth = 0;
    private BitmapHelper bitmapHelper;

    public static CameraUiFragment GetInstance(I_Activity i_activity, I_ThumbClick thumbClick, AppSettingsManager appSettingsManager, I_CameraUiWrapper cameraUiWrapper, BitmapHelper bitmapHelper)
    {
        CameraUiFragment cameraUiFragment = new CameraUiFragment();
        cameraUiFragment.i_activity = i_activity;
        cameraUiFragment.thumbClick = thumbClick;
        cameraUiFragment.appSettingsManager = appSettingsManager;
        cameraUiFragment.cameraUiWrapper = cameraUiWrapper;
        cameraUiFragment.bitmapHelper = bitmapHelper;
        return cameraUiFragment;
    }

    public CameraUiFragment()
    {

    }

    @Override
    protected void setCameraUiWrapperToUi() {
        if (cameraUiWrapper == null || cameraUiWrapper.GetParameterHandler() == null)
        {
            Logger.d(TAG, "failed to set cameraUiWrapper");
            return;
        }
        flash.SetParameter(cameraUiWrapper.GetParameterHandler().FlashMode);
        iso.SetParameter(cameraUiWrapper.GetParameterHandler().IsoMode);
        autoexposure.SetParameter(cameraUiWrapper.GetParameterHandler().ExposureMode);
        whitebalance.SetParameter(cameraUiWrapper.GetParameterHandler().WhiteBalanceMode);
        focus.SetParameter(cameraUiWrapper.GetParameterHandler().FocusMode);
        night.SetParameter(cameraUiWrapper.GetParameterHandler().NightMode);
        aepriority.SetParameter(cameraUiWrapper.GetParameterHandler().AE_PriorityMode);
        thumbView.INIT(cameraUiWrapper,bitmapHelper);

        cameraSwitch.SetCameraUiWrapper(cameraUiWrapper);
        focusImageHandler.SetCamerUIWrapper(cameraUiWrapper);
        UserMessageHandler messageHandler = new UserMessageHandler(view);
        messageHandler.SetCameraUiWrapper(cameraUiWrapper);
        shutterButton.SetCameraUIWrapper(cameraUiWrapper, messageHandler);
        format.SetParameter(cameraUiWrapper.GetParameterHandler().PictureFormat);
        contShot.SetParameter(cameraUiWrapper.GetParameterHandler().ContShootMode);
        if (manualModesFragment != null)
            manualModesFragment.SetCameraUIWrapper(cameraUiWrapper);
        if (cameraUiWrapper.GetParameterHandler().Focuspeak != null) {
            focuspeak.SetParameter(cameraUiWrapper.GetParameterHandler().Focuspeak);
            cameraUiWrapper.GetParameterHandler().AddParametersLoadedListner(focuspeak);
        }
        guideHandler.setCameraUiWrapper(cameraUiWrapper);
        focuspeak.SetCameraUiWrapper(cameraUiWrapper);
        modeSwitch.SetCameraUiWrapper(cameraUiWrapper);
        hdr_switch.SetParameter(cameraUiWrapper.GetParameterHandler().HDRMode);
        horizontLineFragment.setCameraUiWrapper(cameraUiWrapper);
        infoOverlayHandler.setCameraUIWrapper(cameraUiWrapper);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        Logger.d(TAG, "####################ONCREATEDVIEW####################");

        touchHandler = new SwipeMenuListner(this);
        view = inflater.inflate(layout.cameraui, container, false);

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        manualsettingsIsOpen = sharedPref.getBoolean(KEY_MANUALMENUOPEN, false);
        LinearLayout left_cameraUI_holder = (LinearLayout) view.findViewById(id.left_ui_holder);
        LeftWidth = left_cameraUI_holder.getWidth();
        RelativeLayout right_camerUI_holder = (RelativeLayout) view.findViewById(id.right_ui_holder);
        manualModes_holder = (FrameLayout)view.findViewById(id.manualModesHolder);
        LinearLayout LC = (LinearLayout) view.findViewById(id.LCover);

        flash = (UiSettingsChild)view.findViewById(id.Flash);
        flash.SetStuff(i_activity, AppSettingsManager.SETTING_FLASHMODE,appSettingsManager);
        flash.SetMenuItemListner(this, true);

        iso = (UiSettingsChild)view.findViewById(id.ui_settings_iso);
        iso.SetStuff(i_activity, AppSettingsManager.SETTING_ISOMODE,appSettingsManager);
        iso.SetMenuItemListner(this,true);

        autoexposure =(UiSettingsChild)view.findViewById(id.Ae);
        autoexposure.SetStuff(i_activity,AppSettingsManager.SETTING_EXPOSUREMODE,appSettingsManager);
        autoexposure.SetMenuItemListner(this,true);

        aepriority = (UiSettingsChild)view.findViewById(id.AePriority);
        aepriority.SetStuff(i_activity,AppSettingsManager.SETTTING_AE_PRIORITY,appSettingsManager);
        aepriority.SetMenuItemListner(this,true);

        whitebalance = (UiSettingsChild)view.findViewById(id.wb);
        whitebalance.SetStuff(i_activity, AppSettingsManager.SETTING_WHITEBALANCEMODE,appSettingsManager);
        whitebalance.SetMenuItemListner(this,true);

        focus = (UiSettingsChild)view.findViewById(id.focus_uisetting);
        focus.SetStuff(i_activity, AppSettingsManager.SETTING_FOCUSMODE,appSettingsManager);
        focus.SetMenuItemListner(this,true);

        contShot = (UiSettingsChild)view.findViewById(id.continousShot);
        contShot.SetStuff(i_activity, null,appSettingsManager);
        contShot.SetMenuItemListner(this,true);

        night = (UiSettingsChild)view.findViewById(id.night);
        night.SetStuff(i_activity, AppSettingsManager.SETTING_NIGHTEMODE,appSettingsManager);
        night.SetMenuItemListner(this,true);

        format = (UiSettingsChild)view.findViewById(id.format);
        format.SetStuff(i_activity, AppSettingsManager.SETTING_PICTUREFORMAT,appSettingsManager);
        format.SetMenuItemListner(this,true);

        thumbView = (ThumbView)view.findViewById(id.thumbview);
        thumbView.SetOnThumbClickListener(thumbClick);

        modeSwitch = (UiSettingsChildModuleSwitch)view.findViewById(id.mode_switch);
        modeSwitch.SetStuff(i_activity, AppSettingsManager.SETTING_CURRENTMODULE,appSettingsManager);
        modeSwitch.SetMenuItemListner(this,false);

        UiSettingsChildExit exit = (UiSettingsChildExit) view.findViewById(id.exit);
        exit.SetStuff(i_activity, "",appSettingsManager);

        cameraSwitch = (UiSettingsChildCameraSwitch)view.findViewById(id.camera_switch);
        cameraSwitch.SetStuff(i_activity, AppSettingsManager.SETTING_CURRENTCAMERA,appSettingsManager);

        infoOverlayHandler = new SampleInfoOverlayHandler(view,appSettingsManager);
        infoOverlayHandler.setCameraUIWrapper(cameraUiWrapper);

        focusImageHandler = new FocusImageHandler(view, this);

        shutterButton = (ShutterButton)view.findViewById(id.shutter_button);
        view.setOnTouchListener(onTouchListener);

        focuspeak = (UiSettingsFocusPeak)view.findViewById(id.ui_focuspeak);

        focuspeak.SetStuff(i_activity, AppSettingsManager.SETTING_FOCUSPEAK,appSettingsManager);
        focuspeak.SetMenuItemListner(this);

        //adding hdr switch log test v1.0 1-29-2016 6:13 - Defcomk
        hdr_switch = (UiSettingsChild)view.findViewById(id.hdr_toggle);
        hdr_switch.SetStuff(i_activity, AppSettingsManager.SETTING_HDRMODE,appSettingsManager);
        hdr_switch.SetMenuItemListner(this,true);

        manualModesFragment = ManualFragmentRotatingSeekbar.GetInstance(appSettingsManager,i_activity);

        horizontLineFragment = HorizontLineFragment.GetInstance(i_activity,appSettingsManager);

        guideHandler =GuideHandler.GetInstance(appSettingsManager);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(id.guideHolder, guideHandler, "Guide");
        transaction.commitAllowingStateLoss();

        transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(anim.bottom_to_top_enter, anim.empty);
        transaction.replace(id.manualModesHolder, manualModesFragment);
        transaction.commitAllowingStateLoss();

        transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(anim.empty, anim.empty);
        transaction.replace(id.horHolder, horizontLineFragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();

        boolean showhelp = appSettingsManager.getShowHelpOverlay();
        if (showhelp) {
            transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(anim.empty, anim.empty);
            transaction.replace(id.helpfragment_container, HelpFragment.getFragment(helpfragmentCloser,appSettingsManager));
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }

        if(!manualsettingsIsOpen)
            manualModes_holder.setVisibility(View.GONE);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        //setCameraUiWrapperToUi();
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
        View l = view.findViewById(id.cameraui_values_fragment_holder);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(dimen.manualitemwidth);
        params.rightMargin = getResources().getDimensionPixelSize(dimen.shuttericon_size);
        //params.addRule(RelativeLayout.CENTER_VERTICAL);

        if (manualsettingsIsOpen)
            params.bottomMargin = getResources().getDimensionPixelSize(dimen.manualSettingsHeight);

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
        infalteIntoHolder(id.cameraui_values_fragment_holder, horizontalValuesFragment);

    }

    private void infalteIntoHolder(int id, HorizontalValuesFragment fragment)
    {

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(anim.left_to_right_enter, 0);
        transaction.replace(id, fragment);
        transaction.commitAllowingStateLoss();
    }

    private void removeHorizontalFragment()
    {
        getChildFragmentManager().beginTransaction().remove(horizontalValuesFragment).setCustomAnimations(0, anim.right_to_left_exit).commit();
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

    OnTouchListener onTouchListener = new OnTouchListener()
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
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.remove(fragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
    };
}
