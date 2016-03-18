package com.troop.freedcam.camera.modules.image_saver;

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
    public MediatekSaver(BaseCameraHolder cameraHolder, I_WorkeDone i_workeDone, Handler handler, boolean externalSD) {
        super(cameraHolder, i_workeDone, handler, externalSD);
    }

    final String TAG = "MediatekIMG";

    @Override
    public void TakePicture()
    {
        Logger.d(TAG, "Start Take Picture");
        if (ParameterHandler.ZSL != null && ParameterHandler.ZSL.IsSupported() && ParameterHandler.ZSL.GetValue().equals("on"))
        {
            ParameterHandler.ZSL.SetValue("off",true);
        }
        awaitpicture = true;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(ParameterHandler.PictureFormat.GetValue().equals("raw") || ParameterHandler.PictureFormat.GetValue().equals("dng"))
                {
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    ParameterHandler.Set_RAWFNAME("/mnt/sdcard/DCIM/FreeDCam/"+"mtk"+timestamp+".raw");
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
            public void run()
            {
                holdFile = new File(StringUtils.getFilePath(externalSd, fileEnding));
                //final String lastBayerFormat = cameraHolder.ParameterHandler.PictureFormat.GetValue();

             //   Logger.d(TAG,RawToDng.getFilePath());

                if(ParameterHandler.PictureFormat.GetValue().equals("jpeg"))
                {
                    saveBytesToFile(data, holdFile);
                    try
                    {
                        DeviceSwitcher().delete();
                    }
                    catch (Exception ex)
                    {

                    }
                }
                else if(ParameterHandler.PictureFormat.GetValue().equals("dng"))
                {
                    saveBytesToFile(data, holdFile);
                    CreateDNG_DeleteRaw();
                }
                else if(ParameterHandler.PictureFormat.GetValue().equals("raw"))
                {
                    saveBytesToFile(data, holdFile);

                }


            }
        });
    }


    private int loopBreaker = 0;
    private void CreateDNG_DeleteRaw()
    {
        if (!StringUtils.IS_L_OR_BIG()
                || StringUtils.WRITE_NOT_EX_AND_L_ORBigger())
            processData();
        else
        {
            processLData();
        }

    }

    private void processData() {
        final RawToDng dngConverter = RawToDng.GetInstance();
        byte[] data = null;
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
            data = RawToDng.readFile(DeviceSwitcher());
            Logger.d(TAG, "Filesize: " + data.length + " File:" + DeviceSwitcher().getAbsolutePath());

        } catch (FileNotFoundException e) {
            Logger.exception(e);
        } catch (IOException e) {
            Logger.exception(e);
        } catch (InterruptedException e) {
            Logger.exception(e);
        }

        String  out = holdFile.getAbsolutePath().replace(".jpg", ".dng");

        dngConverter.SetBayerData(data, out);
        float fnum, focal = 0;
        fnum = 2.0f;
        focal = 4.7f;


        //  int mISO = mDngResult.get(CaptureResult.SENSOR_SENSITIVITY));
        double mExposuretime;
        int mFlash;


        dngConverter.setExifData(0, 0, 0, fnum, focal, "0", "0", 0);

        dngConverter.WriteDNG(DeviceUtils.DEVICE());
        dngConverter.RELEASE();
        data = null;
        DeviceSwitcher().delete();
        iWorkeDone.OnWorkDone(new File(out));
    }

    private void processLData() {
        final RawToDng dngConverter = RawToDng.GetInstance();
        byte[] data = null;
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
            data = RawToDng.readFile(DeviceSwitcher());
            Logger.d(TAG, "Filesize: " + data.length + " File:" + DeviceSwitcher().getAbsolutePath());

        } catch (FileNotFoundException e) {
            Logger.exception(e);
        } catch (IOException e) {
            Logger.exception(e);
        } catch (InterruptedException e) {
            Logger.exception(e);
        }

        String  out = holdFile.getName().replace(".jpg", ".dng");
        Uri uri = Uri.parse(AppSettingsManager.APPSETTINGSMANAGER.GetBaseFolder());
        DocumentFile df = DocumentFile.fromTreeUri(AppSettingsManager.APPSETTINGSMANAGER.context, uri);
        DocumentFile wr = df.createFile("image/dng", out);
        ParcelFileDescriptor pfd = null;
        try {

            pfd = AppSettingsManager.APPSETTINGSMANAGER.context.getContentResolver().openFileDescriptor(wr.getUri(), "rw");
        } catch (FileNotFoundException e) {
            Logger.exception(e);
        }
        catch (IllegalArgumentException e)
        {
            Logger.exception(e);
        }

        if (pfd != null)
            dngConverter.SetBayerDataFD(data, pfd, out);
        else {
            return;
        }
        float fnum, focal = 0;
        fnum = 2.0f;
        focal = 4.7f;


        //  int mISO = mDngResult.get(CaptureResult.SENSOR_SENSITIVITY));
        double mExposuretime;
        int mFlash;


        dngConverter.setExifData(0, 0, 0, fnum, focal, "0", "0", 0);

        dngConverter.WriteDNG(DeviceUtils.DEVICE());
        dngConverter.RELEASE();
        data = null;
        DeviceSwitcher().delete();
        iWorkeDone.OnWorkDone(new File(out));
    }

    public String FeeDJNI(String msg)
    {
        Logger.d(TAG,msg);

        return DeviceSwitcher().getAbsolutePath();
    }

   /* private int getFileSize()
    {
        S


    }*/

    private File DeviceSwitcher()
    {
        File freedcamFolder = new File(StringUtils.GetInternalSDCARD()+StringUtils.freedcamFolder);
        for (File f : freedcamFolder.listFiles())
        {
            if (f.isFile() && f.getName().startsWith("mtk"))
                return f;
        }
        return null;
        /*File dump = null;
        switch (Build.MODEL)
        {
            case "Retro":
                dump = new File("/mnt/sdcard/DCIM/FreeDCam/mtk___1584x1188_10_0.raw");
                break;
            case "E5663":
                dump = new File("/mnt/sdcard/DCIM/FreeDCam/mtk_pure__5344x4016_10_3.raw");
                break;
            case "i-mobile i-STYLE Q6":
                dump = new File("/mnt/sdcard/DCIM/FreeDCam/mtk___1584x1188_10_0.raw");
                break;
            case "thl 5000":
                dump = new File("/mnt/sdcard/DCIM/FreeDCam/mtk___1584x1188_10_0.raw");
                break;
            case "MX4":
                dump = new File("/mnt/sdcard/DCIM/FreeDCam/mtk___1584x1188_10_0.raw");
                break;
            case "MX5":
                dump = new File("/mnt/sdcard/DCIM/FreeDCam/mtk___1584x1188_10_0.raw");
                break;
            case "Redmi Note 2":
                dump = new File("/mnt/sdcard/DCIM/FreeDCam/mtk___1584x1188_10_0.raw");
                break;


        }
        return dump;*/
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
