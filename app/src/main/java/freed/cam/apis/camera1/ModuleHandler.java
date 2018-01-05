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


import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.IntervalModule;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.camera1.modules.BracketModule;
import freed.cam.apis.camera1.modules.PictureModule;
import freed.cam.apis.camera1.modules.PictureModuleMTK;
import freed.cam.apis.camera1.modules.VideoModule;
import freed.cam.apis.camera1.modules.VideoModuleG3;
import freed.settings.Frameworks;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 16.08.2014.
 */
public class ModuleHandler extends ModuleHandlerAbstract
{


    public  ModuleHandler (CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper);
    }

    @Override
    public void initModules()
    {
        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner
        String TAG = "cam.ModuleHandler";
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            Log.d(TAG, "load mtk picmodule");
            PictureModuleMTK thl5000 = new PictureModuleMTK(cameraUiWrapper,mBackgroundHandler,mainHandler);
            moduleList.put(thl5000.ModuleName(), thl5000);
            IntervalModule intervalModule = new IntervalModule(thl5000, cameraUiWrapper,mBackgroundHandler,mainHandler);
            moduleList.put(intervalModule.ModuleName(), intervalModule);
        }
        else//else //use default pictureModule
        {
            Log.d(TAG, "load default picmodule");
            PictureModule pictureModule = new PictureModule(cameraUiWrapper,mBackgroundHandler,mainHandler);
            moduleList.put(pictureModule.ModuleName(), pictureModule);
            IntervalModule intervalModule = new IntervalModule(pictureModule, cameraUiWrapper,mBackgroundHandler,mainHandler);
            moduleList.put(intervalModule.ModuleName(), intervalModule);
        }

        if (SettingsManager.getInstance().getFrameWork() == Frameworks.LG)
        {
            Log.d(TAG, "load lg videomodule");
            VideoModuleG3 videoModuleG3 = new VideoModuleG3(cameraUiWrapper,mBackgroundHandler,mainHandler);
            moduleList.put(videoModuleG3.ModuleName(), videoModuleG3);
        }
        else
        {
            Log.d(TAG, "load default videomodule");
            VideoModule videoModule = new VideoModule(cameraUiWrapper,mBackgroundHandler,mainHandler);
            moduleList.put(videoModule.ModuleName(), videoModule);
        }

        Log.d(TAG, "load hdr module");
        if (SettingsManager.getInstance().getFrameWork() != Frameworks.MTK)
        {
            BracketModule bracketModule = new BracketModule(cameraUiWrapper,mBackgroundHandler,mainHandler);
            moduleList.put(bracketModule.ModuleName(), bracketModule);
        }
    }

}
