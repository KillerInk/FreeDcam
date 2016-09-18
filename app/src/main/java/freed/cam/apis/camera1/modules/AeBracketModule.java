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

package freed.cam.apis.camera1.modules;

import android.hardware.Camera;

import java.io.File;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.utils.AppSettingsManager;
import freed.utils.FreeDPool;
import freed.utils.Logger;

import static freed.utils.StringUtils.FileEnding.BAYER;
import static freed.utils.StringUtils.FileEnding.DNG;

/**
 * Created by troop on 26.08.2016.
 */
public class AeBracketModule extends PictureModuleMTK
{
    private int hdrCount = 0;
    private final String TAG = AeBracketModule.class.getSimpleName();

    public AeBracketModule(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
        name = KEYS.MODULE_HDR;
    }

    @Override
    public String ShortName() {
        return "AEBracket";
    }

    @Override
    public String LongName() {
        return "AE-Bracketing";
    }

    @Override
    public boolean DoWork()
    {
        if (!isWorking)
        {
            hdrCount = 0;
            setExposureToCamera();
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                Logger.exception(e);
            }
            super.DoWork();
        }
        return true;
    }

    private void setExposureToCamera()
    {
        int value = 0;

        if (hdrCount == 0) {
            value = Integer.parseInt(appSettingsManager.getString(AppSettingsManager.SETTING_AEB1));
        } else if (hdrCount == 1)
            value = Integer.parseInt(appSettingsManager.getString(AppSettingsManager.SETTING_AEB2));
        else if (hdrCount == 2)
            value = Integer.parseInt(appSettingsManager.getString(AppSettingsManager.SETTING_AEB3));
        cameraUiWrapper.GetParameterHandler().ManualExposure.SetValue(value);
    }

    @Override
    protected File getFile(String fileending)
    {
        return new File(cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFilePathHDR(appSettingsManager.GetWriteExternal(), fileending, hdrCount));
    }

    @Override
    public void onPictureTaken(final byte[] data, Camera camera)
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run()
            {
                if (!waitForPicture)
                {
                    isWorking = false;
                    return;
                }
                hdrCount++;
                String picFormat = cameraUiWrapper.GetParameterHandler().PictureFormat.GetValue();
                File holdFile = getFile(".jpg");
                Logger.d(TAG, "HolderFilePath:" + holdFile.getAbsolutePath());
                switch (picFormat) {
                    case KEYS.JPEG:
                        //savejpeg
                        cameraUiWrapper.getActivityInterface().getImageSaver().SaveJpegByteArray(holdFile,data);
                        try {
                            DeviceSwitcher().delete();
                        } catch (Exception ex) {
                            Logger.exception(ex);
                        }
                        break;
                    case DNG:
                        //savejpeg
                        cameraUiWrapper.getActivityInterface().getImageSaver().SaveJpegByteArray(holdFile,data);
                        CreateDNG_DeleteRaw();
                        break;
                    case BAYER:
                        //savejpeg
                        cameraUiWrapper.getActivityInterface().getImageSaver().SaveJpegByteArray(holdFile,data);
                        break;
                }
                scanAndFinishFile(holdFile);
                cameraHolder.StartPreview();
                if (hdrCount == 3)//handel normal capture
                {
                    waitForPicture = false;
                    isWorking = false;
                    changeCaptureState(ModuleHandlerAbstract.CaptureStates.image_capture_stop);
                }
                else
                {
                    setExposureToCamera();
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        Logger.exception(e);
                    }
                    cameraHolder.TakePicture(AeBracketModule.this);
                }
            }
        });
    }
}
