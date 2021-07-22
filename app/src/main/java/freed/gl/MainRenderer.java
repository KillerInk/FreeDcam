package freed.gl;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import freed.cam.histogram.HistogramChangedEvent;
import freed.cam.histogram.HistogramFeed;
import freed.gl.program.ClippingProgram;
import freed.gl.program.FocuspeakProgram;
import freed.gl.program.MergeProgram;
import freed.gl.program.OesProgram;
import freed.gl.program.PreviewProgram;
import freed.gl.program.WaveFormRGBProgram;
import freed.gl.shader.ClippingShader;
import freed.gl.shader.FocuspeakShader;
import freed.gl.shader.MergeShader;
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
    private final FocuspeakProgram focuspeakProgram;
    private final ClippingProgram clippingProgram;
    private final PreviewProgram previewProgram;
    private final MergeProgram mergeProgram;
    private final WaveFormRGBProgram waveFormRGBProgram;

    GLCameraTex cameraInputTextureHolder;
    GLFrameBuffer oesFrameBuffer;
    private GL2DTex oesFbTexture;
    GLFrameBuffer focuspeakBuffer;
    GL2DTex focuspeakFbTexture;
    GLFrameBuffer clippingBuffer;
    GL2DTex clippingFbTexture;
    int width;
    int height;
    int pixels[];
    IntBuffer pixelBuffer;
    byte bytepixels[];
    ByteBuffer byteBuffer;

    public MainRenderer(GLPreview view) {
        mView = view;

        oesFrameBuffer = new GLFrameBuffer();
        oesFbTexture = new GL2DTex();

        cameraInputTextureHolder = new GLCameraTex();

        focuspeakBuffer = new GLFrameBuffer();
        focuspeakFbTexture = new GL2DTex();

        clippingBuffer = new GLFrameBuffer();
        clippingFbTexture = new GL2DTex();

        int glesv = GlVersion.getGlesVersion();
        oesProgram = new OesProgram(glesv);
        focuspeakProgram = new FocuspeakProgram(glesv);
        clippingProgram = new ClippingProgram(glesv);
        previewProgram = new PreviewProgram(glesv);
        mergeProgram = new MergeProgram(glesv);
        waveFormRGBProgram = new WaveFormRGBProgram(glesv);
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

    public void onDrawFrame(GL10 unused) {
        if (!mGLInit) return;

        drawing = true;
        if (mUpdateST)
        {
            try {
                cameraInputTextureHolder.getSurfaceTexture().updateTexImage();
            }
            catch (RuntimeException ex)
            {
                Log.WriteEx(ex);
            }
            finally {
                mUpdateST = false;
            }
        }
        oesFrameBuffer.setActive();
        oesProgram.setGlTex(cameraInputTextureHolder);

        oesProgram.draw();

        if (mView.getHistogramController().isEnabled()) {
            GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, pixelBuffer);
            byteBuffer.asIntBuffer().put(pixels);
            mView.getHistogramController().setImageData(bytepixels.clone(), width, height);
        }
        focuspeakBuffer.setActive();
        //workaround for orientation. draw normal preview first in focuspeakbuffer
        //if we would draw from oesbuffer orientation would be inversed
        if (processors == GLPreview.PreviewProcessors.Normal)
        {
            previewProgram.setGlTex(oesFbTexture);
            previewProgram.draw();
        }
        else if (processors == GLPreview.PreviewProcessors.FocusPeak || processors == GLPreview.PreviewProcessors.FocusPeak_Zebra)
        {
            focuspeakProgram.setGlTex(oesFbTexture);
            focuspeakProgram.draw();
        }
        clippingBuffer.setActive();
        if (processors == GLPreview.PreviewProcessors.Zebra || processors == GLPreview.PreviewProcessors.FocusPeak_Zebra)
        {
            clippingProgram.setGlTex(oesFbTexture);
            clippingProgram.draw();
        }
        oesFrameBuffer.switchToDefaultFB();

        switch (processors)
        {
            case Normal:
            case FocusPeak:
                previewProgram.doClear();
                previewProgram.setGlTex(focuspeakFbTexture);
                previewProgram.draw();
                break;
            case Zebra:
                previewProgram.doClear();
                previewProgram.setGlTex(clippingFbTexture);
                previewProgram.draw();
                break;
            case FocusPeak_Zebra:
                mergeProgram.doClear();
                mergeProgram.setGlTex(clippingFbTexture);
                mergeProgram.setGlTex2(focuspeakFbTexture);
                mergeProgram.draw();
                break;
        }

        if (clippingProgram.getFloat_position() <= 10.0f)
            clippingProgram.setFloat_position(clippingProgram.getFloat_position() +0.05f);
        else
            clippingProgram.setFloat_position(0);
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

        FocuspeakShader focuspeakShader = new FocuspeakShader(glesv);
        focuspeakShader.createShader();

        ClippingShader clippingShader = new ClippingShader(glesv);
        clippingShader.createShader();

        MergeShader mergeShader = new MergeShader(glesv);
        mergeShader.createShader();

        Log.d(TAG,"create Oes Program");
        oesProgram.create();
        oesProgram.setFragmentShader(oesFragmentShader);
        oesProgram.setVertexShader(vertexShader);
        oesProgram.createAndLinkProgram();

        Log.d(TAG,"create Focuspeak Program");
        focuspeakProgram.create();
        focuspeakProgram.setFragmentShader(focuspeakShader);
        focuspeakProgram.setVertexShader(vertexShader);
        focuspeakProgram.createAndLinkProgram();

        Log.d(TAG,"create Clipping Program");
        clippingProgram.create();
        clippingProgram.setFragmentShader(clippingShader);
        clippingProgram.setVertexShader(vertexShader);
        clippingProgram.createAndLinkProgram();

        Log.d(TAG,"create Preview Program");
        previewProgram.create();
        previewProgram.setFragmentShader(previewFragmentShader);
        previewProgram.setVertexShader(previewVertexShader);
        previewProgram.createAndLinkProgram();

        mergeProgram.create();
        mergeProgram.setFragmentShader(mergeShader);
        mergeProgram.setVertexShader(vertexShader);
        mergeProgram.createAndLinkProgram();

        WaveformRGBShader waveformRGBShader = new WaveformRGBShader(glesv);
        waveformRGBShader.createShader();
        waveFormRGBProgram.create();
        waveFormRGBProgram.setFragmentShader(waveformRGBShader);
        waveFormRGBProgram.setVertexShader(previewVertexShader);
        waveFormRGBProgram.createAndLinkProgram();

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

        pixels = new int[width*height];
        pixelBuffer = IntBuffer.wrap(pixels);
        bytepixels = new byte[width*height*4];
        byteBuffer = ByteBuffer.wrap(bytepixels);
        byteBuffer.order(ByteOrder.nativeOrder());

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


    public FocuspeakProgram getFocuspeakProgram() {
        return focuspeakProgram;
    }

    public ClippingProgram getClippingProgram()
    {
        return clippingProgram;
    }

    public PreviewProgram getPreviewProgram() {
        return previewProgram;
    }

    @Override
    public void setHistogramFeed(HistogramChangedEvent feed) {

    }
}
