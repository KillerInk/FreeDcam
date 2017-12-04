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

package freed.utils;

import android.os.Build;
import android.text.TextUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import freed.ActivityAbstract;
import freed.viewer.holder.FileHolder;

/**
 * Created by troop on 01.08.2016.
 */
public class StorageFileManager
{
    public static String freedcamFolder = "/DCIM/FreeDcam/";

    private final String TAG = StorageFileManager.class.getSimpleName();
    private File internalSD;
    private File externalSD;

    public StorageFileManager()
    {
        findSdCards();
    }

    public File getInternalSD()
    {
        return internalSD;
    }

    public File getExternalSD()
    {
        return externalSD;
    }

    public ArrayList<FileHolder> getDCIMDirs()
    {
        ArrayList<FileHolder> subDcimFolders= new ArrayList<>();
        File intdcim = new File(internalSD,StringUtils.DCIMFolder);
        readSubFolders(subDcimFolders,intdcim,false);
        if (externalSD != null)
        {
            File extDcim =  new File(externalSD,StringUtils.DCIMFolder);
            readSubFolders(subDcimFolders,extDcim,true);
        }
        sortFileHolder(subDcimFolders);
        Log.d(TAG,"#############Found DCIM Folders:");
        for (FileHolder fileHolder : subDcimFolders)
            Log.d(TAG,fileHolder.getFile().getAbsolutePath());
        Log.d(TAG,"#############END DCIM Folders:");

        return subDcimFolders;
    }

    public  ArrayList<FileHolder> getFreeDcamDCIMDirsFiles()
    {
        ArrayList<FileHolder> f = new ArrayList<>();
        File intDcimFreed = new File(internalSD, StringUtils.freedcamFolder);
        if (intDcimFreed != null && intDcimFreed.exists())
            readFilesFromFolder(intDcimFreed, f, ActivityAbstract.FormatTypes.all, false);
        if (externalSD != null)
        {
            File extDcimFreed = new File(externalSD, StringUtils.freedcamFolder);
            if (extDcimFreed != null && extDcimFreed.exists())
                readFilesFromFolder(extDcimFreed, f, ActivityAbstract.FormatTypes.all, true);
        }

        sortFileHolder(f);
        return f;
    }

    public String getNewFilePath(boolean externalSd, String fileEnding)
    {
        StringBuilder builder = new StringBuilder();
        if (externalSd && externalSD != null)
            builder.append(externalSD.getAbsolutePath());
        else
            builder.append(internalSD.getAbsolutePath());
        builder.append(freedcamFolder);

        if (fileEnding.equals(".jpg") || fileEnding.equals(".dng") || fileEnding.equals(".jps"))
            builder.append(File.separator).append("IMG_");
        if (fileEnding.equals(".mp4"))
            builder.append(File.separator).append("MOV_");
        builder.append(getStringDatePAttern().format(new Date()))
                .append(fileEnding);
        return builder.toString();
    }

    public String getNewFilePathBurst(boolean externalSd, String fileEnding, int hdrcount)
    {
        StringBuilder builder = new StringBuilder();
        if (externalSd)
            builder.append(externalSD);
        else
            builder.append(internalSD);
        builder.append(freedcamFolder);
        if (fileEnding.equals(".jpg") || fileEnding.equals(".dng") || fileEnding.equals(".jps"))
            builder.append(File.separator).append("IMG_");
        if (fileEnding.equals(".mp4"))
            builder.append(File.separator).append("MOV_");
        Date date = new Date();
        builder.append(getStringDatePAttern().format(date));
        builder.append("_BURST" + hdrcount);
        builder.append(fileEnding);
        return builder.toString();
    }

    public String getNewFilePathHDR(boolean externalSd, String fileEnding, int hdrcount)
    {
        StringBuilder builder = new StringBuilder();
        if (externalSd)
            builder.append(externalSD);
        else
            builder.append(internalSD);
        builder.append(freedcamFolder);
        if (fileEnding.equals(".jpg") || fileEnding.equals(".dng") || fileEnding.equals(".jps"))
            builder.append(File.separator).append("IMG_");
        if (fileEnding.equals(".mp4"))
            builder.append(File.separator).append("MOV_");
        builder.append(getStringDatePAttern().format(new Date()))
                .append("_HDR" + hdrcount)
                .append(fileEnding);
        return builder.toString();
    }

    public String getNewSessionFolderPath(boolean external)
    {
        StringBuilder builder = new StringBuilder();
        if (external)
            builder.append(externalSD.getAbsolutePath());
        else
            builder.append(internalSD.getAbsolutePath());
        builder.append("/DCIM/");
        builder.append(getStringDatePAttern().format(new Date()) + "/");
        return builder.toString();
    }

    public String getNewFileDatedName(String fileending)
    {
        return getStringDatePAttern().format(new Date())+fileending;
    }

    public static SimpleDateFormat getStringDatePAttern()
    {
        return new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    }
    
    public static SimpleDateFormat getStringExifPattern()
    {
        return new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    }

    private void readSubFolders(ArrayList<FileHolder> listToAdd, File folderToRead, boolean external)
    {
        File[] subfolders = folderToRead.listFiles();
        if (subfolders != null)
        {
            for (File f : subfolders)
            {
                if (!f.isHidden() && f.isDirectory())
                    listToAdd.add(new FileHolder(f, external));
            }
        }
    }

    private void findSdCards()
    {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP)
        {
            internalSD = new File(StringUtils.GetInternalSDCARD());
            try {
                externalSD = new File(StringUtils.GetExternalSDCARD());
            }
            catch (Exception ex)
            {
                Log.d(TAG, "No Ext SD!");
            }
        }
        else
        {
            File[] files = getStorageDirectory().listFiles();
            boolean internalfound = false;
            boolean externalfound = false;
            for (File file : files) {
                //first lookup emulated path, for backward compatibility they are mounted too
                //as sdcard1/2 on some devices
                String filename = file.getName();
                if (filename.toLowerCase().equals("emulated")) {
                    internalSD = new File(file.getAbsolutePath() + "/0/");
                    if (internalSD.exists())
                        internalfound = true;
                    File extDcim = new File(file.getAbsolutePath() + "/1/");
                    if (extDcim.exists() && !externalfound) {
                        externalfound = true;
                        externalSD = extDcim;
                    }
                }
                if (filename.toLowerCase().equals("sdcard0") && !internalfound && file.exists()) {
                    internalSD = new File(file.getAbsolutePath());
                    internalfound = true;
                }
                if (filename.toLowerCase().equals("sdcard1") && !externalfound && file.exists()) {
                    File extDcim = new File(file.getAbsolutePath());
                    if (extDcim.exists()) {

                        externalfound = true;
                        externalSD = extDcim;
                    }
                }
                //that is the true sdcard finaly /storage/XXX-XXX/
                if (!filename.toLowerCase().equals("emulated")
                        && !filename.toLowerCase().equals("sdcard0")
                        && !filename.toLowerCase().equals("sdcard1")
                        && !filename.toLowerCase().equals("self")) {
                    if (file.exists()) {
                        externalfound = true;
                        externalSD = file;
                    }
                }
            }
        }
    }

    private  File getStorageDirectory() {
        String path = System.getenv("ANDROID_STORAGE");
        return (path == null || TextUtils.isEmpty(path)) ? new File("/storage") : new File(path);
    }

    private void sortFileHolder(List<FileHolder> f)
    {
        Collections.sort(f, new Comparator<FileHolder>() {
            public int compare(FileHolder f1, FileHolder f2) {
                return Long.valueOf(f2.getFile().lastModified()).compareTo(f1.getFile().lastModified());
            }
        });
    }

    /**
     * reads all files from a folder
     * @param folder to read
     * @param list to fill
     * @param formatsToShow that get added the the list
     * @param external is on external SD
     */
    public void readFilesFromFolder(File folder, List<FileHolder> list, ActivityAbstract.FormatTypes formatsToShow, boolean external) {
        File[] folderfiles = folder.listFiles();
        if (folderfiles == null)
            return;
        for (File f : folderfiles) {
            if (!f.isHidden()) {
                if (formatsToShow == ActivityAbstract.FormatTypes.all && (
                        f.getName().toLowerCase().endsWith(StringUtils.FileEnding.JPG)
                                || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.JPS)
                                || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.RAW)
                                || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.BAYER)
                                || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.DNG)
                                || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.MP4)
                ))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == ActivityAbstract.FormatTypes.dng && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.DNG))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == ActivityAbstract.FormatTypes.raw && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.RAW))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == ActivityAbstract.FormatTypes.raw && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.BAYER))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == ActivityAbstract.FormatTypes.jps && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.JPS))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == ActivityAbstract.FormatTypes.jpg && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.JPG))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == ActivityAbstract.FormatTypes.mp4 && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.MP4))
                    list.add(new FileHolder(f,external));
            }
        }
        sortFileHolder(list);
    }
}
