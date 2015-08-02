package troop.com.themesample.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.I_WorkEvent;
import com.troop.freedcam.ui.I_Activity;

import java.io.File;
import java.io.IOException;

import troop.com.themesample.R;

/**
 * Created by troop on 13.06.2015.
 */
public class ThumbView extends ImageView implements I_WorkEvent, View.OnClickListener
{
    final  String TAG = ThumbView.class.getSimpleName();
    boolean hasWork = false;
    I_Activity i_activity;
    AbstractCameraUiWrapper cameraUiWrapper;
    Bitmap bitmap;
    File lastFile;
    public ThumbView(Context context) {
        super(context);
        this.setOnClickListener(this);
        this.setBackgroundDrawable(context.getResources().getDrawable( troop.com.themesample.R.drawable.thumbnail));
    }

    public ThumbView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.setOnClickListener(this);
        this.setBackgroundDrawable(context.getResources().getDrawable( troop.com.themesample.R.drawable.thumbnail));
    }

    public void INIT(I_Activity i_activity, AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.i_activity = i_activity;
        this.cameraUiWrapper = cameraUiWrapper;
        cameraUiWrapper.moduleHandler.moduleEventHandler.AddWorkFinishedListner(this);
    }

    @Override
    public String WorkHasFinished(final File filePath)
    {
        this.post(new Runnable() {
            @Override
            public void run() {
                if (!hasWork) {
                    hasWork = true;
                    Log.d(TAG, "Load Thumb " + filePath.getName());
                    showThumb(filePath);
                    hasWork = false;
                }
            }
        });
        return null;
    }

    private Bitmap loadThumbViewImage(File file)
    {
        lastFile = file;
        if(file.getAbsolutePath().endsWith("jpg"))
        {
            byte[] thum = null;
            try {
                thum = new ExifInterface(file.getAbsolutePath()).getThumbnail();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (thum != null)
                return BitmapFactory.decodeByteArray(thum, 0, thum.length);

        }
        else if (file.getAbsolutePath().endsWith("mp4"))
        {
            return ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
        }
        return null;
    }

    private void showThumb(File filePath)
    {
        if(filePath != null && !filePath.getAbsolutePath().endsWith(".dng") && !filePath.getAbsolutePath().endsWith(".raw") && filePath.exists())
        {
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
                System.gc();
            }
            bitmap = loadThumbViewImage(filePath);
            this.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onClick(View v)
    {
        i_activity.loadImageViewerFragment(lastFile);


    }
}
