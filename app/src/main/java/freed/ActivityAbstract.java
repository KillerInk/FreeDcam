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
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import freed.file.FileListController;
import freed.image.ImageManager;
import freed.settings.SettingsManager;
import freed.utils.HideNavBarHelper;
import freed.utils.Log;
import freed.utils.PermissionManager;
import freed.viewer.helper.BitmapHelper;

/**
 * Created by troop on 28.03.2016.
 */
public abstract class ActivityAbstract extends AppCompatActivity implements ActivityInterface {

    private final boolean forceLogging = false;

    private final String TAG = ActivityAbstract.class.getSimpleName();
    protected BitmapHelper bitmapHelper;
    protected FileListController fileListController;
    private I_OnActivityResultCallback resultCallback;
    private HideNavBarHelper hideNavBarHelper;
    private PermissionManager permissionManager;
    public PermissionManager getPermissionManager() {
        return permissionManager;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentToView();
        Log.d(TAG,"onCreate");
        hideNavBarHelper = new HideNavBarHelper();
        permissionManager =new PermissionManager(this);
        if (!SettingsManager.getInstance().isInit()) {
            SettingsManager.getInstance().init();
        }
        Log.d(TAG,"onCreatePermissionGranted");
        File log = new File(FreedApplication.getContext().getExternalFilesDir(null)+ "/log.txt");
        if (!forceLogging) {
            if (!Log.isLogToFileEnable() && log.exists()) {
                new Log();
            }
        }
        else
        {
            new Log();
        }
        Log.d(TAG, "initOnCreate()");

    }

    protected abstract void setContentToView();

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ImageManager.getInstance(); // init it
    }

    @Override
    protected void onDestroy() {
        ImageManager.cancelImageSaveTasks();
        ImageManager.cancelImageLoadTasks();
        SettingsManager.getInstance().release();
        super.onDestroy();
        /*if (Log.isLogToFileEnable())
            Log.destroy();*/
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.flush();
    }

    @Override
    public void closeActivity() {
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            hideNavBarHelper.HIDENAVBAR(getWindow());
        else
            hideNavBarHelper.showNavbar(getWindow());
    }

    @Override
    public void ChooseSDCard(I_OnActivityResultCallback callback)
    {
        try {
            resultCallback = callback;
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, READ_REQUEST_CODE);
        }
        catch(ActivityNotFoundException activityNotFoundException)
        {
            Log.WriteEx(activityNotFoundException);

        }
    }

    private final int READ_REQUEST_CODE = 42;
    public static final int DELETE_REQUEST_CODE = 433;

    @TargetApi(VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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


                getContentResolver().takePersistableUriPermission(uri, takeFlags);
                SettingsManager.getInstance().SetBaseFolder(uri.toString());
                if (resultCallback != null) {
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
    public FileListController getFileListController() {
        return this.fileListController;
    }

    @Override
    public int getOrientation() {
        return 0;
    }

    @Override
    public void SetNightOverlay() {

    }

    @Override
    public void runFeatureDetector() {

    }
}
