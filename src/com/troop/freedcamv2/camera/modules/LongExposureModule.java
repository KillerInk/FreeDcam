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
    boolean doWork = false;
    boolean hasWork = false;
    YuvImage baseYuv;
    int height;
    int width;
    String TAG = "freedcam.LongExposure";


    @Override
    public void DoWork() {
        width = baseCameraHolder.ParameterHandler.PreviewSize.GetWidth();
        height = baseCameraHolder.ParameterHandler.PreviewSize.GetHeight();
        baseCameraHolder.SetPreviewCallback(this);
        doWork = true;
        baseYuv = null;

        Runnable runnable = new Runnable() {
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
                    baseYuv.compressToJpeg(new Rect(0,0,width,height), 100, outStream);
                    outStream.flush();
                    outStream.close();

                    System.gc();
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                //baseYuv = null;
                eventHandler.WorkFinished(file);
            }
        };
        int time = Integer.parseInt(Settings.getString(AppSettingsManager.SETTING_EXPOSURELONGTIME));
        Handler handler = new Handler();
        handler.postDelayed(runnable, time*1000);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (doWork && !hasWork && data != null)
        {
            processYuvFrame(data);


        }

    }

    private void processYuvFrame(byte[] bytes) {
        this.hasWork = true;
        int pixelsize = (bytes.length / height) / width;
        if (baseYuv == null)
            baseYuv = new YuvImage(bytes, ImageFormat.NV21, width, height, null);
        else
        {

            YuvImage img = new YuvImage(bytes, ImageFormat.NV21, width, height, null);
            if (baseYuv == null )
                return;
            int row = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int a = baseYuv.getYuvData()[row + x] & 0xff;
                    int b = img.getYuvData()[row + x] & 0xff;
                    int c = (a + b) / 2;
                    baseYuv.getYuvData()[row + x] = (byte) (c & 0xFF);
                }
                row += width;
            }
            img = null;
            //System.gc();
            Log.d(TAG, "ProcessYuvFrame");
        }

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
