package com.troop.freedcam.camera.modules.image_saver;

import android.os.Build;

import com.troop.androiddng.RawToDng;
import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.Size;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.FreeDPool;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;

import troop.com.imageconverter.Staxxer;

/**
 * Created by GeorgeKiarie on 13/04/2016.
 */
public class StackSaver extends JpegSaver {
    final String TAG = DngSaver.class.getSimpleName();
    private byte[] buffered = null;
    private int FrameCount = 0;
    Size size;
    Staxxer staxxer;

    public StackSaver(BaseCameraHolder cameraHolder, I_WorkeDone i_workeDone)
    {
        super(cameraHolder, i_workeDone);
//        size = new Size(ParameterHandler.PictureSize.GetValue());

        staxxer = new Staxxer(/*size,*/AppSettingsManager.APPSETTINGSMANAGER.context);
    }

    @Override
    public void TakePicture()
    {
        Logger.d(TAG, "Start Take Picture");


            ParameterHandler.ZSL.SetValue("off", true);

        awaitpicture = true;

        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                cameraHolder.TakePicture(null, null, StackSaver.this);
            }
        });
    }

    @Override
    public void onPictureTaken(final byte[] data)
    {
        if (awaitpicture == false)
            return;
        awaitpicture =false;
        Logger.d(TAG, "Take Picture Callback");
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                File f = new File(StringUtils.getFilePath(AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal(), fileEnding));
                processData(data, f);
            }
        });

    }



    public void processData(byte[] data, File file) {
        System.out.println("The Data Is " + data.length + " bytes Long" + " and the path is " + file.getAbsolutePath());
        CameraUiWrapper LCUI = new CameraUiWrapper();


        if (FrameCount == 0) {
            buffered = data;
            iWorkeDone.OnWorkDone(file);
            FrameCount++;
        }
        else if(FrameCount == 1)
        {
            staxxer.Process(buffered,data,false);
            iWorkeDone.OnWorkDone(file);
            FrameCount++;
        }
        else
        {

            staxxer.Process(buffered,data,true);
            iWorkeDone.OnWorkDone(file);
        }

    }
}
