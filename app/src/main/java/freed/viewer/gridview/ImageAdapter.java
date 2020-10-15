package freed.viewer.gridview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.troop.freedcam.R;

import java.util.List;
import java.util.concurrent.ExecutorService;

import freed.ActivityInterface;
import freed.FreedApplication;
import com.troop.freedcam.file.FileListController;
import com.troop.freedcam.file.holder.BaseHolder;
import com.troop.freedcam.logger.Log;

/**
 * Created by troop on 02.03.2017.
 */

class ImageAdapter extends BaseAdapter
{
    private final String TAG = ImageAdapter.class.getSimpleName();

    private ActivityInterface viewerActivityInterface;
    private List<BaseHolder> files;

    /**
     * the current state of the gridview if items are in selection mode or normal rdy to click
     */
    private GridViewFragment.ViewStates currentViewState = GridViewFragment.ViewStates.normal;

    public ImageAdapter(ActivityInterface viewerActivityInterface) {
        this.viewerActivityInterface = viewerActivityInterface;
    }

    public void setFiles(List<BaseHolder> files)
    {
        this.files =files;
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
            imageView = new GridImageView(FreedApplication.getContext(),viewerActivityInterface.getBitmapHelper());
        } else {
            imageView = (GridImageView) convertView;
            //imageView.resetImg();
            imageView.SetBitmapHelper(viewerActivityInterface.getBitmapHelper());
        }
        Log.d(TAG, "filessize:" + files.size() + " position:"+position);
        if (viewerActivityInterface.getFileListController().getFiles().size() <= position)
            position = viewerActivityInterface.getFileListController().getFiles().size() -1;
        if (imageView.getFileHolder() == null || !imageView.getFileHolder().equals(files.get(position)) /*||imageView.viewstate != currentViewState*/)
        {
            //imageView.resetImg();
            imageView.SetEventListner(files.get(position));
            imageView.SetViewState(currentViewState);
            imageView.loadFile(files.get(position), FreedApplication.getContext().getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size));
        }
        return imageView;
    }



    public void SetViewState(GridViewFragment.ViewStates states)
    {
        currentViewState = states;
        if (files == null )
            return;
        for (int i = 0; i< files.size(); i++)
        {
            BaseHolder f = files.get(i);
            f.SetViewState(states);
        }

    }
}
