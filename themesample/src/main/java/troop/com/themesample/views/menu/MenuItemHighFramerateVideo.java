package troop.com.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;

import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.ui.AppSettingsManager;

import java.util.ArrayList;

/**
 * Created by GeorgeKiarie on 9/22/2015.
 */
public class MenuItemHighFramerateVideo extends MenuItem
{

    protected ArrayList<String> modulesToShow;
    String currentModule;
    AbstractModuleHandler moduleHandler;
    public MenuItemHighFramerateVideo(Context context) {
        super(context);
    }

    public MenuItemHighFramerateVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void SetModulesToShow(ArrayList<String> modulesToShow, AbstractModuleHandler moduleHandler)
    {
        this.modulesToShow = modulesToShow;
        this.moduleHandler = moduleHandler;
    }

    @Override
    public void SetValue(String value)
    {
        if (parameter != null && parameter.IsSupported())
        {
            if (settingsname != null && !settingsname.equals(""))
                AppSettingsManager.APPSETTINGSMANAGER.setString(settingsname, value);
            if (modulesToShow.contains(moduleHandler.GetCurrentModuleName()))
                parameter.SetValue(value, true);
            onValueChanged(value);
        }
    }

    @Override
    public String ModuleChanged(String module) {
        this.currentModule = module;
        return  null;
    }

    @Override
    public void onValueChanged(String val)
    {
        if (val.contains("@")||val.equals("Default")) {
            sendLog("Set Value to:" + val);
            valueText.setText(val);
        }
    }
}