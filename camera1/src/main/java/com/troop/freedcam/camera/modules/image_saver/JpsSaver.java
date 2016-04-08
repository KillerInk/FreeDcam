package com.troop.freedcam.camera.modules.image_saver;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.FreeDPool;
import com.troop.freedcam.utils.StringUtils;

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
                cameraHolder.TakePicture(null, null, JpsSaver.this);
            }
        });
    }

    @Override
    public void onPictureTaken(final byte[] data)
    {
        if (awaitpicture == false)
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
