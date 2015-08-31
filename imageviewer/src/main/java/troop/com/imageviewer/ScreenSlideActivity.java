package troop.com.imageviewer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.androiddng.RawToDng;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import troop.com.views.MyHistogram;

/**
 * Created by troop on 21.08.2015.
 */
public class ScreenSlideActivity extends FragmentActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 5;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    File[] files;
    Button closeButton;

    File currentFile;
    int flags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screenslide_activity);
        loadFilePaths();
        // Instantiate a ViewPager and a PagerAdapter.

            mPager = (ViewPager) findViewById(R.id.pager);
            mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(mPagerAdapter);
        if (files != null) {
            mPager.setCurrentItem(files.length);
        }
        else
            mPager.setCurrentItem(0);

        this.closeButton = (Button)findViewById(R.id.button_closeView);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        HIDENAVBAR();

    }






    public void reloadFilesAndSetLastPos()
    {

        loadFilePaths();
        if (files == null)
            return;
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        int current = mPager.getCurrentItem();
        mPager.setAdapter(mPagerAdapter);
        if (current-1 >= 0 && current-1 <= files.length)
            mPager.setCurrentItem(current -1);
        else
            mPager.setCurrentItem(files.length);
    }

    public void ReloadFilesAndSetLast()
    {
        loadFilePaths();
        if (files == null)
            return;
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(files.length);
    }


    public void HIDENAVBAR()
    {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else
        {
            //HIDE nav and action bar
            final View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(flags);
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if (visibility > 0) {
                        if (Build.VERSION.SDK_INT >= 16)
                            getWindow().getDecorView().setSystemUiVisibility(flags);
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            if (files == null)
            {
                ImageFragment currentFragment = new ImageFragment();
                currentFragment.activity = ScreenSlideActivity.this;
                currentFragment.SetFilePath(null);
                return currentFragment;
            }
            else {
                currentFile = (files[mPager.getCurrentItem()]);
                ImageFragment currentFragment = new ImageFragment();
                currentFragment.activity = ScreenSlideActivity.this;
                currentFragment.SetFilePath(files[position]);


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

    @Override
    protected void onResume() {
        super.onResume();
        HIDENAVBAR();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            HIDENAVBAR();
    }


    private void loadFilePaths()
    {
        File directory = new File(Environment.getExternalStorageDirectory() + "/DCIM/FreeCam/");
        files = directory.listFiles();
        List<File> jpegs = new ArrayList<File>();

        try {
            //read internal sd
            if(files != null || files.length > 0)
            {
                for (File f : files)
                {
                    if (!f.isDirectory() && (f.getAbsolutePath().endsWith(".jpg") || f.getAbsolutePath().endsWith(".mp4")|| f.getAbsolutePath().endsWith(".dng")|| f.getAbsolutePath().endsWith(".raw")))
                        jpegs.add(f);
                }
            }
            //read external sd
            directory = new File(StringUtils.GetExternalSDCARD() + "/DCIM/FreeCam/");
            files = directory.listFiles();
            for (File f : files)
            {
                if (!f.isDirectory() && (f.getAbsolutePath().endsWith(".jpg") || f.getAbsolutePath().endsWith(".mp4")|| f.getAbsolutePath().endsWith(".dng")|| f.getAbsolutePath().endsWith(".dng")))
                    jpegs.add(f);
            }
        }
        catch (Exception ex){}
        if (jpegs.size() > 0) {
            files = jpegs.toArray(new File[jpegs.size()]);
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                }
            });
        }
        else files = null;
    }
}
