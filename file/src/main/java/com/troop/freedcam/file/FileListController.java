package com.troop.freedcam.file;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.documentfile.provider.DocumentFile;

import com.troop.freedcam.file.holder.BaseHolder;
import com.troop.freedcam.file.holder.FileHolder;
import com.troop.freedcam.file.holder.UriHolder;
import com.troop.freedcam.utils.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FileListController {

    private final String TAG = FileListController.class.getSimpleName();

    public enum FormatTypes
    {
        all,
        raw,
        dng,
        jpg,
        jps,
        mp4,
    }


    public interface NotifyFilesChanged
    {
        void onFilesChanged();
    }


    public static boolean needStorageAccessFrameWork = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;

    private List<BaseHolder> files =new ArrayList<>();
    private StorageFileManager storageFileManager;
    private MediaStoreController mediaStoreController;
    private Context context;
    private NotifyFilesChanged notifyFilesChanged;
    private final Object filesLock = new Object();

    public FileListController(Context context)
    {
        this.context = context;
        Log.d(TAG, "needStorageAccessFrameWork " + needStorageAccessFrameWork);
        storageFileManager = new StorageFileManager(context);
        mediaStoreController = new MediaStoreController(context);
    }

    public void setNotifyFilesChanged(NotifyFilesChanged notifyFilesChanged)
    {
        this.notifyFilesChanged = notifyFilesChanged;
    }

    public List<BaseHolder> getFiles()
    {
        return files;
    }

   /* public StorageFileManager getStorageFileManager()
    {
        return storageFileManager;
    }
    public MediaStoreController getMediaStoreController(){return mediaStoreController; }*/

    public void loadDefaultFiles()
    {
        try {
            Log.d(TAG, "loadDefaultFiles needStorageAccessFrameWork:" + needStorageAccessFrameWork);
            if (!needStorageAccessFrameWork)
                LoadDCIMDirs();
            else
                files = mediaStoreController.getFolders();
            SortFileHolder(files);
            Log.d(TAG, "loadDefaultFiles found Files:" + files.size());
            if (notifyFilesChanged != null)
                notifyFilesChanged.onFilesChanged();
        }
        catch (SecurityException ex)
        {
            Log.e(TAG, ex.getMessage());
            Log.WriteEx(ex);
        }
    }

    /**
     * Loads all Folders from DCIM dir from internal and external SD
     */
    private void LoadDCIMDirs()
    {
        Log.d(TAG, "LoadDCIMDirs needStorageAccessFrameWork" + needStorageAccessFrameWork);
        synchronized (filesLock) {
            if (!needStorageAccessFrameWork) {
                files.clear();
                files = storageFileManager.getDCIMDirs();
            }
            if (notifyFilesChanged != null)
                notifyFilesChanged.onFilesChanged();
        }
    }

    public void LoadFreeDcamDCIMDirsFiles() {
        synchronized (filesLock) {
            if (!needStorageAccessFrameWork) {
                files = storageFileManager.getFreeDcamDCIMDirsFiles();
            }
            else
            {
                files = mediaStoreController.getFilesFromFolder("FreeDcam");
            }
            SortFileHolder(files);
            if (notifyFilesChanged != null)
                notifyFilesChanged.onFilesChanged();
        }
    }

    public void LoadFolder(BaseHolder fileHolder,FormatTypes types )
    {
        Log.d(TAG, "LoadFolder needStorageAccessFrameWork" + needStorageAccessFrameWork);
        synchronized (filesLock) {
            if (!needStorageAccessFrameWork) {
                files.clear();
                storageFileManager.readFilesFromFolder(((FileHolder)fileHolder).getFile(), files, types, fileHolder.isExternalSD());
            }
            else
            {
                files.clear();
                List<BaseHolder> tmplist =new ArrayList<>();
                tmplist = mediaStoreController.getFilesFromFolder(fileHolder.getName());
                if (types != FormatTypes.all) {
                    for (BaseHolder fh : tmplist) {
                        if (fh.getName() != null) {
                            if (fh.getName().endsWith("jpg") && types == FormatTypes.jpg)
                                files.add(fh);
                            if (fh.getName().endsWith("jps") && types == FormatTypes.jps)
                                files.add(fh);
                            if (fh.getName().endsWith("dng") && types == FormatTypes.dng)
                                files.add(fh);
                            if (fh.getName().endsWith("bayer") && types == FormatTypes.raw)
                                files.add(fh);
                            if (fh.getName().endsWith("mp4") && types == FormatTypes.mp4)
                                files.add(fh);
                        }
                    }
                }
                else
                    files = tmplist;
                SortFileHolder(files);
            }
            if (notifyFilesChanged != null)
                notifyFilesChanged.onFilesChanged();
        }
    }

    private void SortFileHolder(List<BaseHolder> f)
    {
        try {
            Collections.sort(f, (f1, f2) -> Long.valueOf(f2.lastModified()).compareTo(Long.valueOf(f1.lastModified())));
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }
    }

    private File getStorageDirectory() {
        String path = System.getenv("ANDROID_STORAGE");
        return (path == null || TextUtils.isEmpty(path)) ? new File("/storage") : new File(path);
    }

    public static DocumentFile getExternalSdDocumentFile(Context context,String extSdFolderUri)
    {
        DocumentFile sdDir = null;
        String extSdFolder =  extSdFolderUri;
        if (extSdFolder == null || TextUtils.isEmpty(extSdFolder))
            return null;
        Uri uri = Uri.parse(extSdFolder);
        sdDir = DocumentFile.fromTreeUri(context, uri);
        return sdDir;
    }

    private DocumentFile getDCIMDocumentFolder(boolean create,String documentfileuri) {
        DocumentFile documentFile = null;
        DocumentFile sdDir;
        if ((sdDir = getExternalSdDocumentFile(context,documentfileuri)) != null) {
            documentFile = sdDir.findFile("DCIM");
            if (documentFile == null && create)
                documentFile = sdDir.createDirectory("DCIM");
        }
        return documentFile;
    }

    public DocumentFile getFreeDcamDocumentFolder(String documentfileuri)
    {
        DocumentFile dcimfolder;
        DocumentFile freedcamfolder = null;
        if((dcimfolder = getDCIMDocumentFolder(true,documentfileuri)) !=null)
        {
            freedcamfolder = dcimfolder.findFile("FreeDcam");
            if (freedcamfolder == null)
                freedcamfolder = dcimfolder.createDirectory("FreeDcam");
        }
        return freedcamfolder;
    }

    public boolean DeleteFile(BaseHolder file) {
        boolean deleted = false;
        synchronized (filesLock) {
            deleted = deleteFile(file);
        }
        Log.d(TAG, "delete file: " + file.getName() + " " + deleted);
        return deleted;
    }

    public void DeleteFiles(List<BaseHolder> files) {
        synchronized (filesLock) {
            for (BaseHolder f : files)
                deleteFile(f);
            if (notifyFilesChanged != null)
                notifyFilesChanged.onFilesChanged();
        }
    }

    public BaseHolder findFile(String name)
    {
        if (files != null)
        {
            for (BaseHolder f : files)
                if (f.getName().equals(name))
                    return f;
        }
        return null;
    }

    private boolean deleteFile(BaseHolder file)
    {
        boolean del = false;

        del = file.delete();
        if (files != null)
            files.remove(file);

        return del;

    }

    public void AddFile(BaseHolder file)
    {
        synchronized (filesLock) {
            files.add(file);
            SortFileHolder(files);
            if (notifyFilesChanged != null)
                notifyFilesChanged.onFilesChanged();
        }

    }

    public void AddFiles(BaseHolder[] fil)
    {
        synchronized (filesLock) {
            for (BaseHolder fh : fil)
            {
                if (fh != null)
                    files.add(fh);
            }
            SortFileHolder(files);
            if (notifyFilesChanged != null)
                notifyFilesChanged.onFilesChanged();
        }
    }

    public BaseHolder getNewImgFileHolder(File file,boolean externalSD, String documentfileUir)
    {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP&& !externalSD && !FileListController.needStorageAccessFrameWork)) {
            checkFileExists(file);
            return new FileHolder(context,file, externalSD);
        }
        else if (getFreeDcamDocumentFolder(documentfileUir) != null && externalSD) {
            DocumentFile df = getExternalSdDocumentFile(context,documentfileUir); //getFreeDcamDocumentFolder();
            Log.d(TAG,"Filepath: " + df.getUri());
            DocumentFile wr = df.createFile("image/*", file.getName());
            Log.d(TAG,"Filepath: " + wr.getUri());
            return new UriHolder(context,wr.getUri(), file.getName(), 0, wr.lastModified(), wr.isDirectory(), externalSD);
        }
        else {
            Uri uri = mediaStoreController.addImg(file);
            return new UriHolder(context,uri,file.getName(),Long.valueOf(uri.getLastPathSegment()), System.currentTimeMillis(),false,externalSD);
        }
    }

    public BaseHolder getNewMovieFileHolder(File file, boolean externalSD,String documentfileuri)
    {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP&& !externalSD && !FileListController.needStorageAccessFrameWork) {
            checkFileExists(file);
            return new FileHolder(context,file, externalSD);
        }
        else if (getFreeDcamDocumentFolder(documentfileuri) != null && externalSD) {
            DocumentFile df = getFreeDcamDocumentFolder(documentfileuri);
            Log.d(TAG,"Filepath: " + df.getUri());
            DocumentFile wr = df.createFile("*/*", file.getName());
            Log.d(TAG,"Filepath: " + wr.getUri());
            return new UriHolder(context,wr.getUri(), file.getName(), Long.valueOf(wr.getUri().getLastPathSegment()), wr.lastModified(), wr.isDirectory(), externalSD);
        }
        else {
            Uri uri = mediaStoreController.addMovie(file);
            return new UriHolder(context,uri,file.getName(),Long.valueOf(uri.getLastPathSegment()), System.currentTimeMillis(),false,externalSD);
        }
    }

    private void checkFileExists(File fileName)
    {
        if (fileName == null)
            return;
        if (fileName.getParentFile() == null)
            return;
        if(!fileName.getParentFile().exists()) {
            fileName.getParentFile().mkdirs();
            fileName.getParentFile().mkdir();
        }
        if (!fileName.exists())
            try {
                fileName.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public String getNewFilePath(boolean external, String fileending)
    {
        if (needStorageAccessFrameWork)
            return mediaStoreController.getNewFilePath(external,fileending);
        else
            return storageFileManager.getNewFilePath(external,fileending);
    }

    public String getNewFilePathBurst(boolean external, String fileending, int count)
    {
        if (needStorageAccessFrameWork)
            return mediaStoreController.getNewFilePathBurst(external,fileending,count);
        else
            return storageFileManager.getNewFilePathBurst(external,fileending,count);
    }


}
