package com.troop.freedcam.ui.menu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.ArrayList;

/**
 * Created by troop on 18.08.2014.
 */
public class ExpandableListViewMenuAdapter extends BaseExpandableListAdapter
{
    private ArrayList<ExpandableGroup> groups;
    private Context context;

    public  ExpandableListViewMenuAdapter(Context context, ArrayList<ExpandableGroup> groups)
    {
        this.groups = groups;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return groups.get(groupPosition).getItems().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).getItems().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        ExpandableGroup group = (ExpandableGroup)getGroup(groupPosition);
        convertView = group;
        //tv.setTag(group.getTag());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        ExpandableChild child = (ExpandableChild) getChild(groupPosition, childPosition);

        convertView = child;
        convertView.setClickable(false);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
