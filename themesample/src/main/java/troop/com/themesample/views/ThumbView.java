package troop.com.themesample.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.troop.filelogger.Logger;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.I_WorkEvent;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import troop.com.imageviewer.FileUtils;
import troop.com.imageviewer.ScreenSlideFragment;
import troop.com.imageviewer.gridviewfragments.GridViewFragment;
import troop.com.imageviewer.holder.FileHolder;
import troop.com.themesample.R;
import troop.com.themesample.SampleThemeFragment;

/**
 * Created by troop on 13.06.2015.
 */
public class ThumbView extends ImageView implements I_WorkEvent, View.OnClickListener
{
    private final  String TAG = ThumbView.class.getSimpleName();
    private boolean hasWork = false;
    private AbstractCameraUiWrapper cameraUiWrapper;
    private Bitmap bitmap;
    private File lastFile;
    private Bitmap mask;
    ScreenSlideFragment.I_ThumbClick click;

    public ThumbView(Context context) {
        super(context);
        this.setOnClickListener(this);
        this.setBackgroundDrawable(context.getResources().getDrawable( troop.com.themesample.R.drawable.thumbnail));
    }

    public void SetOnThumBlickListner(ScreenSlideFragment.I_ThumbClick click)
    {
        this.click = click;
    }

    public ThumbView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.setOnClickListener(this);
        this.setBackgroundDrawable(context.getResources().getDrawable(troop.com.themesample.R.drawable.thumbnail));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            List<FileHolder> f = new ArrayList<FileHolder>();
            FileUtils.readFilesFromFolder(new File(StringUtils.GetInternalSDCARD() + StringUtils.freedcamFolder), f, GridViewFragment.FormatTypes.all);
            if (f != null && f.size() > 0)
                WorkHasFinished(f.get(f.size()-1).getFile());
        }
        catch (NullPointerException ex)
        {}

    }

    public void INIT(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        if(cameraUiWrapper != null && cameraUiWrapper.moduleHandler != null && cameraUiWrapper.moduleHandler.moduleEventHandler != null)
            cameraUiWrapper.moduleHandler.moduleEventHandler.AddWorkFinishedListner(this);
        mask = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.maskthumb);

    }

    @Override
    public String WorkHasFinished(final File filePath)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!hasWork) {
                    hasWork = true;
                    Logger.d(TAG, "Load Thumb " + filePath.getName());
                    try {
                        showThumb(filePath);
                    }
                    catch (NullPointerException ex)
                    {}

                    hasWork = false;
                }
            }
        }).start();
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
                Logger.exception(e);
            }
            if (thum != null)
            {
                try {
                    return Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(thum, 0, thum.length), mask.getWidth(), mask.getHeight(), false);
                }
                catch (NullPointerException ex)
                {
                    return null;
                }

            }

        }
        else if (file.getAbsolutePath().endsWith("mp4"))
        {
            return Bitmap.createScaledBitmap(ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND), mask.getWidth(), mask.getHeight(), false);
        }
        return null;
    }

    private void showThumb(final File filePath)
    {
        this.post(new Runnable() {
            @Override
            public void run() {
                click.newImageRecieved(filePath);
            }
        });
        if(filePath != null && !filePath.getAbsolutePath().endsWith(".dng") && !filePath.getAbsolutePath().endsWith(".raw") && filePath.exists())
        {
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
            bitmap = loadThumbViewImage(filePath);
            final Bitmap drawMap = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas drawc = new Canvas(drawMap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            if (bitmap != null && !bitmap.isRecycled())
                drawc.drawBitmap(bitmap, 0, 0, null);
            drawc.drawBitmap(mask, 0, 0, paint);
            //drawc.drawBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.thumbnail),0,0,null);
            paint.setXfermode(null);
            if (bitmap != null && !bitmap.isRecycled())
                bitmap.recycle();
            this.post(new Runnable() {
                @Override
                public void run() {
                    ThumbView.this.setImageBitmap(drawMap);

                }
            });

        }
    }

    @Override
    public void onClick(View v)
    {
        if (click != null)
            click.onThumbClick();


    }
}
