package com.troop.freedcam.i_camera.parameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 17.12.2014.
 */


public abstract class AbstractManualParameter implements I_ManualParameter
{

    private List<I_ParameterEvent> events;
    protected AbstractParameterHandler camParametersHandler;

    public AbstractManualParameter(AbstractParameterHandler camParametersHandler)
    {
        this.camParametersHandler = camParametersHandler;
        events = new ArrayList<I_ParameterEvent>();
    }

    public interface I_ParameterEvent
    {
        void onIsSupportedChanged(boolean value);
        void onMaxValueChanged(int max);
        void onMinValueChanged(int min);
        void onCurrentValueChanged(int current);
    }


    public void addEventListner(I_ParameterEvent eventListner)
    {
        if (!events.contains(eventListner))
            events.add(eventListner);
    }
    public void removeEventListner(I_ParameterEvent parameterEvent)
    {
        if (events.contains(parameterEvent))
            events.remove(parameterEvent);
    }

    public void currentValueChanged(int current)
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

    @Override
    public boolean IsSupported() {
        return false;
    }

    @Override
    public int GetMaxValue() {
        return 0;
    }

    @Override
    public int GetMinValue() {
        return 0;
    }

    @Override
    public int GetValue() {
        return 0;
    }

    @Override
    public void SetValue(int valueToSet)
    {
        setvalue(valueToSet);
        currentValueChanged(valueToSet);
    }

    @Override
    public void RestartPreview() {

    }

    protected void setvalue(int valueToset)
    {}
}
