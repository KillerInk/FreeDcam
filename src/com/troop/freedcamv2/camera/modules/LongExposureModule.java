package com.troop.freedcamv2.camera.modules;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;
import android.os.Handler;
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
    String TAG = "freedcam.LongExposure";


    @Override
    public void DoWork()
    {
        //check if already working if true return
        if (this.isWorking)
            return;
        //set working true
        this.isWorking = true;

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
            baseYuv = data.clone();
        else if (baseYuv != null && mergeYuv == null && doWork && !hasWork)
        {
            mergeYuv = data.clone();
            if (baseYuv != null && mergeYuv != null)
                handler.post(runnable);
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

    private void processYuvFrame() {
        this.hasWork = true;

        if (baseYuv == null )
            return;
        int row = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int a = (baseYuv[row + x] & 0xff);
                int b = (mergeYuv[row + x] & 0xff);
                int c = (a + b)/2;
                baseYuv[row + x] = (byte) (c & 0xFF);
            }
            row += width;
        }
        mergeYuv = null;
        //System.gc();

        this.hasWork = false;
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
