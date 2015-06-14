package com.troop.freedcam.ui.menu.themes.classic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AbstractFragment;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.I_Fragment;
import com.troop.freedcam.ui.menu.themes.R;
import com.troop.freedcam.ui.menu.themes.classic.manual.ManualMenuFragment;
import com.troop.freedcam.ui.menu.themes.classic.menu.MenuFragment;
import com.troop.freedcam.ui.menu.themes.classic.shutter.ShutterItemsFragments;

import troop.com.themesample.views.ThumbView;

/**
 * Created by troop on 24.03.2015.
 */
public class ClassicUi extends AbstractFragment implements I_Fragment, I_swipe
{
    protected MenuFragment menuFragment;
    protected ManualMenuFragment manualMenuFragment;
    protected ShutterItemsFragments shutterItemsFragment;
    protected AppSettingsManager appSettingsManager;
    protected AbstractCameraUiWrapper cameraUiWrapper;
    protected I_Activity i_activity;
    protected View view;
    protected SwipeMenuListner swipeMenuListner;
    protected boolean settingsLayloutOpen = false;
    protected boolean manualMenuOpen = false;
    protected FocusImageHandler focusImageHandler;
    protected ThumbView thumbView;
    InfoOverlayHandler infoOverlayHandler;
    LinearLayout infoOverlayHolder;

    public ClassicUi(){};

    public void SetStuff(AppSettingsManager appSettingsManager, I_Activity iActivity)
    {
        this.appSettingsManager = appSettingsManager;
        this.i_activity = iActivity;
        menuFragment = new MenuFragment(appSettingsManager, i_activity);
        shutterItemsFragment = new ShutterItemsFragments();
        manualMenuFragment = new ManualMenuFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        inflate(inflater,container);
        view.setOnTouchListener(onTouchListener);
        thumbView = (ThumbView)view.findViewById(R.id.classic_thumb);
        thumbView.INIT(i_activity,cameraUiWrapper);
        inflateShutterItemFragment();
        swipeMenuListner = new SwipeMenuListner(this);
        focusImageHandler = new FocusImageHandler(view, this, i_activity);
        infoOverlayHolder = (LinearLayout)view.findViewById(R.id.infoOverLay);
        infoOverlayHandler= new InfoOverlayHandler(view, appSettingsManager);
        infoOverlayHandler.setCameraUIWrapper(cameraUiWrapper);
        if (cameraUiWrapper != null)
            focusImageHandler.SetCamerUIWrapper(cameraUiWrapper);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        infoOverlayHandler.StartUpdating();
    }

    @Override
    public void onPause() {
        super.onPause();
        infoOverlayHandler.StopUpdating();
    }

    protected void inflate(LayoutInflater inflater, ViewGroup container)
    {
        view = inflater.inflate(R.layout.classicui, container, false);

    }

    View.OnTouchListener onTouchListener = new View.OnTouchListener()
    {
        public boolean onTouch(View v, MotionEvent event)
        {
            return swipeMenuListner.onTouchEvent(event);
        }

    };

    @Override
    public void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper)
    {

        this.cameraUiWrapper = wrapper;
        if (focusImageHandler != null)
            focusImageHandler.SetCamerUIWrapper(cameraUiWrapper);
        setcameraWrapper();
    }


    private void setcameraWrapper() {
        if (manualMenuFragment != null)
            manualMenuFragment.SetCameraUIWrapper(cameraUiWrapper, appSettingsManager);
        if (menuFragment != null)
            menuFragment.SetCameraUIWrapper(cameraUiWrapper, i_activity.GetSurfaceView());
        if (shutterItemsFragment != null)
            shutterItemsFragment.SetCameraUIWrapper(cameraUiWrapper, i_activity.GetSurfaceView(), i_activity);
    }


    private void inflateShutterItemFragment()
    {
        shutterItemsFragment.SetAppSettings(appSettingsManager);
        shutterItemsFragment.SetCameraUIWrapper(cameraUiWrapper, i_activity.GetSurfaceView(), i_activity);

        if (!shutterItemsFragment.isAdded())
        {
            android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.layout__cameraControls, shutterItemsFragment, "Controls");
            transaction.commit();
        }
    }

    private void inflateMenuFragment()
    {

        //menuFragment.SetCameraUIWrapper(cameraUiWrapper, i_activity.GetSurfaceView());
        if (!menuFragment.isAdded()) {
            android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
            transaction.add(R.id.v2_settings_menu, menuFragment, "Menu");
            transaction.commit();
        }
    }

    private void deflateMenuFragment()
    {
        menuFragment.CLEAR();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.remove(menuFragment);
        fragmentTransaction.commit();
    }

    private void inflateManualMenuFragment()
    {

        manualMenuFragment.SetCameraUIWrapper(cameraUiWrapper, appSettingsManager);
        if (!manualMenuFragment.isAdded()) {
            android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
            transaction.add(R.id.manualMenuHolder, manualMenuFragment, "ManualMenu");
            transaction.commit();
        }
    }

    private void deflateManualMenuFragment() {
        android.support.v4.app.FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.remove(manualMenuFragment);
        fragmentTransaction.commit();
    }

    //I_SWIPE START
    @Override
    public void doHorizontalSwipe()
    {
        if (swipeMenuListner.startX - swipeMenuListner.currentX < 0)
        {
            if (!settingsLayloutOpen && cameraUiWrapper != null)
            {
                inflateMenuFragment();
                settingsLayloutOpen = true;
                infoOverlayHolder.setVisibility(View.GONE);
                i_activity.MenuActive(true);
            }
        }
        else
        {
            if (settingsLayloutOpen)
            {
                deflateMenuFragment();
                settingsLayloutOpen = false;
                infoOverlayHolder.setVisibility(View.VISIBLE);
                i_activity.MenuActive(false);

            }
        }
    }

    @Override
    public void doVerticalSwipe()
    {
        if (swipeMenuListner.startY  - swipeMenuListner.currentY < 0)
        {
            if (!manualMenuOpen && cameraUiWrapper != null)
            {
                inflateManualMenuFragment();
                manualMenuOpen = true;
            }
        }
        else
        {
            if (manualMenuOpen)
            {
                deflateManualMenuFragment();
                manualMenuOpen = false;
            }
        }
    }

    @Override
    public void onClick(int x, int y) {
        if (focusImageHandler != null)
            focusImageHandler.OnClick(x, y);
    }
    //I_SWIPE END
}
