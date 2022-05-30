package freed.cam.apis.camera2.modules;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
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
import freed.cam.apis.camera2.modules.helper.CaptureType;
import freed.cam.apis.camera2.modules.helper.FindOutputHelper;
import freed.cam.apis.camera2.modules.helper.Output;
import freed.cam.apis.camera2.modules.ring.CaptureResultRingBuffer;
import freed.cam.apis.camera2.modules.ring.ImageRingBuffer;
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

    RawZslModuleApi2(Camera2 cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
        imageManager = FreedApplication.imageManager();
    }

    @Override
    public void InitModule() {
        super.InitModule();
        imageRingBuffer =  new ImageRingBuffer();
        captureResultRingBuffer = new CaptureResultRingBuffer();
        startPreview();
    }


    @Override
    public void DestroyModule() {
        cameraUiWrapper.captureSessionHandler.CloseCaptureSession();
        imageRingBuffer.clear();
        captureResultRingBuffer.clear();
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
        privateRawImageReader = ImageReader.newInstance(output.raw_width,output.raw_height, ImageFormat.RAW_SENSOR, 30);
        privateRawImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                imageRingBuffer.addImage(reader.acquireLatestImage());
            }
        },mBackgroundHandler);
    }

    @Override
    public void stopPreview() {
        DestroyModule();
    }
}
