package com.troop.freedcam.camera.modules.image_saver;

import android.net.Uri;
import android.os.Handler;
import android.support.v4.provider.DocumentFile;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.modules.I_Callbacks;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.FreeDPool;
import com.troop.freedcam.utils.FileUtils;
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

    final public String fileEnding = ".jpg";
    boolean awaitpicture = false;
    protected CamParametersHandler ParameterHandler;

    public JpegSaver(BaseCameraHolder cameraHolder, I_WorkeDone i_workeDone)
    {
        this.cameraHolder = cameraHolder;
        this.ParameterHandler = (CamParametersHandler)cameraHolder.GetParameterHandler();
        this.iWorkeDone = i_workeDone;
    }

    public void TakePicture()
    {
        awaitpicture = true;
        FreeDPool.Execute(new Runnable() {
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
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                File f = new File(StringUtils.getFilePath(AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal(), fileEnding));
                if (!StringUtils.IS_L_OR_BIG()
                        || StringUtils.WRITE_NOT_EX_AND_L_ORBigger())
                    saveBytesToFile(data, f);
                else {
                    DocumentFile df = FileUtils.getFreeDcamDocumentFolder(true);
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
        Logger.d(TAG, "Start Saving Bytes");
        OutputStream outStream = null;
        try {
            if (!StringUtils.IS_L_OR_BIG() || StringUtils.WRITE_NOT_EX_AND_L_ORBigger())
            {
                checkFileExists(fileName);
                outStream = new FileOutputStream(fileName);
            }
            else
            {
                DocumentFile df = FileUtils.getFreeDcamDocumentFolder(true);
                Logger.d(TAG,"Filepath: " +df.getUri().toString());
                DocumentFile wr = df.createFile("image/jpeg", fileName.getName());
                Logger.d(TAG,"Filepath: " +wr.getUri().toString());
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

    public void checkFileExists(File fileName)
    {
        if (fileName.getParentFile() == null)
            return;
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
