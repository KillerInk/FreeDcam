package com.troop.freedcam.i_camera.parameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 17.12.2014.
 */


public abstract class AbstractManualParameter implements I_ManualParameter
{

    private List<I_ManualParameterEvent> events;
    protected AbstractParameterHandler camParametersHandler;
    protected String[] stringvalues;
    protected String currentString;
    protected int currentInt;
    /**
     * holds the state if the parameter is supported
     */
    protected boolean isSupported = false;

    protected boolean isVisible = false;

    public AbstractManualParameter(AbstractParameterHandler camParametersHandler)
    {
        this.camParametersHandler = camParametersHandler;
        events = new ArrayList<I_ManualParameterEvent>();
    }

    public interface I_ManualParameterEvent
    {
        void onIsSupportedChanged(boolean value);
        void onIsSetSupportedChanged(boolean value);
        void onCurrentValueChanged(int current);
        void onValuesChanged(String[] values);
        void onCurrentStringValueChanged(String value);
    }


    public void addEventListner(I_ManualParameterEvent eventListner)
    {
        if (!events.contains(eventListner))
            events.add(eventListner);
    }
    public void removeEventListner(I_ManualParameterEvent parameterEvent)
    {
        if (events.contains(parameterEvent))
            events.remove(parameterEvent);
    }

    public void ThrowCurrentValueChanged(int current)
    {
        for (int i= 0; i< events.size(); i ++)
        {
            if (events.get(i) == null)
            {
                events.remove(i);
                i--;

            }
            else
                events.get(i).onCurrentValueChanged(current);
        }
    }

    public void ThrowCurrentValueStringCHanged(String value)
    {
        for (int i= 0; i< events.size(); i ++)
        {
            if (events.get(i) == null)
            {
                events.remove(i);
                i--;

            }
            else
                events.get(i).onCurrentStringValueChanged(value);
        }
    }

    public void BackgroundIsSupportedChanged(boolean value)
    {
        for (int i= 0; i< events.size(); i ++)
        {
            if (events.get(i) == null)
            {
                events.remove(i);
                i--;

            }
            else
                events.get(i).onIsSupportedChanged(value);
        }
    }
    public void BackgroundIsSetSupportedChanged(boolean value)
    {
        for (int i= 0; i< events.size(); i ++)
        {
            if (events.get(i) == null)
            {
                events.remove(i);
                i--;

            }
            else
                events.get(i).onIsSetSupportedChanged(value);
        }
    }

    public void BackgroundValuesChanged(String[] value)
    {
        for (int i= 0; i< events.size(); i ++)
        {
            if (events.get(i) == null)
            {
                events.remove(i);
                i--;

            }
            else
                events.get(i).onValuesChanged(value);
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
        return stringvalues[currentInt];
    }

    /**
     *
     * @return returns all values possible vales as StringArray
     */
    public String[] getStringValues() { return  stringvalues;}

    @Override
    public void SetValue(int valueToSet)
    {
        setvalue(valueToSet);
        //ThrowCurrentValueChanged(valueToSet);
    }

    protected void setvalue(int valueToset)
    {}

    protected String[] createStringArray(int min,int max, int step)
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
