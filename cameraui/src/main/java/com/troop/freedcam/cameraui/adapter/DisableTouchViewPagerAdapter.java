package com.troop.freedcam.cameraui.adapter;


import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.troop.freedcam.cameraui.fragment.CameraSettingsFragment;
import com.troop.freedcam.cameraui.fragment.CameraUiFragment;
import com.troop.freedcam.gallery.views.GalleryFragment;



public class DisableTouchViewPagerAdapter extends FragmentStatePagerAdapter
{
    private CameraSettingsFragment settingsMenuFragment = new CameraSettingsFragment();
    private GalleryFragment screenSlideFragment = new GalleryFragment();
    private CameraUiFragment cameraUiFragment = new CameraUiFragment();

    public DisableTouchViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return settingsMenuFragment;
        }
        else if (position == 2) {
            return screenSlideFragment;
        }
        else {
            return cameraUiFragment;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public Parcelable saveState() {
        return null;
    }
}