package com.troop.freedcam.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.util.Size;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;

import java.util.ArrayList;

/**
 * Created by troop on 12.12.2014.
 */
public class PictureFormatParameterApi2 extends BaseModeApi2
{
    BaseCameraHolderApi2 cameraHolder;
    public PictureFormatParameterApi2(BaseCameraHolderApi2 baseCameraHolderApi2)
    {
        super(baseCameraHolderApi2);
        this.cameraHolder = baseCameraHolderApi2;
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.equals("jpeg"))
        {

        }
        else if (valueToSet.equals("raw10"))
        {

        }
        else if (valueToSet.equals("raw_sensor"))
        {

        }

    }

    @Override
    public String GetValue() {
        return super.GetValue();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public String[] GetValues()
    {
        boolean raw10 = cameraHolder.map.isOutputSupportedFor(ImageFormat.RAW10);
        boolean raw_sensor = cameraHolder.map.isOutputSupportedFor(ImageFormat.RAW_SENSOR);
        int [] values = cameraHolder.characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
        ArrayList<String> ret = new ArrayList<String>();
        for (int i =0; i < values.length; i++)
        {
            switch (values[i])
            {
                case ImageFormat.JPEG:
                    ret.add("jpeg");
                    break;
                case ImageFormat.RAW10:
                    ret.add("raw10");
                    break;
                case ImageFormat.RAW_SENSOR:
                    ret.add("raw_sensor");
                    break;
                default:
                break;
            }
        }
        return ret.toArray(new String[ret.size()]);
    }
}
