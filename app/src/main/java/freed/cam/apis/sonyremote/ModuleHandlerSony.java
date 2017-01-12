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

package freed.cam.apis.sonyremote;

import android.util.Log;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.sonyremote.CameraHolderSony.I_CameraShotMode;
import freed.cam.apis.sonyremote.modules.PictureModuleSony;
import freed.cam.apis.sonyremote.modules.VideoModuleSony;
import freed.cam.apis.sonyremote.parameters.ParameterHandler;

/**
 * Created by troop on 13.12.2014.
 */
public class ModuleHandlerSony extends ModuleHandlerAbstract implements I_CameraShotMode
{
    private CameraHolderSony cameraHolder;
    private final String TAG = ModuleHandlerSony.class.getSimpleName();

    public ModuleHandlerSony(CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper);
    }

    public void initModules()
    {
        this.cameraHolder = (CameraHolderSony) cameraUiWrapper.GetCameraHolder();
        ((ParameterHandler)cameraUiWrapper.GetParameterHandler()).cameraShotMode = this;
        PictureModuleSony pic = new PictureModuleSony(cameraUiWrapper,mBackgroundHandler);
        moduleList.put(pic.ModuleName(), pic);
        VideoModuleSony mov = new VideoModuleSony(cameraUiWrapper,mBackgroundHandler);
        moduleList.put(mov.ModuleName(), mov);
        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner

    }

    @Override
    public void SetModule(String name)
    {
        if (name.equals(KEYS.MODULE_VIDEO))
            cameraHolder.SetShootMode("movie");
        else if (name.equals(KEYS.MODULE_PICTURE))
            cameraHolder.SetShootMode("still");
    }

    @Override
    public void onShootModeChanged(String mode)
    {
        Log.d(TAG, "ShotmodeChanged:" + mode);
        if (currentModule !=null) {
            currentModule.SetCaptureStateChangedListner(null);
        }
        if (mode.equals("still"))
        {
            currentModule = moduleList.get(KEYS.MODULE_PICTURE);

            ModuleHasChanged(currentModule.ModuleName());
            currentModule.SetCaptureStateChangedListner(workerListner);
            currentModule.InitModule();
        }
        else if (mode.equals("movie"))
        {
            currentModule = moduleList.get(KEYS.MODULE_VIDEO);
            ModuleHasChanged(currentModule.ModuleName());
            currentModule.SetCaptureStateChangedListner(workerListner);
            currentModule.InitModule();
        }
    }

    @Override
    public void onShootModeValuesChanged(String[] modes) {

    }
}
