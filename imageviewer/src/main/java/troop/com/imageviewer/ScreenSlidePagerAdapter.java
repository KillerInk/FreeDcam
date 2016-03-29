package troop.com.imageviewer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.troop.filelogger.Logger;
import com.troop.freedcam.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import troop.com.imageviewer.gridviewfragments.GridViewFragment;
import troop.com.imageviewer.holder.FileHolder;

/**
 * Created by troopii on 20.03.2016.
 */
public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
{
    private List<FileHolder> files;
    private View.OnClickListener fragmentclickListner;
    private ViewPager mPager;
    private final String TAG = ScreenSlidePagerAdapter.class.getSimpleName();
    private String FilePathToLoad = "";
    private GridViewFragment.FormatTypes filestoshow = GridViewFragment.FormatTypes.all;
    SparseArray<ImageFragment> registeredFragments;

    public ScreenSlidePagerAdapter(FragmentManager fm, ViewPager mPager, View.OnClickListener fragmentclickListner, GridViewFragment.FormatTypes filestoshow)
    {
        super(fm);
        files = new ArrayList<>();
        registeredFragments = new SparseArray<ImageFragment>();
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
        files = holder;
        notifyDataSetChanged();
    }

    public List<FileHolder> getFiles()
    {
        return files;
    }

    public FileHolder getCurrentFile()
    {
        return files.get(mPager.getCurrentItem());
    }

    @Override
    public Fragment getItem(int position)
    {
        ImageFragment currentFragment = new ImageFragment();
        if (files == null || files.size() == 0)
            currentFragment.SetFilePath(null);
        else
            currentFragment.SetFilePath(files.get(position).getFile());
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
        FileHolder file = new FileHolder(((ImageFragment) object).GetFilePath());
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
        ImageFragment fragment = (ImageFragment) super.instantiateItem(container, position);
        fragment.SetOnclickLisnter(fragmentclickListner);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public ImageFragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    public void addFile(File file)
    {
        if (files == null)
            return;
        try {
            files.add(new FileHolder(file));
            Collections.sort(files, new Comparator<FileHolder>() {
                public int compare(FileHolder f1, FileHolder f2) {
                    return Long.valueOf(f2.getFile().lastModified()).compareTo(f1.getFile().lastModified());
                }
            });
            this.notifyDataSetChanged();
        }
        catch(IllegalStateException ex)
        {

        }
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
        List<FileHolder> images = new ArrayList<FileHolder>();
        File folder = new File(FilePathToLoad);
        if(folder.listFiles() == null || folder.listFiles().length ==0)
        {
            Logger.d(TAG, "readFiles failed, folder.listFiles empty");
            files = null;
            return;
        }
        FileHolder.readFilesFromFolder(folder, images, filestoshow);
        files = images;
        Logger.d(TAG, "readFiles sucess, FilesCount" + files.size());
        this.notifyDataSetChanged();
    }
}
