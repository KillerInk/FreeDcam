package troop.com.themesample.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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
    Bitmap mask;
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
        mask = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.maskthumb);
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
                return Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(thum, 0, thum.length), mask.getWidth(), mask.getHeight(), false);

        }
        else if (file.getAbsolutePath().endsWith("mp4"))
        {
            return Bitmap.createScaledBitmap(ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND), mask.getWidth(), mask.getHeight(), false);
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
            Bitmap drawMap = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas drawc = new Canvas(drawMap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            drawc.drawBitmap(bitmap, 0, 0, null);
            drawc.drawBitmap(mask, 0, 0, paint);
            drawc.drawBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.thumbnail),0,0,null);
            paint.setXfermode(null);
            bitmap.recycle();
            this.setImageBitmap(drawMap);
        }
    }

    @Override
    public void onClick(View v)
    {
        i_activity.loadImageViewerFragment(lastFile);


    }
}
