package com.troop.freedcam.camera.modules;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.i_camera.modules.I_Callbacks;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.yuv.Merge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by troop on 27.10.2014.
 */
public class LongExposureModule extends AbstractModule implements I_Callbacks.PreviewCallback
{
    BaseCameraHolder baseCameraHolder;
    private final BlockingQueue<byte[]> mYuvFrameQueue = new ArrayBlockingQueue<byte[]>(2);

    public LongExposureModule(BaseCameraHolder cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
        name = ModuleHandler.MODULE_LONGEXPO;
        exposureModule = this;

        this.baseCameraHolder = cameraHandler;
    }

    private class YuvHolder
    {
        public int y;
        public int u;
        public int v;
    }

    YuvHolder yuvIntHolder[];

    LongExposureModule exposureModule;
    //if true the the preview frames are grabed
    boolean doWork = false;
    //if true a preview frame merge is in progress
    boolean hasWork = false;
    //stores the basyuv data wich get merged with the other frames
    //byte[] baseYuv;
    //stores the actual frame to merge
    //byte[] mergeYuv;
    //preview size height
    int height;
    //preview size width
    int width;
    //handler to process the merge
    Handler handler;

    private static String TAG = "freedcam.LongExposure";
    int count;

    Merge nativeYuvMerge;

    @Override
    public String ShortName() {
        return "LoEx";
    }

    @Override
    public String LongName() {
        return "Long Exposure";
    }

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
        String[] split = baseCameraHolder.ParameterHandler.PreviewSize.GetValue().split("x");

        width = Integer.parseInt(split[0]);
        height = Integer.parseInt(split[1]);
        if (nativeYuvMerge == null)
            nativeYuvMerge = new Merge();

        //start listen to the previewcallback
        baseCameraHolder.SetPreviewCallback(this);
        //enable frame listing for the callback
        doWork = true;
        //set baseyuv to null to start a new merge
        //baseYuv = null;
        // get the exposure duration
        int time = Integer.parseInt(Settings.getString(AppSettingsManager.SETTING_EXPOSURELONGTIME));
        handler = new Handler();
        //post the runnable after wich time it should stop grabbing the preview frames
        handler.postDelayed(runnableFinishWork, time*1000);
        workstarted();
        new Thread() {
            @Override
            public void run()
            {
                byte[] data = null;
                try {
                    while (doWork)
                    {
                        data = mYuvFrameQueue.take();
                        if (data != null)
                            processYuvFrame(data);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally
                {
                    mYuvFrameQueue.clear();
                }
            }
        }.start();

    }

    @Override
    public void onPreviewFrame(final byte[] data, int imageFormat)
    {
        if (mYuvFrameQueue.size() == 2)
        {
            mYuvFrameQueue.remove();
        }
        mYuvFrameQueue.add(data);

    }

    //this runs when the time is gone and stops listen to the preview and convert then the yuv data into an bitmap and saves it
    Runnable runnableFinishWork = new Runnable() {
        @Override
        public void run()
        {
            if (count == 0)
                return;
            exposureModule.doWork = false;
            //wait until the last frame is saved
            while (exposureModule.hasWork) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //remove the previewcallback
            baseCameraHolder.SetPreviewCallback(null);

            File file = createFilename();
            if (!file.exists())
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            OutputStream outStream = null;
            try
            {
                outStream = new FileOutputStream(file);
                YuvImage img  = new YuvImage(nativeYuvMerge.GetMergedYuv(count), ImageFormat.NV21, width, height, null);
                img.compressToJpeg(new Rect(0, 0, width, height), 100, outStream);
                //outStream.write(mergeYuv);
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

            nativeYuvMerge.Release();

            Log.d(TAG, "Work done");
            eventHandler.WorkFinished(file);
            exposureModule.isWorking = false;
            System.gc();
            workfinished(true);
        }
    };


    private void processYuvFrame(byte[] mergeYuv) {
        this.hasWork = true;
        Log.d(TAG, "StartProcessingFrame");
        if (mergeYuv == null)
            return;

        if (count == 0)
        {
            Log.d(TAG, "Processing first frame");
            nativeYuvMerge.AddFirstYuvFrame(mergeYuv, width, height);
            Log.d(TAG, "Processing first frame done");
        }
        else
        {
            Log.d(TAG, "Processing next frame");
            nativeYuvMerge.AddNextYuvFrame(mergeYuv);
            Log.d(TAG, "Processing next frame done");
        }

        count++;
        Log.d(TAG, "Frame Processed:" + count);

        this.hasWork = false;
        baseCameraHolder.SetPreviewCallback(this);
    }


    private File createFilename() {
        Log.d(TAG,"Create FileName");
        String pictureFormat = ParameterHandler.PictureFormat.GetValue();
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/FreeCam/");
        if(!file.exists())
            file.mkdirs();
        Date date = new Date();
        String s = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(date);
        String s1 = (new StringBuilder(String.valueOf(file.getPath()))).append(File.separator).append("IMG_").append(s).toString();
        return new File((new StringBuilder(String.valueOf(s1))).append(".jpg").toString());
    }

    @Override
    public void LoadNeededParameters()
    {
        if (ParameterHandler.PreviewFormat != null && !ParameterHandler.PreviewFormat.GetValue().equals("yuv420sp"))
            ParameterHandler.PreviewFormat.SetValue("yuv420sp",true);

    }

    @Override
    public void UnloadNeededParameters()
    {

    }
}
