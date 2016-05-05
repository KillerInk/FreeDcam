package com.freedcam.apis.sonyremote.camera.modules;

import android.content.Context;
import android.support.v4.provider.DocumentFile;

import com.freedcam.apis.sonyremote.camera.CameraHolderSony;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.ui.handler.MediaScannerManager;
import com.freedcam.utils.FileUtils;
import com.freedcam.utils.Logger;
import com.freedcam.apis.basecamera.camera.modules.AbstractModule;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler;
import com.freedcam.apis.basecamera.camera.modules.ModuleEventHandler;

import com.freedcam.utils.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Created by troop on 22.12.2014.
 */
public class PictureModuleSony extends AbstractModule implements I_PictureCallback, I_CameraStatusChanged
{
    private static String TAG = PictureModuleSony.class.getSimpleName();
    private CameraHolderSony cameraHolder;

    public PictureModuleSony(CameraHolderSony cameraHandler, ModuleEventHandler eventHandler, Context context, AppSettingsManager appSettingsManager) {
        super(cameraHandler, eventHandler,context,appSettingsManager);
        name = AbstractModuleHandler.MODULE_PICTURE;
        this.cameraHolder = cameraHandler;


    }

    @Override
    public String ModuleName() {
        return super.ModuleName();
    }

    @Override
    public boolean DoWork()
    {
        if (cameraHolder.ParameterHandler.ContShootMode != null && cameraHolder.ParameterHandler.ContShootMode.IsSupported()) {
            String shootmode = cameraHolder.ParameterHandler.ContShootMode.GetValue();
            if (!this.isWorking && shootmode.equals("Single"))
                takePicture();
            else if (!this.isWorking) {
                cameraHolder.startContShoot(this);
                return true;
            } else {
                cameraHolder.stopContShoot(this);
                return false;
            }
        }
        else
            if (!this.isWorking)
                takePicture();
        return true;
    }

    @Override
    public boolean IsWorking() {
        return super.IsWorking();
    }

    @Override
    public void LoadNeededParameters()
    {
        Logger.d(TAG, "LoadNeededParameters");
        cameraHolder.CameraStatusListner = this;
        onCameraStatusChanged(cameraHolder.GetCameraStatus());
    }

    @Override
    public void UnloadNeededParameters() {

    }

    @Override
    public String LongName() {
        return "Picture";
    }

    @Override
    public String ShortName() {
        return "Pic";
    }


    private void takePicture()
    {
        cameraHolder.TakePicture(this);
        Logger.d(TAG, "Start Take Picture");
    }

    @Override
    public void onPictureTaken(URL url)
    {
        File file = new File(StringUtils.getFilePath(appSettingsManager.GetWriteExternal(), ".jpg"));
        try {
            file.createNewFile();
        } catch (IOException e) {
            Logger.exception(e);
        }
        InputStream inputStream = null;
        OutputStream output = null;
        try {
            inputStream = new BufferedInputStream(url.openStream());
            if (!StringUtils.IS_L_OR_BIG() ||StringUtils.WRITE_NOT_EX_AND_L_ORBigger(appSettingsManager))
                output = new FileOutputStream(file);
            else
            {
                DocumentFile df = FileUtils.getFreeDcamDocumentFolder(appSettingsManager,context);
                DocumentFile wr = df.createFile("image/jpeg", file.getName());
                output = context.getContentResolver().openOutputStream(wr.getUri());
            }
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
        } catch (IOException e) {
            Logger.exception(e);
        }
        finally
        {
            try
            {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                Logger.exception(e);
            }

            try {
                if (output != null)
                    output.close();
            } catch (IOException e) {
                Logger.exception(e);
            }
        }

        MediaScannerManager.ScanMedia(context.getApplicationContext(), file);
        eventHandler.WorkFinished(file);
    }


    @Override
    public void onCameraStatusChanged(String status)
    {
        Logger.d(TAG, "Status:"+status);
        if (status.equals("IDLE") && isWorking)
        {
            this.isWorking = false;
            workfinished(true);
        }
        else if ((status.equals("StillCapturing") || status.equals("StillSaving")) && !isWorking) {
            this.isWorking = true;
            workstarted();
        }

    }
}
