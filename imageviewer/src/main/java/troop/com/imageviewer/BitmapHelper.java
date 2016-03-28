package troop.com.imageviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.defcomk.jni.libraw.RawUtils;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;

/**
 * Created by troop on 09.03.2016.
 */
public class BitmapHelper
{
    public static CacheHelper CACHE;

    public static void INIT(Context context)
    {
        CACHE = new CacheHelper(context);
    }

    private BitmapHelper(){};

    public static Bitmap getBitmap(final File file,final boolean thumb,final int mImageThumbSizeW,final int  mImageThumbSizeH)
    {
        if (CACHE == null)
            return null;
        Bitmap response = null;
        if (thumb)
        {
            response = CACHE.getBitmapFromMemCache(file.getName() + "_thumb");
            if (response == null) {
                //Logger.d(TAG,"No image in memory try from disk");
                response = CACHE.getBitmapFromDiskCache(file.getName() + "_thumb");
            }
        }
        else
        {
            response = CACHE.getBitmapFromMemCache(file.getName());
            if (response == null) {
                //Logger.d(TAG,"No image in memory try from disk");
                response = CACHE.getBitmapFromDiskCache(file.getName());
            }
        }
        if (response == null && file.exists())
        {
            if (file.getAbsolutePath().endsWith(StringUtils.FileEnding.JPG) || file.getAbsolutePath().endsWith(StringUtils.FileEnding.JPS))
            {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                response = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            }
            else if (file.getAbsolutePath().endsWith(StringUtils.FileEnding.MP4))
                response = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
            else if (file.getAbsolutePath().endsWith(StringUtils.FileEnding.DNG)
                    || file.getAbsolutePath().endsWith(StringUtils.FileEnding.RAW) || file.getAbsolutePath().endsWith(StringUtils.FileEnding.BAYER))
            {
                try {
                    response = RawUtils.UnPackRAW(file.getAbsolutePath());
                }
                catch (IllegalArgumentException ex)
                {

                }
            }
            if (response != null)
            {
                CACHE.addBitmapToCache(file.getName(), response);
                if (thumb)
                {
                    response = ThumbnailUtils.extractThumbnail(response, mImageThumbSizeW, mImageThumbSizeH);
                    CACHE.addBitmapToCache(file.getName() + "_thumb", response);
                } else
                    CACHE.addBitmapToCache(file.getName() + "_thumb", ThumbnailUtils.extractThumbnail(response, mImageThumbSizeW, mImageThumbSizeH));
            }
        }
        return  response;
    }
}

