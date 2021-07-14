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


    public void onDrawFrame(GL10 unused) {
        if (!mGLInit) return;

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
       /* frameBuffer.setActive();
        oesProgram.bindTexture(cameraInputTextureHolder);
        oesProgram.draw();
        frameBuffer.switchToDefaultFB();

        superShaderShape.bindTexture(fbtexture);*/
        superShaderShape.draw();
        if (previewModel.getFloat_position() <= 10.0f)
            previewModel.setFloat_position(previewModel.getFloat_position() +0.05f);
        else
            previewModel.setFloat_position(0);
        //frameBuffer.drawToScreen();
        /*previewShape.bind(tempTexture);
        previewShape.draw();*/

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if (width == 0 && height == 0)
        {
            width = (int)previewModel.getTextSize()[0];
            height = (int)previewModel.getTextSize()[1];
        }
       /* frameBuffer.create();
        fbtexture.create(width,height);
        frameBuffer.setOutputTexture(fbtexture);*/
        Log.d(TAG,"Framebuffer successful:" +frameBuffer.isSuccessfulLoaded());

        //GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        cameraInputTextureHolder.create(width,height);
        int glesv = GlVersion.getGlesVersion();
        Log.d(TAG, "GlesVersion:" + glesv);

        OesVertexShader vertexShader = new OesVertexShader(glesv);
        vertexShader.createShader();
        OesFragmentShader oesFragmentShader = new OesFragmentShader(glesv);
        oesFragmentShader.createShader();

        SuperShader superShader = new SuperShader(glesv);
        superShader.createShader();

        oesProgram  = new OesProgram(glesv,previewModel);
        oesProgram.setFragmentShader(oesFragmentShader);
        oesProgram.setVertexShader(vertexShader);
        oesProgram.createAndLinkProgram();
        //oesProgram.bindTexture(cameraInputTextureHolder);

        superShaderShape = new SuperShaderShape(glesv,previewModel);
        superShaderShape.setFragmentShader(superShader);
        superShaderShape.setVertexShader(vertexShader);
        superShaderShape.createAndLinkProgram();
        //superShaderShape.bindTexture(offScreenTexture);
        //superShaderShape.setGlTex(cameraInputTextureHolder);

        cameraInputTextureHolder.getSurfaceTexture().setOnFrameAvailableListener(this);
        mGLInit = true;
        mView.fireOnSurfaceTextureAvailable(cameraInputTextureHolder.getSurfaceTexture(),0,0);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        this.width = width;
        this.height = height;
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
