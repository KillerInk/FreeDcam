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

package freed.cam.apis.camera2.parameters;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.RggbChannelVector;
import android.os.Build.VERSION_CODES;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.parameters.modes.BaseModeApi2;
import freed.settings.AppSettingsManager;
import freed.utils.Log;
import freed.utils.StringIntArray;
import freed.utils.StringUtils;

/**
 * Created by troop on 18.05.2016.
 */
public class WbHandler
{
    private final CameraWrapperInterface cameraUiWrapper;
    public WhiteBalanceApi2 whiteBalanceApi2;
    private ColorCorrectionModeApi2 colorCorrectionMode;
    public final ManualWbCtApi2 manualWbCt;

    public WbHandler(CameraWrapperInterface cameraUiWrapper)
    {
        this.cameraUiWrapper= cameraUiWrapper;

        if (AppSettingsManager.getInstance().colorCorrectionMode.isSupported())
            colorCorrectionMode = new ColorCorrectionModeApi2();
        if (AppSettingsManager.getInstance().whiteBalanceMode.isSupported())
            whiteBalanceApi2 = new WhiteBalanceApi2();

        manualWbCt = new ManualWbCtApi2(cameraUiWrapper);
    }


    /**
     * Set internal the whitebalancemode and handel the items that get shown/hidden
     * @param wbMode the whitebalance mode that have changed
     */
    private void setWbMode(String wbMode)
    {
        String activeWbMode = wbMode;
        if (!wbMode.equals(cameraUiWrapper.getResString(R.string.off)))
        {
            //if ON or any other preset set the colorcorrection to fast to let is use hal wb
            colorCorrectionMode.SetValue(cameraUiWrapper.getResString(R.string.fast),true);
            //hide manual wbct manualitem in ui
            manualWbCt.fireIsSupportedChanged(false);
        }
        else //if OFF
        {
            //set colorcorrection to TRANSFORMATRIX to have full control
            colorCorrectionMode.SetValue(cameraUiWrapper.getResString(R.string.colorcorrection_transform_matrix),true);
            //show wbct manual item in ui
            manualWbCt.fireStringValueChanged(manualWbCt.GetStringValue());
            manualWbCt.fireIsSupportedChanged(true);
        }

    }

    /**
     * Created by troop on 28.04.2015.
     *
     * Handels the Whitebalance mode
     */
    @TargetApi(VERSION_CODES.LOLLIPOP)
    public class WhiteBalanceApi2 extends BaseModeApi2
    {
        private String lastcctmode = "FAST";
        private boolean isSupported;

        public WhiteBalanceApi2()
        {
            super(WbHandler.this.cameraUiWrapper);
            isSupported = AppSettingsManager.getInstance().whiteBalanceMode.isSupported();
            settingMode = AppSettingsManager.getInstance().whiteBalanceMode;
            parameterKey = CaptureRequest.CONTROL_AWB_MODE;
            parameterValues = StringUtils.StringArrayToIntHashmap(AppSettingsManager.getInstance().whiteBalanceMode.getValues());
            if (parameterValues == null)
            {
                isSupported = false;
                return;
            }
            stringvalues = new String[parameterValues.size()];
            parameterValues.keySet().toArray(stringvalues);
            if (colorCorrectionMode != null)
                lastcctmode = colorCorrectionMode.GetStringValue();
        }

        @Override
        public boolean IsSupported()
        {
            return isSupported;
        }
        @Override
        public void setValue(String valueToSet, boolean setToCamera)
        {
            super.setValue(valueToSet,setToCamera);
            setWbMode(valueToSet);
        }
    }

    /**
     * Created by troop on 01.05.2015.
     */
    @TargetApi(VERSION_CODES.LOLLIPOP)
    public class ManualWbCtApi2  extends AbstractParameter
    {
        private RggbChannelVector wbChannelVector;
        private boolean isSupported;

        private StringIntArray lookupvalues;

        private final String TAG = ManualWbCtApi2.class.getSimpleName();

        public ManualWbCtApi2(CameraWrapperInterface cameraUiWrapper) {
            super(cameraUiWrapper);
            lookupvalues = new StringIntArray(AppSettingsManager.getInstance().getResources().getStringArray(R.array.wbct_lookup));
            currentInt = 0;
        }


        @TargetApi(VERSION_CODES.LOLLIPOP)
        @Override
        public int GetValue()
        {
            return currentInt;
        }

        @Override
        public String GetStringValue()
        {
            return lookupvalues.getKey(currentInt);
        }

        @Override
        public String[] getStringValues() {
            return lookupvalues.getKeys();
        }

        //rgb(255,108, 0)   1500k
        //rgb 255,255,255   6000k
        //rgb(181,205, 255) 15000k
        @Override
        public void setValue(int valueToSet)
        {
            super.setValue(valueToSet);
            if (valueToSet == 0) // = auto
                return;
            currentInt =valueToSet;
            int[] rgb = lookupvalues.getValue(valueToSet);
            if (rgb == null)
            {
                Log.d(TAG, "get cct from lookup failed:" + valueToSet);
                return;
            }
            float rf,gf,bf = 0;

            rf = (float) getRGBToDouble(rgb[0]);
            gf = (float) getRGBToDouble(rgb[1])/2;//we have two green channels
            bf = (float) getRGBToDouble(rgb[2]);
            rf = rf/gf;
            bf = bf/gf;
            gf = 1;

            Log.d(TAG, "r:" +rgb[0] +" g:"+rgb[1] +" b:"+rgb[2]);
            Log.d(TAG, "ColorTemp=" + valueToSet + " WBCT = r:" +rf +" g:"+gf +" b:"+bf);
            wbChannelVector =  new RggbChannelVector(rf,gf,gf,bf);
            ((CameraHolderApi2) cameraUiWrapper.getCameraHolder()).captureSessionHandler.SetParameterRepeating(CaptureRequest.COLOR_CORRECTION_GAINS, wbChannelVector);

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
            t = t / 255;
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
            if (cameraUiWrapper == null || cameraUiWrapper.getParameterHandler() == null || cameraUiWrapper.getParameterHandler().WhiteBalanceMode == null)
                return false;
            isSupported = cameraUiWrapper.getParameterHandler().WhiteBalanceMode.GetStringValue().equals("OFF");
            return isSupported;
        }

        private int getCctFromRGB(int R, int G, int B)
        {
            double n=(0.23881 *R+ 0.25499 *G+ -0.58291 *B)/(0.11109 *R+ -0.85406 *G+ 0.52289 *B);
            return (int)(449*Math.pow(n,3)+3525*Math.pow(n,2)+Math.pow(n,6823.3)+5520.33);
        }
    }

    /**
     * Created by troop on 02.05.2015.
     */
    @TargetApi(VERSION_CODES.LOLLIPOP)
    public class ColorCorrectionModeApi2 extends BaseModeApi2 {

        public ColorCorrectionModeApi2() {
            super(WbHandler.this.cameraUiWrapper);
            parameterKey = CaptureRequest.COLOR_CORRECTION_MODE;
            settingMode = AppSettingsManager.getInstance().colorCorrectionMode;
            parameterValues = StringUtils.StringArrayToIntHashmap(AppSettingsManager.getInstance().colorCorrectionMode.getValues());
        }

        @Override
        public boolean IsSupported() {
            return AppSettingsManager.getInstance().colorCorrectionMode.isSupported();
        }

        @Override
        public void setValue(String valueToSet, boolean setToCamera)
        {
            ((CameraHolderApi2) cameraUiWrapper.getCameraHolder()).captureSessionHandler.SetParameterRepeating(CaptureRequest.COLOR_CORRECTION_MODE, parameterValues.get(valueToSet));
            fireStringValueChanged(valueToSet);
        }

    }
}
