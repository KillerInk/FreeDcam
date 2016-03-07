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
public class ScreenSlideFragment extends Fragment
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

    private FileHolder[] files;
    private Button closeButton;

    private File currentFile;
    public int defitem = -1;
    public String FilePathToLoad = "";
    public GridViewFragment.FormatTypes filestoshow = GridViewFragment.FormatTypes.all;
    private I_ThumbClick thumbclick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.screenslide_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) view.findViewById(R.id.pager);

        this.closeButton = (Button)view.findViewById(R.id.button_closeView);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thumbclick != null)
                    thumbclick.onThumbClick();
                else
                    getActivity().finish();
            }
        });
        /*if(savedInstanceState != null)
        {
            FilePathToLoad = (String) savedInstanceState.get(SAVESTATE_FILEPATH);
            defitem = (int)savedInstanceState.get(SAVESTATE_ITEMINT);
            Logger.d(TAG, "have file to load from saveinstance onCreated" + FilePathToLoad);
        }*/
        if (FilePathToLoad.equals("")) {
            FilePathToLoad = StringUtils.GetInternalSDCARD() + StringUtils.freedcamFolder;
            readFiles();
        }
        else
        {
            readFiles();
        }
        Logger.d(TAG, "onViewCreate" + FilePathToLoad);
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
        /*outState.putString(SAVESTATE_FILEPATH, FilePathToLoad);
        outState.putInt(SAVESTATE_ITEMINT, mPager.getCurrentItem());*/
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

    public void SetOnThumbClick(I_ThumbClick thumbClick)
    {
        this.thumbclick = thumbClick;
    }

    public interface I_ThumbClick
    {
        void onThumbClick();
        void newImageRecieved();
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

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
