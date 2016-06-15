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

package com.freedviewer;

import android.R.id;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.AbstractFragmentActivity;
import com.freedviewer.gridview.GridViewFragment;
import com.freedviewer.screenslide.ScreenSlideFragment;

/**
 * Created by troop on 11.12.2015.
 */
public class GridViewActivity extends AbstractFragmentActivity
{
    private final String TAGGrid = GridViewFragment.class.getSimpleName();
    private final String TAGSlide = ScreenSlideFragment.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        files = getDCIMDirs();
        loadGridViewFragment();
    }

    private void loadGridViewFragment() {
        if (getSupportFragmentManager().findFragmentByTag(TAGGrid) == null)
        {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            GridViewFragment fragment = new GridViewFragment();
            fragment.SetOnGridItemClick(onGridItemClick);
            ft.replace(id.content, fragment, TAGGrid);
            ft.commit();
        }
    }

    private final ScreenSlideFragment.I_ThumbClick onScreenSlideBackClick = new ScreenSlideFragment.I_ThumbClick() {
        @Override
        public void onThumbClick(int position)
        {
            loadGridViewFragment();
        }
    };

    private final ScreenSlideFragment.I_ThumbClick onGridItemClick = new ScreenSlideFragment.I_ThumbClick() {
        @Override
        public void onThumbClick(int position)
        {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ScreenSlideFragment fragment = new ScreenSlideFragment();
            fragment.SetOnThumbClick(onScreenSlideBackClick);
            fragment.defitem  = position;
            ft.replace(id.content, fragment, TAGSlide);
            ft.commit();
        }
    };


}
