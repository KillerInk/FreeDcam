package com.freedcam.apis.camera1.camera.modules.image_saver;

import com.freedcam.apis.camera1.camera.BaseCameraHolder;
import com.freedcam.ui.AppSettingsManager;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;
import com.troop.androiddng.RawToDng;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by GeorgeKiarie on 11/29/2015.
 */
public class MediatekSaver extends JpegSaver {

    private File holdFile = null;

    private final String fileEnding = ".jpg";
    public MediatekSaver(BaseCameraHolder cameraHolder, I_WorkeDone i_workeDone) {
        super(cameraHolder, i_workeDone);
    }

    private final String TAG = "MediatekIMG";

    @Override
    public void TakePicture()
    {
        Logger.d(TAG, "Start Take Picture");
        awaitpicture = true;
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                if (ParameterHandler.PictureFormat.GetValue().equals(StringUtils.FileEnding.BAYER) || ParameterHandler.PictureFormat.GetValue().equals(StringUtils.FileEnding.DNG)) {
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    ParameterHandler.Set_RAWFNAME("/mnt/sdcard/DCIM/FreeDCam/" + "mtk" + timestamp + StringUtils.FileEnding.GetWithDot());
                }
                cameraHolder.TakePicture(null, MediatekSaver.this);
            }
        });
    }

    @Override
    public void onPictureTaken(final byte[] data)
    {
        if (!awaitpicture)
            return;
        awaitpicture =false;
        Logger.d(TAG, "Take Picture CallBack");
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                holdFile = new File(StringUtils.getFilePath(AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal(), fileEnding));
                Logger.d(TAG, "HolderFilePath:" + holdFile.getAbsolutePath());
                if (ParameterHandler.PictureFormat.GetValue().equals("jpeg")) {
                    //savejpeg
                    saveBytesToFile(data, holdFile,true);
                    try {
                        DeviceSwitcher().delete();
                    } catch (Exception ex) {

                    }
                } else if (ParameterHandler.PictureFormat.GetValue().equals(StringUtils.FileEnding.DNG)) {
                    //savejpeg
                    saveBytesToFile(data, holdFile,false);
                    CreateDNG_DeleteRaw();
                } else if (ParameterHandler.PictureFormat.GetValue().equals(StringUtils.FileEnding.BAYER)) {
                    //savejpeg
                    saveBytesToFile(data, holdFile,true);

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

        } catch (InterruptedException | IOException e) {
            Logger.exception(e);
        }
        File dng = new File(holdFile.getAbsolutePath().replace(StringUtils.FileEnding.JPG, StringUtils.FileEnding.DNG));

        Logger.d(TAG,"DNGfile:" + dng.getAbsolutePath());
        DngSaver saver = new DngSaver(cameraHolder, iWorkeDone);
        saver.processData(data, dng, false);

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
