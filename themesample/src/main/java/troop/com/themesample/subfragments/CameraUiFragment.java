package troop.com.themesample.subfragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.ui.AbstractFragment;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.I_swipe;
import com.troop.freedcam.ui.SwipeMenuListner;
import com.troop.freedcam.ui.guide.GuideHandler;

import java.io.File;

import troop.com.themesample.R;
import troop.com.themesample.handler.FocusImageHandler;
import troop.com.themesample.handler.SampleInfoOverlayHandler;
import troop.com.themesample.handler.UserMessageHandler;
import troop.com.themesample.views.ShutterButton;
import troop.com.themesample.views.ThumbView;
import troop.com.themesample.views.uichilds.UiSettingsChild;
import troop.com.themesample.views.uichilds.UiSettingsChildCameraSwitch;
import troop.com.themesample.views.uichilds.UiSettingsChildExit;
import troop.com.themesample.views.uichilds.UiSettingsChildModuleSwitch;
import troop.com.themesample.views.uichilds.UiSettingsFocusPeak;
import troop.com.themesample.views.uichilds.UiSettingsMenu;

/**
 * Created by troop on 14.06.2015.
 */
public class CameraUiFragment extends AbstractFragment implements I_ParametersLoaded, Interfaces.I_MenuItemClick, Interfaces.I_CloseNotice, I_swipe, View.OnClickListener
{
    private final String TAG = CameraUiFragment.class.getSimpleName();
    private UiSettingsChild flash;
    private UiSettingsChild iso;
    private UiSettingsChild autoexposure;
    private UiSettingsChild whitebalance;
    private UiSettingsChild focus;
    private UiSettingsChild night;
    private UiSettingsChild format;
    private UiSettingsChildCameraSwitch cameraSwitch;
    private UiSettingsChildExit exit;
    private UiSettingsChildModuleSwitch modeSwitch;
    UiSettingsMenu menu;
    private UiSettingsChild contShot;

    private UiSettingsChild currentOpendChild;
    private HorizontalValuesFragment horizontalValuesFragment;
    private SwipeMenuListner touchHandler;
    private ShutterButton shutterButton;

    private UiSettingsFocusPeak focuspeak;

    private UserMessageHandler messageHandler;

    private UiSettingsChild hdr_switch;

    private ThumbView thumbView;

    private LinearLayout LC;

    private LinearLayout left_cameraUI_holder;
    private RelativeLayout right_camerUI_holder;
    private ManualFragmentRotatingSeekbar manualModesFragment;
    private FrameLayout manualModes_holder;
    private boolean manualsettingsIsOpen = false;
    private boolean settingsOpen = false;
    final int animationTime = 500;

    private FocusImageHandler focusImageHandler;


    private View view;
    private I_Activity i_activity;
    private AppSettingsManager appSettingsManager;
    private SampleInfoOverlayHandler infoOverlayHandler;

    private GuideHandler guideHandler;
    private LinearLayout guidHolder;

    private SettingsMenuFragment settingsMenuFragment;
    private FrameLayout settingsmenuholer;
    private File lastFile;

    private final String KEY_MANUALMENUOPEN = "key_manualmenuopen";
    private final String KEY_SETTINGSOPEN = "key_settingsopen";
    private SharedPreferences sharedPref;

    private HorizontLineFragment horizontLineFragment;

    @Override
    public void SetStuff(AppSettingsManager appSettingsManager, I_Activity i_activity)
    {
        this.i_activity = i_activity;
        this.appSettingsManager = appSettingsManager;
    }

    @Override
    public void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper)
    {
        if (this.wrapper == wrapper)
            return;
        this.wrapper = wrapper;
        if (wrapper != null && wrapper.camParametersHandler != null && wrapper.camParametersHandler.ParametersEventHandler != null)
            wrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
        if(view != null)
            setWrapper();
        if (settingsMenuFragment != null)
            settingsMenuFragment.SetCameraUIWrapper(wrapper);
    }

    private void setWrapper()
    {
        flash.SetParameter(wrapper.camParametersHandler.FlashMode);
        //abstractCameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(flash);
        iso.SetParameter(wrapper.camParametersHandler.IsoMode);
        //abstractCameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(iso);
        autoexposure.SetParameter(wrapper.camParametersHandler.ExposureMode);
        //abstractCameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(autoexposure);
        whitebalance.SetParameter(wrapper.camParametersHandler.WhiteBalanceMode);
        //abstractCameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(whitebalance);
        focus.SetParameter(wrapper.camParametersHandler.FocusMode);
        //abstractCameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(focus);
        night.SetParameter(wrapper.camParametersHandler.NightMode);
        //abstractCameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(night);
        thumbView.INIT(i_activity, wrapper);

        cameraSwitch.SetCameraUiWrapper(wrapper);
        focusImageHandler.SetCamerUIWrapper(wrapper);
        this.messageHandler = new UserMessageHandler(view);
        messageHandler.SetCameraUiWrapper(wrapper);
        shutterButton.SetCameraUIWrapper(wrapper, appSettingsManager);

        format.SetParameter(wrapper.camParametersHandler.PictureFormat);




        contShot.SetParameter(wrapper.camParametersHandler.ContShootMode);
        if (manualModesFragment != null)
            manualModesFragment.SetCameraUIWrapper(wrapper);
        if (wrapper.camParametersHandler.Focuspeak != null) {
            focuspeak.SetParameter(wrapper.camParametersHandler.Focuspeak);
            wrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(focuspeak);
        }
        guideHandler.setCameraUiWrapper(wrapper, appSettingsManager);
        //guideHandler.SetViewG(appSettingsManager.getString(AppSettingsManager.SETTING_GUIDE));

        focuspeak.SetCameraUiWrapper(wrapper);
        modeSwitch.SetCameraUiWrapper(wrapper);

        hdr_switch.SetParameter(wrapper.camParametersHandler.HDRMode);

        horizontLineFragment.setCameraUiWrapper(wrapper, appSettingsManager);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "####################ONCREATEDVIEW####################");

        touchHandler = new SwipeMenuListner(this);
        manualModesFragment = new ManualFragmentRotatingSeekbar();
        manualModesFragment.SetStuff(appSettingsManager, i_activity);
        settingsMenuFragment = new SettingsMenuFragment();
        settingsMenuFragment.SetStuff(appSettingsManager, i_activity,touchHandler);
        horizontLineFragment = new HorizontLineFragment();
        guideHandler = new GuideHandler();
        return inflater.inflate(R.layout.cameraui, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        Log.d(TAG, "####################VIEW CREATED####################");
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        manualsettingsIsOpen = sharedPref.getBoolean(KEY_MANUALMENUOPEN, false);
        this.left_cameraUI_holder = (LinearLayout)view.findViewById(R.id.left_ui_holder);
        this.right_camerUI_holder = (RelativeLayout)view.findViewById(R.id.right_ui_holder);
        this.manualModes_holder = (FrameLayout)view.findViewById(R.id.manualModesHolder);
        this.LC = (LinearLayout)view.findViewById(R.id.LCover);

        // this.wbtest = (ImgItem)view.findViewById(R.id.testwb);
        //wbtest.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_WHITEBALANCEMODE);
        // wbtest.SetMenuItemListner(this);

        this.flash = (UiSettingsChild)view.findViewById(R.id.Flash);
        flash.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_FLASHMODE);
        flash.SetMenuItemListner(this, true);


        this.iso = (UiSettingsChild)view.findViewById(R.id.ui_settings_iso);
        iso.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_ISOMODE);

        iso.SetMenuItemListner(this,true);

        this.autoexposure =(UiSettingsChild)view.findViewById(R.id.Ae);
        autoexposure.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_EXPOSUREMODE);
        autoexposure.SetMenuItemListner(this,true);
        this.whitebalance = (UiSettingsChild)view.findViewById(R.id.wb);
        whitebalance.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_WHITEBALANCEMODE);
        whitebalance.SetMenuItemListner(this,true);
        this.focus = (UiSettingsChild)view.findViewById(R.id.focus_uisetting);
        focus.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_FOCUSMODE);
        focus.SetMenuItemListner(this,true);
        this.contShot = (UiSettingsChild)view.findViewById(R.id.continousShot);
        contShot.SetStuff(i_activity, appSettingsManager, null);
        contShot.SetMenuItemListner(this,true);
        this.night = (UiSettingsChild)view.findViewById(R.id.night);
        night.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_NIGHTEMODE);
        night.SetMenuItemListner(this,true);

        this.format = (UiSettingsChild)view.findViewById(R.id.format);
        format.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_PICTUREFORMAT);
        format.SetMenuItemListner(this,true);

        this.thumbView = (ThumbView)view.findViewById(R.id.thumbview);
        this.modeSwitch = (UiSettingsChildModuleSwitch)view.findViewById(R.id.mode_switch);
        modeSwitch.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_CURRENTMODULE);
        modeSwitch.SetMenuItemListner(this,false);
        exit = (UiSettingsChildExit)view.findViewById(R.id.exit);
        exit.SetStuff(i_activity, appSettingsManager, "");
        cameraSwitch = (UiSettingsChildCameraSwitch)view.findViewById(R.id.camera_switch);
        cameraSwitch.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_CURRENTCAMERA);
        infoOverlayHandler = new SampleInfoOverlayHandler(view, appSettingsManager);
        infoOverlayHandler.setCameraUIWrapper(wrapper);

        focusImageHandler = new FocusImageHandler(view, this, i_activity);

        shutterButton = (ShutterButton)view.findViewById(R.id.shutter_button);
        view.setOnTouchListener(onTouchListener);

        focuspeak = (UiSettingsFocusPeak)view.findViewById(R.id.ui_focuspeak);

        focuspeak.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_FOCUSPEAK);
        focuspeak.SetMenuItemListner(this);

        //adding hdr switch log test v1.0 1-29-2016 6:13 - Defcomk
        this.hdr_switch = (UiSettingsChild)view.findViewById(R.id.hdr_toggle);
        hdr_switch.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_HDRMODE);
        hdr_switch.SetMenuItemListner(this,true);
        ///

        if(!manualsettingsIsOpen)
            manualModes_holder.setVisibility(View.GONE);

        android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.guideHolder, guideHandler, "Guide");

        transaction.commitAllowingStateLoss();

        guidHolder = (LinearLayout)view.findViewById(R.id.guideHolder);

        this.settingsmenuholer = (FrameLayout)view.findViewById(R.id.settingsMenuHolder);

        settingsMenuFragment.SetCameraUIWrapper(wrapper);
        transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.left_to_right_enter, R.anim.empty);
        transaction.replace(R.id.settingsMenuHolder, settingsMenuFragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
        settingsmenuholer.setVisibility(View.GONE);


        manualModesFragment.SetCameraUIWrapper(wrapper);

        transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.bottom_to_top_enter, R.anim.empty);
        transaction.replace(R.id.manualModesHolder, manualModesFragment);
        transaction.commitAllowingStateLoss();


        transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.empty, R.anim.empty);
        transaction.replace(R.id.horHolder, horizontLineFragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
        setWrapper();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
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
        sharedPref.edit().putBoolean(KEY_MANUALMENUOPEN,manualsettingsIsOpen).commit();
        sharedPref.edit().putBoolean(KEY_SETTINGSOPEN,settingsOpen).commit();
        super.onPause();

    }

    @Override
    public void ParametersLoaded() {
        setWrapper();
    }

    View.OnClickListener settingsButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            Log.d(TAG, "OnSettingsClick settings open:" + manualsettingsIsOpen);
            if (manualsettingsIsOpen) {
                hide_ManualSettings();
            }
            else {

                showManualSettings();
            }
        }
    };

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
        if (settingsOpen)
            replaceSettingsWithCameraUI();
        else
            i_activity.loadImageViewerFragment();

    }

    @Override
    public void doLeftToRightSwipe(){
        replaceCameraUIWithSettings();
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

    private View.OnTouchListener onTouchListener = new View.OnTouchListener()
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



    private void replaceCameraUIWithSettings()
    {
        settingsOpen = true;
        sharedPref.edit().putBoolean(KEY_SETTINGSOPEN,settingsOpen).commit();
        manualModes_holder.setVisibility(View.GONE);
        settingsMenuFragment.touchHandler = touchHandler;
        LC.setVisibility(View.VISIBLE);
        LC.setOnTouchListener(onTouchListener);
        settingsmenuholer.animate().translationX(0).setDuration(300);
        settingsmenuholer.setVisibility(View.VISIBLE);
    }

    private void replaceSettingsWithCameraUI()
    {
        settingsOpen = false;
        sharedPref.edit().putBoolean(KEY_SETTINGSOPEN,settingsOpen).commit();
        float width = guidHolder.getWidth();
        settingsmenuholer.animate().translationX(-width).setDuration(300);
        if(manualsettingsIsOpen)
            manualModes_holder.setVisibility(View.VISIBLE);
        LC.setVisibility(View.GONE);
        //settingsmenuholer.setVisibility(View.GONE);
    }
}
