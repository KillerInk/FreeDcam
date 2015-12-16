package com.troop.freedcam.camera.modules;

import android.annotation.TargetApi;
import android.media.CamcorderProfile;
import android.media.MediaCodec;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesParameter;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.i_camera.modules.I_RecorderStateChanged;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.manager.MediaScannerManager;
import com.troop.freedcam.ui.AppSettingsManager;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by GeorgeKiarie on 12/16/2015.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class VideoModuleMediaCodec extends AbstractModule {

    String mediaSavePath;
    BaseCameraHolder baseCameraHolder;
    CamParametersHandler camParametersHandler;

    private final String TAG = this.getClass().getName();
    private MediaCodec encoder = null;
    private Surface inputSurface = null;
    private boolean eos = true;  // End-Of-Stream
    private MediaMuxer mediaMuxer = null;
    private int videoTrackIndex;
    private int encodeDuration = 300; // Default encode duration is 300 frames

    // Define encode format
    final String MINE_TYPE = "video/avc";
    final int WIDTH = 1280;
    final int HEIGHT = 720;
    final int BIT_RATE = 1250000;
    final int FRAME_RATE = 30;
    final int COLOR_FORMAT = CodecCapabilities.COLOR_FormatSurface;
    final int I_FRAME_INTERVAL = 5;
    final int CAPTURE_RATE = 30;
    final int REPEAT_PREVIOUS_FRAME_AFTER = (1000/FRAME_RATE);

    // Define muxer format
    final String MUXER_OUTPUT_FILE = "/sdcard/Movies/sampleCameraRecord.mp4";
    final int MUXER_OUTPUT_FORMAT = MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4;

    public VideoModuleMediaCodec(BaseCameraHolder cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
        name  = ModuleHandler.MODULE_VIDEO;
        this.baseCameraHolder = cameraHandler;
        camParametersHandler = (CamParametersHandler) ParameterHandler;
    }


    @Override
    public String ShortName() {
        return "Mov";
    }

    @Override
    public String LongName() {
        return "Movie";
    }

    //I_Module START
    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public void DoWork()
    {
        if (!isWorking)
            startRecording();
        else
            stopRecording();

    }

    @Override
    public boolean IsWorking() {
        return isWorking;
    }
//I_Module END

    private void startRecording()
    {

        init();
        workstarted();

    }

    protected void stopRecording()
    {
        try {

            Log.e(TAG, "Stop Recording");
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Stop Recording failed, was called bevor start");
            baseCameraHolder.errorHandler.OnError("Stop Recording failed, was called bevor start");
            ex.printStackTrace();
        }
        finally
        {

            baseCameraHolder.GetCamera().lock();

            isWorking = false;
            final File file = new File(mediaSavePath);
            MediaScannerManager.ScanMedia(Settings.context.getApplicationContext(), file);
            eventHandler.WorkFinished(file);
            eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);
        }
        workfinished(true);
    }


    public Surface init() {
        //  Set up encode format
        MediaFormat encodeFormat = MediaFormat.createVideoFormat(MINE_TYPE, WIDTH, HEIGHT);
        encodeFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        encodeFormat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        encodeFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, COLOR_FORMAT);
        encodeFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, I_FRAME_INTERVAL);
        encodeFormat.setInteger(MediaFormat.KEY_CAPTURE_RATE, CAPTURE_RATE);
        // KEY_REPEAT_PREVIOUS_FRAME_AFTER is for Surface-Input mode. See createInputSurface().
        encodeFormat.setInteger(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER,
                REPEAT_PREVIOUS_FRAME_AFTER);

        // Create encoder and input surface
        try {
            encoder = MediaCodec.createEncoderByType(MINE_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        encoder.configure(encodeFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        inputSurface = encoder.createInputSurface();

        //encoder.setInputSurface(baseCameraHolder.getSurfaceHolder());

        // Create MdieaMuxer for write encoded data to file
        try {
            mediaMuxer = new MediaMuxer(MUXER_OUTPUT_FILE, MUXER_OUTPUT_FORMAT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputSurface;
    }
    public void start(int duration) {
        if(encoder == null || inputSurface == null) {
            return;
        }
        if (duration > 0) {
            encodeDuration = duration;
        }
        new Thread(new Runnable() {
            public void run() {
                try {
                    encodeTask();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void encodeTask() throws IOException {
        Log.v(TAG, "Start encoder");
        boolean muxerStarted = false;
        encoder.start();
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        eos = false;
        int frameLimit = encodeDuration;
        while (!eos) {
            int outputBufferIndex = encoder.dequeueOutputBuffer(bufferInfo, -1);
            if (outputBufferIndex >= 0) {
                // Encode pre-defined amount of frame
                if(frameLimit > 0) {
                    ByteBuffer[] outputBuffer = encoder.getOutputBuffers();
                    if (!muxerStarted) {
                        MediaFormat format = encoder.getOutputFormat();
                        Log.v(TAG, "Adding video track " + format);
                        videoTrackIndex = mediaMuxer.addTrack(format);
                        Log.v(TAG, "MediaMuxer start");
                        mediaMuxer.start();
                        muxerStarted = true;
                    }
                    ByteBuffer encodedData = outputBuffer[outputBufferIndex];
                    if (encodedData == null) {
                        throw new RuntimeException("encoderOutputBuffer " + outputBufferIndex +
                                " was null");
                    }
                    else
                    {
                        mediaMuxer.writeSampleData(videoTrackIndex, encodedData, bufferInfo);
                        if (--frameLimit == 0) {
                            Log.v(TAG, "MediaMuxer stop");
                            mediaMuxer.stop();
                            muxerStarted = false;
                        }

                    }

                }
                encoder.releaseOutputBuffer(outputBufferIndex, false);
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                Log.v(TAG, "Output buffers changed. API Level > 21 can ignore this.");
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                Log.v(TAG, "Output format change. API Level > 21 can ignore this.");
            } else {
                Log.e(TAG, "Un-defined dequeueOutputBuffer() error");
            }
        }

        if (muxerStarted) {
            Log.v(TAG, "MediaMuxer stop");
            mediaMuxer.stop();
        }
        Log.v(TAG, "Stop encoder");
        encoder.stop();
    }


    @Override
    public void LoadNeededParameters()
    {

        if (ParameterHandler.VideoHDR != null)
            if(Settings.getString(AppSettingsManager.SETTING_VIDEOHDR).equals("on") && ParameterHandler.VideoHDR.IsSupported())
                ParameterHandler.VideoHDR.SetValue("on", true);
        loadProfileSpecificParameters();
    }

    @Override
    public void UnloadNeededParameters() {
        // ParameterHandler.PreviewFormat.SetValue("yuv420sp", true);
        if (ParameterHandler.VideoHDR != null && ParameterHandler.VideoHDR.IsSupported())
            ParameterHandler.VideoHDR.SetValue("off", true);
    }

    private void loadProfileSpecificParameters()
    {
        if (Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE).equals("4kUHD") || Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE).contains("HFR"))
        {
            camParametersHandler.MemoryColorEnhancement.SetValue("disable",true);
            camParametersHandler.DigitalImageStabilization.SetValue("disable", true);
            camParametersHandler.Denoise.SetValue("denoise-off", true);
            camParametersHandler.setString("dual-recorder", "0");
            camParametersHandler.setString("preview-format", "nv12-venus");
        }
        else
        {
            camParametersHandler.setString("preview-format", "yuv420sp");
        }

        if (Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE).equals("4kUHD"))
        {

            camParametersHandler.setString("preview-format", "nv12-venus");

        }


            baseCameraHolder.StopPreview();
            baseCameraHolder.StartPreview();





        }

        // camParametersHandler.UHDDO();
    }