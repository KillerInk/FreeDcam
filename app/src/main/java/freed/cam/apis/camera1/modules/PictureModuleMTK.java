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

import com.troop.freedcam.R;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import freed.FreedApplication;
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.apis.camera1.Camera1;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.event.capture.CaptureStates;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.jni.RawToDng;
import freed.settings.SettingKeys;
import freed.utils.Log;
import freed.utils.StringUtils;
import freed.utils.StringUtils.FileEnding;

/**
 * Created by troop on 24.11.2014.
 */
public class PictureModuleMTK extends PictureModule
{
    private final String TAG = PictureModuleMTK.class.getSimpleName();
    private File holdFile;
    private final UserMessageHandler userMessageHandler;
    public PictureModuleMTK(Camera1 cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler)
    {
        super(cameraUiWrapper, mBackgroundHandler,mainHandler);
        userMessageHandler = ActivityFreeDcamMain.userMessageHandler();
    }

    @Override
    public void DoWork()
    {
        mBackgroundHandler.post(() -> {
            if (settingsManager.getGlobal(SettingKeys.LOCATION_MODE).get())
                cameraHolder.SetLocation(locationManager.getCurrentLocation());

            cameraUiWrapper.getParameterHandler().SetPictureOrientation(orientationManager.getCurrentOrientation());
            Log.d(TAG, "Start Take Picture");
            waitForPicture = true;
            ParameterInterface picformat = cameraUiWrapper.getParameterHandler().get(SettingKeys.PICTURE_FORMAT);
            if (picformat.getStringValue().equals(FileEnding.BAYER) || picformat.getStringValue().equals(FileEnding.DNG)) {
                String timestamp = String.valueOf(System.currentTimeMillis());
                ((ParametersHandler)cameraUiWrapper.getParameterHandler()).Set_RAWFNAME(StringUtils.GetInternalSDCARD()+"/DCIM/FreeDCam/" + "mtk" + timestamp + ".bayer");
            }
            isWorking = true;
            changeCaptureState(CaptureStates.image_capture_start);
            cameraHolder.TakePicture(PictureModuleMTK.this);
        });


    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera)
    {
        if (settingsManager.getGlobal(SettingKeys.PLAY_SHUTTER_SOUND).get())
            soundPlayer.play();
        if (!waitForPicture)
            return;
        waitForPicture =false;
        Log.d(TAG, "Take Picture CallBack");
        String picformat = cameraUiWrapper.getParameterHandler().get(SettingKeys.PICTURE_FORMAT).getStringValue();
        // must always be jpg ending. dng gets created based on that
        holdFile = getFile(".jpg");
        Log.d(TAG, "HolderFilePath:" + holdFile.getAbsolutePath());
        if (picformat.equals(FreedApplication.getStringFromRessources(R.string.jpeg_)))
        {

            saveJpeg(data,holdFile);
            try {
                DeviceSwitcher().delete();
            } catch (Exception ex) {
                Log.WriteEx(ex);
            }
        }
        else if (picformat.equals(FreedApplication.getStringFromRessources(R.string.dng_)))
        {
            saveJpeg(data,holdFile);
            CreateDNG_DeleteRaw();
        }
        else
        {
            saveJpeg(data,holdFile);
        }
        waitForPicture = false;
        data = null;
        startPreview();
        isWorking = false;
        changeCaptureState(CaptureStates.image_capture_stop);
    }

    private int loopBreaker;
    protected void CreateDNG_DeleteRaw()
    {
        byte[] data = null;
        File rawfile = null;
        try {
            while (!checkFileCanRead(DeviceSwitcher()))
            {
                Log.d(TAG,"try to read raw");
                if (loopBreaker < 20) {
                    Thread.sleep(100);
                    loopBreaker++;
                }
                else {
                    Log.d(TAG,"############ Failed to read Raw #########" );
                    userMessageHandler.sendMSG("Timout:Failed to read Raw",false);
                    return;
                }
            }
            rawfile = DeviceSwitcher();
            data = RawToDng.readFile(rawfile);
            try {
                rawfile.delete();
            }
            catch (NullPointerException ex)
            {
                Log.d(TAG, "Rawfile delete failed");
            }

            Log.d(TAG, "Found Raw: Filesize: " + data.length + " File:" + rawfile.getAbsolutePath());

        } catch (InterruptedException | IOException ex) {
            Log.WriteEx(ex);
        }
        File dng = new File(holdFile.getAbsolutePath().replace(FileEnding.JPG, FileEnding.DNG));
        saveDng(data,dng);
        data = null;
    }


    protected File DeviceSwitcher()
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
