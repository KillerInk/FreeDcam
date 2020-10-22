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

package com.troop.freedcam.camera.camera1.parameters.manual.whitebalance;

import android.hardware.Camera.Parameters;
import android.os.Handler;
import android.text.TextUtils;

import com.troop.freedcam.camera.R;
import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.parameters.ParameterInterface;
import com.troop.freedcam.camera.camera1.CameraHolder;
import com.troop.freedcam.camera.camera1.parameters.ParametersHandler;
import com.troop.freedcam.camera.camera1.parameters.manual.BaseManualParameter;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.utils.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ingo on 06.03.2016.
 */
public class BaseCCTManual extends BaseManualParameter
{
    private final String TAG = BaseCCTManual.class.getSimpleName();

    private final String manual_WbMode;


    public BaseCCTManual(final Parameters parameters, final CameraControllerInterface cameraUiWrapper, SettingKeys.Key settingMode) {
        super(parameters,cameraUiWrapper, settingMode);
        manual_WbMode = SettingsManager.get(SettingKeys.M_Whitebalance).getMode();
        setViewState(ViewState.Hidden);

        //wait 800ms to give awb a chance to set the ct value to the parameters
        if (TextUtils.isEmpty(SettingsManager.get(SettingKeys.M_Whitebalance).getCamera1ParameterKEY()))
            new Handler().postDelayed(() -> {
                try {
                    //get fresh parameters from camera
                    Parameters parameters1 = ((CameraHolder) cameraUiWrapper.getCameraHolder()).GetCameraParameters();
                    String wbcur = "";
                    //lookup if ct value is avail
                    if (parameters1.get(ContextApplication.getStringFromRessources(R.string.wb_current_cct)) != null)
                        wbcur = ContextApplication.getStringFromRessources(R.string.wb_current_cct);
                    else if (parameters1.get(ContextApplication.getStringFromRessources(R.string.wb_cct)) != null)
                        wbcur = ContextApplication.getStringFromRessources(R.string.wb_cct);
                    else if (parameters1.get(ContextApplication.getStringFromRessources(R.string.wb_ct)) != null)
                        wbcur = ContextApplication.getStringFromRessources(R.string.wb_ct);
                    else if (parameters1.get(ContextApplication.getStringFromRessources(R.string.wb_manual_cct)) != null)
                        wbcur = ContextApplication.getStringFromRessources(R.string.wb_manual_cct);
                    else if (parameters1.get(ContextApplication.getStringFromRessources(R.string.manual_wb_value)) != null)
                        wbcur = ContextApplication.getStringFromRessources(R.string.manual_wb_value);
                    if (!TextUtils.isEmpty(wbcur)) {
                        //update our stored parameters with ct
                        parameters.set(wbcur, parameters1.get(wbcur));
                        setViewState(ViewState.Visible);
                        key_value = wbcur;
                    }
                }
                catch (NullPointerException ex)
                {
                    setViewState(ViewState.Hidden);
                }
            }, 800);
        else
            key_value = SettingsManager.get(SettingKeys.M_Whitebalance).getCamera1ParameterKEY();
    }
    
    @Override
    public void setValue(int valueToSet, boolean setToCamera) {
        currentInt = valueToSet;
        //set to auto
        if (currentInt == 0) {
            set_to_auto();
        } else //set manual wb mode and key_value
        {
            set_manual();
        }
        try {
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        }
        catch (RuntimeException ex)
        {
            Log.WriteEx(ex);
            setViewState(ViewState.Hidden);
        }

    }

    protected void set_manual()
    {
        ParameterInterface wbm = cameraUiWrapper.getParameterHandler().get(SettingKeys.WhiteBalanceMode);
        Log.d(TAG, Arrays.toString(wbm.getStringValues()));
        try {
            if (parameters.get("whitebalance-values").contains("manual") && parameters.get("manual-wb-modes").contains("color-temperature")) {

                wbm.SetValue(manual_WbMode, true);
                parameters.set(ContextApplication.getStringFromRessources(R.string.manual_wb_type), 0);
                parameters.set(ContextApplication.getStringFromRessources(R.string.manual_wb_value), stringvalues[currentInt]);
                Log.d(TAG, "NEW");

            } else {
                if (!wbm.GetStringValue().equals(manual_WbMode) && !TextUtils.isEmpty(manual_WbMode))
                    wbm.SetValue(manual_WbMode, true);
                parameters.set(key_value, stringvalues[currentInt]);
                Log.d(TAG, "OLD");
            }
        }
        catch (Exception err )
        {
            if (!wbm.GetStringValue().equals(manual_WbMode) && !TextUtils.isEmpty(manual_WbMode))
                wbm.SetValue(manual_WbMode, true);
            parameters.set(key_value, stringvalues[currentInt]);
            err.printStackTrace();
        }
        Log.d(TAG, "Set "+ key_value +" to : " + stringvalues[currentInt]);

    }

    protected void set_to_auto()
    {
        cameraUiWrapper.getParameterHandler().get(SettingKeys.WhiteBalanceMode).SetValue("auto", true);
        Log.d(TAG, "Set  to : auto");
    }

    @Override
    protected String[] createStringArray(int min, int max, float step)
    {
        ArrayList<String> t = new ArrayList<>();
        t.add(ContextApplication.getStringFromRessources(R.string.auto_));
        for (int i = min; i<=max;i+=step)
        {
            t.add(i+"");
        }
        stringvalues = new String[t.size()];
        t.toArray(stringvalues);
        return stringvalues;
    }
}
