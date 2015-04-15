package com.troop.freedcam.camera.modules.image_saver;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.modules.I_Callbacks;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.manager.MediaScannerManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by troop on 15.04.2015.
 */
public class JpegSaver implements I_Callbacks.PictureCallback
{

    String TAG = JpegSaver.class.getSimpleName();

    protected BaseCameraHolder cameraHolder;
    I_WorkeDone iWorkeDone;
    Handler handler;

    final String fileEnding = ".jpg";

    public JpegSaver(BaseCameraHolder cameraHolder, I_WorkeDone i_workeDone, Handler handler)
    {
        this.cameraHolder = cameraHolder;
        this.iWorkeDone = i_workeDone;
        this.handler = handler;
    }

    public void TakePicture()
    {
        cameraHolder.TakePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(final byte[] data)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                saveBytesToFile(data, new File(getStringAddTime() + fileEnding));
            }
        });


    }

    protected void saveBytesToFile(byte[] bytes, File fileName)
    {
        Log.d(TAG, "Start Saving Bytes");
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(fileName);
            outStream.write(bytes);
            outStream.flush();
            outStream.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "End Saving Bytes");
        iWorkeDone.OnWorkDone(fileName);

    }

    protected String getStringAddTime()
    {
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/FreeCam/");
        if (!file.exists())
            file.mkdirs();
        Date date = new Date();
        String s = (new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss")).format(date);
        return (new StringBuilder(String.valueOf(file.getPath()))).append(File.separator).append("IMG_").append(s).toString();
    }
}
