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

package freed.cam.apis.sonyremote.modules;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.support.v4.provider.DocumentFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.CaptureStates;
import freed.cam.apis.basecamera.modules.ModuleAbstract;
import freed.cam.apis.sonyremote.CameraHolderSony;
import freed.cam.apis.sonyremote.parameters.ParameterHandler;
import freed.utils.Logger;

/**
 * Created by troop on 22.12.2014.
 */
public class PictureModuleSony extends ModuleAbstract implements I_PictureCallback, I_CameraStatusChanged
{
    private final String TAG = PictureModuleSony.class.getSimpleName();
    private final CameraHolderSony cameraHolder;

    public PictureModuleSony(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler) {
        super(cameraUiWrapper,mBackgroundHandler);
        name = KEYS.MODULE_PICTURE;
        cameraHolder = (CameraHolderSony)cameraUiWrapper.GetCameraHolder();


    }

    @Override
    public String ModuleName() {
        return super.ModuleName();
    }

    @Override
    public boolean DoWork()
    {
        if (cameraUiWrapper.GetParameterHandler().ContShootMode != null && cameraUiWrapper.GetParameterHandler().ContShootMode.IsSupported()) {
            String shootmode = ((ParameterHandler) cameraUiWrapper.GetParameterHandler()).ContShootMode.GetValue();
            if (!isWorking && shootmode.equals("Single"))
            {
                sendCaptureStateChangedBroadCast(CaptureStates.IMAGE_CAPTURE_START);
                takePicture();
            }
            else if (!isWorking)
            {
                sendCaptureStateChangedBroadCast(CaptureStates.CONTINOUSE_CAPTURE_START);
                cameraHolder.startContShoot(this);
                return true;
            } else
            {
                sendCaptureStateChangedBroadCast(CaptureStates.CONTINOUSE_CAPTURE_STOP_WHILE_WORKING);
                cameraHolder.stopContShoot(this);
                return false;
            }
        }
        else
            if (!isWorking)
            {
                sendCaptureStateChangedBroadCast(CaptureStates.IMAGE_CAPTURE_START);
                takePicture();
            }
        return true;
    }

    @Override
    public void InitModule()
    {
        Logger.d(TAG, "InitModule");
        cameraHolder.CameraStatusListner = this;
        onCameraStatusChanged(cameraHolder.GetCameraStatus());
    }

    @Override
    public void DestroyModule() {

    }

    @Override
    public String LongName() {
        return "Picture";
    }

    @Override
    public String ShortName() {
        return "Pic";
    }


    private void takePicture()
    {
        cameraHolder.TakePicture(this);
        Logger.d(TAG, "Start Take Picture");
    }

    @Override
    public void onPictureTaken(URL url)
    {
        File file = new File(cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFilePath(appSettingsManager.GetWriteExternal(), ".jpg"));
        try {
            file.createNewFile();
        } catch (IOException e) {
            Logger.exception(e);
        }
        InputStream inputStream = null;
        OutputStream output = null;
        try {
            inputStream = new BufferedInputStream(url.openStream());
            if (VERSION.SDK_INT <= VERSION_CODES.LOLLIPOP || VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && !appSettingsManager.GetWriteExternal())
                output = new FileOutputStream(file);
            else
            {
                DocumentFile df = cameraUiWrapper.getActivityInterface().getFreeDcamDocumentFolder();
                DocumentFile wr = df.createFile("image/jpeg", file.getName());
                output = cameraUiWrapper.getContext().getContentResolver().openOutputStream(wr.getUri());
            }
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
        } catch (IOException e) {
            Logger.exception(e);
        }
        finally
        {
            try
            {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                Logger.exception(e);
            }

            try {
                if (output != null)
                    output.close();
            } catch (IOException e) {
                Logger.exception(e);
            }
        }

        cameraUiWrapper.getActivityInterface().getImageSaver().scanFile(file);
    }


    @Override
    public void onCameraStatusChanged(String status)
    {
        Logger.d(TAG, "Status:"+status);
        if (status.equals("IDLE") && isWorking)
        {
            isWorking = false;
            if (currentWorkState == CaptureStates.IMAGE_CAPTURE_START)
                sendCaptureStateChangedBroadCast(CaptureStates.IMAGE_CAPTURE_STOP);
            else if (currentWorkState == CaptureStates.CONTINOUSE_CAPTURE_WORK_START || currentWorkState == CaptureStates.CONTINOUSE_CAPTURE_START)
                sendCaptureStateChangedBroadCast(CaptureStates.CONTINOUSE_CAPTURE_WORK_STOP);
        }
        else if ((status.equals("StillCapturing") || status.equals("StillSaving")) && !isWorking) {
            isWorking = true;
            if (currentWorkState == CaptureStates.IMAGE_CAPTURE_STOP)
                sendCaptureStateChangedBroadCast(CaptureStates.IMAGE_CAPTURE_START);
            else if (currentWorkState == CaptureStates.CONTINOUSE_CAPTURE_WORK_STOP || currentWorkState == CaptureStates.CONTINOUSE_CAPTURE_STOP)
                sendCaptureStateChangedBroadCast(CaptureStates.CONTINOUSE_CAPTURE_WORK_START);
        }

    }
}
