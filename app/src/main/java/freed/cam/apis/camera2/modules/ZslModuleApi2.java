package freed.cam.apis.camera2.modules;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.media.ImageWriter;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.camera2.Camera2;
import freed.cam.apis.camera2.modules.helper.CaptureType;
import freed.cam.apis.camera2.modules.helper.FindOutputHelper;
import freed.cam.apis.camera2.modules.helper.Output;
import freed.cam.apis.camera2.modules.zsl.ZslCaptureResultRingBuffer;
import freed.cam.apis.camera2.modules.zsl.ZslImageRingBuffer;
import freed.cam.apis.camera2.modules.zsl.ZslRingBuffer;
import freed.cam.event.capture.CaptureStates;
import freed.file.holder.BaseHolder;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.M)
public class ZslModuleApi2 extends AbstractModuleApi2{

    private final String TAG = ZslModuleApi2.class.getSimpleName();
    private String picFormat;
    protected Output output;
    private CaptureType captureType;
    private ZslImageRingBuffer imageZslRingBuffer;
    private ZslCaptureResultRingBuffer captureResultRingBuffer;
    private ImageReader privateRawImageReader;
    private ImageReader reprocessImageReader;
    private ImageWriter imageWriter;

    public ZslModuleApi2(Camera2 cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
    }

    @Override
    public void InitModule() {
        super.InitModule();
        changeCaptureState(CaptureStates.image_capture_stop);
        imageZslRingBuffer =  new ZslImageRingBuffer();
        captureResultRingBuffer = new ZslCaptureResultRingBuffer();
        startPreview();
    }

    @Override
    public void DestroyModule() {
        cameraUiWrapper.captureSessionHandler.CloseCaptureSession();
        previewController.close();
        imageZslRingBuffer.clear();
        captureResultRingBuffer.clear();
    }

    @Override
    public String LongName() {
        return "ZslCapture";
    }

    @Override
    public String ShortName() {
        return "ZSL";
    }

    @Override
    public void DoWork() {
        takePicture();
    }

    private void takePicture()
    {
        TotalCaptureResult result = captureResultRingBuffer.getLatest();
        Image img = imageZslRingBuffer.getLatest();
        Log.d(TAG,"image:" + img.getTimestamp() + " result:" + result.get(CaptureResult.SENSOR_TIMESTAMP));
        CaptureRequest.Builder request = cameraUiWrapper.captureSessionHandler.createReprocessRequest(result);
        request.set(CaptureRequest.CONTROL_ENABLE_ZSL,true);
        Log.d (TAG,"is reporcess:" +request.build().isReprocess());
        request.addTarget(reprocessImageReader.getSurface());

        imageWriter.queueInputImage(img);
        CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                super.onCaptureStarted(session, request, timestamp, frameNumber);
            }

            @Override
            public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
                super.onCaptureProgressed(session, request, partialResult);
            }

            @Override
            public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                super.onCaptureCompleted(session, request, result);
            }

            @Override
            public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
                super.onCaptureFailed(session, request, failure);
            }

            @Override
            public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
                super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
            }

            @Override
            public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
                super.onCaptureSequenceAborted(session, sequenceId);
            }

            @Override
            public void onCaptureBufferLost(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
                super.onCaptureBufferLost(session, request, target, frameNumber);
            }
        };
        cameraUiWrapper.captureSessionHandler.captureReprocess(request.build(),captureCallback);
    }

    @Override
    public void internalFireOnWorkDone(BaseHolder file) {

    }


    @Override
    public void startPreview() {
        picFormat = settingsManager.get(SettingKeys.PictureFormat).get();
        if (TextUtils.isEmpty(picFormat)) {
            picFormat = FreedApplication.getStringFromRessources(R.string.pictureformat_jpeg);
            settingsManager.get(SettingKeys.PictureFormat).set(picFormat);
            parameterHandler.get(SettingKeys.PictureFormat).fireStringValueChanged(picFormat);
        }
        FindOutputHelper findOutputHelper = new FindOutputHelper();
        if (settingsManager.getFrameWork() == Frameworks.HuaweiCamera2Ex)
        {
            output = findOutputHelper.getHuaweiOutput(cameraHolder,settingsManager);
        }
        else {
            output = findOutputHelper.getStockOutput(cameraHolder,settingsManager);
        }


        Log.d(TAG, "ImageReader JPEG");
        captureType = PictureModuleApi2.getCaptureType(picFormat,TAG);
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

        cameraUiWrapper.cameraBackroundValuesChangedListner.setZslCaptureResultRingBuffer(captureResultRingBuffer);

        cameraUiWrapper.captureSessionHandler.CreateReprocessCaptureSession(output.raw_width, output.raw_height, ImageFormat.PRIVATE);


    }



    private void createImageCaptureListners() {
        privateRawImageReader = ImageReader.newInstance(output.raw_width,output.raw_height, ImageFormat.PRIVATE, ZslRingBuffer.buffer_size+1);
        privateRawImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                imageZslRingBuffer.addImage(reader.acquireLatestImage());
            }
        },mBackgroundHandler);

        reprocessImageReader = ImageReader.newInstance(output.raw_width,output.raw_height,ImageFormat.JPEG,ZslRingBuffer.buffer_size+1);
        reprocessImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image img = reader.acquireLatestImage();
                Log.d(TAG,"Image reprocessed" + img.getFormat());
                img.close();
            }
        },mBackgroundHandler);
        imageWriter = ImageWriter.newInstance(reprocessImageReader.getSurface(), ZslRingBuffer.buffer_size+1,ImageFormat.JPEG);
    }

    @Override
    public void stopPreview() {
        DestroyModule();
    }
}
