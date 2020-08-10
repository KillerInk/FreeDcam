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

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.cam.events.CaptureStateChangedEvent;
import freed.cam.events.EventBusHelper;
import freed.file.holder.BaseHolder;
import freed.utils.Log;

/**
 * Created by troop on 15.08.2014.
 */
public abstract class ModuleAbstract implements ModuleInterface
{
    protected boolean isWorking;
    protected boolean isLowStorage;
    public String name;
    private final String TAG = ModuleAbstract.class.getSimpleName();
    protected CaptureStates currentWorkState;
    protected CameraWrapperInterface cameraUiWrapper;
    protected Handler mBackgroundHandler;
    protected Handler mainHandler;


    public ModuleAbstract(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.mBackgroundHandler = mBackgroundHandler;
        this.mainHandler = new Handler(Looper.getMainLooper());
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
     * its called when a saving task is done and the image/movie is rdy to get attached to the MainActivity/screenslideFragment
     * @param file that is new
     */
    @Override
    public void fireOnWorkFinish(BaseHolder file) {
        EventBusHelper.post(file);
    }



    /**
     * gets called when a capture session with more pics is done
        and the images are rdy to get attached to the MainActivity/screenslideFragment
     * @param files that are new
     */
    @Override
    public void fireOnWorkFinish(BaseHolder[] files)
    {
        EventBusHelper.post(files);
    }

    @Override
    public CaptureStates getCurrentCaptureState() {
        return currentWorkState;
    }
}
