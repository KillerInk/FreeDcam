package com.troop.freedcam.camera.modules.image_saver;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;

import java.io.File;

/**
 * Created by troop on 15.04.2015.
 */
public class RawSaver extends JpegSaver
{
    final public String fileEnding = ".raw";
    public RawSaver(BaseCameraHolder cameraHolder, I_WorkeDone i_workeDone, Handler handler) {
        super(cameraHolder, i_workeDone, handler);
    }

    @Override
    public void TakePicture()
    {
        if (cameraHolder.ParameterHandler.ZSL != null && cameraHolder.ParameterHandler.ZSL.IsSupported() && cameraHolder.ParameterHandler.ZSL.GetValue().equals("on"))
        {
            iWorkeDone.OnError("Error: Disable ZSL for Raw or Dng capture");

            return;
        }
        super.TakePicture();
    }

    @Override
    public void onPictureTaken(final byte[] data)
    {
        handler.post(new Runnable() {
            @Override
            public void run()
            {
                final String lastBayerFormat = cameraHolder.ParameterHandler.PictureFormat.GetValue();
                saveBytesToFile(data, new File(getStringAddTime() + lastBayerFormat + fileEnding));
            }
        });
    }
}
