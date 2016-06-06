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

package com.freedcam.utils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.provider.DocumentFile;

import java.io.File;
import java.util.List;


/**
 * Created by Ingo on 13.12.2015.
 */
public class FileUtils
{

    public static void readSubFolderFromFolder(File folder, List<File> folderList) {
        File[] folderfiles = folder.listFiles();
        for (File f : folderfiles) {
            if (f.isDirectory() && !f.isHidden())
                folderList.add(f);
        }
    }

    public static DocumentFile getExternalSdDocumentFile(AppSettingsManager appSettingsManager, Context context)
    {
        DocumentFile sdDir = null;
        if (appSettingsManager != null)
        {
            String extSdFolder =  appSettingsManager.GetBaseFolder();
            Uri uri = Uri.parse(extSdFolder);
            sdDir = DocumentFile.fromTreeUri(context, uri);
        }
        return sdDir;
    }

    private static DocumentFile getDCIMDocumentFolder(boolean create, AppSettingsManager appSettingsManager,Context context) {
        DocumentFile documentFile = null;
        DocumentFile sdDir;
        if ((sdDir = getExternalSdDocumentFile(appSettingsManager,context)) != null) {
            documentFile = sdDir.findFile("DCIM");
            if (documentFile == null && create)
                documentFile = sdDir.createDirectory("DCIM");
        }
        return documentFile;
    }

    public static DocumentFile getFreeDcamDocumentFolder(AppSettingsManager appSettingsManager,Context context)
    {
        DocumentFile dcimfolder;
        DocumentFile freedcamfolder = null;
        if((dcimfolder = getDCIMDocumentFolder(true, appSettingsManager,context)) !=null)
        {
            freedcamfolder = dcimfolder.findFile("FreeDcam");
            if (freedcamfolder == null)
                freedcamfolder = dcimfolder.createDirectory("FreeDcam");
        }
        return freedcamfolder;
    }

    public static boolean delteDocumentFile(File file, AppSettingsManager appSettingsManager,Context context) throws NullPointerException
    {
        if (file.isDirectory())
        {
            File[] files = file.listFiles();
            for (File f : files)
                deletFile(f, appSettingsManager, context);
            deletFile(file, appSettingsManager, context);
        }
        else
        {
            Boolean d = deletFile(file, appSettingsManager, context);
            if (d != null) return d;
        }
        return true;
    }

    @Nullable
    private static Boolean deletFile(File file, AppSettingsManager appSettingsManager, Context context) {
        if (!file.delete())
        {
            DocumentFile sdDir = FileUtils.getExternalSdDocumentFile(appSettingsManager,context);
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
            Logger.d("delteDocumentFile", "file delted:" + d);
            return d;
        }
        return null;
    }


}
