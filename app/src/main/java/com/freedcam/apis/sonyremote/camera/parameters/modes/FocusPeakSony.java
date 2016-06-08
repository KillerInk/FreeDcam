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

package com.freedcam.apis.sonyremote.camera.parameters.modes;

import android.os.Build;
import android.os.Build.VERSION;

import com.freedcam.apis.sonyremote.camera.sonystuff.SimpleStreamSurfaceView;
import com.freedcam.utils.StringUtils;

import java.util.Set;

/**
 * Created by troop on 23.08.2015.
 */
public class FocusPeakSony extends BaseModeParameterSony {

    private String currentval = StringUtils.OFF;
    private SimpleStreamSurfaceView simpleStreamSurfaceView;


    public FocusPeakSony(SimpleStreamSurfaceView simpleStreamSurfaceView) {
        super(null, null, null, null);
        this.simpleStreamSurfaceView = simpleStreamSurfaceView;
    }

    public void SetValue(String valueToSet, boolean setToCamera)
    {
        simpleStreamSurfaceView.focuspeak = valueToSet.equals(StringUtils.ON);
    }

    @Override
    public String GetValue()
    {
        if (simpleStreamSurfaceView.focuspeak)
            return StringUtils.ON;
        else
            return StringUtils.OFF;
    }

    @Override
    public String[] GetValues() {
        return new String[] {StringUtils.ON, StringUtils.OFF};
    }

    @Override
    public boolean IsSupported() {
        return VERSION.SDK_INT >= 18;
    }


    @Override
    public void SonyApiChanged(Set<String> mAvailableCameraApiSet) {
        //super.SonyApiChanged(mAvailableCameraApiSet);
    }

    @Override
    protected void processValuesToSet(String valueToSet) {
        //super.processValuesToSet(valueToSet);
    }

    @Override
    protected String processGetString() {
        return null;
    }

    @Override
    protected String[] processValuesToReturn() {
        return null;
    }
}
