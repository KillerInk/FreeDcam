package freed.viewer.screenslide;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.android.renderscript.Toolkit;
import com.ortiz.touch.TouchImageView;

import java.lang.ref.WeakReference;

import freed.file.holder.BaseHolder;
import freed.image.ImageTask;
import freed.utils.Log;
import freed.viewer.screenslide.models.ImageFragmentModel;

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
        if(bitmap == null || bitmap.isRecycled())
            return null;
        int [] histogramData = null;

        try {
            histogramData = Toolkit.INSTANCE.histogram(bitmap);
        }
        catch (NullPointerException ex)
        {

        }
        /*Log.d(TAG, "Histodata");

        int [] pixels = null;

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
            *//*int destWidth = 256;
            int destHeight = 256;
            int destPixels[] = new int[destWidth*destHeight];
            int scopeIntensity = 30;
            for (int i = 0; i < w; i++) {
                int destX = i/w*destWidth;
                for(int j = 0; j < h; j++) {
                    //red
                    int redValue = Color.red(pixels[j*w+i]); //(sourcePixels[j*sourceWidth+i] % 256) // [0,255]
                    int destIndex = (destHeight-1-redValue)*destWidth + destX;

                    int destRedVal = destPixels[destIndex];
                    destRedVal = Math.min(destRedVal + scopeIntensity, 255);

                    destPixels[destIndex] = destPixels[destIndex] & 0xff_ff_ff_00;
                    destPixels[destIndex] = destPixels[destIndex] | destRedVal<<16;
                    //green
                    int greenValue = Color.green(pixels[j*w+i]); //(sourcePixels[j*sourceWidth+i] % 256) // [0,255]
                    int destIndexgreen = (destHeight-1-greenValue)*destWidth + destX;

                    int destGreenVal = destPixels[destIndexgreen];
                    destGreenVal = Math.min(destGreenVal + scopeIntensity, 255);

                    destPixels[destIndexgreen] = destPixels[destIndexgreen] & 0xff_ff_ff_00;
                    destPixels[destIndexgreen] = destPixels[destIndexgreen] | (destGreenVal<<8);
                    //blue
                    int blueValue = Color.blue(pixels[j*w+i]); //(sourcePixels[j*sourceWidth+i] % 256) // [0,255]
                    int destIndexblue = (destHeight-1-blueValue)*destWidth + destX;

                    int destblueVal = destPixels[destIndexblue];
                    destblueVal = Math.min(destblueVal + scopeIntensity, 255);

                    destPixels[destIndexblue] = destPixels[destIndexblue] & 0xff_ff_ff_00;
                    destPixels[destIndexblue] = destPixels[destIndexblue] | (destblueVal);
                }
            }*//*
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }*/
        return histogramData;
    }
}
