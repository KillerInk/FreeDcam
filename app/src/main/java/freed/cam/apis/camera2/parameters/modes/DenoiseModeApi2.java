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
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build.VERSION_CODES;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera2.CameraHolderApi2;

/**
 * Created by troop on 05.05.2015.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class DenoiseModeApi2 extends BaseModeApi2
{
    public enum DeNoiseModes
    {
        OFF,
        FAST,
        HIGH_QUALITY,
        MINIMAL,
        ZEROSHUTTERLAG,

    }

    public DenoiseModeApi2(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
    }

    @Override
    public boolean IsSupported() {
        return cameraUiWrapper.GetCameraHolder() != null && ((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).get(CaptureRequest.NOISE_REDUCTION_MODE) != null;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.contains("unknown"))
            return;
        DeNoiseModes sceneModes = Enum.valueOf(DeNoiseModes.class, valueToSet);
        ((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).SetParameterRepeating(CaptureRequest.NOISE_REDUCTION_MODE, sceneModes.ordinal());
        BackgroundValueHasChanged(valueToSet);
    }


    @Override
    public String GetValue()
    {
        int i = ((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).get(CaptureRequest.NOISE_REDUCTION_MODE);
        DeNoiseModes sceneModes = DeNoiseModes.values()[i];
        return sceneModes.toString();

    }


    @Override
    public String[] GetValues()
    {
        int[] values = ((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).characteristics.get(CameraCharacteristics.NOISE_REDUCTION_AVAILABLE_NOISE_REDUCTION_MODES);
        String[] retvals = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            try {
                DeNoiseModes sceneModes = DeNoiseModes.values()[values[i]];
                retvals[i] = sceneModes.toString();
            }
            catch (Exception ex)
            {
                retvals[i] = "unknown Focus" + values[i];
            }

        }
        return retvals;
    }
}
