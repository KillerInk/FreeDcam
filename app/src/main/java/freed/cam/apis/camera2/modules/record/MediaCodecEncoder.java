package freed.cam.apis.camera2.modules.record;

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
import java.util.concurrent.LinkedBlockingQueue;

import freed.cam.apis.basecamera.record.IRecorder;
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
    private LinkedBlockingQueue<byte[]> queue;


    private MediaCodecEncoder(Builder builder)
    {
        this.builder = builder;
        if (!builder.surfaceMode) {
            queue = new LinkedBlockingQueue<>();
            //processInputJob = new ProcessInputJob();
        }
        //processOutputJob = new ProcessOutputJob();
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


    public void addDATA(byte[] data)
    {
        queue.offer(data);
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
                        ByteBuffer data = codec.getInputBuffer(index);
                        byte[] newdata = queue.poll();
                        if (data != null && newdata != null) {
                            //data.clear();
                            data.put(newdata);
                            codec.queueInputBuffer(index, 0, newdata.length, 1000, 0);
                            Log.d(TAG, "put frame to input");
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

                ByteBuffer data = codec.getOutputBuffer(index);
                if (data != null) {
                    final int endOfStream = info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM;
                    if (endOfStream == 0)
                        writeOutputBufferToFile(info, data);
                    codec.releaseOutputBuffer(index, false);
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
    }

    @Override
    public void release()
    {
        if (surface != null)
            surface.release();
        if (queue != null)
            queue.clear();
    }


    private MediaFormat getMediaFormat()
    {
        MediaFormat format = MediaFormat.createVideoFormat(builder.mime, builder.width, builder.height);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, builder.color_format);
        format.setInteger(MediaFormat.KEY_BIT_RATE, builder.bit_rate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, builder.frame_rate);
        //format.setInteger(MediaFormat.KEY_CAPTURE_RATE,builder.frame_rate);
        //format.setInteger(MediaFormat.KEY_MAX_FPS_TO_ENCODER,builder.frame_rate);
        format.setString(MediaFormat.KEY_MIME,builder.mime);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, builder.i_frame_interval);
        //format.setInteger(MediaFormat.KEY_OPERATING_RATE,builder.frame_rate);
        /*if (builder.profile > -1)
            format.setInteger(MediaFormat.KEY_PROFILE,builder.profile);
        if (builder.level > -1)
            format.setInteger(MediaFormat.KEY_LEVEL,builder.level);*/
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

        public MediaCodecEncoder build()
        {
            return new MediaCodecEncoder(this);
        }
    }

    private class ProcessOutputJob implements Runnable
    {
        private boolean mRunning = false;
        @Override
        public void run() {
            mRunning = true;
            MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
            while (mRunning) {
                try {
                    int status = codec.dequeueOutputBuffer(mBufferInfo, 10000l);
                    if (status == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        if (!mRunning) break;
                    }
                    else if (status >= 0) {
                        // encoded sample
                        ByteBuffer data = codec.getOutputBuffer(status);
                        if (data != null) {
                            final int endOfStream = mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM;
                            // pass to whoever listens to
                            if (endOfStream == 0) writeOutputBufferToFile(mBufferInfo, data);
                            // releasing buffer is important
                            codec.releaseOutputBuffer(status, false);
                            if (endOfStream == MediaCodec.BUFFER_FLAG_END_OF_STREAM) break;
                        }
                    }
                }
                catch (IllegalStateException e)
                {
                    Log.WriteEx(e);
                }

            }
            mRunning = false;
            if (builder.surfaceMode)
                codec.signalEndOfInputStream();
            codec.stop();
        }
    }

    private class ProcessInputJob implements Runnable
    {
        private boolean mRunning = false;
        @Override
        public void run() {
            mRunning = true;
            while (mRunning) {
                try {
                    int status = codec.dequeueInputBuffer(10000l);
                    if (status == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        if (!mRunning) break;
                    }
                    else if (status >= 0) {
                        ByteBuffer data = codec.getInputBuffer(status);
                        byte[] newdata = queue.poll();
                        if (data != null && newdata != null) {
                            data.clear();
                            data.put(newdata);
                            codec.queueInputBuffer(status,0,newdata.length,1000,0);
                            Log.d(TAG, "put frame to input");
                        }
                    }
                }
                catch (IllegalStateException e)
                {
                    Log.WriteEx(e);
                }

            }
            mRunning = false;
        }
    }
}
