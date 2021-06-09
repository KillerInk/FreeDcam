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

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.troop.freedcam.R;
import com.troop.freedcam.R.anim;
import com.troop.freedcam.R.dimen;
import com.troop.freedcam.R.id;
import com.troop.freedcam.databinding.CamerauiFragmentBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import freed.ActivityAbstract;
import freed.cam.apis.CameraApiManager;
import freed.cam.apis.basecamera.CameraHolderEvent;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.Size;
import freed.cam.apis.basecamera.parameters.ParameterHandler;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.apis.sonyremote.parameters.JoyPad;
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
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.update.ReleaseChecker;
import freed.update.VersionView;
import freed.utils.Log;

/**
 * Created by troop on 14.06.2015.
 */
@AndroidEntryPoint
public class CameraUiFragment extends AbstractFragment implements
        SettingsChildAbstract.SettingsChildClick,
        SettingsChildAbstract.CloseChildClick,
        I_swipe,
        OnClickListener,
        CameraHolderEvent
{
    final String TAG = CameraUiFragment.class.getSimpleName();

    private CamerauiFragmentBinding binding;

    //button to switch between front and back cam
    private UiSettingsChildCameraSwitch cameraSwitch;
    //hold the button wich opened the horizontalValuesFragment
    private SettingsChildAbstract currentOpendChild;
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
    //show the time,sdspace, pic/video size
    private SampleInfoOverlayHandler infoOverlayHandler;
    //holds guide
    private GuideHandler guideHandler;

    private UiSettingsChild aelock;

    private HorizontLineFragment horizontLineFragment;

    private UiSettingsChildSelfTimer settingsChildSelfTimer;


    //get shown in sony api,when the preview gets zoomed to navigate through the img
    private JoyPad joyPad;

    private LinearLayout left_ui_items_holder;
    private LinearLayout right_ui_items_top;
    @Inject
    public SettingsManager settingsManager;
    @Inject
    UserMessageHandler userMessageHandler;
    @Inject
    CameraApiManager cameraApiManager;

    private Handler handler = new Handler();

    public CameraUiFragment()
    {
    }

    /**
     * Creates and Add an Child to the CameraUi
     * @param layout the layout where the child get added
     * @param parameter to use
     * @param backgroundImg id that get used
     */
    private UiSettingsChild setUiItem(LinearLayout layout, ParameterInterface parameter, int backgroundImg)
    {
        UiSettingsChild child = new UiSettingsChild(getContext());
        child.setLifeCycleOwner(getViewLifecycleOwner());
        child.SetParameter(parameter);
        child.setBackgroundResource(backgroundImg);
        child.SetMenuItemClickListner(this,true);
        child.setVisibility(View.VISIBLE);
        layout.addView(child);
        return child;
    }

    /**
     * add an exitbutton to the right top itemholder
     */
    private void addexit()
    {
        UiSettingsChildExit exit = new UiSettingsChildExit(getContext());
        //exit.onStringValueChanged("");
        exit.setBackgroundResource(R.drawable.quck_set_exit);
        exit.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        right_ui_items_top.addView(exit);
    }

    private void setCameraToUi(CameraWrapperInterface wrapper) {
        if (left_ui_items_holder != null) {
            left_ui_items_holder.removeAllViews();
            right_ui_items_top.removeAllViews();
            addexit();
        }
        if (wrapper == null) {
            if (focusImageHandler != null) {
                focusImageHandler.AEMeteringSupported(false);
                focusImageHandler.TouchToFocusSupported(false);
                joyPad.setVisibility(View.GONE);
                cameraSwitch.setVisibility(View.GONE);
                aelock.setVisibility(View.GONE);
                shutterButton.setVisibility(View.GONE);
                //settingsChildSelfTimer.setVisibility(View.GONE);
                if (isAdded())
                    hide_ManualSettings();
            }
        }
        else {

            ParameterHandler parameterHandler = wrapper.getParameterHandler();
            if (parameterHandler == null)
                return;

            //left cameraui items

            if (parameterHandler.get(SettingKeys.HISTOGRAM) != null) {
                UiSettingsFocusPeak focusPeak = new UiSettingsFocusPeak(getContext());
                focusPeak.SetParameter(wrapper.getParameterHandler().get(SettingKeys.HISTOGRAM));
                focusPeak.SetCameraUiWrapper(wrapper);
                focusPeak.SetUiItemClickListner(this);
                focusPeak.setBackgroundResource(R.drawable.quck_set_histogram);
                left_ui_items_holder.addView(focusPeak);
            }
            if (parameterHandler.get(SettingKeys.CLIPPING) != null) {
                UiSettingsFocusPeak focusPeak = new UiSettingsFocusPeak(getContext());
                focusPeak.SetParameter(wrapper.getParameterHandler().get(SettingKeys.CLIPPING));
                focusPeak.SetCameraUiWrapper(wrapper);
                focusPeak.SetUiItemClickListner(this);
                focusPeak.setBackgroundResource(R.drawable.clipping);
                left_ui_items_holder.addView(focusPeak);
            }

            if (parameterHandler.get(SettingKeys.WhiteBalanceMode) != null) {
                setUiItem(left_ui_items_holder, parameterHandler.get(SettingKeys.WhiteBalanceMode), R.drawable.quck_set_wb);
            }
            if (parameterHandler.get(SettingKeys.IsoMode) != null)
                setUiItem(left_ui_items_holder, parameterHandler.get(SettingKeys.IsoMode), R.drawable.quck_set_iso_png);
            if (parameterHandler.get(SettingKeys.FlashMode) != null)
                setUiItem(left_ui_items_holder, parameterHandler.get(SettingKeys.FlashMode), R.drawable.quck_set_flash);
            if (parameterHandler.get(SettingKeys.FocusMode) != null)
                setUiItem(left_ui_items_holder, parameterHandler.get(SettingKeys.FocusMode), R.drawable.quck_set_focus);
            /*UiSettingsChild focus = left_ui_items_holder.findViewById(id.focusmode);
            focus.SetParameter(parameterHandler.get(SettingKeys.FocusMode));*/
            if (parameterHandler.get(SettingKeys.ExposureMode) != null)
                setUiItem(left_ui_items_holder, parameterHandler.get(SettingKeys.ExposureMode), R.drawable.quck_set_ae);
            if (parameterHandler.get(SettingKeys.AE_PriorityMode) != null)
                setUiItem(left_ui_items_holder, parameterHandler.get(SettingKeys.AE_PriorityMode), R.drawable.ae_priority);
            if (parameterHandler.get(SettingKeys.ContShootMode) != null)
                setUiItem(left_ui_items_holder, parameterHandler.get(SettingKeys.ContShootMode), R.drawable.quck_set_contin);
            if (parameterHandler.get(SettingKeys.HDRMode) != null)
                setUiItem(left_ui_items_holder, parameterHandler.get(SettingKeys.HDRMode), R.drawable.quck_set_hdr);

            if (wrapper.getParameterHandler().get(SettingKeys.NightMode) != null) {
                UiSettingsChild night = new UiSettingsChild(getContext());
                night.SetMenuItemClickListner(this, true);
                night.SetParameter(wrapper.getParameterHandler().get(SettingKeys.NightMode));
                night.setBackgroundResource(R.drawable.quck_set_night);
                left_ui_items_holder.addView(night);
            }
            if (wrapper.getParameterHandler().get(SettingKeys.selfTimer) != null)
                settingsChildSelfTimer.SetParameter(wrapper.getParameterHandler().get(SettingKeys.selfTimer));

            if (wrapper.getParameterHandler().get(SettingKeys.PictureFormat) != null) {
                setUiItem(left_ui_items_holder, parameterHandler.get(SettingKeys.PictureFormat), R.drawable.quck_set_format2);
            }

            //right camera top camerui itmes

            if (isAdded()) {
                UiSettingsChildModuleSwitch moduleSwitch = new UiSettingsChildModuleSwitch(getContext());
                moduleSwitch.SetCameraUiWrapper(wrapper);
                moduleSwitch.SetMenuItemClickListner(this, false);
                moduleSwitch.setBackgroundResource(R.drawable.quck_set_mode);
                moduleSwitch.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                right_ui_items_top.addView(moduleSwitch);

                if (parameterHandler.get(SettingKeys.Focuspeak) != null) {
                    UiSettingsFocusPeak focusPeak = new UiSettingsFocusPeak(getContext());
                    focusPeak.SetParameter(wrapper.getParameterHandler().get(SettingKeys.Focuspeak));
                    focusPeak.SetCameraUiWrapper(wrapper);
                    focusPeak.SetUiItemClickListner(this);
                    focusPeak.setBackgroundResource(R.drawable.quck_set_zebra);
                    focusPeak.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                    right_ui_items_top.addView(focusPeak);
                }

                cameraSwitch.setVisibility(View.VISIBLE);
                cameraSwitch.SetCameraUiWrapper(wrapper);
                cameraSwitch.SetUiItemClickListner(this);
                cameraSwitch.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                focusImageHandler.SetCamerUIWrapper(wrapper);

                shutterButton.setVisibility(View.VISIBLE);
                shutterButton.SetCameraUIWrapper(wrapper);

                guideHandler.setCameraUiWrapper(wrapper);

                horizontLineFragment.setCameraUiWrapper(wrapper);
                infoOverlayHandler.setCameraUIWrapper(wrapper);
                shutterButton.setVisibility(View.VISIBLE);
                aelock.setVisibility(View.VISIBLE);
                aelock.SetParameter(wrapper.getParameterHandler().get(SettingKeys.ExposureLock));


                //restore view state for the manuals
                if (settingsManager.getGlobal(SettingKeys.SHOWMANUALSETTINGS).get())
                    showManualSettings();
                //remove the values fragment from ui when a new api gets loaded and it was open.
                if (horizontalValuesFragment != null && horizontalValuesFragment.isAdded())
                    removeHorizontalFragment();


                joyPad.setVisibility(View.GONE);
            }
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        binding = CamerauiFragmentBinding.inflate(inflater);
        Log.d(TAG, "####################ONCREATEDVIEW####################");
        touchHandler = new SwipeMenuListner(this);
        manualsettingsIsOpen = settingsManager.getGlobal(SettingKeys.SHOWMANUALSETTINGS).get();
        return binding.getRoot();
    }

    private FrameLayout versionView;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.versionView =binding.framelayoutVersion;
        userMessageHandler.setMessageTextView(view.findViewById(id.textView_usermessage), view.findViewById(id.userMessageHolder));
        manualModes_holder = binding.manualModesHolder;
        left_ui_items_holder = binding.leftUiHolder;
        right_ui_items_top = binding.rightUiHolderTop;
        addexit();
        cameraApiManager.addEventListner(this);
        cameraSwitch = binding.cameraSwitch;


        infoOverlayHandler = new SampleInfoOverlayHandler(view);

        focusImageHandler = new FocusImageHandler(view, (ActivityAbstract) getActivity());

        shutterButton = binding.shutterButton;

        view.setOnTouchListener(onTouchListener);

        aelock = binding.aeLock;
        aelock.SetUiItemClickListner(this);


        settingsChildSelfTimer = binding.selftimer;
        settingsChildSelfTimer.SetUiItemClickListner(this);




        manualModesFragment = new ManualFragment();

        horizontLineFragment = new HorizontLineFragment();

        guideHandler =GuideHandler.getInstance();

        manualModes_holder.setVisibility(View.GONE);
        camerauiValuesFragmentHolder = binding.camerauiValuesFragmentHolder;
        joyPad = view.findViewById(id.joypad);
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

        boolean showhelp = settingsManager.getShowHelpOverlay();
        if (showhelp) {
            transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(anim.empty, anim.empty);
            transaction.replace(id.helpfragment_container, HelpFragment.getFragment(helpfragmentCloser));
            transaction.addToBackStack(null);
            transaction.commit();
        }
        setCameraToUi(cameraApiManager.getCamera());
        checkForUpdate();

    }

    private void checkForUpdate() {
        if (ReleaseChecker.isGithubRelease && settingsManager.getGlobal(SettingKeys.CHECKFORUPDATES).get()) {
            new ReleaseChecker(new ReleaseChecker.UpdateEvent() {
                @Override
                public void onUpdateAvailable() {
                    versionView.post(new Runnable() {
                        @Override
                        public void run() {
                            versionView.addView(new VersionView(getContext(), new VersionView.ButtonEvents() {
                                @Override
                                public void onDownloadClick() {
                                    startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/KillerInk/FreeDcam/releases/latest")), "Choose browser"));
                                    versionView.removeAllViews();
                                }

                                @Override
                                public void onCloseClick() {
                                    try {
                                        if (versionView != null)
                                            versionView.removeAllViews();
                                    }
                                    catch (ActivityNotFoundException ex)
                                    {
                                        Log.WriteEx(ex);
                                    }

                                }
                            }));
                        }
                    });
                }
            }).isUpdateAvailable();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        userMessageHandler.setMessageTextView(null,null);
        cameraApiManager.removeEventListner(this);
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
        settingsManager.getGlobal(SettingKeys.SHOWMANUALSETTINGS).set(manualsettingsIsOpen);
        super.onPause();

    }

    private void hide_ManualSettings()
    {
        manualsettingsIsOpen = false;
        Log.d(TAG, "HideSettings");
        manualModes_holder.animate().translationY(manualModes_holder.getHeight()).setDuration(300);
    }

    private void showManualSettings()
    {
        Log.d(TAG, "ShowSettings");
        manualsettingsIsOpen = true;
        manualModes_holder.animate().translationY(0).setDuration(300);
        manualModes_holder.setVisibility(View.VISIBLE);
    }


    @Override
    public void onSettingsChildClick(SettingsChildAbstract item, boolean fromLeftFragment)
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
    public void onCameraOpen() {

    }

    @Override
    public void onCameraOpenFinished() {
        handler.post(() -> setCameraToUi(cameraApiManager.getCamera()));
    }

    @Override
    public void onCameraClose() {
        handler.post(() -> setCameraToUi(null));
    }

    @Override
    public void onCameraError(String error) {

    }

    @Override
    public void onCameraChangedAspectRatioEvent(Size size) {

    }

   /* @Override
    public void onCaptureStateChanged(ModuleHandlerAbstract.CaptureStates captureStates)
    {
    }*/


    interface i_HelpFragment
    {
        void Close(Fragment fragment);
    }

    private final i_HelpFragment helpfragmentCloser = fragment -> {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.remove(fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    };
}
