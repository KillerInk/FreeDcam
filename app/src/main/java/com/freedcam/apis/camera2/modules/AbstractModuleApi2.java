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

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build.VERSION_CODES;
import android.view.Display;
import android.view.WindowManager;

import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.basecamera.modules.AbstractModule;
import com.freedcam.apis.camera2.CameraHolderApi2;


/**
 * Created by troop on 12.12.2014.
 */
public abstract class AbstractModuleApi2 extends AbstractModule implements I_PreviewWrapper
{
    protected com.freedcam.apis.camera2.parameters.ParameterHandler ParameterHandler;

    protected boolean isWorking = false;
    protected CameraHolderApi2 cameraHolder;
    protected Point displaySize;

    @TargetApi(VERSION_CODES.JELLY_BEAN_MR1)
    public AbstractModuleApi2(CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper);
        ParameterHandler = (com.freedcam.apis.camera2.parameters.ParameterHandler) cameraUiWrapper.GetParameterHandler();
        Display display = ((WindowManager)cameraUiWrapper.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        displaySize = new Point();

        display.getRealSize(displaySize);
    }

    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public boolean DoWork() {
        return true;
    }

    @Override
    public boolean IsWorking() {
        return isWorking;
    }

    @Override
    public void InitModule()
    {
        this.cameraHolder = (CameraHolderApi2)cameraUiWrapper.GetCameraHolder();
    }

    @Override
    public void DestroyModule() {

    }
}
