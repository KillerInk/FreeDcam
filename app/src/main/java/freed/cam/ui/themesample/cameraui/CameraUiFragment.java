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

package freed.cam.ui.themesample.cameraui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.troop.freedcam.R;
import com.troop.freedcam.R.anim;
import com.troop.freedcam.R.dimen;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import freed.ActivityAbstract;
import freed.ActivityInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.cam.apis.basecamera.parameters.modes.ModeParameterInterface;
import freed.cam.apis.sonyremote.SonyCameraRemoteFragment;
import freed.cam.apis.sonyremote.parameters.JoyPad;
import freed.cam.apis.sonyremote.sonystuff.SimpleStreamSurfaceView;
import freed.cam.ui.I_swipe;
import freed.cam.ui.SwipeMenuListner;
import freed.cam.ui.guide.GuideHandler;
import freed.cam.ui.themesample.AbstractFragment;
import freed.cam.ui.themesample.SettingsChildAbstract;
import freed.cam.ui.themesample.cameraui.childs.UiSettingsChild;
import freed.cam.ui.themesample.cameraui.childs.UiSettingsChildCameraSwitch;
import freed.cam.ui.themesample.cameraui.childs.UiSettingsChildExit;
import freed.cam.ui.themesample.cameraui.childs.UiSettingsChildModuleSwitch;
import freed.cam.ui.themesample.cameraui.childs.UiSettingsFocusPeak;
import freed.cam.ui.themesample.handler.FocusImageHandler;
import freed.cam.ui.themesample.handler.SampleInfoOverlayHandler;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.utils.AppSettingsManager;
import freed.utils.Log;
import freed.viewer.screenslide.ScreenSlideFragment.I_ThumbClick;

/**
 * Created by troop on 14.06.2015.
 */
public class CameraUiFragment extends AbstractFragment implements SettingsChildAbstract.SettingsChildClick, SettingsChildAbstract.CloseChildClick, I_swipe, OnClickListener, ModuleHandlerAbstract.CaptureStateChanged
{
    final String TAG = CameraUiFragment.class.getSimpleName();
    private UiSettingsChildCameraSwitch cameraSwitch;
    private UiSettingsChild currentOpendChild;
    private HorizontalValuesFragment horizontalValuesFragment;
    private SwipeMenuListner touchHandler;
    private ShutterButton shutterButton;
    private ManualFragment manualModesFragment;
    private FrameLayout manualModes_holder;
    private boolean manualsettingsIsOpen;
    private FocusImageHandler focusImageHandler;
    private ActivityInterface fragment_activityInterface;
    private SampleInfoOverlayHandler infoOverlayHandler;
    private GuideHandler guideHandler;
    private final String KEY_MANUALMENUOPEN = "key_manualmenuopen";
    private SharedPreferences sharedPref;
    public I_ThumbClick thumbClick;

    private UiSettingsChild aelock;

    private UserMessageHandler messageHandler;

    private HorizontLineFragment horizontLineFragment;
    private View camerauiValuesFragmentHolder;

    private JoyPad joyPad;

    private LinearLayout left_ui_items_holder;
    private LinearLayout right_ui_items_top;

    public CameraUiFragment()
    {

    }

    private void setUiItem(LinearLayout layout, ModeParameterInterface parameter, AppSettingsManager.SettingMode settingMode, int backgroundImg)
    {
        UiSettingsChild child = new UiSettingsChild(getContext());
        child.SetParameter(parameter);
        child.SetStuff(settingMode);
        child.setBackgroundResource(backgroundImg);
        child.SetMenuItemClickListner(this,true);
        layout.addView(child);
    }

    private void setUiItem(LinearLayout layout, ModeParameterInterface parameter, String settingMode, int backgroundImg)
    {
        UiSettingsChild child = new UiSettingsChild(getContext());
        child.SetParameter(parameter);
        child.SetStuff(fragment_activityInterface,settingMode);
        child.setBackgroundResource(backgroundImg);
        child.SetMenuItemClickListner(this,true);
        layout.addView(child);
    }

    private void addexit()
    {
        UiSettingsChildExit exit = new UiSettingsChildExit(getContext());
        exit.SetStuff(fragment_activityInterface, "");
        exit.onParameterValueChanged("");
        exit.setBackgroundResource(R.drawable.quck_set_exit);
        right_ui_items_top.addView(exit);
    }

    @Override
    protected void setCameraUiWrapperToUi() {
        if (left_ui_items_holder != null) {
            left_ui_items_holder.removeAllViews();
            right_ui_items_top.removeAllViews();
            addexit();
        }
        if (cameraUiWrapper == null || cameraUiWrapper.getParameterHandler() == null || !isAdded())
        {
            if (focusImageHandler != null) {
                focusImageHandler.AEMeteringSupported(false);
                focusImageHandler.TouchToFocusSupported(false);
            }
            Log.d(TAG, "failed to set cameraUiWrapper");
            if (isAdded())
                hide_ManualSettings();
            return;
        }
        AbstractParameterHandler parameterHandler = cameraUiWrapper.getParameterHandler();
        AppSettingsManager appSettingsManager = cameraUiWrapper.getAppSettingsManager();

        //left cameraui items
        if (parameterHandler.WhiteBalanceMode != null)
        {
            setUiItem(left_ui_items_holder,parameterHandler.WhiteBalanceMode,appSettingsManager.whiteBalanceMode,R.drawable.quck_set_wb);
        }
        if (parameterHandler.IsoMode != null)
            setUiItem(left_ui_items_holder,parameterHandler.IsoMode, appSettingsManager.isoMode, R.drawable.quck_set_iso_png);
        if (parameterHandler.FlashMode != null)
            setUiItem(left_ui_items_holder,parameterHandler.FlashMode,appSettingsManager.flashMode,R.drawable.quck_set_flash);
        if (parameterHandler.FocusMode != null)
            setUiItem(left_ui_items_holder,parameterHandler.FocusMode, appSettingsManager.focusMode,R.drawable.quck_set_focus);
        if (parameterHandler.ExposureMode != null)
            setUiItem(left_ui_items_holder,parameterHandler.ExposureMode, appSettingsManager.exposureMode,R.drawable.quck_set_ae);
        if (parameterHandler.AE_PriorityMode != null)
            setUiItem(left_ui_items_holder,parameterHandler.AE_PriorityMode, appSettingsManager.aePriorityMode,R.drawable.ae_priority);
        if (parameterHandler.ContShootMode != null)
            setUiItem(left_ui_items_holder,parameterHandler.ContShootMode, "",R.drawable.quck_set_contin);
        if (parameterHandler.HDRMode != null)
            setUiItem(left_ui_items_holder,parameterHandler.HDRMode,appSettingsManager.hdrMode,R.drawable.quck_set_hdr);

        if (cameraUiWrapper.getParameterHandler().NightMode != null && cameraUiWrapper.getParameterHandler().NightMode.IsSupported()) {
            UiSettingsChild night = new UiSettingsChild(getContext());
            night.SetStuff(fragment_activityInterface, AppSettingsManager.NIGHTMODE);
            night.SetMenuItemClickListner(this, true);
            night.SetParameter(cameraUiWrapper.getParameterHandler().NightMode);
            night.setBackgroundResource(R.drawable.quck_set_night);
            left_ui_items_holder.addView(night);
        }

        if (cameraUiWrapper.getParameterHandler().PictureFormat != null)
        {
            setUiItem(left_ui_items_holder,parameterHandler.PictureFormat,appSettingsManager.pictureFormat,R.drawable.quck_set_format2);
        }


        //right camera top camerui itmes
        UiSettingsChildModuleSwitch moduleSwitch = new UiSettingsChildModuleSwitch(getContext());
        moduleSwitch.SetCameraUiWrapper(cameraUiWrapper);
        moduleSwitch.SetStuff(appSettingsManager.modules);
        moduleSwitch.SetMenuItemClickListner(this,false);
        moduleSwitch.setBackgroundResource(R.drawable.quck_set_mode);
        right_ui_items_top.addView(moduleSwitch);

        if (parameterHandler.Focuspeak != null && parameterHandler.Focuspeak.IsSupported() && cameraUiWrapper.getRenderScriptHandler().isSucessfullLoaded()) {
            UiSettingsFocusPeak focusPeak = new UiSettingsFocusPeak(getContext());
            focusPeak.SetParameter(cameraUiWrapper.getParameterHandler().Focuspeak);
            focusPeak.SetCameraUiWrapper(cameraUiWrapper);
            focusPeak.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_FOCUSPEAK);
            focusPeak.SetUiItemClickListner(this);
            focusPeak.setBackgroundResource(R.drawable.quck_set_zebra);
            right_ui_items_top.addView(focusPeak);
        }

        //stuff todo

        cameraUiWrapper.getModuleHandler().setWorkListner(this);


        cameraSwitch.SetCameraUiWrapper(cameraUiWrapper);
        focusImageHandler.SetCamerUIWrapper(cameraUiWrapper);

        messageHandler.SetCameraUiWrapper(cameraUiWrapper);
        shutterButton.SetCameraUIWrapper(cameraUiWrapper, messageHandler);

        if (manualModesFragment != null)
            manualModesFragment.SetCameraUIWrapper(cameraUiWrapper);

        guideHandler.setCameraUiWrapper(cameraUiWrapper);

        horizontLineFragment.setCameraUiWrapper(cameraUiWrapper);
        infoOverlayHandler.setCameraUIWrapper(cameraUiWrapper);
        shutterButton.setVisibility(View.VISIBLE);
        aelock.SetParameter(cameraUiWrapper.getParameterHandler().ExposureLock);
        //restore view state for the manuals
        if(manualsettingsIsOpen)
            showManualSettings();
        //remove the values fragment from ui when a new api gets loaded and it was open.
        if (horizontalValuesFragment != null && horizontalValuesFragment.isAdded())
            removeHorizontalFragment();

        if (cameraUiWrapper instanceof SonyCameraRemoteFragment)
        {
            joyPad.setVisibility(View.GONE);
            if (cameraUiWrapper.getParameterHandler().PreviewZoom != null)
                cameraUiWrapper.getParameterHandler().PreviewZoom.addEventListner(joyPad);
            joyPad.setNavigationClickListner((SimpleStreamSurfaceView)cameraUiWrapper.getSurfaceView());
        }
        else
            joyPad.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        Log.d(TAG, "####################ONCREATEDVIEW####################");

        fragment_activityInterface = (ActivityInterface)getActivity();
        touchHandler = new SwipeMenuListner(this);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        manualsettingsIsOpen = sharedPref.getBoolean(KEY_MANUALMENUOPEN, false);

        return inflater.inflate(layout.cameraui_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        manualModes_holder = (FrameLayout) view.findViewById(id.manualModesHolder);
        messageHandler = new UserMessageHandler(view);

        left_ui_items_holder = (LinearLayout)view.findViewById(id.left_ui_holder);

        right_ui_items_top = (LinearLayout)view.findViewById(id.right_ui_holder_top);
        addexit();

        cameraSwitch = (UiSettingsChildCameraSwitch) view.findViewById(id.camera_switch);
        cameraSwitch.SetStuff(fragment_activityInterface, AppSettingsManager.CURRENTCAMERA);

        infoOverlayHandler = new SampleInfoOverlayHandler(view, fragment_activityInterface.getAppSettings());
        infoOverlayHandler.setCameraUIWrapper(cameraUiWrapper);

        focusImageHandler = new FocusImageHandler(view, (ActivityAbstract) getActivity());

        shutterButton = (ShutterButton) view.findViewById(id.shutter_button);

        view.setOnTouchListener(onTouchListener);

        aelock = (UiSettingsChild)view.findViewById(id.ae_lock);
        aelock.SetUiItemClickListner(this);
        aelock.SetStuff(fragment_activityInterface, "");


        manualModesFragment = new ManualFragment();

        horizontLineFragment = new HorizontLineFragment();

        guideHandler =GuideHandler.GetInstance(fragment_activityInterface.getAppSettings());

        manualModes_holder.setVisibility(View.GONE);
        camerauiValuesFragmentHolder =  view.findViewById(id.cameraui_values_fragment_holder);
        joyPad = (JoyPad) view.findViewById(id.joypad);
        joyPad.setVisibility(View.GONE);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(id.guideHolder, guideHandler, "Guide");
        transaction.commit();

        transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(anim.bottom_to_top_enter, anim.empty);
        transaction.replace(id.manualModesHolder, manualModesFragment);
        transaction.commit();

        transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(anim.empty, anim.empty);
        transaction.replace(id.horHolder, horizontLineFragment);
        transaction.addToBackStack(null);
        transaction.commit();



        boolean showhelp = fragment_activityInterface.getAppSettings().getShowHelpOverlay();
        if (showhelp) {
            transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(anim.empty, anim.empty);
            transaction.replace(id.helpfragment_container, HelpFragment.getFragment(helpfragmentCloser, fragment_activityInterface.getAppSettings()));
            transaction.addToBackStack(null);
            transaction.commit();
        }
        if (cameraUiWrapper != null)
            setCameraUiWrapperToUi();
    }

    @Override
    public void onResume() {
        super.onResume();
        infoOverlayHandler.StartUpdating();
    }

    @Override
    public void onPause()
    {
        infoOverlayHandler.StopUpdating();
        sharedPref.edit().putBoolean(KEY_MANUALMENUOPEN, manualsettingsIsOpen).commit();
        boolean settingsOpen = false;
        String KEY_SETTINGSOPEN = "key_settingsopen";
        sharedPref.edit().putBoolean(KEY_SETTINGSOPEN, settingsOpen).commit();
        super.onPause();

    }

    private void hide_ManualSettings()
    {
        manualsettingsIsOpen = false;
        Log.d(TAG, "HideSettings");
        manualModes_holder.animate().translationY(manualModes_holder.getHeight()).setDuration(300);
        //manualModes_holder.setVisibility(View.GONE);
    }

    private void showManualSettings()
    {
        Log.d(TAG, "ShowSettings");
        manualsettingsIsOpen = true;
        manualModes_holder.animate().translationY(0).setDuration(300);
        manualModes_holder.setVisibility(View.VISIBLE);
    }


    @Override
    public void onSettingsChildClick(UiSettingsChild item, boolean fromLeftFragment)
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

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(dimen.cameraui_settingschild_width);
        params.rightMargin = getResources().getDimensionPixelSize(dimen.cameraui_shuttericon_size);
        //params.addRule(RelativeLayout.CENTER_VERTICAL);

        if (manualsettingsIsOpen)
            params.bottomMargin = getResources().getDimensionPixelSize(dimen.cameraui_manualbuttonholder_height);

        if (fromLeftFragment)
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        else  params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        camerauiValuesFragmentHolder.setLayoutParams(params);

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
        transaction.commit();
    }

    private void removeHorizontalFragment()
    {
        getChildFragmentManager().beginTransaction().remove(horizontalValuesFragment).setCustomAnimations(0, anim.right_to_left_exit).commit();
    }


    @Override
    public void onCloseClicked(String value) {
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

    @Override
    public void onCaptureStateChanged(ModuleHandlerAbstract.CaptureStates captureStates)
    {
    }


    interface i_HelpFragment
    {
        void Close(Fragment fragment);
    }

    private final i_HelpFragment helpfragmentCloser = new i_HelpFragment() {
        @Override
        public void Close(Fragment fragment) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.remove(fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    };
}
