package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by troop on 02.09.2014.
 */
public class FocusModeParameter extends BaseModeParameter
{
    private String curmodule = AbstractModuleHandler.MODULE_PICTURE;
    public FocusModeParameter(Handler handler,HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values) {
        super(handler, parameters, parameterChanged, value, values);
    }


    @Override
    public String ModuleChanged(String module)
    {

        curmodule = module;

        return null;
    }


    @Override
    public String[] GetValues()
    {
        List<String> Trimmed = new ArrayList<>(Arrays.asList(parameters.get("focus-mode-values").split(",")));
        boolean hasCont = false;




        if(Trimmed.contains("manual")) {
            Trimmed.remove("manual");
            //return Trimmed.toArray(new String[Trimmed.size()]);
        }

        if(parameters.containsKey("focus-areas")) {
            Trimmed.add("touch");
            //return Trimmed.toArray(new String[Trimmed.size()]);
        }

        if(Trimmed.contains("continuous-video")) {
            Trimmed.remove("continuous-video");
            hasCont =true;
            //return Trimmed.toArray(new String[Trimmed.size()]);
        }
        if(Trimmed.contains("continuous-picture")) {
            Trimmed.remove("continuous-picture");
            hasCont=true;
            //return Trimmed.toArray(new String[Trimmed.size()]);
        }

        if(hasCont) {
            Trimmed.add("continuous");
            //return Trimmed.toArray(new String[Trimmed.size()]);
        }


        return Trimmed.toArray(new String[Trimmed.size()]);



    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        if(valueToSet.equals("continuous"))
        {
            switch (curmodule)
            {
                case AbstractModuleHandler.MODULE_INTERVAL:
                case AbstractModuleHandler.MODULE_HDR:
                case AbstractModuleHandler.MODULE_PICTURE:
                    super.SetValue("continuous-picture", setToCam);
                    break;
                case AbstractModuleHandler.MODULE_VIDEO:
                    super.SetValue("continuous-video", setToCam);
                    break;
            }
        }
        else if(valueToSet.equals("touch"))
        {
            parameters.put("selectable-zone-af", "spot-metering");
            parameters.put("focus-mode", "auto");
            try {
                baseCameraHolder.SetCameraParameters(parameters);
                super.BackgroundValueHasChanged(valueToSet);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        else
        {
            super.SetValue(valueToSet, setToCam);
        }


    }
}
