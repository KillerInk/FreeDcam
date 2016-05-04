package com.freedcam.apis.camera1.camera.modules;

import android.content.Context;

import com.freedcam.apis.camera1.camera.BaseCameraHolder;
import com.freedcam.apis.camera1.camera.modules.image_saver.DngSaver;
import com.freedcam.apis.camera1.camera.modules.image_saver.I_WorkeDone;
import com.freedcam.apis.camera1.camera.modules.image_saver.JpegSaver;
import com.freedcam.apis.camera1.camera.modules.image_saver.JpsSaver;
import com.freedcam.apis.camera1.camera.modules.image_saver.RawSaver;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.i_camera.modules.AbstractModule;
import com.freedcam.apis.i_camera.modules.I_Callbacks;
import com.freedcam.apis.i_camera.modules.ModuleEventHandler;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.ui.handler.MediaScannerManager;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.StringUtils;

import java.io.File;

//import com.drew.metadata.exif.ExifDirectory;



/**
 * Created by troop on 15.08.2014.
 */
public class PictureModule extends AbstractModule implements I_WorkeDone {

    private static String TAG = PictureModule.class.getSimpleName();
    boolean dngcapture = false;
    private int burstcount = 0;
    ////////////
//defcomg 31-1-2015 Pull Orientation From Sesnor

    public String OverRidePath = "";
    BaseCameraHolder baseCameraHolder;
    boolean dngJpegShot = false;
    //public String aeBrackethdr = "";


    public PictureModule(BaseCameraHolder baseCameraHolder, ModuleEventHandler eventHandler, Context context,AppSettingsManager appSettingsManager)
    {
        super(baseCameraHolder, eventHandler,context,appSettingsManager);
        this.baseCameraHolder = baseCameraHolder;
        name = ModuleHandler.MODULE_PICTURE;
        ParameterHandler = baseCameraHolder.GetParameterHandler();
        this.baseCameraHolder = baseCameraHolder;
    }

    @Override
    public String ShortName() {
        return "Pic";
    }

    @Override
    public String LongName() {
        return "Picture";
    }

//I_Module START
    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public boolean DoWork()
    {
        if (!this.isWorking)
        {
            startworking();

            if (ParameterHandler.Burst != null && ParameterHandler.Burst.IsSupported() && ParameterHandler.Burst.GetValue() > 1)
            {
                FreeDPool.Execute(new Runnable() {
                    @Override
                    public void run() {
                        burstcount = 0;
                        baseCameraHolder.TakePicture(null, burstCallback);
                    }
                });

            }
            else {
                final String picFormat = ParameterHandler.PictureFormat.GetValue();
                if (picFormat.equals("jpeg") ) {
                    final JpegSaver jpegSaver = new JpegSaver(baseCameraHolder, this,context,appSettingsManager);
                    jpegSaver.TakePicture();
                } else if (picFormat.equals(StringUtils.FileEnding.JPS)) {
                    final JpsSaver jpsSaver = new JpsSaver(baseCameraHolder, this,context,appSettingsManager);
                    jpsSaver.TakePicture();
                }
                else if (ParameterHandler.IsDngActive() && picFormat.equals(StringUtils.FileEnding.DNG)) {
                    DngSaver dngSaver = new DngSaver(baseCameraHolder, this,context,appSettingsManager);
                    dngSaver.TakePicture();
                }
                else if (!ParameterHandler.IsDngActive() && (picFormat.equals(StringUtils.FileEnding.BAYER) || picFormat.equals(StringUtils.FileEnding.RAW))) {
                    final RawSaver rawSaver = new RawSaver(baseCameraHolder, this,context,appSettingsManager);
                    rawSaver.TakePicture();
                }
            }
        }
        return true;

    }

    private void sendMsg(final String msg)
    {
          baseCameraHolder.errorHandler.OnError(msg);
    }



    @Override
    public void LoadNeededParameters()
    {
        ((CamParametersHandler)ParameterHandler).PreviewFormat.SetValue("yuv420sp",true);
        if (ParameterHandler.VideoHDR != null && ParameterHandler.VideoHDR.IsSupported() && !ParameterHandler.VideoHDR.GetValue().equals("off"))
            ParameterHandler.VideoHDR.SetValue("off", true);
        if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES))
            ((CamParametersHandler)ParameterHandler).SetZTESlowShutter();
    }

    @Override
    public void UnloadNeededParameters()
    {
    }


    void startworking()
    {
        isWorking = true;
        workstarted();
    }

    void stopworking()
    {
        isWorking = false;
        workfinished(true);
    }

    @Override
    public void OnWorkDone(File file)
    {
        baseCameraHolder.StartPreview();
        MediaScannerManager.ScanMedia(context.getApplicationContext() , file);
        stopworking();
        eventHandler.WorkFinished(file);
    }

    @Override
    public void OnError(String error)
    {
        sendMsg(error);
        stopworking();

    }

    private I_Callbacks.PictureCallback burstCallback = new I_Callbacks.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data)
        {
            FreeDPool.Execute(new Runnable() {
                @Override
                public void run() {
                    final String picFormat = ParameterHandler.PictureFormat.GetValue();
                    if (picFormat.equals("jpeg")) {
                        final JpegSaver jpegSaver = new JpegSaver(baseCameraHolder, burstDone,context,appSettingsManager);
                        jpegSaver.saveBytesToFile(data, new File(StringUtils.getFilePathBurst(appSettingsManager.GetWriteExternal(), jpegSaver.fileEnding, burstcount)),true);
                    } else if (picFormat.equals("jps")) {
                        final JpsSaver jpsSaver = new JpsSaver(baseCameraHolder, burstDone,context,appSettingsManager);
                        jpsSaver.saveBytesToFile(data, new File(StringUtils.getFilePathBurst(appSettingsManager.GetWriteExternal(), jpsSaver.fileEnding, burstcount)),true);
                    } else if (!ParameterHandler.IsDngActive() && (picFormat.equals(StringUtils.FileEnding.BAYER) || picFormat.equals(StringUtils.FileEnding.RAW))) {
                        final RawSaver rawSaver = new RawSaver(baseCameraHolder, burstDone,context,appSettingsManager);
                        rawSaver.saveBytesToFile(data, new File(StringUtils.getFilePathBurst(appSettingsManager.GetWriteExternal(), rawSaver.fileEnding, burstcount)),true);
                    } else if (ParameterHandler.IsDngActive() && picFormat.contains(StringUtils.FileEnding.DNG)) {
                        DngSaver dngSaver = new DngSaver(baseCameraHolder, burstDone,context,appSettingsManager);
                        dngSaver.processData(data, new File(StringUtils.getFilePathBurst(appSettingsManager.GetWriteExternal(), dngSaver.fileEnding, burstcount)), true);
                    }
                }
            });

        }
    };

    private I_WorkeDone burstDone = new I_WorkeDone() {
        @Override
        public void OnWorkDone(File file) {
            MediaScannerManager.ScanMedia(context.getApplicationContext(), file);
            if (burstcount == ParameterHandler.Burst.GetValue() -1) {
                stopworking();
                baseCameraHolder.StartPreview();
            }
            else if (burstcount < ParameterHandler.Burst.GetValue() -1)
                burstcount++;
        }

        @Override
        public void OnError(String error)
        {
            baseCameraHolder.errorHandler.OnError(error);
            stopworking();
        }
    };

}
