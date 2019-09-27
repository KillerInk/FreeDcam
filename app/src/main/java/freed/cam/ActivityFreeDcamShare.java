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

package freed.cam;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import freed.cam.apis.basecamera.modules.I_WorkEvent;
import freed.utils.Log;
import freed.viewer.holder.FileHolder;

/**
 * Created by troop on 18.10.2014.
 */
public class ActivityFreeDcamShare extends ActivityFreeDcamMain implements I_WorkEvent
{
    private final String TAG = ActivityFreeDcamShare.class.getSimpleName();
    private File toreturnFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePermissionGranted() {
        super.onCreatePermissionGranted();
        Intent callerIntent = getIntent();
        Log.d(TAG, callerIntent.getAction());
        if (callerIntent.getAction().equals(MediaStore.ACTION_IMAGE_CAPTURE)) {
            Uri imageUri = callerIntent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
            toreturnFile = new File(imageUri.getPath());
            Log.d(TAG, imageUri.getPath());
        }
    }

    @Override
    public void WorkHasFinished(FileHolder fileHolder)
    {
        if (toreturnFile != null) {
            try {
                copy(fileHolder.getFile(), toreturnFile);
            } catch (IOException e) {
                Log.WriteEx(e);
            }
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        else
            super.WorkHasFinished(fileHolder);
    }

    private void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
