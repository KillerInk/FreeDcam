package troop.com.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;

import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GeorgeKiarie on 1/29/2016.
 */
public class MenuItemVideoBitrate extends MenuItem {


    private List<String> Bitrates;

    public MenuItemVideoBitrate(Context context) {
        super(context);
    }



    public MenuItemVideoBitrate(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void SetValue(String value)
    {
        AppSettingsManager.APPSETTINGSMANAGER.setString(AppSettingsManager.SETTING_VideoBitrate, value);
        onValueChanged(value);
    }



    @Override
    public String[] GetValues()
    {
        return Bitrates.toArray(new String[Bitrates.size()]);
    }

    @Override
    public void SetParameter(AbstractModeParameter parameter)
    {



        if (parameter == null || !parameter.IsSupported())
        {
            onIsSupportedChanged(false);
            sendLog("Paramters is null or Unsupported");
            return;
        }

        Bitrates = new ArrayList<>();
        Bitrates.add("Default");
        Bitrates.add("200Mbps");
        Bitrates.add("150Mbps");
        Bitrates.add("100Mbps");
        Bitrates.add("80Mbps");
        Bitrates.add("60Mbps");
        Bitrates.add("50Mbps");
        Bitrates.add("40Mbps");
        Bitrates.add("30Mbps");
        Bitrates.add("10Mbps");
        Bitrates.add("5Mbps");
        Bitrates.add("2Mbps");

        if (Bitrates.size() > 0)
            onIsSupportedChanged(true);
        else
            onIsSupportedChanged(false);

        this.parameter = parameter;

        onValueChanged(AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_VideoBitrate));
    }
}