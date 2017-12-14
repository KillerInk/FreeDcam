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

package freed.cam.apis.camera1.parameters.manual.mtk;

import android.hardware.Camera.Parameters;

import com.troop.freedcam.R;

import java.util.ArrayList;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.manual.AE_Handler_Abstract;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.cam.apis.camera1.parameters.manual.ManualParameterAEHandlerInterface;

/**
 * Created by GeorgeKiarie on 20/04/2016.
 */
public class ISOManualParameterMTK extends BaseManualParameter implements ManualParameterAEHandlerInterface
{
    private final AE_Handler_Abstract.AeManualEvent manualEvent;

    public ISOManualParameterMTK(Parameters parameters, CameraWrapperInterface cameraUiWrapper, AE_Handler_Abstract.AeManualEvent manualevent, int maxiso) {
        super(parameters, cameraUiWrapper,1);

        isSupported = true;
        isVisible = isSupported;
        ArrayList<String> s = new ArrayList<>();
        s.add(cameraUiWrapper.getResString(R.string.auto_));
        for (int i =100; i <= maxiso; i +=100)
        {
            s.add(i + "");
        }
        stringvalues = new String[s.size()];
        s.toArray(stringvalues);
        manualEvent = manualevent;
    }

    @Override
    public void setValue(int valueToSet, boolean setToCamera)
    {
        currentInt = valueToSet;
        if (valueToSet == 0)
        {
            manualEvent.onManualChanged(AE_Handler_Abstract.AeManual.iso, true, valueToSet);
        }
        else
        {
            manualEvent.onManualChanged(AE_Handler_Abstract.AeManual.iso, false,valueToSet);
        }
    }

    @Override
    public void SetValue(int value, boolean setToCamera)
    {

        if (value == 0)
        {
            parameters.set("m-sr-g", "0");
        }
        else
        {
            currentInt = value;
            //cap-isp-g= 1024 == iso100? cause cap-sr-g=7808 / 1024 *100 = 762,5 same with 256 = 3050
            parameters.set("m-sr-g", String.valueOf(Integer.valueOf(stringvalues[value])/100 *256));
        }
        fireStringValueChanged(stringvalues[value]);
    }

    @Override
    public String GetStringValue() {
        try {
            return stringvalues[currentInt];
        } catch (NullPointerException ex) {
            return cameraUiWrapper.getResString(R.string.auto_);
        }
    }

}