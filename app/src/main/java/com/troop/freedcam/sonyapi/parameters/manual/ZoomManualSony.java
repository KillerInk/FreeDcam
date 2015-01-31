package com.troop.freedcam.sonyapi.parameters.manual;

import com.troop.freedcam.sonyapi.parameters.ParameterHandlerSony;
import com.troop.freedcam.sonyapi.sonystuff.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

/**
 * Created by troop on 15.12.2014.
 */
public class ZoomManualSony extends BaseManualParameterSony
{
    int currentzoomPos;
    int zoomToSet;
    private boolean isZooming = false;

    public ZoomManualSony(String MAX_TO_GET, String MIN_TO_GET, String CURRENT_TO_GET, ParameterHandlerSony parameterHandlerSony) {
        super(MAX_TO_GET, MIN_TO_GET, CURRENT_TO_GET, parameterHandlerSony);
    }

    @Override
    public void SonyApiChanged(Set<String> mAvailableCameraApiSet)
    {
        this.mAvailableCameraApiSet = mAvailableCameraApiSet;
        if (isSupported != JsonUtils.isCameraApiAvailable("actZoom", mAvailableCameraApiSet))
        {
            isSupported = JsonUtils.isCameraApiAvailable("actZoom", mAvailableCameraApiSet);
            BackgroundIsSupportedChanged(isSupported);
            BackgroundIsSetSupportedChanged(isSupported);
        }


    }

    @Override
    public boolean IsSupported()
    {
        if (ParameterHandler.mAvailableCameraApiSet != null)
            return JsonUtils.isCameraApiAvailable("actZoom", ParameterHandler.mAvailableCameraApiSet);
        return false;
    }

    @Override
    public int GetMaxValue() {
        return 100;
    }

    @Override
    public int GetMinValue() {
        return 0;
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

        zoomToSet = valueToSet;
        if (!isZooming)
        {
            isZooming = true;
            final String movement = "1shot";
            String direction;
            if (valueToSet < currentzoomPos)
                direction = "out";
            else
                direction = "in";
            //currentzoomPos = valueToSet;
            final String finaldirection = direction;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject object = ParameterHandler.mRemoteApi.actZoom(finaldirection, movement);
                        isZooming = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        //super.SetValue(valueToSet);
    }

    public void setZoomsHasChanged(int zoom)
    {

        currentzoomPos = zoom;
        if(!checkIfIntIsInRange(zoomToSet, currentzoomPos))
            SetValue(zoomToSet);
        else
            zoomToSet = currentzoomPos;
        super.currentValueChanged(zoom);
    }


    private boolean checkIfIntIsInRange(int a, int b)
    {
        // 1 = 1
        if (a == b)
            return  true;
            //1 = 3
        else if (a - 4 <= b && a + 4 >= b)
            return  true;
        else
            return false;
    }

    public String GetStringValue()
    {

        return currentzoomPos + "%";

    }

    @Override
    public String[] getStringValues() {
        return null;
    }
}
