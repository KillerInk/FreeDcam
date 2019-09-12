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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import freed.cam.apis.basecamera.CameraWrapperInterface;

import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.cam.events.CaptureStateChangedEvent;
import freed.cam.events.EventBusHelper;
import freed.cam.events.StartWorkEvent;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.viewer.holder.FileHolder;

/**
 * Created by troop on 15.08.2014.
 */
public abstract class ModuleAbstract implements ModuleInterface
{

    @Subscribe
    public void startWork(StartWorkEvent event)
    {
        DoWork();
    }


    protected boolean isWorking;
    protected boolean isLowStorage;
    public String name;

    //protected CaptureStateChanged captureStateChangedListner;
    private final String TAG = ModuleAbstract.class.getSimpleName();
    protected CaptureStates currentWorkState;
    protected CameraWrapperInterface cameraUiWrapper;
    protected Handler mBackgroundHandler;
    protected Handler mainHandler;
    //used to redirect workevents to the module subscribe to it.
    protected WorkFinishEvents workFinishEventsListner;


    public ModuleAbstract(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.mBackgroundHandler = mBackgroundHandler;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void setOverrideWorkFinishListner(WorkFinishEvents workFinishEvents)
    {
        this.workFinishEventsListner = workFinishEvents;
    }

    /**
     * throw this when camera starts working to notify ui
     */
    public void changeCaptureState(final CaptureStates captureStates)
    {
        Log.d(TAG, "work started");
        currentWorkState = captureStates;
        EventBusHelper.post(new CaptureStateChangedEvent(captureStates));
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

    @Override
    public void IsLowStorage(Boolean x) {
        isLowStorage = x;
    }

    /**
     * this gets called when the module gets loaded. set here specific parameters that are needed by the module
     */
    @Override
    public void InitModule()
    {
        EventBusHelper.register(this);
        isWorking = false;
    }

    /**
     * this gets called when module gets unloaded reset the parameters that where set on InitModule
     */
    @Override
    public void DestroyModule()
    {
        EventBusHelper.unregister(this);
    }

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
