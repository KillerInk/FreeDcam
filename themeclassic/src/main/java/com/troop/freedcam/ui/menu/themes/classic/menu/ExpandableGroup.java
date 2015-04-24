package com.troop.freedcam.ui.menu.themes.classic.menu;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.themes.R;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.ExpandableChild;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.I_OnGroupClicked;

import java.util.ArrayList;

/**
 * Created by troop on 18.08.2014.
 */
public class ExpandableGroup extends LinearLayout implements I_ModuleEvent
{
    private String Name;
    public ArrayList<ExpandableChild> Items;
    Context context;
    public ArrayList<String> modulesToShow;
    TextView textView;
    public LinearLayout submenu;
    LinearLayout groupcontainer;
    boolean submenuVisible = false;
    OnClickListener onChildclick;
    AbstractCameraUiWrapper cameraUiWrapper;
    I_OnGroupClicked onGroupClicked;
    protected AppSettingsManager appSettingsManager;

    private static String TAG = ExpandableGroup.class.getSimpleName();

    public ExpandableGroup(Context context, LinearLayout submenu, AppSettingsManager appSettingsManager) {
        super(context);

        this.appSettingsManager = appSettingsManager;
        init(context);
    }

    private void init(Context context)
    {
        this.context =context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);



        infalteTheme(inflater);

        /*if (theme.equals("Ambient"))
            inflater.inflate(R.layout.expandable_groups_ambient, this);
        if (theme.equals("Material"))
            inflater.inflate(R.layout.expandable_groups_material, this);
        if (theme.equals("Minimal"))
            inflater.inflate(R.layout.expandable_groups_minimal, this);
        if (theme.equals("Nubia"))
            inflater.inflate(R.layout.expandable_groups_nubia, this);*/
        //inflater.inflate(R.layout.expandable_groups, this);
        this.submenu = (LinearLayout)findViewById(R.id.GroupSubMenu);
        this.textView = (TextView)findViewById(R.id.tvGroup);
        this.textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                OnGROUPCLick();
            }
        });
        this.groupcontainer = (LinearLayout)findViewById(R.id.GroupContainer);

        //groupcontainer.removeView(submenu);
        submenu.setVisibility(GONE);
        submenuVisible =false;
    }

    protected void infalteTheme(LayoutInflater inflater)
    {
        inflater.inflate(R.layout.expandable_groups, this);
    }

    public void SetOnGroupItemClickListner(I_OnGroupClicked groupclick)
    {
        this.onGroupClicked = groupclick;
    }

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        this.Name = name;
        textView.setText(Name);
    }
    public ArrayList<ExpandableChild> getItems() {
        return Items;
    }
    public void setItems(ArrayList<ExpandableChild> Items)
    {
        this.Items = Items;
        fillSubMenuItems();
    }

    public void fillSubMenuItems() {
        submenu.removeAllViews();
        for (ExpandableChild child:Items)
        {
            Log.d(TAG, child.getName());
            if(child.getParameterHolder() != null && child.getParameterHolder().IsSupported())
            {
                Log.d(TAG, child.getName() +" is supported : " + child.getParameterHolder().IsSupported());
                submenu.addView(child);
            }
        }
    }


    @Override
    public String ModuleChanged(String module)
    {
        fillSubMenuItems();
        return module;
    }

    public void setOnChildClick(OnClickListener onChildClick)
    {
        this.onChildclick = onChildClick;
        for (ExpandableChild child:Items)
        {
            child.setOnClickListener(onChildclick);
        }
    }

    public void OnGROUPCLick()
    {
        if(submenu.getVisibility() == VISIBLE)
            submenu.setVisibility(GONE);
        else
            submenu.setVisibility(VISIBLE);
    }
}
