package com.troop.freedcam.sonyapi.parameters.manual;

import com.troop.filelogger.Logger;
import com.troop.freedcam.sonyapi.parameters.ParameterHandlerSony;
import com.troop.freedcam.sonyapi.sonystuff.JsonUtils;
import com.troop.freedcam.ui.FreeDPool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Ingo on 19.04.2015.
 */
public class ProgramShiftManualSony extends BaseManualParameterSony
{
    private final String TAG = ProgramShiftManualSony.class.getSimpleName();
    private BaseManualParameterSony shutter;
    private BaseManualParameterSony fnumber;
    public ProgramShiftManualSony(String VALUES_TO_GET, String VALUE_TO_SET, ParameterHandlerSony parameterHandlerSony) {
        super("", "getSupportedProgramShift", "setProgramShift", parameterHandlerSony);
        this.shutter = (BaseManualParameterSony)parameterHandlerSony.ManualShutter;
        this.fnumber = (BaseManualParameterSony)parameterHandlerSony.ManualFNumber;
    }

    @Override
    public void SonyApiChanged(Set<String> mAvailableCameraApiSet)
    {
        this.mAvailableCameraApiSet = mAvailableCameraApiSet;
        if (isSupported != JsonUtils.isCameraApiAvailable(VALUE_TO_SET, mAvailableCameraApiSet))
        {
            isSupported = JsonUtils.isCameraApiAvailable(VALUE_TO_SET, mAvailableCameraApiSet);
        }
        BackgroundIsSupportedChanged(isSupported);
        BackgroundIsSetSupportedChanged(true);
        if (isSetSupported != JsonUtils.isCameraApiAvailable(VALUE_TO_SET, mAvailableCameraApiSet))
        {
            isSetSupported = JsonUtils.isCameraApiAvailable(VALUE_TO_SET, mAvailableCameraApiSet);
        }
        BackgroundIsSetSupportedChanged(isSetSupported);

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
        if (isSupported && isSetSupported)
        {
            FreeDPool.Execute(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Logger.d(TAG, "Trying to get String Values from: " + VALUES_TO_GET);
                        JSONObject object =  ParameterHandler.mRemoteApi.getParameterFromCamera(VALUES_TO_GET);
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
                        BackgroundValuesChanged(stringvalues);
                        onCurrentValueChanged(currentInt);


                    } catch (IOException | JSONException e) {
                        Logger.exception(e);
                        Logger.e(TAG, "Error Trying to get String Values from: " +VALUES_TO_GET);
                        stringvalues = new String[0];
                    }
                }
            });
            while (stringvalues == null)
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Logger.exception(e);
                }
        }
    }

    @Override
    public void SetValue(final int valueToSet)
    {
        this.currentInt = valueToSet;
       FreeDPool.Execute(new Runnable() {
            @Override
            public void run()
            {
                JSONArray array = null;
                try {
                    array = new JSONArray().put(0, Integer.parseInt(stringvalues[currentInt]));
                    JSONObject object =  ParameterHandler.mRemoteApi.setParameterToCamera(VALUE_TO_SET, array);
                    ThrowCurrentValueChanged(valueToSet);
                } catch (JSONException | IOException e) {
                    Logger.exception(e);
                }
            }
        });
    }



    @Override
    public void onCurrentValueChanged(int current) {
        this.currentInt = current;
    }

}
