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

package freed.cam.apis.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.os.Build.VERSION_CODES;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.SettingsManager;
import freed.settings.Settings;

/**
 * Created by troop on 13.12.2014.
 */
public class PictureSizeModeApi2 extends BaseModeApi2
{
    private String size = "1920x1080";
    public PictureSizeModeApi2(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
    }
    boolean firststart = true;
    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        fireStringValueChanged(valueToSet);
        size = valueToSet;
        if (setToCamera)
        {
            cameraUiWrapper.stopPreview();
            cameraUiWrapper.startPreview();
        }
    }

    @Override
    public String GetStringValue()
    {
        return size;
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Override
    public String[] getStringValues()
    {
        return SettingsManager.get(Settings.PictureSize).getValues();
    }
}
