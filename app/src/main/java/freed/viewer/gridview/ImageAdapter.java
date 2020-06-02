package freed.viewer.gridview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.troop.freedcam.R;

import java.util.concurrent.ExecutorService;

import freed.ActivityInterface;
import freed.FreedApplication;
import freed.file.holder.BaseHolder;
import freed.utils.Log;

/**
 * Created by troop on 02.03.2017.
 */

class ImageAdapter extends BaseAdapter
{
    private final String TAG = ImageAdapter.class.getSimpleName();

    private ExecutorService executor;
    private ActivityInterface viewerActivityInterface;

    /**
     * the current state of the gridview if items are in selection mode or normal rdy to click
     */
    private GridViewFragment.ViewStates currentViewState = GridViewFragment.ViewStates.normal;

    public ImageAdapter(ActivityInterface viewerActivityInterface) {
        this.viewerActivityInterface = viewerActivityInterface;
    }

    @Override
    public int getCount()
    {
        if (viewerActivityInterface.getFileListController() != null && viewerActivityInterface.getFileListController().getFiles() != null)
            return viewerActivityInterface.getFileListController().getFiles().size();
        else return 0;
    }

    @Override
    public Object getItem(int position) {
        return viewerActivityInterface.getFileListController().getFiles().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        final GridImageView imageView;
        if (convertView == null) { // if it's not recycled, initialize some attributes
            imageView = new GridImageView(FreedApplication.getContext(),viewerActivityInterface.getBitmapHelper());
        } else {
            imageView = (GridImageView) convertView;
            //imageView.resetImg();
            imageView.SetBitmapHelper(viewerActivityInterface.getBitmapHelper());
        }
        Log.d(TAG, "filessize:" + viewerActivityInterface.getFileListController().getFiles().size() + " position:"+position);
        if (viewerActivityInterface.getFileListController().getFiles().size() <= position)
            position = viewerActivityInterface.getFileListController().getFiles().size() -1;
        if (imageView.getFileHolder() == null || !imageView.getFileHolder().equals(viewerActivityInterface.getFileListController().getFiles().get(position)) /*||imageView.viewstate != currentViewState*/)
        {
            //imageView.resetImg();
            imageView.SetEventListner(viewerActivityInterface.getFileListController().getFiles().get(position));
            imageView.SetViewState(currentViewState);
            imageView.loadFile(viewerActivityInterface.getFileListController().getFiles().get(position), FreedApplication.getContext().getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size));
        }
        return imageView;
    }



    public void SetViewState(GridViewFragment.ViewStates states)
    {
        currentViewState = states;
        if (viewerActivityInterface.getFileListController() == null || viewerActivityInterface.getFileListController().getFiles() == null)
            return;
        for (int i = 0; i< viewerActivityInterface.getFileListController().getFiles().size(); i++)
        {
            BaseHolder f = viewerActivityInterface.getFileListController().getFiles().get(i);
            f.SetViewState(states);
        }

    }
}
