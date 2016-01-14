package troop.com.imageviewer;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.defcomk.jni.libraw.RawUtils;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.ortiz.touch.TouchImageView;
import com.troop.androiddng.RawToDng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import troop.com.views.MyHistogram;

/**
 * Created by troop on 21.08.2015.
 */
public class ImageFragment extends Fragment
{
    TouchImageView imageView;
    private File file;
    ProgressBar spinner;
    TextView iso;
    TextView shutter;
    TextView focal;
    TextView fnumber;
    TextView filename;
    LinearLayout exifinfo;
    MyHistogram myHistogram;
    Button play;

    Button deleteButton;

    public ScreenSlideFragment activity;

    LinearLayout ll;

    private final int animationTime = 500;

    public void SetFilePath(File filepath)
    {
        this.file = filepath;
    }

    public File GetFilePath()
    {
        return file;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.imageframent, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.imageView = (TouchImageView)view.findViewById(R.id.imageView_PicView);
        this.spinner = (ProgressBar)view.findViewById(R.id.progressBar);
        if(savedInstanceState != null && file == null)
        {
            file = new File((String) savedInstanceState.get(ScreenSlideFragment.SAVESTATE_FILEPATH));
        }

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


        this.deleteButton = (Button)view.findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Delete File?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


            }
        });

        this.play = (Button)view.findViewById(R.id.button_play);
        play.setVisibility(View.GONE);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file == null)
                    return;
                if (!file.getAbsolutePath().endsWith(".raw")) {
                    Uri uri = Uri.fromFile(file);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    if (file.getAbsolutePath().endsWith("mp4"))
                        i.setDataAndType(uri, "video/*");
                    else
                        i.setDataAndType(uri, "image/*");
                    startActivity(i);
                }
                else
                {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            convertRawToDng(file);
                            activity.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    activity.ReloadFilesAndSetLast();
                                }
                            });
                        }
                    }).start();

                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ScreenSlideFragment.SAVESTATE_FILEPATH, file.getAbsolutePath());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (file != null) {
            spinner.post(new Runnable() {
                @Override
                public void run() {
                    fadeout();
                    spinner.setVisibility(View.VISIBLE);
                }
            });

            filename.setText(file.getName());
            if (file.getAbsolutePath().endsWith(".jpg")) {
                processJpeg(file);
                exifinfo.setVisibility(View.VISIBLE);
                myHistogram.setVisibility(View.VISIBLE);
            }
            if (file.getAbsolutePath().endsWith(".mp4")) {
                exifinfo.setVisibility(View.GONE);
                myHistogram.setVisibility(View.GONE);

            }
            if (file.getAbsolutePath().endsWith(".dng")) {
                exifinfo.setVisibility(View.GONE);
                myHistogram.setVisibility(View.VISIBLE);
            }
            if (file.getAbsolutePath().endsWith(".raw")) {
                exifinfo.setVisibility(View.GONE);
                myHistogram.setVisibility(View.VISIBLE);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    loadImage();
                }
            }).start();
        }
        else
        {
            filename.setText("No Files");
            spinner.setVisibility(View.GONE);
            myHistogram.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            play.setVisibility(View.GONE);
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
    }

    private void loadImage()
    {
        final Bitmap response = getBitmap();

        imageView.post(new Runnable() {
            @Override
            public void run() {
                fadein();
                imageView.setImageBitmap(response);
                myHistogram.setBitmap(response, false);
            }
        });
    }

    private Bitmap getBitmap()
    {
        Bitmap response;
        if (file.getAbsolutePath().endsWith(".jpg") || file.getAbsolutePath().endsWith(".jps"))
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            response = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        }
        else if (file.getAbsolutePath().endsWith(".mp4"))
            response = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        else if (file.getAbsolutePath().endsWith(".dng")|| file.getAbsolutePath().endsWith(".raw"))
        {
            try {


                response = RawUtils.UnPackRAW(file.getAbsolutePath());
                if(response != null)
                    response.setHasAlpha(true);
            }
            catch (IllegalArgumentException ex)
            {
                response = null;
                filename.post(new Runnable() {
                    @Override
                    public void run() {
                        filename.setText("Failed to load:" + file.getName());

                    }
                });
            }
        }
        else
            response = null;
        if (response == null)
            workDone.onWorkDone(false, file);
        else
            workDone.onWorkDone(true, file);

        return response;
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

    interface WorkeDoneInterface
    {
        void onWorkDone(boolean success, File file);
    }

    WorkeDoneInterface workDone = new WorkeDoneInterface() {
        @Override
        public void onWorkDone(final boolean success, final File file)
        {
            play.post(new Runnable() {
                @Override
                public void run() {
                    if (success)
                    {
                        if (file.getAbsolutePath().endsWith(".jpg")) {
                            play.setVisibility(View.VISIBLE);
                        }
                        else if (file.getAbsolutePath().endsWith(".jps")) {
                            play.setVisibility(View.VISIBLE);
                        }
                        else if (file.getAbsolutePath().endsWith(".mp4")) {
                            play.setVisibility(View.VISIBLE);
                        }
                        else if (file.getAbsolutePath().endsWith(".dng")) {
                            play.setVisibility(View.VISIBLE);
                        }
                        else if (file.getAbsolutePath().endsWith(".raw")) {
                            play.setVisibility(View.GONE);
                        }
                    }
                    else
                    {
                        play.setVisibility(View.GONE);
                    }
                }
            });

        }
    };

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:

                    boolean d = file.delete();
                    activity.reloadFilesAndSetLastPos();
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
            Log.d("Main", "Filesize: " + data.length + " File:" + file.getAbsolutePath());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String out = file.getAbsolutePath().replace(".raw", ".dng");
        RawToDng dng = RawToDng.GetInstance();
        dng.SetBayerData(data, out);
        dng.setExifData(100, 0, 0, 0, 0, "", "0", 0);
        dng.WriteDNG(null);
        data = null;
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        activity.getActivity().sendBroadcast(intent);
    }


}
