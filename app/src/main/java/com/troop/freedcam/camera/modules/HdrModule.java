package com.troop.freedcam.camera.modules;

import android.hardware.Camera;
import android.os.Build;
import android.util.Log;

import com.troop.androiddng.MainActivity;
import com.troop.androiddng.RawToDng;
import com.troop.freedcam.camera.BaseCameraHolder;


import com.troop.freedcam.manager.MediaScannerManager;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by troop on 16.08.2014.
 */
public class HdrModule extends PictureModule
{

    private static String TAG = "freedcam.HdrModule";

    int hdrCount = 0;
    boolean aeBrackethdr = false;
    File[] files;

    public HdrModule(BaseCameraHolder cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
        name = ModuleHandler.MODULE_HDR;
    }

    //I_Module START
    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public void DoWork()
    {
        if (!isWorking)
        {
            files = new File[3];
            hdrCount = 0;
            workstarted();
            takePicture();
        }
    }

    @Override
    public String ShortName() {
        return "HDR";
    }

    @Override
    public String LongName() {
        return "HDR";
    }

    @Override
    public boolean IsWorking() {
        return isWorking;
    }

    @Override
    public void LoadNeededParameters()
    {
        if (ParameterHandler.AE_Bracket != null && ParameterHandler.AE_Bracket.IsSupported() && ParameterHandler.isAeBracketActive)
        {
            aeBrackethdr = true;
            ParameterHandler.AE_Bracket.SetValue("true", true);
        }
    }

    @Override
    public void UnloadNeededParameters()
    {
        if (ParameterHandler.AE_Bracket != null && ParameterHandler.AE_Bracket.IsSupported())
        {
            aeBrackethdr = false;
            ParameterHandler.AE_Bracket.SetValue("false", true);
        }
    }

    //I_Module END

    protected void takePicture()
    {
        this.isWorking = true;
        new Thread(){
            @Override
            public void run() {

                Log.d(TAG, "Start Taking Picture");

                try
                {
                    if (!ParameterHandler.isAeBracketActive)
                    {
                        setExposureToCamera();
                        Thread.sleep(1000);
                    }

                    //soundPlayer.PlayShutter();
                    baseCameraHolder.TakePicture(shutterCallback,rawCallback,HdrModule.this);
                    Log.d(TAG, "Picture Taking is Started");
                }
                catch (Exception ex)
                {
                    Log.d(TAG,"Take Picture Failed");
                    ex.printStackTrace();
                }
            }
        }.start();

    }

    private void setExposureToCamera()
    {
        int value = 0;

        if (hdrCount == 0)
        {
           value = parametersHandler.ManualExposure.GetMinValue();
        }
        else if (hdrCount == 1)
            value = 0;
        else if (hdrCount == 2)
            value = parametersHandler.ManualExposure.GetMaxValue();
        Log.d(TAG, "Set HDR Exposure to :" + value + "for image count " + hdrCount);
        parametersHandler.ManualExposure.SetValue(value);
        Log.d(TAG, "HDR Exposure SET");
    }

    public void onPictureTaken(final byte[] data)
    {
        if (processCallbackData(data, saveFileRunner)) return;

        if (hdrCount == 3)
        {
            baseCameraHolder.StartPreview();

            if (ParameterHandler.PictureFormat.GetValue().contains("bayer") && parametersHandler.isDngActive)
            {
                new Thread()
                {
                    @Override
                    public void run()
                    {
                        for(int i = 0;i<files.length;i++)
                        {
                            byte[] rawdata = null;

                            try {
                                rawdata = MainActivity.readFile(files[i]);
                                Log.d(TAG, "Filesize: " + data.length);

                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            String raw[] = getRawSize();
                            int w = Integer.parseInt(raw[0]);
                            int h = Integer.parseInt(raw[1]);
                            String l;
                            String dngFile = files[i].getAbsolutePath().replace("raw", "dng");
                            if (lastBayerFormat != null)
                                l = lastBayerFormat.substring(lastBayerFormat.length() - 4);
                            else
                                l = parametersHandler.PictureFormat.GetValue().substring(parametersHandler.PictureFormat.GetValue().length() - 4);
                            final RawToDng dng = RawToDng.GetInstance(0, 0, 0, 0, 0, "", "0", 0);
                            dng.SetBayerData(rawdata, dngFile, w, h);
                            dng.WriteDNG(h, l, rawdata.length);
                            //RawToDng.ConvertRawBytesToDngFast( fin,finS,finW,finH,finL);
                            System.out.println("Current Expo" + hdrCount + " " + getStringAddTime());
                            if (files[i].delete() == true)
                                Log.d(TAG, "file: " + files[i].getName() + " deleted");
                            Log.d(TAG, "Start Media Scan " + file.getName());
                            MediaScannerManager.ScanMedia(Settings.context.getApplicationContext(), new File(dngFile));

                        }
                    }
                }.start();
            }
            workfinished(true);
            parametersHandler.ManualExposure.SetValue(0);
        }
        if (!ParameterHandler.isAeBracketActive && hdrCount < 3)
        {
            baseCameraHolder.StartPreview();
            //ParameterHandler.LockExposureAndWhiteBalance(true);
            takePicture();
        }
    }

    protected File createFileName(boolean bevorShot)
    {
        Log.d(TAG, "Create FileName");
        String s1 = getStringAddTime();
        s1 += "HDR" + this.hdrCount;
        hdrCount++;
        return  getFileAndChooseEnding(s1, bevorShot);
    }

    protected boolean processCallbackData(byte[] data, Runnable saveFileRunner) {
        if(data.length < 4500)
        {
            baseCameraHolder.errorHandler.OnError("Data size is < 4kb");
            isWorking = false;
            //baseCameraHolder.StartPreview();
            return true;
        }
        else
        {
            baseCameraHolder.errorHandler.OnError("Datasize : " + StringUtils.readableFileSize(data.length));
        }
        file = createFileName(true);
        files[hdrCount -1] = file;
        bytes = data;
        new Thread(saveFileRunner).start();

        //saveFileRunner.run();
        isWorking = false;


        return false;
    }

    protected Runnable saveFileRunner = new Runnable() {
        @Override
        public void run()
        {
            if (OverRidePath == "")
            {
                saveBytesToFile(bytes, file);
                if (!file.getAbsolutePath().endsWith("raw") || file.getAbsolutePath().endsWith("raw") && !parametersHandler.isDngActive)
                {
                    Log.d(TAG, "Start Media Scan " + file.getName());
                    MediaScannerManager.ScanMedia(Settings.context.getApplicationContext() , file);
                }

            }
            else
            {
                file = new File(OverRidePath);
                saveBytesToFile(bytes, file);

            }




        }
    };

    protected File getFileAndChooseEnding(String s1, boolean bevorShot)
    {
        String pictureFormat = ParameterHandler.PictureFormat.GetValue();
        if (rawFormats.contains(pictureFormat))
        {
            if (pictureFormat.contains("bayer-mipi") && parametersHandler.isDngActive && !bevorShot)
                return new File(s1 +"_" + pictureFormat +".dng");
            else
                return new File(s1 + "_" + pictureFormat + ".raw");

        }
        else if (pictureFormat.contains("yuv"))
        {
            return new File(s1 + "_" + pictureFormat + ".yuv");
        }
        else
        {
            if (jpegFormat.contains(pictureFormat))
                return new File((new StringBuilder(String.valueOf(s1))).append(".jpg").toString());
            if (jpsFormat.contains(pictureFormat))
                return new File((new StringBuilder(String.valueOf(s1))).append(".jps").toString());
        }
        return null;
    }


}
