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

package com.freedcam.apis.camera1.parameters.manual.lg;

import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.CameraWrapperEventInterface;
import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.basecamera.interfaces.ModuleInterface;
import com.freedcam.apis.camera1.CameraHolder;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 27.01.2016.
 */
public class AE_Handler_LGG4 implements CameraWrapperEventInterface
{
    private final ISOManualParameterG4 isoManualParameter;
    private final ShutterManualParameterG4 shutterPrameter;
    private int currentIso;
    private int currentShutter;
    private final Parameters parameters;
    boolean auto = true;
    private ParametersHandler parametersHandler;
    private boolean readMetaData;
    private CameraHolder cameraHolder;

    final String TAG = AE_Handler_LGG4.class.getSimpleName();

    @Override
    public void onCameraOpen(String message) {

    }

    @Override
    public void onCameraOpenFinish(String message) {
        aeevent.onManualChanged(AeManual.shutter,true,0);
    }

    @Override
    public void onCameraClose(String message) {

    }

    @Override
    public void onPreviewOpen(String message) {

    }

    @Override
    public void onPreviewClose(String message)
    {
        readMetaData = false;
    }

    @Override
    public void onCameraError(String error) {

    }

    @Override
    public void onCameraStatusChanged(String status) {

    }

    @Override
    public void onModuleChanged(ModuleInterface module) {

    }

    enum AeManual
    {
        shutter,
        iso,
    }

    public AE_Handler_LGG4(Parameters parameters, CameraWrapperInterface cameraUiWrapper)
    {
        isoManualParameter = new ISOManualParameterG4(parameters,cameraUiWrapper, aeevent);
        shutterPrameter = new ShutterManualParameterG4(parameters, cameraUiWrapper, aeevent);
        this.parameters = parameters;
    }

    public ISOManualParameterG4 getManualIso()
    {
        return isoManualParameter;
    }

    public ShutterManualParameterG4 getShutterManual()
    {
        return shutterPrameter;
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
                        shutterPrameter.ThrowBackgroundIsSetSupportedChanged(false);
                        break;
                }
                parameters.set(KEYS.LG_MANUAL_MODE_RESET, "1");
                parametersHandler.SetParametersToCamera(parameters);
                parameters.set(KEYS.LG_MANUAL_MODE_RESET, "0");
                startReadingMeta();

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
                            shutterPrameter.ThrowBackgroundIsSetSupportedChanged(true);
                            break;
                    }
                    startReadingMeta();
                }
                else
                {
                    readMetaData = false;
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
                parameters.set(KEYS.LG_MANUAL_MODE_RESET, "0");
            }
            parametersHandler.SetParametersToCamera(parameters);
            if (automode) {
                String t = parametersHandler.IsoMode.GetValue();
                if (!t.equals(KEYS.ISO100))
                    parametersHandler.IsoMode.SetValue(KEYS.ISO100, true);
                else
                    parametersHandler.IsoMode.SetValue(KEYS.AUTO, true);
                parametersHandler.IsoMode.SetValue(t, true);
            }
        }
    };

    private void startReadingMeta()
    {
        readMetaData = true;
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                while (readMetaData && auto)
                {
                    try {
                        shutterPrameter.ThrowCurrentValueStringCHanged("1/"+(int) parametersHandler.getDevice().getCurrentExposuretime());
                        isoManualParameter.ThrowCurrentValueStringCHanged(parametersHandler.getDevice().getCurrentIso()+"");
                    }
                    catch (RuntimeException ex)
                    {
                        readMetaData = false;
                        return;
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
