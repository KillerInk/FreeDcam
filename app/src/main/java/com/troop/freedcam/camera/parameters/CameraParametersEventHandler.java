package com.troop.freedcam.camera.parameters;

import java.util.ArrayList;

/**
 * Created by troop on 21.08.2014.
 */
public class CameraParametersEventHandler
{
    ArrayList<I_ParametersLoaded> parametersLoadedListner;
    ArrayList<I_ParameterChanged> parameterChangedListner;

    public CameraParametersEventHandler()
    {
        parameterChangedListner = new ArrayList<I_ParameterChanged>();
        parametersLoadedListner = new ArrayList<I_ParametersLoaded>();
        parameterChangedListner.clear();
        parametersLoadedListner.clear();
    }

    public void AddParametersChangedListner(I_ParameterChanged parameterChanged)
    {
        parameterChangedListner.add(parameterChanged);
    }

    public void AddParametersLoadedListner(I_ParametersLoaded parametersLoaded)
    {
        parametersLoadedListner.add(parametersLoaded);
    }

    public void ParametersHasChanged()
    {
        for (I_ParameterChanged changed : parameterChangedListner)
            changed.ParameterChanged();
    }

    public void ParametersHasLoaded()
    {
        for(int i= 0; i< parametersLoadedListner.size(); i++)
        {
            if (parametersLoadedListner.get(i) == null) {
                parametersLoadedListner.remove(i);
                i--;
            }
            else
                parametersLoadedListner.get(i).ParametersLoaded();
        }
    }

    public void CLEAR()
    {
        parameterChangedListner.clear();
        parametersLoadedListner.clear();
    }
}
