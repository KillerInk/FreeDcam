package freed.viewer.gridview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.troop.freedcam.R;

import java.util.concurrent.ExecutorService;

import freed.ActivityInterface;
import freed.settings.AppSettingsManager;
import freed.utils.Log;
import freed.viewer.holder.FileHolder;

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
        if (viewerActivityInterface.getFiles() != null)
            return viewerActivityInterface.getFiles().size();
        else return 0;
    }

    @Override
    public Object getItem(int position) {
        return viewerActivityInterface.getFiles().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        final GridImageView imageView;
        if (convertView == null) { // if it's not recycled, initialize some attributes
            imageView = new GridImageView(viewerActivityInterface.getContext(),viewerActivityInterface.getBitmapHelper());
        } else {
            imageView = (GridImageView) convertView;
            //imageView.resetImg();
            imageView.SetBitmapHelper(viewerActivityInterface.getBitmapHelper());
        }
        Log.d(TAG, "filessize:" + viewerActivityInterface.getFiles().size() + " position:"+position);
        if (viewerActivityInterface.getFiles().size() <= position)
            position = viewerActivityInterface.getFiles().size() -1;
        if (imageView.getFileHolder() == null || !imageView.getFileHolder().equals(viewerActivityInterface.getFiles().get(position)) /*||imageView.viewstate != currentViewState*/)
        {
            //imageView.resetImg();
            imageView.SetEventListner(viewerActivityInterface.getFiles().get(position));
            imageView.SetViewState(currentViewState);
            imageView.loadFile(viewerActivityInterface.getFiles().get(position), AppSettingsManager.getInstance().getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size));
        }
        return imageView;
    }



    public void SetViewState(GridViewFragment.ViewStates states)
    {
        currentViewState = states;
        if (viewerActivityInterface.getFiles() == null)
            return;
        for (int i = 0; i< viewerActivityInterface.getFiles().size(); i++)
        {
            FileHolder f = viewerActivityInterface.getFiles().get(i);
            f.SetViewState(states);
        }

    }
}
