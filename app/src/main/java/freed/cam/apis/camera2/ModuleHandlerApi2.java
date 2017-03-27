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

package freed.cam.apis.camera2;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.IntervalModule;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.camera2.modules.AeBracketApi2;
import freed.cam.apis.camera2.modules.AfBracketApi2;
import freed.cam.apis.camera2.modules.IntervalApi2;
import freed.cam.apis.camera2.modules.PictureModuleApi2;
import freed.cam.apis.camera2.modules.VideoModuleApi2;
import freed.utils.RenderScriptHandler;

/**
 * Created by troop on 12.12.2014.
 */
public class ModuleHandlerApi2 extends ModuleHandlerAbstract
{
    private final String TAG = "cam.ModuleHandler";


    public  ModuleHandlerApi2 (CameraWrapperInterface cameraUiWrapper, RenderScriptHandler renderScriptHandler)
    {
        super(cameraUiWrapper);
        RenderScriptHandler renderScriptHandler1 = renderScriptHandler;
        initModules();
    }

    public void initModules()
    {
        PictureModuleApi2 pictureModuleApi2 = new PictureModuleApi2(cameraUiWrapper,mBackgroundHandler);
        moduleList.put(pictureModuleApi2.ModuleName(), pictureModuleApi2);
        IntervalModule intervalModule = new IntervalApi2(pictureModuleApi2, cameraUiWrapper,mBackgroundHandler);
        moduleList.put(intervalModule.ModuleName(), intervalModule);
        VideoModuleApi2 videoModuleApi2 = new VideoModuleApi2(cameraUiWrapper,mBackgroundHandler);
        moduleList.put(videoModuleApi2.ModuleName(), videoModuleApi2);
        AeBracketApi2 aeBracketApi2 = new AeBracketApi2(cameraUiWrapper,mBackgroundHandler);
        moduleList.put(aeBracketApi2.ModuleName(),aeBracketApi2);
        AfBracketApi2 afBracketApi2 = new AfBracketApi2(cameraUiWrapper,mBackgroundHandler);
        moduleList.put(afBracketApi2.ModuleName(), afBracketApi2);

    }

    @Override
    public String GetCurrentModuleName() {
        return super.GetCurrentModuleName();
    }
}
