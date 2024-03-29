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

/*
  Created by George on 1/21/2015.
 */

import android.hardware.Camera.Parameters;

import com.troop.freedcam.R;

import java.util.ArrayList;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.settings.SettingKeys;
import freed.utils.Log;

public class BurstManualParam extends BaseManualParameter
{

    final String TAG = BurstManualParam.class.getSimpleName();

    public BurstManualParam(Parameters parameters, CameraWrapperInterface cameraUiWrapper,SettingKeys.Key settingMode) {
        super(parameters,cameraUiWrapper,settingMode);
        currentInt = Integer.parseInt(settingsManager.get(SettingKeys.M_BURST).get());
        setViewState(ViewState.Visible);
    }

    @Override
    protected String[] createStringArray(int min, int max, float step) {
        ArrayList<String> ar = new ArrayList<>();
        ar.add(FreedApplication.getStringFromRessources(R.string.off_));
        if (step == 0)
            step = 1;
        for (int i = min; i < max; i+=step)
        {
            ar.add(i+"");
        }
        return ar.toArray(new String[ar.size()]);
    }


    @Override
    public int getIntValue()
    {
        return currentInt;
    }

    @Override
    public void setValue(int valueToSet, boolean setToCamera)
    {
        currentInt = valueToSet;

        if (parameters.get(FreedApplication.getStringFromRessources(R.string.num_snaps_per_shutter)) != null)
        {
            parameters.set(FreedApplication.getStringFromRessources(R.string.num_snaps_per_shutter),  String.valueOf((currentInt +1)));
            parameters.set(FreedApplication.getStringFromRessources(R.string.snapshot_burst_num),  String.valueOf((currentInt +1)));
            Log.d(TAG, FreedApplication.getStringFromRessources(R.string.num_snaps_per_shutter)+ (currentInt + 1));

        }
        else if (parameters.get(FreedApplication.getStringFromRessources(R.string.snapshot_burst_num))!=null)
        {
                parameters.set(FreedApplication.getStringFromRessources(R.string.snapshot_burst_num), String.valueOf(currentInt +1));
            Log.d(TAG, FreedApplication.getStringFromRessources(R.string.snapshot_burst_num)+ stringvalues[currentInt]);
        }
        else if(parameters.get(FreedApplication.getStringFromRessources(R.string.burst_num)) != null) // mtk
        {
            if (valueToSet == 0)
                parameters.set(FreedApplication.getStringFromRessources(R.string.burst_num), String.valueOf(0));
            else
                parameters.set(FreedApplication.getStringFromRessources(R.string.burst_num), stringvalues[currentInt]);
            Log.d(TAG, FreedApplication.getStringFromRessources(R.string.burst_num)+ stringvalues[currentInt]);
        }

        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);

    }

    @Override
    public String getStringValue() {
        return stringvalues[currentInt];
    }


}
