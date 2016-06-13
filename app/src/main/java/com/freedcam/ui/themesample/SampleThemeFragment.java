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
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.basecamera.modules.I_WorkEvent;
import com.freedcam.apis.basecamera.parameters.I_ParametersLoaded;
import com.freedcam.ui.AbstractFragment;
import com.freedcam.ui.I_Activity;
import com.freedcam.ui.themesample.subfragments.CameraUiFragment;
import com.freedcam.ui.themesample.subfragments.SettingsMenuFragment;
import com.freedcam.ui.views.PagingView;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;
import com.freedviewer.helper.BitmapHelper;
import com.freedviewer.holder.FileHolder;
import com.freedviewer.screenslide.ScreenSlideFragment;
import com.freedviewer.screenslide.ScreenSlideFragment.I_ThumbClick;
import com.troop.freedcam.R;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

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
    private final boolean pagerTouchAllowed = true;
    private AppSettingsManager appSettingsManager;
    private BitmapHelper bitmapHelper;

    public SampleThemeFragment()
    {
    }

    public static SampleThemeFragment GetInstance(AppSettingsManager appSettingsManager, CameraWrapperInterface cameraUiWrapper, BitmapHelper bitmapHelper)
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
    public void SetCameraUIWrapper(CameraWrapperInterface wrapper)
    {
        Logger.d(TAG, "SetCameraUiWrapper");
        cameraUiWrapper = wrapper;
        if (wrapper != null)
            wrapper.GetParameterHandler().AddParametersLoadedListner(this);
        if (wrapper.GetModuleHandler() != null)
            wrapper.GetModuleHandler().moduleEventHandler.AddWorkFinishedListner(newImageRecieved);
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
        i_activity = (I_Activity) getActivity();
        View view = inflater.inflate(layout.samplethemefragment, container, false);
        mPager = (PagingView)view.findViewById(id.viewPager_fragmentHolder);
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPager.setOffscreenPageLimit(2);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(1);
        return view;
    }


    private final I_ThumbClick onThumbClick = new I_ThumbClick() {
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

    private final I_ThumbClick onThumbBackClick = new I_ThumbClick() {
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
        if (screenSlideFragment != null)
            screenSlideFragment.LoadFiles();
        bitmapHelper.LoadFiles();
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
                    settingsMenuFragment = SettingsMenuFragment.GetInstance(i_activity, appSettingsManager);
                    settingsMenuFragment.SetCameraUIWrapper(cameraUiWrapper);
                }
                return settingsMenuFragment;
            }
            else if (position == 2)
            {
                if (screenSlideFragment == null) {
                    screenSlideFragment = new ScreenSlideFragment();
                    screenSlideFragment.setWaitForCameraHasLoaded();
                    screenSlideFragment.SetAppSettingsManagerAndBitmapHelper(appSettingsManager, bitmapHelper);
                    screenSlideFragment.SetOnThumbClick(onThumbBackClick);
                }
                return screenSlideFragment;
            }
            else
            {
                if (cameraUiFragment == null)
                    cameraUiFragment = CameraUiFragment.GetInstance(i_activity, onThumbClick, appSettingsManager, cameraUiWrapper, bitmapHelper);
                return cameraUiFragment;
            }
        }

        @Override
        public int getCount()
        {
            return 3;
        }

    }


    private final I_WorkEvent newImageRecieved = new I_WorkEvent() {
        @Override
        public void WorkHasFinished(final File filePath) {
            FreeDPool.Execute(new Runnable() {
                @Override
                public void run()
                {   int mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnails_size);
                    bitmapHelper.getBitmap(filePath, true, mImageThumbSize, mImageThumbSize);
                    if (screenSlideFragment != null)
                    {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                screenSlideFragment.addFile(filePath);
                            }
                        });

                    }
                    bitmapHelper.AddFile(new FileHolder(filePath, appSettingsManager.GetWriteExternal()));
                }
            });

        }
    };

}
