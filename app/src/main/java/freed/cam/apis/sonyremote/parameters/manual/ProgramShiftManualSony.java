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

package freed.cam.apis.sonyremote.parameters.manual;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.sonyremote.parameters.ParameterHandler;
import freed.cam.apis.sonyremote.sonystuff.JsonUtils;
import freed.settings.SettingKeys;
import freed.utils.FreeDPool;
import freed.utils.Log;

/**
 * Created by Ingo on 19.04.2015.
 */
public class ProgramShiftManualSony extends BaseManualParameterSony
{
    private final String TAG = ProgramShiftManualSony.class.getSimpleName();
    private final BaseManualParameterSony shutter;

    public ProgramShiftManualSony(CameraWrapperInterface cameraUiWrapper) {
        super("", "getSupportedProgramShift", "setProgramShift", cameraUiWrapper);
        shutter = (BaseManualParameterSony) cameraUiWrapper.getParameterHandler().get(SettingKeys.M_ExposureTime);
        BaseManualParameterSony fnumber = (BaseManualParameterSony) cameraUiWrapper.getParameterHandler().get(SettingKeys.M_Fnumber);
    }

    @Override
    public void SonyApiChanged(Set<String> mAvailableCameraApiSet)
    {
        this.mAvailableCameraApiSet = mAvailableCameraApiSet;
        boolean isSupported = JsonUtils.isCameraApiAvailable(VALUE_TO_SET, mAvailableCameraApiSet);
        boolean isSetSupported = JsonUtils.isCameraApiAvailable(VALUE_TO_SET, mAvailableCameraApiSet);
        if (isSupported && isSetSupported)
            setViewState(ViewState.Visible);
        else if (isSupported && !isSetSupported)
            setViewState(ViewState.Disabled);
        else
            setViewState(ViewState.Hidden);
    }


    @Override
    public String[] getStringValues()
    {
        if (stringvalues == null)
            getminmax();
        return stringvalues;
    }

    @Override
    public String GetStringValue()
    {
        if (stringvalues == null)
            getminmax();
        return stringvalues[currentInt];
    }

    private void getminmax() {
        if (getViewState() == ViewState.Visible)
        {
            FreeDPool.Execute(() -> {
                try
                {
                    Log.d(TAG, "Trying to get String Values from: " + VALUES_TO_GET);
                    JSONObject object =  ((ParameterHandler) cameraUiWrapper.getParameterHandler()).mRemoteApi.getParameterFromCamera(VALUES_TO_GET);
                    JSONArray array = object.getJSONArray("result");
                    JSONArray subarray = array.getJSONArray(0);
                    stringvalues = JsonUtils.ConvertJSONArrayToStringArray(subarray);
                    if (stringvalues == null || stringvalues.length != 2)
                        return;
                    int max = Integer.parseInt(stringvalues[0]);
                    int min = Integer.parseInt(stringvalues[1]);
                    ArrayList<String> r = new ArrayList<>();
                    for (int i = min; i<= max; i++)
                    {
                        r.add(i+"");
                    }
                    stringvalues =new String[r.size()];

                    String[] shut = shutter.getStringValues();
                    if (shut != null && r != null && shut.length == r.size())
                    {
                        String s = shutter.GetStringValue();
                        for (int i = 0; i < shut.length; i++)
                        {
                            if (s.equals(shut[i]))
                            {
                                currentInt = i;
                                break;
                            }
                        }
                    }
                    r.toArray(stringvalues);
                    fireStringValuesChanged(stringvalues);
                    onIntValueChanged(currentInt);


                } catch (IOException | JSONException ex) {
                    Log.WriteEx(ex);
                    Log.e(TAG, "Error Trying to get String Values from: " + VALUES_TO_GET);
                    stringvalues = new String[0];
                }
            });
            while (stringvalues == null)
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Log.WriteEx(ex);
                }
        }
    }

    @Override
    public void SetValue(final int valueToSet, boolean setToCamera)
    {
        currentInt = valueToSet;
       FreeDPool.Execute(() -> {
           JSONArray array = null;
           try {
               array = new JSONArray().put(0, Integer.parseInt(stringvalues[currentInt]));
               JSONObject object = mRemoteApi.setParameterToCamera(VALUE_TO_SET, array);
               fireIntValueChanged(valueToSet);
           } catch (JSONException | IOException ex) {
               Log.WriteEx(ex);
           }
       });
    }



    @Override
    public void onIntValueChanged(int current) {
        currentInt = current;
    }

}
