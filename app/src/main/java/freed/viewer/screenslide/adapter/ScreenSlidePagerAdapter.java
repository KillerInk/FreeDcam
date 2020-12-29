package freed.viewer.screenslide.adapter;


import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import freed.file.holder.BaseHolder;
import freed.viewer.screenslide.models.ImageFragmentModel;
import freed.viewer.screenslide.views.ImageFragment;
import freed.viewer.screenslide.views.ScreenSlideFragment;

/**
 * Created by KillerInk on 03.12.2017.
 */

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
{
    private final String TAG = ScreenSlidePagerAdapter.class.getSimpleName();
    private final SparseArray<Fragment> registeredFragments;
    private List<ImageFragmentModel> imageFragmentModels;
    private ViewPager mPager;
    private ScreenSlideFragment.FragmentClickClistner onClickListener;

    public ScreenSlidePagerAdapter(FragmentManager fm, ViewPager mPager, ScreenSlideFragment.FragmentClickClistner onClickListener)
    {
        super(fm);
        this.mPager = mPager;
        this.onClickListener = onClickListener;
        registeredFragments = new SparseArray<>();
    }

    public BaseHolder getCurrentFile()
    {
        if (imageFragmentModels != null && imageFragmentModels.size()>0)
            return imageFragmentModels.get(mPager.getCurrentItem()).getBaseHolder();
        else
            return null;
    }

    public ImageFragmentModel getCurrentImageFragmentModel()
    {
        if (imageFragmentModels != null && imageFragmentModels.size()>0)
            return imageFragmentModels.get(mPager.getCurrentItem());
        else
            return null;
    }

    public void setImageFragmentModels(List<ImageFragmentModel> imageFragmentModels)
    {
        mPager.post(new Runnable() {
            @Override
            public void run() {
                ScreenSlidePagerAdapter.this.imageFragmentModels = imageFragmentModels;
                notifyDataSetChanged();
            }
        });

    }


    //FragmentStatePagerAdapter implementation START

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        ImageFragment currentFragment = new ImageFragment();
        if (imageFragmentModels != null && imageFragmentModels.size() > 0 && position < imageFragmentModels.size())
            currentFragment.setImageFragmentModel(imageFragmentModels.get(position));
        currentFragment.SetOnclickLisnter(onClickListener);

        return currentFragment;
    }

    @Override
    public int getCount()
    {
        if(imageFragmentModels != null && imageFragmentModels.size() > 0)
            return imageFragmentModels.size();
        else return 1;
    }

    @Override
    public int getItemPosition(Object object)
    {
        ImageFragment imageFragment = (ImageFragment) object;
        ImageFragmentModel file = imageFragment.getImageFragmentModel();
        int position = imageFragmentModels.indexOf(file);
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
