package com.troop.freedcam.ui.menu;

import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.modules.I_ModuleEvent;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.MainActivity_v2;
import com.troop.freedcam.ui.menu.childs.ExpandableChild;
import com.troop.freedcam.ui.menu.childs.SaveCamParasExpandableChild;

import java.util.ArrayList;

/**
 * Created by troop on 19.08.2014.
 */
public class MenuHandler  implements ListView.OnItemClickListener, TextureView.OnClickListener, I_ParametersLoaded, I_ModuleEvent
{
    MainActivity_v2 context;
    AbstractCameraUiWrapper cameraUiWrapper;
    MenuCreator menuCreator;
    SurfaceView surfaceView;
    ArrayList<ExpandableGroup> grouplist;
    ExpandableGroup picSettings;
    ExpandableGroup previewSettings;
    ExpandableGroup videoSettings;
    public ScrollView scrollView;

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

    public MenuHandler(MainActivity_v2 context, AbstractCameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager, SurfaceView surfaceView)
    {
        this.context = context;
        this.cameraUiWrapper = cameraUiWrapper;
        this.appSettingsManager = appSettingsManager;
        this.surfaceView = surfaceView;
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
        menuCreator = new MenuCreator(context, cameraUiWrapper, appSettingsManager);
        try
        {
            mainMenuView = (LinearLayout) context.settingsLayoutHolder.findViewById(R.id.expandableListViewSettings);


            listView = (ListView) context.settingsLayoutHolder.findViewById(R.id.subMenuSettings);
            listView.setOnItemClickListener(this);
            scrollView = (ScrollView)context.settingsLayoutHolder.findViewById(R.id.scrollView_ExpandAbleListView);
            context.settingsLayoutHolder.removeView(listView);
        }
        catch (Exception ex)
        {

        }


    }

    public MenuHandler(MainActivity_v2 context, AbstractCameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager, SurfaceView surfaceView, ScrollView scrollView,
                       LinearLayout mainMenuView, ListView listView)
    {
        this(context, cameraUiWrapper, appSettingsManager, surfaceView);
        this.mainMenuView = mainMenuView;


        this.listView = listView;
        listView.setOnItemClickListener(this);
        this.scrollView = scrollView;
        context.settingsLayoutHolder.removeView(listView);

    }

    private ArrayList<ExpandableGroup> createMenu() {
        ArrayList<ExpandableGroup> grouplist = new ArrayList<ExpandableGroup>();
        grouplist.add(menuCreator.CreateSettings());
        grouplist.add(menuCreator.CreateModeSettings());
        grouplist.add(menuCreator.CreateQualitySettings());
        previewSettings = menuCreator.CreatePreviewSettings(surfaceView);
        if (appSettingsManager.GetCurrentModule().equals(ModuleHandler.MODULE_LONGEXPO))
        {
            grouplist.add(previewSettings);
        }
        picSettings = menuCreator.CreatePictureSettings(surfaceView);
        if (appSettingsManager.GetCurrentModule().equals(ModuleHandler.MODULE_PICTURE))
        {
            grouplist.add(picSettings);
        }
        videoSettings = menuCreator.CreateVideoSettings(surfaceView);
        if (appSettingsManager.GetCurrentModule().equals(ModuleHandler.MODULE_VIDEO))
            grouplist.add(videoSettings);
        return grouplist;
    }

    //Expendable LIstview click
    /*@Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
    {
        //get the group
        ExpandableGroup group = (ExpandableGroup)expandableListViewMenuAdapter.getGroup(groupPosition);
        //get the child from group
        selectedChild = group.getItems().get(childPosition);
        if (!(selectedChild instanceof SaveCamParasExpandableChild))
        {
            //get values from child attached parameter
            String[] values = selectedChild.getParameterHolder().GetValues();
            if (selectedChild.getName().equals(context.getString(R.string.picture_format)))
            {
                values = getPictureFormats();
            }

            //set values to the adapter
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                    R.layout.simpel_list_item_v2, R.id.textView_simple_list_item_v2, values);
            //attach adapter to the listview and fill
            listView.setAdapter(adapter);
            hideMenuAndShowSubMenu();
        }
        else
        {
            SaveCamParasExpandableChild child = (SaveCamParasExpandableChild) selectedChild;
            child.SaveCamParameters();
            Toast.makeText(context, "Camera Parameters saved to DCIM/FreeCam/CamParameters.txt", Toast.LENGTH_LONG).show();
        }
        return false;
    }*/

    private String[] getPictureFormats() {
        String[] values;

            values = cameraUiWrapper.camParametersHandler.PictureFormat.GetValues();
        return values;
    }


    private void hideMenuAndShowSubMenu()
    {
        context.settingsLayoutHolder.removeView(scrollView);

        context.settingsLayoutHolder.addView(listView);
    }

    private void hideSubMenuAndShowMenu()
    {
        context.settingsLayoutHolder.removeView(listView);
        context.settingsLayoutHolder.addView(scrollView);
    }



    //this get fired when the cameraparametershandler has finished loading the parameters and all values are availible
    @Override
    public void ParametersLoaded()
    {
        appSettingsManager.context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fillMenu();
            }
        });
        //fillMenu();


    }

    private void fillMenu()
    {

        grouplist = createMenu();
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
            hideSubMenuAndShowMenu();
        }

    }


    @Override
    public String ModuleChanged(String module)
    {
        if (grouplist != null && mainMenuView != null) {
            if (module.equals(ModuleHandler.MODULE_LONGEXPO)) {
                if (grouplist.contains(picSettings))
                    grouplist.remove(picSettings);
                if (grouplist.contains(videoSettings))
                    grouplist.remove(videoSettings);
                if (!grouplist.contains(previewSettings))
                    grouplist.add(previewSettings);
            }
            if (module.equals(ModuleHandler.MODULE_PICTURE) || module.equals(ModuleHandler.MODULE_HDR))
            {
                if (!grouplist.contains(picSettings))
                    grouplist.add(picSettings);
                if (grouplist.contains(previewSettings))
                    grouplist.remove(previewSettings);
                if (grouplist.contains(videoSettings))
                    grouplist.remove(videoSettings);
            }
            if (module.equals(ModuleHandler.MODULE_VIDEO))
            {
                if (grouplist.contains(picSettings))
                    grouplist.remove(picSettings);
                if (grouplist.contains(previewSettings))
                    grouplist.remove(previewSettings);
                if (!grouplist.contains(videoSettings))
                    grouplist.add(videoSettings);
            }
            ParametersLoaded();
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
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                    R.layout.simpel_list_item_v2, R.id.textView_simple_list_item_v2, values);
            //attach adapter to the listview and fill
            listView.setAdapter(adapter);
            hideMenuAndShowSubMenu();
        }
        else if (selectedChild instanceof  SaveCamParasExpandableChild)
        {
            SaveCamParasExpandableChild child = (SaveCamParasExpandableChild) selectedChild;
            child.SaveCamParameters();
            Toast.makeText(context, "Camera Parameters saved to DCIM/FreeCam/CamParameters.txt", Toast.LENGTH_LONG).show();
        }

    }
}
