package com.troop.freedcam.ui.menu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.modules.I_ModuleEvent;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.MainActivity_v2;
import com.troop.freedcam.ui.TextureView.ExtendedSurfaceView;
import com.troop.freedcam.ui.TextureView.PreviewHandler;
import com.troop.freedcam.ui.menu.childs.ExpandableChild;
import com.troop.freedcam.ui.menu.childs.SaveCamParasExpandableChild;

import java.util.ArrayList;

/**
 * Created by troop on 19.08.2014.
 */
public class MenuHandler  implements ExpandableListView.OnChildClickListener, ListView.OnItemClickListener, I_ParametersLoaded, I_ModuleEvent
{
    MainActivity_v2 context;
    CameraUiWrapper cameraUiWrapper;
    MenuCreator menuCreator;
    PreviewHandler previewHandler;
    ArrayList<ExpandableGroup> grouplist;
    ExpandableGroup picSettings;
    ExpandableGroup previewSettings;
    ExpandableGroup videoSettings;

    /**
     * this holds the mainmenu
     */
    ExpandableListView expandableListView;
    ExpandableListViewMenuAdapter expandableListViewMenuAdapter;
    /**
     * this hold the main submenu
     */
    ListView listView;

    int mShortAnimationDuration = 200;

    ExpandableChild selectedChild;
    AppSettingsManager appSettingsManager;

    public MenuHandler(MainActivity_v2 context, CameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager, PreviewHandler previewHandler)
    {
        this.context = context;
        this.cameraUiWrapper = cameraUiWrapper;
        this.appSettingsManager = appSettingsManager;
        this.previewHandler = previewHandler;
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
        menuCreator = new MenuCreator(context, cameraUiWrapper, appSettingsManager);

    }

    private ArrayList<ExpandableGroup> createMenu() {
        ArrayList<ExpandableGroup> grouplist = new ArrayList<ExpandableGroup>();
        grouplist.add(menuCreator.CreateModeSettings());
        grouplist.add(menuCreator.CreateQualitySettings());
        previewSettings = menuCreator.CreatePreviewSettings(previewHandler);
        if (appSettingsManager.GetCurrentModule().equals(ModuleHandler.MODULE_LONGEXPO))
        {
            grouplist.add(previewSettings);
        }
        picSettings = menuCreator.CreatePictureSettings(previewHandler);
        if (appSettingsManager.GetCurrentModule().equals(ModuleHandler.MODULE_PICTURE))
        {
            grouplist.add(picSettings);
        }
        videoSettings = menuCreator.CreateVideoSettings(previewHandler);
        if (appSettingsManager.GetCurrentModule().equals(ModuleHandler.MODULE_VIDEO))
            grouplist.add(videoSettings);
        return grouplist;
    }

    //Expendable LIstview click
    @Override
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
            if (selectedChild.getName().equals(context.getString(R.string.picture_format))) {
                if (cameraUiWrapper.camParametersHandler.dngSupported && cameraUiWrapper.camParametersHandler.rawSupported)
                    values = new String[]{"jpeg", "raw", "dng"};
                else
                    values = new String[]{"jpeg", "raw"};
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
    }


    private void hideMenuAndShowSubMenu()
    {
        context.settingsLayoutHolder.removeView(expandableListView);
        context.settingsLayoutHolder.addView(listView);
       /* expandableListView.setAlpha(1f);
        expandableListView.setVisibility(View.VISIBLE);
        expandableListView.animate()
                .alpha(0f)
                .translationXBy(-expandableListView.getWidth())
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        expandableListView.setVisibility(View.GONE);
                        showSubMenu();
                    }
                });*/
    }

    private void showSubMenu()
    {
        listView.setAlpha(0f);
        listView.setVisibility(View.VISIBLE);
        listView.animate()
                .alpha(1f)
                .translationX(0)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {

                    }
                });
    }

    private void hideSubMenuAndShowMenu()
    {
        context.settingsLayoutHolder.removeView(listView);
        context.settingsLayoutHolder.addView(expandableListView);
        /*listView.setAlpha(1f);
        listView.setVisibility(View.VISIBLE);
        listView.animate()
                .alpha(0f)
                .translationX(-expandableListView.getWidth())
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        listView.setVisibility(View.GONE);
                        showMenu();
                    }
                });*/
    }

    private void showMenu()
    {
        expandableListView.setAlpha(0f);
        expandableListView.setVisibility(View.VISIBLE);
        expandableListView.animate()
                .alpha(1f)
                .translationX(0)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }
                });
    }

    //this get fired when the cameraparametershandler has finished loading the parameters and all values are availible
    @Override
    public void ParametersLoaded()
    {
        grouplist = createMenu();
        expandableListViewMenuAdapter = new ExpandableListViewMenuAdapter(context, grouplist);
        expandableListView = (ExpandableListView) context.settingsLayoutHolder.findViewById(R.id.expandableListViewSettings);
        expandableListView.setAdapter(expandableListViewMenuAdapter);
        expandableListView.setOnChildClickListener(this);

        if (listView == null) {
            listView = (ListView) context.settingsLayoutHolder.findViewById(R.id.subMenuSettings);
            listView.setOnItemClickListener(this);
            context.settingsLayoutHolder.removeView(listView);
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
        if (grouplist != null && expandableListView != null) {
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
            expandableListViewMenuAdapter = new ExpandableListViewMenuAdapter(context, grouplist);
            expandableListView.setAdapter(expandableListViewMenuAdapter);
        }
        return null;
    }
}
