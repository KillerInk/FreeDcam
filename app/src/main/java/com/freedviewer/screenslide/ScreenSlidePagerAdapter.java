package com.freedviewer.screenslide;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.freedcam.ui.AppSettingsManager;
import com.freedcam.utils.Logger;
import com.freedviewer.gridviewfragments.GridViewFragment;
import com.freedviewer.holder.FileHolder;

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
    private ScreenSlideFragment.FragmentClickClistner fragmentclickListner;
    private ViewPager mPager;
    private final String TAG = ScreenSlidePagerAdapter.class.getSimpleName();
    private String FilePathToLoad = "";
    private GridViewFragment.FormatTypes filestoshow = GridViewFragment.FormatTypes.all;

    public ScreenSlidePagerAdapter(FragmentManager fm, ViewPager mPager, ScreenSlideFragment.FragmentClickClistner fragmentclickListner, GridViewFragment.FormatTypes filestoshow)
    {
        super(fm);
        files = new ArrayList<>();
        this.mPager = mPager;
        this.fragmentclickListner = fragmentclickListner;
        this.filestoshow = filestoshow;
    }

    public void SetFileToLoadPath(String Filetoload)
    {
        this.FilePathToLoad = Filetoload;
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

    @Override
    public Fragment getItem(int position)
    {
        ImageFragment  currentFragment = new ImageFragment();
        if (files == null || files.size() == 0)
            currentFragment.SetFilePath(null);
        else
            currentFragment.SetFilePath(files.get(position));
        currentFragment.SetOnclickLisnter(fragmentclickListner);
        currentFragment.setTag(position);
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

    public void addFile(File file)
    {
        if (files == null)
            return;
        mPager.setAdapter(null);
        Logger.d(TAG, "addfile:" +file.getName() + " currentCount:"+files.size());
        files.add(new FileHolder(file, files.get(0).isExternalSD()));
        Collections.sort(files, new Comparator<FileHolder>() {
            public int compare(FileHolder f1, FileHolder f2) {
                return Long.valueOf(f2.getFile().lastModified()).compareTo(f1.getFile().lastModified());
            }
        });
        Logger.d(TAG, "currentCount:"+files.size());
        this.notifyDataSetChanged();
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
        FileHolder.readFilesFromFolder(folder, images, filestoshow,AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal());
        files = images;
        Logger.d(TAG, "readFiles sucess, FilesCount" + files.size());
        this.notifyDataSetChanged();
        mPager.setCurrentItem(0);
    }
}
