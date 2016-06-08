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

package com.freedcam.apis.camera2.parameters.manual;

import android.os.Handler;
import android.os.Looper;

import com.freedcam.apis.basecamera.parameters.AbstractParameterHandler;
import com.freedcam.apis.basecamera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.parameters.modes.AbstractModeParameter.I_ModeParameterEvent;
import com.freedcam.apis.camera2.CameraHolder;

/**
 * Created by troop on 10.09.2015.
 */
public class BurstApi2 extends AbstractManualParameter implements I_ModeParameterEvent
{
    int current = 1;
    CameraHolder cameraHolder;

    public BurstApi2(AbstractParameterHandler camParametersHandler, CameraHolder cameraHolder) {
        super(camParametersHandler);
        this.cameraHolder = cameraHolder;
    }

    @Override
    public void onValueChanged(String val) {

    }

    @Override
    public void onIsSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onIsSetSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public void onVisibilityChanged(boolean visible) {

    }

    @Override
    public boolean IsSupported() {
        return false;
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }

    @Override
    public int GetValue() {
        return current;
    }

    @Override
    public String GetStringValue() {
        return current +"";
    }

    @Override
    public String[] getStringValues() {
        return null;
    }

    @Override
    public void SetValue(int valueToSet)
    {
        current = valueToSet;
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                cameraHolder.StopPreview();
                //TODO FIX BURST
                //cameraHolder.SetBurst(current+1);
            }
        });


    }
}
