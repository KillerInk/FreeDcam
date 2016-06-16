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

package freed.cam.ui.themesample;

import android.graphics.Bitmap;
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

import com.troop.freedcam.R;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.interfaces.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.I_WorkEvent;
import freed.cam.apis.basecamera.parameters.I_ParametersLoaded;
import freed.cam.ui.themesample.cameraui.CameraUiFragment;
import freed.cam.ui.themesample.settings.SettingsMenuFragment;
import freed.utils.AppSettingsManager;
import freed.utils.FreeDPool;
import freed.utils.Logger;
import freed.viewer.helper.BitmapHelper;
import freed.viewer.holder.FileHolder;
import freed.viewer.screenslide.ScreenSlideFragment;
import freed.viewer.screenslide.ScreenSlideFragment.I_ThumbClick;

/**
 * Created by troop on 09.06.2015.
 */
public class SampleThemeFragment extends AbstractFragment implements I_ParametersLoaded
{
    final String TAG = SampleThemeFragment.class.getSimpleName();

    private ActivityInterface fragment_activityInterface;

    private PagingView mPager;
    private PagerAdapter mPagerAdapter;
    private CameraUiFragment cameraUiFragment;
    private SettingsMenuFragment settingsMenuFragment;
    private ScreenSlideFragment screenSlideFragment;
    private final boolean pagerTouchAllowed = true;

    public SampleThemeFragment()
    {
    }

    public static SampleThemeFragment GetInstance(AppSettingsManager appSettingsManager, CameraWrapperInterface cameraUiWrapper, BitmapHelper bitmapHelper)
    {
        SampleThemeFragment sampleThemeFragment = new SampleThemeFragment();
        sampleThemeFragment.cameraUiWrapper = cameraUiWrapper;
        return sampleThemeFragment;
    }


    @Override
    public void SetCameraUIWrapper(CameraWrapperInterface wrapper)
    {
        Logger.d(TAG, "SetCameraUiWrapper");
        cameraUiWrapper = wrapper;
        if (wrapper != null)
            wrapper.GetParameterHandler().AddParametersLoadedListner(this);
        if (wrapper.GetModuleHandler() != null)
            wrapper.GetModuleHandler().AddWorkFinishedListner(newImageRecieved);
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
        fragment_activityInterface = (ActivityInterface) getActivity();
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
        public void onThumbClick(int position) {
            mPager.setCurrentItem(2);
        }


    };

    private final I_ThumbClick onThumbBackClick = new I_ThumbClick() {
        @Override
        public void onThumbClick(int position)
        {
            if (mPager != null)
                mPager.setCurrentItem(1);
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
                    settingsMenuFragment = new SettingsMenuFragment();
                    settingsMenuFragment.SetCameraUIWrapper(cameraUiWrapper);
                }
                return settingsMenuFragment;
            }
            else if (position == 2)
            {
                if (screenSlideFragment == null) {
                    screenSlideFragment = new ScreenSlideFragment();
                    screenSlideFragment.setWaitForCameraHasLoaded();
                    screenSlideFragment.SetOnThumbClick(onThumbBackClick);
                }

                return screenSlideFragment;
            }
            else
            {
                if (cameraUiFragment == null)
                    cameraUiFragment = CameraUiFragment.GetInstance(onThumbClick, cameraUiWrapper);
                return cameraUiFragment;
            }
        }

        @Override
        public int getCount()
        {
            return 3;
        }

    }


    private final I_WorkEvent newImageRecieved = new I_WorkEvent()
    {
        @Override
        public void WorkHasFinished(final FileHolder fileHolder)
        {
            Logger.d(TAG, "newImageRecieved:" + fileHolder.getFile().getAbsolutePath());
            FreeDPool.Execute(new Runnable() {
                @Override
                public void run()
                {
                    int mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnails_size);
                    final Bitmap b = fragment_activityInterface.getBitmapHelper().getBitmap(fileHolder.getFile(), true, mImageThumbSize, mImageThumbSize);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            fragment_activityInterface.AddFile(fileHolder);
                            if (screenSlideFragment != null)
                                screenSlideFragment.NotifyDATAhasChanged();
                            if (cameraUiFragment != null)
                                cameraUiFragment.SetThumbImage(b);
                        }
                    });
                }
            });

        }
    };

}
