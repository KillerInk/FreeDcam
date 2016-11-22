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
import android.os.Handler;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.utils.AppSettingsManager;
import freed.utils.Logger;

/**
 * Created by troop on 15.08.2014.
 */
public abstract class ModuleAbstract implements ModuleInterface
{

    protected boolean isWorking;
    public String name;

    private final String TAG = ModuleAbstract.class.getSimpleName();
    protected AppSettingsManager appSettingsManager;
    protected CaptureStates currentWorkState;
    protected CameraWrapperInterface cameraUiWrapper;
    protected Handler mBackgroundHandler;


    public ModuleAbstract(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.appSettingsManager = cameraUiWrapper.GetAppSettingsManager();
        this.mBackgroundHandler = mBackgroundHandler;

    }

    /**
     * throw this when camera starts working to notify ui
     */
    protected void changeCaptureState(final CaptureStates captureStates)
    {
        Logger.d(TAG, "work started");
        currentWorkState = captureStates;
        Intent intent = new Intent("troop.com.freedcam.capturestateIntent");
        intent.putExtra("CaptureState", currentWorkState.ordinal());
        cameraUiWrapper.getActivityInterface().getContext().sendBroadcast(intent);
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
    public  void DestroyModule()
    {

    }

    @Override
    public abstract String LongName();

    @Override
    public abstract String ShortName();



}
