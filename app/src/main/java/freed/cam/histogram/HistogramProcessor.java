package freed.cam.histogram;

import android.graphics.Color;

import com.google.android.renderscript.Toolkit;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import freed.utils.Log;

public class HistogramProcessor {

    private static final String TAG = HistogramProcessor.class.getSimpleName();
    private HistogramController histogramController;

    private final int KEEP_ALIVE_TIME = 500;
    private final BlockingQueue<Runnable> histogramProcessingQueue;
    private final ThreadPoolExecutor histogramProcessingExecutor;

    public HistogramProcessor(HistogramController histogramController)
    {
        this.histogramController = histogramController;
        int coresize = 1;
        histogramProcessingQueue = new ArrayBlockingQueue<>(2);

        histogramProcessingExecutor = new ThreadPoolExecutor(
                coresize,       // Initial pool size
                coresize,       // Max pool size
                KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS,
                histogramProcessingQueue);
        //handel case that queue is full, and wait till its free
        histogramProcessingExecutor.setRejectedExecutionHandler((r, executor) -> {
            Log.d(TAG, "imageSave Queue full");
            try {
                executor.getQueue().take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }


    public void add(final byte[] bytes,int width, int height)
    {
        histogramProcessingExecutor.execute(new HistogramCreatorRunner(bytes,width,height));
        //histogramProcessingExecutor.execute(new WaveformCreatorRunner(bytes,width,height,512,256));
    }


    class HistogramCreatorRunner implements Runnable {
        private byte[] bytes;
        private int width;
        private int height;
        HistogramCreatorRunner(byte[] bytes, int width, int height)
        {
            this.bytes = bytes;
            this.width = width;
            this.height = height;
        }

        @Override
        public void run() {
            int[] histo = Toolkit.INSTANCE.histogram(bytes,4, width,height);
            histogramController.onHistogramChanged(histo);
        }
    }

    public static class Colormerge
    {
        int red;
        int green;
        int blue;
        int alpha;

        public void setARGBColor(int color)
        {
            this.alpha = Color.alpha(color);
            this.red = Color.red(color);
            this.green = Color.green(color);
            this.blue = Color.blue(color);
        }

        public void setRGBAColor(int color)
        {
            this.red = Color.alpha(color);
            this.green = Color.red(color);
            this.blue = Color.green(color);
            this.alpha = Color.blue(color);
        }

        public void addRed(int red)
        {
            this.red += red;
            if (this.red > 255)
                this.red = 255;
        }

        public void addGreen(int green)
        {
            this.green += green;
            if (this.green > 255)
                this.green = 255;
        }

        public void addBlue(int blue)
        {
            this.blue += blue;
            if (this.blue > 255)
                this.blue = 255;
        }

        public int getARGB()
        {
            return Color.argb(alpha,red,green,blue);
        }

        public int getAlpha() {
            return alpha;
        }

        public int getRed() {
            return red;
        }

        public int getGreen() {
            return green;
        }

        public int getBlue() {
            return blue;
        }

        public float getLuminance()
        {
           return  (red * 0.2126f + green * 0.7152f + blue * 0.0722f);
        }

        public float getLuminanceNormalized()
        {
           return  getLuminance()/255;
        }
    }

    class WaveformCreatorRunner implements Runnable {
        private byte[] bytes;
        private int width;
        private int height;
        int dest_width;
        int dest_height;
        WaveformCreatorRunner(byte[] bytes, int width, int height, int dest_width, int dest_height)
        {
            this.bytes = bytes;
            this.width = width;
            this.height = height;
            this.dest_width = dest_width;
            this.dest_height = dest_height;
        }

        @Override
        public void run() {

            int destWidth = dest_width;
            int destHeight = dest_height;
            int destPixels[] = new int[destWidth*destHeight];
            int stepW = width / destWidth;
            int stepH = height / destHeight;
            IntBuffer pixels = ByteBuffer.wrap(bytes).asIntBuffer();
            Colormerge inputPix = new Colormerge();

            for (int y = 0; y < destHeight; y++)
            {
                for (int x = 0; x< destWidth; x ++)
                {
                    inputPix.setRGBAColor(pixels.get(x*stepW+y*stepH));
                    destPixels[y*destWidth+x] = inputPix.getARGB();
                }
            }

            /*int scopeIntensity = 30;
            IntBuffer pixels = ByteBuffer.wrap(bytes).asIntBuffer();
            for (int i = 0; i < w; i++) {
                int destX = i/w*destWidth;
                for(int j = 0; j < h; j++) {
                    //red
                    int redValue = Color.red(pixels.get(j*w+i)); //(sourcePixels[j*sourceWidth+i] % 256) // [0,255]
                    int destIndex = 0;
                    try {
                        destIndex =  (destHeight-1-redValue) * destWidth + destX;
                    }
                    catch (ArithmeticException ex)
                    {}

                    int destRedVal = Color.red(destPixels[destIndex]);
                    destRedVal = Math.min(destRedVal + scopeIntensity, 255);

                    //destPixels[destIndex] = destPixels[destIndex] & 0xff_00_ff_ff;
                    destPixels[destIndex] =  0xff_00_00_00 | destPixels[destIndex] | destRedVal<<16;
                    //green
                    int greenValue = Color.green(pixels.get(j*w+i)); //(sourcePixels[j*sourceWidth+i] % 256) // [0,255]
                    int destIndexgreen = 0;
                    try {
                        destIndexgreen = (destHeight-1-greenValue) * destWidth + destX;
                    }
                    catch (ArithmeticException ex)
                    {

                    }


                    int destGreenVal = Color.green(destPixels[destIndexgreen]);
                    destGreenVal = Math.min(destGreenVal + scopeIntensity, 255);

                    //destPixels[destIndexgreen] = destPixels[destIndexgreen] ;
                    destPixels[destIndexgreen] = 0xff_ff_00_00 | destPixels[destIndexgreen] | (destGreenVal<<8);
                    //blue
                    int blueValue = Color.blue(pixels.get(j*w+i)); //(sourcePixels[j*sourceWidth+i] % 256) // [0,255]
                    int destIndexblue = 0;
                    try {
                        destIndexblue =  (destHeight-1-blueValue) * destWidth + destX;
                    }
                    catch (ArithmeticException ex)
                    {

                    }

                    int destblueVal = Color.blue(destPixels[destIndexblue]);
                    destblueVal = Math.min(destblueVal + scopeIntensity, 255);
                    //destPixels[destIndexblue] = destPixels[destIndexblue] & 0xff_ff_ff_00;
                    destPixels[destIndexblue] =  0xff_ff_ff_00 | destPixels[destIndexblue] | (destblueVal);
                }
            }*/
            histogramController.setWaveFormData(destPixels,destWidth,destHeight);
        }
    }
}
