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

import android.media.CamcorderProfile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.VideoMediaProfile;
import freed.cam.apis.basecamera.modules.VideoMediaProfile.VideoMode;
import freed.cam.apis.basecamera.parameters.modes.AbstractModeParameter;
import freed.cam.apis.camera1.parameters.modes.VideoProfilesParameter;
import freed.cam.apis.camera2.CameraHolderApi2;

/**
 * Created by troop on 24.02.2016.
 */
public class VideoProfilesApi2 extends VideoProfilesParameter
{
    final String TAG = VideoProfilesApi2.class.getSimpleName();
    protected CameraWrapperInterface cameraUiWrapper;

    public VideoProfilesApi2(CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        profile = valueToSet;
        if (cameraUiWrapper.GetModuleHandler().GetCurrentModule() != null && cameraUiWrapper.GetModuleHandler().GetCurrentModuleName().equals(KEYS.MODULE_VIDEO))
        {
            cameraUiWrapper.GetModuleHandler().GetCurrentModule().DestroyModule();
            cameraUiWrapper.GetModuleHandler().GetCurrentModule().InitModule();
        }
    }


}
