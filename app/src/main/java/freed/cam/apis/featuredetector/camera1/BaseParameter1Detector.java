package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import java.util.ArrayList;

import freed.FreedApplication;
import freed.cam.apis.featuredetector.BaseParameterDetector;
import freed.settings.mode.SettingMode;
import freed.utils.Log;

public abstract class BaseParameter1Detector extends BaseParameterDetector<Camera.Parameters> {

    protected void detectMode(Camera.Parameters parameters, int key, int keyvalues, SettingMode mode)
    {
        if (parameters.get(camstring(keyvalues)) == null || parameters.get(camstring(key)) == null)
        {
            mode.setIsSupported(false);
            return;
        }
        try {
            mode.setValues(parameters.get(camstring(keyvalues)).split(","));
            mode.setCamera1ParameterKEY(camstring(key));
            mode.set(parameters.get(mode.getCamera1ParameterKEY()));

            if (mode.getValues().length > 0)
                mode.setIsSupported(true);
        }
        catch (NumberFormatException ex)
        {
            Log.WriteEx(ex);
            mode.setIsSupported(false);

        }
        catch(ArrayIndexOutOfBoundsException ex)
        {
            Log.WriteEx(ex);
            mode.setIsSupported(false);
        }

    }

    protected String camstring(int id)
    {
        return FreedApplication.getStringFromRessources(id);
    }

    protected String[] createStringArray(int min, int max, float step)
    {
        ArrayList<String> ar = new ArrayList<>();
        if (step == 0)
            step = 1;
        for (int i = min; i <= max; i+=step)
        {
            ar.add(i+"");
        }
        return ar.toArray(new String[ar.size()]);
    }
}
