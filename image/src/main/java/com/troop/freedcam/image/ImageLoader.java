package com.troop.freedcam.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Size;

import java.io.File;
import java.io.IOException;

import freed.jni.RawUtils;

public class ImageLoader {

    public static Bitmap getBitmap(Context context, BitmapFactory.Options options, File file) {
        Bitmap response = null;
        if (file != null)
        {
            response = BitmapFactory.decodeFile(file.getAbsolutePath(),options);
        }
        return response;
    }

    public static Bitmap getVideoThumb(Context context,File file) {
        Bitmap response = null;
        if (file != null)
            response = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        return response;
    }

    public static Bitmap getBitmapFromDng(Context context, File file) throws IOException {
        Bitmap response = null;
        if (file != null)
            response = new RawUtils().UnPackRAW(file.getAbsolutePath());
        return response;
    }

    public static Bitmap getBitmap(Context context, BitmapFactory.Options options, Uri mediaStoreUri) {
        Bitmap response = null;
        if (mediaStoreUri != null){
            try (ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(mediaStoreUri, "r")) {
                response = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, options);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    public static Bitmap getVideoThumb(Context context, Uri mediaStoreUri, long ID) throws IOException {
        Bitmap response = null;
        if (mediaStoreUri != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            response = context.getContentResolver().loadThumbnail(mediaStoreUri,new Size(512, 384),null);
        else if (mediaStoreUri != null)
            response = MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(),ID, MediaStore.Images.Thumbnails.MINI_KIND, null );
        return response;
    }

    public static Bitmap getBitmapFromDng(Context context, Uri mediaStoreUri) throws IOException {
        Bitmap response = null;
        if(mediaStoreUri != null) {
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(mediaStoreUri, "r");
            response = new RawUtils().UnPackRAWFD(pfd.getFd());
            pfd.close();
        }
        return response;
    }
}
