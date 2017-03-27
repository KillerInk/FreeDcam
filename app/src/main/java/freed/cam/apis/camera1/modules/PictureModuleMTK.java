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
import java.io.FileReader;
import java.io.IOException;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.jni.RawToDng;
import freed.utils.AppSettingsManager;
import freed.utils.StringUtils;
import freed.utils.StringUtils.FileEnding;

/**
 * Created by troop on 24.11.2014.
 */
public class PictureModuleMTK extends PictureModule
{
    private final String TAG = PictureModuleMTK.class.getSimpleName();
    private File holdFile;
    public PictureModuleMTK(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler)
    {
        super(cameraUiWrapper, mBackgroundHandler);
    }

    @Override
    public void DoWork()
    {
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                if (cameraUiWrapper.GetAppSettingsManager().getApiString(AppSettingsManager.SETTING_LOCATION).equals(cameraUiWrapper.getResString(R.string.on_)))
                    cameraHolder.SetLocation(cameraUiWrapper.getActivityInterface().getLocationHandler().getCurrentLocation());

                cameraUiWrapper.GetParameterHandler().SetPictureOrientation(cameraUiWrapper.getActivityInterface().getOrientation());
                Log.d(TAG, "Start Take Picture");
                waitForPicture = true;
                if (cameraUiWrapper.GetParameterHandler().PictureFormat.GetValue().equals(FileEnding.BAYER) || cameraUiWrapper.GetParameterHandler().PictureFormat.GetValue().equals(FileEnding.DNG)) {
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    ((ParametersHandler)cameraUiWrapper.GetParameterHandler()).Set_RAWFNAME(StringUtils.GetInternalSDCARD()+"/DCIM/FreeDCam/" + "mtk" + timestamp + ".bayer");
                }
                isWorking = true;
                changeCaptureState(CaptureStates.image_capture_start);
                cameraHolder.TakePicture(PictureModuleMTK.this);
            }
        });


    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera)
    {
        if (!waitForPicture)
            return;
        waitForPicture =false;
        Log.d(TAG, "Take Picture CallBack");
        String picformat = cameraUiWrapper.GetParameterHandler().PictureFormat.GetValue();
        // must always be jpg ending. dng gets created based on that
        holdFile = getFile(".jpg");
        Log.d(TAG, "HolderFilePath:" + holdFile.getAbsolutePath());
        if (picformat.equals(cameraUiWrapper.getResString(R.string.jpeg_)))
        {
            saveJpeg(holdFile,data);
            try {
                DeviceSwitcher().delete();
            } catch (Exception ex) {
                Log.WriteEx(ex);
            }
        }
        else if (picformat.equals(cameraUiWrapper.getResString(R.string.dng_)))
        {
            saveJpeg(holdFile,data);
            CreateDNG_DeleteRaw();
        }
        else
        {
            saveJpeg(holdFile,data);
        }
        fireOnWorkFinish(holdFile);
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
                    cameraUiWrapper.GetCameraHolder().SendUIMessage("Timout:Failed to read Raw");
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
        fireOnWorkFinish(dng);
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
