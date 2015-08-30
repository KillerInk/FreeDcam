package troop.com.imageviewer;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.File;
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
        View view = inflater.inflate(R.layout.imageframent, container, false);
        this.imageView = (TouchImageView)view.findViewById(R.id.imageView_PicView);
        this.spinner = (ProgressBar)view.findViewById(R.id.progressBar);

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
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        spinner.post(new Runnable() {
            @Override
            public void run() {
                fadeout();
                spinner.setVisibility(View.VISIBLE);
            }
        });

        filename.setText(file.getName());
        if (file.getAbsolutePath().endsWith(".jpg"))
        {
            processJpeg(file);
            exifinfo.setVisibility(View.VISIBLE);
            myHistogram.setVisibility(View.VISIBLE);
        }
        if (file.getAbsolutePath().endsWith(".mp4"))
        {
            exifinfo.setVisibility(View.GONE);
            myHistogram.setVisibility(View.GONE);

        }
        if (file.getAbsolutePath().endsWith(".dng"))
        {
            exifinfo.setVisibility(View.GONE);
            myHistogram.setVisibility(View.VISIBLE);
        }
        if (file.getAbsolutePath().endsWith(".raw"))
        {
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
        if (file.getAbsolutePath().endsWith(".jpg"))
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
                        filename.setText("File Damaged:" + file.getAbsolutePath());

                    }
                });
            }
        }
        else
            response = null;
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
}
