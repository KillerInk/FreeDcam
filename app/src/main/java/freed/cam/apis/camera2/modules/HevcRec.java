package freed.cam.apis.camera2.modules;

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
import freed.cam.apis.camera2.modules.record.MediaCodecEncoder;
import freed.file.holder.BaseHolder;
import freed.file.holder.UriHolder;

@RequiresApi(api = Build.VERSION_CODES.M)
public class HevcRec extends RawZslModuleApi2{

    private MediaCodecEncoder hevcencoder;
    private boolean isrecording = false;
    int width = 1920;
    int height = 1080;

    public HevcRec(Camera2 cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
        name = LongName();
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
    }

    @Override
    public void DestroyModule() {
        super.DestroyModule();
    }

    @Override
    public void DoWork() {
        if (!isrecording)
        {
            String file = fileListController.getNewFilePath(settingsManager.GetWriteExternal(), ".mp4");
            Size largestImageSize = Collections.max(Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.YUV_420_888)), new CameraHolderApi2.CompareSizesByArea());
            configureMediaCodecEncoder(file, width, height, 15000000,30,2,2097152);
            new Thread(new FrameFeeder()).start();
        }
        else
        {
            isrecording = false;
        }
    }

    private class FrameFeeder implements Runnable
    {

        @Override
        public void run() {
            while (isrecording) {
                Image img = imageRingBuffer.pollLast();
                TotalCaptureResult result = captureResultRingBuffer.pollLast();
                if (img != null) {
                    byte[] data = getYUVBuffer(img);
                    img.close();
                    hevcencoder.addDATA(data);
                }
            }
            hevcencoder.stop();
            hevcencoder.release();
        }
    }

    private void configureMediaCodecEncoder(String file, int width, int height, int bitrate, int framerate, int profile, int level) {
        MediaCodecEncoder.Builder builder = new MediaCodecEncoder.Builder();

        builder.setWidth(width)
                .setHeight(height)
                .setBit_rate(bitrate)
                .setFrame_rate(framerate)
                .setI_frame_interval(10)
                .setMime(builder.hvecMime)
                .setColor_format(MediaCodecInfo.CodecCapabilities.COLOR_Format24bitBGR888)
                .setProfile(profile)
                .setSurfaceMode(false)
                .setLevel(level);
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

    private static byte[] YUV_420_888toNV21(Image image) {

        int width = image.getWidth();
        int height = image.getHeight();
        int ySize = width*height;
        int uvSize = width*height/4;

        byte[] nv21 = new byte[ySize + uvSize*2];

        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer(); // Y
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer(); // U
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer(); // V

        int rowStride = image.getPlanes()[0].getRowStride();
        assert(image.getPlanes()[0].getPixelStride() == 1);

        int pos = 0;

        if (rowStride == width) { // likely
            yBuffer.get(nv21, 0, ySize);
            pos += ySize;
        }
        else {
            int yBufferPos = -rowStride; // not an actual position
            for (; pos<ySize; pos+=width) {
                yBufferPos += rowStride;
                yBuffer.position(yBufferPos);
                yBuffer.get(nv21, pos, width);
            }
        }

        rowStride = image.getPlanes()[2].getRowStride();
        int pixelStride = image.getPlanes()[2].getPixelStride();

        assert(rowStride == image.getPlanes()[1].getRowStride());
        assert(pixelStride == image.getPlanes()[1].getPixelStride());

        if (pixelStride == 2 && rowStride == width && uBuffer.get(0) == vBuffer.get(1)) {
            // maybe V an U planes overlap as per NV21, which means vBuffer[1] is alias of uBuffer[0]
            byte savePixel = vBuffer.get(1);
            try {
                vBuffer.put(1, (byte)~savePixel);
                if (uBuffer.get(0) == (byte)~savePixel) {
                    vBuffer.put(1, savePixel);
                    vBuffer.position(0);
                    uBuffer.position(0);
                    vBuffer.get(nv21, ySize, 1);
                    uBuffer.get(nv21, ySize + 1, uBuffer.remaining());

                    return nv21; // shortcut
                }
            }
            catch (ReadOnlyBufferException ex) {
                // unfortunately, we cannot check if vBuffer and uBuffer overlap
            }

            // unfortunately, the check failed. We must save U and V pixel by pixel
            vBuffer.put(1, savePixel);
        }

        // other optimizations could check if (pixelStride == 1) or (pixelStride == 2),
        // but performance gain would be less significant

        for (int row=0; row<height/2; row++) {
            for (int col=0; col<width/2; col++) {
                int vuPos = col*pixelStride + row*rowStride;
                nv21[pos++] = vBuffer.get(vuPos);
                nv21[pos++] = uBuffer.get(vuPos);
            }
        }

        return nv21;
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

    @Override
    protected void createImageCaptureListners()
    {
        privateRawImageReader = ImageReader.newInstance(width,height, ImageFormat.YUV_420_888, getImageCount());
        privateRawImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                imageRingBuffer.addImage(reader.acquireLatestImage());
            }
        },mBackgroundHandler);
    }
}
