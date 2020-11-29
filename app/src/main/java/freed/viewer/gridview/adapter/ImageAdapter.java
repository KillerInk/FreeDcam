package freed.viewer.gridview.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import freed.ActivityInterface;
import freed.FreedApplication;
import freed.file.holder.BaseHolder;
import freed.utils.Log;
import freed.viewer.gridview.enums.ViewStates;
import freed.viewer.gridview.models.GridImageViewModel;
import freed.viewer.gridview.views.GridImageView;
import freed.viewer.gridview.views.GridViewFragment;

/**
 * Created by troop on 02.03.2017.
 */

public class ImageAdapter extends BaseAdapter
{
    private final String TAG = ImageAdapter.class.getSimpleName();

    private ActivityInterface viewerActivityInterface;
    private List<BaseHolder> files;
    private List<GridImageViewModel> gridImageViewModels;

    /**
     * the current state of the gridview if items are in selection mode or normal rdy to click
     */
    private ViewStates currentViewState = ViewStates.normal;

    public ImageAdapter(ActivityInterface viewerActivityInterface) {
        this.viewerActivityInterface = viewerActivityInterface;
    }

    public void setFiles(List<BaseHolder> files)
    {
        this.files =files;
        gridImageViewModels = new ArrayList<>();
        for (BaseHolder baseHolder : files)
        {
            gridImageViewModels.add(new GridImageViewModel(viewerActivityInterface.getBitmapHelper(),baseHolder));
        }
        notifyDataSetChanged();
    }

    public BaseHolder getBaseHolder(int pos)
    {
        return files.get(pos);
    }

    @Override
    public int getCount()
    {
        if (files != null)
            return files.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        final GridImageView imageView;
        if (convertView == null) { // if it's not recycled, initialize some attributes
            imageView = new GridImageView(FreedApplication.getContext());
        }
        else
            imageView = (GridImageView) convertView;
        Log.d(TAG, "filessize:" + files.size() + " position:"+position);
        if (viewerActivityInterface.getFileListController().getFiles().size() <= position)
            position = viewerActivityInterface.getFileListController().getFiles().size() -1;

        gridImageViewModels.get(position).setViewState(currentViewState);
        imageView.bindModel(gridImageViewModels.get(position));
        return imageView;
    }



    public void SetViewState(ViewStates states)
    {
        currentViewState = states;
        if (files == null )
            return;
        for (int i = 0; i< files.size(); i++)
        {
            /*BaseHolder f = files.get(i);
            f.SetViewState(states);*/
            gridImageViewModels.get(i).setViewState(states);
        }
    }

    public void setViewState(ViewStates states, int pos)
    {
        currentViewState = states;
        if (files == null )
            return;
        gridImageViewModels.get(pos).setViewState(states);

    }
}
