package troop.com.imageviewer.gridviewfragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.troop.filelogger.Logger;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import troop.com.imageviewer.BitmapHelper;
import troop.com.imageviewer.R;
import troop.com.imageviewer.ScreenSlideActivity;
import troop.com.imageviewer.gridimageviews.GridImageView;
import troop.com.imageviewer.holder.FileHolder;

/**
 * Created by troop on 28.03.2016.
 */
public class ImageAdapter extends BaseAdapter
{
    private final Context mContext;
    private List<FileHolder> files;
    private GridViewFragment.FormatTypes formatsToShow = GridViewFragment.FormatTypes.all;
    private BaseGridViewFragment.ViewStates currentViewState = BaseGridViewFragment.ViewStates.normal;
    private int mImageThumbSize = 0;

    public ImageAdapter(Context context, int mImageThumbSize) {
        super();
        mContext = context;
        files = new ArrayList<>();
        this.mImageThumbSize = mImageThumbSize;
    }

    @Override
    public int getCount() {
        return files.size();
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
        GridImageView imageView;
        if (convertView == null) { // if it's not recycled, initialize some attributes
            imageView = new GridImageView(mContext);
        } else {
            imageView = (GridImageView) convertView;
        }
        if (imageView.getFileHolder() == null || !imageView.getFileHolder().equals(files.get(position)) /*||imageView.viewstate != currentViewState*/) {
            imageView.SetEventListner(files.get(position));
            imageView.SetViewState(currentViewState);
            imageView.loadFile(files.get(position), mImageThumbSize);
        }
        return imageView;
    }



    public void SetViewState(BaseGridViewFragment.ViewStates states)
    {
        currentViewState = states;
        for (int i = 0; i< files.size(); i++)
        {
            FileHolder f = files.get(i);
            f.SetViewState(states);
        }
        notifyDataSetChanged();
    }

    public void loadDCIMFolders()
    {
        files = BitmapHelper.getDCIMDirs();
        notifyDataSetChanged();
    }

    public void loadFiles(File file)
    {
        files.clear();
        FileHolder.readFilesFromFolder(file,files,formatsToShow, false);

        notifyDataSetChanged();
    }

    public FileHolder GetFileHolder(int pos)
    {
        return files.get(pos);
    }

    public void SetFormatToShow(GridViewFragment.FormatTypes formatsToShow)
    {
        this.formatsToShow = formatsToShow;
    }

    public List<FileHolder> getFiles()
    {
        return files;
    }
}
