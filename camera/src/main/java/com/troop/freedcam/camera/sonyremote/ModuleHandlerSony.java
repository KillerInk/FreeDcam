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

package com.troop.freedcam.camera.sonyremote;

import com.troop.freedcam.R;

import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.modules.ModuleHandlerAbstract;
import com.troop.freedcam.camera.sonyremote.CameraHolderSony.I_CameraShotMode;
import com.troop.freedcam.camera.sonyremote.modules.PictureModuleSony;
import com.troop.freedcam.camera.sonyremote.modules.VideoModuleSony;
import com.troop.freedcam.camera.sonyremote.parameters.ParameterHandler;
import com.troop.freedcam.utils.Log;

/**
 * Created by troop on 13.12.2014.
 */
public class ModuleHandlerSony extends ModuleHandlerAbstract implements I_CameraShotMode
{
    private CameraHolderSony cameraHolder;
    private final String TAG = ModuleHandlerSony.class.getSimpleName();

    public ModuleHandlerSony(CameraControllerInterface cameraUiWrapper)
    {
        super(cameraUiWrapper);
    }

    public void initModules()
    {
        this.cameraHolder = (CameraHolderSony) cameraUiWrapper.getCameraHolder();
        ((ParameterHandler)cameraUiWrapper.getParameterHandler()).cameraShotMode = this;
        PictureModuleSony pic = new PictureModuleSony(cameraUiWrapper,mBackgroundHandler,mainHandler);
        moduleList.put(pic.ModuleName(), pic);
        VideoModuleSony mov = new VideoModuleSony(cameraUiWrapper,mBackgroundHandler,mainHandler);
        moduleList.put(mov.ModuleName(), mov);
        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner

    }

    @Override
    public void setModule(String name)
    {
        if (name.equals(ContextApplication.getStringFromRessources(R.string.module_video)))
            cameraHolder.SetShootMode("movie");
        else if (name.equals(ContextApplication.getStringFromRessources(R.string.module_picture)))
            cameraHolder.SetShootMode("still");
    }

    @Override
    public void onShootModeChanged(String mode)
    {
        Log.d(TAG, "ShotmodeChanged:" + mode);
        /*if (currentModule !=null) {
            currentModule.SetCaptureStateChangedListner(null);
        }*/
        if (mode.equals("still"))
        {
            currentModule = moduleList.get(ContextApplication.getStringFromRessources(R.string.module_picture));

            ModuleHasChanged(currentModule.ModuleName());
            //currentModule.SetCaptureStateChangedListner(workerListner);
            currentModule.InitModule();
        }
        else if (mode.equals("movie"))
        {
            currentModule = moduleList.get(ContextApplication.getStringFromRessources(R.string.module_video));
            ModuleHasChanged(currentModule.ModuleName());
            //currentModule.SetCaptureStateChangedListner(workerListner);
            currentModule.InitModule();
        }
    }

    @Override
    public void onShootModeValuesChanged(String[] modes) {

    }
}
