package com.troop.freedcam.camera.modules.image_saver;

import android.os.Handler;
import android.util.Log;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;

/**
 * Created by troop on 15.04.2015.
 */
public class RawSaver extends JpegSaver
{
    final public String fileEnding = ".raw";
    public RawSaver(BaseCameraHolder cameraHolder, I_WorkeDone i_workeDone, Handler handler, boolean externalSD) {
        super(cameraHolder, i_workeDone, handler, externalSD);
    }

    final String TAG = "RawSaver";

    @Override
    public void TakePicture()
    {
        Logger.d(TAG, "Start Take Picture");
        if (ParameterHandler.ZSL != null && ParameterHandler.ZSL.IsSupported() && ParameterHandler.ZSL.GetValue().equals("on"))
        {
            iWorkeDone.OnError("Error: Disable ZSL for Raw or Dng capture");

            return;
        }
        awaitpicture = true;
        handler.post(new Runnable() {
            @Override
            public void run() {
                cameraHolder.TakePicture(null, null, RawSaver.this);
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
                final String lastBayerFormat = ParameterHandler.PictureFormat.GetValue();
                saveBytesToFile(data, new File(StringUtils.getFilePath(externalSd, fileEnding)));
            }
        });
    }
}
