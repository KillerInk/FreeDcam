package freed.viewer.screenslide;

import android.graphics.Bitmap;
import android.graphics.Color;
import com.ortiz.touch.TouchImageView;
import java.lang.ref.WeakReference;
import freed.file.holder.BaseHolder;
import freed.image.ImageTask;
import freed.utils.Log;
import freed.viewer.screenslide.models.ImageFragmentModel;

public class BitmapLoader extends ImageTask {
    private final String TAG = BitmapLoader.class.getSimpleName();
    private final WeakReference<TouchImageView> imageviewRef;
    private final BaseHolder file;
    private final ImageFragmentModel imageFragmentModel;

    public BitmapLoader(ImageFragmentModel file, TouchImageView imageFragment) {
        this.file = file.getBaseHolder();
        this.imageFragmentModel = file;
        imageviewRef = new WeakReference<>(imageFragment);
    }

    @Override
    public boolean process() {

        Log.d(TAG, "ImageLoaderTask: LoadImage:" + file.getName());
        final Bitmap response = imageFragmentModel.getBitmapHelper().getBitmap(file,false);
        int[] hist = null;
        if (response == null)
            return true;
        if (response.getConfig() == Bitmap.Config.ARGB_8888)
            hist = createHistogramm(response);
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

    private int[] createHistogramm(Bitmap bitmap) {
        if(bitmap == null || bitmap.isRecycled())
            return null;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int size = w * h;
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
        int[] histogramData = new int[768]; // 256 for red, 256 for green offset by 256, 256 for blue offset by 512
        for (int i = 0; i < size; i++) {
            int color = pixels[i];
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            histogramData[r]++;
            histogramData[256 + g]++;
            histogramData[512 + b]++;
        }
        return histogramData;
    }
}
