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

package freed.cam.apis.camera2.modules;

import android.annotation.TargetApi;
import android.os.Build.VERSION_CODES;
import android.os.Handler;

import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.modules.ModuleAbstract;
import freed.cam.apis.camera2.Camera2;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.parameters.ParameterHandlerApi2;
import freed.cam.previewpostprocessing.PreviewController;


/**
 * Created by troop on 12.12.2014.
 */
public abstract class AbstractModuleApi2 extends ModuleAbstract<Camera2> implements I_PreviewWrapper
{
    ParameterHandlerApi2 parameterHandler;

    boolean isWorking;
    CameraHolderApi2 cameraHolder;
    protected PreviewController previewController;

    @TargetApi(VERSION_CODES.JELLY_BEAN_MR1)
    AbstractModuleApi2(Camera2 cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler)
    {
        super(cameraUiWrapper,mBackgroundHandler,mainHandler);
        parameterHandler = cameraUiWrapper.getParameterHandler();
        previewController = ActivityFreeDcamMain.previewController();
    }

    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public void DoWork() {
    }

    @Override
    public boolean IsWorking() {
        return isWorking;
    }

    @Override
    public void InitModule()
    {
        super.InitModule();
        this.cameraHolder = cameraUiWrapper.getCameraHolder();
    }
}
