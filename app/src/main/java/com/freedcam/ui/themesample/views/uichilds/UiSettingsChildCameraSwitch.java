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

package com.freedcam.ui.themesample.views.uichilds;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.freedcam.apis.basecamera.AbstractCameraUiWrapper;
import com.freedcam.apis.camera1.ExtendedSurfaceView;
import com.freedcam.apis.sonyremote.CameraUiWrapper;
import com.freedcam.ui.I_Activity;
import com.freedcam.utils.AppSettingsManager;

/**
 * Created by troop on 13.06.2015.
 */
public class UiSettingsChildCameraSwitch extends UiSettingsChild
{
    private AbstractCameraUiWrapper cameraUiWrapper;
    private int currentCamera = 0;
    public UiSettingsChildCameraSwitch(Context context) {
        super(context);
    }

    public UiSettingsChildCameraSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });
    }

    @Override
    public void SetStuff(I_Activity i_activity, String settingvalue,AppSettingsManager appSettingsManager) {
        super.SetStuff(i_activity, settingvalue,appSettingsManager);

        currentCamera = appSettingsManager.GetCurrentCamera();
        valueText.setText(getCamera(currentCamera));
    }

    public void SetCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        if (cameraUiWrapper instanceof CameraUiWrapper)
        {
            setVisibility(GONE);
        }
        else {
            setVisibility(VISIBLE);
        }
    }

    private void switchCamera()
    {
        int maxcams = cameraUiWrapper.cameraHolder.CameraCout();
        if (currentCamera++ >= maxcams - 1)
            currentCamera = 0;

        appSettingsManager.SetCurrentCamera(currentCamera);
        sendLog("Stop Preview and Camera");
        if (cameraUiWrapper.getSurfaceView() != null &&  cameraUiWrapper.getSurfaceView() instanceof ExtendedSurfaceView)
        {
            ((ExtendedSurfaceView)cameraUiWrapper.getSurfaceView()).SwitchViewMode();
        }
        cameraUiWrapper.StopCamera();
        cameraUiWrapper.StartCamera();
        valueText.setText(getCamera(currentCamera));
    }

    private String getCamera(int i)
    {
        if (i == 0)
            return "Back";
        else if (i == 1)
            return "Front";
        else
            return "3D";
    }

    @Override
    public String[] GetValues() {
        return null;
    }


}
