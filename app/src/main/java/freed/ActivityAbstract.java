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

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.EntryPointAccessors;
import freed.image.ImageManager;
import freed.settings.SettingsManager;
import freed.utils.HideNavBarHelper;
import freed.utils.Log;
import freed.utils.PermissionManager;
import hilt.ImageManagerEntryPoint;
import hilt.PermissionManagerEntryPoint;

/**
 * Created by troop on 28.03.2016.
 */
@AndroidEntryPoint
public abstract class ActivityAbstract extends AppCompatActivity implements ActivityInterface {


    private static Activity context;
    protected static <T> T getEntryPointFromActivity(Class<T> entryPoint) {
        return EntryPointAccessors.fromActivity(context, entryPoint);
    }

    public static PermissionManager permissionManager()
    {
        return getEntryPointFromActivity(PermissionManagerEntryPoint.class).permissionManager();
    }

    private final boolean forceLogging = false;

    private final String TAG = ActivityAbstract.class.getSimpleName();

    private I_OnActivityResultCallback resultCallback;
    private HideNavBarHelper hideNavBarHelper;
    @Inject
    PermissionManager permissionManager;
    @Inject
    public SettingsManager settingsManager;
    @Inject protected ImageManager imageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentToView();
        context = this;
        Log.d(TAG,"onCreate");
        hideNavBarHelper = new HideNavBarHelper();
        if (!settingsManager.isInit()) {
            settingsManager.init();
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
    }

    @Override
    protected void onDestroy() {
        imageManager.cancelImageSaveTasks();
        imageManager.cancelImageLoadTasks();
        settingsManager.release();
        context = null;
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            hideNavBarHelper.HIDENAVBAR(getWindow());
        else
            hideNavBarHelper.showNavbar(getWindow());
    }

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
                settingsManager.SetBaseFolder(uri.toString());
                if (resultCallback != null) {
                    resultCallback.onActivityResultCallback(uri);
                    resultCallback = null;
                }
            }
        }
    }

}
