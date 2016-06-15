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

package com.freedcam;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.freedcam.apis.basecamera.modules.I_WorkEvent;
import com.freedviewer.holder.FileHolder;

/**
 * Created by troop on 18.10.2014.
 */
public class ShareActivity extends MainActivity implements I_WorkEvent
{
    private Intent callerIntent;
    private Uri data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callerIntent = getIntent();
        data = callerIntent.getData();
        Uri imageUri = callerIntent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);

        /*cameraUiWrapper.moduleHandler.moduleEventHandler.AddWorkFinishedListner(this);
        if(!cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_PICTURE))
            cameraUiWrapper.moduleHandler.SetModule(ModuleHandler.MODULE_PICTURE);
        PictureModule pictureModule = (PictureModule)cameraUiWrapper.moduleHandler.GetCurrentModule();
        pictureModule.OverRidePath = imageUri.getPath();
        if (pictureModule.OverRidePath.endsWith(".jpg"))
        {
            appSettingsManager.setString(AppSettingsManager.SETTING_PICTUREFORMAT, KEYS.JPEG);
        }*/

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void WorkHasFinished(FileHolder fileHolder)
    {
        setResult(Activity.RESULT_OK, callerIntent);
        finish();
    }
}
