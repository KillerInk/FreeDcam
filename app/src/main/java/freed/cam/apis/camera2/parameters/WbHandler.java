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

import freed.FreedApplication;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera2.Camera2;
import freed.cam.apis.camera2.parameters.modes.BaseModeApi2;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.StringIntArray;
import freed.utils.StringUtils;

/**
 * Created by troop on 18.05.2016.
 */
public class WbHandler
{
    private final Camera2 cameraUiWrapper;
    public WhiteBalanceApi2 whiteBalanceApi2;
    private ColorCorrectionModeApi2 colorCorrectionMode;
    public ManualWbCtApi2 manualWbCt;
    private final SettingsManager settingsManager;

    public WbHandler(Camera2 cameraUiWrapper)
    {
        this.cameraUiWrapper= cameraUiWrapper;
        settingsManager = FreedApplication.settingsManager();
        if (settingsManager.get(SettingKeys.COLOR_CORRECTION_MODE).isSupported())
            colorCorrectionMode = new ColorCorrectionModeApi2();
        if (settingsManager.get(SettingKeys.WHITE_BALANCE_MODE).isSupported())
            whiteBalanceApi2 = new WhiteBalanceApi2();
        if (!settingsManager.get(SettingKeys.USE_HUAWEI_WHITE_BALANCE).get())
            manualWbCt = new ManualWbCtApi2(cameraUiWrapper);
    }


    /**
     * Set internal the whitebalancemode and handel the items that get shown/hidden
     * @param wbMode the whitebalance mode that have changed
     */
    private void setWbMode(String wbMode)
    {
        if (!wbMode.equals(FreedApplication.getStringFromRessources(R.string.off)))
        {
            //if ON or any other preset set the colorcorrection to fast to let is use hal wb
            colorCorrectionMode.setStringValue(FreedApplication.getStringFromRessources(R.string.fast),true);
            //hide manual wbct manualitem in ui
            if (manualWbCt != null)
                manualWbCt.setViewState(AbstractParameter.ViewState.Hidden);
        }
        else //if OFF
        {
            //set colorcorrection to TRANSFORMATRIX to have full control
            colorCorrectionMode.setStringValue(FreedApplication.getStringFromRessources(R.string.colorcorrection_transform_matrix),true);
            //show wbct manual item in ui
            if (manualWbCt != null) {
                manualWbCt.fireStringValueChanged(manualWbCt.getStringValue());
                manualWbCt.setViewState(AbstractParameter.ViewState.Visible);
            }

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
            super(WbHandler.this.cameraUiWrapper,SettingKeys.WHITE_BALANCE_MODE);
            isSupported = settingsManager.get(SettingKeys.WHITE_BALANCE_MODE).isSupported();
            settingMode = settingsManager.get(SettingKeys.WHITE_BALANCE_MODE);
            parameterKey = CaptureRequest.CONTROL_AWB_MODE;
            parameterValues = StringUtils.StringArrayToIntHashmap(settingsManager.get(SettingKeys.WHITE_BALANCE_MODE).getValues());
            if (parameterValues == null)
            {
                isSupported = false;
                return;
            }
            stringvalues = new String[parameterValues.size()];
            parameterValues.keySet().toArray(stringvalues);
            if (colorCorrectionMode != null && colorCorrectionMode.getStringValue() != null)
                lastcctmode = colorCorrectionMode.getStringValue();
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
    public class ManualWbCtApi2  extends AbstractParameter<Camera2>
    {
        private RggbChannelVector wbChannelVector;
        private boolean isSupported;

        private final StringIntArray lookupvalues;

        private final String TAG = ManualWbCtApi2.class.getSimpleName();

        public ManualWbCtApi2(Camera2 cameraUiWrapper) {
            super(cameraUiWrapper,SettingKeys.M_WHITEBALANCE);
            lookupvalues = new StringIntArray(FreedApplication.getContext().getResources().getStringArray(R.array.wbct_lookup));
            currentInt = 0;
        }


        @TargetApi(VERSION_CODES.LOLLIPOP)
        @Override
        public int getIntValue()
        {
            return currentInt;
        }

        @Override
        public String getStringValue()
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
        private final double MINCAP = 1.0;

        /*
        * <p>The gains in the result metadata are the gains actually
     * applied by the camera device to the current frame.</p>
     * <p>The valid range of gains varies on different devices, but gains
     * between [1.0, 3.0] are guaranteed not to be clipped. Even if a given
     * device allows gains below 1.0, this is usually not recommended because
     * this can create color artifacts.</p>
         */
        @Override
        public void setValue(int valueToSet, boolean setToCamera)
        {
            super.setValue(valueToSet, setToCamera);
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
            gf =(float) getRGBToDouble(rgb[1])/2;//we have two green channels
            bf = (float) getRGBToDouble(rgb[2]);
            if (gf < MINCAP)
                gf= (float)MINCAP;

            Log.d(TAG, "r:" +rgb[0] +" g:"+rgb[1] +" b:"+rgb[2]);
            Log.d(TAG, "ColorTemp=" + valueToSet + " WBCT = r:" +rf +" g:"+gf +" b:"+bf);
            wbChannelVector =  new RggbChannelVector(rf,gf,gf,bf);
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequest.COLOR_CORRECTION_GAINS, wbChannelVector,setToCamera);

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
            double s = ((2.0/255.0) * color) +1;
            if (s < MINCAP)
                return MINCAP;
            return s;
        }

        @Override
        public ViewState getViewState() {
            try {
                if (cameraUiWrapper == null || cameraUiWrapper.getParameterHandler() == null || cameraUiWrapper.getParameterHandler().get(SettingKeys.WHITE_BALANCE_MODE) == null)
                    return ViewState.Hidden;
                else if (cameraUiWrapper.getParameterHandler().get(SettingKeys.WHITE_BALANCE_MODE).getStringValue().equals(FreedApplication.getStringFromRessources(R.string.off)))
                    return ViewState.Visible;
            }
            catch (NullPointerException ex) {
                Log.WriteEx(ex);
            }
            return ViewState.Hidden;
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
            super(WbHandler.this.cameraUiWrapper, SettingKeys.COLOR_CORRECTION_MODE);
            parameterKey = CaptureRequest.COLOR_CORRECTION_MODE;
            settingMode = settingsManager.get(SettingKeys.COLOR_CORRECTION_MODE);
            parameterValues = StringUtils.StringArrayToIntHashmap(settingMode.getValues());
            if (settingsManager.get(SettingKeys.COLOR_CORRECTION_MODE).isSupported())
                setViewState(ViewState.Visible);
        }


        @Override
        public void setValue(String valueToSet, boolean setToCamera)
        {
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequest.COLOR_CORRECTION_MODE, parameterValues.get(valueToSet),setToCamera);
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequest.COLOR_CORRECTION_GAINS, null,setToCamera);
            fireStringValueChanged(valueToSet);
        }

    }
}
