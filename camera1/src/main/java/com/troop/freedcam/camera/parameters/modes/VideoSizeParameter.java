package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;
import android.util.Log;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.utils.StringUtils;

import java.util.HashMap;

/**
 * Created by troop on 13.11.2014.
 */
public class VideoSizeParameter extends BaseModeParameter
{
    String[] sizeString;
    public final String UHDSIZE = "3840x2160";
    private static String TAG = StringUtils.TAG + VideoSizeParameter.class.getSimpleName();

    public VideoSizeParameter(Handler handler,HashMap<String,String> parameters, BaseCameraHolder parameterChanged, String value, String values)
    {
        super(handler,parameters, parameterChanged, value, values);
        String[] sizes = null;
        try {
            sizes = parameters.get("video-size-values").split(",");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        if (sizes == null || sizes.length == 0)
        {
            Logger.d(TAG, "Couldnt finde Video Size Values loading Preview Size Values");
            try {
                sizes = parameters.get("preview-size-values").split(",");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        if (sizes == null || sizes.length == 0)
            this.isSupported = false;
        else
            this.isSupported = true;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        super.SetValue(valueToSet, setToCam);
    }

    @Override
    public String GetValue() {
        return super.GetValue();
    }

    @Override
    public String[] GetValues() {
        return sizeString;
    }

    @Override
    public boolean IsSupported() {
        return this.isSupported;
    }
}
