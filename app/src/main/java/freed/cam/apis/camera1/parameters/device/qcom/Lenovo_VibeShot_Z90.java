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

package freed.cam.apis.camera1.parameters.device.qcom;

import android.hardware.Camera;

import com.troop.freedcam.R;

import java.util.ArrayList;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualShutter;
import freed.cam.apis.basecamera.parameters.manual.ManualParameterInterface;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.device.BaseQcomNew;
import freed.cam.apis.camera1.parameters.manual.AE_Handler_Abstract;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.cam.apis.camera1.parameters.manual.ManualParameterAEHandlerInterface;
import freed.dng.DngProfile;
import freed.utils.Logger;

/**
 * Created by troop on 16.06.2016.
 */
public class Lenovo_VibeShot_Z90 extends BaseQcomNew
{
    //private AeHandlerVibeShotZ90 aeHandlerVibeShotZ90;
    public Lenovo_VibeShot_Z90(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
        //aeHandlerVibeShotZ90 = new AeHandlerVibeShotZ90(parameters,cameraUiWrapper);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }
    @Override
    public DngProfile getDngProfile(int filesize)
    {
        switch (filesize)
        {
            case 19992576:  //lenovo k920
                return new DngProfile(64, 5328,3000, DngProfile.Mipi, DngProfile.GBRG, 0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
        }
        return null;
    }


    @Override
    public ManualParameterInterface getExposureTimeParameter()
    {
        return null;// aeHandlerVibeShotZ90.getShutterManual();
    }

    @Override
    public ManualParameterInterface getIsoParameter() {
        return null;// aeHandlerVibeShotZ90.getManualIso();
    }

    class AeHandlerVibeShotZ90 extends AE_Handler_Abstract
    {
        private final String TAG = AeHandlerVibeShotZ90.class.getSimpleName();

        public AeHandlerVibeShotZ90(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
            super(parameters, cameraUiWrapper);
            iso = new IsoManual(parameters,cameraUiWrapper, this.aeevent);
            shutter = new ShutterManualZ90(parameters,cameraUiWrapper,this.aeevent);
        }

        @Override
        protected void resetManualMode() {

        }

        final AeManualEvent aeevent =  new AeManualEvent() {
            @Override
            public void onManualChanged(AeManual fromManual, boolean automode, int value) {
                if (shutter.IsSupported() && iso.IsSupported() && cameraWrapper.GetAppSettingsManager().GetCurrentCamera() == 0)
                {
                    if (automode) {
                        Logger.d(TAG, "AutomodeActive");
                        auto = automode;
                        parameters.set("force-aec-enable",0);
                        ((ParametersHandler)cameraWrapper.GetParameterHandler()).SetParametersToCamera(parameters);

                        switch (fromManual) {
                            case shutter:
                                currentIso = iso.GetValue();
                                iso.setValue(0);
                                break;
                            case iso:
                                currentShutter = shutter.GetValue();
                                shutter.setValue(0);
                                shutter.ThrowBackgroundIsSetSupportedChanged(false);
                                break;
                        }

                    } else {
                        if (auto) {
                            Logger.d(TAG, "Automode Deactivated, set last values");
                            auto = false;
                            parameters.set("force-aec-enable",1);
                            ((ParametersHandler)cameraWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
                            switch (fromManual) {
                                case shutter:
                                    iso.setValue(currentIso);
                                    break;
                                case iso:
                                    if (currentShutter == 0) currentShutter = 9;
                                    shutter.setValue(currentShutter);
                                    shutter.ThrowBackgroundIsSetSupportedChanged(true);
                                    break;
                            }
                            //startReadingMeta();
                        } else {
                            //readMetaData = false;
                            Logger.d(TAG, "Automode Deactivated, set UserValues");
                            if (parameters.get("force-aec-enable").equals("0"))
                            {
                                parameters.set("force-aec-enable",1);
                                ((ParametersHandler)cameraWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
                            }
                            switch (fromManual) {
                                case shutter:
                                    shutter.setValue(value);

                                    break;
                                case iso:
                                    iso.setValue(value);

                                    break;
                            }
                        }
                    }
                    ((ParametersHandler) cameraWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
                    if (automode) {
                        String t = cameraWrapper.GetParameterHandler().IsoMode.GetValue();
                        if (!t.equals(KEYS.ISO100))
                            cameraWrapper.GetParameterHandler().IsoMode.SetValue(KEYS.ISO100, true);
                        else
                            cameraWrapper.GetParameterHandler().IsoMode.SetValue(KEYS.AUTO, true);
                        cameraWrapper.GetParameterHandler().IsoMode.SetValue(t, true);
                    }
                }
            }
        };

    }


    class IsoManual extends BaseManualParameter implements ManualParameterAEHandlerInterface
    {
        private final AeHandlerVibeShotZ90.AeManualEvent manualEvent;

        public IsoManual(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper, AE_Handler_Abstract.AeManualEvent manualevent) {
            super(parameters, cameraUiWrapper,1);

            isSupported = true;
            isVisible = isSupported;
            ArrayList<String> s = new ArrayList<>();
            for (int i = 0; i <= 3200; i += 50) {
                if (i == 0)
                    s.add(KEYS.AUTO);
                else
                    s.add(i + "");
            }
            stringvalues = new String[s.size()];
            s.toArray(stringvalues);

            manualEvent = manualevent;
        }

        @Override
        public void SetValue(int valueToSet)
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

        public void setValue(int value)
        {

            if (value == 0)
            {
                parameters.set("aec-force-gain", 0);
                parameters.set("aec-force-snap-gain", 0);
            }
            else
            {
                int iso = Integer.parseInt(stringvalues[value])/100;
                parameters.set("aec-force-gain", iso);
                parameters.set("aec-force-snap-gain", iso);
                currentInt = value;
            }
            ThrowCurrentValueStringCHanged(stringvalues[value]);
        }

        @Override
        public String GetStringValue() {
            try {
                return stringvalues[currentInt];
            } catch (NullPointerException ex) {
                return KEYS.AUTO;
            }
        }
    }

    public class ShutterManualZ90 extends AbstractManualShutter implements ManualParameterAEHandlerInterface
    {
        private final String TAG = ShutterManualZ90.class.getSimpleName();
        private final AE_Handler_Abstract.AeManualEvent manualevent;
        private Camera.Parameters parameters;

        public ShutterManualZ90(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper, AE_Handler_Abstract.AeManualEvent manualevent) {
            super(cameraUiWrapper);
            this.parameters = parameters;
            isSupported = true;
            isVisible = isSupported;
            stringvalues = cameraUiWrapper.getContext().getResources().getStringArray(R.array.shutter_values_zte_z5s);
            this.manualevent =manualevent;
        }


        @Override
        public void SetValue(int valueToSet)
        {
            manualevent.onManualChanged(AE_Handler_Abstract.AeManual.shutter, false, valueToSet);
        }
        @Override
        public void setValue(int value)
        {

            if (value == 0)
            {
                parameters.set("aec-force-linecount", "0");
                parameters.set("aec-force-snap-linecount", "0");
                parameters.set("aec-force-snap-exp", "0");
                parameters.set("aec-force-exp", "0");
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
                parameters.set("aec-force-linecount", 1642);
                parameters.set("aec-force-snap-linecount", 1642);
                parameters.set("aec-force-snap-exp", FLOATtoThirty(shutterstring));
                parameters.set("aec-force-exp", FLOATtoThirty(shutterstring));
                ThrowCurrentValueStringCHanged(stringvalues[value]);
            }

        }

        private String FLOATtoThirty(String a)
        {
            Float b =  Float.parseFloat(a);
            float c = b * 1000000;
            return String.valueOf(c);
        }

    }

}
