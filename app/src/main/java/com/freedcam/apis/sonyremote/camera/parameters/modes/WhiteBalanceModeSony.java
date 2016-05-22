package com.freedcam.apis.sonyremote.camera.parameters.modes;

import android.os.Handler;

import com.freedcam.apis.sonyremote.camera.parameters.manual.WbCTManualSony;
import com.freedcam.apis.sonyremote.camera.sonystuff.SimpleRemoteApi;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Ingo on 19.04.2015.
 */
public class WhiteBalanceModeSony extends BaseModeParameterSony
{
    final String TAG = WhiteBalanceModeSony.class.getSimpleName();
    private WbCTManualSony wb;
    public WhiteBalanceModeSony(Handler handler, String VALUE_TO_SET, String VALUES_TO_GET, SimpleRemoteApi mRemoteApi, WbCTManualSony wb) {
        super(handler, "getWhiteBalance", "setWhiteBalance", "getAvailableWhiteBalance", mRemoteApi);
        this.wb = wb;
    }

    @Override
    public String[] GetValues()
    {
        if(values == null || values.length == 0) {
            jsonObject = null;
            FreeDPool.Execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        jsonObject = mRemoteApi.getParameterFromCamera(VALUES_TO_GET);
                        values = processValuesToReturn();
                        BackgroundValuesHasChanged(values);
                    } catch (IOException e) {
                        Logger.exception(e);
                    }
                }
            });
        }

        return values;
    }

    protected String[] processValuesToReturn() {
        String[] ret = null;
        try {
            JSONArray array = jsonObject.getJSONArray("result");
            JSONArray subarray = array.getJSONArray(1);
            ret = new String[subarray.length()];
            for (int i = 0; i< subarray.length(); i++)
            {
                JSONObject ob = subarray.getJSONObject(i);
                ret[i] = ob.getString("whiteBalanceMode");
                if (ret[i].equals("Color Temperature"))
                {

                    wb.SetMinMAx(ob);
                }
            }
            JSONObject ob = array.getJSONObject(0);
            if(ob.getString("whiteBalanceMode").equals("Color Temperature"))
            {
                int cur = ob.getInt("colorTemperature");
                wb.setValueInternal(cur);
            }

        } catch (JSONException e) {
            Logger.exception(e);
        }
        return ret;
    }

    //{
    // "method":"setWhiteBalance",
    // "params":[{"setWhiteBalance":"Daylight"},false,-1],
    // "id":21,"version":"1.0"
    // }

    /*{
        "method": "setWhiteBalance",
            "params": ["Color Temperature", true, 2500],
        "id": 1,
            "version": "1.0"
    }*/
    protected void processValuesToSet(String valueToSet)
    {
        try
        {
            JSONArray array = new JSONArray().put(valueToSet).put(false).put(-1) ;
            JSONObject jsonObject = mRemoteApi.setParameterToCamera(VALUE_TO_SET, array);
        } catch (IOException e) {
            Logger.exception(e);
        }
    }

    protected String processGetString() {
        JSONArray array = null;
        String ret ="";
        try {
            array = jsonObject.getJSONArray("result");
            ret = array.getJSONObject(0).getString("whiteBalanceMode");
        } catch (JSONException e) {
            Logger.exception(e);
        }
        return ret;
    }
}
