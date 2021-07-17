package freed.gl;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import freed.gl.program.OesProgram;
import freed.gl.program.SuperShaderShape;
import freed.gl.shader.OesFragmentShader;
import freed.gl.shader.OesVertexShader;
import freed.gl.shader.PreviewVertexShader;
import freed.gl.shader.SuperShader;
import freed.gl.texture.GL2DTex;
import freed.gl.texture.GLCameraTex;
import freed.gl.texture.GLFrameBuffer;
import freed.utils.Log;

public class MainRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {


    private static final String TAG = MainRenderer.class.getSimpleName();

    private boolean mGLInit = false;
    private boolean mUpdateST = false;

    private final GLPreview mView;

    private GLPreview.PreviewProcessors processors = GLPreview.PreviewProcessors.Normal;
    private final PreviewModel previewModel;
    private SuperShaderShape superShaderShape;
    private OesProgram oesProgram;

    GLCameraTex cameraInputTextureHolder;
    GLFrameBuffer frameBuffer;
    private GL2DTex fbtexture;
    int width;
    int height;

    public MainRenderer(GLPreview view, PreviewModel previewModel) {
        mView = view;
        this.previewModel = previewModel;
        frameBuffer = new GLFrameBuffer();
        fbtexture = new GL2DTex();
        cameraInputTextureHolder = new GLCameraTex();
    }

    public void setProgram(GLPreview.PreviewProcessors processors)
    {
        this.processors = processors;
        switch (processors) {
            case Normal:
                previewModel.setZebra(false);
                previewModel.setFocuspeak(false);
                break;
            case FocusPeak:
                previewModel.setZebra(false);
                previewModel.setFocuspeak(true);
                break;
            case Zebra:
                previewModel.setZebra(true);
                previewModel.setFocuspeak(false);
                break;
            case FocusPeak_Zebra:
                previewModel.setZebra(true);
                previewModel.setFocuspeak(true);
                break;
        }
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
        frameBuffer.setActive();
        oesProgram.setGlTex(cameraInputTextureHolder);
        oesProgram.draw();
        frameBuffer.switchToDefaultFB();

        superShaderShape.setGlTex(fbtexture);
        superShaderShape.draw();
        if (previewModel.getFloat_position() <= 10.0f)
            previewModel.setFloat_position(previewModel.getFloat_position() +0.05f);
        else
            previewModel.setFloat_position(0);
        drawing = false;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if (width == 0 && height == 0)
        {
            width = (int)previewModel.getTextSize()[0];
            height = (int)previewModel.getTextSize()[1];
        }
        Log.d(TAG, "onSurface created " +width + "/" + height);
        int m_viewport[] = new int[4];

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

        SuperShader superShader = new SuperShader(glesv);
        superShader.createShader();

        oesProgram  = new OesProgram(glesv,previewModel);
        oesProgram.setFragmentShader(oesFragmentShader);
        oesProgram.setVertexShader(vertexShader);
        oesProgram.createAndLinkProgram();

        superShaderShape = new SuperShaderShape(glesv,previewModel);
        superShaderShape.setFragmentShader(superShader);
        superShaderShape.setVertexShader(previewVertexShader);
        superShaderShape.createAndLinkProgram();

        cameraInputTextureHolder.getSurfaceTexture().setOnFrameAvailableListener(this);
        mGLInit = true;
        mView.fireOnSurfaceTextureAvailable(cameraInputTextureHolder.getSurfaceTexture(),0,0);
    }

    private void createBuffers()
    {
        frameBuffer.create();
        fbtexture.create(width,height);
        frameBuffer.setOutputTexture(fbtexture);
        Log.d(TAG,"Framebuffer successful:" +frameBuffer.isSuccessfulLoaded());


    }

    private void closeBuffers()
    {
        frameBuffer.delete();
        fbtexture.delete();
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

}
