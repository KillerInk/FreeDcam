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

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraControllerInterface;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;

/**
 * Created by troop on 17.08.2015.
 */
public class SettingsChildMenuVideoHDR extends SettingsChildMenu
{
    private CameraControllerInterface cameraControllerInterface;

    public SettingsChildMenuVideoHDR(Context context, ParameterInterface parameter, int headerid, int descriptionid) {
        super(context, parameter, headerid, descriptionid);
    }

    public SettingsChildMenuVideoHDR(Context context) {
        super(context);
    }

    public SettingsChildMenuVideoHDR(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void SetCameraInterface(CameraControllerInterface cameraControllerInterface)
    {
        this.cameraControllerInterface = cameraControllerInterface;
    }

    @Override
    public void SetValue(String value)
    {
        if (parameter != null &&  cameraControllerInterface.getModuleHandler().getCurrentModule() != null)
        {
            SettingsManager.get(SettingKeys.VideoHDR).set(value);
            if (cameraControllerInterface.getModuleHandler().getCurrentModule().ModuleName().equals(FreedApplication.getStringFromRessources(R.string.module_video)))
                parameter.SetValue(value, true);
            onStringValueChanged(value);
        }
    }

}
