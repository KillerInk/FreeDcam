package troop.com.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 19.07.2015.
 */
public class MenuItemBayerFormat extends MenuItem
{
    public static String APPSETTING_BAYERFORMAT = "APPSETTING_BAYERFORMAT";

    private List<String> bayerformats;

    public MenuItemBayerFormat(Context context) {
        super(context);
    }



    public MenuItemBayerFormat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void SetValue(String value)
    {
        appSettingsManager.setString(APPSETTING_BAYERFORMAT, value);
        if (!appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT).toLowerCase().equals("jpeg"))
        {
            appSettingsManager.setString(AppSettingsManager.SETTING_PICTUREFORMAT, value);
            parameter.SetValue(value, false);
        }
    }

    @Override
    public String[] GetValues()
    {
        return bayerformats.toArray(new String[bayerformats.size()]);
    }

    @Override
    public void SetParameter(AbstractModeParameter parameter)
    {
        if (parameter == null || !parameter.IsSupported())
        {
            onIsSupportedChanged(false);
            Log.d(TAG, "Paramters is null or Unsupported");
            return;
        }

        bayerformats = new ArrayList<String>();
        for(String s : parameter.GetValues())
        {
            if (s.contains("bayer"))
            {
                bayerformats.add(s);
            }
        }
        if (bayerformats.size() > 0)
            onIsSupportedChanged(true);
        else
            onIsSupportedChanged(false);
        this.parameter = parameter;
        if (parameter != null)
            parameter.addEventListner(this);

        if (appSettingsManager.getString(APPSETTING_BAYERFORMAT).equals("") && bayerformats.size() >0) {
            appSettingsManager.setString(APPSETTING_BAYERFORMAT, bayerformats.get(0));
        }
        onValueChanged(appSettingsManager.getString(APPSETTING_BAYERFORMAT));
    }
}
