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


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.File;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStateChanged;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.settings.SettingsManager;
import freed.utils.Log;
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
    protected CaptureStates currentWorkState;
    protected CameraWrapperInterface cameraUiWrapper;
    protected Handler mBackgroundHandler;
    protected UiHandler mainHandler;

    private final int MSG_ONCAPTURESTATECHANGED = 0;

    public class UiHandler extends Handler
    {
        UiHandler(Looper looper)
        {
            super(looper);
        }



        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ONCAPTURESTATECHANGED:
                    if (captureStateChangedListner != null)
                        captureStateChangedListner.onCaptureStateChanged((CaptureStates)msg.obj);
                    break;
                default:
                super.handleMessage(msg);
            }
        }

    }

    public ModuleAbstract(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.mBackgroundHandler = mBackgroundHandler;
        this.mainHandler = new UiHandler(Looper.getMainLooper());
    }

    public void SetCaptureStateChangedListner(CaptureStateChanged captureStateChangedListner)
    {
        this.captureStateChangedListner = captureStateChangedListner;
        if (captureStateChangedListner != null)
            captureStateChangedListner.onCaptureStateChanged(currentWorkState);
    }

    /**
     * throw this when camera starts working to notify ui
     */
    public void changeCaptureState(final CaptureStates captureStates)
    {
        Log.d(TAG, "work started");
        currentWorkState = captureStates;
        mainHandler.obtainMessage(MSG_ONCAPTURESTATECHANGED, captureStates).sendToTarget();
    }

    @Override
    public String ModuleName() {
        return name;
    }


    @Override
    public abstract void DoWork();

    @Override
    public boolean IsWorking() {
        return isWorking;
    }

    /**
     * this gets called when the module gets loaded. set here specific parameters that are needed by the module
     */
    @Override
    public void InitModule()
    {
        isWorking = false;
    }

    /**
     * this gets called when module gets unloaded reset the parameters that where set on InitModule
     */
    @Override
    public abstract void DestroyModule();

    @Override
    public abstract String LongName();

    @Override
    public abstract String ShortName();

    /**
     * ts called when a saving task is done and the image/movie is rdy to get attached to the MainActivity/screenslideFragment
     * @param file that is new
     */
    @Override
    public void fireOnWorkFinish(File file) {
        cameraUiWrapper.getActivityInterface().WorkHasFinished(new FileHolder(file, SettingsManager.getInstance().GetWriteExternal()));
    }



    /**
     * gets called when a capture session with more pics is done
        and the images are rdy to to the MainActivity/screenslideFragment
     * @param files that are new
     */
    @Override
    public void fireOnWorkFinish(File files[])
    {
        FileHolder[] fileHolders = new FileHolder[files.length];
        int i= 0;
        for (File f : files) {
            if (f != null)
                fileHolders[i++] = new FileHolder(f, SettingsManager.getInstance().GetWriteExternal());
        }
        cameraUiWrapper.getActivityInterface().WorkHasFinished(fileHolders);
    }

    @Override
    public CaptureStates getCurrentCaptureState() {
        return currentWorkState;
    }
}
