package com.troop.freecam.interfaces;

import com.troop.freecam.manager.parameters.ParametersManager;

/**
 * Created by troop on 01.12.13.
 */
public interface ParametersChangedInterface
{
    public void parametersHasChanged(boolean restarted, ParametersManager.enumParameters enumParameters);

}
