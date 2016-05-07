package com.freedcam.apis.camera1.camera.modules.image_saver;

import android.content.Context;

import com.freedcam.Native.StaxxerJNI;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.basecamera.camera.Size;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;
import com.imageconverter.Staxxer;

import java.io.File;
import java.util.Date;


/**
 * Created by GeorgeKiarie on 13/04/2016.
 */
public class StackSaver extends JpegSaver {
    private final String TAG = JpegSaver.class.getSimpleName();
    private byte[] buffered = null;
    private int FrameCount = 0;
    Size size;
    private Staxxer staxxer;

    private StaxxerJNI jpg2rgb;
    private boolean NewSession = false;
    private String SessionFolder="";

    public StackSaver(CameraHolderApi1 cameraHolder, I_WorkeDone i_workeDone, Context context, AppSettingsManager appSettingsManager)
    {
        super(cameraHolder, i_workeDone,context,appSettingsManager);
        jpg2rgb = StaxxerJNI.GetInstance();

        staxxer = new Staxxer(new Size(ParameterHandler.PictureSize.GetValue()), context);
        staxxer.Enable();
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
                cameraHolder.TakePicture(null, StackSaver.this);
            }
        });
    }

    @Override
    public void onPictureTaken(final byte[] data)
    {
        if (!awaitpicture)
            return;
        awaitpicture =false;
        Logger.d(TAG, "Take Picture Callback");
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                File f = new File(StringUtils.getFilePath(appSettingsManager.GetWriteExternal(), fileEnding));
                processData(data, f);
            }
        });

    }




    private void processData(byte[] data, File file) {
        System.out.println("The Data Is " + data.length + " bytes Long" + " and the path is " + file.getAbsolutePath());
        //CameraUiWrapper LCUI = new CameraUiWrapper();

        int x = Integer.parseInt(ParameterHandler.PictureSize.GetValue().split("x")[0]);
        int y = Integer.parseInt(ParameterHandler.PictureSize.GetValue().split("x")[1]);

        if(!NewSession) {
             SessionFolder = "/sdcard/DCIM/FreeDcam/" + StringUtils.getStringDatePAttern().format(new Date()) + "/";

            NewSession = true;
        }


       File f = new File(SessionFolder+StringUtils.getStringDatePAttern().format(new Date())+".jpg");
       saveBytesToFile(data,f,true);


        if (FrameCount == 0) {
          //  jpg2rgb.StoreMerged();
            buffered = jpg2rgb.ExtractRGB(data);
           // jpg2rgb.RELEASE();
           // System.out.println("The buffer Data Is " + jpg2rgb.ExtractRGB(data).length + " bytes Long");
           // saveBytesToFile(buffered,f,true);
            iWorkeDone.OnWorkDone(file);
            FrameCount++;


        }
        else if(FrameCount == 1)
        {
            staxxer.Process(buffered, jpg2rgb.ExtractRGB(data),false,SessionFolder);
           // jpg2rgb.RELEASE();
            iWorkeDone.OnWorkDone(file);
            FrameCount++;
        }
        else
        {

            staxxer.Process(null, jpg2rgb.ExtractRGB(data),true,SessionFolder);
            iWorkeDone.OnWorkDone(file);
        }

    }
}
