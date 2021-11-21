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
import freed.cam.histogram.HistogramFeed;
import freed.gl.program.ClippingComputeProgram;
import freed.gl.program.FocusPeakComputeProgram;
import freed.gl.program.OesProgram;
import freed.gl.program.PreviewProgram;
import freed.gl.program.WaveFormRGBProgram;
import freed.gl.shader.ClippingComputeShader;
import freed.gl.shader.FocuspeakComputeShader;
import freed.gl.shader.OesFragmentShader;
import freed.gl.shader.OesVertexShader;
import freed.gl.shader.PreviewFragmentShader;
import freed.gl.shader.PreviewVertexShader;
import freed.gl.shader.WaveformRGBShader;
import freed.gl.texture.GL2DTex;
import freed.gl.texture.GLCameraTex;
import freed.gl.texture.GLFrameBuffer;
import freed.utils.Log;

public class MainRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener, HistogramFeed {


    private static final String TAG = MainRenderer.class.getSimpleName();

    private boolean mGLInit = false;
    private boolean mUpdateST = false;

    private final GLPreview mView;

    private GLPreview.PreviewProcessors processors = GLPreview.PreviewProcessors.Normal;

    private final OesProgram oesProgram;
    private final PreviewProgram previewProgram;
    private final WaveFormRGBProgram waveFormRGBProgram;
    private final ClippingComputeProgram clippingComputeProgram;
    private final FocusPeakComputeProgram focusPeakComputeProgram;

    GLCameraTex cameraInputTextureHolder;
    GLFrameBuffer oesFrameBuffer;
    private GL2DTex oesFbTexture;
    GLFrameBuffer focuspeakBuffer;
    GL2DTex focuspeakFbTexture;
    GLFrameBuffer clippingBuffer;
    GL2DTex clippingFbTexture;
    GLFrameBuffer waveformBuffer;
    GL2DTex waveformFbTexture;
    int width;
    int height;
    int pixels[];
    IntBuffer pixelBuffer;
    byte bytepixels[];
    ByteBuffer byteBuffer;

    GL2DTex scaledownTexture;
    GLFrameBuffer scaledownBuffer;

    IntBuffer waveformPixel;

    private int histo_update_counter = 0;

    public MainRenderer(GLPreview view) {
        mView = view;

        oesFrameBuffer = new GLFrameBuffer();
        oesFbTexture = new GL2DTex();

        cameraInputTextureHolder = new GLCameraTex();

        focuspeakBuffer = new GLFrameBuffer();
        focuspeakFbTexture = new GL2DTex();

        clippingBuffer = new GLFrameBuffer();
        clippingFbTexture = new GL2DTex();

        waveformBuffer = new GLFrameBuffer();
        waveformFbTexture = new GL2DTex();

        scaledownBuffer = new GLFrameBuffer();
        scaledownTexture = new GL2DTex();

        int glesv = GlVersion.getGlesVersion();
        oesProgram = new OesProgram(glesv);
        previewProgram = new PreviewProgram(glesv);
        waveFormRGBProgram = new WaveFormRGBProgram(glesv);
        clippingComputeProgram = new ClippingComputeProgram(glesv);
        focusPeakComputeProgram = new FocusPeakComputeProgram(glesv);
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

    private boolean drawing = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onDrawFrame(GL10 unused) {
        if (!mGLInit) return;

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
            previewProgram.draw(oesFbTexture,scaledownBuffer);


            //custom ae
            if (mView.getHistogramController().getMeteringProcessor() != null && mView.getHistogramController().getMeteringProcessor().isMeteringEnabled())
                mView.getHistogramController().getMeteringProcessor().getMeters();

            //histogram and waveform
            if (mView.getHistogramController().isEnabled()) {
                if (histo_update_counter++ == 6) {
                    GLES31.glReadPixels(0, 0, width / 2, height / 2, GLES31.GL_RGBA, GLES31.GL_UNSIGNED_BYTE, pixelBuffer);
                    byteBuffer.asIntBuffer().put(pixels);
                    mView.getHistogramController().setImageData(bytepixels.clone(), width / 2, height / 2);
                }
                if (histo_update_counter == 11)
                {
                    waveFormRGBProgram.draw(oesFbTexture,waveformBuffer);
                    GLES31.glReadPixels(0, height / 3 * 2, width, height / 3, GLES31.GL_RGBA, GLES31.GL_UNSIGNED_BYTE, waveformPixel);
                    mView.getHistogramController().setWaveFormData(waveformPixel.array(), width, height / 3);
                    histo_update_counter = 0;
                }
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
                focusPeakComputeProgram.compute(width,height,oesFbTexture.getId(),focuspeakFbTexture.getId());
                previewProgram.inverseOrientation(false);
                previewProgram.draw(focuspeakFbTexture,null);
                break;
            case Zebra:
                clippingComputeProgram.compute(width,height,oesFbTexture.getId(),clippingFbTexture.getId());
                previewProgram.inverseOrientation(false);
                previewProgram.draw(clippingFbTexture,null);
                break;
            case FocusPeak_Zebra:
                focusPeakComputeProgram.compute(width,height,oesFbTexture.getId(),focuspeakFbTexture.getId());
                clippingComputeProgram.compute(width,height,focuspeakFbTexture.getId(),clippingFbTexture.getId());
                previewProgram.inverseOrientation(false);
                previewProgram.draw(clippingFbTexture,null);
                break;
        }

        if (clippingComputeProgram.getFloat_position() <= 10.0f)
            clippingComputeProgram.setFloat_position(clippingComputeProgram.getFloat_position() +0.05f);
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

        OesVertexShader vertexShader = new OesVertexShader(glesv);
        vertexShader.createShader();

        OesFragmentShader oesFragmentShader = new OesFragmentShader(glesv);
        oesFragmentShader.createShader();

        PreviewVertexShader previewVertexShader = new PreviewVertexShader(glesv);
        previewVertexShader.createShader();

        PreviewFragmentShader previewFragmentShader = new PreviewFragmentShader(glesv);
        previewFragmentShader.createShader();

        ClippingComputeShader clippingComputeShader = new ClippingComputeShader(glesv);
        clippingComputeShader.createShader();

        FocuspeakComputeShader focuspeakComputeShader = new FocuspeakComputeShader(glesv);
        focuspeakComputeShader.createShader();

        Log.d(TAG,"create Oes Program");
        oesProgram.create();
        oesProgram.setFragmentShader(oesFragmentShader);
        oesProgram.setVertexShader(vertexShader);
        oesProgram.createAndLinkProgram();

        Log.d(TAG,"create Preview Program");
        previewProgram.create();
        previewProgram.setFragmentShader(previewFragmentShader);
        previewProgram.setVertexShader(previewVertexShader);
        previewProgram.createAndLinkProgram();

        WaveformRGBShader waveformRGBShader = new WaveformRGBShader(glesv);
        waveformRGBShader.createShader();

        waveFormRGBProgram.create();
        waveFormRGBProgram.setFragmentShader(waveformRGBShader);
        waveFormRGBProgram.setVertexShader(vertexShader);
        waveFormRGBProgram.createAndLinkProgram();

        clippingComputeProgram.setComputeShader(clippingComputeShader);
        clippingComputeProgram.createAndLinkProgram();

        focusPeakComputeProgram.setComputeShader(focuspeakComputeShader);
        focusPeakComputeProgram.createAndLinkProgram();

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

        focuspeakBuffer.create();
        focuspeakFbTexture.create(width,height);
        focuspeakBuffer.setOutputTexture(focuspeakFbTexture);
        Log.d(TAG,"FocuspeakFramebuffer successful:" + focuspeakBuffer.isSuccessfulLoaded());

        clippingBuffer.create();
        clippingFbTexture.create(width,height);
        clippingBuffer.setOutputTexture(clippingFbTexture);
        Log.d(TAG,"ClippingFramebuffer successful:" + clippingBuffer.isSuccessfulLoaded());

        waveformBuffer.create();
        waveformFbTexture.create(width,height);
        waveformBuffer.setOutputTexture(waveformFbTexture);
        Log.d(TAG,"Waveformbuffer successful:" + waveformBuffer.isSuccessfulLoaded());

        int w = width;
        int h = height;
        pixels = new int[w*h];
        pixelBuffer = IntBuffer.wrap(pixels);
        bytepixels = new byte[w*h*4];
        byteBuffer = ByteBuffer.wrap(bytepixels);
        byteBuffer.order(ByteOrder.nativeOrder());

        scaledownBuffer.create();
        scaledownTexture.create(w,h);
        scaledownBuffer.setOutputTexture(scaledownTexture);

        waveformPixel = IntBuffer.allocate(width *(height/3));
        Log.d(TAG,"pixelbuffer isReadOnly: " + pixelBuffer.isReadOnly() + " pixelbuffer isDirect:" + pixelBuffer.isDirect());

    }

    private void closeBuffers()
    {
        oesFrameBuffer.delete();
        oesFbTexture.delete();

        focuspeakBuffer.delete();
        focuspeakFbTexture.delete();

        clippingBuffer.delete();
        clippingFbTexture.delete();

        waveformBuffer.delete();
        waveformFbTexture.delete();

        scaledownBuffer.delete();
        scaledownTexture.delete();
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

    public WaveFormRGBProgram getWaveFormRGBProgram() {
        return waveFormRGBProgram;
    }
    
}
