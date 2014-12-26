package com.troop.freedcam.i_camera.parameters;

import java.util.List;

/**
 * Created by Ingo on 26.12.2014.
 */
public class AbstractModeParameter implements I_ModeParameter
{

    public interface I_ModeParameterEvent
    {
        void onModeParameterChanged(String val);

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
                events.get(i).onModeParameterChanged(value);
        }
    }
}
