package freed.gl;

import android.graphics.SurfaceTexture;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import freed.gl.program.SuperShaderShape;
import freed.gl.shader.DefaultVertexShader;
import freed.gl.shader.SuperShader;
import freed.utils.Log;

public class MainRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {


    private static final String TAG = MainRenderer.class.getSimpleName();

    private boolean mGLInit = false;
    private boolean mUpdateST = false;

    private final GLPreview mView;

    private GLPreview.PreviewProcessors processors = GLPreview.PreviewProcessors.Normal;
    private final PreviewModel previewModel;
    private SuperShaderShape superShaderShape;

    GLTex cameraInputTextureHolder;

    public MainRenderer(GLPreview view, PreviewModel previewModel) {
        mView = view;
        this.previewModel = previewModel;
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
                cameraInputTextureHolder.getmSTexture().updateTexImage();
            }
            catch (RuntimeException ex)
            {
                Log.WriteEx(ex);
            }
            finally {
                mUpdateST = false;
            }
        }
        superShaderShape.draw();
        if (previewModel.getFloat_position() <= 10.0f)
            previewModel.setFloat_position(previewModel.getFloat_position() +0.05f);
        else
            previewModel.setFloat_position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        cameraInputTextureHolder = new GLTex(0);
        cameraInputTextureHolder.setActive();
        int glesv = GlVersion.getGlesVersion();
        Log.d(TAG, "GlesVersion:" + glesv);

        DefaultVertexShader vertexShader = new DefaultVertexShader(glesv);
        vertexShader.createShader();

        SuperShader superShader = new SuperShader(glesv);
        superShader.createShader();

        superShaderShape = new SuperShaderShape(glesv,previewModel);
        superShaderShape.setFragmentShader(superShader);
        superShaderShape.setVertexShader(vertexShader);
        superShaderShape.createAndLinkProgram();
        superShaderShape.setGlTex(cameraInputTextureHolder);

        cameraInputTextureHolder.getmSTexture().setOnFrameAvailableListener(this);
        mGLInit = true;
        mView.fireOnSurfaceTextureAvailable(cameraInputTextureHolder.getmSTexture(),0,0);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }


    public SurfaceTexture getmSTexture()
    {
        if (cameraInputTextureHolder != null)
            return cameraInputTextureHolder.getmSTexture();
        return null;
    }


    public synchronized void onFrameAvailable(SurfaceTexture st) {
        mUpdateST = true;
        mView.requestRender();
    }

}
