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

package com.freedcam.apis.camera2.camera.parameters;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.RggbChannelVector;
import android.os.Build;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.camera2.camera.CameraHolder;
import com.freedcam.apis.camera2.camera.parameters.modes.BaseModeApi2;
import com.freedcam.utils.Logger;

import java.util.HashMap;

/**
 * Created by troop on 18.05.2016.
 */
public class WbHandler
{
    private CameraHolder cameraHolder;
    private ParameterHandler parameterHandler;
    private WhiteBalanceApi2 whiteBalanceApi2;
    private ColorCorrectionModeApi2 colorCorrectionMode;
    private ManualWbCtApi2 manualWbCt;

    private WhiteBalanceValues activeWbMode = WhiteBalanceValues.AUTO;

    public WbHandler(CameraHolder cameraHolder, ParameterHandler parameterHandler)
    {
        this.cameraHolder= cameraHolder;
        this.parameterHandler = parameterHandler;
        colorCorrectionMode = new ColorCorrectionModeApi2();
        whiteBalanceApi2 = new WhiteBalanceApi2();
        manualWbCt = new ManualWbCtApi2(this.parameterHandler);
        this.parameterHandler.CCT = manualWbCt;
        this.parameterHandler.WhiteBalanceMode = whiteBalanceApi2;
        //dont make that avail for the ui
        //parameterHandler.ColorCorrectionMode = colorCorrectionMode;

    }

    public enum WhiteBalanceValues
    {
        OFF,
        AUTO,
        INCANDESCENT,
        FLUORESCENT,
        WARM_FLUORESCENT,
        DAYLIGHT,
        CLOUDY_DAYLIGHT,
        TWILIGHT,
        SHADE,
    }

    public enum ColorCorrectionModes
    {
        TRANSFORM_MATRIX,
        FAST,
        HIGH_QUALITY,
    }

    /**
     * Set internal the whitebalancemode and handel the items that get shown/hidden
     * @param wbMode the whitebalance mode that have changed
     */
    private void setWbMode(WhiteBalanceValues wbMode)
    {
        //set new mode as activemode
        if (colorCorrectionMode == null || manualWbCt == null)
            return;
        activeWbMode =wbMode;
        if (wbMode != WhiteBalanceValues.OFF)
        {
            //if ON or any other preset set the colorcorrection to fast to let is use hal wb
            colorCorrectionMode.setValue(ColorCorrectionModes.FAST);
            //hide manual wbct manualitem in ui
            manualWbCt.ThrowBackgroundIsSupportedChanged(false);
        }
        else //if OFF
        {
            //set colorcorrection to TRANSFORMATRIX to have full control
            colorCorrectionMode.setValue(ColorCorrectionModes.TRANSFORM_MATRIX);
            //show wbct manual item in ui
            manualWbCt.ThrowBackgroundIsSupportedChanged(true);
        }

    }

    /**
     * Created by troop on 28.04.2015.
     *
     * Handels the Whitebalance mode
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public class WhiteBalanceApi2 extends BaseModeApi2
    {
        private String lastcctmode = "FAST";
        private boolean isSupported = false;

        public WhiteBalanceApi2()
        {
            super(cameraHolder);
            int[] values = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
            if (values.length > 1)
                this.isSupported = true;
            lastcctmode = colorCorrectionMode.GetValue();
        }



        @Override
        public boolean IsSupported()
        {
            return this.isSupported;
        }

        @Override
        public void SetValue(String valueToSet, boolean setToCamera)
        {
            if (valueToSet.equals("") || valueToSet.isEmpty())
                return;
            if (valueToSet.contains("unknown"))
            {
                String t = valueToSet.substring(valueToSet.length() -2);
                int i = Integer.parseInt(t);
                cameraHolder.SetParameterRepeating(CaptureRequest.CONTROL_AWB_MODE, i);
            }
            else
            {
                WhiteBalanceValues sceneModes = Enum.valueOf(WhiteBalanceValues.class, valueToSet);
                setWbMode(sceneModes);
                cameraHolder.SetParameterRepeating(CaptureRequest.CONTROL_AWB_MODE, sceneModes.ordinal());
            }
            BackgroundValueHasChanged(valueToSet);
        }

        @Override
        public String GetValue()
        {
            if (cameraHolder != null || !cameraHolder.get(CaptureRequest.CONTROL_AWB_MODE).equals("null"))
            {
                try {
                    int i = cameraHolder.get(CaptureRequest.CONTROL_AWB_MODE);
                    WhiteBalanceValues sceneModes = WhiteBalanceValues.values()[i];
                    return sceneModes.toString();
                }
                catch (NullPointerException ex)
                {
                    Logger.exception(ex);
                    return "AUTO";
                }

            }
            else
                return "AUTO";
        }

        @Override
        public String[] GetValues()
        {
            int[] values = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
            String[] retvals = new String[values.length];
            for (int i = 0; i < values.length; i++)
            {
                try {
                    WhiteBalanceValues sceneModes = WhiteBalanceValues.values()[values[i]];
                    retvals[i] = sceneModes.toString();
                }
                catch (Exception ex)
                {
                    if (i < 10)
                        retvals[i] = "unknown awb 0" + values[i];
                    else
                        retvals[i] = "unknown awb " + values[i];
                }

            }
            return retvals;
        }
    }

    /**
     * Created by troop on 01.05.2015.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public class ManualWbCtApi2  extends AbstractManualParameter
    {
        private RggbChannelVector wbChannelVector;
        private boolean isSupported = false;
        private HashMap<String, int[]> cctLookup;

        private final String TAG = ManualWbCtApi2.class.getSimpleName();

        public ManualWbCtApi2(ParameterHandler camParametersHandler) {
            super(camParametersHandler);
            stringvalues = createStringArray(1500,10000,100);
            cctLookup = getCctlooup();
            currentInt = 0;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public int GetValue()
        {
            return currentInt;
        }

        @Override
        public String GetStringValue()
        {
            if (stringvalues != null)
                return stringvalues[currentInt];
            return 0+"";
        }


        //rgb(255,108, 0)   1500k
        //rgb 255,255,255   6000k
        //rgb(181,205, 255) 15000k
        @Override
        public void SetValue(int valueToSet)
        {
            if (valueToSet == 0) // = auto
                return;
            currentInt =valueToSet;
            valueToSet = Integer.parseInt(stringvalues[valueToSet]);
            int[] rgb = cctLookup.get(valueToSet+"");
            if (rgb == null)
            {
                Logger.d(TAG, "get cct from lookup failed:" + valueToSet);
                return;
            }
            float rf,gf,bf = 0;

            rf = (float)getRGBToDouble(rgb[0]);
            gf = (float)getRGBToDouble(rgb[1])/2;//we have two green channels
            bf = (float)getRGBToDouble(rgb[2]);
            rf = rf/gf;
            bf = bf/gf;
            gf = 1;

            Logger.d(TAG, "r:" +rgb[0] +" g:"+rgb[1] +" b:"+rgb[2]);
            Logger.d(TAG, "ColorTemp=" + valueToSet + " WBCT = r:" +rf +" g:"+gf +" b:"+bf);
            wbChannelVector =  new RggbChannelVector(rf,gf,gf,bf);
            cameraHolder.SetParameterRepeating(CaptureRequest.COLOR_CORRECTION_GAINS, wbChannelVector);

        }

        private int checkminmax(int val)
        {
            if (val>255)
                return 255;
            else if(val < 0)
                return 0;
            else return val;
        }

        private double getRGBToDouble(int color)
        {
            double t = color;
            t = t * 3 *2;
            t = t / (255);
            t = t / 3;
            t += 1;

            return t;
        }

        @Override
        public boolean IsSetSupported() {
            return true;
        }

        @Override
        public boolean IsVisible() {
            return isSupported;
        }

        @Override
        public boolean IsSupported() {
            isSupported = parametersHandler.WhiteBalanceMode.GetValue().equals("OFF");
            return isSupported;
        }

        private int getCctFromRGB(int R, int G, int B)
        {
            double n=((0.23881)*R+(0.25499)*G+(-0.58291)*B)/((0.11109)*R+(-0.85406)*G+(0.52289)*B);
            return (int)(449*Math.pow(n,3)+3525*Math.pow(n,2)+Math.pow(n,6823.3)+5520.33);
        }
    }

    /**
     * Created by troop on 02.05.2015.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public class ColorCorrectionModeApi2 extends BaseModeApi2 {
        public ColorCorrectionModeApi2() {
            super(cameraHolder);
        }

        @Override
        public boolean IsSupported() {
            return cameraHolder != null && cameraHolder.get(CaptureRequest.COLOR_CORRECTION_MODE) != null;
        }

        @Override
        public void SetValue(String valueToSet, boolean setToCamera)
        {
            if (valueToSet.contains("unknown Scene"))
                return;
            setValue(Enum.valueOf(ColorCorrectionModes.class, valueToSet));
            BackgroundValueHasChanged(valueToSet);
        }

        private void setValue(ColorCorrectionModes modes)
        {
            cameraHolder.SetParameterRepeating(CaptureRequest.COLOR_CORRECTION_MODE, modes.ordinal());
        }


        @Override
        public String GetValue()
        {
            try {
                int i = cameraHolder.get(CaptureRequest.COLOR_CORRECTION_MODE);
                ColorCorrectionModes sceneModes = ColorCorrectionModes.values()[i];
                return sceneModes.toString();
            }
            catch (NullPointerException ex)
            {
                Logger.exception(ex);
            }
            return "";


        }

        @Override
        public String[] GetValues()
        {
            String[] retvals = new String[3];
            for (int i = 0; i < 3; i++)
            {
                try {
                    final ColorCorrectionModes sceneModes = ColorCorrectionModes.values()[i];
                    retvals[i] = sceneModes.toString();
                }
                catch (Exception ex)
                {
                    retvals[i] = "unknown Scene" + i;
                }

            }
            return retvals;
        }
    }

    private HashMap<String, int[]> getCctlooup()
    {
        return new HashMap<String, int[]>()
        {
            {
                put("1500",new int[]{255,108,0});
                put("1600",new int[]{255,115,0});
                put("1700",new int[]{255,121,0});
                put("1800",new int[]{255,126,0});
                put("1900",new int[]{255,132,0});

                put("2000",new int[]{255,137,14});
                put("2100",new int[]{255,142,27});
                put("2200",new int[]{255,146,39});
                put("2300",new int[]{255,151,50});
                put("2400",new int[]{255,155,61});
                put("2500",new int[]{255,159,70});
                put("2600",new int[]{255,163,79});
                put("2700",new int[]{255,167,87});
                put("2800",new int[]{255,170,95});
                put("2900",new int[]{255,174,103});

                put("3000",new int[]{255,177,110});
                put("3100",new int[]{255,180,117});
                put("3200",new int[]{255,184,123});
                put("3300",new int[]{255,187,129});
                put("3400",new int[]{255,190,135});
                put("3500",new int[]{255,193,141});
                put("3600",new int[]{255,195,146});
                put("3700",new int[]{255,198, 151});
                put("3800",new int[]{255,201,157});
                put("3900",new int[]{255,203, 161});

                put("4000",new int[]{255,206, 166});
                put("4100",new int[]{255,208, 171});
                put("4200",new int[]{255,211, 175});
                put("4300",new int[]{255,213, 179});
                put("4400",new int[]{255,215, 183});
                put("4500",new int[]{255,218, 187});
                put("4600",new int[]{255,220, 191});
                put("4700",new int[]{255,222, 195});
                put("4800",new int[]{255,224, 199});
                put("4900",new int[]{255,226, 202});

                put("5000",new int[]{255,228, 206});
                put("5100",new int[]{255,230, 209});
                put("5200",new int[]{255,232, 213});
                put("5300",new int[]{255,234, 216});
                put("5400",new int[]{255,236, 219});
                put("5500",new int[]{255,237, 222});
                put("5600",new int[]{255,239, 225});
                put("5700",new int[]{255,241, 228});
                put("5800",new int[]{255,243, 231});
                put("5900",new int[]{255,244, 234});

                put("6000",new int[]{255,246, 237});
                put("6100",new int[]{255,248, 240});
                put("6200",new int[]{255,249, 242});
                put("6300",new int[]{255,251, 245});
                put("6400",new int[]{255,253, 248});
                put("6500",new int[]{255,254, 250});
                put("6600",new int[]{255,255, 255});
                put("6700",new int[]{254,249, 255});
                put("6800",new int[]{250,246, 255});
                put("6900",new int[]{246,244, 255});

                put("7000",new int[]{243,242, 255});
                put("7100",new int[]{240,240, 255});
                put("7200",new int[]{237,239, 255});
                put("7300",new int[]{234,237, 255});
                put("7400",new int[]{232,236, 255});
                put("7500",new int[]{230,235, 255});
                put("7600",new int[]{228,234, 255});
                put("7700",new int[]{226,233, 255});
                put("7800",new int[]{224,232, 255});
                put("7900",new int[]{223,231, 255});

                put("8000",new int[]{221,230, 255});
                put("8100",new int[]{220,229, 255});
                put("8200",new int[]{218,228, 255});
                put("8300",new int[]{217,227, 255});
                put("8400",new int[]{216,227, 255});
                put("8500",new int[]{215,226, 255});
                put("8600",new int[]{214,225, 255});
                put("8700",new int[]{213,225, 255});
                put("8800",new int[]{212,224, 255});
                put("8900",new int[]{211,223, 255});

                put("9000",new int[]{210,223, 255});
                put("9100",new int[]{209,222, 255});
                put("9200",new int[]{208,222, 255});
                put("9300",new int[]{207,221, 255});
                put("9400",new int[]{206,221, 255});
                put("9500",new int[]{205,220, 255});
                put("9600",new int[]{205,220, 255});
                put("9700",new int[]{204,219, 255});
                put("9800",new int[]{203,219, 255});
                put("9900",new int[]{202,218, 255});

                put("10000",new int[]{201,218, 255});
            }
        };
    }
}
