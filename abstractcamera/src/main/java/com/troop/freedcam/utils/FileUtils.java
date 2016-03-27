package com.troop.freedcam.utils;

import android.net.Uri;
import android.support.v4.provider.DocumentFile;

import com.troop.filelogger.Logger;
import com.troop.freedcam.ui.AppSettingsManager;
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

    public static DocumentFile getExternalSdDocumentFile()
    {
        DocumentFile sdDir = null;
        if (!AppSettingsManager.APPSETTINGSMANAGER.GetBaseFolder().equals("")) {
            Uri uri = Uri.parse(AppSettingsManager.APPSETTINGSMANAGER.GetBaseFolder());
            sdDir = DocumentFile.fromTreeUri(AppSettingsManager.APPSETTINGSMANAGER.context, uri);
        }
        return sdDir;
    }

    public static DocumentFile getDCIMDocumentFolder(boolean create) {
        DocumentFile documentFile = null;
        DocumentFile sdDir;
        if ((sdDir = getExternalSdDocumentFile()) != null) {
            documentFile = sdDir.findFile("DCIM");
            if (documentFile == null && create)
                documentFile = sdDir.createDirectory("DCIM");
        }
        return documentFile;
    }

    public static DocumentFile getFreeDcamDocumentFolder(boolean create)
    {
        DocumentFile dcimfolder;
        DocumentFile freedcamfolder = null;
        if((dcimfolder = getDCIMDocumentFolder(create)) !=null)
        {
            freedcamfolder = dcimfolder.findFile("FreeDcam");
            if (freedcamfolder == null && create)
                freedcamfolder = dcimfolder.createDirectory("FreeDcam");
        }
        return freedcamfolder;
    }

    public static boolean delteDocumentFile(File file)
    {
        if (file.delete() == false) {
            DocumentFile sdDir = FileUtils.getExternalSdDocumentFile();
            String baseS = sdDir.getName();
            String fileFolder = file.getAbsolutePath();
            String[] split = fileFolder.split("/");
            DocumentFile tmpdir = null;
            boolean append = false;
            for (int t = 0; t < split.length; t++) {
                if (split[t].equals(baseS) || append) {
                    if (!append) {
                        append = true;
                        tmpdir = sdDir;
                    } else {
                        tmpdir = tmpdir.findFile(split[t]);
                    }
                }
            }
            boolean d = tmpdir.delete();
            Logger.d("delteDocumentFile", "file delted:" + d);
            return d;
        }
        return true;
    }


}
