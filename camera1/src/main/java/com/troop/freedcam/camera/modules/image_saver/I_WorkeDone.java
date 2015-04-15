package com.troop.freedcam.camera.modules.image_saver;

import java.io.File;

/**
 * Created by troop on 15.04.2015.
 */
public interface I_WorkeDone
{
    void OnWorkDone(File file);
    void OnError(String error);
}
