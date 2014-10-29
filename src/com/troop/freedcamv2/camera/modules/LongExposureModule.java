package com.troop.freedcamv2.camera.modules;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.troop.freedcamv2.camera.BaseCameraHolder;
import com.troop.freedcamv2.ui.AppSettingsManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by troop on 27.10.2014.
 */
public class LongExposureModule extends AbstractModule implements Camera.PreviewCallback {
    public LongExposureModule(BaseCameraHolder cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
        name = ModuleHandler.MODULE_LONGEXPO;
        exposureModule = this;
        frameThread = new HandlerThread(TAG);
        frameThread.start();
        frameHandler = new Handler(frameThread.getLooper());
    }

    LongExposureModule exposureModule;
    //if true the the preview frames are grabed
    boolean doWork = false;
    //if true a preview frame merge is in progress
    boolean hasWork = false;
    //stores the basyuv data wich get merged with the other frames
    byte[] baseYuv;
    //stores the actual frame to merge
    byte[] mergeYuv;
    //preview size height
    int height;
    //preview size width
    int width;
    //handler to process the merge
    Handler handler;
    Handler frameHandler;
    HandlerThread frameThread;
    static String TAG = "freedcam.LongExposure";
    int count;


    @Override
    public void DoWork()
    {
        //check if already working if true return
        if (this.isWorking)
            return;
        //set working true
        this.isWorking = true;
        count = 0;
        //get width and height from the preview
        width = baseCameraHolder.ParameterHandler.PreviewSize.GetWidth();
        height = baseCameraHolder.ParameterHandler.PreviewSize.GetHeight();
        //start listen to the previewcallback
        baseCameraHolder.SetPreviewCallback(this);
        //enable frame listing for the callback
        doWork = true;
        //set baseyuv to null to start a new merge
        baseYuv = null;
        // get the exposure duration
        int time = Integer.parseInt(Settings.getString(AppSettingsManager.SETTING_EXPOSURELONGTIME));
        handler = new Handler();
        //post the runnable after wich time it should stop grabbing the preview frames
        handler.postDelayed(runnableFinishWork, time*1000);

    }

    @Override
    public void onPreviewFrame(final byte[] data, Camera camera)
    {
        //if base yuv null a new
        if (baseYuv == null)
            baseYuv = data;
        else if (baseYuv != null && doWork && !hasWork)
        {
            mergeYuv = data;
            if (baseYuv != null && mergeYuv != null)
            {
                baseCameraHolder.GetCamera().setPreviewCallback(null);
                new Thread(runnable).start();
            }
        }
    }

    //this runs when the time is gone and stops listen to the preview and convert then the yuv data into an bitmap and saves it
    Runnable runnableFinishWork = new Runnable() {
        @Override
        public void run()
        {
            exposureModule.doWork = false;
            while (exposureModule.hasWork) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //baseCameraHolder.SetPreviewCallback(null);
            File file = createFilename();
            OutputStream outStream = null;
            try
            {
                outStream = new FileOutputStream(file);
                YuvImage img  = new YuvImage(baseYuv, ImageFormat.NV21, width, height, null);
                img.compressToJpeg(new Rect(0, 0, width, height), 100, outStream);
                outStream.flush();
                outStream.close();
                img = null;
                //System.gc();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            baseCameraHolder.GetCamera().setPreviewCallback(null);
            baseYuv = null;

            eventHandler.WorkFinished(file);
            exposureModule.isWorking = false;
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run()
        {
             processYuvFrame();
        }
    };


    //Y = image[ w * y + x];
    //U = image[ w * y + floor(y/2) * (w/2) + floor(x/2) + 1]
    //V = image[ w * y + floor(y/2) * (w/2) + floor(x/2) + 0]
    private void processYuvFrame() {
        this.hasWork = true;
        Log.d(TAG, "StartProcessingFrame");
        if (baseYuv == null)
            return;
        int row = 0;

        int frameSize = width * height;

        /*for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int yPos = y * width + x;

                //baseYuv[y * width + x] = baseYuv[y * width + x] + mergeYuv[y * width + x];
                baseYuv[yPos] = (byte) (((baseYuv[yPos] & 0xff) + (mergeYuv[yPos] & 0xff)) / 2 & 0xFF);
                //i++;
            }

        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int uPos = (width * height) + (y * width) + x;
                int vPos = (width * height) + (y * width) + (x + 1);
                baseYuv[uPos] = (byte) (((baseYuv[uPos] & 0xff) + (mergeYuv[uPos] & 0xff)) / 2 & 0xFF);
                baseYuv[vPos] = (byte) (((baseYuv[vPos] & 0xff) + (mergeYuv[vPos] & 0xff)) / 2 & 0xFF);

            }
        }*/

        /*for (int j = 0, yp = 0; j < height; j++)
        {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0, ub =0, vb=0, startuvp =0;
            for (int i = 0; i < width; i++, yp++)
            {
                int y = (0xff & ((int) baseYuv[yp])) - 16;
                int yb = (0xff & ((int) mergeYuv[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0)
                {
                    startuvp = uvp;
                    v = (0xff & baseYuv[uvp]) - 128;
                    vb= (0xff & mergeYuv[uvp++]) - 128;
                    u = (0xff & baseYuv[uvp]) - 128;
                    ub = (0xff & mergeYuv[uvp++]) - 128;
                }

            }
        }*/

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {


                //baseYuv[row + x] = (byte) (((baseYuv[row + x] & 0xff) + (mergeYuv[row + x] & 0xff))/2 & 0xFF);
                int yPos = y * width + x;
                int uPos = (y/2)*(width/2)+(x/2) + frameSize;
                int vPos = (y/2)*(width/2)+(x/2) + frameSize + (frameSize/4);
                baseYuv[yPos] = (byte) (((baseYuv[yPos] & 0xff) + (mergeYuv[yPos] & 0xff))/2 & 0xFF);
                baseYuv[uPos] = (byte) (((baseYuv[uPos] & 0xff) + (mergeYuv[uPos] & 0xff))/2 & 0xFF);
                baseYuv[vPos] = (byte) (((baseYuv[vPos] & 0xff) + (mergeYuv[vPos] & 0xff))/2 & 0xFF);
            }
            //row += width;
        }
        count++;
        Log.d(TAG, "Frame Processed:" + count);
        mergeYuv = null;
        //System.gc();

        this.hasWork = false;
        baseCameraHolder.GetCamera().setPreviewCallback(this);
    }

    public static int byteArrayToInt(byte[] b) {
        final ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getInt();
    }

    public static byte[] intToByteArray(int i) {
        final ByteBuffer bb = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(i);
        return bb.array();
    }

    private File createFilename() {
        Log.d(TAG,"Create FileName");
        String pictureFormat = baseCameraHolder.ParameterHandler.PictureFormat.GetValue();
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/FreeCam/");
        if(!file.exists())
            file.mkdirs();
        Date date = new Date();
        String s = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(date);
        String s1 = (new StringBuilder(String.valueOf(file.getPath()))).append(File.separator).append("IMG_").append(s).toString();
        return new File((new StringBuilder(String.valueOf(s1))).append(".jpg").toString());
    }
}
