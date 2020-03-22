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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import freed.file.FileApiStorageDetector;
import freed.file.FileListController;
import freed.file.holder.BaseHolder;
import freed.file.holder.FileHolder;

/**
 * Created by troop on 01.08.2016.
 */
public class StorageFileManager
{
    public static String freedcamFolder = "/DCIM/FreeDcam/";

    private final String TAG = StorageFileManager.class.getSimpleName();
    private FileApiStorageDetector fileApiStorageDetector;

    public StorageFileManager()
    {
        fileApiStorageDetector = new FileApiStorageDetector();
    }

    public ArrayList<BaseHolder> getDCIMDirs()
    {
        ArrayList<BaseHolder> subDcimFolders= new ArrayList<>();
        File intdcim = new File(fileApiStorageDetector.getInternalSD(),StringUtils.DCIMFolder);
        readSubFolders(subDcimFolders,intdcim,false);
        if (fileApiStorageDetector.getExternalSD() != null)
        {
            File extDcim =  new File(fileApiStorageDetector.getExternalSD(),StringUtils.DCIMFolder);
            readSubFolders(subDcimFolders,extDcim,true);
        }
        sortFileHolder(subDcimFolders);
        Log.d(TAG,"#############Found DCIM Folders:");
        for (BaseHolder fileHolder : subDcimFolders)
            Log.d(TAG,fileHolder.getName());
        Log.d(TAG,"#############END DCIM Folders:");

        return subDcimFolders;
    }

    public  ArrayList<BaseHolder> getFreeDcamDCIMDirsFiles()
    {
        ArrayList<BaseHolder> f = new ArrayList<>();
        File intDcimFreed = new File(fileApiStorageDetector.getInternalSD(), StringUtils.freedcamFolder);
        if (intDcimFreed != null && intDcimFreed.exists())
            readFilesFromFolder(intDcimFreed, f, FileListController.FormatTypes.all, false);
        if (fileApiStorageDetector.getExternalSD() != null)
        {
            File extDcimFreed = new File(fileApiStorageDetector.getExternalSD(), StringUtils.freedcamFolder);
            if (extDcimFreed != null && extDcimFreed.exists())
                readFilesFromFolder(extDcimFreed, f, FileListController.FormatTypes.all, true);
        }

        sortFileHolder(f);
        return f;
    }

    public String getNewFilePath(boolean externalSd, String fileEnding)
    {
        StringBuilder builder = new StringBuilder();
        if (externalSd && fileApiStorageDetector.getExternalSD() != null)
            builder.append(fileApiStorageDetector.getExternalSD().getAbsolutePath());
        else
            builder.append(fileApiStorageDetector.getInternalSD().getAbsolutePath());
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
        appendStorageToPath(externalSd, builder);
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

    private void appendStorageToPath(boolean externalSd, StringBuilder builder) {
        if (externalSd)
            builder.append(fileApiStorageDetector.getExternalSD());
        else
            builder.append(fileApiStorageDetector.getInternalSD());
    }

    public String getNewFilePathHDR(boolean externalSd, String fileEnding, int hdrcount)
    {
        StringBuilder builder = new StringBuilder();
        appendStorageToPath(externalSd, builder);
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
            builder.append(fileApiStorageDetector.getExternalSD().getAbsolutePath());
        else
            builder.append(fileApiStorageDetector.getInternalSD().getAbsolutePath());
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

    private void readSubFolders(ArrayList<BaseHolder> listToAdd, File folderToRead, boolean external)
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

    private void sortFileHolder(List<BaseHolder> f)
    {
        Collections.sort(f, (f1, f2) -> Long.valueOf(f2.lastModified()).compareTo(f1.lastModified()));
    }

    /**
     * reads all files from a folder
     * @param folder to read
     * @param list to fill
     * @param formatsToShow that get added the the list
     * @param external is on external SD
     */
    public void readFilesFromFolder(File folder, List<BaseHolder> list, FileListController.FormatTypes formatsToShow, boolean external) {
        File[] folderfiles = folder.listFiles();
        if (folderfiles == null)
            return;
        for (File f : folderfiles) {
            if (!f.isHidden()) {
                if (formatsToShow == FileListController.FormatTypes.all && (
                        f.getName().toLowerCase().endsWith(StringUtils.FileEnding.JPG)
                                || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.JPS)
                                || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.RAW)
                                || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.BAYER)
                                || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.DNG)
                                || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.MP4)
                ))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == FileListController.FormatTypes.dng && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.DNG))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == FileListController.FormatTypes.raw && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.RAW))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == FileListController.FormatTypes.raw && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.BAYER))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == FileListController.FormatTypes.jps && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.JPS))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == FileListController.FormatTypes.jpg && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.JPG))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == FileListController.FormatTypes.mp4 && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.MP4))
                    list.add(new FileHolder(f,external));
            }
        }
        sortFileHolder(list);
    }
}
