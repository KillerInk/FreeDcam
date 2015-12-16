package troop.com.imageviewer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by troop on 18.09.2015.
 */
public class ScreenSlideFragment extends Fragment
{
    final static String TAG = ScreenSlideActivity.class.getSimpleName();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.screenslide_fragment, container, false);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) view.findViewById(R.id.pager);

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
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (FilePathToLoad.equals("")) {
            FilePathToLoad = StringUtils.GetInternalSDCARD() + StringUtils.freedcamFolder;
            readFiles();
        }
        else
        {
            readFiles();
        }
        mPagerAdapter = new ScreenSlidePagerAdapter(getActivity().getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        if (files != null && files.length > 0 && defitem == -1) {
            mPager.setCurrentItem(files.length);
        }
        else
            mPager.setCurrentItem(defitem);
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    private void readFiles()
    {
        List<FileHolder> images = new ArrayList<FileHolder>();
        File folder = new File(FilePathToLoad);
        FileUtils.readFilesFromFolder(folder, images, filestoshow);
        files = images.toArray(new FileHolder[images.size()]);

        Arrays.sort(files, new Comparator<FileHolder>() {
            public int compare(FileHolder f1, FileHolder f2) {
                return Long.valueOf(f1.getFile().lastModified()).compareTo(f2.getFile().lastModified());
            }
        });
    }

    public void Set_I_Activity(I_Activity activity)
    {
        this.activity = activity;
    }

    public void reloadFilesAndSetLastPos()
    {

        readFiles();
        if (files == null)
            return;
        mPagerAdapter = new ScreenSlidePagerAdapter(getActivity().getSupportFragmentManager());
        int current = mPager.getCurrentItem();
        mPager.setAdapter(mPagerAdapter);
        if (current-1 >= 0 && current-1 <= files.length)
            mPager.setCurrentItem(current -1);
        else
            mPager.setCurrentItem(files.length);
    }

    public void ReloadFilesAndSetLast()
    {
        readFiles();
        if (files == null)
            return;
        mPagerAdapter = new ScreenSlidePagerAdapter(getActivity().getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(files.length);
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            if (files == null || files.length == 0)
            {
                ImageFragment currentFragment = new ImageFragment();
                currentFragment.activity = ScreenSlideFragment.this;
                currentFragment.SetFilePath(null);
                return currentFragment;
            }
            else {
                currentFile = (files[mPager.getCurrentItem()].getFile());
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
        Log.d(TAG, "Loading Files...");
        File internalSDCIM = new File(StringUtils.GetInternalSDCARD() + StringUtils.DCIMFolder);
        List<File> folders = new ArrayList<>();
        List<File> images = new ArrayList<File>();
        //read internal Folders
        try {
            FileUtils.readSubFolderFromFolder(internalSDCIM, folders);
        }
        catch (Exception ex){}
        Log.d(TAG, "Found internal " + folders.size() + "Folders");
        //read external Folders
        File externalSDCIM = new File(StringUtils.GetExternalSDCARD() + StringUtils.DCIMFolder);
        try {
            FileUtils.readSubFolderFromFolder(externalSDCIM, folders);
        }
        catch (Exception ex){}
        Log.d(TAG, "Found external " + folders.size() + "Folders");
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
        Log.d(TAG, "Found " + images.size() + "Images");
        final File[] s = images.toArray(new File[images.size()]);

        Arrays.sort(s, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });
        return s;
    }*/


}
