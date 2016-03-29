package troop.com.imageviewer;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.provider.DocumentFile;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.troop.androiddng.RawToDng;
import com.troop.filelogger.Logger;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.I_swipe;
import com.troop.freedcam.ui.SwipeMenuListner;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import troop.com.imageviewer.gridimageviews.GridImageView;
import troop.com.imageviewer.gridviewfragments.GridViewFragment;
import troop.com.imageviewer.holder.FileHolder;
import troop.com.views.MyHistogram;

/**
 * Created by troop on 18.09.2015.
 */
public class ScreenSlideFragment extends Fragment implements ViewPager.OnPageChangeListener
{
    final static String TAG = ScreenSlideFragment.class.getSimpleName();
    final public static String SAVESTATE_FILEPATH = "savestae_filepath";
    final public static String SAVESTATE_ITEMINT = "savestate_itemint";
    int mImageThumbSize = 0;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private ScreenSlidePagerAdapter mPagerAdapter;


    private Button closeButton;

    public int defitem = -1;
    public String FilePathToLoad = "";
    public GridViewFragment.FormatTypes filestoshow = GridViewFragment.FormatTypes.all;
    private I_ThumbClick thumbclick;
    private RelativeLayout topbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.screenslide_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        Logger.d(TAG, "onViewCreated");
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) view.findViewById(R.id.pager);
        topbar =(RelativeLayout)view.findViewById(R.id.top_bar);

        this.closeButton = (Button)view.findViewById(R.id.button_closeView);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thumbclick != null) {
                    thumbclick.onThumbClick();
                    mPager.setCurrentItem(0);
                } else
                    getActivity().finish();
            }
        });

        if(savedInstanceState != null)
        {
            FilePathToLoad = (String) savedInstanceState.get(SAVESTATE_FILEPATH);
            defitem = (int)savedInstanceState.get(SAVESTATE_ITEMINT);
            Logger.d(TAG, "have file to load from saveinstance onCreated" + FilePathToLoad);

        }


    }

    @Override
    public void onResume()
    {
        Logger.d(TAG,"onResume");
        super.onResume();
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager(),mPager,fragmentclickListner,filestoshow);
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(this);

        if (FilePathToLoad.equals("")) {
            mPagerAdapter.SetFiles(FileHolder.getDCIMFiles());
        }
        else
        {
            mPagerAdapter.SetFileToLoadPath(FilePathToLoad);
        }

        if(mPagerAdapter.getFiles() != null ) {
            if (mPagerAdapter.getFiles().size() > 0 && defitem == -1) {
                mPager.setCurrentItem(0);
            } else
                mPager.setCurrentItem(defitem);
        try {
            try {
            }
            catch (ArrayIndexOutOfBoundsException esx)
            {
                esx.printStackTrace();
            }
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SAVESTATE_FILEPATH, FilePathToLoad);
        outState.putInt(SAVESTATE_ITEMINT, mPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    public void SetOnThumbClick(I_ThumbClick thumbClick)
    {
        this.thumbclick = thumbClick;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        final ImageFragment imgFragment = mPagerAdapter.getRegisteredFragment(position);
        if (imgFragment == null)
            return;
        if (topbar.getVisibility() == View.VISIBLE)
            imgFragment.SetVisibility(true);
        else
            imgFragment.SetVisibility(false);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public interface I_ThumbClick
    {
        void onThumbClick();
        void newImageRecieved(File file);
    }

    private View.OnClickListener fragmentclickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            {
                ImageFragment imageFragment = mPagerAdapter.getRegisteredFragment(mPager.getCurrentItem());
                if (topbar.getVisibility() == View.GONE) {
                    topbar.setVisibility(View.VISIBLE);
                    if (imageFragment != null)
                        imageFragment.SetVisibility(true);
                }
                else {
                    topbar.setVisibility(View.GONE);
                    if (imageFragment != null)
                        imageFragment.SetVisibility(false);
                }
            }
        }
    };


    public void addFile(File file)
    {
        if (mPagerAdapter != null)
            mPagerAdapter.addFile(file);
    }

    public void reloadFilesAndSetLastPos()
    {
        if (FilePathToLoad.equals("")) {
            mPagerAdapter.SetFiles(FileHolder.getDCIMFiles());
        }
        else
        {
            mPagerAdapter.SetFileToLoadPath(FilePathToLoad);
        }
    }

}
