package com.troop.freecamv2.ui.handler;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.troop.freecam.R;
import com.troop.freecam.manager.MediaScannerManager;
import com.troop.freecamv2.camera.modules.I_WorkEvent;
import com.troop.freecamv2.ui.MainActivity_v2;

import java.io.File;

/**
 * Created by troop on 25.08.2014.
 */
public class ThumbnailHandler implements View.OnClickListener, I_WorkEvent
{
    final MainActivity_v2 activity;
    ImageView thumbView;
    Button delButton;
    File lastFile;

    public ThumbnailHandler(final MainActivity_v2 activity)
    {
        this.activity = activity;
        thumbView = (ImageView)activity.findViewById(R.id.imageView_Thumbnail);
        thumbView.setOnClickListener(this);
        thumbView.setAlpha(0f);

    }

    @Override
    public void onClick(View v) {
        if (lastFile != null)
        {
            Uri uri = Uri.fromFile(lastFile);
            Intent i=new Intent(Intent.ACTION_VIEW);
            if (lastFile.getAbsolutePath().endsWith("mp4"))
                i.setDataAndType(uri, "video/*");
            else
                i.setDataAndType(uri, "image/*");
            activity.startActivity(i);
        }
    }

    @Override
    public String WorkHasFinished(final File filePath)
    {
        thumbView.post(new Runnable() {
            @Override
            public void run() {
                lastFile = filePath;
                if (thumbView.getAlpha() == 1f)
                    hideThumb(filePath);
                else
                    showThumb(filePath);
                MediaScannerManager.ScanMedia(activity, filePath);
            }
        });
        return null;
    }

    private Bitmap loadThumbViewImage(File file)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, thumbView.getWidth(), thumbView.getHeight());
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    private void hideThumb(final File filePath)
    {
        if (thumbView.getAlpha() == 1f)
        {
            thumbView.animate().alpha(0f).setDuration(200).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //thumbView.setVisibility(View.GONE);
                    //thumbView.setImageBitmap(loadThumbViewImage(filePath));

                    showThumb(filePath);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).start();
        }
    }

    private void showThumb(File filePath)
    {
            if(filePath != null && !filePath.getAbsolutePath().endsWith(".dng"))
                thumbView.setImageBitmap(loadThumbViewImage(filePath));
            thumbView.animate().alpha(1f).setDuration(200).start();


    }
}
