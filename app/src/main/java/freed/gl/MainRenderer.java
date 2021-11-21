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
import freed.gl.program.compute.ClippingComputeProgram;
import freed.gl.program.compute.FocusPeakComputeProgram;
import freed.gl.program.draw.OesProgram;
import freed.gl.program.draw.PreviewProgram;
import freed.gl.program.draw.WaveFormRGBProgram;
import freed.gl.shader.Shader;
import freed.gl.shader.compute.ClippingComputeShader;
import freed.gl.shader.compute.FocuspeakComputeShader;
import freed.gl.shader.fragment.OesFragmentShader;
import freed.gl.shader.vertex.OesVertexShader;
import freed.gl.shader.fragment.PreviewFragmentShader;
import freed.gl.shader.vertex.PreviewVertexShader;
import freed.gl.shader.fragment.WaveformRGBShader;
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
    GLFrameBuffer processingBuffer1;
    GL2DTex processingTexture1;
    int width;
    int height;
    int pixels[];
    IntBuffer pixelBuffer;
    byte bytepixels[];
    ByteBuffer byteBuffer;


    IntBuffer waveformPixel;

    private int histo_update_counter = 0;

    public MainRenderer(GLPreview view) {
        mView = view;

        oesFrameBuffer = new GLFrameBuffer();
        oesFbTexture = new GL2DTex();

        cameraInputTextureHolder = new GLCameraTex();

        processingBuffer1 = new GLFrameBuffer();
        processingTexture1 = new GL2DTex();

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
            //draw
            previewProgram.draw(oesFbTexture,processingBuffer1);
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
                    waveFormRGBProgram.draw(oesFbTexture,processingBuffer1);
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
                focusPeakComputeProgram.compute(width,height,oesFbTexture.getId(), processingTexture1.getId());
                previewProgram.inverseOrientation(false);
                previewProgram.draw(processingTexture1,null);
                break;
            case Zebra:
                clippingComputeProgram.compute(width,height,oesFbTexture.getId(),processingTexture1.getId());
                previewProgram.inverseOrientation(false);
                previewProgram.draw(processingTexture1,null);
                break;
            case FocusPeak_Zebra:
                focusPeakComputeProgram.compute(width,height,oesFbTexture.getId(), processingTexture1.getId());
                clippingComputeProgram.compute(width,height, processingTexture1.getId(),oesFbTexture.getId());
                previewProgram.inverseOrientation(false);
                previewProgram.draw(oesFbTexture,null);
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

        Shader vertexShader = new OesVertexShader(glesv);

        oesProgram.create(vertexShader, new OesFragmentShader(glesv));
        previewProgram.create(new PreviewVertexShader(glesv),new PreviewFragmentShader(glesv));
        waveFormRGBProgram.create(vertexShader,new WaveformRGBShader(glesv));
        clippingComputeProgram.setComputeShader(new ClippingComputeShader(glesv));
        focusPeakComputeProgram.setComputeShader(new FocuspeakComputeShader(glesv));

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

        int w = width;
        int h = height;
        pixels = new int[w*h];
        pixelBuffer = IntBuffer.wrap(pixels);
        bytepixels = new byte[w*h*4];
        byteBuffer = ByteBuffer.wrap(bytepixels);
        byteBuffer.order(ByteOrder.nativeOrder());

        waveformPixel = IntBuffer.allocate(width *(height/3));
        Log.d(TAG,"pixelbuffer isReadOnly: " + pixelBuffer.isReadOnly() + " pixelbuffer isDirect:" + pixelBuffer.isDirect());

    }

    private void closeBuffers()
    {
        oesFrameBuffer.delete();
        oesFbTexture.delete();

        processingBuffer1.delete();
        processingTexture1.delete();
        
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
