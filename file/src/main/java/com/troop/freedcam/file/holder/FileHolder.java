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

package com.troop.freedcam.file.holder;

import android.content.Context;
import android.content.UriPermission;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import com.troop.freedcam.file.FileListController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by troop on 12.12.2015.
 *
 * This class represent the State of GridImageview when its added or not to Gridview and updates the
 * GridviewItem when its visibile/invisible on screen
 */
public class FileHolder extends BaseHolder
{
    private File file;
    private static final String TAG = FileHolder.class.getSimpleName();
    private String documentUri;

    public FileHolder(Context context,File file, boolean external)
    {
        super(context,file.getName(),file.lastModified(),file.isDirectory(),external);
        this.file = file;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            List<UriPermission> list =  context.getContentResolver().getPersistedUriPermissions();
            if (list != null && list.size() > 0)
            {
                if (list.get(0).isWritePermission())
                    documentUri = list.get(0).getUri().getPath();
            }
        }
    }

    public File getFile()
    {
        return file;
    }

    public FileHolder getParent()
    {
        return new FileHolder(context,file.getParentFile(), isExternalSD());
    }


   /* @Override
    public Bitmap getBitmap(Context context, BitmapFactory.Options options) {
        Bitmap response = null;
        if (file != null)
        {
            response = BitmapFactory.decodeFile(file.getAbsolutePath(),options);
        }
        return response;
    }

    @Override
    public Bitmap getVideoThumb(Context context) {
        Bitmap response = null;
        if (file != null)
            response = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        return response;
    }

    public Bitmap getBitmapFromDng(Context context) throws IOException {
        Bitmap response = null;
        if (file != null)
            response = new RawUtils().UnPackRAW(file.getAbsolutePath());
        return response;
    }*/

    @Override
    public boolean delete() {
        boolean del = false;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP || file.canWrite()) {
            del = file.delete();
        }
        if (!del && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            del = delteDocumentFile(file,documentUri);
        return del;
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public OutputStream getOutputStream() throws FileNotFoundException {
        return new FileOutputStream(file);
    }

    @Override
    public InputStream getInputStream() throws FileNotFoundException {
        return new FileInputStream(file);
    }

    private boolean delteDocumentFile(File file,String documentUri) throws NullPointerException
    {
        if (file.isDirectory())
        {
            File[] files = file.listFiles();
            for (File f : files)
                deletFile(f,documentUri);
            deletFile(file,documentUri);
        }
        else
        {
            Boolean d = deletFile(file,documentUri);
            if (d != null) return d;
        }
        return true;
    }

    private boolean deletFile(File file,String documentUri) {
        if (!file.delete())
        {
            DocumentFile sdDir = FileListController.getExternalSdDocumentFile(context,documentUri);
            if (sdDir == null)
                return false;
            String baseS = sdDir.getName();
            String fileFolder = file.getAbsolutePath();
            String[] split = fileFolder.split("/");
            DocumentFile tmpdir = null;
            boolean append = false;
            for (String aSplit : split) {
                if (aSplit.equals(baseS) || append) {
                    if (!append) {
                        append = true;
                        tmpdir = sdDir;
                    } else {
                        tmpdir = tmpdir.findFile(aSplit);
                    }
                }
            }
            boolean d = false;
            d = !(tmpdir != null && tmpdir.exists()) || tmpdir.delete();
            Log.d("delteDocumentFile", "file delted:" + d);
            return d;
        }
        return true;
    }
}