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
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.provider.DocumentFile;
import android.support.v4.view.ViewPager;
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
import com.freedcam.ui.handler.MediaScannerManager;
import com.freedcam.ui.views.MyHistogram;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.FileUtils;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;
import com.freedviewer.gridview.GridViewFragment;
import com.freedviewer.helper.BitmapHelper;
import com.freedviewer.holder.FileHolder;
import com.troop.freedcam.R;

import java.io.File;
import java.io.IOException;



/**
 * Created by troop on 18.09.2015.
 */
public class ScreenSlideFragment extends Fragment implements ViewPager.OnPageChangeListener, I_Activity.I_OnActivityResultCallback, ImageFragment.I_WaitForWorkFinish
{

    final public static String TAG = ScreenSlideFragment.class.getSimpleName();
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
    public GridViewFragment.FormatTypes filestoshow = GridViewFragment.FormatTypes.all;
    private I_ThumbClick thumbclick;
    private RelativeLayout topbar;
    //hold the showed file
    private FileHolder file;
    private BitmapHelper bitmapHelper;

    public void SetAppSettingsManagerAndBitmapHelper(AppSettingsManager appSettingsManager, BitmapHelper helper)
    {
        this.appSettingsManager = appSettingsManager;
        this.bitmapHelper = helper;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.screenslide_fragment, container, false);
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) view.findViewById(R.id.pager);
        topbar =(RelativeLayout)view.findViewById(R.id.top_bar);
        histogram = (MyHistogram)view.findViewById(R.id.screenslide_histogram);

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


        bottombar =(LinearLayout)view.findViewById(R.id.bottom_bar);

        exifinfo = (LinearLayout)view.findViewById(R.id.exif_info);
        exifinfo.setVisibility(View.GONE);
        iso = (TextView)view.findViewById(R.id.textView_iso);
        iso.setText("");
        shutter = (TextView)view.findViewById(R.id.textView_shutter);
        shutter.setText("");
        focal = (TextView)view.findViewById(R.id.textView_focal);
        focal.setText("");
        fnumber = (TextView)view.findViewById(R.id.textView_fnumber);
        fnumber.setText("");
        filename = (TextView)view.findViewById(R.id.textView_filename);

        this.play = (Button)view.findViewById(R.id.button_play);
        play.setVisibility(View.GONE);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file == null)
                    return;
                if (!file.getFile().getName().endsWith(StringUtils.FileEnding.RAW) || !file.getFile().getName().endsWith(StringUtils.FileEnding.BAYER)) {
                    Uri uri = Uri.fromFile(file.getFile());

                    Intent i = new Intent(Intent.ACTION_EDIT);
                    if (file.getFile().getName().endsWith(StringUtils.FileEnding.MP4))
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
        this.deleteButton = (Button)view.findViewById(R.id.button_delete);
        deleteButton.setVisibility(View.GONE);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !appSettingsManager.GetWriteExternal())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Delete File?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                } else {
                    DocumentFile sdDir = FileUtils.getExternalSdDocumentFile(appSettingsManager,getContext());
                    if (sdDir == null) {
                        I_Activity i_activity = (I_Activity) getActivity();
                        i_activity.ChooseSDCard(ScreenSlideFragment.this);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Delete File?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }
                }


            }
        });
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager(),mPager,fragmentclickListner,filestoshow,appSettingsManager,bitmapHelper);
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
        }

        return view;
    }

    public ScreenSlideFragment()
    {
    }



    public void SetOnThumbClick(I_ThumbClick thumbClick)
    {
        this.thumbclick = thumbClick;
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
            {
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

    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
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
            if (file.getFile().getName().endsWith(StringUtils.FileEnding.JPG) || file.getFile().getName().endsWith(StringUtils.FileEnding.JPS)) {
                processJpeg(file.getFile());
                exifinfo.setVisibility(View.VISIBLE);
                play.setVisibility(View.VISIBLE);
            }
            if (file.getFile().getName().endsWith(StringUtils.FileEnding.MP4)) {
                exifinfo.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
            }
            if (file.getFile().getName().endsWith(StringUtils.FileEnding.DNG)) {
                exifinfo.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
            }
            if (file.getFile().getName().endsWith(StringUtils.FileEnding.RAW) || file.getFile().getName().endsWith(StringUtils.FileEnding.BAYER)) {
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
                    final Metadata metadata = JpegMetadataReader.readMetadata(file);
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
