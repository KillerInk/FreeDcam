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
    final String NOIMAGE = "noimage_thumb";
    final String FOLDER = "folder_thumb";
    private Bitmap noimg;
    private Bitmap fold;
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
            loadBitmap(files.get(position).getFile(), imageView); // Load image into ImageView
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
        FileHolder.readFilesFromFolder(file,files,formatsToShow);

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

    public void loadBitmap(File file, GridImageView imageView)
    {
        if (cancelPotentialWork(file, imageView))
        {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            if (!file.isDirectory())
            {
                if (noimg == null)
                    noimg = BitmapHelper.getBitmap(new File(NOIMAGE),true, mImageThumbSize,mImageThumbSize);
                if (noimg == null)
                {
                    noimg = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.noimage);
                    BitmapHelper.CACHE.addBitmapToCache(NOIMAGE, noimg);
                }

                final AsyncDrawable asyncDrawable =
                        new AsyncDrawable(mContext.getResources(), noimg, task);
                imageView.setImageDrawable(asyncDrawable);
            }
            else
            {
                if (fold == null)
                    fold = BitmapHelper.CACHE.getBitmapFromMemCache(FOLDER);
                if (fold == null)
                    fold = BitmapHelper.CACHE.getBitmapFromDiskCache(FOLDER);
                if (fold == null)
                {
                    fold = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.folder);
                    BitmapHelper.CACHE.addBitmapToCache(FOLDER, fold);

                }
                final AsyncDrawable asyncDrawable =
                        new AsyncDrawable(mContext.getResources(),fold, task);
                imageView.setImageDrawable(asyncDrawable);
            }
            String f = file.getName();
            if (!file.isDirectory()) {
                imageView.SetFolderName("");
                imageView.SetFileEnding(f.substring(f.length() - 3));
            }

            else {
                imageView.SetFileEnding("");
                imageView.SetFolderName(f);
            }

            task.execute(file);
        }
    }


    private static BitmapWorkerTask getBitmapWorkerTask(GridImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    public static boolean cancelPotentialWork(File file, GridImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final File bitmapData = bitmapWorkerTask.file;
            if (bitmapData != file) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    class BitmapWorkerTask extends AsyncTask<File, Void, Bitmap> {
        private final WeakReference<GridImageView> imageViewReference;
        private File file;

        public BitmapWorkerTask(GridImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<GridImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(File... params) {
            file = params[0];
            return getBitmap(file);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final GridImageView imageView = imageViewReference.get();
                if (imageView != null)
                {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    private Bitmap getBitmap(File file)
    {
        return BitmapHelper.getBitmap(file,true,mImageThumbSize,mImageThumbSize);
    }
}
