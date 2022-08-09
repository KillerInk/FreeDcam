package freed.cam.apis.camera2.modules;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.util.Size;

import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.util.Collections;

import freed.FreedApplication;
import freed.cam.apis.camera2.Camera2;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.CameraValuesChangedCaptureCallback;
import freed.cam.apis.camera2.modules.helper.FindOutputHelper;
import freed.cam.apis.camera2.modules.helper.Output;
import freed.cam.apis.camera2.modules.ring.CaptureResultRingBuffer;
import freed.cam.apis.camera2.modules.ring.ImageRingBuffer;
import freed.cam.event.capture.CaptureStates;
import freed.file.holder.BaseHolder;
import freed.image.ImageManager;
import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public abstract class RawZslModuleApi2 extends AbstractModuleApi2{

    protected ImageRingBuffer imageRingBuffer;
    protected CaptureResultRingBuffer captureResultRingBuffer;
    protected ImageReader privateRawImageReader;
    protected ImageManager imageManager;
    protected Output output;
    private final String TAG = RawZslModuleApi2.class.getSimpleName();
    private boolean closed = true;

    private final int STATE_WAIT_FOR_PRECAPTURE = 0;
    private final int STATE_WAIT_FOR_NONPRECAPTURE = 1;
    private final int STATE_PICTURE_TAKEN = 2;
    private int mState = STATE_PICTURE_TAKEN;

    RawZslModuleApi2(Camera2 cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
        imageManager = FreedApplication.imageManager();
    }

    protected int getImageCount()
    {
        return 33;
    }

    @Override
    public void InitModule() {
        super.InitModule();
        closed = false;
        imageRingBuffer =  new ImageRingBuffer(30);
        captureResultRingBuffer = new CaptureResultRingBuffer(30);
        startPreview();
    }


    @Override
    public void DestroyModule() {
        closed = true;
        cameraUiWrapper.captureSessionHandler.StopRepeatingCaptureSession();
        cameraUiWrapper.captureSessionHandler.CloseCaptureSession();
        if (privateRawImageReader != null)
            privateRawImageReader.close();
        privateRawImageReader = null;
        imageRingBuffer.clear();
        imageRingBuffer = null;
        captureResultRingBuffer.clear();
        captureResultRingBuffer = null;

    }

    @Override
    public void internalFireOnWorkDone(BaseHolder file) {
        fireOnWorkFinish(file);
    }

    @Override
    public void startPreview() {
        FindOutputHelper findOutputHelper = new FindOutputHelper();
        output = findOutputHelper.getStockOutput(cameraHolder,settingsManager);
        Size largestImageSize = Collections.max(Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.RAW_SENSOR)), new CameraHolderApi2.CompareSizesByArea());
        output.raw_width = largestImageSize.getWidth();
        output.raw_height = largestImageSize.getHeight();
        cameraUiWrapper.captureSessionHandler.CreateZSLRequestBuilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequest.CONTROL_ENABLE_ZSL,true,false);
        }
        createImageCaptureListners();

        int sensorOrientation = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        Log.d(TAG, "sensorOrientation:" + sensorOrientation);
        int orientationToSet = (360 + sensorOrientation)%360;
        Log.d(TAG, "orientation to set :" +orientationToSet);

        // Here, we create a CameraCaptureSession for camera preview

        Size previewSize = cameraUiWrapper.getSizeForPreviewDependingOnImageSize(ImageFormat.YUV_420_888, output.jpeg_width, output.jpeg_height);

        PictureModuleApi2.preparePreviewTextureView(orientationToSet, previewSize,previewController,settingsManager,TAG,mainHandler,cameraUiWrapper);
        cameraUiWrapper.captureSessionHandler.AddSurface(privateRawImageReader.getSurface(),true);
        //cameraUiWrapper.captureSessionHandler.AddSurface(reprocessImageReader.getSurface(),false);

        cameraUiWrapper.cameraBackroundValuesChangedListner.setCaptureResultRingBuffer(captureResultRingBuffer);

        cameraUiWrapper.captureSessionHandler.CreateCaptureSession();
    }

    protected void createImageCaptureListners()
    {
        privateRawImageReader = ImageReader.newInstance(output.raw_width,output.raw_height, ImageFormat.RAW_SENSOR, getImageCount());
        privateRawImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                try {
                    Image img = reader.acquireLatestImage();
                    if (!closed)
                        imageRingBuffer.offerFirst(img);
                    else img.close();
                }
                catch (NullPointerException ex)
                {
                    Log.e(TAG,"Ringbuffer already closed");
                }

            }
        },mBackgroundHandler);
    }

    @Override
    public void stopPreview() {
        DestroyModule();
    }


    private boolean isContAutoFocus()
    {
        return cameraUiWrapper.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AF_MODE) == CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE ||
                cameraUiWrapper.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AF_MODE) == CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO;
    }

    protected void captureStillPicture()
    {
        changeCaptureState(CaptureStates.image_capture_start);
        cameraUiWrapper.captureSessionHandler.StopRepeatingCaptureSession();
    }

    protected void startPreCapture() {
        mState = (STATE_WAIT_FOR_PRECAPTURE);
        cameraUiWrapper.cameraBackroundValuesChangedListner.setWaitForAe_af_lock(new CameraValuesChangedCaptureCallback.WaitForAe_Af_Lock() {
            @Override
            public void on_Ae_Af_Lock(CameraValuesChangedCaptureCallback.AeAfLocker aeAfLocker) {
                if (mState == STATE_WAIT_FOR_PRECAPTURE) {
                    if (isContAutoFocus()) {
                        if ((aeAfLocker.getAfLock() && aeAfLocker.getAeLock())) {
                            cameraUiWrapper.cameraBackroundValuesChangedListner.setWaitForAe_af_lock(null);
                            mState = (STATE_PICTURE_TAKEN);
                            captureStillPicture();
                        }
                    } else if (aeAfLocker.getAeLock()) {
                        cameraUiWrapper.cameraBackroundValuesChangedListner.setWaitForAe_af_lock(null);
                        mState =(STATE_PICTURE_TAKEN);
                        captureStillPicture();
                    }
                }
            }
        });

        if (cameraUiWrapper.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AE_MODE) != CaptureRequest.CONTROL_AE_MODE_OFF)
            cameraUiWrapper.captureSessionHandler.StartAePrecapture();
        if (isContAutoFocus())
            cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START,false);
        cameraUiWrapper.captureSessionHandler.capture();
    }
}
