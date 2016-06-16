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

package freed.cam.apis.camera1.parameters.manual;

import android.hardware.Camera.Parameters;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.utils.Logger;

/**
 * Created by Ingo on 06.03.2016.
 */
public class BaseWB_CCT_QC extends BaseCCTManual {
    public BaseWB_CCT_QC(Parameters parameters, int max, int min, CameraWrapperInterface cameraUiWrapper, float step, String wbmode) {
        super(parameters, KEYS.WB_MANUAL_CCT, 8000, 2000, cameraUiWrapper, (float) 100, KEYS.WB_MODE_MANUAL_CCT);
    }

    public BaseWB_CCT_QC(Parameters parameters, String value, String maxValue, String MinValue, CameraWrapperInterface cameraUiWrapper, float step, String wbmode) {
        super(parameters, value, maxValue, MinValue, cameraUiWrapper, step, wbmode);
    }

    @Override
    protected void set_manual() {
        super.set_manual();
        try {
            parameters.set(KEYS.MANUAL_WB_TYPE, KEYS.MANUAL_WB_TYPE_COLOR_TEMPERATURE);
            parameters.set(KEYS.MANUAL_WB_VALUE, stringvalues[currentInt]);
        } catch (Exception ex) {
            Logger.exception(ex);}
    }
}
