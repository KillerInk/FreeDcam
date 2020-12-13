package freed.viewer.screenslide;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;

import com.ortiz.touch.TouchImageView;

import java.lang.ref.WeakReference;

import freed.ActivityInterface;
import freed.file.holder.BaseHolder;
import freed.image.ImageTask;
import freed.utils.Log;
import freed.viewer.screenslide.models.ImageFragmentModel;
import freed.viewer.screenslide.views.ImageFragment;

public class BitmapLoader extends ImageTask
{
    private final String TAG = BitmapLoader.class.getSimpleName();
    private WeakReference<TouchImageView> imageviewRef;
    private BaseHolder file;
    private ImageFragmentModel imageFragmentModel;

    public BitmapLoader(ImageFragmentModel file, TouchImageView imageFragment)
    {
        this.file = file.getBaseHolder();
        this.imageFragmentModel = file;
        imageviewRef = new WeakReference<>(imageFragment);
    }

    @Override
    public boolean process() {

        Log.d(TAG, "ImageLoaderTask: LoadImage:" + file.getName());
        final Bitmap response = imageFragmentModel.getBitmapHelper().getBitmap(file,false);
        int hist[] = createHistogramm(response);
        Log.d(TAG, "ImageLoaderTask: LoadImage Done:" + file.getName());
        if (imageviewRef != null && response != null) {
            final TouchImageView imageFragment = imageviewRef.get();
            if (imageFragment != null && imageFragmentModel.getBaseHolder() == file)
            {
                imageFragmentModel.setHistodata(hist);
                Log.d(TAG, "set bitmap to imageview");
                imageFragment.post(() -> {
                    imageFragmentModel.setProgressBarVisible(false);
                    imageFragment.setImageBitmap(response);
                });

            }
            else
                response.recycle();
        }
        else
        {
            if (response != null)
                response.recycle();
            imageFragmentModel.setProgressBarVisible(false);
        }
        return true;
    }

    private  int [] createHistogramm(Bitmap bitmap)
    {
        Log.d(TAG, "Histodata");
        if(bitmap == null || bitmap.isRecycled())
            return null;
        int [] pixels = null;
        int [] histogramData = null;
        if (histogramData == null)
            histogramData = new int [ 256 * 3 ];
        int w = bitmap.getWidth ();
        int h = bitmap.getHeight ();
        if ((pixels == null) || (pixels.length < (w * h)))
            pixels = new int [ w * h ];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
        if (pixels == null)
            return null;
        try {
            for ( int i = 0 ; i < w ; i+=4) {
                for ( int j = 0 ; j < h ; j+=4) {
                    int index = j * w + i ;
                    int r = Color.red ( pixels [ index ]);
                    int g = Color.green ( pixels [ index ]);
                    int b = Color.blue ( pixels [ index ]);
                    histogramData [ r ]++;
                    histogramData [ 256 + g ]++;
                    histogramData [ 512 + b ]++;
                }
            }
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }
        return histogramData;
    }
}
