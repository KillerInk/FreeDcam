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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.utils.StringUtils;

import java.io.File;
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
    Button play;
    Button closeButton;
    Button deleteButton;

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
        mPager.setCurrentItem(files.length);

        this.closeButton = (Button)findViewById(R.id.button_closeView);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        this.deleteButton = (Button)findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(ScreenSlideActivity.this);
                builder.setMessage("Delete File?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });

        this.play = (Button)findViewById(R.id.button_play);
        play.setVisibility(View.GONE);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFile == null)
                    return;
                Uri uri = Uri.fromFile(currentFile);
                Intent i = new Intent(Intent.ACTION_VIEW);
                if (currentFile.getAbsolutePath().endsWith("mp4"))
                    i.setDataAndType(uri, "video/*");
                else
                    i.setDataAndType(uri, "image/*");
                startActivity(i);
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

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    int current = mPager.getCurrentItem();
                    boolean d = currentFile.delete();
                    loadFilePaths();
                    mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
                    mPager.setAdapter(mPagerAdapter);
                    if (current-1 >= 0 && current-1 <= files.length)
                        mPager.setCurrentItem(current -1);
                    else
                        mPager.setCurrentItem(files.length);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };


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
            //final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            //imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);

        }
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem());
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            currentFile = (files[mPager.getCurrentItem()]);
            if (currentFile.getAbsolutePath().endsWith(".jpg"))
            {
                play.setVisibility(View.GONE);
            }
            if (currentFile.getAbsolutePath().endsWith(".mp4"))
            {
                play.setText("Play");
                play.setVisibility(View.VISIBLE);
            }
            if (currentFile.getAbsolutePath().endsWith(".dng"))
            {
                play.setText("Open DNG");
                play.setVisibility(View.VISIBLE);
            }
            ImageFragment currentFragment = new ImageFragment();
            currentFragment.SetFilePath(files[position]);


            return currentFragment;
        }

        @Override
        public int getCount() {
            return files.length;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        HIDENAVBAR();
    }

    private void loadFilePaths()
    {
        File directory = new File(Environment.getExternalStorageDirectory() + "/DCIM/FreeCam/");
        files = directory.listFiles();
        List<File> jpegs = new ArrayList<File>();

        try {

            if(files != null || files.length > 0)
            {
                for (File f : files)
                {
                    if (!f.isDirectory() && (f.getAbsolutePath().endsWith(".jpg") || f.getAbsolutePath().endsWith(".mp4")|| f.getAbsolutePath().endsWith(".dng")))
                        jpegs.add(f);
                }
            }
            directory = new File(StringUtils.GetExternalSDCARD() + "/DCIM/FreeCam/");
            files = directory.listFiles();
            for (File f : files)
            {
                if (!f.isDirectory() && (f.getAbsolutePath().endsWith(".jpg") || f.getAbsolutePath().endsWith(".mp4")|| f.getAbsolutePath().endsWith(".dng")))
                    jpegs.add(f);
            }
        }
        catch (Exception ex){}
        files = jpegs.toArray(new File[jpegs.size()]);
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });
    }
}
