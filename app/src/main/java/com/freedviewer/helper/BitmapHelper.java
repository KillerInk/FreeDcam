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

package com.freedviewer.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.ThumbnailUtils;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.provider.MediaStore.Video.Thumbnails;

import com.freedcam.jni.RawUtils;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.FileUtils;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils.FileEnding;
import com.freedviewer.holder.FileHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 09.03.2016.
 */
public class BitmapHelper
{
    public  CacheHelper CACHE;
    private  List<FileEvent> fileListners;
    private  List<FileHolder> files;

    public BitmapHelper(Context context)
    {
        CACHE = new CacheHelper(context);
        fileListners =  new ArrayList<>();

    }

    public void LoadFiles()
    {
        files = FileHolder.getDCIMFiles();
        throwOnFileAdded(files.get(0).getFile());
    }

    public  void DESTROY()
    {
        CACHE = null;
        if (fileListners != null)
            fileListners.clear();
        fileListners = null;
        if (files != null)
            files.clear();
        files = null;
    }

    private BitmapHelper(){}

    public Bitmap getBitmap(File file, boolean thumb, int mImageThumbSizeW, int  mImageThumbSizeH)
    {
        Bitmap response = null;
        try {
            if (CACHE == null)
                return null;
            response = null;
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
                if (file.getAbsolutePath().endsWith(FileEnding.JPG) || file.getAbsolutePath().endsWith(FileEnding.JPS))
                {
                    Options options = new Options();
                    options.inSampleSize = 2;
                    response = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                }
                else if (file.getAbsolutePath().endsWith(FileEnding.MP4))
                    response = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), Thumbnails.FULL_SCREEN_KIND);
                else if (file.getAbsolutePath().endsWith(FileEnding.DNG)
                        || file.getAbsolutePath().endsWith(FileEnding.RAW) || file.getAbsolutePath().endsWith(FileEnding.BAYER))
                {
                    try {
                        response = new RawUtils().UnPackRAW(file.getAbsolutePath());
                    }
                    catch (IllegalArgumentException ex)
                    {
                        Logger.exception(ex);

                    }
                }
                if (response != null && CACHE != null)
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
        } catch (NullPointerException e) {
            e.printStackTrace();
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

    public interface FileEvent
    {
        void onFileDeleted(File file);
        void onFileAdded(File file);
    }

    private void throwOnFileDeleted(File file)
    {
        if (fileListners == null)
            return;
        for (int i= 0; i<fileListners.size(); i++)
        {
            if (fileListners.get(i) !=null)
                fileListners.get(i).onFileDeleted(file);
            else
            {
                fileListners.remove(i);
                i--;
            }
        }
    }

    public boolean DeleteFile(FileHolder file, AppSettingsManager appSettingsManager,Context context) throws NullPointerException
    {
        boolean del = false;
        DeleteCache(file.getFile());
        if (VERSION.SDK_INT <= VERSION_CODES.LOLLIPOP || file.getFile().canWrite())
        {
            del = file.getFile().delete();
        }
        if (!del && VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP)
            del =FileUtils.delteDocumentFile(file.getFile(),appSettingsManager,context);
        if (del)
        {
            if (files != null)
                files.remove(file);
            throwOnFileDeleted(file.getFile());
        }
        return del;
    }

    private void throwOnFileAdded(File file)
    {
        if (fileListners == null)
            return;
        for (int i= 0; i<fileListners.size(); i++)
        {
            if (fileListners.get(i) !=null)
                fileListners.get(i).onFileAdded(file);
            else
            {
                fileListners.remove(i);
                i--;
            }
        }
    }

    public void AddFile(FileHolder file)
    {
        if (files == null)
            return;
        files.add(file);
        throwOnFileAdded(file.getFile());
    }

    public void AddFileListner(FileEvent event)
    {
        if (fileListners == null)
            return;
        else
        {
            if (!fileListners.contains(event))
                fileListners.add(event);
            return;
        }
    }

    public List<FileHolder> getFiles()
    {
        return files;
    }

}

