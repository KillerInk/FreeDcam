package freed.cam.apis.camera2.modules.record;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.camera2.CaptureResult;
import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.concurrent.LinkedBlockingQueue;

import freed.cam.apis.basecamera.record.IRecorder;
import freed.cam.apis.camera2.modules.ring.ByteRingBuffer;
import freed.cam.apis.camera2.modules.ring.CaptureResultRingBuffer;
import freed.cam.apis.camera2.modules.ring.ImageRingBuffer;
import freed.cam.apis.camera2.modules.ring.RingBuffer;
import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MediaCodecEncoder implements IRecorder {

    private static final String TAG = MediaCodecEncoder.class.getSimpleName();
    private Surface surface;

    private FileOutputStream fileOutputStream;
    private MediaCodec codec;
    private MediaFormat mediaFormat;
    ParcelFileDescriptor parcelFileDesciptor;
    /*private ProcessOutputJob processOutputJob;
    private ProcessInputJob processInputJob;*/
    private Builder builder;


    private MediaCodecEncoder(Builder builder)
    {
        this.builder = builder;
        Log.d(TAG, "create mime:" + builder.mime +" size:"+builder.width+"/"+builder.height + " colorformat:" + builder.color_format + " fps:" + builder.frame_rate + " bps:" + builder.bit_rate);
    }

    public void setParcelFileDesciptor(ParcelFileDescriptor parcelFileDesciptor)
    {
        this.parcelFileDesciptor = parcelFileDesciptor;
        fileOutputStream = new FileOutputStream(parcelFileDesciptor.getFileDescriptor());
    }

    //call this only after prepare and bevor start
    @Override
    public Surface getSurface()
    {
        return surface;
    }

    @Override
    public boolean prepare() {
        boolean prep = true;
        MediaCodecInfo mediaCodecInfo = getMediaCodecInfo(builder.mime);
        Log.d(TAG, "codecInfo:" + mediaCodecInfo.toString());
        try {
            codec = MediaCodec.createByCodecName(mediaCodecInfo.getName());
        } catch (IOException e) {
            e.printStackTrace();
            prep = false;
        }

        codec.setCallback(new MediaCodec.Callback() {
            @Override
            public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
                if (!builder.surfaceMode) {
                    try {

                        if (builder.imageRingBuffer.getCurrent_buffer_size() == 0)
                            synchronized (builder.imageRingBuffer)
                            {
                                try {
                                    builder.imageRingBuffer.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        byte[] newdata = builder.imageRingBuffer.pollLast();
                        if (newdata != null) {
                            ByteBuffer data = codec.getInputBuffer(index);
                            if (data != null) {
                                data.clear();
                                data.put(newdata);
                                codec.queueInputBuffer(index, 0, newdata.length, System.currentTimeMillis(), 0);
                                Log.d(TAG, "put frame to input");
                            }
                            else
                                Log.d(TAG, "mediacodec input buffer is null");
                        }
                        else
                        {
                            ByteBuffer data = codec.getInputBuffer(index);
                            if (data != null) {
                                data.clear();
                                codec.queueInputBuffer(index, 0, 0, 0, 0);
                                Log.d(TAG, "put frame to input");
                            }
                            Log.d(TAG, "inputdata is null");
                        }
                    }
                    catch (IllegalStateException ex)
                    {
                        Log.WriteEx(ex);
                    }
                }
            }

            @Override
            public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {

                try {
                    ByteBuffer data = codec.getOutputBuffer(index);
                    if (data != null) {
                        final int endOfStream = info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM;
                        if (endOfStream == 0)
                            writeOutputBufferToFile(info, data);
                        codec.releaseOutputBuffer(index, false);
                        Log.d(TAG, "wrote to output");
                    }
                }
                catch (IllegalStateException ex)
                {
                    Log.WriteEx(ex);
                }
            }

            @Override
            public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
                Log.WriteEx(e);
            }

            @Override
            public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {

            }
        });
        mediaFormat = getMediaFormat();

        codec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        MediaFormat outformat = codec.getOutputFormat();
        MediaFormat informat = codec.getInputFormat();

        if (builder.surfaceMode)
            surface = codec.createInputSurface();
        return prep;
    }

    private void writeOutputBufferToFile(MediaCodec.BufferInfo info, ByteBuffer outputBuffer) {
        byte[] outData = new byte[info.size];
        if (outputBuffer != null) {
            outputBuffer.get(outData);
            try {
                fileOutputStream.write(outData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start()
    {
        codec.start();
    }

    @Override
    public void stop()
    {
        codec.stop();
        synchronized (builder.imageRingBuffer)
        {
            builder.imageRingBuffer.notifyAll();
        }
    }

    @Override
    public void release()
    {
        if (surface != null)
            surface.release();
    }


    private MediaFormat getMediaFormat()
    {
        MediaFormat format = MediaFormat.createVideoFormat(builder.mime, builder.width, builder.height);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, builder.color_format);
        format.setInteger(MediaFormat.KEY_BIT_RATE, builder.bit_rate);
        format.setFloat(MediaFormat.KEY_FRAME_RATE, builder.frame_rate);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, builder.i_frame_interval);
        format.setString(MediaFormat.KEY_MIME,builder.mime);
        //format.setInteger(MediaFormat.KEY_CAPTURE_RATE,builder.frame_rate);
        //format.setInteger(MediaFormat.KEY_MAX_FPS_TO_ENCODER,builder.frame_rate);

        format.setInteger(MediaFormat.KEY_COLOR_RANGE,MediaFormat.COLOR_RANGE_FULL);
        format.setInteger(MediaFormat.KEY_COLOR_STANDARD,MediaFormat.COLOR_STANDARD_BT601_PAL);
        format.setInteger(MediaFormat.KEY_COLOR_TRANSFER,MediaFormat.COLOR_TRANSFER_HLG);

        //format.setInteger(MediaFormat.KEY_OPERATING_RATE,builder.frame_rate);

        if (builder.profile > -1)
            format.setInteger(MediaFormat.KEY_PROFILE,builder.profile);
        if (builder.level > -1)
            format.setInteger(MediaFormat.KEY_LEVEL,builder.level);
        return format;
    }

    private MediaCodecInfo getMediaCodecInfo(String mime)
    {
        MediaCodecInfo mediaCodecInfos[] = new MediaCodecList(MediaCodecList.REGULAR_CODECS).getCodecInfos();
        for (MediaCodecInfo i : mediaCodecInfos) {
            if (i.isEncoder()) {
                String types[] = i.getSupportedTypes();
                for (String t : types)
                    if (t.equals(mime))
                        return i;
            }
        }
        return null;
    }

    public static class Builder
    {
        private String mime;
        private int width;
        private int height;
        private int color_format;
        private int frame_rate;
        private int i_frame_interval;
        private int bit_rate;
        private int profile;
        private int level;
        private ByteRingBuffer imageRingBuffer;

        private boolean surfaceMode = true;
        /* <li>"video/x-vnd.on2.vp8" - VP8 video (i.e. video in .webm)
         * <li>"video/x-vnd.on2.vp9" - VP9 video (i.e. video in .webm)
         * <li>"video/avc" - H.264/AVC video
         * <li>"video/hevc" - H.265/HEVC video
         * <li>"video/mp4v-es" - MPEG4 video
         * <li>"video/3gpp" - H.263 video
         * <li>"audio/3gpp" - AMR narrowband audio
         * <li>"audio/amr-wb" - AMR wideband audio
         * <li>"audio/mpeg" - MPEG1/2 audio layer III
         * <li>"audio/mp4a-latm" - AAC audio (note, this is raw AAC packets, not packaged in LATM!)
         * <li>"audio/vorbis" - vorbis audio
         * <li>"audio/g711-alaw" - G.711 alaw audio
         * <li>"audio/g711-mlaw" - G.711 ulaw audio*/
        public final String hvecMime = MediaFormat.MIMETYPE_VIDEO_HEVC;

        public Builder setMime(String mime)
        {
            this.mime = mime;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setBit_rate(int bit_rate) {
            this.bit_rate = bit_rate;
            return this;
        }

        public Builder setColor_format(int color_format) {
            this.color_format = color_format;
            return this;
        }

        public Builder setFrame_rate(int frame_rate) {
            this.frame_rate = frame_rate;
            return this;
        }

        public Builder setI_frame_interval(int i_frame_interval) {
            this.i_frame_interval = i_frame_interval;
            return this;
        }

        public Builder setProfile(int profile) {
            this.profile = profile;
            return this;
        }

        public Builder setLevel(int level) {
            this.level = level;
            return this;
        }

        public Builder setSurfaceMode(boolean surfaceMode) {
            this.surfaceMode = surfaceMode;
            return this;
        }

        public Builder setRingBuffer(ByteRingBuffer imageRingBuffer)
        {
            this.imageRingBuffer = imageRingBuffer;
            return  this;
        }

        public MediaCodecEncoder build()
        {
            return new MediaCodecEncoder(this);
        }
    }


    public static byte[] YUV_420_888toNV21(Image image) {

        int width = image.getWidth();
        int height = image.getHeight();
        int ySize = width*height;
        int uvSize = width*height/4;

        byte[] nv21 = new byte[ySize + uvSize*2];

        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer(); // Y
        ByteBuffer vBuffer = image.getPlanes()[1].getBuffer(); // U
        ByteBuffer uBuffer = image.getPlanes()[2].getBuffer(); // V

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

    public static byte[] getYUVBuffer(Image image) {
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
