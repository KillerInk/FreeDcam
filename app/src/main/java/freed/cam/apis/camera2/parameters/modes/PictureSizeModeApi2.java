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
import freed.cam.apis.basecamera.CameraThreadHandler;
import freed.cam.apis.camera2.Camera2;
import freed.settings.SettingKeys;

/**
 * Created by troop on 13.12.2014.
 */
public class PictureSizeModeApi2 extends BaseModeApi2
{
    private String size = "1920x1080";
    public PictureSizeModeApi2(Camera2 cameraUiWrapper) {
        super(cameraUiWrapper,SettingKeys.PICTURE_SIZE);
        setViewState(ViewState.Visible);
    }

    @Override
    public void setStringValue(String valueToSet, boolean setToCamera)
    {
        fireStringValueChanged(valueToSet);
        settingsManager.get(SettingKeys.PICTURE_SIZE).set(valueToSet);
        size = valueToSet;
        if (setToCamera &&
                (settingsManager.get(SettingKeys.PICTURE_FORMAT).get().equals(FreedApplication.getStringFromRessources(R.string.pictureformat_jpeg))
                    || settingsManager.get(SettingKeys.PICTURE_FORMAT).get().equals(FreedApplication.getStringFromRessources(R.string.pictureformat_jpg_p_dng)))
                )
        {
            CameraThreadHandler.restartPreviewAsync();
        }
    }

    @Override
    public String getStringValue()
    {
        return size;
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Override
    public String[] getStringValues()
    {
        return settingsManager.get(SettingKeys.PICTURE_SIZE).getValues();
    }
}
