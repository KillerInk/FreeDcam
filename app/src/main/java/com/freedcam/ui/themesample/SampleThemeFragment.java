package com.freedcam.ui.themesample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freedcam.apis.i_camera.AbstractCameraUiWrapper;
import com.freedcam.ui.AbstractFragment;
import com.freedcam.ui.I_Activity;
import com.freedcam.ui.themesample.subfragments.CameraUiFragment;
import com.freedcam.ui.themesample.subfragments.SettingsMenuFragment;
import com.freedcam.ui.views.PagingView;
import com.freedcam.utils.AppSettingsManager;
import com.freedviewer.screenslide.ScreenSlideFragment;
import com.troop.freedcam.R;

import java.io.File;

/**
 * Created by troop on 09.06.2015.
 */
public class SampleThemeFragment extends AbstractFragment
{
    final String TAG = SampleThemeFragment.class.getSimpleName();

    private I_Activity i_activity;
    private CameraUiFragment cameraUiFragment;

    private PagingView mPager;
    private PagerAdapter mPagerAdapter;

    private SettingsMenuFragment settingsMenuFragment;
    private ScreenSlideFragment screenSlideFragment;
    private boolean pagerTouchAllowed = true;
    private AppSettingsManager appSettingsManager;

    public SampleThemeFragment()
    {
    }

    @Override
    public void SetStuff(I_Activity i_activity, AppSettingsManager appSettingsManager) {
        this.i_activity = i_activity;
        this.appSettingsManager = appSettingsManager;
    }

    @Override
    public void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper)
    {
        this.wrapper = wrapper;
        if (cameraUiFragment != null) {
            cameraUiFragment.SetCameraUIWrapper(wrapper);
        }
        if (settingsMenuFragment != null)
        {
            //settingsMenuFragment.SetStuff(i_activity,appSettingsManager);
            settingsMenuFragment.SetCameraUIWrapper(wrapper);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, null);
        return inflater.inflate(R.layout.samplethemefragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mPager = (PagingView)view.findViewById(R.id.viewPager_fragmentHolder);
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(1);

    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();

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
                    settingsMenuFragment = new SettingsMenuFragment();
                settingsMenuFragment.SetStuff(i_activity,appSettingsManager);
                settingsMenuFragment.SetCameraUIWrapper(wrapper);
                return settingsMenuFragment;
            }
            else if (position == 2)
            {
                if (screenSlideFragment == null) {
                    screenSlideFragment = new ScreenSlideFragment();
                }
                screenSlideFragment.SetAppSettingsManager(appSettingsManager);
                screenSlideFragment.SetOnThumbClick(onThumbBackClick);
                return screenSlideFragment;
            }
            else
            {
                if (cameraUiFragment == null)
                    cameraUiFragment = new CameraUiFragment();
                cameraUiFragment.SetStuff(i_activity,onThumbClick,appSettingsManager);
                cameraUiFragment.SetCameraUIWrapper(wrapper);
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
