package com.freedcam.apis.basecamera.camera.parameters.manual;

import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 17.12.2014.
 */


public abstract class AbstractManualParameter implements I_ManualParameter
{
    /**
     *
     */
    private List<I_ManualParameterEvent> listners;
    /**
     * the parameterhandler
     */
    protected AbstractParameterHandler camParametersHandler;
    /**
     * contains the values that are supported by the parameters
     */
    protected String[] stringvalues;
    /**
     * the value that is currently in use by the parameters
     */
    protected String currentString;
    /**
     * the true integer value that represents the the currentstring in the array stringvalues
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

    public AbstractManualParameter(AbstractParameterHandler camParametersHandler)
    {
        this.camParametersHandler = camParametersHandler;
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
         * @param current int value representing the array state
         */
        void onCurrentValueChanged(int current);
        /**
         * Notify the listner that the parameter has changed its values
         * com.freedcam.ui.themesample.views.ManualButton.java
         * @param values the new values
         */
        void onValuesChanged(String[] values);
        /**
         * Notify the listner that the parameter has changed its value
         * com.freedcam.ui.themesample.views.ManualButton.java
         * @param value the new string value
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

    public void BackgroundIsSupportedChanged(boolean value)
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
    public void BackgroundIsSetSupportedChanged(boolean value)
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

    public void BackgroundValuesChanged(String[] value)
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

    public boolean IsSetSupported() {return false;}

    @Override
    public boolean IsVisible() {
        return isVisible;
    }

    /**
     *
     * @return returns the current value as int
     */
    @Override
    public int GetValue() {
        return currentInt;
    }

    /**
     *
     * @return returns the current value as string
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
