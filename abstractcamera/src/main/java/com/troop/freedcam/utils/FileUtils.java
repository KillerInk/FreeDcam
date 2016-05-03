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

    public static DocumentFile getExternalSdDocumentFile(AppSettingsManager appSettingsManager)
    {
        DocumentFile sdDir = null;
        if (appSettingsManager != null && appSettingsManager.GetBaseFolder() != null && !appSettingsManager.GetBaseFolder().equals("")) {
            Uri uri = Uri.parse(appSettingsManager.GetBaseFolder());
            sdDir = DocumentFile.fromTreeUri(appSettingsManager.context, uri);
        }
        return sdDir;
    }

    private static DocumentFile getDCIMDocumentFolder(boolean create, AppSettingsManager appSettingsManager) {
        DocumentFile documentFile = null;
        DocumentFile sdDir;
        if ((sdDir = getExternalSdDocumentFile(appSettingsManager)) != null) {
            documentFile = sdDir.findFile("DCIM");
            if (documentFile == null && create)
                documentFile = sdDir.createDirectory("DCIM");
        }
        return documentFile;
    }

    public static DocumentFile getFreeDcamDocumentFolder(AppSettingsManager appSettingsManager)
    {
        DocumentFile dcimfolder;
        DocumentFile freedcamfolder = null;
        if((dcimfolder = getDCIMDocumentFolder(true, appSettingsManager)) !=null)
        {
            freedcamfolder = dcimfolder.findFile("FreeDcam");
            if (freedcamfolder == null && true)
                freedcamfolder = dcimfolder.createDirectory("FreeDcam");
        }
        return freedcamfolder;
    }

    public static boolean delteDocumentFile(File file, AppSettingsManager appSettingsManager) throws NullPointerException
    {
        if (!file.delete()) {
            DocumentFile sdDir = FileUtils.getExternalSdDocumentFile(appSettingsManager);
            if (sdDir == null)
                throw new NullPointerException();
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
        return true;
    }


}
