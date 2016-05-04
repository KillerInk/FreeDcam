package com.freedcam.apis.camera1.camera.modules.image_saver;

import com.freedcam.apis.camera1.camera.BaseCameraHolder;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.StringUtils;


import java.io.File;

/**
 * Created by troop on 17.04.2015.
 */
public class JpsSaver extends JpegSaver
{
    final public String fileEnding = ".jps";
    public JpsSaver(BaseCameraHolder cameraHolder, I_WorkeDone i_workeDone) {
        super(cameraHolder, i_workeDone);
    }

    @Override
    public void TakePicture() {
        awaitpicture = true;
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                cameraHolder.TakePicture(null, JpsSaver.this);
            }
        });
    }

    @Override
    public void onPictureTaken(final byte[] data)
    {
        if (!awaitpicture)
            return;
        awaitpicture =false;
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                saveBytesToFile(data, new File(StringUtils.getFilePath(AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal(), fileEnding)),true);
            }
        });
    }
}
