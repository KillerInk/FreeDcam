package freed.viewer.gridview;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import freed.file.holder.BaseHolder;
import freed.image.ImageTask;
import freed.utils.Log;
import freed.viewer.gridview.models.GridImageViewModel;
import freed.viewer.helper.BitmapHelper;

public  class BitmapLoadRunnable extends ImageTask
{
    private final String TAG = BitmapLoadRunnable.class.getSimpleName();
    WeakReference<ImageView> imageviewRef;
    BaseHolder fileHolder;
    private final BitmapHelper bitmapHelper;
    private final GridImageViewModel model;

    public BitmapLoadRunnable(ImageView imageView, GridImageViewModel model)
    {
        imageviewRef = new WeakReference<>(imageView);
        this.fileHolder = model.getImagePath();
        this.bitmapHelper = model.getBitmapHelper();
        this.model = model;
    }

    public void resetImageView()
    {
        imageviewRef = null;
    }

    public ImageView getImageView()
    {
        if (imageviewRef != null)
            return imageviewRef.get();
        return null;
    }

    public void stopProgessbar()
    {
        model.setProgressBarVisible(false);
    }

    @Override
    public boolean process() {
        Log.d(TAG, "load file:" + fileHolder.getName());
        final Bitmap bitmap = bitmapHelper.getBitmap(fileHolder, true);
        if (imageviewRef != null && bitmap != null) {
            final ImageView imageView = imageviewRef.get();
            if (imageView != null && model.getImagePath() == fileHolder)
            {
                imageView.post(() -> imageView.setImageBitmap(bitmap));
                Log.d(TAG, "set bitmap to imageview");
            }
            else {
                Log.d(TAG, "Imageview has new file already, skipping it");
                bitmap.recycle();
            }
        }
        else if (bitmap != null) {
            bitmap.recycle();
        }
        model.setProgressBarVisible(false);
        model.bitmapLoadRunnable = null;
        return false;
    }
}
