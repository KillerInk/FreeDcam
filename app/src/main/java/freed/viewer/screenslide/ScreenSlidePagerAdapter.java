package freed.viewer.screenslide;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import freed.viewer.holder.FileHolder;

/**
 * Created by KillerInk on 03.12.2017.
 */

class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
{
    private final String TAG = ScreenSlidePagerAdapter.class.getSimpleName();
    private final SparseArray<Fragment> registeredFragments;
    private List<FileHolder> files =  new ArrayList<>();
    private ViewPager mPager;
    private ScreenSlideFragment.FragmentClickClistner onClickListener;

    public ScreenSlidePagerAdapter(FragmentManager fm, ViewPager mPager, ScreenSlideFragment.FragmentClickClistner onClickListener)
    {
        super(fm);
        this.mPager = mPager;
        this.onClickListener = onClickListener;
        registeredFragments = new SparseArray<>();
    }

    public FileHolder getCurrentFile()
    {
        if (files != null && files.size()>0)
            return files.get(mPager.getCurrentItem());
        else
            return null;
    }

    public void setFiles(List<FileHolder> files)
    {
        this.files =files;
        notifyDataSetChanged();
    }


    //FragmentStatePagerAdapter implementation START

    @Override
    public Fragment getItem(int position)
    {
        ImageFragment  currentFragment = new ImageFragment();
        if (files == null || files.size() == 0)
            currentFragment.SetFilePath(null);
        else
            currentFragment.SetFilePath(files.get(position));
        currentFragment.SetOnclickLisnter(onClickListener);

        return currentFragment;
    }

    @Override
    public int getCount()
    {
        if(files != null && files.size() > 0)
            return files.size();
        else return 1;
    }

    @Override
    public int getItemPosition(Object object)
    {
        ImageFragment imageFragment = (ImageFragment) object;
        FileHolder file = imageFragment.GetFilePath();
        int position = files.indexOf(file);
        // The current data matches the data in this active fragment, so let it be as it is.
        if (position == imageFragment.getPosition){
            return PagerAdapter.POSITION_UNCHANGED;
        } else {
            // Returning POSITION_NONE means the current data does not matches the data this fragment is showing right now.  Returning POSITION_NONE constant will force the fragment to redraw its view layout all over again and show new data.
            return PagerAdapter.POSITION_NONE;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageFragment fragment = (ImageFragment) super.instantiateItem(container, position);
        fragment.getPosition = position;
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
