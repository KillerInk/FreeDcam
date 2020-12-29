package freed.file;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.documentfile.provider.DocumentFile;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import freed.cam.events.EventBusHelper;
import freed.cam.events.UpdateScreenSlide;
import freed.file.holder.BaseHolder;
import freed.file.holder.FileHolder;
import freed.file.holder.UriHolder;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.MediaScannerManager;
import freed.utils.StorageFileManager;

public class FileListController {

    private final String TAG = FileListController.class.getSimpleName();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addFromEventFile(BaseHolder fileHolder)
    {
        MediaScannerManager.ScanMedia(context,fileHolder);
        AddFile(fileHolder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addFromEventFiles(BaseHolder[] fileHolder)
    {
        MediaScannerManager.ScanMedia(context,fileHolder);
        AddFiles(fileHolder);
    }

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
        void onFileDeleted(int id);
    }


    public static boolean needStorageAccessFrameWork = Build.VERSION.SDK_INT > Build.VERSION_CODES.P;

    private List<BaseHolder> files =new ArrayList<>();
    private StorageFileManager storageFileManager;
    private MediaStoreController mediaStoreController;
    private Context context;
    private List<NotifyFilesChanged> notifyFilesChangedList;
    private final Object filesLock = new Object();

    public FileListController(Context context)
    {
        this.context = context;
        Log.d(TAG, "needStorageAccessFrameWork " + needStorageAccessFrameWork);
        storageFileManager = new StorageFileManager();
        mediaStoreController = new MediaStoreController(context);
        notifyFilesChangedList = new ArrayList<>();
    }

    public void setNotifyFilesChanged(NotifyFilesChanged notifyFilesChanged)
    {
        if (!notifyFilesChangedList.contains(notifyFilesChanged))
            notifyFilesChangedList.add(notifyFilesChanged);
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
            fireNotifyFilesChanged();
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
            fireNotifyFilesChanged();
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
            fireNotifyFilesChanged();
            EventBusHelper.post(new UpdateScreenSlide());
        }
    }

    public void LoadFolder(BaseHolder fileHolder,FormatTypes types)
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
            fireNotifyFilesChanged();
        }
    }

    private void SortFileHolder(List<BaseHolder> f)
    {
        try {
            Collections.sort(f, (f1, f2) -> Long.valueOf(f2.lastModified()).compareTo(Long.valueOf(f1.lastModified())));
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }
    }

    private File getStorageDirectory() {
        String path = System.getenv("ANDROID_STORAGE");
        return (path == null || TextUtils.isEmpty(path)) ? new File("/storage") : new File(path);
    }

    public static DocumentFile getExternalSdDocumentFile(Context context)
    {
        DocumentFile sdDir = null;
        String extSdFolder =  SettingsManager.getInstance().GetBaseFolder();
        if (extSdFolder == null || TextUtils.isEmpty(extSdFolder))
            return null;
        Uri uri = Uri.parse(extSdFolder);
        sdDir = DocumentFile.fromTreeUri(context, uri);
        return sdDir;
    }

    private DocumentFile getDCIMDocumentFolder(boolean create) {
        DocumentFile documentFile = null;
        DocumentFile sdDir;
        if ((sdDir = getExternalSdDocumentFile(context)) != null) {
            documentFile = sdDir.findFile("DCIM");
            if (documentFile == null && create)
                documentFile = sdDir.createDirectory("DCIM");
        }
        return documentFile;
    }

    private DocumentFile getChoosenDocumentFolder()
    {
        return getExternalSdDocumentFile(context);
    }

    public DocumentFile getFreeDcamDocumentFolder()
    {
       /* DocumentFile dcimfolder;
        DocumentFile freedcamfolder = null;
        if((dcimfolder = getDCIMDocumentFolder(true)) !=null)
        {
            freedcamfolder = dcimfolder.findFile("FreeDcam");
            if (freedcamfolder == null)
                freedcamfolder = dcimfolder.createDirectory("FreeDcam");
        }
        return freedcamfolder;*/
        return getChoosenDocumentFolder();
    }

    public boolean DeleteFile(BaseHolder file) {
        boolean deleted = false;
        for (int i = 0; i< getFiles().size();i++)
        {
            if (getFiles().get(i).getName().equals(file.getName()))
                fireNotifyFilesDeleted(i);
        }
        synchronized (filesLock) {
            deleted = deleteFile(file);
        }
        Log.d(TAG, "delete file: " + file.getName() + " " + deleted);
        if (deleted) {
            getFiles().remove(file);

        }

        return deleted;
    }

    public void DeleteFiles(List<BaseHolder> files) {
        synchronized (filesLock) {
            if (files.get(0) instanceof FileHolder) {
                for (BaseHolder f : files)
                    deleteFile(f);
            }
            else
                mediaStoreController.deleteFiles(files);
            fireNotifyFilesChanged();
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

        del = file.delete(context);
        if (files != null)
            files.remove(file);

        return del;

    }

    public void AddFile(BaseHolder file)
    {
        synchronized (filesLock) {
            files.add(file);
            SortFileHolder(files);
            fireNotifyFilesChanged();
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
            fireNotifyFilesChanged();
        }
    }

    private void fireNotifyFilesChanged() {
        for (NotifyFilesChanged n : notifyFilesChangedList)
            n.onFilesChanged();
    }

    private void fireNotifyFilesDeleted(int id) {
        for (NotifyFilesChanged n : notifyFilesChangedList)
            n.onFileDeleted(id);
    }

    public BaseHolder getNewImgFileHolder(File file)
    {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP&& !SettingsManager.getInstance().GetWriteExternal() && !FileListController.needStorageAccessFrameWork)) {
            checkFileExists(file);
            return new FileHolder(file, SettingsManager.getInstance().GetWriteExternal());
        }
        else if (getFreeDcamDocumentFolder() != null && SettingsManager.getInstance().GetWriteExternal()) {
            DocumentFile df = getExternalSdDocumentFile(context); //getFreeDcamDocumentFolder();
            Log.d(TAG,"Filepath: " + df.getUri());
            DocumentFile wr = df.createFile("image/*", file.getName());
            Log.d(TAG,"Filepath: " + wr.getUri());
            return new UriHolder(wr.getUri(), file.getName(), 0, wr.lastModified(), wr.isDirectory(), SettingsManager.getInstance().GetWriteExternal());
        }
        else {
            Uri uri = mediaStoreController.addImg(file);
            return new UriHolder(uri,file.getName(),Long.valueOf(uri.getLastPathSegment()), System.currentTimeMillis(),false,SettingsManager.getInstance().GetWriteExternal());
        }
    }

    public BaseHolder getNewMovieFileHolder(File file)
    {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP&& !SettingsManager.getInstance().GetWriteExternal() && !FileListController.needStorageAccessFrameWork) {
            checkFileExists(file);
            return new FileHolder(file, SettingsManager.getInstance().GetWriteExternal());
        }
        else if (getFreeDcamDocumentFolder() != null && SettingsManager.getInstance().GetWriteExternal()) {
            DocumentFile df = getFreeDcamDocumentFolder();
            Log.d(TAG,"Filepath: " + df.getUri());
            DocumentFile wr = df.createFile("*/*", file.getName());
            Log.d(TAG,"Filepath: " + wr.getUri());
            Uri uri = wr.getUri();
            return new UriHolder(uri, file.getName(), 0, wr.lastModified(), wr.isDirectory(), SettingsManager.getInstance().GetWriteExternal());
        }
        else {
            Uri uri = mediaStoreController.addMovie(file);
            return new UriHolder(uri,file.getName(),Long.valueOf(uri.getLastPathSegment()), System.currentTimeMillis(),false,SettingsManager.getInstance().GetWriteExternal());
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
                Log.WriteEx(e);
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
