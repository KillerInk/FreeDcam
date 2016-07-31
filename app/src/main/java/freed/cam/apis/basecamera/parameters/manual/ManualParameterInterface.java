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
 * This class represent the basic ManualPrameter that get used in ManualcameraFragment
 */
public interface ManualParameterInterface
{
    /**
     * State of the parameter if its is supported
     * @return true when supported, false when not
     */
    boolean IsSupported();

    /**
     * State of the parameter if its read only
     * @return true when it can get set. false when its read only
     */
    boolean IsSetSupported();

    /**
     * state of the parameter if its visible to the ui
     * @return true when its should be visible to the ui
     */
    boolean IsVisible();

    /**
     * the current int value from the String array
     * @return
     */
    int GetValue();

    /**
     *
     * @return the current String value from the array
     */
    String GetStringValue();

    /**
     *
     * @return the string array with all values represent by that parameters
     */
    String[] getStringValues();

    /**
     * Set the value to the parameters
     * @param valueToSet the int value to set
     */
    void SetValue(int valueToSet);

    /**
     * add a listner to listne to background changes applied to the parameters
     * @param eventListner to notify when something got changed in background
     */
    void addEventListner(AbstractManualParameter.I_ManualParameterEvent eventListner);

    /**
     * remove the listner
     * @param eventListner to remove
     */
    void removeEventListner(AbstractManualParameter.I_ManualParameterEvent eventListner);

    /**
     * notify the listners that the int value has changed in background
     * @param current
     */
    void ThrowCurrentValueChanged(int current);

    /**
     * notify the listner that the string value got changed in background
     * @param value
     */
    void ThrowCurrentValueStringCHanged(String value);

    /**
     * notfiy the listners that the parameter support state got changed
     * @param value
     */
    void ThrowBackgroundIsSupportedChanged(boolean value);

    /**
     * notify the listners that the parameter can get written or is read only
     * @param value when true its writeable, false its read only
     */
    void ThrowBackgroundIsSetSupportedChanged(boolean value);

    /**
     * notify the listners that string values have changed
     * @param value new values that are useable to get set
     */
    void ThrowBackgroundValuesChanged(String[] value);
}
