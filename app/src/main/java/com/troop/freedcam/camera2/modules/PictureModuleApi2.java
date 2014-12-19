package com.troop.freedcam.camera2.modules;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.troop.freedcam.camera.modules.ModuleEventHandler;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.ui.AppSettingsManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static android.hardware.camera2.CameraCaptureSession.CaptureCallback;

/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class PictureModuleApi2 extends AbstractModuleApi2
{
    final static String TAG = PictureModuleApi2.class.getSimpleName();
    BaseCameraHolderApi2 cameraHolder;
    /**
     * An {@link android.media.ImageReader} that handles still image capture.
     */
    private ImageReader mImageReader;


    public PictureModuleApi2(BaseCameraHolderApi2 cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
        this.cameraHolder = (BaseCameraHolderApi2)cameraHandler;
        name = ModuleHandler.MODULE_PICTURE;
    }

    @Override
    public String LongName() {
        return "Picture";
    }

    @Override
    public String ShortName() {
        return "Pic";
    }

    @Override
    public void DoWork()
    {
        /*get pic size*/
        String[] split = Settings.getString(AppSettingsManager.SETTING_PICTURESIZE).split("x");
        int width = Integer.parseInt(split[0]);
        int height = Integer.parseInt(split[1]);
        //create new ImageReader with the size and format for the image
        mImageReader = ImageReader.newInstance(width,height,
                ImageFormat.RAW10, /*maxImages*/2);
        //this returns the image data finaly
        mImageReader.setOnImageAvailableListener(
                mOnImageAvailableListener, null);

        final CaptureRequest.Builder captureBuilder;
        try {
            // create a new capture requestbuilder for image caputre
            captureBuilder = cameraHolder.mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            //add the imagerader surface to the capture that it gets the image data stored
            captureBuilder.addTarget(mImageReader.getSurface());
            //tell the cameradevice to create a new capturesession
            cameraHolder.mCameraDevice.createCaptureSession(Arrays.asList(mImageReader.getSurface()),new CameraCaptureSession.StateCallback()
            {
                //if the capture session is sucessfull configured
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {

                        //start the capture and wait for the mOnImageAvailableListener.callback
                        //the capturecallback tells only that the capturesession has finished
                        session.capture(captureBuilder.build(), CaptureCallback, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    CameraCaptureSession.CaptureCallback CaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                       TotalCaptureResult result) {
            //Toast.makeText(getActivity(), "Saved: " + mFile, Toast.LENGTH_SHORT).show();
            //unlockFocus();
            Log.d(TAG, "Recieved on capturecompleted");
        }
    };

    @Override
    public void LoadNeededParameters()
    {
        super.LoadNeededParameters();
    }

    @Override
    public void UnloadNeededParameters() {
        super.UnloadNeededParameters();
    }

    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader)
        {
            File file = new File(getStringAddTime() +".jpg");
            new ImageSaver(reader.acquireNextImage(), file).run();
            //mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), mFile));
            Log.d(TAG, "Recieved on onImageAvailabel");
            cameraHolder.StartPreview();
        }

    };

    protected String getStringAddTime()
    {
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/FreeCam/");
        if (!file.exists())
            file.mkdirs();
        Date date = new Date();
        String s = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(date);
        return (new StringBuilder(String.valueOf(file.getPath()))).append(File.separator).append("IMG_").append(s).toString();
    }

    /**
     * Saves a JPEG {@link android.media.Image} into the specified {@link File}.
     */
    private static class ImageSaver implements Runnable {

        /**
         * The JPEG image
         */
        private final Image mImage;
        /**
         * The file we save the image into.
         */
        private final File mFile;

        public ImageSaver(Image image, File file) {
            mImage = image;
            mFile = file;
        }

        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
