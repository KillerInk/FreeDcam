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

/**
 * Created by George on 1/21/2015.
 */

import android.hardware.Camera.Parameters;

import com.troop.freedcam.R;

import java.util.ArrayList;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleChangedEvent;
import freed.settings.Settings;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.settings.SettingsManager;
import freed.utils.Log;

public class BurstManualParam extends BaseManualParameter
{

    final String TAG = BurstManualParam.class.getSimpleName();

    public BurstManualParam(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, "", "", "", cameraUiWrapper,1);
        isSupported = SettingsManager.get(Settings.M_Burst).isSupported();
        stringvalues = SettingsManager.get(Settings.M_Burst).getValues();
        currentInt = Integer.parseInt(SettingsManager.get(Settings.M_Burst).get());
    }

    @Override
    protected String[] createStringArray(int min, int max, float step) {
        ArrayList<String> ar = new ArrayList<>();
        ar.add(cameraUiWrapper.getResString(R.string.off_));
        if (step == 0)
            step = 1;
        for (int i = min; i < max; i+=step)
        {
            ar.add(i+"");
        }
        return ar.toArray(new String[ar.size()]);
    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    public int GetValue()
    {
        return currentInt;
    }

    @Override
    public void setValue(int valueToSet, boolean setToCamera)
    {
        currentInt = valueToSet;

        if (parameters.get(cameraUiWrapper.getResString(R.string.num_snaps_per_shutter)) != null)
        {
            parameters.set(cameraUiWrapper.getResString(R.string.num_snaps_per_shutter), stringvalues[currentInt]);
            parameters.set(cameraUiWrapper.getResString(R.string.snapshot_burst_num), stringvalues[currentInt]);
            Log.d(TAG, cameraUiWrapper.getResString(R.string.num_snaps_per_shutter)+ stringvalues[currentInt]);

        }
        if (parameters.get(cameraUiWrapper.getResString(R.string.snapshot_burst_num))!=null)
        {
                parameters.set(cameraUiWrapper.getResString(R.string.snapshot_burst_num), stringvalues[currentInt]);
            Log.d(TAG, cameraUiWrapper.getResString(R.string.snapshot_burst_num)+ stringvalues[currentInt]);
        }
        else if(parameters.get(cameraUiWrapper.getResString(R.string.burst_num)) != null) // mtk
        {
            if (valueToSet == 0)
                parameters.set(cameraUiWrapper.getResString(R.string.burst_num), String.valueOf(0));
            else
                parameters.set(cameraUiWrapper.getResString(R.string.burst_num), stringvalues[currentInt]);
            Log.d(TAG, cameraUiWrapper.getResString(R.string.burst_num)+ stringvalues[currentInt]);
        }

        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);

    }

    @Override
    public String GetStringValue() {
        return stringvalues[currentInt];
    }

    @Override
    public ModuleChangedEvent GetModuleListner() {
        return moduleListner;
    }

    private final ModuleChangedEvent moduleListner =new ModuleChangedEvent() {
        @Override
        public void onModuleChanged(String module)
        {
            if ((module.equals(cameraUiWrapper.getResString(R.string.module_video)) || module.equals(cameraUiWrapper.getResString(R.string.module_hdr))) && isSupported)
                fireIsSupportedChanged(false);
            else if ((module.equals(cameraUiWrapper.getResString(R.string.module_picture))
                    || module.equals(cameraUiWrapper.getResString(R.string.module_interval))
                    )&& isSupported)
            {
                fireIsSupportedChanged(true);
            }
        }
    };

}
