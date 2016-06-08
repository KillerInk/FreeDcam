/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedviewer.screenslide;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.provider.DocumentFile;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.freedcam.ui.I_Activity;
import com.freedcam.ui.I_Activity.I_OnActivityResultCallback;
import com.freedcam.ui.handler.MediaScannerManager;
import com.freedcam.ui.views.MyHistogram;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.FileUtils;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;
import com.freedcam.utils.StringUtils.FileEnding;
import com.freedviewer.gridview.GridViewFragment;
import com.freedviewer.gridview.GridViewFragment.FormatTypes;
import com.freedviewer.helper.BitmapHelper;
import com.freedviewer.holder.FileHolder;
import com.freedviewer.screenslide.ImageFragment.I_WaitForWorkFinish;
import com.troop.freedcam.R;
import com.troop.freedcam.R.dimen;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import java.io.File;
import java.io.IOException;



/**
 * Created by troop on 18.09.2015.
 */
public class ScreenSlideFragment extends Fragment implements OnPageChangeListener, I_OnActivityResultCallback, I_WaitForWorkFinish
{

    public static final String TAG = ScreenSlideFragment.class.getSimpleName();
    public interface I_ThumbClick
    {
        void onThumbClick();
        void newImageRecieved(File file);
    }

    public interface FragmentClickClistner
    {
        void onClick(Fragment fragment);
    }

    private int mImageThumbSize = 0;
    private AppSettingsManager appSettingsManager;

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

    private TextView iso;
    private TextView shutter;
    private TextView focal;
    private TextView fnumber;
    private TextView filename;
    private LinearLayout exifinfo;
    private Button deleteButton;
    private Button play;
    private LinearLayout bottombar;
    private MyHistogram histogram;

    public int defitem = -1;
    public String FilePathToLoad = "";
    public FormatTypes filestoshow = FormatTypes.all;
    private I_ThumbClick thumbclick;
    private RelativeLayout topbar;
    //hold the showed file
    private FileHolder file;
    private BitmapHelper bitmapHelper;

    private boolean waitForCameraHasLoaded = false;

    public void SetAppSettingsManagerAndBitmapHelper(AppSettingsManager appSettingsManager, BitmapHelper helper)
    {
        this.appSettingsManager = appSettingsManager;
        bitmapHelper = helper;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(layout.screenslide_fragment, container, false);
        mImageThumbSize = getResources().getDimensionPixelSize(dimen.image_thumbnail_size);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) view.findViewById(id.pager);
        topbar =(RelativeLayout)view.findViewById(id.top_bar);
        histogram = (MyHistogram)view.findViewById(id.screenslide_histogram);

        closeButton = (Button)view.findViewById(id.button_closeView);
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


        bottombar =(LinearLayout)view.findViewById(id.bottom_bar);

        exifinfo = (LinearLayout)view.findViewById(id.exif_info);
        exifinfo.setVisibility(View.GONE);
        iso = (TextView)view.findViewById(id.textView_iso);
        iso.setText("");
        shutter = (TextView)view.findViewById(id.textView_shutter);
        shutter.setText("");
        focal = (TextView)view.findViewById(id.textView_focal);
        focal.setText("");
        fnumber = (TextView)view.findViewById(id.textView_fnumber);
        fnumber.setText("");
        filename = (TextView)view.findViewById(id.textView_filename);

        play = (Button)view.findViewById(id.button_play);
        play.setVisibility(View.GONE);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file == null)
                    return;
                if (!file.getFile().getName().endsWith(FileEnding.RAW) || !file.getFile().getName().endsWith(FileEnding.BAYER)) {
                    Uri uri = Uri.fromFile(file.getFile());

                    Intent i = new Intent(Intent.ACTION_EDIT);
                    if (file.getFile().getName().endsWith(FileEnding.MP4))
                        i.setDataAndType(uri, "video/*");
                    else
                        i.setDataAndType(uri, "image/*");
                    Intent chooser = Intent.createChooser(i, "Choose App");
                    //startActivity(i);
                    if (i.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(chooser);
                    }

                }
            }
        });
        deleteButton = (Button)view.findViewById(id.button_delete);
        deleteButton.setVisibility(View.GONE);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (VERSION.SDK_INT <= VERSION_CODES.LOLLIPOP || VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && !appSettingsManager.GetWriteExternal()) {
                    Builder builder = new Builder(getContext());
                    builder.setMessage("Delete File?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                } else {
                    DocumentFile sdDir = FileUtils.getExternalSdDocumentFile(appSettingsManager,getContext());
                    if (sdDir == null) {
                        I_Activity i_activity = (I_Activity) getActivity();
                        i_activity.ChooseSDCard(ScreenSlideFragment.this);
                    } else {
                        Builder builder = new Builder(getContext());
                        builder.setMessage("Delete File?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }
                }


            }
        });
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager(),mPager,fragmentclickListner,filestoshow,appSettingsManager,bitmapHelper);
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(this);
        if (!waitForCameraHasLoaded)
            LoadFiles();

        return view;
    }

    public void setWaitForCameraHasLoaded()
    {
        waitForCameraHasLoaded = true;
    }

    public void LoadFiles()
    {
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
        }
    }

    public ScreenSlideFragment()
    {

    }



    public void SetOnThumbClick(I_ThumbClick thumbClick)
    {
        thumbclick = thumbClick;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
        updateUi(mPagerAdapter.getCurrentFile());
        ImageFragment fragment = (ImageFragment)mPagerAdapter.getRegisteredFragment(position);
        if (fragment == null)
        {
            histogram.setVisibility(View.GONE);
            return;
        }

        int[] histodata = fragment.GetHistogramData();
        if (histodata != null)
        {
            if (topbar.getVisibility() == View.VISIBLE)
                histogram.setVisibility(View.VISIBLE);
            histogram.SetHistogramData(histodata);
        }
        else
        {
            histogram.setVisibility(View.GONE);
            fragment.SetWaitForWorkFinishLisnter(this, position);
        }
    }

    @Override
    public void onPageSelected(int position)
    {


    }

    @Override
    public void HistograRdyToSet(final int[] histodata, final int position)
    {
        histogram.post(new Runnable() {
            @Override
            public void run() {
                if (mPager.getCurrentItem() == position)
                {
                    if (topbar.getVisibility() == View.VISIBLE)
                        histogram.setVisibility(View.VISIBLE);
                    histogram.SetHistogramData(histodata);
                }
            }
        });
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onActivityResultCallback(Uri uri) {

    }

    private FragmentClickClistner fragmentclickListner = new FragmentClickClistner() {
        @Override
        public void onClick(Fragment v) {
            if (topbar.getVisibility() == View.GONE) {
                topbar.setVisibility(View.VISIBLE);
                bottombar.setVisibility(View.VISIBLE);
                histogram.setVisibility(View.VISIBLE);
            }
            else {
                topbar.setVisibility(View.GONE);
                bottombar.setVisibility(View.GONE);
                histogram.setVisibility(View.GONE);
            }
        }
    };


    public void addFile(File file)
    {
        if (mPagerAdapter != null) {
            Logger.d(TAG, "addFile: " +file.getName());
            mPagerAdapter.addFile(file);
            //reloadFilesAndSetLastPos();
        }
    }

    private void reloadFilesAndSetLastPos()
    {
        if (FilePathToLoad.equals("")) {
            mPagerAdapter.SetFiles(FileHolder.getDCIMFiles());
        }
        else
        {
            mPagerAdapter.SetFileToLoadPath(FilePathToLoad);
        }
    }

    private OnClickListener dialogClickListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    bitmapHelper.DeleteFile(file,appSettingsManager,getContext());
                    MediaScannerManager.ScanMedia(getContext(), file.getFile());
                    reloadFilesAndSetLastPos();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    private void updateUi(FileHolder file)
    {
        this.file = file;
        if (file != null)
        {
            filename.setText(file.getFile().getName());
            deleteButton.setVisibility(View.VISIBLE);
            if (file.getFile().getName().endsWith(FileEnding.JPG) || file.getFile().getName().endsWith(FileEnding.JPS)) {
                processJpeg(file.getFile());
                exifinfo.setVisibility(View.VISIBLE);
                play.setVisibility(View.VISIBLE);
            }
            if (file.getFile().getName().endsWith(FileEnding.MP4)) {
                exifinfo.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
            }
            if (file.getFile().getName().endsWith(FileEnding.DNG)) {
                exifinfo.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
            }
            if (file.getFile().getName().endsWith(FileEnding.RAW) || file.getFile().getName().endsWith(FileEnding.BAYER)) {
                exifinfo.setVisibility(View.GONE);
                play.setVisibility(View.GONE);
            }

        }
        else
        {
            filename.setText("No Files");
            histogram.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            play.setVisibility(View.GONE);
        }
    }

    private void processJpeg(final File file)
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Metadata metadata = JpegMetadataReader.readMetadata(file);
                    final Directory exifsub = metadata.getDirectory(ExifSubIFDDirectory.class);
                    iso.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                shutter.setText("S:" +exifsub.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME));
                            }catch (NullPointerException e){
                                shutter.setVisibility(View.GONE);
                            }
                            try
                            {
                                fnumber.setText("f~:" +exifsub.getString(ExifSubIFDDirectory.TAG_FNUMBER));
                            }catch (NullPointerException e){
                                fnumber.setVisibility(View.GONE);
                            }
                            try {
                                focal.setText("A:" +exifsub.getString(ExifSubIFDDirectory.TAG_FOCAL_LENGTH));
                            }catch (NullPointerException e){
                                focal.setVisibility(View.GONE);
                            }
                            try {
                                iso.setText("ISO:" +exifsub.getString(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT));
                            }catch (NullPointerException e){
                                iso.setVisibility(View.GONE);
                            }
                        }
                    });

                } catch (NullPointerException | JpegProcessingException | IOException ex)
                {
                    Logger.d(TAG, "Failed to read Exif");
                }
            }
        });
    }

}
