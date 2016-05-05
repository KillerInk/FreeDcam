package com.freedcam.apis.basecamera.camera.modules;

import java.io.File;

/**
 * Created by troop on 25.08.2014.
 *
 * this interface should get used by the modules when the work is finishd to tell the ui that thumbview need updated
 */
public interface I_WorkEvent
{
    String WorkHasFinished(File filePath);
}
