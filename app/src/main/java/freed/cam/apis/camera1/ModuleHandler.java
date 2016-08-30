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

package freed.cam.apis.camera1;


import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.IntervalModule;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.camera1.CameraHolder.Frameworks;
import freed.cam.apis.camera1.modules.AeBracketModule;
import freed.cam.apis.camera1.modules.BracketModule;
import freed.cam.apis.camera1.modules.PictureModule;
import freed.cam.apis.camera1.modules.PictureModuleMTK;
import freed.cam.apis.camera1.modules.StackingModule;
import freed.cam.apis.camera1.modules.VideoModule;
import freed.cam.apis.camera1.modules.VideoModuleG3;
import freed.utils.Logger;

/**
 * Created by troop on 16.08.2014.
 */
public class ModuleHandler extends ModuleHandlerAbstract
{
    private final String TAG = "cam.ModuleHandler";


    public  ModuleHandler (CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper);
    }

    @Override
    public void initModules()
    {
        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner
        if (((CameraHolder) cameraUiWrapper.GetCameraHolder()).DeviceFrameWork == Frameworks.MTK)
        {
            Logger.d(TAG, "load mtk picmodule");
            PictureModuleMTK thl5000 = new PictureModuleMTK(cameraUiWrapper);
            moduleList.put(thl5000.ModuleName(), thl5000);
            IntervalModule intervalModule = new IntervalModule(thl5000, cameraUiWrapper);
            moduleList.put(intervalModule.ModuleName(), intervalModule);
        }
        else//else //use default pictureModule
        {
            Logger.d(TAG, "load default picmodule");
            PictureModule pictureModule = new PictureModule(cameraUiWrapper);
            moduleList.put(pictureModule.ModuleName(), pictureModule);
            IntervalModule intervalModule = new IntervalModule(pictureModule, cameraUiWrapper);
            moduleList.put(intervalModule.ModuleName(), intervalModule);
        }

        if (((CameraHolder) cameraUiWrapper.GetCameraHolder()).DeviceFrameWork == Frameworks.LG)
        {
            Logger.d(TAG, "load lg videomodule");
            VideoModuleG3 videoModuleG3 = new VideoModuleG3(cameraUiWrapper);
            moduleList.put(videoModuleG3.ModuleName(), videoModuleG3);
        }
        else
        {
            Logger.d(TAG, "load default videomodule");
            VideoModule videoModule = new VideoModule(cameraUiWrapper);
            moduleList.put(videoModule.ModuleName(), videoModule);
        }

        Logger.d(TAG, "load hdr module");
        if (((CameraHolder) cameraUiWrapper.GetCameraHolder()).DeviceFrameWork == Frameworks.MTK)
        {
            AeBracketModule bracketModule = new AeBracketModule(cameraUiWrapper);
            moduleList.put(bracketModule.ModuleName(), bracketModule);
        }
        else {
            BracketModule bracketModule = new BracketModule(cameraUiWrapper);
            moduleList.put(bracketModule.ModuleName(), bracketModule);
        }

        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT && cameraUiWrapper.getRenderScriptHandler().isSucessfullLoaded()) {
            StackingModule sTax = new StackingModule(cameraUiWrapper);
            moduleList.put(sTax.ModuleName(), sTax);
        }

        //BurstModule burstModule = new BurstModule(this.cameraHolder, soundPlayer, appSettingsManager, moduleEventHandler);
        //moduleList.put(burstModule.ModuleName(), burstModule);

    }

}
