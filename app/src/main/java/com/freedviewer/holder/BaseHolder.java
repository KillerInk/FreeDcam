package com.freedviewer.holder;

import com.freedviewer.gridviewfragments.BaseGridViewFragment;
import com.freedviewer.gridviewfragments.GridViewFragment;

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
