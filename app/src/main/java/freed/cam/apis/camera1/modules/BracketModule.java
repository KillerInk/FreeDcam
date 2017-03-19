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
import android.os.Handler;
import freed.utils.Log;

import com.troop.freedcam.R;

import java.io.File;
import java.util.Date;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.cam.apis.camera1.CameraHolder;
import freed.utils.AppSettingsManager;


/**
 * Created by troop on 16.08.2014.
 */
public class BracketModule extends PictureModule
{

    private final String TAG = BracketModule.class.getSimpleName();

    private int hdrCount;
    private File[] files;

    public BracketModule(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler) {
        super(cameraUiWrapper,mBackgroundHandler);
        name = cameraUiWrapper.getResString(R.string.module_hdr);
    }

    //ModuleInterface START
    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public void DoWork()
    {
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                if (cameraUiWrapper.GetAppSettingsManager().getApiString(AppSettingsManager.SETTING_LOCATION).equals(cameraUiWrapper.getResString(R.string.on_)))
                    cameraHolder.SetLocation(cameraUiWrapper.getActivityInterface().getLocationHandler().getCurrentLocation());
                files = new File[7];
                hdrCount = 0;
                String picformat = cameraUiWrapper.GetParameterHandler().PictureFormat.GetValue();
                if (picformat.equals(appSettingsManager.getResString(R.string.dng_)) ||picformat.equals(appSettingsManager.getResString(R.string.bayer_)))
                {
                    if (cameraUiWrapper.GetParameterHandler().ZSL != null && cameraUiWrapper.GetParameterHandler().ZSL.IsSupported()
                            && cameraUiWrapper.GetParameterHandler().ZSL.GetValue().equals("on")
                            && ((CameraHolder) cameraUiWrapper.GetCameraHolder()).DeviceFrameWork != CameraHolder.Frameworks.MTK)
                        cameraUiWrapper.GetParameterHandler().ZSL.SetValue("off", true);
                }
                changeCaptureState(CaptureStates.image_capture_start);
                waitForPicture = true;

                setExposureToCamera();
                sleep(400);
                startcapturetime =new Date().getTime();
                cameraHolder.TakePicture(BracketModule.this);

            }
        });
    }

    @Override
    public String ShortName() {
        return "Bracket";
    }

    @Override
    public String LongName() {
        return "Bracketing";
    }

    @Override
    public boolean IsWorking() {
        return isWorking;
    }

    //ModuleInterface END

    private void setExposureToCamera()
    {
        int value = 0;
        if (hdrCount == 0) {
            value = Integer.parseInt(appSettingsManager.getApiString(AppSettingsManager.SETTING_AEB1));
        } else if (hdrCount == 1)
            value = Integer.parseInt(appSettingsManager.getApiString(AppSettingsManager.SETTING_AEB2));
        else if (hdrCount == 2)
            value = Integer.parseInt(appSettingsManager.getApiString(AppSettingsManager.SETTING_AEB3));
        else if (hdrCount == 3)
            value = Integer.parseInt(appSettingsManager.getApiString(AppSettingsManager.SETTING_AEB4));
        else if (hdrCount == 4)
            value = Integer.parseInt(appSettingsManager.getApiString(AppSettingsManager.SETTING_AEB5));
        else if (hdrCount == 5)
            value = Integer.parseInt(appSettingsManager.getApiString(AppSettingsManager.SETTING_AEB6));
        else if (hdrCount == 6)
            value = Integer.parseInt(appSettingsManager.getApiString(AppSettingsManager.SETTING_AEB7));
        else if (hdrCount == 7)
            value = 0;

        Log.d(TAG, "Set HDR Exposure to :" + value + "for image count " + hdrCount);
        int toset = value + cameraUiWrapper.GetParameterHandler().ManualExposure.getStringValues().length/2;
        cameraUiWrapper.GetParameterHandler().ManualExposure.SetValue(toset);
        Log.d(TAG, "HDR Exposure SET");
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera)
    {
        if(data == null)
            return;
        if (!waitForPicture)
        {
            isWorking = false;
            return;
        }
        hdrCount++;
        String picFormat = cameraUiWrapper.GetParameterHandler().PictureFormat.GetValue();
        saveImage(data,picFormat);
        startPreview();
        if (hdrCount == 7)//handel normal capture
        {
            waitForPicture = false;
            isWorking = false;
            changeCaptureState(CaptureStates.image_capture_stop);
            setExposureToCamera();
            fireOnWorkFinish(files);
        }
        else
        {
            setExposureToCamera();
            sleep(600);
            startcapturetime =new Date().getTime();
            cameraHolder.TakePicture(BracketModule.this);
        }
        data = null;
    }

    @Override
    protected File getFile(String fileending)
    {
        return new File(cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFilePathHDR(appSettingsManager.GetWriteExternal(), fileending, hdrCount));
    }

    private void sleep(int time)
    {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
            Log.WriteEx(ex);
        }
    }

    @Override
    protected void fireInternalOnWorkFinish(File tosave) {
        files[hdrCount-1] = tosave;
    }
}
