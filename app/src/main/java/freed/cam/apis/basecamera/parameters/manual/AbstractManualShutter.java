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

package freed.cam.apis.basecamera.parameters.manual;

import com.troop.freedcam.R;

import java.util.ArrayList;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;

/**
 * Created by troop on 19.05.2016.
 */
public abstract class AbstractManualShutter
{


    public static long getMilliSecondStringFromShutterString(String shuttervalue)
    {
        float a;
        if (shuttervalue.contains("/")) {
            String[] split = shuttervalue.split("/");
            a = Float.parseFloat(split[0]) / Float.parseFloat(split[1])*1000000f;
        }
        else
            a = Float.parseFloat(shuttervalue)*1000000f;
        a = Math.round(a);
        return  (long)a;
    }
}
