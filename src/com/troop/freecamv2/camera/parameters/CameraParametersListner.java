package com.troop.freecamv2.camera.parameters;

import android.media.audiofx.BassBoost;

import com.troop.freecam.interfaces.ParametersChangedInterface;

import java.util.ArrayList;

/**
 * Created by troop on 21.08.2014.
 */
public class CameraParametersListner
{
    ArrayList<I_ParametersLoaded> parametersLoadedListner;
    ArrayList<I_ParameterChanged> parameterChangedListner;

    public CameraParametersListner()
    {
        parameterChangedListner = new ArrayList<I_ParameterChanged>();
        parametersLoadedListner = new ArrayList<I_ParametersLoaded>();
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
        for(I_ParametersLoaded loaded : parametersLoadedListner)
            loaded.ParametersLoaded();
    }
}
