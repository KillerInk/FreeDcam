package troop.com.imageviewer;

import android.view.View;

import java.io.File;

/**
 * Created by troop on 12.12.2015.
 *
 * This class represent the State of GridImageview when its added or not to Gridview and updates the
 * GridviewItem when its visibile/invisible on screen
 */
public class FileHolder
{
    private File file;
    private GridViewFragment.ViewStates currentstate = GridViewFragment.ViewStates.normal;
    private EventHandler handler;
    private boolean selected = false;
    private boolean isFolder = false;

    public FileHolder(File file)
    {
        this.file = file;
        if (file.isDirectory())
            isFolder=true;
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

    public File getFile()
    {
        return file;
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

    public interface EventHandler
    {
        void onViewStateChanged(GridViewFragment.ViewStates state);
        void onSelectionChanged(boolean selected);
    }

    public GridViewFragment.ViewStates GetCurrentViewState()
    {
        return currentstate;
    }

    public boolean IsFolder()
    {
        return isFolder;
    }
}
