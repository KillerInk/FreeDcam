package com.troop.freedcam.camera.modules.image_saver;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;

/**
 * Created by troop on 15.04.2015.
 */
public class RawSaver extends JpegSaver
{
    final public String fileEnding = ".raw";
    public RawSaver(BaseCameraHolder cameraHolder, I_WorkeDone i_workeDone, Handler handler) {
        super(cameraHolder, i_workeDone, handler);
    }
}
