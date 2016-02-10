package com.troop.freedcam.i_camera.parameters;

import android.os.Handler;

import java.util.ArrayList;


/**
 * Created by troop on 21.08.2014.
 */
public class CameraParametersEventHandler
{
    private ArrayList<I_ParametersLoaded> parametersLoadedListner;
    private Handler uiHandler;

    public CameraParametersEventHandler(Handler uiHandler)
    {
        parametersLoadedListner = new ArrayList<I_ParametersLoaded>();
        parametersLoadedListner.clear();
        this.uiHandler = uiHandler;
    }

    public void AddParametersLoadedListner(I_ParametersLoaded parametersLoaded)
    {
        parametersLoadedListner.add(parametersLoaded);
    }

    public void ParametersHasLoaded()
    {
        if (parametersLoadedListner == null)
            return;
        for(int i= 0; i< parametersLoadedListner.size(); i++)
        {

            if (parametersLoadedListner.get(i) == null) {
                parametersLoadedListner.remove(i);
                i--;
            }
            else {
                final int t = i;
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (parametersLoadedListner.size()> 0 && t < parametersLoadedListner.size())
                            parametersLoadedListner.get(t).ParametersLoaded();
                    }
                });

            }
        }
    }

    public void CLEAR()
    {
        
        parametersLoadedListner.clear();
    }
}
