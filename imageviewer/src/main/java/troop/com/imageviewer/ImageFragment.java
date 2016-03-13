package troop.com.imageviewer;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import com.troop.filelogger.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import troop.com.views.MyHistogram;

/**
 * Created by troop on 21.08.2015.
 */
public class ImageFragment extends Fragment
{
    final String TAG = ImageFragment.class.getSimpleName();
    private TouchImageView imageView;
    private File file;
    private int mImageThumbSize = 0;
    private View.OnClickListener onClickListener;

    public void SetFilePath(File filepath)
    {
        this.file = filepath;
    }

    public File GetFilePath()
    {
        return file;
    }

    public void SetOnclickLisnter(View.OnClickListener onClickListener)
    {
        this.onClickListener = onClickListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        return inflater.inflate(R.layout.imageframent, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.imageView = (TouchImageView)view.findViewById(R.id.imageView_PicView);

        if(savedInstanceState != null && file == null)
        {
            file = new File((String) savedInstanceState.get(ScreenSlideFragment.SAVESTATE_FILEPATH));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if (file != null && file.getAbsolutePath() != null)
            outState.putString(ScreenSlideFragment.SAVESTATE_FILEPATH, file.getAbsolutePath());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(onClickListener != null)
            imageView.setOnClickListener(onClickListener);
        if (file != null) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    loadImage();
                }
            }).start();
        }

    }

    private void loadImage()
    {
        final Bitmap response = getBitmap();

        imageView.post(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(response);
            }
        });
    }

    private Bitmap getBitmap()
    {
        Bitmap response =null;
        try {
            response = BitmapHelper.getBitmap(file,false,mImageThumbSize,mImageThumbSize);
        }
        catch (IllegalArgumentException ex)
        {

        }
        return response;
    }
}
