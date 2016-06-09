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

package com;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.WindowManager.LayoutParams;

import com.freedcam.ui.I_Activity;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.Logger;
import com.freedviewer.helper.BitmapHelper;

/**
 * Created by troop on 28.03.2016.
 */
public abstract class AbstractFragmentActivity extends FragmentActivity implements I_Activity
{
    private final String TAG = AbstractFragmentActivity.class.getSimpleName();
    protected AppSettingsManager appSettingsManager;
    protected BitmapHelper bitmapHelper;
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
        Context ctx = getApplicationContext();
        bitmapHelper =new BitmapHelper(getApplicationContext());
        if (appSettingsManager.getDevice() == null)
            appSettingsManager.SetDevice(DeviceUtils.getDevice(ctx));
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

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
       /* if (FreeDPool.IsInit())
            FreeDPool.Destroy();*/
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

    protected int getMuliplier()
    {
        return 4;
    }

    private I_OnActivityResultCallback resultCallback;

    @Override
    public void SwitchCameraAPI(String Api) {

    }

    @Override
    public void SetTheme(String Theme) {

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
}
