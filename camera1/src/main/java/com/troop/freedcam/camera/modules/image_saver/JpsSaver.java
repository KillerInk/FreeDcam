package com.troop.freedcam.camera.modules.image_saver;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;

/**
 * Created by troop on 17.04.2015.
 */
public class JpsSaver extends JpegSaver
{
    final public String fileEnding = ".jps";
    public JpsSaver(BaseCameraHolder cameraHolder, I_WorkeDone i_workeDone, Handler handler) {
        super(cameraHolder, i_workeDone, handler);
    }
}
