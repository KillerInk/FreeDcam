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

package freed.cam.apis.basecamera.modules;


import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.provider.DocumentFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStateChanged;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.cam.ui.handler.MediaScannerManager;
import freed.utils.AppSettingsManager;
import freed.utils.Logger;
import freed.viewer.holder.FileHolder;

/**
 * Created by troop on 15.08.2014.
 */
public abstract class ModuleAbstract implements ModuleInterface
{

    protected boolean isWorking;
    public String name;

    protected CaptureStateChanged captureStateChangedListner;
    private final String TAG = ModuleAbstract.class.getSimpleName();
    protected AppSettingsManager appSettingsManager;
    protected CaptureStates currentWorkState;
    protected CameraWrapperInterface cameraUiWrapper;
    private HandlerThread mBackgroundThread;
    protected Handler mBackgroundHandler;


    public ModuleAbstract(CameraWrapperInterface cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.appSettingsManager = cameraUiWrapper.GetAppSettingsManager();

    }

    public void SetCaptureStateChangedListner(CaptureStateChanged captureStateChangedListner)
    {
        this.captureStateChangedListner = captureStateChangedListner;
    }

    /**
     * throw this when camera starts working to notify ui
     */
    protected void changeCaptureState(CaptureStates captureStates)
    {
        Logger.d(TAG, "work started");
        currentWorkState = captureStates;
        if (captureStateChangedListner != null)
            captureStateChangedListner.onCaptureStateChanged(captureStates);
    }

    protected void scanAndFinishFile(File file)
    {
        MediaScannerManager.ScanMedia(cameraUiWrapper.getContext(),file);
        cameraUiWrapper.GetModuleHandler().WorkFinished(new FileHolder(file, appSettingsManager.GetWriteExternal()));
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
    public void InitModule()
    {
        isWorking = false;
        startBackgroundThread();
    }

    /**
     * this gets called when module gets unloaded reset the parameters that where set on InitModule
     */
    @Override
    public  void DestroyModule()
    {
        stopBackgroundThread();
    }

    @Override
    public abstract String LongName();

    @Override
    public abstract String ShortName();

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread()
    {
        if(mBackgroundThread == null)
            return;
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) {
            mBackgroundThread.quitSafely();
        }
        else
            mBackgroundThread.quit();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
