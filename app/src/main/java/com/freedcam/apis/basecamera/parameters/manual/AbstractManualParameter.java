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

package com.freedcam.apis.basecamera.parameters.manual;

import com.freedcam.apis.basecamera.parameters.AbstractParameterHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 17.12.2014.
 */


public abstract class AbstractManualParameter implements I_ManualParameter
{
    /**
     * Listners that attached to that parameter
     */
    private List<I_ManualParameterEvent> listners;
    /**
     * the parameterhandler
     */
    protected AbstractParameterHandler parametersHandler;
    /**
     * contains the values that are supported by the parameters
     */
    protected String[] stringvalues;
    /**
     * the key_value that is currently in use by the parameters
     */
    protected String currentString;
    /**
     * the true integer key_value that represents the the currentstring in the array stringvalues
     * so on negative values  -1 = stringarray[stringarray/2 + -1] must get used
     */
    protected int currentInt;
    /**
     * holds the state if the parameter is supported
     */
    protected boolean isSupported = false;
    /**
     * holds the state if the parameter should be visible to ui
     */
    protected boolean isVisible = false;

    public AbstractManualParameter(AbstractParameterHandler parametersHandler)
    {
        this.parametersHandler = parametersHandler;
        listners = new ArrayList<>();
    }

    public interface I_ManualParameterEvent
    {
        /**
         * Notify the listner that the parameter support state has changed
         * com.freedcam.ui.themesample.views.ManualButton.java
         * @param value if true the parameter shown in ui is visible
         *              if false the parameter is hidden
         */
        void onIsSupportedChanged(boolean value);
        /**
         * Notify the listner that the parameter can changed/set state has changed
         * com.freedcam.ui.themesample.views.ManualButton.java
         * @param value if true the parameter shown in ui is accessible
         *              if false the parameter is not accessible
         */
        void onIsSetSupportedChanged(boolean value);
        /**
         * Notify the listner that the parameter has changed in the background
         * com.freedcam.ui.themesample.views.ManualButton.java
         * @param current int key_value representing the array state
         */
        void onCurrentValueChanged(int current);
        /**
         * Notify the listner that the parameter has changed its values
         * com.freedcam.ui.themesample.views.ManualButton.java
         * @param values the new values
         */
        void onValuesChanged(String[] values);
        /**
         * Notify the listner that the parameter has changed its key_value
         * com.freedcam.ui.themesample.views.ManualButton.java
         * @param value the new string key_value
         */
        void onCurrentStringValueChanged(String value);
    }


    /**
     * Add and listner that get informed when somthings happen
     * @param eventListner that gets informed
     */
    public void addEventListner(I_ManualParameterEvent eventListner)
    {
        if (!listners.contains(eventListner))
            listners.add(eventListner);
    }
    /**
    * Remove the listner
    * @param eventListner that gets informed
    */
    public void removeEventListner(I_ManualParameterEvent eventListner)
    {
        if (listners.contains(eventListner))
            listners.remove(eventListner);
    }

    public void ThrowCurrentValueChanged(int current)
    {
        for (int i = 0; i< listners.size(); i ++)
        {
            if (listners.get(i) == null)
            {
                listners.remove(i);
                i--;

            }
            else
                listners.get(i).onCurrentValueChanged(current);
        }
    }

    public void ThrowCurrentValueStringCHanged(String value)
    {
        for (int i = 0; i< listners.size(); i ++)
        {
            if (listners.get(i) == null)
            {
                listners.remove(i);
                i--;

            }
            else
                listners.get(i).onCurrentStringValueChanged(value);
        }
    }

    public void ThrowBackgroundIsSupportedChanged(boolean value)
    {
        for (int i = 0; i< listners.size(); i ++)
        {
            if (listners.get(i) == null)
            {
                listners.remove(i);
                i--;

            }
            else
                listners.get(i).onIsSupportedChanged(value);
        }
    }
    public void ThrowBackgroundIsSetSupportedChanged(boolean value)
    {
        for (int i = 0; i< listners.size(); i ++)
        {
            if (listners.get(i) == null)
            {
                listners.remove(i);
                i--;

            }
            else
                listners.get(i).onIsSetSupportedChanged(value);
        }
    }

    public void ThrowBackgroundValuesChanged(String[] value)
    {
        for (int i = 0; i< listners.size(); i ++)
        {
            if (listners.get(i) == null)
            {
                listners.remove(i);
                i--;

            }
            else
                listners.get(i).onValuesChanged(value);
        }
    }



    /**
     *
     * @return true if the parameter is supported
     */
    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    /**
     * if true the parameter can get set and is readable
     * if false the parameter is read only
     * @return  parameter can set
     */
    public boolean IsSetSupported() {return false;}

    /**
     *
     * @return the visiblity state for the ui item
     */
    @Override
    public boolean IsVisible() {
        return isVisible;
    }

    /**
     *
     * @return returns the current key_value as int
     */
    @Override
    public int GetValue() {
        return currentInt;
    }

    /**
     *
     * @return returns the current key_value as string
     */
    @Override
    public String GetStringValue()
    {
        if (stringvalues == null || stringvalues.length == 0)
            return null;
        if (currentInt > stringvalues.length)
            return stringvalues[currentInt - stringvalues.length/2];
        else
            return stringvalues[currentInt];
    }

    /**
     *
     * @return returns all values as StringArray
     */
    @Override
    public String[] getStringValues() { return  stringvalues;}

    @Override
    public void SetValue(int valueToSet)
    {

        //ThrowCurrentValueChanged(valueToSet);
    }

    /**
     * Creates a string array from the passed arguments
     * @param min the minimum value to start
     * @param max the maximum value to end
     * @param step the step wich get applied from min to max
     * @return the string array created
     */
    protected String[] createStringArray(int min,int max, float step)
    {
        ArrayList<String> ar = new ArrayList<>();
        if (step == 0)
            step = 1;
        for (int i = min; i <= max; i+=step)
        {
            ar.add(i+"");
        }
        return ar.toArray(new String[ar.size()]);
    }
}
