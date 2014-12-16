package com.troop.freedcam.sonyapi.parameters.manual;

import com.troop.freedcam.sonyapi.parameters.ParameterHandlerSony;
import com.troop.freedcam.sonyapi.sonystuff.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by troop on 15.12.2014.
 */
public class ZoomManualSony extends BaseManualParameterSony
{
    int currentzoomPos;

    public ZoomManualSony(String MAX_TO_GET, String MIN_TO_GET, String CURRENT_TO_GET, ParameterHandlerSony parameterHandlerSony) {
        super(MAX_TO_GET, MIN_TO_GET, CURRENT_TO_GET, parameterHandlerSony);
    }

    @Override
    public boolean IsSupported() {
        return JsonUtils.isCameraApiAvailable("actZoom", ParameterHandler.mAvailableCameraApiSet);
    }

    @Override
    public int GetMaxValue() {
        return super.GetMaxValue();
    }

    @Override
    public int GetMinValue() {
        return super.GetMinValue();
    }

    @Override
    public int GetValue()
    {
        currentzoomPos = -1;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    JSONObject object =  ParameterHandler.mRemoteApi.getEvent(false);
                    JSONArray array = object.getJSONArray("result");
                    JSONObject zoom = array.getJSONObject(2);
                    String zoompos = zoom.getString("zoomPosition");
                    currentzoomPos = Integer.parseInt(zoompos);
                } catch (IOException e) {
                    e.printStackTrace();
                    currentzoomPos= 0;
                } catch (JSONException e) {
                    e.printStackTrace();
                    currentzoomPos = 0;
                }
            }
        }).start();
        while (currentzoomPos == -1)
        {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return currentzoomPos;
    }

    @Override
    public void SetValue(int valueToSet)
    {
        final String movement = "start";
        String direction;
        if (valueToSet < currentzoomPos)
            direction = "out";
        else
            direction = "in";
        currentzoomPos = valueToSet;
        final String finaldirection = direction;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject object = ParameterHandler.mRemoteApi.actZoom(finaldirection, movement);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        super.SetValue(valueToSet);
    }
}
