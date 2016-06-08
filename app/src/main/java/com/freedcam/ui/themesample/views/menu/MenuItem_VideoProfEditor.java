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
import android.content.Intent;
import android.util.AttributeSet;

import com.freedcam.ui.themesample.subfragments.VideoProfileEditorActivity;

/**
 * Created by troop on 17.02.2016.
 */
public class MenuItem_VideoProfEditor extends MenuItem {
    public MenuItem_VideoProfEditor(Context context) {
        super(context);
    }

    public MenuItem_VideoProfEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onClick(int x, int y) {
        Intent i = new Intent(context, VideoProfileEditorActivity.class);
        context.startActivity(i);
    }
}
