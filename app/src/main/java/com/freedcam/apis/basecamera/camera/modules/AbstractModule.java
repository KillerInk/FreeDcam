package com.freedcam.apis.basecamera.camera.modules;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.provider.DocumentFile;

import com.freedcam.apis.basecamera.camera.AbstractCameraHolder;
import com.freedcam.apis.basecamera.camera.interfaces.I_Module;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler.CaptureModes;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler.I_worker;
import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.FileUtils;
import com.freedcam.utils.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by troop on 15.08.2014.
 */
public abstract class AbstractModule implements I_Module
{
    protected AbstractCameraHolder cameraHolder;
    protected AbstractParameterHandler ParameterHandler;

    protected boolean isWorking = false;
    public String name;

    protected ModuleEventHandler eventHandler;
    protected I_worker workerListner;
    final private String TAG = AbstractModule.class.getSimpleName();
    protected Context context;
    protected AppSettingsManager appSettingsManager;
    protected CaptureModes currentWorkState;


    public AbstractModule(AbstractCameraHolder cameraHandler, ModuleEventHandler eventHandler, Context context, AppSettingsManager appSettingsManager)
    {
        this.cameraHolder = cameraHandler;
        this.eventHandler = eventHandler;
        this.ParameterHandler = cameraHolder.GetParameterHandler();
        this.context = context;
        this.appSettingsManager = appSettingsManager;

    }

    public void SetWorkerListner(I_worker workerListner)
    {
        this.workerListner = workerListner;
    }

    /**
     * throw this when camera starts working to notify ui
     */
    protected void changeWorkState(CaptureModes captureModes)
    {
        Logger.d(TAG, "work started");
        currentWorkState = captureModes;
        if (this.workerListner != null)
            workerListner.onCaptureStateChanged(captureModes);
    }

    @Override
    public String ModuleName() {
        return name;
    }


    @Override
    public boolean DoWork() {
        return false;
    }

    @Override
    public boolean IsWorking() {
        return isWorking;
    }

    /**
     * this gets called when the module gets loaded. set here specific paramerters that are needed by the module
     */
    @Override
    public abstract void InitModule();

    /**
     * this gets called when module gets unloaded reset the parameters that where set on InitModule
     */
    @Override
    public abstract void DestroyModule();

    @Override
    public abstract String LongName();

    @Override
    public abstract String ShortName();

    public void saveBytesToFile(byte[] bytes, File fileName)
    {
        Logger.d(TAG, "Start Saving Bytes");
        OutputStream outStream = null;
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP&& !appSettingsManager.GetWriteExternal()))
            {
                checkFileExists(fileName);
                outStream = new FileOutputStream(fileName);
            }
            else
            {
                DocumentFile df = FileUtils.getFreeDcamDocumentFolder(appSettingsManager,context);
                Logger.d(TAG,"Filepath: " +df.getUri().toString());
                DocumentFile wr = df.createFile("image/*", fileName.getName());
                Logger.d(TAG,"Filepath: " +wr.getUri().toString());
                outStream = context.getContentResolver().openOutputStream(wr.getUri());
            }
            outStream.write(bytes);
            outStream.flush();
            outStream.close();


        } catch (IOException e) {
            Logger.exception(e);
        }
        Logger.d(TAG, "End Saving Bytes");
    }

    public void SaveBitmapToFile(Bitmap bitmap, File file)
    {
        OutputStream outStream = null;
        boolean writetoExternalSD = appSettingsManager.GetWriteExternal();
        Logger.d(TAG, "Write External " + writetoExternalSD);
        if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) || (!writetoExternalSD && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP))
        {
            try {
                outStream= new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            DocumentFile df = FileUtils.getFreeDcamDocumentFolder(appSettingsManager,context);
            Logger.d(TAG,"Filepath: " +df.getUri().toString());
            DocumentFile wr = df.createFile("image/*", file.getName());
            Logger.d(TAG,"Filepath: " +wr.getUri().toString());
            try {
                outStream = context.getContentResolver().openOutputStream(wr.getUri());
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void checkFileExists(File fileName)
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
