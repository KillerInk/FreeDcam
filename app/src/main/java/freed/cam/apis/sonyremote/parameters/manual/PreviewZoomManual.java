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

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import java.util.Set;

import freed.cam.apis.basecamera.CameraControllerInterface;
import freed.cam.apis.sonyremote.PreviewStreamDrawer;
import com.troop.freedcam.settings.SettingKeys;

/**
 * Created by troop on 09.04.2016.
 */
public class PreviewZoomManual extends BaseManualParameterSony
{
    private final PreviewStreamDrawer surfaceView;

    public PreviewZoomManual(PreviewStreamDrawer surfaceView, CameraControllerInterface cameraUiWrapper) {
        super("", "", "", cameraUiWrapper, SettingKeys.M_PreviewZoom);
        this.surfaceView = surfaceView;
        if(VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP)
            setViewState(ViewState.Visible);
        stringvalues = new String[] {"1","2","4","8","10","12","14","16","18","20"};
    }

    @Override
    public void SonyApiChanged(Set<String> mAvailableCameraApiSet) {
        super.SonyApiChanged(mAvailableCameraApiSet);
    }

    @Override
    public String[] getStringValues() {
        return stringvalues;
    }

    @Override
    public void SetValue(int valueToSet, boolean setToCamera) {
        surfaceView.PreviewZOOMFactor = Integer.parseInt(stringvalues[valueToSet]);
        fireIntValueChanged(valueToSet);
    }

    @Override
    public String GetStringValue() {
        return surfaceView.PreviewZOOMFactor+"";
    }
}
