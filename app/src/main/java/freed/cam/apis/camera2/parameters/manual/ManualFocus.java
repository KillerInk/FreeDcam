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

package freed.cam.apis.camera2.parameters.manual;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureRequest;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera2.Camera2;
import freed.cam.events.EventBusHelper;
import freed.cam.events.FocusPositionChangedEvent;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.StringFloatArray;

/**
 * Created by troop on 28.04.2015.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class ManualFocus extends AbstractParameter<Camera2>
{
    private final String TAG = ManualFocus.class.getSimpleName();
    protected StringFloatArray focusvalues;
    private Handler handler = new Handler(Looper.getMainLooper());

    public ManualFocus(Camera2 cameraUiWrapper)
    {
        super(cameraUiWrapper,SettingKeys.M_Focus);
        if (stringvalues != null && stringvalues.length > 0) {
            focusvalues = new StringFloatArray(stringvalues);
            setViewState(ViewState.Visible);
        }
        else
            setViewState(ViewState.Hidden);
    }

    @Override
    public int GetValue() {
        return currentInt;
    }

    @Override
    public String GetStringValue()
    {
        return focusvalues.getKey(currentInt);
    }


    @Override
    public void setValue(int valueToSet, boolean setToCamera)
    {
        super.setValue(valueToSet,setToCamera);
        //set to auto
        if(valueToSet == 0)
        {
            //apply last used focuse mode
            cameraUiWrapper.getParameterHandler().get(SettingKeys.FocusMode).SetValue(settingsManager.get(SettingKeys.FocusMode).get(), setToCamera);
            cameraUiWrapper.captureSessionHandler.SetParameter(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE);
        }
        else // set to manual
        {
            //if focusmode is in any other mode, turn af off
            if (!cameraUiWrapper.getParameterHandler().get(SettingKeys.FocusMode).GetStringValue().equals(FreedApplication.getStringFromRessources(R.string.off)))
            {
                //apply turn off direct to the capturesession, else it get stored in settings.
                cameraUiWrapper.getParameterHandler().get(SettingKeys.FocusMode).fireStringValueChanged(FreedApplication.getStringFromRessources(R.string.off));
                cameraUiWrapper.captureSessionHandler.SetParameter(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
                cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF,setToCamera);
            }
            applyAutoZoom();
            if (currentInt > focusvalues.getSize())
                currentInt = focusvalues.getSize() -1;
            float valtoset= focusvalues.getValue(currentInt);
            Log.d(TAG, "Set MF TO: " + valtoset+ " ValueTOSET: " + valueToSet);
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequest.LENS_FOCUS_DISTANCE, valtoset,setToCamera);

        }
    }

    private void applyAutoZoom()
    {
        if (settingsManager.get(SettingKeys.ZOOM_ON_MANUALFOCUS).isSupported() && settingsManager.get(SettingKeys.ZOOM_ON_MANUALFOCUS).get())
        {
            int factor = Integer.parseInt(settingsManager.get(SettingKeys.ZOOM_ON_MANUALFOCUS_ZOOMFACTOR).get());
            cameraUiWrapper.getParameterHandler().get(SettingKeys.M_Zoom).SetValue(factor,true);
            handler.removeCallbacks(resetzoomRunner);
            int delay = Integer.parseInt(settingsManager.get(SettingKeys.ZOOM_ON_MANUALFOCUS_ZOOMDURATION).get());
            handler.postDelayed(resetzoomRunner,delay*1000);
        }
    }

    private Runnable resetzoomRunner = new Runnable() {
        @Override
        public void run() {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.M_Zoom).SetValue(0,true);
        }
    };

    @Override
    public String[] getStringValues() {
        return focusvalues.getKeys();
    }

    public float getFloatValue(int index)
    {
        return focusvalues.getValue(index);
    }

    public String getStringValue(int index)
    {
        return focusvalues.getKey(index);
    }

    @Override
    public void fireStringValueChanged(String value)
    {
        currentString = value;
        EventBusHelper.post(new FocusPositionChangedEvent(key,value, String.class));
    }


}
