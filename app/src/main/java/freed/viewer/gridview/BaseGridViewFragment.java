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

package freed.viewer.gridview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

/**
 * Created by troop on 23.12.2015.
 */
public class BaseGridViewFragment extends Fragment  implements OnItemClickListener, OnItemLongClickListener
{
    protected GridView gridView;
    protected View view;
    protected boolean pos0ret;
    protected ViewStates currentViewState = ViewStates.normal;

    public enum ViewStates
    {
        normal,
        selection,
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        inflate(inflater, container);
        view = inflater.inflate(layout.freedviewer_gridviewfragment, container, false);
        gridView = (GridView) view.findViewById(id.gridView);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);
        return view;
    }

    protected void inflate(LayoutInflater inflater, ViewGroup container)
    {
        view = inflater.inflate(layout.freedviewer_gridview_base, container, false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }
}
