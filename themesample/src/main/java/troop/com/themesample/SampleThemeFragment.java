package troop.com.themesample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.troop.filelogger.Logger;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AbstractFragment;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;

import java.io.File;

import troop.com.imageviewer.ImageFragment;
import troop.com.imageviewer.ScreenSlideFragment;
import troop.com.imageviewer.holder.FileHolder;
import troop.com.themesample.subfragments.CameraUiFragment;
import troop.com.themesample.subfragments.SettingsMenuFragment;

/**
 * Created by troop on 09.06.2015.
 */
public class SampleThemeFragment extends AbstractFragment
{
    final String TAG = SampleThemeFragment.class.getSimpleName();

    private I_Activity i_activity;
    private CameraUiFragment cameraUiFragment;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    private SettingsMenuFragment settingsMenuFragment;
    ScreenSlideFragment screenSlideFragment;

    public SampleThemeFragment()
    {
    }

    @Override
    public void SetStuff(I_Activity i_activity) {
        this.i_activity = i_activity;

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
            settingsMenuFragment.SetStuff(i_activity);
            settingsMenuFragment.SetCameraUIWrapper(wrapper);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, null);
        if (cameraUiFragment == null)
        {
            cameraUiFragment = new CameraUiFragment();
            cameraUiFragment.SetStuff(i_activity,onThumbClick);
        }
        if (settingsMenuFragment == null) {
            settingsMenuFragment = new SettingsMenuFragment();
            settingsMenuFragment.SetStuff(i_activity);
        }
        return inflater.inflate(R.layout.samplethemefragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mPager = (ViewPager)view.findViewById(R.id.viewPager_fragmentHolder);
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

    ScreenSlideFragment.I_ThumbClick onThumbClick = new ScreenSlideFragment.I_ThumbClick() {
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

    ScreenSlideFragment.I_ThumbClick onThumbBackClick = new ScreenSlideFragment.I_ThumbClick() {
        @Override
        public void onThumbClick() {
            mPager.setCurrentItem(1);
        }

        @Override
        public void newImageRecieved(File file) {

        }
    };



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
                settingsMenuFragment = new SettingsMenuFragment();
                settingsMenuFragment.SetStuff(i_activity);
                settingsMenuFragment.SetCameraUIWrapper(wrapper);
                return settingsMenuFragment;
            }
            else if (position == 2)
            {
                screenSlideFragment = new ScreenSlideFragment();
                screenSlideFragment.SetOnThumbClick(onThumbBackClick);
                return screenSlideFragment;
            }
            else
            {
                //cameraUiFragment = new CameraUiFragment();
                cameraUiFragment.SetStuff(i_activity,onThumbClick);
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
