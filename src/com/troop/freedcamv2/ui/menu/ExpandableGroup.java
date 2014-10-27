package com.troop.freedcamv2.ui.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcamv2.camera.modules.I_ModuleEvent;
import com.troop.freedcamv2.camera.modules.ModuleHandler;

import java.util.ArrayList;

/**
 * Created by troop on 18.08.2014.
 */
public class ExpandableGroup extends LinearLayout implements I_ModuleEvent
{
    private String Name;
    private ArrayList<ExpandableChild> Items;
    Context context;
    public ArrayList<String> modulesToShow;
    TextView textView;

    public ExpandableGroup(Context context) {
        super(context);
        init(context);
    }

    public ExpandableGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExpandableGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context)
    {
        this.context =context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expandable_groups, this);
        textView = (TextView)findViewById(R.id.tvGroup);
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
    public void setItems(ArrayList<ExpandableChild> Items) {
        this.Items = Items;
    }



    @Override
    public String ModuleChanged(String module) {
        if(modulesToShow.contains(module) || modulesToShow.contains(ModuleHandler.MODULE_ALL))
            this.setVisibility(VISIBLE);
        else
            this.setVisibility(GONE);
        return null;
    }
}
