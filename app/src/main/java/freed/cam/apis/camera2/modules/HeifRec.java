package freed.cam.apis.camera2.modules;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Size;

import androidx.annotation.RequiresApi;
import androidx.heifwriter.HeifWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import freed.cam.apis.camera2.Camera2;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.modules.helper.FindOutputHelper;
import freed.cam.event.capture.CaptureStates;
import freed.file.holder.BaseHolder;
import freed.file.holder.UriHolder;
import freed.utils.Log;
import freed.utils.StorageFileManager;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class HeifRec extends RawZslModuleApi2{

    private static final String TAG = "HeifRec";
    int orientationToSet;
    private HeifRecRunne heifRecRunner;

    public HeifRec(Camera2 cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
        name = "HeifRec";
    }

    @Override
    public String LongName() {
        return "Heif Recording";
    }

    @Override
    public String ShortName() {
        return "HeifRec";
    }

    @Override
    public void DoWork() {
        if (!heifRecRunner.recording) {
            changeCaptureState(CaptureStates.video_recording_start);
            new Thread(heifRecRunner).start();
            Log.d(TAG,"start Recording");
        }
        else {
            changeCaptureState(CaptureStates.video_recording_stop);
            heifRecRunner.recording = false;
            Log.d(TAG,"stop Recording");
        }
    }

    @Override
    public void InitModule() {
        super.InitModule();
        heifRecRunner = new HeifRecRunne();
    }

    @Override
    public void DestroyModule() {
        super.DestroyModule();
    }

    @Override
    public void startPreview() {
        FindOutputHelper findOutputHelper = new FindOutputHelper();
        output = findOutputHelper.getStockOutput(cameraHolder,settingsManager);
        Size largestImageSize = Collections.max(Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.YUV_420_888)), new CameraHolderApi2.CompareSizesByArea());
        output.raw_width = largestImageSize.getWidth();
        output.raw_height = largestImageSize.getHeight();
        cameraUiWrapper.captureSessionHandler.CreateZSLRequestBuilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequest.CONTROL_ENABLE_ZSL,true,false);
        }
        createImageCaptureListners();

        int sensorOrientation = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        Log.d(TAG, "sensorOrientation:" + sensorOrientation);
        orientationToSet = (360 + sensorOrientation)%360;
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
        privateRawImageReader = ImageReader.newInstance(output.jpeg_width,output.jpeg_height, ImageFormat.YUV_420_888, getImageCount());
        privateRawImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                imageRingBuffer.offerFirst(reader.acquireLatestImage());
            }
        },mBackgroundHandler);
    }

    private  class HeifRecRunne implements  Runnable
    {
        private boolean recording = false;
        private int quality = 100;


        @Override
        public void run() {
            recording = true;
            Date date = new Date();
            String name = StorageFileManager.getStringDatePAttern().format(date);
            File file = new File(fileListController.getNewFilePath((name + "_HeifRec"), ".heic"));
            BaseHolder fileholder = fileListController.getNewImgFileHolder(file);
            ParcelFileDescriptor pfd = null;
            try {
                pfd = ((UriHolder)fileholder).getParcelFileDescriptor();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            HeifWriter.Builder  builder = new HeifWriter.Builder(pfd.getFileDescriptor(),output.jpeg_width,output.jpeg_height,HeifWriter.INPUT_MODE_BUFFER);
            builder.setQuality(quality);
            builder.setRotation(orientationToSet);
            builder.setMaxImages(60);
            HeifWriter writer = null;
            try {
                writer = builder.build();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (writer == null)
                throw new NullPointerException("Heif writer is null!");
            writer.start();
            while (recording)
            {
                Image image = imageRingBuffer.pollLast();
                CaptureResult result = captureResultRingBuffer.pollLast();

                if (image == null) {
                    Log.d(TAG, "image null");
                    return;
                }
                byte bytes[] = getYUVBuffer(image);
                if (bytes == null) {
                    Log.d(TAG,"Buffer null or not an array");
                    image.close();
                    return;
                }
                Log.d(TAG,"write frame");
                writer.addYuvBuffer(ImageFormat.YUV_420_888, bytes);
                image.close();
            }
            try {
                writer.stop(20);
            } catch (Exception e) {
                e.printStackTrace();
            }
            writer.close();
        }

        private byte[] getYUVBuffer(Image image) {
            if (image.getFormat() != ImageFormat.YUV_420_888) {
                throw new IllegalArgumentException("Format not support!");
            }
            Rect crop = image.getCropRect();
            int format = image.getFormat();
            int width = crop.width();
            int height = crop.height();
            Image.Plane[] planes = image.getPlanes();
            byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
            byte[] rowData = new byte[planes[0].getRowStride()];
            int channelOffset = 0;
            int outputStride = 1;
            for (int i = 0; i < planes.length; i++) {
                switch (i) {
                    case 0:
                        channelOffset = 0;
                        outputStride = 1;
                        break;
                    case 1:
                        channelOffset = width * height + 1;
                        outputStride = 2;
                        break;
                    case 2:
                        channelOffset = width * height;
                        outputStride = 2;
                        break;
                }
                ByteBuffer buffer = planes[i].getBuffer();
                int rowStride = planes[i].getRowStride();
                int pixelStride = planes[i].getPixelStride();
                int shift = (i == 0) ? 0 : 1;
                int w = width >> shift;
                int h = height >> shift;
                buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
                for (int row = 0; row < h; row++) {
                    int length;
                    if (pixelStride == 1 && outputStride == 1) {
                        length = w;
                        buffer.get(data, channelOffset, length);
                        channelOffset += length;
                    } else {
                        length = (w - 1) * pixelStride + 1;
                        buffer.get(rowData, 0, length);
                        for (int col = 0; col < w; col++) {
                            data[channelOffset] = rowData[col * pixelStride];
                            channelOffset += outputStride;
                        }
                    }
                    if (row < h - 1) {
                        buffer.position(buffer.position() + rowStride - length);
                    }
                }
            }
            return data;
        }
    }
}
