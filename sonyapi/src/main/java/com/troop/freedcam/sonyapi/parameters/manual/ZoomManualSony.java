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
    private int currentzoomPos = -1;
    private int zoomToSet;
    private boolean isZooming = false;

    private boolean fromUser = false;

    public ZoomManualSony(String MAX_TO_GET, String MIN_TO_GET, ParameterHandlerSony parameterHandlerSony) {
        super("actZoom", "", "actZoom", parameterHandlerSony);
    }

    @Override
    public void SonyApiChanged(Set<String> mAvailableCameraApiSet)
    {
        this.mAvailableCameraApiSet = mAvailableCameraApiSet;
        //if (isSupported != JsonUtils.isCameraApiAvailable("actZoom", mAvailableCameraApiSet))
        //{
            isSupported = JsonUtils.isCameraApiAvailable("actZoom", mAvailableCameraApiSet);
            BackgroundIsSupportedChanged(isSupported);
            BackgroundIsSetSupportedChanged(isSupported);
        //}


    }

    @Override
    public boolean IsSupported() {
        return ParameterHandler.mAvailableCameraApiSet != null && JsonUtils.isCameraApiAvailable("actZoom", ParameterHandler.mAvailableCameraApiSet);
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
        if (currentzoomPos == -1) {
            currentzoomPos = -1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject object = ParameterHandler.mRemoteApi.getEvent(false, "1.0");
                        JSONArray array = object.getJSONArray("result");
                        JSONObject zoom = array.getJSONObject(2);
                        String zoompos = zoom.getString("zoomPosition");
                        currentzoomPos = Integer.parseInt(zoompos);
                    } catch (IOException e) {
                        e.printStackTrace();
                        currentzoomPos = 0;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        currentzoomPos = 0;
                    }
                }
            }).start();
            while (currentzoomPos == -1) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
        if (zoomToSet != currentzoomPos && fromUser)
        {
            if (!checkIfIntIsInRange(zoomToSet, currentzoomPos))
                SetValue(zoomToSet);
            else {
                zoomToSet = currentzoomPos;
                fromUser = false;
            }
        }
        else
            zoomToSet = currentzoomPos;
        super.ThrowCurrentValueChanged(zoom);
    }


    private boolean checkIfIntIsInRange(int a, int b)
    {
        // 1 = 1
        if (a == b)
            return  true;
            //1 = 3
        else return a - 5 <= b && a + 5 >= b;
    }

    public String GetStringValue()
    {

        return currentzoomPos + "%";

    }

    @Override
    public String[] getStringValues() {
        return null;
    }

    @Override
    public void onCurrentValueChanged(int current)
    {

    }
}
