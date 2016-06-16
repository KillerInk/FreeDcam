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

package com.freedcam.ui;

import android.content.Context;
import android.net.Uri;
import android.support.v4.provider.DocumentFile;

import com.AbstractFragmentActivity;
import com.freedcam.utils.AppSettingsManager;
import com.freedviewer.helper.BitmapHelper;
import com.freedviewer.holder.FileHolder;

import java.util.List;

/**
 * Created by troop on 22.03.2015.
 */
public interface I_Activity
{
    void SwitchCameraAPI(String Api);
    void closeActivity();
    void ChooseSDCard(I_OnActivityResultCallback callback);
    interface I_OnActivityResultCallback
    {
        void onActivityResultCallback(Uri uri);
    }

    /**
     * @return all files from /DCIM/FreeDcam from internal and external
     */
    List<FileHolder> getFreeDcamDCIMFiles();

    /**
     * Lists all Folders stored in DCIM on internal and external SD
     * @return folders from DCIM dirs
     */
    List<FileHolder> getDCIMDirs();


    BitmapHelper getBitmapHelper();

    Context getContext();

    AppSettingsManager getAppSettings();

    boolean DeleteFile(FileHolder file);

    void AddFile(FileHolder file);

    void AddFileListner(AbstractFragmentActivity.FileEvent event);

    List<FileHolder> getFiles();

    void LoadDCIMDirs();
    void LoadFreeDcamDCIMDirsFiles();

    void LoadFolder(FileHolder fileHolder, AbstractFragmentActivity.FormatTypes types);

    DocumentFile getFreeDcamDocumentFolder();
    DocumentFile getExternalSdDocumentFile();
}


