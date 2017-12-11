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

package freed.cam.ui.themesample.settings.childs;

import android.content.Context;
import android.util.AttributeSet;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.Settings;

/**
 * Created by troop on 21.07.2015.
 */
public class SettingsChildMenuGPS extends SettingsChildMenu
{
    public SettingsChildMenuGPS(Context context) {
        super(context);
    }

    public SettingsChildMenuGPS(Context context, int headerid, int descriptionid) {
        super(context, headerid, descriptionid);
    }

    public SettingsChildMenuGPS(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void SetCameraUIWrapper(CameraWrapperInterface cameraUiWrapper)
    {
        SetParameter(cameraUiWrapper.getParameterHandler().get(Settings.locationParameter));

    }
}
