package freed.file;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.documentfile.provider.DocumentFile;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import freed.cam.ActivityFreeDcamMain;
import freed.cam.events.EventBusHelper;
import freed.cam.events.UpdateScreenSlide;
import freed.file.holder.BaseHolder;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.MediaScannerManager;
import freed.utils.StorageFileManager;
import freed.file.holder.FileHolder;

public class FileListController {

    private final String TAG = FileListController.class.getSimpleName();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addFromEventFile(FileHolder fileHolder)
    {
        MediaScannerManager.ScanMedia(context,fileHolder.getFile());
        AddFile(fileHolder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addFromEventFiles(FileHolder[] fileHolder)
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
    }


    public static boolean needStorageAccessFrameWork = true; //Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;

    private List<BaseHolder> files =new ArrayList<>();
    private StorageFileManager storageFileManager;
    private MediaStoreController mediaStoreController;
    private Context context;
    private NotifyFilesChanged notifyFilesChanged;
    private final Object filesLock = new Object();

    public FileListController(Context context)
    {
        this.context = context;
        storageFileManager = new StorageFileManager();
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

    public StorageFileManager getStorageFileManager()
    {
        return storageFileManager;
    }
    public MediaStoreController getMediaStoreController(){return mediaStoreController; }

    public void loadDefaultFiles()
    {
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
            EventBusHelper.post(new UpdateScreenSlide());
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
        Collections.sort(f, (f1, f2) -> Long.valueOf(f2.lastModified()).compareTo(f1.lastModified()));
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

    public DocumentFile getFreeDcamDocumentFolder()
    {
        DocumentFile dcimfolder;
        DocumentFile freedcamfolder = null;
        if((dcimfolder = getDCIMDocumentFolder(true)) !=null)
        {
            freedcamfolder = dcimfolder.findFile("FreeDcam");
            if (freedcamfolder == null)
                freedcamfolder = dcimfolder.createDirectory("FreeDcam");
        }
        return freedcamfolder;
    }

    public boolean DeleteFile(BaseHolder file) {
        return deleteFile(file);
    }

    public void DeleteFiles(List<BaseHolder> files) {
        synchronized (filesLock) {
            for (BaseHolder f : files)
                deleteFile(f);
            if (notifyFilesChanged != null)
                notifyFilesChanged.onFilesChanged();
        }
    }

    private boolean deleteFile(BaseHolder file)
    {
        boolean del = false;

        del = file.delete(context);
        if (del) {
            if (files != null)
                files.remove(file);
        }

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
}
