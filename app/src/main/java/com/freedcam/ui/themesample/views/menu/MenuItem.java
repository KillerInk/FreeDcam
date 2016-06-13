/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedcam.ui.themesample.views.menu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freedcam.ui.I_Activity;
import com.freedcam.ui.I_swipe;
import com.freedcam.ui.SwipeMenuListner;
import com.freedcam.ui.themesample.views.uichilds.UiSettingsChild;
import com.freedcam.utils.AppSettingsManager;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;
import com.troop.freedcam.R.styleable;

/**
 * Created by troop on 14.06.2015.
 */
public class MenuItem extends UiSettingsChild implements I_swipe
{
    private TextView description;

    private LinearLayout toplayout;

    private TextView headerText;

    private SwipeMenuListner controlswipeListner;

    public MenuItem(Context context) {
        super(context);
    }

    public MenuItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        //get custom attributs
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                styleable.MenuItem,
                0, 0
        );
        TypedArray b = context.getTheme().obtainStyledAttributes(
                attrs,
                styleable.UiSettingsChild,
                0, 0
        );
        //try to set the attributs
        try
        {

            headerText.setText(b.getText(styleable.UiSettingsChild_HeaderText));

            description.setText(a.getText(styleable.MenuItem_Description));
        }
        finally {
            a.recycle();
        }
        sendLog("Ctor done");
    }

    @Override
    protected void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflateTheme(inflater);
        headerText = (TextView) findViewById(id.textview_menuitem_header);
        valueText = (TextView) findViewById(id.textview_menuitem_header_value);
        description = (TextView) findViewById(id.textview_menuitem_description);
        toplayout = (LinearLayout) findViewById(id.menu_item_toplayout);
        //toplayout.setOnClickListener(this);
        controlswipeListner = new SwipeMenuListner(this);
        toplayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return controlswipeListner.onTouchEvent(event);
            }
        });
    }

    @Override
    protected void inflateTheme(LayoutInflater inflater) {
        inflater.inflate(layout.menu_item, this);
    }

    @Override
    public void onValueChanged(String val)
    {
        sendLog("Set Value to:" + val);
        valueText.setText(val);
    }

    @Override
    public void onClick(View v) {
        if (onItemClick != null)
            onItemClick.onMenuItemClick(this, false);
    }

    @Override
    public void SetStuff(I_Activity i_activity, String settingvalue, AppSettingsManager appSettingsManager) {
        super.SetStuff(i_activity, settingvalue,appSettingsManager);

    }

    @Override
    public void doLeftToRightSwipe()
    {

    }

    @Override
    public void doRightToLeftSwipe()
    {

    }

    @Override
    public void doTopToBottomSwipe() {

    }

    @Override
    public void doBottomToTopSwipe() {

    }

    @Override
    public void onClick(int x, int y) {
        if (onItemClick != null)
            onItemClick.onMenuItemClick(this, false);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {

    }

    @Override
    public void onIsSupportedChanged(boolean isSupported)
    {
        sendLog("isSupported:" + isSupported);
        if (isSupported) {
            setVisibility(View.VISIBLE);
        }
        else
            setVisibility(View.GONE);
    }
}
