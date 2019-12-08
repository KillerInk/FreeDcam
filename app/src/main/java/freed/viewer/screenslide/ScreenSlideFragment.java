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

package freed.viewer.screenslide;


import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.troop.freedcam.R;
import com.troop.freedcam.R.dimen;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import java.io.File;
import java.io.IOException;
import java.util.List;

import freed.ActivityAbstract;
import freed.ActivityAbstract.FormatTypes;
import freed.ActivityInterface;
import freed.ActivityInterface.I_OnActivityResultCallback;
import freed.image.ImageManager;
import freed.image.ImageTask;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.MediaScannerManager;
import freed.utils.StringUtils.FileEnding;
import freed.viewer.holder.FileHolder;
import freed.viewer.screenslide.ImageFragment.I_WaitForWorkFinish;


/**
 * Created by troop on 18.09.2015.
 */
public class ScreenSlideFragment extends Fragment implements ViewPager.OnPageChangeListener, I_OnActivityResultCallback, I_WaitForWorkFinish
{
    public final String TAG = ScreenSlideFragment.class.getSimpleName();
    public interface ButtonClick
    {
        void onButtonClick(int position, View view);
    }

    public interface FragmentClickClistner
    {
        void onFragmentClick(Fragment fragment);
    }

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private ScreenSlidePagerAdapter mPagerAdapter;


    private TextView iso;
    private TextView shutter;
    private TextView focal;
    private TextView fnumber;
    private TextView filename;
    private LinearLayout exifinfo_holder;
    private Button deleteButton;
    private Button play;
    private Button infoButton;
    private LinearLayout bottombar;
    private MyHistogram histogram;

    public int defitem = -1;
    public FormatTypes filestoshow = ActivityAbstract.FormatTypes.all;
    private ButtonClick backClickListner;
    private LinearLayout topbar;
    //hold the showed folder_to_show
    private FileHolder folder_to_show;
    private ExifHandler exifHandler;



    private ActivityInterface activityInterface;
    View view;

    private boolean showExifInfo = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        view = inflater.inflate(layout.freedviewer_screenslide_fragment, container, false);

        int mImageThumbSize = getResources().getDimensionPixelSize(dimen.image_thumbnail_size);
        activityInterface = (ActivityInterface) getActivity();
        exifHandler =new ExifHandler(Looper.getMainLooper());

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = view.findViewById(id.pager);
        topbar = view.findViewById(id.top_bar);
        histogram = view.findViewById(id.screenslide_histogram);

        Button closeButton = view.findViewById(id.button_closeView);
        closeButton.setOnClickListener(v -> {
            if (backClickListner != null) {
                backClickListner.onButtonClick(mPager.getCurrentItem(), view);
                mPager.setCurrentItem(0);
            } else
                getActivity().finish();
        });


        bottombar = view.findViewById(id.bottom_bar);
        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.freedcam);

        iso = view.findViewById(id.textView_iso);
        iso.setTypeface(typeface);
        iso.setText("");
        shutter = view.findViewById(id.textView_shutter);
        shutter.setTypeface(typeface);
        shutter.setText("");
        focal = view.findViewById(id.textView_focal);
        focal.setText("");
        focal.setTypeface(typeface);
        fnumber = view.findViewById(id.textView_fnumber);
        fnumber.setText("");
        fnumber.setTypeface(typeface);
        filename = view.findViewById(id.textView_filename);

        play = view.findViewById(id.button_play);
        play.setVisibility(View.GONE);
        play.setOnClickListener(v -> {
            if (folder_to_show == null)
                return;
            if (!folder_to_show.getFile().getName().endsWith(FileEnding.RAW) || !folder_to_show.getFile().getName().endsWith(FileEnding.BAYER)) {
                    Uri uri = Uri.fromFile(folder_to_show.getFile());

                Intent i;
                if (folder_to_show.getFile().getName().endsWith(FileEnding.MP4))
                {
                    i = new Intent(Intent.ACTION_VIEW);
                    i.setDataAndType(uri, "video/*");
                }
                else {
                    i = new Intent(Intent.ACTION_EDIT);
                    i.setDataAndType(uri, "image/*");
                }

                Intent chooser = Intent.createChooser(i, "Choose App");
                chooser.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //startActivity(i);
                if (i.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(chooser);
                }

            }
        });
        deleteButton = view.findViewById(id.button_delete);
        deleteButton.setVisibility(View.GONE);
        deleteButton.setOnClickListener(view -> {
            if (VERSION.SDK_INT <= VERSION_CODES.LOLLIPOP || VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && !SettingsManager.getInstance().GetWriteExternal()) {
                Builder builder = new Builder(getContext());
                builder.setMessage("Delete File?").setPositiveButton("Yes", onDeleteButtonClick)
                        .setNegativeButton("No", onDeleteButtonClick).show();
            } else {
                DocumentFile sdDir = activityInterface.getExternalSdDocumentFile();
                if (sdDir == null) {

                    activityInterface.ChooseSDCard(ScreenSlideFragment.this);
                } else {
                    Builder builder = new Builder(getContext());
                    builder.setMessage("Delete File?").setPositiveButton("Yes", onDeleteButtonClick)
                            .setNegativeButton("No", onDeleteButtonClick).show();
                }
            }


        });

        infoButton = view.findViewById(id.button_info);
        infoButton.setVisibility(View.GONE);
        infoButton.setOnClickListener(v -> {
            if (showExifInfo)
            {
                showExifInfo = false;
                exifinfo_holder.setVisibility(View.GONE);
            }
            else
            {
                showExifInfo = true;
                exifinfo_holder.setVisibility(View.VISIBLE);
            }
        });

        exifinfo_holder = view.findViewById(id.screenslide_exif_holder);
        exifinfo_holder.setVisibility(View.GONE);


        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager(),mPager,fragmentclickListner);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(2);
        mPager.addOnPageChangeListener(this);
        return view;
    }


    public void SetPostition(int position)
    {
        mPager.setCurrentItem(position, false);
    }


    public ScreenSlideFragment()
    {

    }


    public void NotifyDATAhasChanged(List<FileHolder> files)
    {
        Log.d(TAG,"notifyDataHasChanged");
        if (mPagerAdapter != null && mPager != null) {
            mPagerAdapter.setFiles(files);
        }
    }

    public void setOnBackClickListner(ButtonClick thumbClick)
    {
        backClickListner = thumbClick;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
        updateUi(mPagerAdapter.getCurrentFile());
        ImageFragment fragment = (ImageFragment) mPagerAdapter.getRegisteredFragment(position);
        if (fragment == null)
        {
            histogram.setVisibility(View.GONE);
            return;
        }

        int[] histodata = fragment.GetHistogramData();
        if (histodata != null)
        {
            histogram.SetHistogramData(histodata);
        }
        else
        {
            deleteButton.setVisibility(View.GONE);
            fragment.SetWaitForWorkFinishLisnter(this, position);
        }
    }

    @Override
    public void onPageSelected(int position)
    {


    }

    @Override
    public void onHistogramData(final int[] histodata, final int position)
    {
        exifHandler.setHistogram(histodata,position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onActivityResultCallback(Uri uri) {

    }

    //toggle ui items visibility when a single click from the ImageFragment happen
    private final FragmentClickClistner fragmentclickListner = new FragmentClickClistner() {
        @Override
        public void onFragmentClick(Fragment v) {
            if (topbar.getVisibility() == View.GONE) {
                topbar.setVisibility(View.VISIBLE);
                bottombar.setVisibility(View.VISIBLE);
                histogram.setVisibility(View.VISIBLE);
                if (showExifInfo)
                    exifinfo_holder.setVisibility(View.VISIBLE);
            }
            else {
                topbar.setVisibility(View.GONE);
                bottombar.setVisibility(View.GONE);
                histogram.setVisibility(View.GONE);
                exifinfo_holder.setVisibility(View.GONE);
            }
        }
    };

    private final OnClickListener onDeleteButtonClick = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    activityInterface.DeleteFile(folder_to_show);
                    try {
                        MediaScannerManager.ScanMedia(getContext(), folder_to_show.getFile());
                    }
                    catch (NullPointerException ex)
                    {
                        Log.WriteEx(ex);
                    }

                    if (activityInterface.getFiles() != null && activityInterface.getFiles().size() >0)
                        activityInterface.LoadFolder(activityInterface.getFiles().get(0).getParent(),FormatTypes.all);
                    else
                    {
                        activityInterface.LoadFreeDcamDCIMDirsFiles();
                        updateUi(null);
                    }
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    private void updateUi(FileHolder file)
    {
        this.folder_to_show = file;
        if (file != null)
        {
            filename.setText(file.getFile().getName());
            deleteButton.setVisibility(View.VISIBLE);
            infoButton.setVisibility(View.VISIBLE);
            if (file.getFile().getName().toLowerCase().endsWith(FileEnding.JPG) || file.getFile().getName().toLowerCase().endsWith(FileEnding.JPS)) {
                processExif(file.getFile());
                if (showExifInfo)
                    exifinfo_holder.setVisibility(View.VISIBLE);
                play.setVisibility(View.VISIBLE);
            }
            if (file.getFile().getName().toLowerCase().endsWith(FileEnding.MP4)) {
                exifinfo_holder.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
            }
            if (file.getFile().getName().toLowerCase().endsWith(FileEnding.DNG)) {
                processExif(file.getFile());
                if (showExifInfo)
                    exifinfo_holder.setVisibility(View.VISIBLE);
                play.setVisibility(View.VISIBLE);
            }
            if (file.getFile().getName().toLowerCase().endsWith(FileEnding.RAW) || file.getFile().getName().toLowerCase().endsWith(FileEnding.BAYER)) {
                if (showExifInfo)
                    exifinfo_holder.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);
            }

        }
        else
        {
            filename.setText("No Files");
            infoButton.setVisibility(View.GONE);
            exifinfo_holder.setVisibility(View.GONE);
            histogram.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            play.setVisibility(View.GONE);
        }
    }

    private void processExif(final File file)
    {
        ImageManager.putImageLoadTask(new ExifLoader(file));
    }

    private class ExifLoader extends ImageTask {

        private final File file;

        public ExifLoader(File file)
        {
            this.file = file;
        }

        @Override
        public boolean process() {
            ExifInterface exifInterface = null;
            try {
                exifInterface = new ExifInterface(file.getAbsolutePath());
            } catch (IOException e) {
                Log.WriteEx(e);
            }
            catch (ArrayIndexOutOfBoundsException ex)
            {
                Log.WriteEx(ex);
            }
            if (exifInterface == null)
                return false;
            try {
                String expostring = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
                if (expostring == null)
                    exifHandler.setSHUTTERSPEED("");
                else
                {
                    exifHandler.setSHUTTERSPEED("\uE00B" + getShutterStringSeconds(Double.parseDouble(expostring)));
                }
            }catch (NullPointerException e){
                Log.WriteEx(e);
                exifHandler.setSHUTTERSPEED("");
            }
            try
            {
                String fnums = exifInterface.getAttribute(ExifInterface.TAG_F_NUMBER);
                if (fnums != null)
                    exifHandler.setFNUM("\ue003" + fnums);
                else
                    exifHandler.setFNUM("");
            }catch (NullPointerException e){
                exifHandler.setFNUM("");
                Log.WriteEx(e);
            }
            try {
                String focs = exifInterface.getAttribute(ExifInterface.TAG_APERTURE_VALUE);
                if (focs == null)
                {
                    exifHandler.setFOCAL("");
                }
                else {
                    if (focs.contains("/"))
                    {
                        String split[] = focs.split("/");
                        double numerator = Integer.parseInt(split[0]);
                        double denumerator = Integer.parseInt(split[1]);
                        double foc = numerator /denumerator;
                        focs = foc+"";
                    }
                    exifHandler.setFOCAL("\uE00c" + focs);
                }
            }catch (NullPointerException e){
                exifHandler.setFOCAL("");
                Log.WriteEx(e);
            }
            try {
                String isos = exifInterface.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS);
                if (isos != null)
                    exifHandler.setISO("\uE002" + isos);
                else
                    exifHandler.setISO("");
            }catch (NullPointerException e){
                exifHandler.setISO("");
                Log.WriteEx(e);
            }
            return true;
        }
    }

    private class ExifHandler extends Handler
    {
        private final int MSG_ISO = 0;
        private final int MSG_SHUTTERSPEED = 1;
        private final int MSG_FOCAL = 2;
        private final int MSG_FNUM = 3;
        private final int MSG_HISTOGRAM = 4;

        public ExifHandler(Looper looper)
        {
            super(looper);
        }

        public void setISO(String iso)
        {
            this.obtainMessage(MSG_ISO,iso).sendToTarget();
        }

        public void setSHUTTERSPEED(String iso)
        {
            this.obtainMessage(MSG_SHUTTERSPEED,iso).sendToTarget();
        }

        public void setFOCAL(String iso)
        {
            this.obtainMessage(MSG_FOCAL,iso).sendToTarget();
        }

        public void setFNUM(String iso)
        {
            this.obtainMessage(MSG_FNUM,iso).sendToTarget();
        }

        public void setHistogram(int[] histodata, final int position)
        {
            this.obtainMessage(MSG_HISTOGRAM,position,0,histodata).sendToTarget();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case MSG_ISO:
                    iso.setText((String)msg.obj);
                    break;
                case MSG_FNUM:
                    fnumber.setText((String)msg.obj);
                    break;
                case MSG_FOCAL:
                    focal.setText((String)msg.obj);
                    break;
                case MSG_SHUTTERSPEED:
                    shutter.setText((String)msg.obj);
                    break;
                case MSG_HISTOGRAM:
                    int position = msg.arg1;
                    if (mPager.getCurrentItem() == position)
                    {
                        if (topbar.getVisibility() == View.VISIBLE)
                            histogram.setVisibility(View.VISIBLE);
                        histogram.SetHistogramData((int[])msg.obj);
                        deleteButton.setVisibility(View.VISIBLE);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private String getShutterStringSeconds(double val)
    {
        if (val >= 1) {
            return "" + (int)val;
        }
        int i = (int)(1 / val);
        return "1/" + Integer.toString(i);
    }



}
