package com.troop.freedcam.sonyapi.parameters.manual;

import com.troop.filelogger.Logger;
import com.troop.freedcam.sonyapi.parameters.ParameterHandlerSony;
import com.troop.freedcam.sonyapi.sonystuff.JsonUtils;
import com.troop.freedcam.ui.FreeDPool;

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
    final String TAG = ZoomManualSony.class.getSimpleName();
    private int zoomToSet;
    private boolean isZooming = false;

    private boolean fromUser = false;

    public ZoomManualSony(String MIN_TO_GET, String CURRENT_TO_GET, ParameterHandlerSony parameterHandlerSony) {
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
        stringvalues = createStringArray(0,100,1);
        //}


    }

    @Override
    public boolean IsSupported() {
        return ParameterHandler.mAvailableCameraApiSet != null && JsonUtils.isCameraApiAvailable("actZoom", ParameterHandler.mAvailableCameraApiSet);
    }

    @Override
    public int GetValue()
    {
        if (currentInt == -1) {
            currentInt = -1;
            FreeDPool.Execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject object = ParameterHandler.mRemoteApi.getEvent(false, "1.0");
                        JSONArray array = object.getJSONArray("result");
                        JSONObject zoom = array.getJSONObject(2);
                        String zoompos = zoom.getString("zoomPosition");
                        currentInt = Integer.parseInt(zoompos);
                    } catch (IOException | JSONException e) {
                        Logger.exception(e);
                        currentInt = 0;
                    }
                }
            });
            while (currentInt == -1) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Logger.exception(e);
                }
            }
        }
        return currentInt;
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
            if (valueToSet < currentInt)
                direction = "out";
            else
                direction = "in";
            //currentzoomPos = valueToSet;
            final String finaldirection = direction;
            FreeDPool.Execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject object = ParameterHandler.mRemoteApi.actZoom(finaldirection, movement);
                        isZooming = false;
                    } catch (IOException e) {
                        Logger.exception(e);
                    }
                }
            });
        }
        //super.SetValue(valueToSet);
    }

    public void setZoomsHasChanged(int zoom)
    {

        currentInt = zoom;
        if (zoomToSet != currentInt && fromUser)
        {
            if (!checkIfIntIsInRange(zoomToSet, currentInt))
                SetValue(zoomToSet);
            else {
                zoomToSet = currentInt;
                fromUser = false;
            }
        }
        else
            zoomToSet = currentInt;
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

        return currentInt + "%";

    }

    @Override
    public void onCurrentValueChanged(int current)
    {

    }
}
