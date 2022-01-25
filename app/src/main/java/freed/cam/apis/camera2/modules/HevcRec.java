package freed.cam.apis.camera2.modules;

import static android.media.MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible;
import static android.media.MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaCodecInfo;
import android.os.Build;
import android.os.Handler;
import android.util.Size;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.Arrays;
import java.util.Collections;

import freed.cam.apis.camera2.Camera2;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.modules.capture.RawImageCapture;
import freed.cam.apis.camera2.modules.record.MediaCodecEncoder;
import freed.cam.apis.camera2.modules.ring.ByteRingBuffer;
import freed.cam.event.capture.CaptureStates;
import freed.file.holder.BaseHolder;
import freed.file.holder.UriHolder;
import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.M)
public class HevcRec extends RawZslModuleApi2{

    private MediaCodecEncoder hevcencoder;
    private boolean isrecording = false;
    int width = 1920;
    int height = 1080;
    private ByteRingBuffer byteRingBuffer;
    private FrameFeeder frameFeeder;

    public HevcRec(Camera2 cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
        name = LongName();
    }

    @Override
    protected int getImageCount() {
        return 38;
    }

    @Override
    public String LongName() {
        return "HevcRecorder";
    }

    @Override
    public String ShortName() {
        return "HevcRec";
    }

    @Override
    public void InitModule() {
        super.InitModule();
        changeCaptureState(CaptureStates.video_recording_stop);
        byteRingBuffer = new ByteRingBuffer(5);
        frameFeeder = new FrameFeeder();
    }

    @Override
    public void DestroyModule() {
        if (isrecording)
            DoWork();
        super.DestroyModule();
        byteRingBuffer.clear();
    }

    @Override
    public void DoWork() {
        if (!isrecording)
        {
            changeCaptureState(CaptureStates.video_recording_start);
            isrecording = true;
            new Thread(frameFeeder).start();


        }
        else {
            isrecording = false;
            changeCaptureState(CaptureStates.video_recording_stop);
        }
    }

    private class FrameFeeder implements Runnable
    {

        private final String TAG = FrameFeeder.class.getSimpleName();

        @Override
        public void run() {
            Log.d(TAG, "start recording");
            String file = fileListController.getNewFilePath(settingsManager.GetWriteExternal(), ".mp4");
            Size largestImageSize = Collections.max(Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.YUV_420_888)), new CameraHolderApi2.CompareSizesByArea());
            configureMediaCodecEncoder(file, width, height, 150000000,25,MediaCodecInfo.CodecProfileLevel.HEVCProfileMain,MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel6);
            while (isrecording) {
                Image img = imageRingBuffer.pollLast();
                TotalCaptureResult result = captureResultRingBuffer.pollLast();
                boolean data_added = false;
                if (img != null) {
                    byte[] data = MediaCodecEncoder.getYUVBuffer(img);
                    img.close();
                    byteRingBuffer.offerFirst(data);
                    data_added = true;
                }
                else
                    data_added = false;
                /*Log.d(TAG, "imageRingBufferSize:" +imageRingBuffer.buffer_size + "/"+ imageRingBuffer.getCurrent_buffer_size() + "/" + imageRingBuffer.size() +"\n"
                        + "byteRingBufferSize:" +byteRingBuffer.getCurrent_buffer_size() + "\n"
                + "image was null = " + data_added);*/
            }
            hevcencoder.stop();
            hevcencoder.release();
            Log.d(TAG, "stopped recording");
        }
    }


    private void configureMediaCodecEncoder(String file, int width, int height, int bitrate, int framerate, int profile, int level) {
        MediaCodecEncoder.Builder builder = new MediaCodecEncoder.Builder();

        builder.setWidth(width)
                .setHeight(height)
                .setBit_rate(bitrate)
                .setFrame_rate(framerate)
                .setI_frame_interval(1)
                .setMime(builder.hvecMime)
                .setColor_format(COLOR_FormatYUV420Flexible)
                .setProfile(profile)
                .setSurfaceMode(false)
                .setLevel(level)
                .setRingBuffer(byteRingBuffer);
        MediaCodecEncoder encoder = builder.build();
        BaseHolder f = fileListController.getNewMovieFileHolder(new File(file));
        if (f instanceof UriHolder) {
            UriHolder uh = (UriHolder) f;
            try {
                encoder.setParcelFileDesciptor(uh.getParcelFileDescriptor());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        hevcencoder = encoder;
        hevcencoder.prepare();
        hevcencoder.start();
    }



    @Override
    protected void createImageCaptureListners()
    {
        if (privateRawImageReader != null)
            privateRawImageReader.close();
        privateRawImageReader = ImageReader.newInstance(width,height, ImageFormat.YUV_420_888, getImageCount());
        privateRawImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                imageRingBuffer.offerFirst(reader.acquireLatestImage());
            }
        },mBackgroundHandler);
    }
}
