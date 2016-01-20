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
import android.widget.ImageView;
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
import troop.com.themesample.views.uichilds.UiSettingsChildFormat;
import troop.com.themesample.views.uichilds.UiSettingsChildModuleSwitch;
import troop.com.themesample.views.uichilds.UiSettingsFocusPeak;
import troop.com.themesample.views.uichilds.UiSettingsMenu;

/**
 * Created by troop on 14.06.2015.
 */
public class CameraUiFragment extends AbstractFragment implements I_ParametersLoaded, Interfaces.I_MenuItemClick, Interfaces.I_CloseNotice, I_swipe, View.OnClickListener
{
    final String TAG = CameraUiFragment.class.getSimpleName();
    UiSettingsChild flash;
    UiSettingsChild iso;
    UiSettingsChild autoexposure;
    UiSettingsChild whitebalance;
    UiSettingsChild focus;
    UiSettingsChild night;
    UiSettingsChildFormat format;
    UiSettingsChildCameraSwitch cameraSwitch;
    UiSettingsChildExit exit;
    UiSettingsChildModuleSwitch modeSwitch;
    UiSettingsMenu menu;
    UiSettingsChild contShot;

    UiSettingsChild currentOpendChild;
    HorizontalValuesFragment horizontalValuesFragment;
    SwipeMenuListner touchHandler;
    ShutterButton shutterButton;

    UiSettingsFocusPeak focuspeak;

    UserMessageHandler messageHandler;

    ThumbView thumbView;

    ImageView ManualSettingsButton;
    LinearLayout left_cameraUI_holder;
    RelativeLayout right_camerUI_holder;
    ManualFragmentRotatingSeekbar manualModesFragment;
    FrameLayout manualModes_holder;
    boolean manualsettingsIsOpen = false;
    boolean settingsOpen = false;
    final int animationTime = 500;

    FocusImageHandler focusImageHandler;


    View view;
    I_Activity i_activity;
    AppSettingsManager appSettingsManager;
    SampleInfoOverlayHandler infoOverlayHandler;

    GuideHandler guideHandler;
    LinearLayout guidHolder;

    SettingsMenuFragment settingsMenuFragment;
    FrameLayout settingsmenuholer;
    File lastFile;

    final String KEY_MANUALMENUOPEN = "key_manualmenuopen";
    SharedPreferences sharedPref;

    HorizontLineFragment horizontLineFragment;

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
        this.messageHandler = new UserMessageHandler(view, appSettingsManager);
        messageHandler.SetCameraUiWrapper(wrapper);
        shutterButton.SetCameraUIWrapper(wrapper, appSettingsManager, messageHandler);

        format.SetCameraUiWrapper(wrapper);
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

        horizontLineFragment.setCameraUiWrapper(wrapper, appSettingsManager);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(TAG, "####################ONCREATEDVIEW####################");
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
        this.ManualSettingsButton = (ImageView)view.findViewById(R.id.fastsettings_button);
        ManualSettingsButton.setOnClickListener(onSettingsClick);

        // this.wbtest = (ImgItem)view.findViewById(R.id.testwb);
        //wbtest.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_WHITEBALANCEMODE);
        // wbtest.SetMenuItemListner(this);

        this.flash = (UiSettingsChild)view.findViewById(R.id.Flash);
        flash.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_FLASHMODE);
        flash.SetMenuItemListner(this);


        this.iso = (UiSettingsChild)view.findViewById(R.id.ui_settings_iso);
        iso.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_ISOMODE);

        iso.SetMenuItemListner(this);

        this.autoexposure =(UiSettingsChild)view.findViewById(R.id.Ae);
        autoexposure.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_EXPOSUREMODE);
        autoexposure.SetMenuItemListner(this);
        this.whitebalance = (UiSettingsChild)view.findViewById(R.id.wb);
        whitebalance.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_WHITEBALANCEMODE);
        whitebalance.SetMenuItemListner(this);
        this.focus = (UiSettingsChild)view.findViewById(R.id.focus_uisetting);
        focus.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_FOCUSMODE);
        focus.SetMenuItemListner(this);
        this.contShot = (UiSettingsChild)view.findViewById(R.id.continousShot);
        contShot.SetStuff(i_activity, appSettingsManager, null);
        contShot.SetMenuItemListner(this);
        this.night = (UiSettingsChild)view.findViewById(R.id.night);
        night.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_NIGHTEMODE);
        night.SetMenuItemListner(this);
        this.format = (UiSettingsChildFormat)view.findViewById(R.id.format);
        format.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_PICTUREFORMAT);
        format.SetMenuItemListner(this);
        this.thumbView = (ThumbView)view.findViewById(R.id.thumbview);
        this.modeSwitch = (UiSettingsChildModuleSwitch)view.findViewById(R.id.mode_switch);
        modeSwitch.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_CURRENTMODULE);
        modeSwitch.SetMenuItemListner(this);
        exit = (UiSettingsChildExit)view.findViewById(R.id.exit);
        exit.SetStuff(i_activity, appSettingsManager, "");
        cameraSwitch = (UiSettingsChildCameraSwitch)view.findViewById(R.id.camera_switch);
        cameraSwitch.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_CURRENTCAMERA);
        infoOverlayHandler = new SampleInfoOverlayHandler(view, appSettingsManager);
        infoOverlayHandler.setCameraUIWrapper(wrapper);

        focusImageHandler = new FocusImageHandler(view, this, i_activity);
        touchHandler = new SwipeMenuListner(this);
        shutterButton = (ShutterButton)view.findViewById(R.id.shutter_button);
        view.setOnTouchListener(onTouchListener);

        focuspeak = (UiSettingsFocusPeak)view.findViewById(R.id.ui_focuspeak);
        focuspeak.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_FOCUSPEAK);
        focuspeak.SetMenuItemListner(this);



        if(!manualsettingsIsOpen)
            manualModes_holder.setVisibility(View.GONE);



        guideHandler = new GuideHandler();
        android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.guideHolder, guideHandler, "Guide");

        transaction.commitAllowingStateLoss();

        guidHolder = (LinearLayout)view.findViewById(R.id.guideHolder);

        this.settingsmenuholer = (FrameLayout)view.findViewById(R.id.settingsMenuHolder);
        settingsMenuFragment = new SettingsMenuFragment();
        settingsMenuFragment.SetStuff(appSettingsManager, i_activity);
        settingsMenuFragment.SetCameraUIWrapper(wrapper);
        transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.empty, R.anim.empty);
        transaction.replace(R.id.settingsMenuHolder, settingsMenuFragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
        settingsmenuholer.setVisibility(View.GONE);

        manualModesFragment = new ManualFragmentRotatingSeekbar();
        manualModesFragment.SetStuff(appSettingsManager, i_activity);
        manualModesFragment.SetCameraUIWrapper(wrapper);

        transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.bottom_to_top_enter, R.anim.empty);
        transaction.replace(R.id.manualModesHolder, manualModesFragment);
        transaction.commitAllowingStateLoss();

        horizontLineFragment = new HorizontLineFragment();
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
            if (manualsettingsIsOpen)
                hide_ManualSettings();
            else
                showManualSettings();
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

        i_activity.loadImageViewerFragment(lastFile);

    }

    @Override
    public void doLeftToRightSwipe(){}

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

    View.OnClickListener onSettingsClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (!settingsOpen)
                replaceCameraUIWithSettings();
            else
                replaceSettingsWithCameraUI();
        }
    };

    private void replaceCameraUIWithSettings()
    {
        settingsOpen = true;
        manualModes_holder.setVisibility(View.GONE);
        settingsmenuholer.setVisibility(View.VISIBLE);
    }

    private void replaceSettingsWithCameraUI()
    {
        settingsOpen = false;
        if(manualsettingsIsOpen)
            manualModes_holder.setVisibility(View.VISIBLE);
        settingsmenuholer.setVisibility(View.GONE);
    }
}
