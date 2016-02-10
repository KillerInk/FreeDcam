package com.troop.freedcam.camera.parameters.manual;

import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.CamParametersHandler;

import java.util.HashMap;

/**
 * Created by troop on 27.01.2016.
 */
public class LG_G4AeHandler
{
    private ISOManualParameterG4 isoManualParameter;
    private ShutterManualParameterG4 shutterPrameter;
    private int currentIso = 0;
    private int currentShutter = 0;
    private BaseCameraHolder cameraHolder;
    private HashMap<String, String> parameters;
    private boolean auto = true;

    private final String TAG = LG_G4AeHandler.class.getSimpleName();

    enum AeManual
    {
        shutter,
        iso,
    }

    public LG_G4AeHandler(HashMap<String, String> parameters,BaseCameraHolder cameraHolder, CamParametersHandler camParametersHandler)
    {
        this.isoManualParameter = new ISOManualParameterG4(parameters,cameraHolder, camParametersHandler, aeevent);
        camParametersHandler.ISOManual = isoManualParameter;
        this.shutterPrameter = new ShutterManualParameterG4(parameters,cameraHolder, camParametersHandler, aeevent);
        camParametersHandler.ManualShutter = shutterPrameter;
        this.cameraHolder = cameraHolder;
        this.parameters = parameters;
    }

    public interface AeManualEvent
    {
        void onManualChanged(AeManual fromManual, boolean automode, int value);
    }

    private AeManualEvent aeevent =  new AeManualEvent() {
        @Override
        public void onManualChanged(AeManual fromManual, boolean automode, int value)
        {
            if (automode)
            {
                Log.d(TAG, "AutomodeActive");
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
                parameters.put("lg-manual-mode-reset", "1");

            }
            else
            {
                if (auto)
                {
                    Log.d(TAG, "Automode Deactivated, set last values");
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
                    Log.d(TAG, "Automode Deactivated, set UserValues");
                    switch (fromManual) {
                        case shutter:
                            shutterPrameter.setValue(value);
                            break;
                        case iso:
                            isoManualParameter.setValue(value);
                            break;
                    }
                }
                parameters.put("lg-manual-mode-reset", "0");
            }
            cameraHolder.SetCameraParameters(parameters);
            if (automode) {
                String t = cameraHolder.ParameterHandler.IsoMode.GetValue();
                if (!t.equals("ISO100"))
                    cameraHolder.ParameterHandler.IsoMode.SetValue("ISO100", true);
                else
                    cameraHolder.ParameterHandler.IsoMode.SetValue("auto", true);
                cameraHolder.ParameterHandler.IsoMode.SetValue(t, true);
            }
        }
    };
}
