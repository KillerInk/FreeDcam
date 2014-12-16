package com.troop.freedcam.camera2.modules;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.ImageReader;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.troop.freedcam.camera.modules.ModuleEventHandler;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.ui.AppSettingsManager;

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
    public void DoWork()
    {
        String[] split = Settings.getString(AppSettingsManager.SETTING_PICTURESIZE).split("x");
        int width = Integer.parseInt(split[0]);
        int height = Integer.parseInt(split[1]);
        mImageReader = ImageReader.newInstance(width,height,
                ImageFormat.JPEG, /*maxImages*/2);
        cameraHolder.SetImageReader(mImageReader);
        mImageReader.setOnImageAvailableListener(
                mOnImageAvailableListener, cameraHolder.mBackgroundHandler);
        final CaptureRequest.Builder captureBuilder;
        try {
            captureBuilder = cameraHolder.mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());




            cameraHolder.mCaptureSession.stopRepeating();
            cameraHolder.mCaptureSession.capture(captureBuilder.build(), CaptureCallback, null);
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
        public void onImageAvailable(ImageReader reader) {
            //mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), mFile));
            Log.d(TAG, "Recieved on onImageAvailabel");
        }

    };
}
