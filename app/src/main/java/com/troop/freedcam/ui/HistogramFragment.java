package com.troop.freedcam.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_Module;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.I_Callbacks;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import troop.com.imageviewer.MyHistogram;

/**
 * Created by George on 3/26/2015.
 */
public class HistogramFragment extends Fragment implements I_Callbacks.PreviewCallback, I_ModuleEvent, I_CameraChangedListner, AbstractModuleHandler.I_worker {

    private AppSettingsManager appSettingsManager;
    AbstractCameraUiWrapper cameraUiWrapper;
    boolean fragmentloaded = false;
    View view;
    MyHistogram histogram;
    private final BlockingQueue<byte[]> mYuvFrameQueue = new ArrayBlockingQueue<byte[]>(2);
    LinearLayout ll;
    I_Activity i_activity;

    boolean doWork = false;
    boolean stoppedOnModuleChange = false;

    int width;
    int height;




    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.histogram_fragment, container, false);
        ll = (LinearLayout)view.findViewById(R.id.histoOverlay);
        histogram = new MyHistogram(container.getContext());
        ll.addView(histogram);
        fragmentloaded = true;
        return view;
    }


    public void SetAppSettings(AppSettingsManager appSettingsManager, I_Activity i_activity)
    {
        this.appSettingsManager = appSettingsManager;
        this.i_activity = i_activity;
    }


    private void extactMutable(byte[] PreviewFrame)
    {

        final YuvImage yuvImage = new YuvImage(PreviewFrame, ImageFormat.NV21,width,height,null);
        if (!doWork)
            return;
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        yuvImage.compressToJpeg(new Rect(0,0,width,height),50,byteArrayOutputStream);

        if (!doWork)
            return;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        options.inMutable = true;
        //options.inJustDecodeBounds = true;
        options.inDither = false ; // Disable Dithering mode
        options.inPurgeable = true ; // Tell to gc that whether it needs free memory, // the Bitmap can be cleared
        options.inInputShareable = true;
        histogram.setBitmap(BitmapFactory.decodeByteArray(byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.size(), options));

    }



    @Override
    public void onPreviewFrame(final byte[] data)
    {
        if (mYuvFrameQueue.size() == 2)
        {
            mYuvFrameQueue.remove();
        }
        mYuvFrameQueue.add(data);
    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        if (cameraUiWrapper == null)
            return;
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
        cameraUiWrapper.SetCameraChangedListner(this);


    }
    public void strtLsn()
    {
        if (cameraUiWrapper == null || cameraUiWrapper.camParametersHandler == null || cameraUiWrapper.camParametersHandler.PreviewSize == null)
            return;
        String[] split = cameraUiWrapper.camParametersHandler.PreviewSize.GetValue().split("x");
        if(split.length < 2)
            return;
        width = Integer.parseInt(split[0]);
        height = Integer.parseInt(split[1]);
        if (cameraUiWrapper != null && cameraUiWrapper.cameraHolder != null && cameraUiWrapper.cameraHolder.isPreviewRunning)
        {
            try {
                cameraUiWrapper.cameraHolder.SetPreviewCallback(this);
            }
            catch (java.lang.RuntimeException ex)
            {
                ex.printStackTrace();
                return;
            }

        }
        else return;
        doWork = true;
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
                            extactMutable(data);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally
                {
                    mYuvFrameQueue.clear();
                    doWork = false;
                }
            }
        }.start();


    }

    void stopLsn()
    {
        doWork = false;
        if (cameraUiWrapper != null && cameraUiWrapper.cameraHolder != null)
            cameraUiWrapper.cameraHolder.SetPreviewCallback(null);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public String ModuleChanged(String module)
    {


        return null;
    }

    @Override
    public void onCameraOpen(String message) {

    }

    @Override
    public void onCameraOpenFinish(String message) {

    }

    @Override
    public void onCameraClose(String message) {

    }

    @Override
    public void onPreviewOpen(String message)
    {
        if (!doWork && isAdded())
            strtLsn();
    }

    @Override
    public void onPreviewClose(String message)
    {
        if (doWork)
            stopLsn();
    }

    @Override
    public void onCameraError(String error) {

    }

    @Override
    public void onCameraStatusChanged(String status) {

    }

    @Override
    public void onModuleChanged(I_Module module) {

    }

    @Override
    public void onWorkStarted()
    {
        if (doWork)
        {
            stoppedOnModuleChange = true;
            stopLsn();
        }
    }

    @Override
    public void onWorkFinished(boolean finished)
    {
        if (stoppedOnModuleChange && isAdded())
        {
            stoppedOnModuleChange = false;
            strtLsn();

        }
    }



}







