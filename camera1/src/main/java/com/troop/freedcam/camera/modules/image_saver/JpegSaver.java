package com.troop.freedcam.camera.modules.image_saver;

import android.net.Uri;
import android.os.Handler;
import android.support.v4.provider.DocumentFile;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.modules.I_Callbacks;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
    protected CamParametersHandler ParameterHandler;

    public JpegSaver(BaseCameraHolder cameraHolder, I_WorkeDone i_workeDone, Handler handler, boolean externalSd)
    {
        this.cameraHolder = cameraHolder;
        this.ParameterHandler = (CamParametersHandler)cameraHolder.GetParameterHandler();
        this.iWorkeDone = i_workeDone;
        this.handler = handler;
        this.externalSd = externalSd;
    }

    public void TakePicture()
    {
        awaitpicture = true;
        handler.post(new Runnable() {
            @Override
            public void run() {
                cameraHolder.TakePicture(null, raw, JpegSaver.this);

            }
        });

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
                File f = new File(StringUtils.getFilePath(externalSd, fileEnding));
                if (!StringUtils.IS_L_OR_BIG()
                        || StringUtils.WRITE_NOT_EX_AND_L_ORBigger())
                    saveBytesToFile(data, f);
                else {
                    Uri uri = Uri.parse(AppSettingsManager.APPSETTINGSMANAGER.GetBaseFolder());
                    DocumentFile df = DocumentFile.fromTreeUri(AppSettingsManager.APPSETTINGSMANAGER.context, uri);
                    DocumentFile wr = df.createFile("image/jpeg", f.getName());
                    try {
                        OutputStream outStream = AppSettingsManager.APPSETTINGSMANAGER.context.getContentResolver().openOutputStream(wr.getUri());
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

    I_Callbacks.PictureCallback raw = new I_Callbacks.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data)
        {
            if (data != null)
            {
                Logger.d(TAG, "RawSize:" + data.length + "");
            }
            else
                Logger.d(TAG, "RawSize: null");
        }
    };

    public void saveBytesToFile(byte[] bytes, File fileName)
    {
        checkFileExists(fileName);

        Logger.d(TAG, "Start Saving Bytes");
        OutputStream outStream = null;
        try {
            if (!StringUtils.IS_L_OR_BIG()
                    || StringUtils.WRITE_NOT_EX_AND_L_ORBigger())
                outStream = new FileOutputStream(fileName);
            else
            {
                Uri uri = Uri.parse(AppSettingsManager.APPSETTINGSMANAGER.GetBaseFolder());
                DocumentFile df = DocumentFile.fromTreeUri(AppSettingsManager.APPSETTINGSMANAGER.context, uri);
                DocumentFile wr = df.createFile("image/jpeg", fileName.getName());
                outStream = AppSettingsManager.APPSETTINGSMANAGER.context.getContentResolver().openOutputStream(wr.getUri());
            }
            outStream.write(bytes);
            outStream.flush();
            outStream.close();


        } catch (FileNotFoundException e) {
            Logger.exception(e);
        } catch (IOException e) {
            Logger.exception(e);
        }
        Logger.d(TAG, "End Saving Bytes");
        iWorkeDone.OnWorkDone(fileName);

    }

    public void checkFileExists(File fileName) {
        if(!fileName.getParentFile().exists())
            fileName.getParentFile().mkdirs();
        if (!fileName.exists())
            try {
                fileName.createNewFile();
            } catch (IOException e) {
                Logger.exception(e);
            }
    }
}
