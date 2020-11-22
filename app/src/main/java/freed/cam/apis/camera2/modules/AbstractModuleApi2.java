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
import android.content.Context;
import android.graphics.Point;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.renderscript.RenderScript;
import android.view.Display;
import android.view.WindowManager;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleAbstract;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.parameters.ParameterHandlerApi2;
import freed.settings.SettingsManager;
import freed.utils.Log;


/**
 * Created by troop on 12.12.2014.
 */
public abstract class AbstractModuleApi2 extends ModuleAbstract implements I_PreviewWrapper
{
    ParameterHandlerApi2 parameterHandler;

    boolean isWorking;
    CameraHolderApi2 cameraHolder;
    private boolean renderScriptError5 = false;

    @TargetApi(VERSION_CODES.JELLY_BEAN_MR1)
    AbstractModuleApi2(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler)
    {
        super(cameraUiWrapper,mBackgroundHandler,mainHandler);
        parameterHandler = (ParameterHandlerApi2) cameraUiWrapper.getParameterHandler();
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
        this.cameraHolder = (CameraHolderApi2) cameraUiWrapper.getCameraHolder();
    }

    //use to workaround the problem with activated renderscript when switching back from a non renderscript session
    protected class MyRSErrorHandler extends RenderScript.RSErrorHandler
    {
        @Override
        public void run() {
            super.run();
            Log.e(MyRSErrorHandler.class.getSimpleName(), mErrorNum +":"+ mErrorMessage);
            if (mErrorNum == 5) // Error:5 setting IO output buffer usage.
            {
                renderScriptError5 = true;
                if (renderScriptError5)
                {
                    renderScriptError5 = false;
                    //clear the error else it trigger over and over....
                    mErrorNum = 0;
                    mErrorMessage = null;
                    //Restart the module
                    mBackgroundHandler.post(() -> {
                        Log.e(MyRSErrorHandler.class.getSimpleName(), "RS5 ERROR; RELOAD MODULE");
                        try {
                            cameraUiWrapper.getModuleHandler().setModule(SettingsManager.getInstance().GetCurrentModule());
                        }
                        catch (NullPointerException ex)
                        {
                            Log.WriteEx(ex);
                        }
                    });
                }
            }
        }
    }

}
