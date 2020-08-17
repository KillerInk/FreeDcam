/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.viewer.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.media.ThumbnailUtils;

import java.io.FileNotFoundException;
import java.io.IOException;

import freed.file.holder.BaseHolder;
import freed.utils.Log;
import freed.utils.StringUtils.FileEnding;

/**
 * Created by troop on 09.03.2016.
 */
public class BitmapHelper
{
    private static final String TAG = BitmapHelper.class.getSimpleName();
    private CacheHelper CACHE;
    private int mImageThumbSizeW;
    private Context context;


    public BitmapHelper(Context context, int mImageThumbSizeW)
    {
        this.context = context;
        CACHE = new CacheHelper(context);
        this.mImageThumbSizeW = mImageThumbSizeW;
    }

    public Bitmap getBitmap(final BaseHolder file, boolean thumb)
    {
        Bitmap response = null;
        try {
            if (CACHE == null)
                return null;

            response = getCacheBitmap(file,thumb);
            if (response == null)
            {
                response = createCacheImage(file,thumb);
            }

        } catch (NullPointerException e) {
            Log.WriteEx(e);
        }
        return  response;
    }

    public Bitmap getCacheBitmap(final BaseHolder file, boolean thumb)
    {

        Bitmap response = null;

        try {
            if (CACHE == null)
                return null;
            if (!file.exists()) {
                response = CACHE.getBitmapFromDiskCache(file.getName() + "_thumb");
                if (response != null)
                    DeleteCache(file.getName());
                return null;
            }
            if (thumb)
            {
                if (response == null)
                {
                    response = CACHE.getBitmapFromDiskCache(file.getName() + "_thumb");
                }
            }
            else
            {
                if (response == null) {
                    response = CACHE.getBitmapFromDiskCache(file.getName());
                }
            }


        } catch (NullPointerException e) {
            Log.WriteEx(e);
        }
        return  response;
    }

    public  void DeleteCache(String name)
    {
        if (CACHE == null)
            return;
        CACHE.deleteFileFromDiskCache(name);
        CACHE.deleteFileFromDiskCache(name+"_thumb");
    }

    private Bitmap createCacheImage(BaseHolder file, boolean thumb)
    {
        Bitmap response = null;
        if (!file.exists())
            return response;
        if (response == null && file.getName() !=null)
        {
            if (file.getName().toLowerCase().endsWith(FileEnding.JPG) ||
                    file.getName().toLowerCase().endsWith(FileEnding.JPS)
                    )
            {
                Options options = new Options();
                options.inSampleSize = 2;
                response = file.getBitmap(context,options);
            }
            else if (file.getName().toLowerCase().endsWith(FileEnding.MP4)) {
                try {
                    response = file.getVideoThumb(context);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //
            }
            else if (file.getName().toLowerCase().endsWith(FileEnding.DNG)
                    || file.getName().toLowerCase().endsWith(FileEnding.RAW) || file.getName().toLowerCase().endsWith(FileEnding.BAYER))
            {

                try {
                    response = file.getBitmapFromDng(context);
                }
                catch (IllegalArgumentException ex)
                {
                    Log.WriteEx(ex);

                }catch (UnsatisfiedLinkError ex)
                {
                    Log.WriteEx(ex);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (response != null && CACHE != null)
            {
                CACHE.addBitmapToCache(file.getName(), response);
                Bitmap thumbbitmap = ThumbnailUtils.extractThumbnail(response, mImageThumbSizeW, mImageThumbSizeW);
                CACHE.addBitmapToCache(file.getName() + "_thumb", thumbbitmap);
                if (thumb) {
                    response.recycle();
                    response = thumbbitmap;
                }
                else
                    thumbbitmap.recycle();

            }
        }
        else
        {
            Log.e(TAG, "failed to get file name");
        }
        return response;
    }

}

