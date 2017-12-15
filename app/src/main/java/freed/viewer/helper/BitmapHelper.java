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
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Video.Thumbnails;

import java.io.File;

import freed.cam.apis.basecamera.modules.I_WorkEvent;
import freed.jni.RawUtils;
import freed.utils.Log;
import freed.utils.StringUtils.FileEnding;
import freed.viewer.holder.FileHolder;

/**
 * Created by troop on 09.03.2016.
 */
public class BitmapHelper
{
    private CacheHelper CACHE;
    private int mImageThumbSizeW;
    private I_WorkEvent done;


    public BitmapHelper(Context context, int mImageThumbSizeW, I_WorkEvent done)
    {
        CACHE = new CacheHelper(context);
        this.mImageThumbSizeW = mImageThumbSizeW;
        this.done = done;
    }

    public void SetWorkDoneListner(I_WorkEvent event)
    {
        this.done = event;
    }

    public Bitmap getBitmap(final FileHolder file, boolean thumb)
    {
        Bitmap response = null;
        try {
            if (CACHE == null)
                return null;

            response = getCacheBitmap(file,thumb);
            if (response == null)
            {
                response = createCacheImage(file.getFile(),thumb);
            }

        } catch (NullPointerException e) {
            Log.WriteEx(e);
        }
        return  response;
    }

    public Bitmap getCacheBitmap(final FileHolder file, boolean thumb)
    {
        Bitmap response = null;
        try {
            if (CACHE == null)
                return null;
            response = null;
            if (thumb)
            {
                if (response == null)
                {
                    response = CACHE.getBitmapFromDiskCache(file.getFile().getName() + "_thumb");
                }
            }
            else
            {
                if (response == null) {
                    response = CACHE.getBitmapFromDiskCache(file.getFile().getName());
                }
            }

        } catch (NullPointerException e) {
            Log.WriteEx(e);
        }
        return  response;
    }

    public  void DeleteCache(File file)
    {
        if (CACHE == null)
            return;
        CACHE.deleteFileFromDiskCache(file.getName());
        CACHE.deleteFileFromDiskCache(file.getName()+"_thumb");
    }

    private Bitmap createCacheImage(File file, boolean thumb)
    {
        Bitmap response = null;
        if (response == null && file.exists())
        {
            if (file.getAbsolutePath().toLowerCase().endsWith(FileEnding.JPG) || file.getAbsolutePath().toLowerCase().endsWith(FileEnding.JPS))
            {
                Options options = new Options();
                options.inSampleSize = 2;
                response = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            }
            else if (file.getAbsolutePath().toLowerCase().endsWith(FileEnding.MP4))
                response = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), Thumbnails.FULL_SCREEN_KIND);
            else if (file.getAbsolutePath().toLowerCase().endsWith(FileEnding.DNG)
                    || file.getAbsolutePath().toLowerCase().endsWith(FileEnding.RAW) || file.getAbsolutePath().toLowerCase().endsWith(FileEnding.BAYER))
            {
                try {
                    response = new RawUtils().UnPackRAW(file.getAbsolutePath());
                }
                catch (IllegalArgumentException ex)
                {
                    Log.WriteEx(ex);

                }catch (UnsatisfiedLinkError ex)
                {
                    Log.WriteEx(ex);

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
        return response;
    }

}

