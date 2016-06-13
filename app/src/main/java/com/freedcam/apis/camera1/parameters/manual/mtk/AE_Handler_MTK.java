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

package com.freedcam.apis.camera1.parameters.manual.mtk;

import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.utils.Logger;

/**
 * Created by GeorgeKiarie on 20/04/2016.
 */
public class AE_Handler_MTK
    {
        public ISOManualParameterMTK isoManualParameter;
        public ShutterManualMtk shutterPrameter;
        private int currentIso;
        private int currentShutter;
        private final Parameters parameters;
        boolean auto = true;
        private final CameraWrapperInterface cameraUiWrapper;

        final String TAG = AE_Handler_MTK.class.getSimpleName();

        enum AeManual
        {
            shutter,
            iso,
        }

        public AE_Handler_MTK(Parameters parameters, CameraWrapperInterface cameraUiWrapper, int maxiso)
        {
            this.cameraUiWrapper = cameraUiWrapper;
            isoManualParameter = new ISOManualParameterMTK(parameters,cameraUiWrapper, aeevent, maxiso);
            shutterPrameter = new ShutterManualMtk(parameters, cameraUiWrapper, aeevent);
            this.parameters = parameters;
        }

        public interface AeManualEvent
        {
            void onManualChanged(AeManual fromManual, boolean automode, int value);
        }

        AeManualEvent aeevent =  new AeManualEvent() {
            @Override
            public void onManualChanged(AeManual fromManual, boolean automode, int value)
            {
                if (automode)
                {
                    Logger.d(TAG, "AutomodeActive");
                    auto = automode;


                    switch (fromManual) {
                        case shutter:
                            currentIso = isoManualParameter.GetValue();
                            isoManualParameter.setValue(0);
                            break;
                        case iso:
                            currentShutter = shutterPrameter.GetValue();
                            shutterPrameter.setValue(0);
                            break;
                    }
                }
                else
                {
                    if (auto)
                    {
                        Logger.d(TAG, "Automode Deactivated, set last values");
                        auto = false;
                        switch (fromManual) {
                            case shutter:
                                isoManualParameter.setValue(currentIso);
                                break;
                            case iso:
                                if (currentShutter == 0) currentShutter =9;
                                shutterPrameter.setValue(currentShutter);
                                break;
                        }
                    }
                    else
                    {
                        Logger.d(TAG, "Automode Deactivated, set UserValues");
                        switch (fromManual) {
                            case shutter:
                                shutterPrameter.setValue(value);
                                break;
                            case iso:
                                isoManualParameter.setValue(value);
                                break;
                        }
                    }
                   // parameters.put("lg-manual-mode-reset", "0");
                }
                ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
                if (automode) {
                    String t = cameraUiWrapper.GetParameterHandler().IsoMode.GetValue();
                    if (!t.equals(KEYS.ISO100))
                        cameraUiWrapper.GetParameterHandler().IsoMode.SetValue(KEYS.ISO100, true);
                    else
                        cameraUiWrapper.GetParameterHandler().IsoMode.SetValue(KEYS.AUTO, true);
                    cameraUiWrapper.GetParameterHandler().IsoMode.SetValue(t, true);
                }
            }
        };
    }