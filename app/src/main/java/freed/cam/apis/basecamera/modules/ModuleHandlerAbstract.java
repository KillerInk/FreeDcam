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

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;

import com.troop.freedcam.R;

import java.util.AbstractMap;
import java.util.HashMap;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.utils.AppSettingsManager;
import freed.utils.Logger;

/**
 * Created by troop on 09.12.2014.
 */
public abstract class ModuleHandlerAbstract implements ModuleHandlerInterface
{
    private final String TAG = ModuleHandlerAbstract.class.getSimpleName();
    public AbstractMap<String, ModuleInterface> moduleList;
    protected ModuleInterface currentModule;
    protected CameraWrapperInterface cameraUiWrapper;

    private HandlerThread mBackgroundThread;
    protected Handler mBackgroundHandler;

    protected AppSettingsManager appSettingsManager;

    public ModuleHandlerAbstract(CameraWrapperInterface cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        moduleList = new HashMap<>();
        this.appSettingsManager = cameraUiWrapper.GetAppSettingsManager();
        startBackgroundThread();

    }

    /**
     * Load the new module
     * @param name of the module to load
     */
    @Override
    public void SetModule(String name) {
        if (currentModule !=null) {
            currentModule.DestroyModule();
            currentModule = null;
        }
        currentModule = moduleList.get(name);
        currentModule.InitModule();
        ModuleHasChanged(currentModule.ModuleName());
        Logger.d(TAG, "Set Module to " + name);
    }

    @Override
    public String GetCurrentModuleName() {
        if (currentModule != null)
            return currentModule.ModuleName();
        else return KEYS.MODULE_PICTURE;
    }

    @Override
    public @Nullable ModuleInterface GetCurrentModule() {
        if (currentModule != null)
            return currentModule;
        return null;
    }

    @Override
    public boolean DoWork() {
        if (currentModule != null) {
            currentModule.DoWork();
            return true;
        }
        else
            return false;
    }

    /**
     * Gets thrown when the module has changed
     * @param module the new module that gets loaded
     */
    public void ModuleHasChanged(final String module)
    {
        Intent intent = new Intent(cameraUiWrapper.getActivityInterface().getContext().getResources().getString(R.string.INTENT_MODULECHANGED));
        intent.putExtra(cameraUiWrapper.getActivityInterface().getContext().getResources().getString(R.string.INTENT_EXTRA_MODULECHANGED), module);
        cameraUiWrapper.getActivityInterface().getContext().sendBroadcast(intent);
    }

    public void onRecorderstateChanged(int state)
    {
        Intent intent = new Intent(cameraUiWrapper.getActivityInterface().getContext().getResources().getString(R.string.INTENT_RECORDSTATECHANGED));
        intent.putExtra(cameraUiWrapper.getActivityInterface().getContext().getResources().getString(R.string.INTENT_EXTRA_RECORDSTATECHANGED), state);
        cameraUiWrapper.getActivityInterface().getContext().sendBroadcast(intent);
    }

    //clears all listner this happens when the camera gets destroyed
    public void CLEAR()
    {
        stopBackgroundThread();
    }

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
        Logger.d(TAG,"stopBackgroundThread");
        if(mBackgroundThread == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
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
