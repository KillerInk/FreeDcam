package troop.com.imageviewer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.troop.filelogger.Logger;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.I_swipe;
import com.troop.freedcam.ui.SwipeMenuListner;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import troop.com.imageviewer.gridviewfragments.GridViewFragment;
import troop.com.imageviewer.holder.FileHolder;

/**
 * Created by troop on 18.09.2015.
 */
public class ScreenSlideFragment extends Fragment implements I_swipe
{
    final static String TAG = ScreenSlideFragment.class.getSimpleName();
    final public static String SAVESTATE_FILEPATH = "savestae_filepath";
    final public static String SAVESTATE_ITEMINT = "savestate_itemint";

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    FileHolder[] files;
    Button closeButton;

    File currentFile;
    int flags;
    View view;
    I_Activity activity;
    public int defitem = -1;
    public String FilePathToLoad = "";
    public GridViewFragment.FormatTypes filestoshow = GridViewFragment.FormatTypes.all;
    SwipeMenuListner touchHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.screenslide_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        touchHandler = new SwipeMenuListner(this);
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchHandler.onTouchEvent(event);
                return false;
            }
        });

        this.closeButton = (Button)view.findViewById(R.id.button_closeView);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity != null)
                    activity.loadCameraUiFragment();
                else
                    getActivity().finish();
            }
        });
        if(savedInstanceState != null){
            FilePathToLoad = (String) savedInstanceState.get(SAVESTATE_FILEPATH);
            defitem = (int)savedInstanceState.get(SAVESTATE_ITEMINT);
            Logger.d(TAG, "have file to load from saveinstance onCreated" + FilePathToLoad);
        }
        if (FilePathToLoad.equals("")) {
            FilePathToLoad = StringUtils.GetInternalSDCARD() + StringUtils.freedcamFolder;
            readFiles();
        }
        else
        {
            readFiles();
        }
        Logger.d(TAG, "onResume" + FilePathToLoad);
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager(),files);
        mPager.setAdapter(mPagerAdapter);
        if (files != null && files.length > 0 && defitem == -1) {
            mPager.setCurrentItem(0);
        }
        else
            mPager.setCurrentItem(defitem);

    }

    @Override
    public void onResume()
    {
        super.onResume();


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SAVESTATE_FILEPATH, FilePathToLoad);
        outState.putInt(SAVESTATE_ITEMINT, mPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    private void readFiles()
    {
        List<FileHolder> images = new ArrayList<FileHolder>();
        File folder = new File(FilePathToLoad);
        if(folder.listFiles() == null || folder.listFiles().length ==0)
        {
            Logger.d(TAG, "readFiles failed, folder.listFiles empty");
            files = null;
            return;
        }
        FileUtils.readFilesFromFolder(folder, images, filestoshow);
        files = images.toArray(new FileHolder[images.size()]);
        Logger.d(TAG, "readFiles sucess, FilesCount" + files.length);
        Arrays.sort(files, new Comparator<FileHolder>() {
            public int compare(FileHolder f1, FileHolder f2) {
                return Long.valueOf(f2.getFile().lastModified()).compareTo(f1.getFile().lastModified());
            }
        });
    }

    public void Set_I_Activity(I_Activity activity)
    {
        this.activity = activity;
    }

    public void reloadFilesAndSetLastPos()
    {
        Logger.d(TAG, "reloadFilesAndSetLastPos");
        readFiles();
        if (files == null)
            return;
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager(),files);
        int current = mPager.getCurrentItem();
        mPager.setAdapter(mPagerAdapter);
        if (current-1 >= 0 && current-1 <= files.length)
            mPager.setCurrentItem(current -1);
        else
            mPager.setCurrentItem(0);
    }

    public void ReloadFilesAndSetLast()
    {
        readFiles();
        if (files == null)
            return;
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager(),files);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(0);
        Logger.d(TAG, "reloadFilesAndSetLast");
    }

    @Override
    public void doLeftToRightSwipe()
    {
        Logger.d(TAG, "left to right");
        if (activity != null && mPager.getCurrentItem() == 0)
            activity.loadCameraUiFragment();
    }

    @Override
    public void doRightToLeftSwipe() {
        Logger.d(TAG, "right to left");
    }

    @Override
    public void doTopToBottomSwipe() {

    }

    @Override
    public void doBottomToTopSwipe() {

    }

    @Override
    public void onClick(int x, int y) {

    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
    {
        FileHolder[] f;
        public ScreenSlidePagerAdapter(FragmentManager fm,FileHolder[] f)
        {
            super(fm);

            this.f = f;
            if (f != null)
                Logger.d(TAG, "loading screenslidePageAdapter: file count:" + f.length);
            else
                Logger.d(TAG, "loading screenslidePageAdapter: No Files found");

        }

        @Override
        public Fragment getItem(int position)
        {

            if (f == null || f.length == 0)
            {
                ImageFragment currentFragment = new ImageFragment();
                currentFragment.activity = ScreenSlideFragment.this;
                currentFragment.SetFilePath(null);
                return currentFragment;
            }
            else {
                currentFile = (f[mPager.getCurrentItem()].getFile());
                ImageFragment currentFragment = new ImageFragment();
                currentFragment.activity = ScreenSlideFragment.this;
                currentFragment.SetFilePath(files[position].getFile());


                return currentFragment;
            }
        }

        @Override
        public int getCount()
        {
            if(files != null)
                return files.length;
            else return 1;
        }


    }

/*    public static File[] loadFilePaths()
    {
        Logger.d(TAG, "Loading Files...");
        File internalSDCIM = new File(StringUtils.GetInternalSDCARD() + StringUtils.DCIMFolder);
        List<File> folders = new ArrayList<>();
        List<File> images = new ArrayList<File>();
        //read internal Folders
        try {
            FileUtils.readSubFolderFromFolder(internalSDCIM, folders);
        }
        catch (Exception ex){}
        Logger.d(TAG, "Found internal " + folders.size() + "Folders");
        //read external Folders
        File externalSDCIM = new File(StringUtils.GetExternalSDCARD() + StringUtils.DCIMFolder);
        try {
            FileUtils.readSubFolderFromFolder(externalSDCIM, folders);
        }
        catch (Exception ex){}
        Logger.d(TAG, "Found external " + folders.size() + "Folders");
        //Lookup files in folders
        try
        {
            for (File folder : folders)
            {
                if (folder.isDirectory())
                {
                    FileUtils.readFilesFromFolder(folder, images);
                }
            }
        }
        catch (Exception ex){}
        Logger.d(TAG, "Found " + images.size() + "Images");
        final File[] s = images.toArray(new File[images.size()]);

        Arrays.sort(s, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });
        return s;
    }*/


}
