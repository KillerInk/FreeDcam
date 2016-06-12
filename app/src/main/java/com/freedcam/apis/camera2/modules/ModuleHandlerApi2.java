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

package com.freedcam.apis.camera2.modules;

import android.content.Context;

import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.modules.AbstractModuleHandler;
import com.freedcam.apis.basecamera.modules.IntervalModule;
import com.freedcam.utils.RenderScriptHandler;

/**
 * Created by troop on 12.12.2014.
 */
public class ModuleHandlerApi2 extends AbstractModuleHandler
{
    private static String TAG = "freedcam.ModuleHandler";
    private RenderScriptHandler renderScriptHandler;


    public  ModuleHandlerApi2 (Context context, I_CameraUiWrapper cameraUiWrapper, RenderScriptHandler renderScriptHandler)
    {
        super(context,cameraUiWrapper);
        this.renderScriptHandler = renderScriptHandler;
        initModules();
    }

    public void initModules()
    {
        PictureModuleApi2 pictureModuleApi2 = new PictureModuleApi2(context,cameraUiWrapper);
        moduleList.put(pictureModuleApi2.ModuleName(), pictureModuleApi2);
        IntervalModule intervalModule = new IntervalApi2(pictureModuleApi2,context,cameraUiWrapper);
        moduleList.put(intervalModule.ModuleName(), intervalModule);
        VideoModuleApi2 videoModuleApi2 = new VideoModuleApi2(context,cameraUiWrapper);
        moduleList.put(videoModuleApi2.ModuleName(), videoModuleApi2);
        StackingModuleApi2 stackingModuleApi2 = new StackingModuleApi2(context,cameraUiWrapper, renderScriptHandler);
        moduleList.put(stackingModuleApi2.ModuleName(), stackingModuleApi2);
        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner

    }

    @Override
    public String GetCurrentModuleName() {
        return super.GetCurrentModuleName();
    }
}
