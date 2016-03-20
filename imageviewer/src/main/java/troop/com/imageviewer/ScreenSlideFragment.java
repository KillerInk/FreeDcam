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
    Button deleteButton;
    Button play;

    private File currentFile;
    public int defitem = -1;
    public String FilePathToLoad = "";
    public GridViewFragment.FormatTypes filestoshow = GridViewFragment.FormatTypes.all;
    private I_ThumbClick thumbclick;


    LinearLayout ll;

    TextView iso;
    TextView shutter;
    TextView focal;
    TextView fnumber;
    TextView filename;
    LinearLayout exifinfo;
    MyHistogram myHistogram;

    public boolean barsvisible;
    private RelativeLayout topbar;
    private LinearLayout bottombar;
    SharedPreferences sharedPref;
    final String KEY_BARSVISIBLE = "key_barsvisible";
    private Animation animBottomToTopShow, animBottomToTopHide, animTopToBottomShow, animTopToBottomHide;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.screenslide_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        myHistogram = new MyHistogram(view.getContext());
        ll = (LinearLayout)view.findViewById(R.id.histoView);
        ll.addView(myHistogram);

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
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) view.findViewById(R.id.pager);


        topbar =(RelativeLayout)view.findViewById(R.id.top_bar);
        bottombar =(LinearLayout)view.findViewById(R.id.bottom_bar);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        initAnimation();
        barsvisible = sharedPref.getBoolean(KEY_BARSVISIBLE, false);
        if (barsvisible)
            showBars();
        else
            hideBars();

        this.closeButton = (Button)view.findViewById(R.id.button_closeView);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thumbclick != null)
                    thumbclick.onThumbClick();
                else
                    getActivity().finish();
            }
        });
        this.play = (Button)view.findViewById(R.id.button_play);
        play.setVisibility(View.GONE);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFile == null)
                    return;
                if (!currentFile.getAbsolutePath().endsWith(".raw")) {
                    Uri uri = Uri.fromFile(currentFile);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    if (currentFile.getAbsolutePath().endsWith("mp4"))
                        i.setDataAndType(uri, "video/*");
                    else
                        i.setDataAndType(uri, "image/*");
                    startActivity(i);
                } else {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final File tmp = currentFile;
                            convertRawToDng(tmp);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mPagerAdapter.addFile(tmp);
                                }
                            });
                        }
                    }).start();

                }
            }
        });
        this.deleteButton = (Button)view.findViewById(R.id.button_delete);
        deleteButton.setVisibility(View.GONE);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Delete File?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


            }
        });
        /*if(savedInstanceState != null)
        {
            FilePathToLoad = (String) savedInstanceState.get(SAVESTATE_FILEPATH);
            defitem = (int)savedInstanceState.get(SAVESTATE_ITEMINT);
            Logger.d(TAG, "have file to load from saveinstance onCreated" + FilePathToLoad);
        }*/
    }


    @Override
    public void onResume()
    {
        super.onResume();

        if (FilePathToLoad.equals("")) {
            FilePathToLoad = StringUtils.GetInternalSDCARD() + StringUtils.freedcamFolder;

        }
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager(),mPager,fragmentclickListner,filestoshow);
        mPagerAdapter.SetFileToLoadPath(FilePathToLoad);
        mPager.addOnPageChangeListener(this);
        mPager.setAdapter(mPagerAdapter);
        if(mPagerAdapter.getFiles() != null ) {
            if (mPagerAdapter.getFiles().size() > 0 && defitem == -1) {
                mPager.setCurrentItem(0);
            } else
                mPager.setCurrentItem(defitem);
            if (mPagerAdapter.getFiles().size() > 0) {
                currentFile = mPagerAdapter.getCurrentFile().getFile();
            }
            else
                currentFile = null;
        }
        updateUi(currentFile);
    }

    private void updateUi(File file)
    {
        if (file != null)
        {
            filename.setText(file.getName());
            deleteButton.setVisibility(View.VISIBLE);
            if (file.getAbsolutePath().endsWith(".jpg")) {
                processJpeg(file);
                exifinfo.setVisibility(View.VISIBLE);
                //myHistogram.setVisibility(View.VISIBLE);
                play.setVisibility(View.VISIBLE);
            }
            if (file.getAbsolutePath().endsWith(".mp4")) {
                exifinfo.setVisibility(View.GONE);
                //myHistogram.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
            }
            if (file.getAbsolutePath().endsWith(".dng")) {
                exifinfo.setVisibility(View.GONE);
                //myHistogram.setVisibility(View.VISIBLE);
                play.setVisibility(View.VISIBLE);
            }
            if (file.getAbsolutePath().endsWith(".raw")) {
                exifinfo.setVisibility(View.GONE);
                //myHistogram.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);
            }
            myHistogram.setBitmap(BitmapHelper.getBitmap(file,true,mImageThumbSize,mImageThumbSize), false);
        }
        else
        {
            filename.setText("No Files");
            myHistogram.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            play.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        /*outState.putString(SAVESTATE_FILEPATH, FilePathToLoad);
        outState.putInt(SAVESTATE_ITEMINT, mPager.getCurrentItem());*/
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
    public void onPageSelected(int position)
    {
        currentFile = mPagerAdapter.getFiles().get(position).getFile();
        updateUi(currentFile);
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
            showHideBars();
        }
    };


    private void processJpeg(final File file)
    {
        try {
            final Metadata metadata = JpegMetadataReader.readMetadata(file);
            final Directory exifsub = metadata.getDirectory(ExifSubIFDDirectory.class);
            iso.setText("ISO: " +exifsub.getString(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT));
            shutter.setText("Exposure Time: " +exifsub.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME));
            fnumber.setText("Aperture:" +exifsub.getString(ExifSubIFDDirectory.TAG_FNUMBER));
            focal.setText("Focal Length:" +exifsub.getString(ExifSubIFDDirectory.TAG_FOCAL_LENGTH));
        } catch (IOException e) {
            Logger.exception(e);
        } catch (JpegProcessingException e) {
            Logger.exception(e);
        }
        catch (NullPointerException ex)
        {
            Logger.exception(ex);
        }
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:

                    if (!StringUtils.IS_L_OR_BIG()) {
                        boolean d = currentFile.delete();
                    }
                    else
                    {
                       /* DocumentFile df = DocumentFile.fromFile(currentFile);
                        boolean d = df.delete();
                        if (!d)
                        {
                            Uri uri = getImageContentUri(getContext(), currentFile);
                            d = DocumentsContract.deleteDocument(getContext().getContentResolver(), uri);
                        }
                        Logger.d(TAG, "file delted:" +d);*/
                    }
                    mPagerAdapter.reloadFilesAndSetLastPos();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };


    private void convertRawToDng(File file)
    {
        byte[] data = null;
        try {
            data = RawToDng.readFile(file);
            Logger.d("Main", "Filesize: " + data.length + " File:" + file.getAbsolutePath());

        } catch (FileNotFoundException e) {
            Logger.exception(e);
        } catch (IOException e) {
            Logger.exception(e);
        }

        String out = file.getAbsolutePath().replace(".raw", ".dng");
        RawToDng dng = RawToDng.GetInstance();
        dng.SetBayerData(data, out);
        dng.setExifData(100, 0, 0, 0, 0, "", "0", 0);
        dng.WriteDNG(null);
        data = null;
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        getActivity().sendBroadcast(intent);
    }

    public void showHideBars()
    {
        if (!barsvisible) {
            showBars();
        }
        else {
            hideBars();
        }

    }

    private void showBars()
    {
        topbar.startAnimation(animTopToBottomShow);
        topbar.setVisibility(View.VISIBLE);
        bottombar.startAnimation(animBottomToTopShow);
        myHistogram.startAnimation(animBottomToTopShow);
        myHistogram.setVisibility(View.VISIBLE);
        bottombar.setVisibility(View.VISIBLE);
        barsvisible = true;
        sharedPref.edit().putBoolean(KEY_BARSVISIBLE, barsvisible).commit();
    }

    private void hideBars()
    {
        topbar.startAnimation(animTopToBottomHide);
        topbar.setVisibility(View.GONE);
        bottombar.startAnimation(animBottomToTopHide);
        bottombar.setVisibility(View.GONE);
        myHistogram.setVisibility(View.GONE);
        barsvisible = false;
        sharedPref.edit().putBoolean(KEY_BARSVISIBLE, barsvisible).commit();
    }

    private void initAnimation()
    {
        animBottomToTopShow = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_to_top_enter);
        animBottomToTopHide = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_to_top_exit);
        animTopToBottomShow = AnimationUtils.loadAnimation(getContext(), R.anim.top_to_bottom_enter);
        animTopToBottomHide = AnimationUtils.loadAnimation(getContext(), R.anim.top_to_bottom_exit);
    }

    public void addFile(File file)
    {
        if (mPagerAdapter != null)
            mPagerAdapter.addFile(file);
    }

}
