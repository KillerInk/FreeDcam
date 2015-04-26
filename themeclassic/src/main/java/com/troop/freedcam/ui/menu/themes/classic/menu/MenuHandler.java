package com.troop.freedcam.ui.menu.themes.classic.menu;

import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.i_camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.menu.themes.R;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.ExpandableChild;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.I_OnGroupClicked;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.SaveCamParasExpandableChild;

import java.util.ArrayList;

/**
 * Created by troop on 19.08.2014.
 */
public class MenuHandler  implements ListView.OnItemClickListener, TextureView.OnClickListener, I_ModuleEvent, I_OnGroupClicked
{
    MenuFragment context;
    AbstractCameraUiWrapper cameraUiWrapper;
    public MenuCreator menuCreator;
    SurfaceView surfaceView;
    boolean childsSubmenuVisible = false;
    ExpandableGroup settings;
    ExpandableGroup modes;
    ExpandableGroup quality;
    ExpandableGroup longexpo;
    ExpandableGroup picSettings;
    ExpandableGroup videoSettings;

    /**
     * this holds the mainmenu
     */
    public LinearLayout mainMenuView;
    /**
     * this hold the main submenu
     */
    public ListView listView;

    int mShortAnimationDuration = 200;

    ExpandableChild selectedChild;
    AppSettingsManager appSettingsManager;

    public MenuHandler(MenuFragment context, AppSettingsManager appSettingsManager, I_Activity activity)
    {
        this.context = context;
        this.appSettingsManager = appSettingsManager;

        loadMenuCreator(context, appSettingsManager, activity);
    }

    public void loadMenuCreator(MenuFragment context, AppSettingsManager appSettingsManager, I_Activity activity) {
        menuCreator = new MenuCreator(context, appSettingsManager,activity);
    }

    public void INIT()
    {
        mainMenuView = (LinearLayout) context.settingsLayoutHolder.findViewById(R.id.expandableListViewSettings);
        listView = (ListView) context.settingsLayoutHolder.findViewById(R.id.subMenuSettings);
        listView.setOnItemClickListener(this);
        context.settingsLayoutHolder.removeView(listView);
        if (settings.getParent() == null)
        {
            mainMenuView.addView(settings);
            mainMenuView.addView(modes);
            mainMenuView.addView(quality);
            mainMenuView.addView(picSettings);
            mainMenuView.addView(videoSettings);
            mainMenuView.addView(longexpo);
        }
        ModuleChanged(cameraUiWrapper.moduleHandler.GetCurrentModuleName());


    }

    public void CLEARPARENT()
    {
        mainMenuView.removeAllViews();
    }

    public void SetCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper, SurfaceView surfaceView)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.surfaceView = surfaceView;
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
        settings = menuCreator.CreateSettings();
        settings.setOnChildClick(this);
        modes =menuCreator.CreateModeSettings();
        modes.setOnChildClick(this);
        quality =menuCreator.CreateQualitySettings();
        quality.setOnChildClick(this);
        longexpo = menuCreator.CreatePreviewSettings(surfaceView);
        longexpo.setOnChildClick(this);
        picSettings = menuCreator.CreatePictureSettings(surfaceView);
        picSettings.setOnChildClick(this);
        videoSettings = menuCreator.CreateVideoSettings(surfaceView);
        videoSettings.setOnChildClick(this);
        menuCreator.setCameraUiWrapper(cameraUiWrapper);

    }

    private void showChildsSubMenu()
    {
        //context.settingsLayoutHolder.removeView(scrollView);
        childsSubmenuVisible = true;
        context.settingsLayoutHolder.addView(listView);
    }

    private void hideChildsSubMenu()
    {
        childsSubmenuVisible = false;
        context.settingsLayoutHolder.removeView(listView);
        //context.settingsLayoutHolder.addView(scrollView);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (selectedChild != null)
        {
            String value = (String) listView.getItemAtPosition(position);
            selectedChild.setValue(value);
            selectedChild = null;
        }
        if (childsSubmenuVisible)
            hideChildsSubMenu();
        else
            showChildsSubMenu();

    }


    @Override
    public String ModuleChanged(String module)
    {
        if (module == null || module.equals(""))
            module = AbstractModuleHandler.MODULE_PICTURE;
        if (mainMenuView != null) {
            if (module.equals(AbstractModuleHandler.MODULE_LONGEXPO))
            {
                picSettings.setVisibility(View.GONE);
                videoSettings.setVisibility(View.GONE);
                longexpo.setVisibility(View.VISIBLE);
            }
            if (module.equals(AbstractModuleHandler.MODULE_PICTURE) || module.equals(AbstractModuleHandler.MODULE_HDR))
            {
                picSettings.setVisibility(View.VISIBLE);
                longexpo.setVisibility(View.GONE);
                videoSettings.setVisibility(View.GONE);
            }
            if (module.equals(AbstractModuleHandler.MODULE_VIDEO))
            {
                picSettings.setVisibility(View.GONE);
                longexpo.setVisibility(View.GONE);
                videoSettings.setVisibility(View.VISIBLE);
            }
            settings.ModuleChanged(module);
            picSettings.ModuleChanged(module);
            videoSettings.ModuleChanged(module);
            longexpo.ModuleChanged(module);
            quality.ModuleChanged(module);
            modes.ModuleChanged(module);
            //picSettings.submenu.removeAllViews();
        }
        return null;
    }

    //OnChildClick
    @Override
    public void onClick(View v)
    {
        selectedChild = (ExpandableChild)v;
        if (!(selectedChild instanceof SaveCamParasExpandableChild))
        {
            //get values from child attached parameter
            String[] values = selectedChild.getParameterHolder().GetValues();
            //set values to the adapter
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context.getActivity().getApplicationContext(),
                    R.layout.simpel_list_item_v2, R.id.textView_simple_list_item_v2, values);
            //attach adapter to the listview and fill
            listView.setAdapter(adapter);
            if (childsSubmenuVisible)
                hideChildsSubMenu();
            else
                showChildsSubMenu();
        }
        else if (selectedChild instanceof  SaveCamParasExpandableChild)
        {
            SaveCamParasExpandableChild child = (SaveCamParasExpandableChild) selectedChild;
            child.SaveCamParameters();
            Toast.makeText(context.getActivity().getApplicationContext(), "Camera Parameters saved to DCIM/FreeCam/CamParameters.txt", Toast.LENGTH_LONG).show();
        }

    }

    View.OnClickListener onGroupListner = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {

        }
    };

    @Override
    public void onGroupClicked(ExpandableGroup group)
    {
        /*if (lastGroupView != null && lastGroupView == group) {
            group.submenu.removeAllViews();
            lastGroupView = null;
        }
        else
        {
            if (childsSubmenuVisible)
                hideChildsSubMenu();
            group.fillSubMenuItems();
            lastGroupView = group;
        }*/
    }
}
