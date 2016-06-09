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

package com.freedviewer.screenslide;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;
import com.freedviewer.gridview.GridViewFragment.FormatTypes;
import com.freedviewer.helper.BitmapHelper;
import com.freedviewer.holder.FileHolder;
import com.freedviewer.screenslide.ScreenSlideFragment.FragmentClickClistner;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by troopii on 20.03.2016.
 */
class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
{
    private List<FileHolder> files;
    private FragmentClickClistner fragmentclickListner;
    private ViewPager mPager;
    private final String TAG = ScreenSlidePagerAdapter.class.getSimpleName();
    private String FilePathToLoad = "";
    private FormatTypes filestoshow = FormatTypes.all;
    private AppSettingsManager appSettingsManager;
    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    private BitmapHelper bitmapHelper;

    public ScreenSlidePagerAdapter(FragmentManager fm, ViewPager mPager, FragmentClickClistner fragmentclickListner, FormatTypes filestoshow,AppSettingsManager appSettingsManager, BitmapHelper bitmapHelper)
    {
        super(fm);
        files = new ArrayList<>();
        this.mPager = mPager;
        this.fragmentclickListner = fragmentclickListner;
        this.filestoshow = filestoshow;
        this.appSettingsManager = appSettingsManager;
        this.bitmapHelper =bitmapHelper;
    }

    public void SetFileToLoadPath(String Filetoload)
    {
        FilePathToLoad = Filetoload;
        readFiles();
    }

    public void SetFiles(List<FileHolder> holder)
    {
        try {
            files = holder;
            notifyDataSetChanged();
            mPager.setCurrentItem(0);
        }
        catch (IllegalStateException ex)
        {
            Logger.exception(ex);
        }

    }

    public List<FileHolder> getFiles()
    {
        return files;
    }

    public FileHolder getCurrentFile()
    {
        if (files != null && files.size()>0)
            return files.get(mPager.getCurrentItem());
        else
            return null;
    }

    public void addFile(File file)
    {
        if (files == null)
            return;
        mPager.setAdapter(null);
        Logger.d(TAG, "addfile:" +file.getName() + " currentCount:"+files.size());
        if (files.size() >0)
            files.add(new FileHolder(file, files.get(0).isExternalSD()));
        else
            files.add(new FileHolder(file, false));
        Collections.sort(files, new Comparator<FileHolder>() {
            public int compare(FileHolder f1, FileHolder f2) {
                return Long.valueOf(f2.getFile().lastModified()).compareTo(f1.getFile().lastModified());
            }
        });
        Logger.d(TAG, "currentCount:"+files.size());
        notifyDataSetChanged();
        mPager.setAdapter(this);
        mPager.setCurrentItem(0);
    }

    public void reloadFilesAndSetLastPos() {
        Logger.d(TAG, "reloadFilesAndSetLastPos");
        readFiles();
        if (files == null)
            return;
        int current = mPager.getCurrentItem();

        if (current-1 >= 0 && current-1 <= files.size())
            mPager.setCurrentItem(current -1);
        else
            mPager.setCurrentItem(0);
    }

    private void readFiles()
    {
        List<FileHolder> images = new ArrayList<>();
        File folder = new File(FilePathToLoad);
        if(folder.listFiles() == null || folder.listFiles().length ==0)
        {
            Logger.d(TAG, "readFiles failed, folder.listFiles empty");
            files = null;
            return;
        }
        FileHolder.readFilesFromFolder(folder, images, filestoshow,appSettingsManager.GetWriteExternal());
        files = images;
        Logger.d(TAG, "readFiles sucess, FilesCount" + files.size());
        notifyDataSetChanged();
        mPager.setCurrentItem(0);
    }



    //FragmentStatePagerAdapter implementation START

    @Override
    public Fragment getItem(int position)
    {
        ImageFragment  currentFragment = new ImageFragment();
        currentFragment.SetBitmapHelper(bitmapHelper);
        if (files == null || files.size() == 0)
            currentFragment.SetFilePath(null);
        else
            currentFragment.SetFilePath(files.get(position));
        currentFragment.SetOnclickLisnter(fragmentclickListner);

        return currentFragment;
    }

    @Override
    public int getCount()
    {
        if(files != null)
            return files.size();
        else return 1;
    }

    @Override
    public int getItemPosition(Object object) {
        FileHolder file = ((ImageFragment) object).GetFilePath();
        int position = files.indexOf(file);
        if (position >= 0) {
            // The current data matches the data in this active fragment, so let it be as it is.
            return position;
        } else {
            // Returning POSITION_NONE means the current data does not matches the data this fragment is showing right now.  Returning POSITION_NONE constant will force the fragment to redraw its view layout all over again and show new data.
            return POSITION_NONE;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
