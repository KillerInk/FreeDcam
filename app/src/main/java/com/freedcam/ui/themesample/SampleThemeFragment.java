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
import com.freedviewer.helper.BitmapHelper;
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
    private BitmapHelper bitmapHelper;

    public SampleThemeFragment()
    {
    }

    public static SampleThemeFragment GetInstance(AppSettingsManager appSettingsManager,AbstractCameraUiWrapper cameraUiWrapper, BitmapHelper bitmapHelper)
    {
        SampleThemeFragment sampleThemeFragment = new SampleThemeFragment();
        sampleThemeFragment.SetAppSettingsManagerAndBitmapHelper(appSettingsManager, bitmapHelper);
        sampleThemeFragment.cameraUiWrapper = cameraUiWrapper;
        return sampleThemeFragment;
    }

    public void SetAppSettingsManagerAndBitmapHelper(AppSettingsManager appSettingsManager, BitmapHelper bitmapHelper)
    {
        this.appSettingsManager = appSettingsManager;
        this.bitmapHelper =bitmapHelper;
    }

    @Override
    public void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper)
    {
        Logger.d(TAG, "SetCameraUiWrapper");
        this.cameraUiWrapper = wrapper;
        if (wrapper != null)
            wrapper.parametersHandler.AddParametersLoadedListner(this);
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
        this.i_activity = (I_Activity)getActivity();
        View view = inflater.inflate(R.layout.samplethemefragment, container, false);
        this.mPager = (PagingView)view.findViewById(R.id.viewPager_fragmentHolder);
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPager.setOffscreenPageLimit(2);
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
                    screenSlideFragment.SetAppSettingsManagerAndBitmapHelper(appSettingsManager, bitmapHelper);
                    screenSlideFragment.SetOnThumbClick(onThumbBackClick);
                }
                return screenSlideFragment;
            }
            else
            {
                if (cameraUiFragment == null)
                    cameraUiFragment = CameraUiFragment.GetInstance(i_activity,onThumbClick,appSettingsManager,cameraUiWrapper, bitmapHelper);
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
