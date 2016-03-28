package com.troop.freedcam.camera.modules.image_saver;

import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.troop.androiddng.RawToDng;
import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by GeorgeKiarie on 11/29/2015.
 */
public class MediatekSaver extends JpegSaver {

    File holdFile = null;

    final public String fileEnding = ".jpg";
    public MediatekSaver(BaseCameraHolder cameraHolder, I_WorkeDone i_workeDone, Handler handler) {
        super(cameraHolder, i_workeDone, handler);
    }

    final String TAG = "MediatekIMG";

    @Override
    public void TakePicture()
    {
        Logger.d(TAG, "Start Take Picture");
        awaitpicture = true;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(ParameterHandler.PictureFormat.GetValue().equals(StringUtils.FileEnding.BAYER) || ParameterHandler.PictureFormat.GetValue().equals(StringUtils.FileEnding.DNG))
                {
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    ParameterHandler.Set_RAWFNAME("/mnt/sdcard/DCIM/FreeDCam/"+"mtk"+timestamp+StringUtils.FileEnding.GetWithDot(StringUtils.FileEnding.BAYER));
                }
                cameraHolder.TakePicture(null, null, MediatekSaver.this);
            }
        });
    }

    @Override
    public void onPictureTaken(final byte[] data)
    {
        if (awaitpicture == false)
            return;
        awaitpicture =false;
        Logger.d(TAG, "Take Picture CallBack");
        handler.post(new Runnable() {
            @Override
            public void run() {
                holdFile = new File(StringUtils.getFilePath(AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal(), fileEnding));
                Logger.d(TAG,"HolderFilePath:" +holdFile.getAbsolutePath());
                if (ParameterHandler.PictureFormat.GetValue().equals("jpeg"))
                {
                    //savejpeg
                    saveBytesToFile(data, holdFile);
                    try {
                        DeviceSwitcher().delete();
                    } catch (Exception ex) {

                    }
                } else if (ParameterHandler.PictureFormat.GetValue().equals(StringUtils.FileEnding.DNG))
                {
                    //savejpeg
                    saveBytesToFile(data, holdFile);
                    CreateDNG_DeleteRaw();
                } else if (ParameterHandler.PictureFormat.GetValue().equals(StringUtils.FileEnding.BAYER))
                {
                    //savejpeg
                    saveBytesToFile(data, holdFile);

                }


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
                    iWorkeDone.OnError("Error:Cant find raw");
                    return;
                }
            }
            rawfile = DeviceSwitcher();
            data = RawToDng.readFile(rawfile);
            Logger.d(TAG, "Filesize: " + data.length + " File:" + rawfile.getAbsolutePath());

        } catch (FileNotFoundException e) {
            Logger.exception(e);
        } catch (IOException e) {
            Logger.exception(e);
        } catch (InterruptedException e) {
            Logger.exception(e);
        }
        File dng = new File(holdFile.getName().replace(StringUtils.FileEnding.BAYER, StringUtils.FileEnding.DNG));
        Logger.d(TAG,"DNGfile:" + dng.getAbsolutePath());
        DngSaver saver = new DngSaver(cameraHolder, iWorkeDone, handler);
        saver.processData(data, dng);

        data = null;
        rawfile.delete();
        iWorkeDone.OnWorkDone(dng);
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

    public boolean checkFileCanRead(File file)
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
