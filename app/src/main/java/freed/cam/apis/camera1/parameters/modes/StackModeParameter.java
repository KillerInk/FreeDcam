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

package freed.cam.apis.camera1.parameters.modes;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import freed.cam.apis.basecamera.parameters.modes.AbstractModeParameter;

/**
 * Created by Ingo on 15.05.2016.
 */
public class StackModeParameter extends AbstractModeParameter
{
    public static String AVARAGE = "avarage";
    public static String AVARAGE1x2 = "avarage1x2";
    public static String AVARAGE1x3 = "avarage1x3";
    public static String AVARAGE3x3 = "avarage3x3";
    public static String LIGHTEN = "lighten";
    public static String LIGHTEN_V = "lighten_v";
    public static String MEDIAN = "median";
    public static String EXPOSURE = "exposure";

    private String current = AVARAGE;

    public StackModeParameter() {
    }

    @Override
    public boolean IsSupported() {
        return VERSION.SDK_INT >= VERSION_CODES.KITKAT;
    }

    @Override
    public boolean IsVisible() {
        return VERSION.SDK_INT >= VERSION_CODES.KITKAT;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        current = valueToSet;
    }

    @Override
    public String GetValue() {
        return current;
    }

    @Override
    public String[] GetValues() {
        return new String[] {AVARAGE, AVARAGE1x2, AVARAGE1x3, AVARAGE3x3, LIGHTEN, LIGHTEN_V, MEDIAN, EXPOSURE};
    }
}
