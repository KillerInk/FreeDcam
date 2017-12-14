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

import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.sonyremote.parameters.ParameterHandler;
import freed.cam.apis.sonyremote.sonystuff.JsonUtils;
import freed.utils.FreeDPool;
import freed.utils.Log;

/**
 * Created by troop on 15.12.2014.
 */
public class ZoomManualSony extends BaseManualParameterSony
{
    final String TAG = ZoomManualSony.class.getSimpleName();
    private int zoomToSet;
    private boolean isZooming;

    private boolean fromUser;

    public ZoomManualSony(CameraWrapperInterface cameraUiWrapper) {
        super("actZoom", "", "actZoom", cameraUiWrapper);
    }

    @Override
    public void SonyApiChanged(Set<String> mAvailableCameraApiSet)
    {
        this.mAvailableCameraApiSet = mAvailableCameraApiSet;
        //if (isSupported != JsonUtils.isCameraApiAvailable("actZoom", mAvailableCameraApiSet))
        //{
        isSupported = JsonUtils.isCameraApiAvailable("actZoom", mAvailableCameraApiSet);
        fireIsSupportedChanged(isSupported);
        fireIsReadOnlyChanged(isSupported);
        stringvalues = createStringArray(0,100,1);
        //}


    }

    @Override
    public boolean IsSupported() {
        return ((ParameterHandler) cameraUiWrapper.getParameterHandler()).mAvailableCameraApiSet != null && JsonUtils.isCameraApiAvailable("actZoom", ((ParameterHandler) cameraUiWrapper.getParameterHandler()).mAvailableCameraApiSet);
    }

    public int GetValue()
    {
        /*if (currentInt == -1) {
            currentInt = -1;
            FreeDPool.Execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject object = mRemoteApi.getEvent(false, "1.0");
                        JSONArray array = object.getJSONArray("result");
                        JSONObject zoom = array.getJSONObject(2);
                        String zoompos = zoom.getApiString("zoomPosition");
                        currentInt = Integer.parseInt(zoompos);
                    } catch (IOException | JSONException e) {
                        Log.exception(e);
                        currentInt = 0;
                    }
                }
            });
            while (currentInt == -1) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Log.exception(e);
                }
            }
        }*/
        return currentInt;
    }

    @Override
    public void SetValue(int valueToSet, boolean setToCamera)
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
                        JSONObject object = mRemoteApi.actZoom(finaldirection, movement);
                        isZooming = false;
                    } catch (IOException ex) {
                        Log.WriteEx(ex);
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
                SetValue(zoomToSet, true);
            else {
                zoomToSet = currentInt;
                fromUser = false;
            }
        }
        else
            zoomToSet = currentInt;
        fireIntValueChanged(zoom);
    }


    private boolean checkIfIntIsInRange(int a, int b) {
        // 1 = 1
        //1 = 3
        return a == b || a - 5 <= b && a + 5 >= b;
    }

    public String GetStringValue()
    {

        return currentInt + "%";

    }

    @Override
    public void onIntValueChanged(int current)
    {

    }
}
