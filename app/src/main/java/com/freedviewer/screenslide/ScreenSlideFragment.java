package com.freedviewer.screenslide;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
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
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.ui.I_Activity;
import com.freedcam.ui.handler.MediaScannerManager;
import com.freedcam.ui.views.MyHistogram;
import com.freedcam.utils.FileUtils;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;
import com.freedviewer.helper.BitmapHelper;
import com.freedviewer.gridview.GridViewFragment;
import com.freedviewer.holder.FileHolder;
import com.freedcam.Native.RawToDng;
import com.troop.freedcam.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;



/**
 * Created by troop on 18.09.2015.
 */
public class ScreenSlideFragment extends Fragment implements ViewPager.OnPageChangeListener, I_Activity.I_OnActivityResultCallback
{
    private final static String TAG = ScreenSlideFragment.class.getSimpleName();
    final public static String SAVESTATE_FILEPATH = "savestae_filepath";
    private final static String SAVESTATE_ITEMINT = "savestate_itemint";
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.screenslide_fragment, container, false);
    }

    public ScreenSlideFragment()
    {}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        Logger.d(TAG, "onViewCreated");
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

        if(savedInstanceState != null)
        {
            FilePathToLoad = (String) savedInstanceState.get(SAVESTATE_FILEPATH);
            defitem = (int)savedInstanceState.get(SAVESTATE_ITEMINT);
            Logger.d(TAG, "have file to load from saveinstance onCreated" + FilePathToLoad);

        }

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

                } else {

                    FreeDPool.Execute(new Runnable() {
                        @Override
                        public void run() {
                            final File tmp = file.getFile();
                            convertRawToDng(tmp);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScreenSlideFragment) getParentFragment()).addFile(tmp);
                                }
                            });
                        }
                    });

                }
            }
        });
        this.deleteButton = (Button)view.findViewById(R.id.button_delete);
        deleteButton.setVisibility(View.GONE);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!StringUtils.IS_L_OR_BIG() || StringUtils.WRITE_NOT_EX_AND_L_ORBigger(appSettingsManager)) {
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


    }

    @Override
    public void onResume()
    {
        Logger.d(TAG,"onResume");
        super.onResume();
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager(),mPager,fragmentclickListner,filestoshow,appSettingsManager);
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

    public void SetAppSettingsManager(AppSettingsManager appSettingsManager)
    {
        this.appSettingsManager = appSettingsManager;
    }

    public void SetOnThumbClick(I_ThumbClick thumbClick)
    {
        this.thumbclick = thumbClick;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        updateUi(mPagerAdapter.getCurrentFile());
    }

    @Override
    public void onPageSelected(int position)
    {
        updateUi(mPagerAdapter.getCurrentFile());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onActivityResultCallback(Uri uri) {

    }

    public interface I_ThumbClick
    {
        void onThumbClick();
        void newImageRecieved(File file);
    }

    public interface FragmentClickClistner
    {
        void onClick(Fragment fragment);
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
            reloadFilesAndSetLastPos();
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
                    BitmapHelper.DeleteFile(file,appSettingsManager,getContext());
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
                //myHistogram.setVisibility(View.VISIBLE);
                play.setVisibility(View.VISIBLE);
            }
            if (file.getFile().getName().endsWith(StringUtils.FileEnding.MP4)) {
                exifinfo.setVisibility(View.GONE);
                //myHistogram.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
            }
            if (file.getFile().getName().endsWith(StringUtils.FileEnding.DNG)) {
                exifinfo.setVisibility(View.GONE);
                //myHistogram.setVisibility(View.VISIBLE);
                play.setVisibility(View.VISIBLE);
            }
            if (file.getFile().getName().endsWith(StringUtils.FileEnding.RAW) || file.getFile().getName().endsWith(StringUtils.FileEnding.BAYER)) {
                exifinfo.setVisibility(View.GONE);
                //myHistogram.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);
            }
            //FreeDPool.Execute(new historunner(file));


        }
        else
        {
            filename.setText("No Files");
            histogram.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            play.setVisibility(View.GONE);
        }
    }




    public void SetVisibility(boolean Visible)
    {
        if (deleteButton == null)
            return;
        if (!Visible)
        {
            deleteButton.setVisibility(View.GONE);
            play.setVisibility(View.GONE);
            histogram.setVisibility(View.GONE);
            bottombar.setVisibility(View.GONE);
        }
        else
        {
            deleteButton.setVisibility(View.VISIBLE);
            play.setVisibility(View.VISIBLE);
            histogram.setVisibility(View.VISIBLE);
            bottombar.setVisibility(View.VISIBLE);
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
                                iso.setText("ISO:" +exifsub.getString(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT));
                                shutter.setText("S:" +exifsub.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME));
                                fnumber.setText("f~:" +exifsub.getString(ExifSubIFDDirectory.TAG_FNUMBER));
                                focal.setText("A:" +exifsub.getString(ExifSubIFDDirectory.TAG_FOCAL_LENGTH));
                            }
                        });

                    } catch (NullPointerException | JpegProcessingException | IOException ex)
                    {
                        Logger.d(TAG, "Failed to read Exif");
                    }
                }
            });


    }

    private void convertRawToDng(File file)
    {
        byte[] data = null;
        try {
            data = RawToDng.readFile(file);
            Logger.d("Main", "Filesize: " + data.length + " File:" + file.getAbsolutePath());

        } catch (IOException e) {
            Logger.exception(e);
        }

        String out =null;
        if (file.getName().endsWith(StringUtils.FileEnding.RAW))
            out = file.getAbsolutePath().replace(StringUtils.FileEnding.RAW, StringUtils.FileEnding.DNG);
        if (file.getName().endsWith(StringUtils.FileEnding.BAYER))
            out = file.getAbsolutePath().replace(StringUtils.FileEnding.BAYER, StringUtils.FileEnding.DNG);
        RawToDng dng = RawToDng.GetInstance();
        if (!StringUtils.IS_L_OR_BIG()
                || file.canWrite())
            dng.SetBayerData(data, out);
        else
        {
            DocumentFile df = FileUtils.getFreeDcamDocumentFolder(appSettingsManager,getContext());
            DocumentFile wr = df.createFile("image/dng", file.getName().replace(StringUtils.FileEnding.JPG, StringUtils.FileEnding.DNG));
            ParcelFileDescriptor pfd = null;
            try {

                pfd = getContext().getContentResolver().openFileDescriptor(wr.getUri(), "rw");
            } catch (FileNotFoundException | IllegalArgumentException e) {
                Logger.exception(e);
            }
            if (pfd != null) {
                dng.SetBayerDataFD(data, pfd, file.getName());
                try {
                    pfd.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pfd = null;
            }
        }
        dng.setExifData(100, 0, 0, 0, 0, "", "0", 0);
        dng.WriteDNG(null);
        data = null;
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        getActivity().sendBroadcast(intent);
    }

    class historunner implements Runnable
    {
        FileHolder f;
        historunner(FileHolder f)
        {
            this.f = f;
        }

        @Override
        public void run() {
            Bitmap b =BitmapHelper.getBitmap(f.getFile(),true, mImageThumbSize,mImageThumbSize);
            if (this.f == file)
                histogram.setBitmap(b,false);
            b = null;
        }
    }
}
