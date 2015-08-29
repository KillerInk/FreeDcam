package com.troop.freedcam.camera.modules.image_saver;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.modules.I_Callbacks;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.manager.MediaScannerManager;
import com.troop.freedcam.utils.StringUtils;

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
    boolean externalSd = false;

    final public String fileEnding = ".jpg";
    boolean awaitpicture = false;

    public JpegSaver(BaseCameraHolder cameraHolder, I_WorkeDone i_workeDone, Handler handler, boolean externalSd)
    {
        this.cameraHolder = cameraHolder;
        this.iWorkeDone = i_workeDone;
        this.handler = handler;
        this.externalSd = externalSd;
    }

    public void TakePicture()
    {
        awaitpicture = true;
        cameraHolder.TakePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(final byte[] data)
    {
        if (awaitpicture == false)
            return;
        awaitpicture =false;
        handler.post(new Runnable() {
            @Override
            public void run() {
                saveBytesToFile(data, new File(StringUtils.getFilePath(externalSd, fileEnding)));
            }
        });


    }

    public void saveBytesToFile(byte[] bytes, File fileName)
    {
        checkFileExists(fileName);

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

    public void checkFileExists(File fileName) {
        if(!fileName.getParentFile().exists())
            fileName.getParentFile().mkdirs();
        if (!fileName.exists())
            try {
                fileName.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
