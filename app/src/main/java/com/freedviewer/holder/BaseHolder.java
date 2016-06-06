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

package com.freedviewer.holder;

import com.freedviewer.gridview.BaseGridViewFragment;
import com.freedviewer.gridview.GridViewFragment;

/**
 * Created by Ingo on 27.12.2015.
 */
public class BaseHolder
{
    protected BaseGridViewFragment.ViewStates currentstate = GridViewFragment.ViewStates.normal;
    protected EventHandler handler;
    protected boolean selected = false;


    public GridViewFragment.ViewStates GetCurrentViewState()
    {
        return currentstate;
    }

    public void SetViewState(GridViewFragment.ViewStates state)
    {
        this.currentstate = state;
        if (handler != null)
            handler.onViewStateChanged(state);
    }

    public void SetEventListner(EventHandler handler)
    {
        this.handler = handler;
    }

    public interface EventHandler
    {
        void onViewStateChanged(GridViewFragment.ViewStates state);
        void onSelectionChanged(boolean selected);
    }

    public boolean IsSelected()
    {
        return selected;
    }
    public void SetSelected(boolean selected)
    {
        this.selected = selected;
        if (handler!=null)
            handler.onSelectionChanged(selected);
    }
}
