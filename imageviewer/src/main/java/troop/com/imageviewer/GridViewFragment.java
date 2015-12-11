package troop.com.imageviewer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.defcomk.jni.libraw.RawUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

/**
 * Created by troop on 11.12.2015.
 */
public class GridViewFragment extends Fragment implements AdapterView.OnItemClickListener
{
    GridView gridView;
    View view;
    private ImageAdapter mPagerAdapter;
    File[] files;
    int mImageThumbSize = 0;
    CacheHelper cacheHelper;
    final String TAG = GridViewFragment.class.getSimpleName();




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.gridviewfragment, container, false);
        this.gridView = (GridView) view.findViewById(R.id.gridView);
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        return view;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        files = ScreenSlideFragment.loadFilePaths();
        mPagerAdapter = new ImageAdapter(getContext());
        cacheHelper = new CacheHelper(getActivity());
        gridView.setAdapter(mPagerAdapter);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

    }

    private class ImageAdapter extends BaseAdapter {
        private final Context mContext;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
        }

        @Override
        public int getCount() {
            return files.length;
        }

        @Override
        public Object getItem(int position) {
            return files[position];
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

                imageView.setLayoutParams(new GridView.LayoutParams(
                        AbsoluteLayout.LayoutParams.MATCH_PARENT, AbsoluteLayout.LayoutParams.MATCH_PARENT));
            } else {
                imageView = (GridImageView) convertView;
            }
           loadBitmap(files[position], imageView); // Load image into ImageView
            return imageView;
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

    public void loadBitmap(File file, GridImageView imageView) {
        if (cancelPotentialWork(file, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(getResources(), ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(getResources(),R.drawable.noimage), mImageThumbSize, mImageThumbSize), task);
            imageView.setImageDrawable(asyncDrawable);
            String f = file.getName();
            imageView.SetFileEnding(f.substring(f.length()-3));
            task.execute(file);
        }
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
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    private Bitmap getBitmap(File file)
    {
        Bitmap response = cacheHelper.getBitmapFromMemCache(file.getName());
        if (response == null)
        {
            Log.d(TAG,"No image in memory try from disk");
            response = cacheHelper.getBitmapFromDiskCache(file.getName());
        }
        if (response == null)
        {
            Log.d(TAG,"No image in thumbcache try from disk");
            if (file.getAbsolutePath().endsWith(".jpg")) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 20;
                response = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                response = ThumbnailUtils.extractThumbnail(response, mImageThumbSize, mImageThumbSize);
                if (response != null) {
                    cacheHelper.addBitmapToMemoryCache(file.getName(), response);
                    cacheHelper.addBitmapToCache(file.getName(), response);
                }

            } else if (file.getAbsolutePath().endsWith(".mp4")) {
                response = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                response = ThumbnailUtils.extractThumbnail(response, mImageThumbSize, mImageThumbSize);
                if (response != null)
                {
                    cacheHelper.addBitmapToCache(file.getName(), response);
                    cacheHelper.addBitmapToMemoryCache(file.getName(), response);
                }
            } else if (file.getAbsolutePath().endsWith(".dng") || file.getAbsolutePath().endsWith(".raw")) {
                try {


                    response = RawUtils.UnPackRAW(file.getAbsolutePath());
                    if (response != null)
                        response.setHasAlpha(true);
                    response = ThumbnailUtils.extractThumbnail(response, mImageThumbSize, mImageThumbSize);
                    if (response != null) {
                        cacheHelper.addBitmapToMemoryCache(file.getName(), response);
                        cacheHelper.addBitmapToCache(file.getName(), response);
                    }
                } catch (IllegalArgumentException ex) {
                    response = null;

                }
            }
        }


        return response;
    }



}
