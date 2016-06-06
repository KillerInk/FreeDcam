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

package com.freedcam.apis.basecamera.apis;

import android.support.v4.app.Fragment;
import android.view.View;

import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;
import com.freedcam.utils.RenderScriptHandler;

/**
 * Created by troop on 06.06.2015.
 * That Fragment is used as base for all camera apis added.
 */
public abstract class AbstractCameraFragment extends Fragment
{
    private final String TAG = AbstractCameraFragment.class.getSimpleName();

    //the cameraWrapper to hold
    protected AbstractCameraUiWrapper cameraUiWrapper;
    protected View view;
    //the even listner when the camerauiwrapper is rdy to get attached to ui
    protected CamerUiWrapperRdy onrdy;
    //holds the appsettings
    protected AppSettingsManager appSettingsManager;
    protected RenderScriptHandler renderScriptHandler;
    public void SetRenderScriptHandler(RenderScriptHandler renderScriptHandler)
    {
        this.renderScriptHandler = renderScriptHandler;
    }

    public void SetAppSettingsManager(AppSettingsManager appSettingsManager)
    {
        this.appSettingsManager = appSettingsManager;
    }

    /**
     *
     * @return the current instance of the cameruiwrapper
     */
    public AbstractCameraUiWrapper GetCameraUiWrapper()
    {
        return cameraUiWrapper;
    }

    /**
     *
     * @param rdy the listner that gets thrown when the cameraUIwrapper
     *            has loaded all stuff and is rdy to get attached to ui.
     */
    public void Init(CamerUiWrapperRdy rdy)
    {
        this.onrdy = rdy;
    }


    /**
     * shutdown the current camera instance
     */
    public void DestroyCameraUiWrapper()
    {
        if (cameraUiWrapper != null)
        {
            Logger.d(TAG, "Destroying Wrapper");
            cameraUiWrapper.parametersHandler.CLEAR();
            cameraUiWrapper.moduleHandler.moduleEventHandler.CLEAR();
            cameraUiWrapper.moduleHandler.CLEARWORKERLISTNER();
            cameraUiWrapper.StopPreview();
            cameraUiWrapper.StopCamera();
            cameraUiWrapper = null;
            Logger.d(TAG, "destroyed cameraWrapper");
        }
    }

    /**
     * inteface for event listning when the camerauiwrapper is rdy
     */
    public interface CamerUiWrapperRdy
    {
        void onCameraUiWrapperRdy(AbstractCameraUiWrapper cameraUiWrapper);
    }

}
