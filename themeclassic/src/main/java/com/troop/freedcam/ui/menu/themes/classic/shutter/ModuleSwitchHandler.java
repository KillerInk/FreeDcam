package com.troop.freedcam.ui.menu.themes.classic.shutter;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.i_camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.sonyapi.modules.ModuleHandlerSony;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.themes.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by troop on 20.08.2014.
 */
public class ModuleSwitchHandler implements View.OnClickListener, I_ParametersLoaded , I_ModuleEvent
{

    protected View activity;
    protected AbstractCameraUiWrapper cameraUiWrapper;
    protected AppSettingsManager appSettingsManager;
    protected AbstractModuleHandler moduleHandler;
    TextView moduleViewx;
    ListView listView;
    Fragment fragment;

    public ModuleSwitchHandler(View activity, AppSettingsManager appSettingsManager, Fragment fragment)
    {
        this.activity = activity;
        this.appSettingsManager = appSettingsManager;

        this.fragment = fragment;
        init();

    }

    protected void init()
    {
        moduleViewx = (TextView)activity.findViewById(R.id.textView_ModuleSwitch);
        moduleViewx.setOnClickListener(this);
        //moduleView.setVisibility(View.GONE);
    }


    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        if (cameraUiWrapper == null)
        {
            this.moduleHandler = null;
            return;
        }
        this.moduleHandler = cameraUiWrapper.moduleHandler;
        moduleHandler.moduleEventHandler.addListner(this);
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
    }

    //shows the popupmenu
    @Override
    public void onClick(View v)
    {
        listView = (ListView) activity.findViewById(R.id.listView_popup);
        List<String> mods = new ArrayList<String>();
        for (HashMap.Entry<String,AbstractModule> module : cameraUiWrapper.moduleHandler.moduleList.entrySet())
        {
            mods.add(module.getValue().LongName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity.getContext(),
                R.layout.simpel_list_item_v2, R.id.textView_simple_list_item_v2, mods);
        //attach adapter to the listview and fill
        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (moduleHandler == null && cameraUiWrapper == null)
                    return;
                String value = (String) listView.getItemAtPosition(position);
                for (HashMap.Entry<String,AbstractModule> module : cameraUiWrapper.moduleHandler.moduleList.entrySet())
                {
                    if (value.equals(module.getValue().LongName()))
                    {
                        appSettingsManager.SetCurrentModule(module.getValue().ModuleName());
                        moduleHandler.SetModule(module.getValue().ModuleName());

                        moduleViewx.setText(module.getValue().ShortName());
                        break;
                    }

                }

                listView.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void ParametersLoaded()
    {

        if ((moduleHandler instanceof ModuleHandlerSony) || moduleHandler == null)
            return;
        if (moduleHandler.GetCurrentModule() == null)
            moduleHandler.SetModule(appSettingsManager.GetCurrentModule());
        moduleViewx.setText(moduleHandler.GetCurrentModule().ShortName());
        moduleViewx.setVisibility(View.VISIBLE);
    }

    @Override
    public String ModuleChanged(String module) {
        moduleViewx.setText(moduleHandler.GetCurrentModule().ShortName());
        return null;
    }


}
