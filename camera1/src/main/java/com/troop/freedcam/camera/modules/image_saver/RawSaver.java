package com.troop.freedcam.camera.modules.image_saver;

import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

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
                File f = new File(StringUtils.getFilePath(externalSd, fileEnding));
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT
                        || (!AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT))
                    saveBytesToFile(data, f);
                else
                {
                    Uri uri = Uri.parse(AppSettingsManager.APPSETTINGSMANAGER.GetBaseFolder());
                    DocumentFile df = DocumentFile.fromTreeUri(AppSettingsManager.APPSETTINGSMANAGER.context, uri);
                    DocumentFile wr = df.createFile("image/raw", f.getName());
                    try {
                        OutputStream outStream =  AppSettingsManager.APPSETTINGSMANAGER.context.getContentResolver().openOutputStream(wr.getUri());
                        outStream.write(data);
                        outStream.flush();
                        outStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    iWorkeDone.OnWorkDone(f);

                }
            }
        });
    }
}
