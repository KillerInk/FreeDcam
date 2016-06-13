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

package com.freedviewer.screenslide;

import android.R.id;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.AbstractFragmentActivity;
import com.freedviewer.gridview.GridViewFragment.FormatTypes;

/**
 * Created by troop on 21.08.2015.
 */
public class ScreenSlideActivity extends AbstractFragmentActivity
{
    private final String TAG = ScreenSlideActivity.class.getSimpleName();
    public static final String EXTRA_IMAGE = "extra_image";
    public static final String IMAGE_PATH = "image_path";
    public static final String FileType = "filetype";
    private int extra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenSlideFragment fragment = (ScreenSlideFragment) getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment == null)
        {
            fragment = new ScreenSlideFragment();
            fragment.SetAppSettingsManagerAndBitmapHelper(appSettingsManager, bitmapHelper);
            int extraCurrentItem = getIntent().getIntExtra(EXTRA_IMAGE, -1);
            String path = getIntent().getStringExtra(IMAGE_PATH);
            if (extraCurrentItem != -1) {
                extra = extraCurrentItem;
            }
            if (path != null && !path.equals(""))
                fragment.FilePathToLoad = path;

        }
        fragment.defitem = extra;
        fragment.filestoshow = FormatTypes.valueOf(getIntent().getStringExtra(FileType));
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(id.content, fragment, TAG);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
