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

package freed.cam.ui.themesample.cameraui.childs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import freed.cam.apis.basecamera.CameraThreadHandler;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.SettingsManager;

/**
 * Created by troop on 13.06.2015.
 */
@AndroidEntryPoint
public class UiSettingsChildCameraSwitch extends UiSettingsChild
{
    private int currentCamera;

    @Inject
    SettingsManager settingsManager;

    public UiSettingsChildCameraSwitch(Context context) {
        super(context);
    }

    public UiSettingsChildCameraSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
    }

    public void SetCameraUiWrapper(CameraWrapperInterface cameraUiWrapper)
    {

        setVisibility(View.VISIBLE);
        currentCamera = settingsManager.GetCurrentCamera();
        binding.textView2.setText(getCamera(currentCamera));
    }

    @Override
    public void SetValue(String value)
    {
        String[] split = value.split(" ");
        currentCamera = Integer.parseInt(split[1]);
        settingsManager.SetCurrentCamera(currentCamera);
        CameraThreadHandler.restartCameraAsync();
        binding.textView2.setText(getCamera(currentCamera));
    }


    private String getCamera(int i)
    {
        if (settingsManager.getIsFrontCamera())
            return "Front " + i;
        else
            return "Back " + i;
    }

    @Override
    public String[] GetValues() {
        int[] camids = settingsManager.getCameraIds();
        String[] retarr = new String[camids.length];
        for (int i = 0; i < camids.length; i++)
        {
            if (settingsManager.getCamIsFrontCamera(i))
                retarr[i] = "Front "+i;
            else
                retarr[i] = "Back "+i;
        }
        return retarr;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
