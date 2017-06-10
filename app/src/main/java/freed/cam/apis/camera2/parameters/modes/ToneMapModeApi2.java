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
public class ToneMapModeApi2 extends BaseModeApi2 {
    public ToneMapModeApi2(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
    }

    public enum ToneMapModes
    {
        CONTRAST_CURVE,
        FAST,
        HIGH_QUALITY,
    }


    @Override
    public boolean IsSupported() {
        return ((CameraHolderApi2) cameraUiWrapper.getCameraHolder()).characteristics.get(CameraCharacteristics.TONEMAP_AVAILABLE_TONE_MAP_MODES) != null;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.contains("unknown Scene"))
            return;
        ToneMapModes sceneModes = Enum.valueOf(ToneMapModes.class, valueToSet);
        ((CameraHolderApi2) cameraUiWrapper.getCameraHolder()).captureSessionHandler.SetParameterRepeating(CaptureRequest.TONEMAP_MODE, sceneModes.ordinal());
        onValueHasChanged(valueToSet);
    }


    @Override
    public String GetValue()
    {
        int i = ((CameraHolderApi2) cameraUiWrapper.getCameraHolder()).captureSessionHandler.getPreviewParameter(CaptureRequest.TONEMAP_MODE);
        ToneMapModes sceneModes = ToneMapModes.values()[i];
        return sceneModes.toString();

    }

    @Override
    public String[] GetValues()
    {
        int[] values = ((CameraHolderApi2) cameraUiWrapper.getCameraHolder()).characteristics.get(CameraCharacteristics.TONEMAP_AVAILABLE_TONE_MAP_MODES);
        String[] retvals = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            try {
                ToneMapModes sceneModes = ToneMapModes.values()[values[i]];
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
