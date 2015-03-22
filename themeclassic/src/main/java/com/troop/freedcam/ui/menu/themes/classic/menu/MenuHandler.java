package com.troop.freedcam.ui.menu.themes.classic.menu;

import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
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
import com.troop.freedcam.ui.menu.themes.R;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.ExpandableChild;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.I_OnGroupClicked;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.SaveCamParasExpandableChild;

import java.util.ArrayList;

/**
 * Created by troop on 19.08.2014.
 */
public class MenuHandler  implements ListView.OnItemClickListener, TextureView.OnClickListener, I_ParametersLoaded, I_ModuleEvent, I_OnGroupClicked
{
    MenuFragment context;
    AbstractCameraUiWrapper cameraUiWrapper;
    MenuCreator menuCreator;
    SurfaceView surfaceView;
    ArrayList<ExpandableGroup> grouplist;
    ExpandableGroup picSettings;
    ExpandableGroup previewSettings;
    ExpandableGroup videoSettings;
    boolean childsSubmenuVisible = false;
    ExpandableGroup lastGroupView;
    //public ScrollView scrollView;

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

    public MenuHandler(MenuFragment context, AppSettingsManager appSettingsManager)
    {
        this.context = context;
        this.appSettingsManager = appSettingsManager;
        mainMenuView = (LinearLayout) context.settingsLayoutHolder.findViewById(R.id.expandableListViewSettings);
        listView = (ListView) context.settingsLayoutHolder.findViewById(R.id.subMenuSettings);
        listView.setOnItemClickListener(this);
        menuCreator = new MenuCreator(context, appSettingsManager);
        //scrollView = (ScrollView)context.settingsLayoutHolder.findViewById(R.id.scrollView_ExpandAbleListView);
        context.settingsLayoutHolder.removeView(listView);
    }

    public void SetCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper, SurfaceView surfaceView)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.surfaceView = surfaceView;
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);

        fillMenu();
        menuCreator.setCameraUiWrapper(cameraUiWrapper);
        ModuleChanged(cameraUiWrapper.moduleHandler.GetCurrentModuleName());

    }


    private ArrayList<ExpandableGroup> createMenu() {
        ArrayList<ExpandableGroup> grouplist = new ArrayList<ExpandableGroup>();
        grouplist.add(menuCreator.CreateSettings());
        grouplist.add(menuCreator.CreateModeSettings());
        grouplist.add(menuCreator.CreateQualitySettings());
        previewSettings = menuCreator.CreatePreviewSettings(surfaceView);
        grouplist.add(previewSettings);

        picSettings = menuCreator.CreatePictureSettings(surfaceView);
        grouplist.add(picSettings);

        videoSettings = menuCreator.CreateVideoSettings(surfaceView);
        grouplist.add(videoSettings);
        for(ExpandableGroup g : grouplist)
        {
            g.SetOnGroupItemClickListner(this);
        }
        //if (appSettingsManager.GetCurrentModule().equals(ModuleHandler.MODULE_VIDEO))


        return grouplist;
    }

    private String[] getPictureFormats() {
        String[] values;

            values = cameraUiWrapper.camParametersHandler.PictureFormat.GetValues();
        return values;
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



    //this get fired when the cameraparametershandler has finished loading the parameters and all values are availible
    @Override
    public void ParametersLoaded()
    {
        /*appSettingsManager.context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fillMenu();
            }
        });*/
        //SetCameraUiWrapper(cameraUiWrapper);
        menuCreator.setCameraUiWrapper(cameraUiWrapper);
    }

    private void fillMenu()
    {
        grouplist = createMenu();

    }

    private void fillMainMenu() {
        mainMenuView.removeAllViews();
        for (ExpandableGroup g:grouplist)
        {
            g.setOnChildClick(this);
            mainMenuView.addView(g);
        }
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
        if (grouplist != null && mainMenuView != null) {
            if (module.equals(AbstractModuleHandler.MODULE_LONGEXPO)) {
                if (grouplist.contains(picSettings))
                    grouplist.remove(picSettings);
                if (grouplist.contains(videoSettings))
                    grouplist.remove(videoSettings);
                if (!grouplist.contains(previewSettings))
                    grouplist.add(previewSettings);
            }
            if (module.equals(AbstractModuleHandler.MODULE_PICTURE) || module.equals(AbstractModuleHandler.MODULE_HDR))
            {
                if (!grouplist.contains(picSettings))
                    grouplist.add(picSettings);
                if (grouplist.contains(previewSettings))
                    grouplist.remove(previewSettings);
                if (grouplist.contains(videoSettings))
                    grouplist.remove(videoSettings);
            }
            if (module.equals(AbstractModuleHandler.MODULE_VIDEO))
            {
                if (grouplist.contains(picSettings))
                    grouplist.remove(picSettings);
                if (grouplist.contains(previewSettings))
                    grouplist.remove(previewSettings);
                if (!grouplist.contains(videoSettings))
                    grouplist.add(videoSettings);
            }
            fillMainMenu();
            for(ExpandableGroup g : grouplist)
                g.ModuleChanged(module);
            picSettings.submenu.removeAllViews();
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
            if (selectedChild.getName().equals(context.getString(R.string.picture_format)))
            {
                values = getPictureFormats();
            }

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
    public void onGroupClicked(ExpandableGroup group) {
        if (lastGroupView != null && lastGroupView == group) {
            group.submenu.removeAllViews();
            lastGroupView = null;
        }
        else
        {
            if (childsSubmenuVisible)
                hideChildsSubMenu();
            group.fillSubMenuItems();
            lastGroupView = group;
        }
    }
}
