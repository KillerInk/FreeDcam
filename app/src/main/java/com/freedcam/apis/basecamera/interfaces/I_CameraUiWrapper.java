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

package com.freedcam.apis.basecamera.interfaces;

import android.view.SurfaceView;

import com.freedcam.apis.basecamera.AbstractFocusHandler;
import com.freedcam.apis.basecamera.modules.AbstractModuleHandler;
import com.freedcam.apis.basecamera.parameters.AbstractParameterHandler;
import com.freedcam.utils.AppSettingsManager;

/**
 * Created by troop on 09.12.2014.
 */
public interface I_CameraUiWrapper
{
    void StartCamera();
    void StopCamera();
    void StartPreview();
    void StopPreview();
    /**
     * Starts a new work with the current active module
     * the module must handle the workstate on its own if it gets hit twice while work is already in progress
     */
    void DoWork();
    I_CameraHolder GetCameraHolder();
    AbstractParameterHandler GetParameterHandler();
    AppSettingsManager GetAppSettingsManager();
    AbstractModuleHandler GetModuleHandler();
    SurfaceView getSurfaceView();
    AbstractFocusHandler getFocusHandler();
    void SetCameraChangedListner(I_CameraChangedListner cameraChangedListner);

    int getMargineLeft();
    int getMargineRight();
    int getMargineTop();
    int getPreviewWidth();
    int getPreviewHeight();

    boolean isAeMeteringSupported();

}
