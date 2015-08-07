package troop.com.imageviewer;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.defcomk.jni.libraw.RawUtils;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import com.ortiz.touch.TouchImageView;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.TouchHandler;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by troop on 09.05.2015.
 */
public class ImageViewerFragment extends Fragment implements View.OnTouchListener
{
    final String TAG = ImageViewerFragment.class.getSimpleName();
    View view;
    View viewHist;
    Button closeButton;
    TouchImageView imageView;
    File[] files;
    int current = 0;
    Button play;
    Button next;
    Button last;
    TextView iso;
    TextView shutter;
    TextView focal;
    TextView fnumber;
    TextView filename;
    LinearLayout exifinfo;
    ProgressBar spinner;
    MyHistogram myHistogram;
    LinearLayout ll;
    RelativeLayout ui_holder;
    private I_Activity i_activity;

    int imageviewDEfaultPos;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.imageviewer_fragment, container, false);


        this.closeButton = (Button)view.findViewById(R.id.button_closeView);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i_activity != null)
                    i_activity.loadCameraUiFragment();
                //stopThread();
                if (i_activity == null)
                    getActivity().finish();
            }
        });
        myHistogram = new MyHistogram(container.getContext());
        ll = (LinearLayout)view.findViewById(R.id.histoView);
        ll.addView(myHistogram);

        this.imageView = (TouchImageView)view.findViewById(R.id.imageView_PicView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ui_holder.getVisibility() == View.GONE)
                    ui_holder.setVisibility(View.VISIBLE);
                else
                    ui_holder.setVisibility(View.GONE);
            }
        });
        imageviewDEfaultPos = (int)imageView.getX();
        imageView.setOnTouchListener(this);
        this.play = (Button)view.findViewById(R.id.button_play);
        play.setVisibility(View.GONE);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.fromFile(files[current]);
                Intent i = new Intent(Intent.ACTION_VIEW);
                if (files[current].getAbsolutePath().endsWith("mp4"))
                    i.setDataAndType(uri, "video/*");
                else
                    i.setDataAndType(uri, "image/*");
                getActivity().startActivity(i);
            }
        });



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

        spinner = (ProgressBar)view.findViewById(R.id.progressBar);
        //spinner.setVisibility(View.GONE);
        filename = (TextView)view.findViewById(R.id.textView_filename);
        ui_holder = (RelativeLayout)view.findViewById(R.id.ui_holder);



        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //imageloader = new LoadImageTask();
        try {
            loadFilePaths();
            //startThread();
            current = files.length -1;
            if (files.length > 0)
                setBitmap(files[current]);
            else
            {
                spinner.setVisibility(View.GONE);
                filename.setText("No Files in FreeDcam Folder");
            }
        }
        catch (Exception ex)
        {
            Log.d(TAG, "WTF DOES THAT FUCKInG SHIT CRASH");
        }

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    public void SetIActivity(I_Activity i_activity)
    {
        this.i_activity =  i_activity;
    }

    /*private void startThread() {
        backgroundThread = new HandlerThread("PictureModuleThread");
        backgroundThread.start();
        handler = new Handler(backgroundThread.getLooper());
    }

    private void stopThread() {
        if (Build.VERSION.SDK_INT>17)
            backgroundThread.quitSafely();
        else
            backgroundThread.quit();
        try {
            backgroundThread.join();
            backgroundThread = null;
            handler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/

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


    private int animationTime = 500;
    private void crossfade()
    {
        imageView.animate().alpha(0f).setDuration(animationTime).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                imageView.animate().alpha(1f).setDuration(animationTime).setListener(null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
    private void fadeout()
    {
        imageView.animate().alpha(0f).setDuration(animationTime).setListener(null);
        spinner.animate().alpha(1f).setDuration(animationTime).setListener(null);
    }

    private void fadein()
    {
        spinner.animate().alpha(0f).setDuration(animationTime).setListener(null);
        imageView.animate().alpha(1f).setDuration(animationTime).setListener(null);
    }

    private void setBitmap(final File file)
    {
        spinner.post(new Runnable() {
            @Override
            public void run() {
                fadeout();
                spinner.setVisibility(View.VISIBLE);
            }
        });

        //imageView.setImageBitmap(null);
        filename.setText(file.getName());
        if (file.getAbsolutePath().endsWith(".jpg"))
        {
            processJpeg(file);
            exifinfo.setVisibility(View.VISIBLE);
            myHistogram.setVisibility(View.VISIBLE);
            play.setVisibility(View.GONE);
        }
        if (file.getAbsolutePath().endsWith(".mp4"))
        {
            exifinfo.setVisibility(View.GONE);
            play.setText("Play");
            play.setVisibility(View.VISIBLE);
            myHistogram.setVisibility(View.GONE);

        }
        if (file.getAbsolutePath().endsWith(".dng"))
        {
            play.setText("Open DNG");
            exifinfo.setVisibility(View.GONE);
            play.setVisibility(View.VISIBLE);
            myHistogram.setVisibility(View.VISIBLE);
        }
        imageView.post(new Runnable() {
            @Override
            public void run() {
                LoadFUCKINGIMAGE(file);
            }
        });

    }

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
            e.printStackTrace();
        } catch (JpegProcessingException e) {
            e.printStackTrace();
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }
    }


    private void loadNextImage()
    {
        spinner.post(new Runnable() {
            @Override
            public void run() {
                if (files.length == 0)
                    return;
                current++;
                if (current == files.length)
                    current = 0;
                setBitmap(files[current]);
            }
        });

    }

    private void loadLastImage()
    {
        spinner.post(new Runnable() {
            @Override
            public void run() {
                if (files.length == 0)
                    return;
                current--;
                if (current < 0)
                    current = files.length-1;
                setBitmap(files[current]);
            }
        });

    }

    private void LoadFUCKINGIMAGE(File file)
    {
        final Bitmap response;
        if (file.getAbsolutePath().endsWith(".jpg"))
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            response = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        }
        else if (file.getAbsolutePath().endsWith(".mp4"))
            response = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        else if (file.getAbsolutePath().endsWith(".dng"))
        {
            response = new RawUtils().UnPackRAW(file.getAbsolutePath());
            response.setHasAlpha(true);
        }
        else response = null;
        imageView.post(new Runnable() {
            @Override
            public void run() {
                fadein();
                imageView.setImageBitmap(response);
                myHistogram.setBitmap(response, false);
            }
        });

    }


    public int startX;
    public int currentX;

    boolean swipe = false;
    long start;
    long duration;
    static final int MAX_DURATION = 3500;
    float x, difx;
    boolean lastset;

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if (!imageView.isZoomed()) {
            boolean fireagain = false;
            final int distance = imageView.getWidth() / 4;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = (int) event.getX() - (int) imageView.getX();
                    x = (int) event.getX();
                    start = System.currentTimeMillis();
                    break;
                // case MotionEvent.A
                case MotionEvent.ACTION_MOVE:
                    difx = x - imageView.getX();
                    int xd = TouchHandler.getDistance(imageviewDEfaultPos, (int) imageView.getX());
                    if (xd < distance) {
                        imageView.setX(event.getX() - difx);

                    }
                    break;
                case MotionEvent.ACTION_UP:
                    xd = TouchHandler.getDistance(imageviewDEfaultPos, (int) imageView.getX());
                    if (xd >= distance && !lastset) {
                        lastset = true;

                        if (imageviewDEfaultPos + distance > xd) {
                            loadNextImage();
                        } else if (imageviewDEfaultPos - distance < xd) {
                            loadLastImage();
                        }
                        imageView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                lastset = false;
                            }
                        }, 100);
                        imageView.setX(imageviewDEfaultPos);
                        x = imageviewDEfaultPos;
                        return false;
                    }
                    else
                        imageView.setX(imageviewDEfaultPos);
                    break;
            }
        }
        return false;
    }
}
