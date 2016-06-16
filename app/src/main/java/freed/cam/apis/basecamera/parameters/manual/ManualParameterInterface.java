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

/**
 * Created by troop on 01.09.2014.
 */
public interface ManualParameterInterface
{
    boolean IsSupported();
    boolean IsSetSupported();
    boolean IsVisible();

    int GetValue();
    String GetStringValue();
    String[] getStringValues();
    void SetValue(int valueToSet);

    void addEventListner(AbstractManualParameter.I_ManualParameterEvent eventListner);
    void removeEventListner(AbstractManualParameter.I_ManualParameterEvent eventListner);
    void ThrowCurrentValueChanged(int current);
    void ThrowCurrentValueStringCHanged(String value);
    void ThrowBackgroundIsSupportedChanged(boolean value);
    void ThrowBackgroundIsSetSupportedChanged(boolean value);
    void ThrowBackgroundValuesChanged(String[] value);
}
