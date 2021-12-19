package freed.gl;

import android.graphics.SurfaceTexture;
import android.opengl.GLES31;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import freed.cam.histogram.HistogramChangedEvent;
import freed.cam.histogram.HistogramData;
import freed.cam.histogram.HistogramFeed;
import freed.gl.program.compute.AvgLumaComputeProgram;
import freed.gl.program.compute.ClippingComputeProgram;
import freed.gl.program.compute.FocusPeakComputeProgram;
import freed.gl.program.compute.HistogramComputeProgram;
import freed.gl.program.compute.WaveformComputeProgam;
import freed.gl.program.draw.OesProgram;
import freed.gl.program.draw.PreviewProgram;
import freed.gl.shader.Shader;
import freed.gl.shader.compute.AvgLumaComputeShader;
import freed.gl.shader.compute.ClippingComputeShader;
import freed.gl.shader.compute.FocuspeakComputeShader;
import freed.gl.shader.compute.HistogramShader;
import freed.gl.shader.compute.WaveformComputeShader;
import freed.gl.shader.fragment.OesFragmentShader;
import freed.gl.shader.vertex.OesVertexShader;
import freed.gl.shader.fragment.PreviewFragmentShader;
import freed.gl.shader.vertex.PreviewVertexShader;
import freed.gl.texture.GL2DTex;
import freed.gl.texture.GLCameraTex;
import freed.gl.texture.GLFrameBuffer;
import freed.gl.texture.SharedStorageBufferObject;

import freed.utils.Log;

public class MainRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener, HistogramFeed {


    private static final String TAG = MainRenderer.class.getSimpleName();

    private final int groupfactor = 8;
    private final int waveform_factor = 8;

    private boolean mGLInit = false;
    private boolean mUpdateST = false;
    private boolean drawing = false;

    private final GLPreview mView;

    private GLPreview.PreviewProcessors processors = GLPreview.PreviewProcessors.Normal;

    private final OesProgram oesProgram;
    private final PreviewProgram previewProgram;
    private final ClippingComputeProgram clippingComputeProgram;
    private final FocusPeakComputeProgram focusPeakComputeProgram;
    private final HistogramComputeProgram histogramComputeProgram;
    private final WaveformComputeProgam waveformComputeProgam;
    private final AvgLumaComputeProgram avgLumaComputeProgram;

    GLCameraTex cameraInputTextureHolder;
    GLFrameBuffer oesFrameBuffer;
    private GL2DTex oesFbTexture;
    GLFrameBuffer processingBuffer1;
    GL2DTex processingTexture1;
    SharedStorageBufferObject histogramR_SSBO;
    SharedStorageBufferObject histogramG_SSBO;
    SharedStorageBufferObject histogramB_SSBO;
    SharedStorageBufferObject waveform_SSBO;
    SharedStorageBufferObject avgLuma_SSBO;
    int width;
    int height;


    public MainRenderer(GLPreview view) {
        mView = view;

        oesFrameBuffer = new GLFrameBuffer();
        oesFbTexture = new GL2DTex();

        cameraInputTextureHolder = new GLCameraTex();

        processingBuffer1 = new GLFrameBuffer();
        processingTexture1 = new GL2DTex();

       histogramR_SSBO = new SharedStorageBufferObject();
       histogramG_SSBO = new SharedStorageBufferObject();
       histogramB_SSBO = new SharedStorageBufferObject();
       waveform_SSBO = new SharedStorageBufferObject();
       avgLuma_SSBO = new SharedStorageBufferObject();

        int glesv = GlVersion.getGlesVersion();
        oesProgram = new OesProgram(glesv);
        previewProgram = new PreviewProgram(glesv);
        clippingComputeProgram = new ClippingComputeProgram(glesv);
        focusPeakComputeProgram = new FocusPeakComputeProgram(glesv);
        histogramComputeProgram = new HistogramComputeProgram(glesv);
        waveformComputeProgam = new WaveformComputeProgam(glesv);
        avgLumaComputeProgram = new AvgLumaComputeProgram(glesv);
    }

    public void setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public void setProgram(GLPreview.PreviewProcessors processors)
    {
        this.processors = processors;
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onDrawFrame(GL10 unused) {
        if (!mGLInit) return;
        if (drawing) return;

        drawing = true;
        if (mUpdateST) {
            try {
                cameraInputTextureHolder.getSurfaceTexture().updateTexImage();
            } catch (RuntimeException ex) {
                Log.WriteEx(ex);
            } finally {
                mUpdateST = false;
            }
        }
        oesProgram.draw(cameraInputTextureHolder,oesFrameBuffer);

        if ((mView.getHistogramController().getMeteringProcessor() != null
                && mView.getHistogramController().getMeteringProcessor().isMeteringEnabled())
                || mView.getHistogramController().isEnabled())
        {
            //draw
            previewProgram.draw(oesFbTexture,processingBuffer1);
            //custom ae
            if (mView.getHistogramController().getMeteringProcessor() != null
                    && mView.getHistogramController().getMeteringProcessor().isMeteringEnabled()) {
                avgLuma_SSBO.clearBuffer();
                avgLumaComputeProgram.compute(width/16/4,height/16/4,oesFrameBuffer,avgLuma_SSBO);
                int l[] = avgLuma_SSBO.getHistogramChannel();
                float luma = (float)l[0] / 1000000f;
                mView.getHistogramController().getMeteringProcessor().setLuma(luma);
            }

            //histogram and waveform
            if (mView.getHistogramController().isEnabled()) {
                histogramComputeProgram.computeFB(width/16,height/16,oesFrameBuffer,histogramR_SSBO,histogramG_SSBO,histogramB_SSBO);
                int red[] = histogramR_SSBO.getHistogramChannel();
                int green[] = histogramG_SSBO.getHistogramChannel();
                int blue[] = histogramB_SSBO.getHistogramChannel();

                HistogramData data = new HistogramData(red,green,blue);
                mView.getHistogramController().updateData(data);
                histogramR_SSBO.clearBuffer();
                histogramG_SSBO.clearBuffer();
                histogramB_SSBO.clearBuffer();

                waveformComputeProgam.compute(width/64,height/waveform_factor,oesFrameBuffer,waveform_SSBO);
                int wave[] = waveform_SSBO.getHistogramChannel();
                mView.getHistogramController().setWaveFormData(wave, width, height/waveform_factor);
            }
        }

        GLFrameBuffer.switchToDefaultFB();
        switch (processors)
        {
            case Normal:
                previewProgram.inverseOrientation(false);
                previewProgram.doClear();
                previewProgram.draw(oesFbTexture,null);
                break;
            case FocusPeak:
                focusPeakComputeProgram.compute(width/groupfactor,height/groupfactor,oesFbTexture.getId(), processingTexture1.getId());
                previewProgram.inverseOrientation(false);
                previewProgram.draw(processingTexture1,null);
                break;
            case Zebra:
                clippingComputeProgram.compute(width/groupfactor,height/groupfactor,oesFbTexture.getId(),processingTexture1.getId());
                previewProgram.inverseOrientation(false);
                previewProgram.draw(processingTexture1,null);
                break;
            case FocusPeak_Zebra:
                focusPeakComputeProgram.compute(width/groupfactor,height/groupfactor,oesFbTexture.getId(), processingTexture1.getId());
                clippingComputeProgram.compute(width/groupfactor,height/groupfactor, processingTexture1.getId(),oesFbTexture.getId());
                previewProgram.inverseOrientation(false);
                previewProgram.draw(oesFbTexture,null);
                break;
        }

        if (clippingComputeProgram.getFloat_position() <= 1000)
            clippingComputeProgram.setFloat_position(clippingComputeProgram.getFloat_position() +1);
        else
            clippingComputeProgram.setFloat_position(0);
        drawing = false;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        Log.d(TAG, "onSurface created " +width + "/" + height);

        createBuffers();
        cameraInputTextureHolder.create(width,height);
        int glesv = GlVersion.getGlesVersion();
        Log.d(TAG, "GlesVersion:" + glesv);

        Shader vertexShader = new OesVertexShader(glesv);
        histogramComputeProgram.setComputeShader(new HistogramShader(glesv));
        new WaveformComputeShader(glesv);

        oesProgram.create(vertexShader, new OesFragmentShader(glesv));
        previewProgram.create(new PreviewVertexShader(glesv),new PreviewFragmentShader(glesv));
        clippingComputeProgram.setComputeShader(new ClippingComputeShader(glesv));
        focusPeakComputeProgram.setComputeShader(new FocuspeakComputeShader(glesv));
        waveformComputeProgam.setComputeShader( new WaveformComputeShader(glesv));
        avgLumaComputeProgram.setComputeShader(new AvgLumaComputeShader(glesv));

        cameraInputTextureHolder.getSurfaceTexture().setOnFrameAvailableListener(this);
        mGLInit = true;
        mView.fireOnSurfaceTextureAvailable(cameraInputTextureHolder.getSurfaceTexture(),0,0);
    }

    private void createBuffers()
    {
        oesFrameBuffer.create();
        oesFbTexture.create(width,height);
        oesFrameBuffer.setOutputTexture(oesFbTexture);
        Log.d(TAG,"OesFramebuffer successful:" + oesFrameBuffer.isSuccessfulLoaded());

        processingBuffer1.create();
        processingTexture1.create(width,height);
        processingBuffer1.setOutputTexture(processingTexture1);
        Log.d(TAG,"FocuspeakFramebuffer successful:" + processingBuffer1.isSuccessfulLoaded());

        histogramR_SSBO.create(1,256);
        histogramG_SSBO.create(2,256);
        histogramB_SSBO.create(3,256);
        waveform_SSBO.create(1,height/waveform_factor* width);
        avgLuma_SSBO.create(1,1);

    }

    private void closeBuffers()
    {
        oesFrameBuffer.delete();
        oesFbTexture.delete();

        processingBuffer1.delete();
        processingTexture1.delete();
        histogramR_SSBO.delete();
        histogramG_SSBO.delete();
        histogramB_SSBO.delete();
        waveform_SSBO.delete();
        avgLuma_SSBO.delete();
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        Log.d(TAG, "onSurface changed " +width + "/" + height);
        this.width = width;
        this.height = height;
        if (drawing) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        closeBuffers();
        createBuffers();
        if (mView.getHistogramController().getMeteringProcessor() != null)
            mView.getHistogramController().getMeteringProcessor().setSize(width,height);
        GLES30.glViewport(0, 0, width, height);
    }


    public SurfaceTexture getmSTexture()
    {
        if (cameraInputTextureHolder != null)
            return cameraInputTextureHolder.getSurfaceTexture();
        return null;
    }


    public synchronized void onFrameAvailable(SurfaceTexture st) {
        mUpdateST = true;
        mView.requestRender();
    }


    public FocusPeakComputeProgram getFocuspeakProgram() {
        return focusPeakComputeProgram;
    }

    public ClippingComputeProgram getClippingProgram()
    {
        return clippingComputeProgram;
    }

    public PreviewProgram getPreviewProgram() {
        return previewProgram;
    }

    @Override
    public void setHistogramFeed(HistogramChangedEvent feed) {

    }

    public WaveformComputeProgam getWaveFormRGBProgram() {
        return waveformComputeProgam;
    }



}
