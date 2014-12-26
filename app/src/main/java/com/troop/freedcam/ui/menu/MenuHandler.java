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

    public MenuHandler(MainActivity_v2 context, AppSettingsManager appSettingsManager)
    {
        this.context = context;

        this.appSettingsManager = appSettingsManager;



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

    public void SetCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper, SurfaceView surfaceView)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.surfaceView = surfaceView;
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
        menuCreator = new MenuCreator(context, cameraUiWrapper, appSettingsManager);
        fillMenu();
    }


    private ArrayList<ExpandableGroup> createMenu() {
        ArrayList<ExpandableGroup> grouplist = new ArrayList<ExpandableGroup>();
        grouplist.add(menuCreator.CreateSettings());
        grouplist.add(menuCreator.CreateModeSettings());
        grouplist.add(menuCreator.CreateQualitySettings());
        previewSettings = menuCreator.CreatePreviewSettings(surfaceView);
        grouplist.add(previewSettings);
        if (appSettingsManager.GetCurrentModule().equals(ModuleHandler.MODULE_LONGEXPO))
        {

        }
        picSettings = menuCreator.CreatePictureSettings(surfaceView);
        grouplist.add(picSettings);
        if (appSettingsManager.GetCurrentModule().equals(ModuleHandler.MODULE_PICTURE))
        {

        }
        videoSettings = menuCreator.CreateVideoSettings(surfaceView);
        grouplist.add(videoSettings);
        //if (appSettingsManager.GetCurrentModule().equals(ModuleHandler.MODULE_VIDEO))

        return grouplist;
    }

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
        fillMainMenu();
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
            fillMainMenu();
            for(ExpandableGroup g : grouplist)
                g.ModuleChanged(module);
            //ParametersLoaded();
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
