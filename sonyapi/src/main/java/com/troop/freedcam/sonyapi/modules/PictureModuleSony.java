package com.troop.freedcam.sonyapi.modules;

import android.util.Log;

import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.manager.MediaScannerManager;
import com.troop.freedcam.sonyapi.CameraHolderSony;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by troop on 22.12.2014.
 */
public class PictureModuleSony extends AbstractModule implements I_PictureCallback, I_CameraStatusChanged
{
    private static String TAG = PictureModuleSony.class.getSimpleName();
    private CameraHolderSony cameraHolder;
    private PictureModuleSony() {
        super();
    }

    public PictureModuleSony(CameraHolderSony cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
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
                cameraHolder.startContShoot();
                return true;
            } else {
                cameraHolder.stopContShoot();
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
        Log.d(TAG,"LoadNeededParameters");
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
        Log.d(TAG, "Start Take Picture");
    }

    @Override
    public void onPictureTaken(URL url)
    {
        File file = new File(StringUtils.getFilePath(Settings.GetWriteExternal(), ".jpg"));
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream inputStream = null;
        FileOutputStream output = null;
        try {
            inputStream = new BufferedInputStream(url.openStream());
            output = new FileOutputStream(file);
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (output != null)
                    output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        MediaScannerManager.ScanMedia(Settings.context.getApplicationContext(), file);
        eventHandler.WorkFinished(file);
    }


    @Override
    public void onCameraStatusChanged(String status)
    {
        Log.d(TAG, "Status:"+status);
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
