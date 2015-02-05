package com.troop.freedcam.camera.parameters;

import java.util.ArrayList;

/**
 * Created by troop on 21.08.2014.
 */
public class CameraParametersEventHandler
{
    ArrayList<I_ParametersLoaded> parametersLoadedListner;

    public CameraParametersEventHandler()
    {
        parametersLoadedListner = new ArrayList<I_ParametersLoaded>();
        parametersLoadedListner.clear();
    }

    public void AddParametersLoadedListner(I_ParametersLoaded parametersLoaded)
    {
        parametersLoadedListner.add(parametersLoaded);
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
        
        parametersLoadedListner.clear();
    }
}
