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

package freed.cam.apis.camera1.parameters.manual.whitebalance;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Handler;

import com.drew.lang.StringUtil;

import java.util.ArrayList;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.utils.Logger;
import freed.utils.StringUtils;

/**
 * Created by Ingo on 06.03.2016.
 */
public class BaseCCTManual extends BaseManualParameter
{
    private final String TAG = BaseCCTManual.class.getSimpleName();

    private final String manual_WbMode;
    /**
     * @param parameters
     * @param value
     * @param maxValue
     * @param MinValue
     * @param cameraUiWrapper
     * @param step
     */
    public BaseCCTManual(Parameters parameters, String value, String maxValue, String MinValue
            , CameraWrapperInterface cameraUiWrapper, float step,
                         String wbmode) {
        super(parameters, value, maxValue, MinValue, cameraUiWrapper, step);
        manual_WbMode = wbmode;
    }

    public BaseCCTManual(final Parameters parameters, String maxValue, String MinValue
            , final CameraWrapperInterface cameraUiWrapper, float step,
                         String wbmode) {
        super(parameters, "", maxValue, MinValue, cameraUiWrapper, step);
        isSupported = false;
        isVisible = false;
        int min = Integer.parseInt(parameters.get(key_min_value));
        int max = Integer.parseInt(parameters.get(key_max_value));
        stringvalues = createStringArray(min,max,step);
        manual_WbMode = wbmode;

        //wait 800ms to give awb a chance to set the ct value to the parameters
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //get fresh parameters from camera
                Camera.Parameters parameters1 = ((CameraHolder)cameraUiWrapper.GetCameraHolder()).GetCameraParameters();
                String wbcur = "";
                //lookup if ct value is avail
                if (parameters1.get(KEYS.WB_CURRENT_CCT)!=null)
                    wbcur = KEYS.WB_CURRENT_CCT;
                else if (parameters1.get(KEYS.WB_CCT) != null)
                    wbcur = KEYS.WB_CCT;
                else if (parameters1.get(KEYS.WB_CT) != null)
                    wbcur = KEYS.WB_CT;
                else if (parameters1.get(KEYS.WB_MANUAL_CCT) != null)
                    wbcur = KEYS.WB_MANUAL_CCT;
                else if (parameters1.get(KEYS.MANUAL_WB_VALUE) != null)
                    wbcur = KEYS.MANUAL_WB_VALUE;
                if (wbcur != "")
                {
                    //update our stored parameters with ct
                    parameters.set(wbcur, parameters1.get(wbcur));
                    isSupported = true;
                    isVisible = true;
                    key_value = wbcur;
                    BaseCCTManual.this.ThrowBackgroundIsSupportedChanged(true);
                }
            }
        }, 800);
    }

    public BaseCCTManual(Parameters parameters, String value, int max, int min
            , CameraWrapperInterface cameraUiWrapper, float step, String wbmode) {
        super(parameters, value, "", "", cameraUiWrapper, step);
        isSupported = true;
        isVisible = true;
        stringvalues = createStringArray(min,max,step);
        manual_WbMode =wbmode;
    }

    @Override
    public void SetValue(int valueToSet) {
        currentInt = valueToSet;
        //set to auto
        if (currentInt == 0) {
            set_to_auto();
        } else //set manual wb mode and key_value
        {
            set_manual();
        }
        ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
    }

    protected void set_manual()
    {
        if (!cameraUiWrapper.GetParameterHandler().WhiteBalanceMode.GetValue().equals(manual_WbMode) && manual_WbMode != "")
            cameraUiWrapper.GetParameterHandler().WhiteBalanceMode.SetValue(manual_WbMode, true);
        parameters.set(key_value, stringvalues[currentInt]);
        Logger.d(TAG, "Set "+ key_value +" to : " + stringvalues[currentInt]);

    }

    protected void set_to_auto()
    {
        cameraUiWrapper.GetParameterHandler().WhiteBalanceMode.SetValue("auto", true);
        Logger.d(TAG, "Set  to : auto");
    }

    @Override
    protected String[] createStringArray(int min, int max, float step)
    {
        ArrayList<String> t = new ArrayList<>();
        t.add(KEYS.AUTO);
        for (int i = min; i<=max;i+=step)
        {
            t.add(i+"");
        }
        stringvalues = new String[t.size()];
        t.toArray(stringvalues);
        return stringvalues;
    }
}
