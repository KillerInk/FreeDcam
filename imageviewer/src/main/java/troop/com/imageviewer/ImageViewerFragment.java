package troop.com.imageviewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.defcomk.jni.libraw.RawUtils;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import troop.com.imageviewer.R;
import troop.com.imageviewer.MyHistogram;

import com.ortiz.touch.TouchImageView;
import com.troop.freedcam.ui.menu.themes.classic.I_swipe;
import com.troop.freedcam.ui.menu.themes.classic.SwipeMenuListner;
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
public class ImageViewerFragment extends Fragment
{
    final String TAG = ImageViewerFragment.class.getSimpleName();
    View view;
    View viewHist;
    Button closeButton;
    //TouchImageView imageView;
    ImageView imageView;
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
    private HandlerThread backgroundThread;
    Handler handler;
    MyHistogram myHistogram;
    LinearLayout ll;
    RelativeLayout ui_holder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.imageviewer_fragment, container, false);


        this.closeButton = (Button)view.findViewById(R.id.button_closeView);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.remove(ImageViewerFragment.this);
                transaction.commit();
                stopThread();
            }
        });
        myHistogram = new MyHistogram(container.getContext());
        ll = (LinearLayout)view.findViewById(R.id.histoView);
        ll.addView(myHistogram);

        this.imageView = (ImageView)view.findViewById(R.id.Output);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ui_holder.getVisibility() == View.GONE)
                    ui_holder.setVisibility(View.VISIBLE);
                else
                    ui_holder.setVisibility(View.GONE);
            }
        });
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

        this.last = (Button)view.findViewById(R.id.button_last);
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadLastImage();
            }
        });

        this.next = (Button)view.findViewById(R.id.button_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNextImage();
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
        spinner.setVisibility(View.GONE);
        filename = (TextView)view.findViewById(R.id.textView_filename);
        ui_holder = (RelativeLayout)view.findViewById(R.id.ui_holder);


        loadFilePaths();
        startThread();
        current = files.length -1;
        if (files.length > 0)
            setBitmap(files[current]);
        return view;
    }

    private void startThread() {
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
    }

    private void loadFilePaths()
    {

        File directory = new File(Environment.getExternalStorageDirectory() + "/DCIM/FreeCam/");
        files = directory.listFiles();
        List<File> jpegs = new ArrayList<File>();
        for (File f : files)
        {
            if (!f.isDirectory() && (f.getAbsolutePath().endsWith(".jpg") || f.getAbsolutePath().endsWith(".mp4")|| f.getAbsolutePath().endsWith(".dng")))
                jpegs.add(f);
        }
        try {
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


    private void setBitmap(final File file)
    {
        imageView.setImageBitmap(null);
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
            spinner.setVisibility(View.VISIBLE);
            myHistogram.setVisibility(View.GONE);

            handler.post(new Runnable() {
                @Override
                public void run()
                {
                    final int itemint = current;
                    final Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                    imageView.post(new Runnable() {
                        @Override
                        public void run()
                        {
                            if (itemint == current)
                            {
                                imageView.setImageBitmap(bitmap);
                                spinner.setVisibility(View.GONE);
                            }
                        }
                    });

                }
            });

        }
        if (file.getAbsolutePath().endsWith(".dng"))
        {

            //RawUtils.convertFileToByteArray(file);

            play.setText("Open DNG");
            exifinfo.setVisibility(View.GONE);
            play.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
            myHistogram.setVisibility(View.VISIBLE);
            handler.post(new Runnable() {
                @Override
                public void run()
                {
                    final int itemint = current;
                    final Bitmap map= RawUtils.UnPackRAW(file.getAbsolutePath());

                    map.setHasAlpha(true);

                    //saveBytesToFile(bytes,file);
                    //Log.d("THUMB Size",String.valueOf(bytes.length));
                    //final Bitmap map = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (itemint == current) {
                                //imageView.setImageBitmap(map);
                                ImageView IV = (ImageView)view.findViewById(R.id.Output);
                                IV.setImageBitmap(map);
                                myHistogram.setBitmap(map, false);
                                spinner.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            });

        }
    }

    public void saveBytesToFile(byte[] bytes, File fileName)
    {
        File newy = new File(fileName.getAbsoluteFile().getAbsolutePath().replace(".dng","_thumb.jpg"));
        Log.d(TAG, "Start Saving Bytes");
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(newy);
            outStream.write(bytes);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


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
        spinner.setVisibility(View.VISIBLE);
        handler.post(new Runnable() {
            @Override
            public void run()
            {

                //loadBitmapSampleSized(16, file);
                loadBitmapSampleSized(2, file);
            }
        });

    }

    private void loadBitmapSampleSized(int samplesize, File file)
    {
        final int itemint = current;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = samplesize;
        final Bitmap map = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        Log.d(TAG, "Bitmap loaded");

        //options =null;
        imageView.post(new Runnable() {
            @Override
            public void run()
            {
                if (itemint == current) {
                    imageView.setImageBitmap(map);
                    myHistogram.setBitmap(map, false);
                    spinner.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadNextImage()
    {
        current++;
        if (current == files.length)
            current = 0;
        setBitmap(files[current]);
    }

    private void loadLastImage()
    {
        current--;
        if (current < 0)
            current = files.length-1;
        setBitmap(files[current]);
    }

}
