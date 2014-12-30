package com.troop.freedcam.i_camera.parameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ingo on 26.12.2014.
 */
public class AbstractModeParameter implements I_ModeParameter
{

    public AbstractModeParameter()
    {
        events = new ArrayList<I_ModeParameterEvent>();
    }
    public interface I_ModeParameterEvent
    {
        void onValueChanged(String val);
        void onIsSupportedChanged(boolean isSupported);
        void onIsSetSupportedChanged(boolean isSupported);
        void onValuesChanged(String[] values);

    }

    private List<I_ModeParameterEvent> events;

    public void addEventListner(I_ModeParameterEvent eventListner)
    {
        if (!events.contains(eventListner))
            events.add(eventListner);
    }
    public void removeEventListner(I_ModeParameterEvent parameterEvent)
    {
        if (events.contains(parameterEvent))
            events.remove(parameterEvent);
    }

    @Override
    public boolean IsSupported() {
        return false;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {

    }

    @Override
    public String GetValue() {
        return null;
    }

    @Override
    public String[] GetValues() {
        return new String[0];
    }

    public void BackgroundValueHasChanged(String value)
    {
        for (int i= 0; i< events.size(); i ++)
        {
            if (events.get(i) == null)
            {
                events.remove(i);
                i--;

            }
            else
                events.get(i).onValueChanged(value);
        }
    }
    public void BackgroundValuesHasChanged(String[] value)
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

    public void BackgroundSetIsSupportedHasChanged(boolean value)
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
}
