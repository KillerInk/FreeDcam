package com.troop.freedcam.sonyapi.modules;

import android.os.Environment;

import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.sonyapi.CameraHolderSony;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by troop on 22.12.2014.
 */
public class PictureModuleSony extends AbstractModule implements I_PictureCallback, I_CameraStatusChanged
{
    private static String TAG = StringUtils.TAG + PictureModuleSony.class.getSimpleName();
    CameraHolderSony cameraHolder;
    public PictureModuleSony() {
        super();
    }

    public PictureModuleSony(CameraHolderSony cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
        name = ModuleHandler.MODULE_PICTURE;
        this.cameraHolder = cameraHandler;
        cameraHolder.CameraStatusListner = this;
    }

    @Override
    public String ModuleName() {
        return super.ModuleName();
    }

    @Override
    public void DoWork()
    {
        String shootmode = cameraHolder.ParameterHandler.ContShootMode.GetValue();
        if (!this.isWorking && shootmode.equals("Single"))
            takePicture();
        else if (!this.isWorking)
        {
            cameraHolder.startContShoot(this);
        }
        else {
            cameraHolder.stopContShoot(this);
        }
    }

    @Override
    public boolean IsWorking() {
        return super.IsWorking();
    }

    @Override
    public void LoadNeededParameters()
    {

    }

    @Override
    public void UnloadNeededParameters() {
        super.UnloadNeededParameters();
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
    }

    @Override
    public void onPictureTaken(URL url)
    {

        File file = new File(getStringAddTime() + ".jpg");
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


        eventHandler.WorkFinished(file);


    }

    protected String getStringAddTime()
    {
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/FreeCam/");
        if (!file.exists())
            file.mkdirs();
        Date date = new Date();
        String s = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(date);
        return (new StringBuilder(String.valueOf(file.getPath()))).append(File.separator).append("IMG_").append(s).toString();
    }

    @Override
    public void onCameraStatusChanged(String status)
    {
        if (status.equals("IDLE") && isWorking)
        {
            this.isWorking = false;
            workfinished(true);
        }
        else if (status.equals("StillCapturing") && !isWorking) {
            this.isWorking = true;
            workstarted();
        }

    }
}
