package troop.com.imageviewer.gridviewfragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import troop.com.imageviewer.R;

/**
 * Created by troop on 23.12.2015.
 */
public class BaseGridViewFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
{
    GridView gridView;
    View view;
    boolean pos0ret = false;
    ViewStates currentViewState = ViewStates.normal;

    public enum ViewStates
    {
        normal,
        selection,
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        inflate(inflater, container);
        view = inflater.inflate(R.layout.gridviewfragment, container, false);
        this.gridView = (GridView) view.findViewById(R.id.gridView);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);
        return view;
    }

    void inflate(LayoutInflater inflater, ViewGroup container)
    {
        view = inflater.inflate(R.layout.basegridview, container, false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }
}
