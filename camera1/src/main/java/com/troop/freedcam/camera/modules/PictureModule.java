package com.troop.freedcam.camera.modules;

import android.os.Build;
import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.modules.image_saver.DngSaver;
import com.troop.freedcam.camera.modules.image_saver.I_WorkeDone;
import com.troop.freedcam.camera.modules.image_saver.JpegSaver;
import com.troop.freedcam.camera.modules.image_saver.JpsSaver;
import com.troop.freedcam.camera.modules.image_saver.RawSaver;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.i_camera.modules.I_Callbacks;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.manager.MediaScannerManager;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.MetaDataExtractor;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;

//import com.drew.metadata.exif.ExifDirectory;



/**
 * Created by troop on 15.08.2014.
 */
public class PictureModule extends AbstractModule implements I_WorkeDone {

    private static String TAG = PictureModule.class.getSimpleName();
    boolean dngcapture = false;

    int burstcount = 0;

    //private HandlerThread backgroundThread;
    Handler handler;
    ////////////
//defcomg 31-1-2015 Pull Orientation From Sesnor

    public String OverRidePath = "";
    BaseCameraHolder baseCameraHolder;
    boolean dngJpegShot = false;
    //public String aeBrackethdr = "";


    public PictureModule(BaseCameraHolder baseCameraHolder, ModuleEventHandler eventHandler, Handler backgroundHandler)
    {
        super(baseCameraHolder, eventHandler);
        this.baseCameraHolder = baseCameraHolder;
        this.handler = backgroundHandler;
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
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        burstcount = 0;
                        baseCameraHolder.TakePicture(null,null, burstCallback);
                    }
                });

            }
            else {
                final String picFormat = ParameterHandler.PictureFormat.GetValue();
                if (picFormat.equals("jpeg") ) {
                    final JpegSaver jpegSaver = new JpegSaver(baseCameraHolder, this, handler);
                    jpegSaver.TakePicture();
                } else if (picFormat.equals(StringUtils.FileEnding.JPS)) {
                    final JpsSaver jpsSaver = new JpsSaver(baseCameraHolder, this, handler);
                    jpsSaver.TakePicture();
                }
                else if (ParameterHandler.IsDngActive() && picFormat.equals(StringUtils.FileEnding.DNG)) {
                    DngSaver dngSaver = new DngSaver(baseCameraHolder, this, handler);
                    dngSaver.TakePicture();
                }
                else if (ParameterHandler.IsDngActive() == false && (picFormat.equals(StringUtils.FileEnding.BAYER) || picFormat.equals(StringUtils.FileEnding.RAW))) {
                    final RawSaver rawSaver = new RawSaver(baseCameraHolder, this, handler);
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
            ((CamParametersHandler)ParameterHandler).SetZTESlowShutter("-1");
    }

    @Override
    public void UnloadNeededParameters()
    {
    }


    protected void startworking()
    {
        isWorking = true;
        workstarted();
    }

    protected void stopworking()
    {
        isWorking = false;
        workfinished(true);
    }

    @Override
    public void OnWorkDone(File file)
    {
        baseCameraHolder.StartPreview();
        MediaScannerManager.ScanMedia(AppSettingsManager.APPSETTINGSMANAGER.context.getApplicationContext() , file);
        stopworking();
        eventHandler.WorkFinished(file);
    }

    @Override
    public void OnError(String error)
    {
        sendMsg(error);
        stopworking();

    }

    I_Callbacks.PictureCallback burstCallback = new I_Callbacks.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data)
        {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    final String picFormat = ParameterHandler.PictureFormat.GetValue();
                    if (picFormat.equals("jpeg")) {
                        final JpegSaver jpegSaver = new JpegSaver(baseCameraHolder, burstDone,handler);
                        jpegSaver.saveBytesToFile(data, new File(StringUtils.getFilePathBurst(AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal(), jpegSaver.fileEnding, burstcount)));
                    }
                    else if (picFormat.equals("jps")) {
                        final JpsSaver jpsSaver = new JpsSaver(baseCameraHolder, burstDone, handler);
                        jpsSaver.saveBytesToFile(data,  new File(StringUtils.getFilePathBurst(AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal(), jpsSaver.fileEnding, burstcount)));
                    }
                    else if (!ParameterHandler.IsDngActive() && (picFormat.equals(StringUtils.FileEnding.BAYER) || picFormat.equals(StringUtils.FileEnding.RAW))) {
                        final RawSaver rawSaver = new RawSaver(baseCameraHolder, burstDone, handler);
                        rawSaver.saveBytesToFile(data,  new File(StringUtils.getFilePathBurst(AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal(), rawSaver.fileEnding, burstcount)));
                    } else if (ParameterHandler.IsDngActive() && picFormat.contains(StringUtils.FileEnding.DNG)) {
                        DngSaver dngSaver = new DngSaver(baseCameraHolder, burstDone, handler);
                        dngSaver.processData(data, new File(StringUtils.getFilePathBurst(AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal(), dngSaver.fileEnding, burstcount)));
                    }
                }
            });

        }
    };

    I_WorkeDone burstDone = new I_WorkeDone() {
        @Override
        public void OnWorkDone(File file) {
            MediaScannerManager.ScanMedia(AppSettingsManager.APPSETTINGSMANAGER.context.getApplicationContext(), file);
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
