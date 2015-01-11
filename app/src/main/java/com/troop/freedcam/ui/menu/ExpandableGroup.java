package com.troop.freedcam.ui.menu;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.modules.I_ModuleEvent;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.menu.childs.ExpandableChild;

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

    private static String TAG = ExpandableGroup.class.getSimpleName();

    public ExpandableGroup(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context)
    {
        this.context =context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expandable_groups, this);
        this.textView = (TextView)findViewById(R.id.tvGroup);
        this.textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (submenuVisible)
                {
                    groupcontainer.removeView(submenu);
                    submenuVisible = false;
                }
                else
                {
                    groupcontainer.addView(submenu);
                    submenuVisible = true;
                }

            }
        });
        this.groupcontainer = (LinearLayout)findViewById(R.id.GroupContainer);
        this.submenu = (LinearLayout)findViewById(R.id.GroupSubMenu);
        groupcontainer.removeView(submenu);
        submenuVisible =false;
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

    private void fillSubMenuItems() {
        //submenu.removeAllViews();
        for (ExpandableChild child:Items)
        {
            Log.d(TAG, child.getName());
            if(child.getParameterHolder() != null)
            {
                Log.d(TAG, child.getName() +" is supported : " + child.getParameterHolder().IsSupported());
                child.onIsSupportedChanged(child.getParameterHolder().IsSupported());
            }
        }
    }


    @Override
    public String ModuleChanged(String module) {
        fillSubMenuItems();
        return null;
    }

    public void setOnChildClick(OnClickListener onChildClick)
    {
        this.onChildclick = onChildClick;
        for (ExpandableChild child:Items)
        {
            child.setOnClickListener(onChildclick);
        }
    }
}
