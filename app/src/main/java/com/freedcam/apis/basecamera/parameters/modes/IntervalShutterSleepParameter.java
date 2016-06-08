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

package com.freedcam.apis.basecamera.parameters.modes;

/**
 * Created by troop on 08.01.2016.
 */
public class IntervalShutterSleepParameter extends AbstractModeParameter
{
    private String current = "1 sec";
    public IntervalShutterSleepParameter() {
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {
        current = valueToSet;
    }

    @Override
    public String GetValue() {
        return current;
    }

    @Override
    public String[] GetValues() {
        return new String[] {/*"off",*/"1 sec","2 sec","3 sec","4 sec","5 sec","6 sec","7 sec","8 sec","9 sec",
                "10 sec","11 sec","12 sec","13 sec","14 sec","15 sec","16 sec","17 sec","18 sec","19 sec","20 sec",
                "21 sec","22 sec","23 sec","24 sec","25 sec","26 sec","27 sec","28 sec","29 sec","30 sec","60 sec","120 sec","240 sec"};
    }
}
