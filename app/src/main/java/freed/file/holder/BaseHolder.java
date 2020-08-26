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
import freed.viewer.gridview.GridViewFragment;
import freed.viewer.gridview.GridViewFragment.ViewStates;

/**
 * Created by Ingo on 27.12.2015.
 */
public abstract class BaseHolder
{
    protected ViewStates currentstate = GridViewFragment.ViewStates.normal;
    protected EventHandler handler;
    protected boolean selected;
    private String name;
    private long lastmodified;
    private boolean isFolder;
    private boolean isSDCard;

    public BaseHolder(String name, long lastmodified, boolean isFolder,boolean isSDCard)
    {
        this.name = name;
        this.lastmodified = lastmodified;
        this.isFolder = isFolder;
        this.isSDCard = isSDCard;
    }

    public GridViewFragment.ViewStates GetCurrentViewState()
    {
        return currentstate;
    }

    public void SetViewState(GridViewFragment.ViewStates state)
    {
        currentstate = state;
        if (handler != null)
            handler.onViewStateChanged(state);
    }

    public void SetEventListner(EventHandler handler)
    {
        this.handler = handler;
    }

    public void UpdateImage()
    {
        handler.updateImage();
    }

    public interface EventHandler
    {
        void onViewStateChanged(GridViewFragment.ViewStates state);
        void onSelectionChanged(boolean selected);
        void updateImage();
    }

    public boolean IsSelected()
    {
        return selected;
    }
    public void SetSelected(boolean selected)
    {
        this.selected = selected;
        if (handler !=null)
            handler.onSelectionChanged(selected);
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
