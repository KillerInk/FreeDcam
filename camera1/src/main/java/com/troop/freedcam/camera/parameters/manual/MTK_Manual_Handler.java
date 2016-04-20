package com.troop.freedcam.camera.parameters.manual;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.CamParametersHandler;

import java.util.HashMap;

/**
 * Created by GeorgeKiarie on 20/04/2016.
 */
public class MTK_Manual_Handler
    {
        private ISOManualParameterMTK isoManualParameter;
        private ShutterManualMtk shutterPrameter;
        private int currentIso = 0;
        private int currentShutter = 0;
        private BaseCameraHolder cameraHolder;
        private HashMap<String, String> parameters;
        boolean auto = true;
        private CamParametersHandler camParametersHandler;

        final String TAG = MTK_Manual_Handler.class.getSimpleName();

        enum AeManual
        {
            shutter,
            iso,
        }

        public MTK_Manual_Handler(HashMap<String, String> parameters,BaseCameraHolder cameraHolder, CamParametersHandler camParametersHandler)
        {
            this.camParametersHandler = camParametersHandler;
            this.isoManualParameter = new ISOManualParameterMTK(parameters,cameraHolder, camParametersHandler, aeevent);
            camParametersHandler.ISOManual = isoManualParameter;
            this.shutterPrameter = new ShutterManualMtk(parameters,cameraHolder, camParametersHandler, aeevent);
            camParametersHandler.ManualShutter = shutterPrameter;
            this.cameraHolder = cameraHolder;
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
                   // parameters.put("lg-manual-mode-reset", "1");
                   // camParametersHandler.SetParametersToCamera(parameters);
                   // parameters.put("lg-manual-mode-reset", "0");


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
                camParametersHandler.SetParametersToCamera(parameters);
                if (automode) {
                    String t = camParametersHandler.IsoMode.GetValue();
                    if (!t.equals("ISO100"))
                        camParametersHandler.IsoMode.SetValue("ISO100", true);
                    else
                        camParametersHandler.IsoMode.SetValue("auto", true);
                    camParametersHandler.IsoMode.SetValue(t, true);
                }
            }
        };
    }