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

package freed.cam.apis.camera1.parameters.manual.shutter;


import android.hardware.Camera.Parameters;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualShutter;
import freed.cam.apis.camera1.parameters.manual.AE_Handler_Abstract;
import freed.cam.apis.camera1.parameters.manual.ManualParameterAEHandlerInterface;

/**
 * Created by troop on 28.03.2016.
 */
public class ShutterManualMtk extends AbstractManualShutter implements ManualParameterAEHandlerInterface
{
    private final String TAG = ShutterManualMtk.class.getSimpleName();
    private final AE_Handler_Abstract.AeManualEvent manualevent;
    private Parameters parameters;

    public ShutterManualMtk(Parameters parameters, CameraWrapperInterface cameraUiWrapper, AE_Handler_Abstract.AeManualEvent manualevent) {
        super(cameraUiWrapper);
        this.parameters = parameters;
        isSupported = true;
        isVisible = isSupported;
        stringvalues = cameraUiWrapper.getContext().getResources().getStringArray(R.array.mtk_shutter);
        this.manualevent =manualevent;
    }


    @Override
    public void SetValue(int valueToSet, boolean setToCamera)
    {
        manualevent.onManualChanged(AE_Handler_Abstract.AeManual.shutter, false, valueToSet);
    }
    @Override
    public void setValue(int value, boolean setToCamera)
    {

        if (value == 0)
        {
            parameters.set("m-ss", "0");
        }
        else
        {
            String shutterstring = stringvalues[value];
            if (shutterstring.contains("/")) {
                String[] split = shutterstring.split("/");
                Double a = Double.parseDouble(split[0]) / Double.parseDouble(split[1]);
                shutterstring = "" + a;
            }
            currentInt = value;
            parameters.set("m-ss", FLOATtoThirty(shutterstring));
            fireStringValueChanged(stringvalues[value]);
        }

    }

    private String FLOATtoThirty(String a)
    {
        Float b =  Float.parseFloat(a);
        float c = b * 1000;
        return String.valueOf(c);
    }

}
