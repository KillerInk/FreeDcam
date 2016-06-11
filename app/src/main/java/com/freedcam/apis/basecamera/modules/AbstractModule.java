/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedcam.apis.basecamera.modules;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.provider.DocumentFile;

import com.freedcam.apis.basecamera.AbstractCameraHolder;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.interfaces.I_Module;
import com.freedcam.apis.basecamera.modules.AbstractModuleHandler.CaptureModes;
import com.freedcam.apis.basecamera.modules.AbstractModuleHandler.I_worker;
import com.freedcam.apis.basecamera.parameters.AbstractParameterHandler;
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

    protected boolean isWorking = false;
    public String name;

    protected ModuleEventHandler eventHandler;
    protected I_worker workerListner;
    private final String TAG = AbstractModule.class.getSimpleName();
    protected Context context;
    protected AppSettingsManager appSettingsManager;
    protected CaptureModes currentWorkState;
    protected I_CameraUiWrapper cameraUiWrapper;


    public AbstractModule(Context context, I_CameraUiWrapper cameraUiWrapper, ModuleEventHandler eventHandler)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.eventHandler = eventHandler;
        this.context = context;
        this.appSettingsManager = cameraUiWrapper.GetAppSettingsManager();

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
        if (workerListner != null)
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
            if (VERSION.SDK_INT <= VERSION_CODES.LOLLIPOP || VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP&& !appSettingsManager.GetWriteExternal())
            {
                checkFileExists(fileName);
                outStream = new FileOutputStream(fileName);
            }
            else
            {
                DocumentFile df = FileUtils.getFreeDcamDocumentFolder(appSettingsManager,context);
                Logger.d(TAG,"Filepath: " + df.getUri());
                DocumentFile wr = df.createFile("image/*", fileName.getName());
                Logger.d(TAG,"Filepath: " + wr.getUri());
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
        if (VERSION.SDK_INT <= VERSION_CODES.LOLLIPOP || !writetoExternalSD && VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP)
        {
            try {
                outStream= new FileOutputStream(file);
                bitmap.compress(CompressFormat.JPEG, 100, outStream);
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            DocumentFile df = FileUtils.getFreeDcamDocumentFolder(appSettingsManager,context);
            Logger.d(TAG,"Filepath: " + df.getUri());
            DocumentFile wr = df.createFile("image/*", file.getName());
            Logger.d(TAG,"Filepath: " + wr.getUri());
            try {
                outStream = context.getContentResolver().openOutputStream(wr.getUri());
                bitmap.compress(CompressFormat.JPEG, 100, outStream);
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
