package com.troop.freedcam.gallery.model;

import android.util.Log;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.troop.freedcam.file.holder.BaseHolder;
import com.troop.freedcam.gallery.R;
import com.troop.freedcam.gallery.util.BitmapLoadRunnable;
import com.troop.freedcam.image.ImageManager;


public class LoadImageModel
{

    private static String TAG = LoadImageModel.class.getSimpleName();

    @BindingAdapter("loadBitmap")
    public static void loadImage(ImageView view, BaseHolder image) {
        Log.d(TAG,"view tag:" + view.getTag(R.id.image_id) + " file:" + image.getName());
        if (view.getTag() != null && view.getTag().equals(image.getName()))
            return;
        view.setImageBitmap(null);
        view.setTag(R.id.image_id, image.getName());
        BitmapLoadRunnable bitmapLoadRunnable = new BitmapLoadRunnable(image, view);
        ImageManager.putImageLoadTask(bitmapLoadRunnable);
       /* if (view.getTag(R.id.image_id) == null || !view.getTag(R.id.image_id).equals(image.getName())) {

        }
        else
        {
            view.setImageBitmap(null);
            view.setTag(R.id.image_id, null);
        }*/
    }
}
