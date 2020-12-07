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

package freed.file.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.os.ParcelFileDescriptor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import freed.ActivityInterface;
import freed.file.FileListController;
import freed.utils.StringUtils;
import freed.viewer.gridview.enums.ViewStates;
import freed.viewer.gridview.views.GridViewFragment;

/**
 * Created by Ingo on 27.12.2015.
 */
public abstract class BaseHolder
{
    private String name;
    private long lastmodified;
    private boolean isFolder;
    private boolean isSDCard;
    private FileListController.FormatTypes fileformat;

    public BaseHolder(String name, long lastmodified, boolean isFolder,boolean isSDCard)
    {
        this.name = name;
        this.lastmodified = lastmodified;
        this.isFolder = isFolder;
        this.isSDCard = isSDCard;
        if (name.toLowerCase().endsWith(StringUtils.FileEnding.BAYER))
            fileformat = FileListController.FormatTypes.raw;
        if (name.toLowerCase().endsWith(StringUtils.FileEnding.DNG))
            fileformat = FileListController.FormatTypes.dng;
        if (name.toLowerCase().endsWith(StringUtils.FileEnding.RAW))
            fileformat = FileListController.FormatTypes.raw;
        if (name.toLowerCase().endsWith(StringUtils.FileEnding.JPG))
            fileformat = FileListController.FormatTypes.jpg;
        if (name.toLowerCase().endsWith(StringUtils.FileEnding.JPS))
            fileformat = FileListController.FormatTypes.jps;
        if (name.toLowerCase().endsWith(StringUtils.FileEnding.MP4))
            fileformat = FileListController.FormatTypes.mp4;
    }

    public abstract Class getHolderType();


    public FileListController.FormatTypes getFileformat() {
        return fileformat;
    }

    public String getName()
    {
        return name;
    }
    public Long lastModified() {
        return lastmodified;
    }
    public boolean IsFolder()
    {
        return isFolder;
    }
    public boolean isExternalSD() { return isSDCard; }

    public abstract Bitmap getBitmap(Context context, BitmapFactory.Options options);
    public abstract Bitmap getVideoThumb(Context context) throws IOException;
    public abstract Bitmap getBitmapFromDng(Context context) throws IOException;
    public abstract boolean delete(Context context);
    public abstract boolean exists();

    public abstract OutputStream getOutputStream() throws FileNotFoundException;
    public abstract InputStream getInputStream() throws FileNotFoundException;

    public void setToMediaRecorder(MediaRecorder recorder, ActivityInterface activityInterface) throws FileNotFoundException {
        if (this instanceof FileHolder)
        {
            recorder.setOutputFile(((FileHolder)this).getFile().getAbsolutePath());
        }
        else if (this instanceof UriHolder)
        {
            ParcelFileDescriptor fileDescriptor = ((UriHolder)this).getParcelFileDescriptor();
            recorder.setOutputFile(fileDescriptor.getFileDescriptor());
        }
    }
}
