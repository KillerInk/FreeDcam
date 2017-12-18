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
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
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
import freed.cam.ui.themesample.cameraui.childs.UiSettingsChildSelfTimer;
import freed.cam.ui.themesample.cameraui.childs.UiSettingsFocusPeak;
import freed.cam.ui.themesample.handler.FocusImageHandler;
import freed.cam.ui.themesample.handler.SampleInfoOverlayHandler;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.settings.Settings;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 14.06.2015.
 */
public class CameraUiFragment extends AbstractFragment implements SettingsChildAbstract.SettingsChildClick, SettingsChildAbstract.CloseChildClick, I_swipe, OnClickListener, ModuleHandlerAbstract.CaptureStateChanged
{
    final String TAG = CameraUiFragment.class.getSimpleName();
    //button to switch between front and back cam
    private UiSettingsChildCameraSwitch cameraSwitch;
    //hold the button wich opened the horizontalValuesFragment
    private UiSettingsChild currentOpendChild;
    //Shows the values when a uibutton got clicked
    private HorizontalValuesFragment horizontalValuesFragment;
    //there the horizontalValuesFragment gets inflated
    private View camerauiValuesFragmentHolder;
    //handels the touch events that happen on screen
    private SwipeMenuListner touchHandler;
    //well that get clicked when we want to start an action
    private ShutterButton shutterButton;
    //holds the manualButtons
    private ManualFragment manualModesFragment;
    //used to inflate the manualsModesFragment
    private FrameLayout manualModes_holder;
    //open state from manualModesFragment
    private boolean manualsettingsIsOpen;
    //Handel the animation/visibility for focus trigger, meteringarea, manualwbarea
    private FocusImageHandler focusImageHandler;
    //the activity interface that holds this fragment
    private ActivityInterface fragment_activityInterface;
    //show the time,sdspace, pic/video size
    private SampleInfoOverlayHandler infoOverlayHandler;
    //holds guide
    private GuideHandler guideHandler;
    private final String KEY_MANUALMENUOPEN = "key_manualmenuopen";
    private SharedPreferences sharedPref;

    private UiSettingsChild aelock;

    private UserMessageHandler messageHandler;

    private HorizontLineFragment horizontLineFragment;

    private UiSettingsChildSelfTimer settingsChildSelfTimer;


    //get shown in sony api,when the preview gets zoomed to navigate through the img
    private JoyPad joyPad;

    private LinearLayout left_ui_items_holder;
    private LinearLayout right_ui_items_top;

    public CameraUiFragment()
    {

    }

    /**
     * Creates and Add an Child to the CameraUi
     * @param layout the layout where the child get added
     * @param parameter to use
     * @param settingMode to use
     * @param backgroundImg id that get used
     */
    private void setUiItem(LinearLayout layout, ParameterInterface parameter, SettingsManager.SettingMode settingMode, int backgroundImg)
    {
        UiSettingsChild child = new UiSettingsChild(getContext());
        child.SetParameter(parameter);
        child.SetStuff(settingMode);
        child.setBackgroundResource(backgroundImg);
        child.SetMenuItemClickListner(this,true);
        layout.addView(child);
    }

    private void setUiItem(LinearLayout layout, ParameterInterface parameter, String settingMode, int backgroundImg)
    {
        UiSettingsChild child = new UiSettingsChild(getContext());
        child.SetParameter(parameter);
        child.SetStuff(fragment_activityInterface,settingMode);
        child.setBackgroundResource(backgroundImg);
        child.SetMenuItemClickListner(this,true);
        layout.addView(child);
    }

    /**
     * add an exitbutton to the right top itemholder
     */
    private void addexit()
    {
        UiSettingsChildExit exit = new UiSettingsChildExit(getContext());
        exit.SetStuff(fragment_activityInterface, "");
        exit.onStringValueChanged("");
        exit.setBackgroundResource(R.drawable.quck_set_exit);
        right_ui_items_top.addView(exit);
    }

    @Override
    public void setCameraToUi(CameraWrapperInterface wrapper) {
        super.setCameraToUi(wrapper);
        if (left_ui_items_holder != null) {
            left_ui_items_holder.removeAllViews();
            right_ui_items_top.removeAllViews();
            addexit();
        }
        if (cameraUiWrapper == null) {
            if (focusImageHandler != null) {
                focusImageHandler.AEMeteringSupported(false);
                focusImageHandler.TouchToFocusSupported(false);
                joyPad.setVisibility(View.GONE);
                cameraSwitch.setVisibility(View.GONE);
                aelock.setVisibility(View.GONE);
                shutterButton.setVisibility(View.GONE);
                settingsChildSelfTimer.setVisibility(View.GONE);
                if (isAdded())
                    hide_ManualSettings();
            }
        }
        else {

            AbstractParameterHandler parameterHandler = cameraUiWrapper.getParameterHandler();
            if (parameterHandler == null)
                return;

            //left cameraui items
            if (parameterHandler.get(Settings.WhiteBalanceMode) != null) {
                setUiItem(left_ui_items_holder, parameterHandler.get(Settings.WhiteBalanceMode), SettingsManager.get(Settings.WhiteBalanceMode), R.drawable.quck_set_wb);
            }
            if (parameterHandler.get(Settings.IsoMode) != null)
                setUiItem(left_ui_items_holder, parameterHandler.get(Settings.IsoMode), SettingsManager.get(Settings.IsoMode), R.drawable.quck_set_iso_png);
            if (parameterHandler.get(Settings.FlashMode) != null)
                setUiItem(left_ui_items_holder, parameterHandler.get(Settings.FlashMode), SettingsManager.get(Settings.FlashMode), R.drawable.quck_set_flash);
            if (parameterHandler.get(Settings.FocusMode) != null)
                setUiItem(left_ui_items_holder, parameterHandler.get(Settings.FocusMode), SettingsManager.get(Settings.FocusMode), R.drawable.quck_set_focus);
            if (parameterHandler.get(Settings.ExposureMode) != null)
                setUiItem(left_ui_items_holder, parameterHandler.get(Settings.ExposureMode), SettingsManager.get(Settings.ExposureMode), R.drawable.quck_set_ae);
            if (parameterHandler.get(Settings.AE_PriorityMode) != null)
                setUiItem(left_ui_items_holder, parameterHandler.get(Settings.AE_PriorityMode), SettingsManager.get(Settings.AE_PriorityMode), R.drawable.ae_priority);
            if (parameterHandler.get(Settings.ContShootMode) != null)
                setUiItem(left_ui_items_holder, parameterHandler.get(Settings.ContShootMode), "", R.drawable.quck_set_contin);
            if (parameterHandler.get(Settings.HDRMode) != null)
                setUiItem(left_ui_items_holder, parameterHandler.get(Settings.HDRMode), SettingsManager.get(Settings.HDRMode), R.drawable.quck_set_hdr);

            if (cameraUiWrapper.getParameterHandler().get(Settings.NightMode) != null && cameraUiWrapper.getParameterHandler().get(Settings.NightMode).IsSupported()) {
                UiSettingsChild night = new UiSettingsChild(getContext());
                night.SetStuff(fragment_activityInterface, SettingsManager.NIGHTMODE);
                night.SetMenuItemClickListner(this, true);
                night.SetParameter(cameraUiWrapper.getParameterHandler().get(Settings.NightMode));
                night.setBackgroundResource(R.drawable.quck_set_night);
                left_ui_items_holder.addView(night);
            }

            if (cameraUiWrapper.getParameterHandler().get(Settings.PictureFormat) != null) {
                setUiItem(left_ui_items_holder, parameterHandler.get(Settings.PictureFormat), SettingsManager.get(Settings.PictureFormat), R.drawable.quck_set_format2);
            }


            //right camera top camerui itmes

            if (isAdded()) {
                UiSettingsChildModuleSwitch moduleSwitch = new UiSettingsChildModuleSwitch(getContext());
                moduleSwitch.SetCameraUiWrapper(cameraUiWrapper);
                moduleSwitch.SetStuff(SettingsManager.get(Settings.Module));
                moduleSwitch.SetMenuItemClickListner(this, false);
                moduleSwitch.setBackgroundResource(R.drawable.quck_set_mode);
                right_ui_items_top.addView(moduleSwitch);

                if (parameterHandler.get(Settings.Focuspeak) != null && parameterHandler.get(Settings.Focuspeak).IsSupported() && cameraUiWrapper.getRenderScriptManager().isSucessfullLoaded()) {
                    UiSettingsFocusPeak focusPeak = new UiSettingsFocusPeak(getContext());
                    focusPeak.SetParameter(cameraUiWrapper.getParameterHandler().get(Settings.Focuspeak));
                    focusPeak.SetCameraUiWrapper(cameraUiWrapper);
                    focusPeak.SetStuff(fragment_activityInterface, SettingsManager.SETTING_FOCUSPEAK);
                    focusPeak.SetUiItemClickListner(this);
                    focusPeak.setBackgroundResource(R.drawable.quck_set_zebra);
                    right_ui_items_top.addView(focusPeak);
                }

                cameraSwitch.setVisibility(View.VISIBLE);
                cameraSwitch.SetCameraUiWrapper(cameraUiWrapper);
                focusImageHandler.SetCamerUIWrapper(cameraUiWrapper);

                messageHandler.SetCameraUiWrapper(cameraUiWrapper);
                shutterButton.setVisibility(View.VISIBLE);
                shutterButton.SetCameraUIWrapper(cameraUiWrapper, messageHandler);

                cameraUiWrapper.getModuleHandler().setWorkListner(this);

                if (manualModesFragment != null)
                    manualModesFragment.setCameraToUi(cameraUiWrapper);

                guideHandler.setCameraUiWrapper(cameraUiWrapper);

                horizontLineFragment.setCameraUiWrapper(cameraUiWrapper);
                infoOverlayHandler.setCameraUIWrapper(cameraUiWrapper);
                shutterButton.setVisibility(View.VISIBLE);
                aelock.setVisibility(View.VISIBLE);
                aelock.SetParameter(cameraUiWrapper.getParameterHandler().get(Settings.ExposureLock));


                //restore view state for the manuals
                if (manualsettingsIsOpen)
                    showManualSettings();
                //remove the values fragment from ui when a new api gets loaded and it was open.
                if (horizontalValuesFragment != null && horizontalValuesFragment.isAdded())
                    removeHorizontalFragment();

                if (cameraUiWrapper instanceof SonyCameraRemoteFragment) {
                    joyPad.setVisibility(View.GONE);
                    if (cameraUiWrapper.getParameterHandler().get(Settings.scalePreview) != null)
                        cameraUiWrapper.getParameterHandler().get(Settings.scalePreview).addEventListner(joyPad);
                    joyPad.setNavigationClickListner((SimpleStreamSurfaceView) cameraUiWrapper.getSurfaceView());
                } else
                    joyPad.setVisibility(View.GONE);

                //register timer to to moduleevent handler that it get shown/hidden when its video or not
                //and start/stop working when recording starts/stops
                cameraUiWrapper.getModuleHandler().addListner(settingsChildSelfTimer);
            }
        }
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
        cameraSwitch.SetStuff(fragment_activityInterface, SettingsManager.CURRENTCAMERA);

        infoOverlayHandler = new SampleInfoOverlayHandler(view);
        infoOverlayHandler.setCameraUIWrapper(cameraUiWrapper);

        focusImageHandler = new FocusImageHandler(view, (ActivityAbstract) getActivity());

        shutterButton = (ShutterButton) view.findViewById(id.shutter_button);

        view.setOnTouchListener(onTouchListener);

        aelock = (UiSettingsChild)view.findViewById(id.ae_lock);
        aelock.SetUiItemClickListner(this);
        aelock.SetStuff(fragment_activityInterface, "");

        settingsChildSelfTimer = (UiSettingsChildSelfTimer)view.findViewById(id.selftimer);
        settingsChildSelfTimer.SetUiItemClickListner(this);
        settingsChildSelfTimer.SetStuff(SettingsManager.get(Settings.selfTimer));


        manualModesFragment = new ManualFragment();

        horizontLineFragment = new HorizontLineFragment();

        guideHandler =GuideHandler.getInstance();

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

        boolean showhelp = SettingsManager.getInstance().getShowHelpOverlay();
        if (showhelp) {
            transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(anim.empty, anim.empty);
            transaction.replace(id.helpfragment_container, HelpFragment.getFragment(helpfragmentCloser));
            transaction.addToBackStack(null);
            transaction.commit();
        }
        setCameraToUi(cameraUiWrapper);
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
