package troop.com.themesample.subfragments;

import android.animation.Animator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.ui.AbstractFragment;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;

import troop.com.themesample.R;
import troop.com.themesample.handler.SampleInfoOverlayHandler;
import troop.com.themesample.views.ThumbView;
import troop.com.themesample.views.UiSettingsChild;
import troop.com.themesample.views.UiSettingsChildCameraSwitch;
import troop.com.themesample.views.UiSettingsChildExit;
import troop.com.themesample.views.UiSettingsChildModeSwitch;
import troop.com.themesample.views.UiSettingsMenu;

/**
 * Created by troop on 14.06.2015.
 */
public class CameraUiFragment extends AbstractFragment implements I_ParametersLoaded, Interfaces.I_MenuItemClick, Interfaces.I_CloseNotice
{
    final String TAG = CameraUiFragment.class.getSimpleName();

    UiSettingsChild flash;
    UiSettingsChild iso;
    UiSettingsChild autoexposure;
    UiSettingsChild whitebalance;
    UiSettingsChild focus;
    UiSettingsChild night;
    UiSettingsChild format;
    UiSettingsChildCameraSwitch cameraSwitch;
    UiSettingsChildExit exit;
    UiSettingsChildModeSwitch modeSwitch;
    UiSettingsMenu menu;

    UiSettingsChild currentOpendChild;
    HorizontalValuesFragment horizontalValuesFragment;



    ThumbView thumbView;

    ImageView SettingsButton;
    LinearLayout left_cameraUI_holder;
    RelativeLayout right_camerUI_holder;
    boolean settingsIsOpen = true;
    final int animationTime = 500;

    AbstractCameraUiWrapper abstractCameraUiWrapper;

    View view;
    I_Activity i_activity;
    AppSettingsManager appSettingsManager;
    SampleInfoOverlayHandler infoOverlayHandler;
    View.OnClickListener onSettingsClickListner;

    public void SetStuff(AppSettingsManager appSettingsManager, I_Activity i_activity, View.OnClickListener onSettingsClickListner)
    {
        SetStuff(appSettingsManager, i_activity);
        this.onSettingsClickListner = onSettingsClickListner;
    }

    @Override
    public void SetStuff(AppSettingsManager appSettingsManager, I_Activity i_activity)
    {
        this.i_activity = i_activity;
        this.appSettingsManager = appSettingsManager;

    }

    @Override
    public void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper)
    {
        this.abstractCameraUiWrapper = wrapper;
        abstractCameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
    }

    private void setWrapper()
    {
        flash.SetParameter(abstractCameraUiWrapper.camParametersHandler.FlashMode);
        //abstractCameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(flash);
        iso.SetParameter(abstractCameraUiWrapper.camParametersHandler.IsoMode);
        //abstractCameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(iso);
        autoexposure.SetParameter(abstractCameraUiWrapper.camParametersHandler.ExposureMode);
        //abstractCameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(autoexposure);
        whitebalance.SetParameter(abstractCameraUiWrapper.camParametersHandler.WhiteBalanceMode);
        //abstractCameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(whitebalance);
        focus.SetParameter(abstractCameraUiWrapper.camParametersHandler.FocusMode);
        //abstractCameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(focus);
        night.SetParameter(abstractCameraUiWrapper.camParametersHandler.NightMode);
        //abstractCameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(night);
        thumbView.INIT(i_activity,abstractCameraUiWrapper);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.view = inflater.inflate(R.layout.cameraui, container, false);
        this.left_cameraUI_holder = (LinearLayout)view.findViewById(R.id.left_ui_holder);
        this.right_camerUI_holder = (RelativeLayout)view.findViewById(R.id.right_ui_holder);
        this.SettingsButton = (ImageView)view.findViewById(R.id.fastsettings_button);
        SettingsButton.setOnClickListener(settingsButtonClick);
        this.menu = (UiSettingsMenu)view.findViewById(R.id.menu);
        menu.setOnClickListener(onSettingsClickListner);
        this.flash = (UiSettingsChild)view.findViewById(R.id.Flash);
        flash.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_FLASHMODE);
        flash.SetMenuItemListner(this);
        this.iso = (UiSettingsChild)view.findViewById(R.id.Iso);
        iso.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_ISOMODE);
        iso.SetMenuItemListner(this);
        this.autoexposure =(UiSettingsChild)view.findViewById(R.id.Ae);
        autoexposure.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_EXPOSUREMODE);
        autoexposure.SetMenuItemListner(this);
        this.whitebalance = (UiSettingsChild)view.findViewById(R.id.wb);
        whitebalance.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_WHITEBALANCEMODE);
        whitebalance.SetMenuItemListner(this);
        this.focus = (UiSettingsChild)view.findViewById(R.id.focus);
        focus.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_FOCUSMODE);
        focus.SetMenuItemListner(this);
        this.night = (UiSettingsChild)view.findViewById(R.id.focus);
        night.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_NIGHTEMODE);
        night.SetMenuItemListner(this);
        this.format = (UiSettingsChild)view.findViewById(R.id.format);
        format.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_PICTUREFORMAT);
        this.thumbView = (ThumbView)view.findViewById(R.id.thumbview);
        this.modeSwitch = (UiSettingsChildModeSwitch)view.findViewById(R.id.mode_switch);
        modeSwitch.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_CURRENTMODULE);
        exit = (UiSettingsChildExit)view.findViewById(R.id.exit);
        exit.SetStuff(i_activity, appSettingsManager, "");
        cameraSwitch = (UiSettingsChildCameraSwitch)view.findViewById(R.id.camera_switch);
        cameraSwitch.SetStuff(i_activity, appSettingsManager, AppSettingsManager.SETTING_CURRENTCAMERA);
        infoOverlayHandler = new SampleInfoOverlayHandler(view, appSettingsManager);
        infoOverlayHandler.setCameraUIWrapper(abstractCameraUiWrapper);
        setWrapper();
        return view;
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
            Log.d(TAG, "OnSettingsClick settings open:" + settingsIsOpen);
            if (settingsIsOpen)
                hide_settings();
            else
                showSettings();
        }
    };

    private void hide_settings()
    {
        settingsIsOpen = false;
        Log.d(TAG, "HideSettings");
        left_cameraUI_holder.animate().alpha(0F).setDuration(animationTime).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                left_cameraUI_holder.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();

        right_camerUI_holder.animate().alpha(0F).setDuration(animationTime).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                right_camerUI_holder.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    private void showSettings()
    {
        Log.d(TAG, "ShowSettings");
        settingsIsOpen = true;
        left_cameraUI_holder.setAlpha(0F);
        left_cameraUI_holder.setVisibility(View.VISIBLE);
        left_cameraUI_holder.animate().alpha(1F).setDuration(animationTime).setListener(null).start();

        right_camerUI_holder.setAlpha(0F);
        right_camerUI_holder.setVisibility(View.VISIBLE);
        right_camerUI_holder.animate().alpha(1F).setDuration(animationTime).setListener(null).start();
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
        horizontalValuesFragment.SetStringValues(item.GetValues(),this);
        infalteIntoHolder(R.id.cameraui_values_fragment_holder, horizontalValuesFragment);

    }

    private void infalteIntoHolder(int id, HorizontalValuesFragment fragment)
    {
        android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.left_to_right_enter, 0);
        transaction.replace(id, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void removeHorizontalFragment()
    {
        getActivity().getSupportFragmentManager().beginTransaction().remove(horizontalValuesFragment).setCustomAnimations(0, R.anim.right_to_left_exit).commit();
    }


    @Override
    public void onClose(String value) {
        currentOpendChild.SetValue(value);
        removeHorizontalFragment();
        currentOpendChild = null;
    }
}
