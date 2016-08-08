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

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.provider.DocumentFile;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.WindowManager.LayoutParams;

import com.troop.freedcam.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import freed.cam.apis.basecamera.modules.I_WorkEvent;
import freed.cam.ui.handler.MediaScannerManager;
import freed.utils.AppSettingsManager;
import freed.utils.DeviceUtils;
import freed.utils.Logger;
import freed.utils.StorageFileHandler;
import freed.viewer.helper.BitmapHelper;
import freed.viewer.holder.FileHolder;

/**
 * Created by troop on 28.03.2016.
 */
public abstract class ActivityAbstract extends FragmentActivity implements ActivityInterface, I_WorkEvent {

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
    private AppSettingsManager appSettingsManager;
    protected BitmapHelper bitmapHelper;
    protected  List<FileHolder> files;
    private  List<FileEvent> fileListners;
    protected StorageFileHandler storageHandler;

    protected boolean RequestPermission = false;


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
        bitmapHelper =new BitmapHelper(getApplicationContext(),getResources().getDimensionPixelSize(R.dimen.image_thumbnails_size),this);
        fileListners =  new ArrayList<>();
        storageHandler = new StorageFileHandler(this);
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
        super.onPause();
        try {
            appSettingsManager.SaveAppSettings();
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }
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
    public StorageFileHandler getStorageHandler() {
        return this.storageHandler;
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
        storageHandler.readFilesFromFolder(fileHolder.getFile(),files,types,fileHolder.isExternalSD());
    }

    /**
     * Loads all Folders from DCIM dir from internal and external SD
     */
    @Override
    public void LoadDCIMDirs()
    {
        if (files != null)
            files.clear();
        files = storageHandler.getDCIMDirs();
    }

    /**
     * Loads all files stored in DCIM/FreeDcam from internal and external SD
     */
    @Override
    public void LoadFreeDcamDCIMDirsFiles() {
        if (files != null)
            files.clear();
        files = storageHandler.getFreeDcamDCIMDirsFiles();
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
        return (path == null || TextUtils.isEmpty(path)) ? new File("/storage") : new File(path);
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

    @Override
    public boolean hasCameraPermission()
    {
        if (VERSION.SDK_INT >= VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED)
            {
                RequestPermission = true;
                Logger.d(TAG, "Request cameraPermission");
                requestPermissions(new String[]{
                        Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},1);
                return false;
            }
            return true;
        }
        return true;
    }

    @Override
    public boolean hasExternalSDPermission() {
        if (VERSION.SDK_INT >= VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                RequestPermission = true;
                Logger.d(TAG, "Request externalSdPermission");
                requestPermissions(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                return false;
            }
            return true;
        }
        return true;
    }

    @Override
    public boolean hasWifiPermission() {
        if (VERSION.SDK_INT >= VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                RequestPermission = true;
                Logger.d(TAG, "Request wifiPermission");
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE},1);
                return false;
            }
            return true;
        }
        return true;
    }

    @Override
    public boolean hasLocationPermission() {
        if (VERSION.SDK_INT >= VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
            {
                RequestPermission = true;
                Logger.d(TAG, "Request LocationPermission");
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},1);
                return false;
            }
            return true;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        for (int i = 0; i < permissions.length; ++i) {
            String perm = permissions[i];
            boolean wasGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
            if (Manifest.permission.CAMERA.equals(perm)) {
                cameraPermsissionGranted(wasGranted);
            } else if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(perm)) {
                externalSDPermissionGranted(wasGranted);
            } else if (Manifest.permission.ACCESS_WIFI_STATE.equals(perm)) {
                wifiPermissionGranted(wasGranted);
            } else if (Manifest.permission.ACCESS_COARSE_LOCATION.equals(perm)) {
                locationPermissionGranted(wasGranted);
            }
        }
        RequestPermission = false;
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void cameraPermsissionGranted(boolean granted)
    {}

    protected void wifiPermissionGranted(boolean granted)
    {}

    protected void locationPermissionGranted(boolean granted)
    {
    }

    protected void externalSDPermissionGranted(boolean granted)
    {}

    @Override
    public int getOrientation() {
        return 0;
    }
}
