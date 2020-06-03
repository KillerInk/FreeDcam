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

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

/**
 * Created by troop on 13.12.2014.
 */
public class YuvSizeModeApi2 extends BaseModeApi2
{
    private String size = "1920x1080";
    public YuvSizeModeApi2(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper,SettingKeys.YuvSize);
        setViewState(ViewState.Visible);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        fireStringValueChanged(valueToSet);
        SettingsManager.get(SettingKeys.YuvSize).set(valueToSet);
        size = valueToSet;
        if (setToCamera && SettingsManager.get(SettingKeys.PictureFormat).get().equals(FreedApplication.getStringFromRessources(R.string.pictureformat_yuv)))
        {
            cameraUiWrapper.stopPreviewAsync();
            cameraUiWrapper.startPreviewAsync();
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
        return SettingsManager.get(SettingKeys.YuvSize).getValues();
    }
}
