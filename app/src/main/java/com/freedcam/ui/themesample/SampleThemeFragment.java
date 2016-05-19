package com.freedcam.ui.themesample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.apis.basecamera.camera.parameters.I_ParametersLoaded;
import com.freedcam.ui.AbstractFragment;
import com.freedcam.ui.I_Activity;
import com.freedcam.ui.themesample.subfragments.CameraUiFragment;
import com.freedcam.ui.themesample.subfragments.SettingsMenuFragment;
import com.freedcam.ui.views.PagingView;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;
import com.freedviewer.screenslide.ScreenSlideFragment;
import com.troop.freedcam.R;

import java.io.File;

/**
 * Created by troop on 09.06.2015.
 */
public class SampleThemeFragment extends AbstractFragment implements I_ParametersLoaded
{
    final String TAG = SampleThemeFragment.class.getSimpleName();

    private I_Activity i_activity;

    private PagingView mPager;
    private PagerAdapter mPagerAdapter;
    private CameraUiFragment cameraUiFragment;
    private SettingsMenuFragment settingsMenuFragment;
    private ScreenSlideFragment screenSlideFragment;
    private boolean pagerTouchAllowed = true;
    private AppSettingsManager appSettingsManager;

    public SampleThemeFragment()
    {
    }

    public static SampleThemeFragment GetInstance(AppSettingsManager appSettingsManager,AbstractCameraUiWrapper cameraUiWrapper)
    {
        SampleThemeFragment sampleThemeFragment = new SampleThemeFragment();
        sampleThemeFragment.SetAppSettingsManager(appSettingsManager);
        sampleThemeFragment.cameraUiWrapper = cameraUiWrapper;
        return sampleThemeFragment;
    }

    public void SetAppSettingsManager(AppSettingsManager appSettingsManager)
    {
        this.appSettingsManager = appSettingsManager;
        this.i_activity = (I_Activity)getActivity();
    }

    @Override
    public void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper)
    {
        Logger.d(TAG, "SetCameraUiWrapper");
        this.cameraUiWrapper = wrapper;
        if (wrapper != null)
            wrapper.camParametersHandler.AddParametersLoadedListner(this);
        if (cameraUiFragment != null) {
            cameraUiFragment.SetCameraUIWrapper(wrapper);
        }
        if (settingsMenuFragment != null)
            settingsMenuFragment.SetCameraUIWrapper(wrapper);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.samplethemefragment, container, false);
        this.mPager = (PagingView)view.findViewById(R.id.viewPager_fragmentHolder);
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(1);
        return view;
    }


    private ScreenSlideFragment.I_ThumbClick onThumbClick = new ScreenSlideFragment.I_ThumbClick() {
        @Override
        public void onThumbClick() {
            mPager.setCurrentItem(2);
        }

        @Override
        public void newImageRecieved(File file) {
            if (screenSlideFragment != null)
                screenSlideFragment.addFile(file);
        }
    };

    private ScreenSlideFragment.I_ThumbClick onThumbBackClick = new ScreenSlideFragment.I_ThumbClick() {
        @Override
        public void onThumbClick()
        {
            if (mPager != null)
                mPager.setCurrentItem(1);
        }

        @Override
        public void newImageRecieved(File file) {

        }
    };


    public void DisablePagerTouch(boolean disable)
    {
        if (disable)
            mPager.EnableScroll(false);
        else
            mPager.EnableScroll(true);
    }

    @Override
    public void ParametersLoaded() {
        if (cameraUiFragment != null) {
            cameraUiFragment.SetCameraUIWrapper(cameraUiWrapper);
        }
        if (settingsMenuFragment != null)
            settingsMenuFragment.SetCameraUIWrapper(cameraUiWrapper);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
    {

        public ScreenSlidePagerAdapter(FragmentManager fm)
        {
            super(fm);

        }

        @Override
        public Fragment getItem(int position)
        {
            if (position == 0)
            {
                if (settingsMenuFragment == null)
                {
                    settingsMenuFragment = SettingsMenuFragment.GetInstance(i_activity,appSettingsManager);
                    settingsMenuFragment.SetCameraUIWrapper(cameraUiWrapper);
                }
                return settingsMenuFragment;
            }
            else if (position == 2)
            {
                if (screenSlideFragment == null) {
                    screenSlideFragment = new ScreenSlideFragment();
                    screenSlideFragment.SetAppSettingsManager(appSettingsManager);
                    screenSlideFragment.SetOnThumbClick(onThumbBackClick);
                }
                return screenSlideFragment;
            }
            else
            {
                if (cameraUiFragment == null)
                    cameraUiFragment = CameraUiFragment.GetInstance(i_activity,onThumbClick,appSettingsManager,cameraUiWrapper);
                return cameraUiFragment;
            }
        }

        @Override
        public int getCount()
        {
            return 3;
        }


    }

}
