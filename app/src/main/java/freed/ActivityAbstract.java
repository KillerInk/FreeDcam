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

package freed;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.provider.DocumentFile;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.WindowManager.LayoutParams;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import freed.cam.ui.handler.MediaScannerManager;
import freed.utils.AppSettingsManager;
import freed.utils.DeviceUtils;
import freed.utils.Logger;
import freed.utils.StringUtils;
import freed.viewer.helper.BitmapHelper;
import freed.viewer.holder.FileHolder;

/**
 * Created by troop on 28.03.2016.
 */
public abstract class ActivityAbstract extends FragmentActivity implements ActivityInterface {

    public enum FormatTypes
    {
        all,
        raw,
        dng,
        jpg,
        jps,
        mp4,
    }

    public interface FileEvent
    {
        void onFileDeleted(File file);
        void onFileAdded(File file);
    }

    private final String TAG = ActivityAbstract.class.getSimpleName();
    protected AppSettingsManager appSettingsManager;
    protected BitmapHelper bitmapHelper;
    protected  List<FileHolder> files;
    private  List<FileEvent> fileListners;


    private final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "createHandlers()");
        appSettingsManager = new AppSettingsManager(getApplicationContext());
        bitmapHelper =new BitmapHelper(getApplicationContext());
        fileListners =  new ArrayList<>();
        if (appSettingsManager.getDevice() == null)
            appSettingsManager.SetDevice(new DeviceUtils().getDevice(getResources()));
        HIDENAVBAR();
    }

   @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            HIDENAVBAR();
    }

    @Override
    protected void onPause()
    {
        try {
            appSettingsManager.SaveAppSettings();
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }
        super.onPause();
    }

    private void HIDENAVBAR()
    {
        if (VERSION.SDK_INT < 16) {
            getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN,
                    LayoutParams.FLAG_FULLSCREEN);
        }
        else
        {
            //HIDE nav and action bar
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(flags);
            decorView.setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if (visibility > 0) {
                        if (VERSION.SDK_INT >= 16)
                            getWindow().getDecorView().setSystemUiVisibility(flags);
                    }
                }
            });
        }
    }


    private I_OnActivityResultCallback resultCallback;

    @Override
    public void SwitchCameraAPI(String Api) {

    }

    @Override
    public void closeActivity() {

    }

    @Override
    public void ChooseSDCard(I_OnActivityResultCallback callback)
    {
        resultCallback = callback;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    private final int READ_REQUEST_CODE = 42;

    @TargetApi(VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                // Check for the freshest data.


                getContentResolver().takePersistableUriPermission(uri,takeFlags);
                appSettingsManager.SetBaseFolder(uri.toString());
                if (resultCallback != null)
                {
                    resultCallback.onActivityResultCallback(uri);
                    resultCallback = null;
                }
            }
        }
    }

    /**
     * @return all files from /DCIM/FreeDcam from internal and external
     */
    private List<FileHolder> getFreeDcamDCIMFiles() {
        List<FileHolder> f = new ArrayList<>();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            File internal = new File(StringUtils.GetInternalSDCARD() + StringUtils.freedcamFolder);
            if (internal != null)
                Logger.d(TAG, "InternalSDPath:" + internal.getAbsolutePath());
            readFilesFromFolder(internal, f,FormatTypes.all, false);
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

    /**
     * Lists all Folders stored in DCIM on internal and external SD
     *
     * @return folders from DCIM dirs
     */
    private List<FileHolder> getDCIMDirs() {
        ArrayList<FileHolder> list = new ArrayList<>();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP)
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
            File[] files = getStorageDirectory().listFiles();
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

    /**
     * reads all files from a folder
     * @param folder to read
     * @param list to fill
     * @param formatsToShow that get added the the list
     * @param external is on external SD
     */
    private void readFilesFromFolder(File folder, List<FileHolder> list, FormatTypes formatsToShow, boolean external) {
        File[] folderfiles = folder.listFiles();
        if (folderfiles == null)
            return;
        for (File f : folderfiles) {
            if (!f.isHidden()) {
                if (formatsToShow == FormatTypes.all && (
                        f.getName().toLowerCase().endsWith(StringUtils.FileEnding.JPG)
                                || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.JPS)
                                || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.RAW)
                                || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.BAYER)
                                || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.DNG)
                                || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.MP4)
                ))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == FormatTypes.dng && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.DNG))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == FormatTypes.raw && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.RAW))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == FormatTypes.raw && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.BAYER))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == FormatTypes.jps && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.JPS))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == FormatTypes.jpg && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.JPG))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == FormatTypes.mp4 && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.MP4))
                    list.add(new FileHolder(f,external));
            }
        }
        SortFileHolder(list);
    }

    @Override
    public BitmapHelper getBitmapHelper() {
        return bitmapHelper;
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public AppSettingsManager getAppSettings() {
        return appSettingsManager;
    }

    @Override
    public boolean DeleteFile(FileHolder file) {
        return deleteFile(file);
    }

    @Override
    public void DeleteFiles(List<FileHolder> files) {
        for (FileHolder f : files)
            deleteFile(f);
    }

    private boolean deleteFile(FileHolder file)
    {
        boolean del = false;
        bitmapHelper.DeleteCache(file.getFile());
        if (VERSION.SDK_INT <= VERSION_CODES.LOLLIPOP || file.getFile().canWrite())
        {
            del = file.getFile().delete();
        }
        if (!del && VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP)
            del = delteDocumentFile(file.getFile());
        if (del)
        {
            if (files != null)
                files.remove(file);
            throwOnFileDeleted(file.getFile());
        }
        MediaScannerManager.ScanMedia(getContext(), file.getFile());
        return del;
    }

    public void AddFile(FileHolder file)
    {
        if (files == null)
            return;
        files.add(file);
        SortFileHolder(files);
        throwOnFileAdded(file.getFile());
    }

    public void AddFileListner(FileEvent event)
    {
        if (fileListners == null)
            return;
        else
        {
            if (!fileListners.contains(event))
                fileListners.add(event);
            return;
        }
    }

    @Override
    public List<FileHolder> getFiles() {
        return this.files;
    }

    /**
     * Loads the files stored from that folder
     * @param fileHolder the folder to lookup
     * @param types the file format to load
     */
    @Override
    public void LoadFolder(FileHolder fileHolder,FormatTypes types )
    {
        files.clear();
        readFilesFromFolder(fileHolder.getFile(),files,types,fileHolder.isExternalSD());
    }

    /**
     * Loads all Folders from DCIM dir from internal and external SD
     */
    @Override
    public void LoadDCIMDirs()
    {
        if (files != null)
            files.clear();
        files = getDCIMDirs();
    }

    /**
     * Loads all files stored in DCIM/FreeDcam from internal and external SD
     */
    @Override
    public void LoadFreeDcamDCIMDirsFiles() {
        if (files != null)
            files.clear();
        files = getFreeDcamDCIMFiles();
    }

    private void SortFileHolder(List<FileHolder> f)
    {
        Collections.sort(f, new Comparator<FileHolder>() {
            public int compare(FileHolder f1, FileHolder f2) {
                return Long.valueOf(f2.getFile().lastModified()).compareTo(f1.getFile().lastModified());
            }
        });
    }

    private  File getStorageDirectory() {
        String path = System.getenv("ANDROID_STORAGE");
        return path == null ? new File("/storage") : new File(path);
    }

    @Override
    public DocumentFile getExternalSdDocumentFile()
    {
        DocumentFile sdDir = null;
        String extSdFolder =  appSettingsManager.GetBaseFolder();
        if (extSdFolder == null || extSdFolder.equals(""))
            return null;
        Uri uri = Uri.parse(extSdFolder);
        sdDir = DocumentFile.fromTreeUri(getContext(), uri);
        return sdDir;
    }

    private DocumentFile getDCIMDocumentFolder(boolean create) {
        DocumentFile documentFile = null;
        DocumentFile sdDir;
        if ((sdDir = getExternalSdDocumentFile()) != null) {
            documentFile = sdDir.findFile("DCIM");
            if (documentFile == null && create)
                documentFile = sdDir.createDirectory("DCIM");
        }
        return documentFile;
    }

    @Override
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

    private boolean delteDocumentFile(File file) throws NullPointerException
    {
        if (file.isDirectory())
        {
            File[] files = file.listFiles();
            for (File f : files)
                deletFile(f);
            deletFile(file);
        }
        else
        {
            Boolean d = deletFile(file);
            if (d != null) return d;
        }
        return true;
    }

    @Nullable
    private boolean deletFile(File file) {
        if (!file.delete())
        {
            DocumentFile sdDir = getExternalSdDocumentFile();
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
        return true;
    }

    private void throwOnFileDeleted(File file)
    {
        if (fileListners == null)
            return;
        for (int i = 0; i< fileListners.size(); i++)
        {
            if (fileListners.get(i) !=null)
                fileListners.get(i).onFileDeleted(file);
            else
            {
                fileListners.remove(i);
                i--;
            }
        }
    }

    private void throwOnFileAdded(File file)
    {
        if (fileListners == null)
            return;
        for (int i = 0; i< fileListners.size(); i++)
        {
            if (fileListners.get(i) !=null)
                fileListners.get(i).onFileAdded(file);
            else
            {
                fileListners.remove(i);
                i--;
            }
        }
    }

    @Override
    public void DisablePagerTouch(boolean disable)
    {

    }
}
