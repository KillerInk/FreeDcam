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

import freed.cam.apis.basecamera.CameraWrapperInterface;

/**
 * Created by troop on 10.09.2015.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class FocusPeakModeApi2 extends BaseModeApi2 {
    public FocusPeakModeApi2(CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper,null);
    }


    @Override
    public boolean IsSupported()
    {
        return true;//cameraHolder.characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL) != CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY;
    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.equals(cameraUiWrapper.getResString(R.string.on_)))
        {
            cameraUiWrapper.getFocusPeakProcessor().Enable(true);
            fireStringValueChanged(cameraUiWrapper.getResString(R.string.true_));
        }
        else {
            cameraUiWrapper.getFocusPeakProcessor().Enable(false);
            fireStringValueChanged(cameraUiWrapper.getResString(R.string.false_));
        }

    }

    @Override
    public String GetStringValue() {
        if (cameraUiWrapper.getFocusPeakProcessor().isEnabled())
            return cameraUiWrapper.getResString(R.string.on_);
        else
            return cameraUiWrapper.getResString(R.string.off_);
    }

    @Override
    public String[] getStringValues() {
        return new String[] {cameraUiWrapper.getResString(R.string.on_), cameraUiWrapper.getResString(R.string.off_)};
    }
}
