package com.freedviewer.gridviewfragments;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.freedcam.utils.Logger;
import com.freedviewer.BitmapHelper;
import com.freedviewer.gridimageviews.GridImageView;
import com.freedviewer.holder.FileHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 28.03.2016.
 */
class ImageAdapter extends BaseAdapter
{
    private final Context mContext;
    private List<FileHolder> files;
    private GridViewFragment.FormatTypes formatsToShow = GridViewFragment.FormatTypes.all;
    private BaseGridViewFragment.ViewStates currentViewState = BaseGridViewFragment.ViewStates.normal;
    private int mImageThumbSize = 0;

    private final String TAG = ImageAdapter.class.getSimpleName();

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
        Logger.d(TAG, "filessize:" +files.size() + " position:"+position);
        if (files.size() <= position)
            position = files.size() -1;
        if (imageView.getFileHolder() == null || !imageView.getFileHolder().equals(files.get(position)) /*||imageView.viewstate != currentViewState*/)
        {
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
        if(files.size() > 0)
            files.clear();
        files = BitmapHelper.getDCIMDirs();

        for (FileHolder f: files)
        {
            Logger.d(TAG, f.getFile().getAbsolutePath());
        }
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
