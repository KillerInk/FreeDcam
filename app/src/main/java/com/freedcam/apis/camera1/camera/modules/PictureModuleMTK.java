package com.freedcam.apis.camera1.camera.modules;

import android.content.Context;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.basecamera.camera.modules.ModuleEventHandler;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;
import com.freedcam.Native.RawToDng;

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
    public PictureModuleMTK(CameraHolderApi1 cameraHolderApi1, ModuleEventHandler eventHandler, Context context, AppSettingsManager appSettingsManager)
    {
        super(cameraHolderApi1, eventHandler,context,appSettingsManager);

    }

    @Override
    public boolean DoWork()
    {
        if (!this.isWorking)
        {
            workstarted();
            Logger.d(TAG, "Start Take Picture");
            waitForPicture = true;
            if (ParameterHandler.PictureFormat.GetValue().equals(StringUtils.FileEnding.BAYER) || ParameterHandler.PictureFormat.GetValue().equals(StringUtils.FileEnding.DNG)) {
                String timestamp = String.valueOf(System.currentTimeMillis());
                ParameterHandler.Set_RAWFNAME("/mnt/sdcard/DCIM/FreeDCam/" + "mtk" + timestamp + StringUtils.FileEnding.GetWithDot());
            }
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
                final String picformat = ParameterHandler.PictureFormat.GetValue();
                holdFile = getFile(picformat);
                Logger.d(TAG, "HolderFilePath:" + holdFile.getAbsolutePath());
                if (picformat.equals("jpeg")) {
                    //savejpeg
                    saveBytesToFile(data, holdFile);
                    try {
                        DeviceSwitcher().delete();
                    } catch (Exception ex) {

                    }
                } else if (picformat.equals(StringUtils.FileEnding.DNG)) {
                    //savejpeg
                    saveBytesToFile(data, holdFile);
                    CreateDNG_DeleteRaw();
                } else if (picformat.equals(StringUtils.FileEnding.BAYER)) {
                    //savejpeg
                    saveBytesToFile(data, holdFile);

                }
                waitForPicture = false;
                workfinished(true);
                cameraHolder.StartPreview();
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
        File dng = new File(holdFile.getAbsolutePath().replace(StringUtils.FileEnding.JPG, StringUtils.FileEnding.DNG));
        saveDng(data,dng);
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
