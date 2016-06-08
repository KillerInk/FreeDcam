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

package com.freedviewer.holder;

import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;
import com.freedcam.utils.StringUtils.FileEnding;
import com.freedviewer.gridview.GridViewFragment;
import com.freedviewer.gridview.GridViewFragment.FormatTypes;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private boolean isFolder = false;
    private boolean isSDCard = false;

    public FileHolder(File file, boolean external)
    {
        this.file = file;
        if (file.isDirectory())
            isFolder =true;
        isSDCard = external;
    }

    public File getFile()
    {
        return file;
    }

    public boolean IsFolder()
    {
        return isFolder;
    }
    public boolean isExternalSD() { return isSDCard; }

    public static void readFilesFromFolder(File folder, List<FileHolder> list, FormatTypes formatsToShow, boolean external) {
        File[] folderfiles = folder.listFiles();
        if (folderfiles == null)
            return;
        for (File f : folderfiles) {
            if (!f.isHidden()) {
                if (formatsToShow == FormatTypes.all && (
                        f.getName().toLowerCase().endsWith(FileEnding.JPG)
                                || f.getName().toLowerCase().endsWith(FileEnding.JPS)
                                || f.getName().toLowerCase().endsWith(FileEnding.RAW)
                                || f.getName().toLowerCase().endsWith(FileEnding.BAYER)
                                || f.getName().toLowerCase().endsWith(FileEnding.DNG)
                                || f.getName().toLowerCase().endsWith(FileEnding.MP4)
                ))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == FormatTypes.dng && f.getName().toLowerCase().endsWith(FileEnding.DNG))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == FormatTypes.raw && f.getName().toLowerCase().endsWith(FileEnding.RAW))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == FormatTypes.raw && f.getName().toLowerCase().endsWith(FileEnding.BAYER))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == FormatTypes.jps && f.getName().toLowerCase().endsWith(FileEnding.JPS))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == FormatTypes.jpg && f.getName().toLowerCase().endsWith(FileEnding.JPG))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == FormatTypes.mp4 && f.getName().toLowerCase().endsWith(FileEnding.MP4))
                    list.add(new FileHolder(f,external));
            }
        }
        SortFileHolder(list);
    }

    public static List<FileHolder> getDCIMFiles()
    {
        List<FileHolder> f = new ArrayList<>();
        if (VERSION.SDK_INT <= VERSION_CODES.LOLLIPOP) {
            File internal = new File(StringUtils.GetInternalSDCARD() + StringUtils.freedcamFolder);
            if (internal != null)
                Logger.d(TAG, "InternalSDPath:" + internal.getAbsolutePath());
            readFilesFromFolder(internal, f, FormatTypes.all, false);
            try {
                File fs = new File(StringUtils.GetExternalSDCARD());
                if (fs != null && fs.exists()) {
                    File external = new File(fs + StringUtils.freedcamFolder);
                    if (external != null && external.exists())
                        Logger.d(TAG, "ExternalSDPath:" + external.getAbsolutePath());
                    else
                        Logger.d(TAG, "No ExternalSDFound");
                    readFilesFromFolder(external, f, FormatTypes.all, true);
                }
            } catch (NullPointerException ex) {
                Logger.e(TAG, "Looks like there is no External SD");
            }
        }
        else
        {
            List<FileHolder> dcims= getDCIMDirs();
            for (FileHolder fileHolder : dcims)
            {
                readFilesFromFolder(fileHolder.getFile(),f, FormatTypes.all, fileHolder.isExternalSD());
            }
        }

        SortFileHolder(f);
        return f;
    }

    public static List<FileHolder> getDCIMDirs()
    {
        ArrayList<FileHolder> list = new ArrayList<>();
        if (VERSION.SDK_INT <= VERSION_CODES.LOLLIPOP)
        {
            File internalSDCIM = new File(StringUtils.GetInternalSDCARD() + StringUtils.DCIMFolder);
            File[] f = internalSDCIM.listFiles();
            if (f != null) {
                for (File aF : f) {
                    if (!aF.isHidden())
                        list.add(new FileHolder(aF, false));
                }
            }
            try {
                File fs = new File(StringUtils.GetExternalSDCARD());
                if (fs != null && fs.exists()) {
                    File externalSDCIM = new File(StringUtils.GetExternalSDCARD() + StringUtils.DCIMFolder);
                    f = externalSDCIM.listFiles();
                    for (File aF : f) {
                        if (!aF.isHidden())
                            list.add(new FileHolder(aF, true));
                    }
                }
            } catch (Exception ex) {
                Logger.d(TAG, "No external SD!");
            }
        }
        else
        {
            File[] files =  getStorageDirectory().listFiles();
            boolean internalfound = false;
            boolean externalfound = false;
            for (File file : files)
            {
                if (file.getName().toLowerCase().equals("emulated"))
                {

                    File intDcim = new File(file.getAbsolutePath()+"/0/" + StringUtils.DCIMFolder);
                    if (intDcim.exists()  && !internalfound) {
                        internalfound = true;
                        list.add(new FileHolder(intDcim, false));
                    }
                    File extDcim = new File(file.getAbsolutePath()+"/1/" + StringUtils.DCIMFolder);
                    if (extDcim.exists() && !externalfound) {
                        externalfound = true;
                        list.add(new FileHolder(extDcim, true));
                    }
                }
                if (file.getName().toLowerCase().equals("sdcard0") && !internalfound)
                {
                    File intDcim = new File(file.getAbsolutePath() + StringUtils.DCIMFolder);
                    internalfound = true;
                    list.add(new FileHolder(intDcim, false));
                }
                if (file.getName().toLowerCase().equals("sdcard1") && !externalfound)
                {
                    File extDcim = new File(file.getAbsolutePath() + StringUtils.DCIMFolder);
                    if (extDcim.exists())
                    {
                        externalfound = true;
                        list.add(new FileHolder(extDcim, true));
                    }
                }
                if (!file.getName().toLowerCase().equals("emulated") && !file.getName().toLowerCase().equals("sdcard0") &&  !file.getName().toLowerCase().equals("sdcard1"))
                {
                    File extDcim = new File(file.getAbsolutePath() + StringUtils.DCIMFolder);
                    if (extDcim.exists())
                    {
                        externalfound = true;
                        list.add(new FileHolder(extDcim, true));
                    }
                }

            }
            ArrayList<FileHolder> subDcimFolders= new ArrayList<>();
            for (FileHolder dcim : list)
            {
                File[] subfolders = dcim.getFile().listFiles();
                if (subfolders != null)
                {
                    for (File f : subfolders)
                    {
                        if (!f.isHidden())
                            subDcimFolders.add(new FileHolder(f, dcim.isExternalSD()));
                    }
                }
            }
            list = subDcimFolders;

        }
        SortFileHolder(list);
        Logger.d(TAG,"#############Found DCIM Folders:");
        for (FileHolder fileHolder : list)
            Logger.d(TAG,fileHolder.getFile().getAbsolutePath());
        Logger.d(TAG,"#############END DCIM Folders:");
        return list;
    }

    private static  File getStorageDirectory() {
        String path = System.getenv("ANDROID_STORAGE");
        return path == null ? new File("/storage") : new File(path);
    }

    private static void SortFileHolder(List<FileHolder> f)
    {
        Collections.sort(f, new Comparator<FileHolder>() {
            public int compare(FileHolder f1, FileHolder f2) {
                return Long.valueOf(f2.getFile().lastModified()).compareTo(f1.getFile().lastModified());
            }
        });
    }
}
