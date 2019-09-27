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

package freed.cam.apis.camera1.parameters.manual.qcom;

import android.hardware.Camera.Parameters;

import com.troop.freedcam.R;

import java.util.ArrayList;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.settings.SettingKeys;
import freed.utils.Log;

/**
 * Created by GeorgeKiarie on 6/2/2016.
 */
public class BaseISOManual extends BaseManualParameter {
    private final String TAG = BaseISOManual.class.getSimpleName();
    private String cur_iso_mode = cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto_);

    public BaseISOManual(Parameters parameters, CameraWrapperInterface cameraUiWrapper,SettingKeys.Key settingMode) {
        super(parameters, cameraUiWrapper, settingMode);
    }

    @Override
    public int GetValue() {
        return currentInt;
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
        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
    }


    protected void set_manual()
    {
        if (cameraUiWrapper == null){
            Log.e(TAG,"set_manual cameraUiWrapper is null");
            return;
        }
        if (cameraUiWrapper.getParameterHandler() == null)
        {
            Log.e(TAG,"set_manual ParameterHandler is null");
            return;
        }
        if (cameraUiWrapper.getParameterHandler().get(SettingKeys.IsoMode) == null)
        {
            Log.e(TAG, "set_manual IsoMode is null");
            return;
        }
        if (cameraUiWrapper.getParameterHandler().get(SettingKeys.IsoMode).GetStringValue() == null)
        {
            Log.e(TAG, "set_manual IsoMode.GetStringValue is null");
            return;
        }
        cur_iso_mode = cameraUiWrapper.getParameterHandler().get(SettingKeys.IsoMode).GetStringValue();

        if (!cameraUiWrapper.getParameterHandler().get(SettingKeys.IsoMode).GetStringValue().equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.manual)))
            cameraUiWrapper.getParameterHandler().get(SettingKeys.IsoMode).SetValue(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.manual), true);
        parameters.set(key_value, stringvalues[currentInt]);


    }

    protected void set_to_auto()
    {
        if (cameraUiWrapper == null){
            Log.e(TAG,"set_to_auto cameraUiWrapper is null");
            return;
        }
        if (cameraUiWrapper.getParameterHandler() == null)
        {
            Log.e(TAG,"set_to_auto ParameterHandler is null");
            return;
        }
        if (cameraUiWrapper.getParameterHandler().get(SettingKeys.IsoMode) == null)
        {
            Log.e(TAG, "set_to_auto IsoMode is null");
            return;
        }
        if (cameraUiWrapper.getParameterHandler().get(SettingKeys.IsoMode).GetStringValue() == null)
        {
            Log.e(TAG, "set_to_auto IsoMode.GetStringValue is null");
            return;
        }
        if (cameraUiWrapper.getParameterHandler().get(SettingKeys.IsoMode).GetStringValue().equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.manual)))
            cameraUiWrapper.getParameterHandler().get(SettingKeys.IsoMode).SetValue(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto_), true);
        cameraUiWrapper.getParameterHandler().get(SettingKeys.IsoMode).SetValue(cur_iso_mode, true);

    }


    @Override
    protected String[] createStringArray(int min, int max, float step)
    {
        ArrayList<String> t = new ArrayList<>();
        t.add(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto_));
        for (int i = min; i<=max;i+=step)
        {
            t.add(i+"");
        }
        stringvalues = new String[t.size()];
        t.toArray(stringvalues);
        return stringvalues;
    }
}
