package com.troop.freedcam.sonyapi.parameters.modes;

import android.os.Handler;
import android.util.Log;

import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.sonyapi.sonystuff.JsonUtils;
import com.troop.freedcam.sonyapi.sonystuff.SimpleRemoteApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

/**
 * Created by troop on 15.12.2014.
 */
public class BaseModeParameterSony extends AbstractModeParameter implements I_SonyApi
{

    protected String VALUE_TO_GET = "";
    protected String VALUE_TO_SET ="";
    protected String VALUES_TO_GET ="";

    protected SimpleRemoteApi mRemoteApi;
    protected Set<String> mAvailableCameraApiSet;
    protected Set<String> mSupportedApiSet;
    JSONObject jsonObject;

    protected boolean isSupported =false;
    protected boolean isSetSupported = false;
    protected String value ="";
    protected String[] values;
    private static String TAG = BaseModeParameterSony.class.getSimpleName();

    public BaseModeParameterSony(Handler handler,String VALUE_TO_GET, String VALUE_TO_SET, String VALUES_TO_GET, SimpleRemoteApi mRemoteApi)
    {
        super(handler);
        this.VALUE_TO_GET = VALUE_TO_GET;
        this.VALUE_TO_SET = VALUE_TO_SET;
        this.VALUES_TO_GET = VALUES_TO_GET;
        this.mRemoteApi = mRemoteApi;
    }

    @Override
    public void SonyApiChanged(final Set<String> mAvailableCameraApiSet)
    {
        this.mAvailableCameraApiSet = mAvailableCameraApiSet;
        if (isSupported != JsonUtils.isCameraApiAvailable(VALUE_TO_GET, mAvailableCameraApiSet))
        {
            isSupported = JsonUtils.isCameraApiAvailable(VALUE_TO_GET, mAvailableCameraApiSet);
            BackgroundIsSupportedChanged(isSupported);
            BackgroundValueHasChanged(GetValue());
        }

    }


    @Override
    public boolean IsSupported()
    {
        if (mAvailableCameraApiSet != null)
        {
            boolean sup = JsonUtils.isCameraApiAvailable(VALUE_TO_GET, mAvailableCameraApiSet);
            Log.d(TAG, VALUE_TO_GET + " is supported: " +sup);
            BackgroundIsSupportedChanged(sup);
            return sup;
        }else
            return false;
    }

    @Override
    public void SetValue(final String valueToSet, boolean setToCamera)
    {
        value = valueToSet;
        new Thread(new Runnable() {
            @Override
            public void run() {
                processValuesToSet(valueToSet);
                BackgroundValueHasChanged(valueToSet);
            }
        }).start();
    }

    protected void processValuesToSet(String valueToSet)
    {
        try
        {
            try {
                JSONArray array = new JSONArray().put(0, valueToSet);
                JSONObject jsonObject = mRemoteApi.setParameterToCamera(VALUE_TO_SET, array);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public String GetValue()
    {
        if (value == null || value.equals("")) {
            jsonObject = null;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        jsonObject = mRemoteApi.getParameterFromCamera(VALUE_TO_GET);
                        value = processGetString();
                        BackgroundValueHasChanged(value);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
            return value;

    }

    protected String processGetString() {
        JSONArray array = null;
        String ret ="";
        try {
            array = jsonObject.getJSONArray("result");
            ret = array.getString(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public String[] GetValues()
    {
        jsonObject =null;
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    jsonObject = mRemoteApi.getParameterFromCamera(VALUES_TO_GET);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        while (jsonObject == null)
        {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String[] ret = processValuesToReturn();

        return ret;
    }

    protected String[] processValuesToReturn() {
        String[] ret = null;
        try {
            JSONArray array = jsonObject.getJSONArray("result");
            JSONArray subarray = array.getJSONArray(1);
            ret = JsonUtils.ConvertJSONArrayToStringArray(subarray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
