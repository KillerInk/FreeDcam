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

package com.freedcam.apis.camera1.camera.modules;

import android.content.Context;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler.CaptureModes;
import com.freedcam.apis.basecamera.camera.modules.ModuleEventHandler;
import com.freedcam.apis.camera1.camera.CameraHolder;
import com.freedcam.jni.RawToDng;
import com.freedcam.ui.handler.MediaScannerManager;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;
import com.freedcam.utils.StringUtils.FileEnding;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by troop on 24.11.2014.
 */
public class PictureModuleMTK extends PictureModule
{
    private final String TAG = PictureModuleMTK.class.getSimpleName();
    private File holdFile = null;
    public PictureModuleMTK(CameraHolder cameraHolder, ModuleEventHandler eventHandler, Context context, AppSettingsManager appSettingsManager)
    {
        super(cameraHolder, eventHandler,context,appSettingsManager);

    }

    @Override
    public boolean DoWork()
    {
        if (!isWorking)
        {

            Logger.d(TAG, "Start Take Picture");
            waitForPicture = true;
            if (ParameterHandler.PictureFormat.GetValue().equals(FileEnding.BAYER) || ParameterHandler.PictureFormat.GetValue().equals(FileEnding.DNG)) {
                String timestamp = String.valueOf(System.currentTimeMillis());
                ParameterHandler.Set_RAWFNAME(StringUtils.GetInternalSDCARD()+"/DCIM/FreeDCam/" + "mtk" + timestamp + ".bayer");
            }
            isWorking = true;
            changeWorkState(CaptureModes.image_capture_start);
            cameraHolder.TakePicture(null, this);
        }
        return true;
    }

    @Override
    public void onPictureTaken(final byte[] data)
    {
        if (!waitForPicture)
            return;
        waitForPicture =false;
        Logger.d(TAG, "Take Picture CallBack");
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run()
            {
                String picformat = ParameterHandler.PictureFormat.GetValue();
                // must always be jpg ending. dng gets created based on that
                holdFile = getFile(".jpg");
                Logger.d(TAG, "HolderFilePath:" + holdFile.getAbsolutePath());
                switch (picformat) {
                    case KEYS.JPEG:
                        //savejpeg
                        saveBytesToFile(data, holdFile);
                        try {
                            DeviceSwitcher().delete();
                        } catch (Exception ex) {
                            Logger.exception(ex);
                        }
                        break;
                    case FileEnding.DNG:
                        //savejpeg
                        saveBytesToFile(data, holdFile);
                        CreateDNG_DeleteRaw();
                        break;
                    case FileEnding.BAYER:
                        //savejpeg
                        saveBytesToFile(data, holdFile);
                        break;
                }
                waitForPicture = false;
                cameraHolder.StartPreview();
                MediaScannerManager.ScanMedia(context,holdFile);
                eventHandler.WorkFinished(holdFile);
                isWorking = false;
                changeWorkState(CaptureModes.image_capture_stop);
            }
        });
    }

    private int loopBreaker = 0;
    private void CreateDNG_DeleteRaw()
    {
        byte[] data = null;
        File rawfile = null;
        try {
            while (!checkFileCanRead(DeviceSwitcher()))
            {
                if (loopBreaker < 20) {
                    Thread.sleep(100);
                    loopBreaker++;
                }
                else {
                    return;
                }
            }
            rawfile = DeviceSwitcher();
            data = RawToDng.readFile(rawfile);
            Logger.d(TAG, "Filesize: " + data.length + " File:" + rawfile.getAbsolutePath());

        } catch (InterruptedException | IOException e) {
            Logger.exception(e);
        }
        File dng = new File(holdFile.getAbsolutePath().replace(FileEnding.JPG, FileEnding.DNG));
        saveDng(data,dng);
        MediaScannerManager.ScanMedia(context,dng);
        data = null;
        rawfile.delete();
    }


    private File DeviceSwitcher()
    {
        File freedcamFolder = new File(StringUtils.GetInternalSDCARD()+StringUtils.freedcamFolder);
        for (File f : freedcamFolder.listFiles())
        {
            if (f.isFile() && f.getName().startsWith("mtk"))
                return f;
        }
        return null;
    }

    private boolean checkFileCanRead(File file)
    {
        try {
            if (!file.exists())
                return false;
            if (!file.canRead())
                return false;
            try {
                FileReader fileReader = new FileReader(file.getAbsolutePath());
                fileReader.read();
                fileReader.close();
            } catch (Exception e) {
                return false;
            }
        }
        catch (NullPointerException ex)
        {
            return false;
        }

        return true;
    }

}
