package com.troop.freedcam.gallery.util;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.troop.freedcam.file.holder.BaseHolder;
import com.troop.freedcam.gallery.R;
import com.troop.freedcam.gallery.helper.BitmapHelper;
import com.troop.freedcam.image.ImageTask;


public class BitmapLoadRunnable extends ImageTask
{
    private final String TAG = BitmapLoadRunnable.class.getSimpleName();
    BaseHolder fileHolder;
    ImageView imageView;
    private final int TAGID = R.id.image_id;

    public BitmapLoadRunnable(BaseHolder fileHolder, ImageView view)
    {
        this.fileHolder = fileHolder;
        this.imageView = view;
    }

    @Override
    public boolean process() {
        try {
            Log.d(TAG, "load file:" + fileHolder.getName());
            final Bitmap bitmap = BitmapHelper.GET().getBitmap(fileHolder, true);
            if (bitmap != null && imageView != null) {
                if (imageView.getTag(TAGID) == null || imageView.getTag(TAGID).equals(fileHolder.getName())) {
                    Log.d(TAG, "set Image To view:" + fileHolder.getName());
                    imageView.post(() ->{
                        if (imageView != null)
                            imageView.setImageBitmap(bitmap);
                    });
                }
                else
                    Log.d(TAG,"same image, already loaded " + fileHolder.getName());
            }
            else if (bitmap == null && imageView != null) {
                Log.d(TAG, "Bitmap failed to load:" +fileHolder.getName());
                imageView.post(() -> {
                    if (imageView != null)
                        imageView.setImageBitmap(null);
                });
            }
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }
        return false;
    }
}
