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

package freed.cam.apis.sonyremote.parameters.modes;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterEvents;
import freed.cam.apis.sonyremote.sonystuff.SimpleStreamSurfaceView;

/**
 * Created by troop on 25.03.2016.
 */
public class PreviewZoomSony extends AbstractParameter
{
    private final SimpleStreamSurfaceView surfaceView;
    private int zoomFactor = 8;
    public PreviewZoomSony( SimpleStreamSurfaceView surfaceView) {
        this.surfaceView = surfaceView;
    }

    @Override
    public boolean IsSupported() {
        return VERSION.SDK_INT >= VERSION_CODES.KITKAT;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        zoomFactor = Integer.parseInt(valueToSet);
        surfaceView.PreviewZOOMFactor = zoomFactor;
        fireIntValueChanged(zoomFactor);
    }

    @Override
    public String GetStringValue() {
        return zoomFactor +"";
    }

    @Override
    public String[] getStringValues() {
        return new String[] {"2","4","8","10","12","14","16","18","20"};
    }

    @Override
    public void addEventListner(ParameterEvents eventListner) {
        super.addEventListner(eventListner);
    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }
}
