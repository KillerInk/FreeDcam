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

import com.troop.freedcam.R;

import java.util.AbstractMap;
import java.util.HashMap;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.events.EventBusHelper;
import freed.cam.events.ModuleHasChangedEvent;
import freed.utils.BackgroundHandlerThread;
import freed.utils.Log;

/**
 * Created by troop on 09.12.2014.
 */
public abstract class ModuleHandlerAbstract implements ModuleHandlerInterface
{
    public enum CaptureStates
    {
        video_recording_stop,
        video_recording_start,
        image_capture_stop,
        image_capture_start,
        continouse_capture_start,
        continouse_capture_stop,
        continouse_capture_work_start,
        continouse_capture_work_stop,
        cont_capture_stop_while_working,
        cont_capture_stop_while_notworking,
        selftimerstart,
        selftimerstop
    }

    private final String TAG = ModuleHandlerAbstract.class.getSimpleName();
    public AbstractMap<String, ModuleInterface> moduleList;
    protected ModuleInterface currentModule;
    protected CameraWrapperInterface cameraUiWrapper;

    private BackgroundHandlerThread backgroundHandlerThread;

    protected Handler mBackgroundHandler;
    protected Handler mainHandler;

    public ModuleHandlerAbstract(CameraWrapperInterface cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        moduleList = new HashMap<>();
        backgroundHandlerThread = new BackgroundHandlerThread(TAG);
        backgroundHandlerThread.create();
        mBackgroundHandler = new Handler(backgroundHandlerThread.getThread().getLooper());
    }

    /**
     * Load the new module
     * @param name of the module to load
     */
    @Override
    public void setModule(String name) {
        if (currentModule !=null) {
            currentModule.DestroyModule();
            //currentModule.SetCaptureStateChangedListner(null);
            currentModule = null;
        }
        currentModule = moduleList.get(name);
        if(currentModule == null)
            currentModule = moduleList.get(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.module_picture));
        currentModule.InitModule();
        ModuleHasChanged(currentModule.ModuleName());
        //currentModule.SetCaptureStateChangedListner(workerListner);
        Log.d(TAG, "Set Module to " + name);
    }

    @Override
    public String getCurrentModuleName() {
        if (currentModule != null)
            return currentModule.ModuleName();
        else return cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.module_picture);
    }

    @Override
    public ModuleInterface getCurrentModule() {
        if (currentModule != null)
            return currentModule;
        return null;
    }

    @Override
    public boolean startWork() {
        if (currentModule != null) {
            currentModule.DoWork();
            return true;
        }
        else
            return false;
    }

    @Override
    public void SetIsLowStorage(Boolean x) {
        if( currentModule != null )
            currentModule.IsLowStorage(x);
    }

    /**
     * Gets thrown when the module has changed
     * @param module the new module that gets loaded
     */
    public void ModuleHasChanged(final String module)
    {
        EventBusHelper.post(new ModuleHasChangedEvent(module));
    }



}
