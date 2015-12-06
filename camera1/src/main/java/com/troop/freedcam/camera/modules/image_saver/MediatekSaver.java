package com.troop.freedcam.camera.modules.image_saver;

import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.troop.androiddng.RawToDng;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by GeorgeKiarie on 11/29/2015.
 */
public class MediatekSaver extends JpegSaver {

    File holdFile = null;
    protected AbstractParameterHandler ParameterHandlerx;

    final public String fileEnding = ".jpg";
    public MediatekSaver(BaseCameraHolder cameraHolder, I_WorkeDone i_workeDone, Handler handler, boolean externalSD) {
        super(cameraHolder, i_workeDone, handler, externalSD);
        this.ParameterHandlerx = cameraHolder.ParameterHandler;
    }

    final String TAG = "MediatekIMG";

    @Override
    public void TakePicture()
    {
        Log.d(TAG, "Start Take Picture");
        if (cameraHolder.ParameterHandler.ZSL != null && cameraHolder.ParameterHandler.ZSL.IsSupported() && cameraHolder.ParameterHandler.ZSL.GetValue().equals("on"))
        {
            iWorkeDone.OnError("Error: Disable ZSL for Raw or Dng capture");

            return;
        }
        awaitpicture = true;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(cameraHolder.ParameterHandler.PictureFormat.GetValue().equals("jpeg+raw"))
                {
                    String timestamp = String.valueOf(System.currentTimeMillis());
                            ((CamParametersHandler) ParameterHandlerx).setString("rawfname", "/mnt/sdcard/DCIM/FreeDCam/"+timestamp+".raw");
                    cameraHolder.SetCameraParameters(((CamParametersHandler) ParameterHandlerx).getParameters());
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
        Log.d(TAG, "Take Picture CallBack");
        handler.post(new Runnable() {
            @Override
            public void run()
            {
                holdFile = new File(StringUtils.getFilePath(externalSd, fileEnding));
                //final String lastBayerFormat = cameraHolder.ParameterHandler.PictureFormat.GetValue();

             //   Log.d(TAG,RawToDng.getFilePath());

                if(cameraHolder.ParameterHandler.PictureFormat.GetValue().equals("jpeg"))
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
                else if(cameraHolder.ParameterHandler.PictureFormat.GetValue().equals("jpeg+dng"))
                {
                    saveBytesToFile(data, holdFile);
                    CreateDNG_DeleteRaw();
                }
                else if(cameraHolder.ParameterHandler.PictureFormat.GetValue().equals("jpeg+raw"))
                {
                    saveBytesToFile(data, holdFile);

                }


            }
        });
    }

    private void CreateDNG_DeleteRaw()
    {
        final RawToDng dngConverter = RawToDng.GetInstance();


        byte[] data = null;
        try {
            while (!checkFileCanRead(DeviceSwitcher()))
            {
                Thread.sleep(100);
            }
            data = RawToDng.readFile(DeviceSwitcher());
            Log.d(TAG, "Filesize: " + data.length + " File:" +DeviceSwitcher().getAbsolutePath());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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

        dngConverter.WriteDNG(null);
        dngConverter.RELEASE();
        data = null;
        DeviceSwitcher().delete();
    }

    public String FeeDJNI(String msg)
    {
        Log.d(TAG,msg);

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

    public boolean checkFileCanRead(File file){
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
        return true;
    }


}
