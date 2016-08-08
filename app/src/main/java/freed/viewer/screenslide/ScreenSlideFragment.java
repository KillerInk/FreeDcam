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
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.provider.DocumentFile;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
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
import com.troop.freedcam.R.dimen;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import java.io.File;
import java.io.IOException;

import freed.ActivityAbstract;
import freed.ActivityAbstract.FormatTypes;
import freed.ActivityInterface;
import freed.ActivityInterface.I_OnActivityResultCallback;
import freed.cam.ui.handler.MediaScannerManager;
import freed.utils.FreeDPool;
import freed.utils.Logger;
import freed.utils.StringUtils.FileEnding;
import freed.viewer.holder.FileHolder;
import freed.viewer.screenslide.ImageFragment.I_WaitForWorkFinish;


/**
 * Created by troop on 18.09.2015.
 */
public class ScreenSlideFragment extends Fragment implements OnPageChangeListener, I_OnActivityResultCallback, I_WaitForWorkFinish
{

    public final String TAG = ScreenSlideFragment.class.getSimpleName();
    public interface I_ThumbClick
    {
        void onThumbClick(int position,View view);
    }

    public interface FragmentClickClistner
    {
        void onClick(Fragment fragment);
    }
    private int mImageThumbSize;

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
    public FormatTypes filestoshow = ActivityAbstract.FormatTypes.all;
    private I_ThumbClick thumbclick;
    private RelativeLayout topbar;
    //hold the showed folder_to_show
    private FileHolder folder_to_show;

    private ActivityInterface activityInterface;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        view = inflater.inflate(layout.freedviewer_screenslide_fragment, container, false);

        mImageThumbSize = getResources().getDimensionPixelSize(dimen.image_thumbnail_size);
        activityInterface = (ActivityInterface) getActivity();

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) view.findViewById(id.pager);
        topbar =(RelativeLayout)view.findViewById(id.top_bar);
        histogram = (MyHistogram)view.findViewById(id.screenslide_histogram);

        closeButton = (Button)view.findViewById(id.button_closeView);
        closeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (thumbclick != null) {
                    thumbclick.onThumbClick(mPager.getCurrentItem(), view);
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
                if (VERSION.SDK_INT <= VERSION_CODES.LOLLIPOP || VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && !activityInterface.getAppSettings().GetWriteExternal()) {
                    Builder builder = new Builder(getContext());
                    builder.setMessage("Delete File?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                } else {
                    DocumentFile sdDir = activityInterface.getExternalSdDocumentFile();
                    if (sdDir == null) {

                        activityInterface.ChooseSDCard(ScreenSlideFragment.this);
                    } else {
                        Builder builder = new Builder(getContext());
                        builder.setMessage("Delete File?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }
                }


            }
        });
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
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


    public void NotifyDATAhasChanged()
    {
        if (mPagerAdapter != null || mPager != null)
            mPagerAdapter.notifyDataSetChanged();
    }

    public void SetOnThumbClick(I_ThumbClick thumbClick)
    {
        thumbclick = thumbClick;
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
            if (topbar.getVisibility() == View.VISIBLE)
                histogram.setVisibility(View.VISIBLE);
            histogram.SetHistogramData(histodata);
        }
        else
        {
            histogram.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
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
                    deleteButton.setVisibility(View.VISIBLE);
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

    private final FragmentClickClistner fragmentclickListner = new FragmentClickClistner() {
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

    private final OnClickListener dialogClickListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    activityInterface.DeleteFile(folder_to_show);
                    MediaScannerManager.ScanMedia(getContext(), folder_to_show.getFile());
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
            exifinfo.setVisibility(View.GONE);
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

    class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
    {
        private final String TAG = ScreenSlidePagerAdapter.class.getSimpleName();
        private final SparseArray<Fragment> registeredFragments;

        public ScreenSlidePagerAdapter(FragmentManager fm)
        {
            super(fm);

            registeredFragments = new SparseArray<>();
        }

        public FileHolder getCurrentFile()
        {
            if (activityInterface.getFiles() != null && activityInterface.getFiles().size()>0)
                return activityInterface.getFiles().get(mPager.getCurrentItem());
            else
                return null;
        }


        //FragmentStatePagerAdapter implementation START

        @Override
        public Fragment getItem(int position)
        {
            ImageFragment  currentFragment = new ImageFragment();
            if (activityInterface.getFiles() == null || activityInterface.getFiles().size() == 0)
                currentFragment.SetFilePath(null);
            else
                currentFragment.SetFilePath(activityInterface.getFiles().get(position));
            currentFragment.SetOnclickLisnter(fragmentclickListner);

            return currentFragment;
        }

        @Override
        public int getCount()
        {
            if(activityInterface.getFiles() != null)
                return activityInterface.getFiles().size();
            else return 1;
        }

        @Override
        public int getItemPosition(Object object)
        {
            ImageFragment imageFragment = (ImageFragment) object;
            FileHolder file = imageFragment.GetFilePath();
            int position = activityInterface.getFiles().indexOf(file);
            //if (position >= 0) {
                // The current data matches the data in this active fragment, so let it be as it is.
            if (position == imageFragment.getPosition){
                return position;
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

}
