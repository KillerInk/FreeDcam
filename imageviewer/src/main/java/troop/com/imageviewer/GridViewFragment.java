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
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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

        gridView.setAdapter(mPagerAdapter);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
            ImageView imageView;
            if (convertView == null) { // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(new GridView.LayoutParams(
                        AbsoluteLayout.LayoutParams.MATCH_PARENT, AbsoluteLayout.LayoutParams.MATCH_PARENT));
            } else {
                imageView = (ImageView) convertView;
            }
           loadBitmap(files[position], imageView); // Load image into ImageView
            return imageView;
        }
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public void loadBitmap(File file, ImageView imageView) {
        if (cancelPotentialWork(file, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(getResources(), ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(getResources(),R.drawable.noimage), mImageThumbSize, mImageThumbSize), task);
            imageView.setImageDrawable(asyncDrawable);
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

    public static boolean cancelPotentialWork(File file, ImageView imageView) {
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
        private final WeakReference<ImageView> imageViewReference;
        private File file;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
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
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    private Bitmap getBitmap(File file)
    {
        Bitmap response;
        if (file.getAbsolutePath().endsWith(".jpg"))
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 20;
            response = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            response = ThumbnailUtils.extractThumbnail(response, mImageThumbSize, mImageThumbSize);
        }
        else if (file.getAbsolutePath().endsWith(".mp4"))
        {
            response = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
            response = ThumbnailUtils.extractThumbnail(response, mImageThumbSize, mImageThumbSize);
        }
        else if (file.getAbsolutePath().endsWith(".dng")|| file.getAbsolutePath().endsWith(".raw"))
        {
            try {


                response = RawUtils.UnPackRAW(file.getAbsolutePath());
                if(response != null)
                    response.setHasAlpha(true);
                response = ThumbnailUtils.extractThumbnail(response,mImageThumbSize,mImageThumbSize);
            }
            catch (IllegalArgumentException ex)
            {
                response = null;

            }
        }
        else
            response = null;

        return response;
    }



}
