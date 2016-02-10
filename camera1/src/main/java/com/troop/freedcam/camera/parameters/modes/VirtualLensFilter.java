package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by GeorgeKiarie on 9/24/2015.
 */
public class VirtualLensFilter extends  BaseModeParameter {

    BaseCameraHolder cameraHolder;
    CameraUiWrapper cameraUiWrapper;

    public static final int[] asT = new int[]{0, 1, 2, 3, 4, 5, 6};
    public static final String[] asU = new String[]{"", "100 0 0 0 100 0 0 0 100 0 0 80", "100 0 0 0 100 0 0 0 100 12 50 100",
            "100 0 0 0 100 0 0 0 100 0 100 100", "100 0 0 0 100 0 0 0 100 0 85 0", "100 0 0 0 100 0 0 0 100 80 80 0"
            , "100 0 0 0 100 0 0 0 100 80 0 0", "100 0 0 0 100 0 0 0 50 115 20 70", "100 0 0 0 100 0 0 0 40 -60 -60 -60"
            , "100 0 0 0 100 0 0 0 40 -60 -60 -60", "100 0 0 0 100 0 0 0 40 -60 -60 -60", "100 0 0 0 100 0 0 0 40 -60 -60 -60"};
    public VirtualLensFilter(Handler handler, HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values, CameraUiWrapper cameraUiWrapper)
    {
        super(handler, parameters, parameterChanged, value, values);

        if (DeviceUtils.IS(DeviceUtils.Devices.ZTE_ADV))
            this.isSupported = true;
        this.cameraHolder = parameterChanged;
        this.cameraUiWrapper = cameraUiWrapper;

    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public String[] GetValues()
    {
        return new String[]{"Off","Red","Orange","Yellow","Green","Cyan","Blue","Purple","Grad Left","Grad Right","Grad Top","Grad Bottom"};
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {



        switch (valueToSet)
        {
            case "Off":
                parameters.put("color-filter-type", ""+asT[0]);
                break;
            case "Red":
                parameters.put("color-filter-type", ""+asT[1]);
                parameters.put("color-filter-param", asU[1]);
                break;

            case "Orange":
                parameters.put("color-filter-type", ""+asT[1]);
                parameters.put("color-filter-param", asU[2]);
                break;
            case "Yellow":
                parameters.put("color-filter-type", ""+asT[1]);
                parameters.put("color-filter-param", asU[3]);
                break;
            case "Green":
                parameters.put("color-filter-type", ""+asT[1]);
                parameters.put("color-filter-param", asU[4]);
                break;
            case "Cyan":
                parameters.put("color-filter-type", ""+asT[1]);
                parameters.put("color-filter-param", asU[5]);
                break;
            case "Blue":
                parameters.put("color-filter-type", ""+asT[1]);
                parameters.put("color-filter-param", asU[6]);
                break;
            case "Purple":
                parameters.put("color-filter-type", ""+asT[1]);
                parameters.put("color-filter-param", asU[7]);
                break;
            case "Grad Left":
                parameters.put("color-filter-type", ""+asT[4]);
                parameters.put("color-filter-param", asU[8]);
                break;
            case "Grad Right":
                parameters.put("color-filter-type", ""+asT[3]);
                parameters.put("color-filter-param", asU[9]);
                break;
            case "Grad Top":
                parameters.put("color-filter-type", ""+asT[5]);
                parameters.put("color-filter-param", asU[10]);
                break;
            case "Grad Bottom":
                parameters.put("color-filter-type", ""+asT[6]);
                parameters.put("color-filter-param", asU[11]);
                break;


        }
        baseCameraHolder.SetCameraParameters(parameters);
    }

    @Override
    public String GetValue()
    {
        return "Off";
    }
}