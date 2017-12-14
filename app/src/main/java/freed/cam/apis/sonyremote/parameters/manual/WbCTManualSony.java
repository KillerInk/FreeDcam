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
import freed.utils.FreeDPool;
import freed.utils.Log;

/**
 * Created by troop on 19.04.2015.
 */
public class WbCTManualSony extends BaseManualParameterSony
{
    final String TAG = WbCTManualSony.class.getSimpleName();
    private int step;

    private String[] values;
    public WbCTManualSony(CameraWrapperInterface cameraUiWrapper) {
        super("", "", "", cameraUiWrapper);
    }

    @Override
    public void SonyApiChanged(Set<String> mAvailableCameraApiSet)
    {
        this.mAvailableCameraApiSet = mAvailableCameraApiSet;
    }


    public int GetValue()
    {
        if (currentInt == -200)
            FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject object = mRemoteApi.getParameterFromCamera("getWhiteBalance");
                    JSONArray array = null;

                    try {
                        array = object.getJSONArray("result");
                        currentInt = array.getJSONObject(0).getInt("colorTemperature");
                        if (step == 0) {
                            getMinMax();
                            return;
                        }

                        fireIntValueChanged(currentInt / step);
                    } catch (JSONException ex) {
                        Log.WriteEx(ex);
                    }
                } catch (IOException ex) {
                    Log.WriteEx(ex);

                }
            }
        });
        return currentInt;
    }

    @Override
    public void SetValue(int valueToSet, boolean setToCamera)
    {
        currentInt = valueToSet;
        if (valueToSet > values.length)
            currentInt = values.length;
        if (valueToSet < 0)
            currentInt = 0;
        final int set= currentInt;
        final String[] t = values;
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                try
                {
                    Log.d("WBCT", values[set]);

                    JSONArray array = new JSONArray().put("Color Temperature").put(true).put(Integer.parseInt(t[set])) ;
                    JSONObject jsonObject = mRemoteApi.setParameterToCamera("setWhiteBalance", array);
                } catch (IOException ex) {
                    Log.WriteEx(ex);
                }
            }
        });
    }

    @Override
    public void onIsSupportedChanged(boolean value)
    {
        super.onIsSupportedChanged(value);
        if (step != 0 && value)
        {
            GetStringValue();
        }


    }

    public void SetMinMAx(JSONObject ob) throws JSONException {
        if(ob.getString("whiteBalanceMode").equals("Color Temperature"))
        {
            JSONArray ar = ob.getJSONArray("colorTemperatureRange");
            step = ar.getInt(2);
            int max = ar.getInt(0) / step;
            int min = ar.getInt(1) / step;
            ArrayList<String> r = new ArrayList<>();
            for (int t = min; t < max; t++)
                r.add(t* step +"");
            values =new String[r.size()];
            r.toArray(values);
            fireStringValuesChanged(values);
            fireIsReadOnlyChanged(true);
        }
    }

    public void setValueInternal(int val)
    {
        for (int i = 0; i< values.length; i++)
        {
            if (values[i].equals(val))
                currentInt = i;
        }
        if (currentInt == -200)
            return;
        fireStringValueChanged(val+"");

        fireIntValueChanged(currentInt);

    }

    private void getMinMax()
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run()
            {
                try {
                    JSONObject jsonObject = mRemoteApi.getParameterFromCamera("getAvailableWhiteBalance");
                    try {
                        JSONArray array = jsonObject.getJSONArray("result");
                        JSONArray subarray = array.getJSONArray(1);

                        for (int i = 0; i< subarray.length(); i++)
                        {
                            JSONObject ob = subarray.getJSONObject(i);
                            SetMinMAx(ob);
                        }
                    } catch (JSONException ex) {
                        Log.WriteEx(ex);
                    }
                } catch (IOException ex) {
                    Log.WriteEx(ex);
                }
            }
        });
        while (values == null)
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Log.WriteEx(ex);
            }
    }

    @Override
    public String[] getStringValues()
    {
        if (values == null)
            getMinMax();
        return values;
    }

    @Override
    public String GetStringValue()
    {
        if (values == null)
            return "";
        return values[currentInt];
    }
}
