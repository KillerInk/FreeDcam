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

package com.freedcam.apis.camera1.parameters.manual;

import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.parameters.modes.AbstractModeParameter.I_ModeParameterEvent;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.apis.camera1.parameters.modes.BaseModeParameter;


/**
 * Created by troop on 26.04.2016.
 */
public class AE_Handler_QcomM
{
    private ShutterManual_ExposureTime_Micro exposureTime;
    private BaseISOManual isoManual;
    private BaseModeParameter AE_Mode;

    public AE_Handler_QcomM(Parameters parameters, I_CameraUiWrapper cameraUiWrapper, ParametersHandler parametersHandler)
    {
        AE_Mode = new BaseModeParameter(parameters, cameraUiWrapper, KEYS.MANUAL_EXPOSURE, KEYS.MANUAL_EXPOSURE_MODES);
        AE_Mode.addEventListner(aemodeChangedListner);
        parametersHandler.AE_PriorityMode = AE_Mode;
        exposureTime = new ShutterManual_ExposureTime_Micro(parameters, parametersHandler,KEYS.EXPOSURE_TIME, KEYS.MAX_EXPOSURE_TIME, KEYS.MIN_EXPOSURE_TIME,false);
        isoManual = new BaseISOManual(parameters,"continuous-iso",parameters.getInt("min-iso"),parameters.getInt("max-iso"), parametersHandler,1);
    }

    public ShutterManual_ExposureTime_Micro getManualIso()
    {
        return exposureTime;
    }

    public BaseISOManual getShutterManual()
    {
        return isoManual;
    }

    I_ModeParameterEvent aemodeChangedListner = new I_ModeParameterEvent() {
        @Override
        public void onValueChanged(String val)
        {
            switch (val) {
                case KEYS.MANUAL_EXPOSURE_MODES_OFF:
                    exposureTime.ThrowBackgroundIsSetSupportedChanged(false);
                    isoManual.ThrowBackgroundIsSetSupportedChanged(false);
                    break;
                case KEYS.MANUAL_EXPOSURE_MODES_EXP_TIME_PRIORITY:
                    exposureTime.ThrowBackgroundIsSetSupportedChanged(true);
                    isoManual.ThrowBackgroundIsSetSupportedChanged(false);
                    break;
                case KEYS.MANUAL_EXPOSURE_MODES_ISO_PRIORITY:
                    exposureTime.ThrowBackgroundIsSetSupportedChanged(false);
                    isoManual.ThrowBackgroundIsSetSupportedChanged(true);
                    break;
                case KEYS.MANUAL_EXPOSURE_MODES_USER_SETTING:
                    exposureTime.ThrowBackgroundIsSetSupportedChanged(true);
                    isoManual.ThrowBackgroundIsSetSupportedChanged(true);
                    break;
            }

        }

        @Override
        public void onIsSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onIsSetSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onValuesChanged(String[] values) {

        }

        @Override
        public void onVisibilityChanged(boolean visible) {

        }
    };

}
