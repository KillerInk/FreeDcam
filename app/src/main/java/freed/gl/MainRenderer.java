package freed.gl;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.SurfaceTexture;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import freed.FreedApplication;
import freed.gl.program.FPShape;
import freed.gl.program.FocuspeakZebraShape;
import freed.gl.program.GLProgram;
import freed.gl.program.PreviewShape;
import freed.gl.program.ZebraShape;
import freed.gl.shader.DefaultVertexShader;
import freed.gl.shader.FocusPeakZebraShader;
import freed.gl.shader.PreviewFragmentShader;
import freed.gl.shader.SobelFpFragmentShader;
import freed.gl.shader.ZebraShader;
import freed.utils.Log;

public class MainRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {


    private static final String TAG = MainRenderer.class.getSimpleName();

    private boolean mGLInit = false;
    private boolean mUpdateST = false;

    private final GLPreview mView;

    private GLProgram activeProgram;
    private PreviewShape previewShape;
    private FPShape fpShape;
    private ZebraShape zebraShape;
    private FocuspeakZebraShape focuspeakZebraShape;
    private GLPreview.PreviewProcessors processors = GLPreview.PreviewProcessors.Normal;
    private final PreviewModel previewModel;

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
                activeProgram = previewShape;
                break;
            case FocusPeak:
                activeProgram = fpShape;
                break;
            case Zebra:
                activeProgram = zebraShape;
                break;
            case FocusPeak_Zebra:
                activeProgram = focuspeakZebraShape;
                break;
        }
    }


    public void onDrawFrame(GL10 unused) {
        if (!mGLInit) return;
            if (mUpdateST) {
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
        activeProgram.draw();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        cameraInputTextureHolder = new GLTex(0);
        cameraInputTextureHolder.setActive();
        int glesv = GlVersion.getGlesVersion();
        Log.d(TAG, "GlesVersion:" + glesv);

        DefaultVertexShader vertexShader = new DefaultVertexShader(glesv);
        vertexShader.createShader();

        SobelFpFragmentShader fpFragmentShader = new SobelFpFragmentShader(glesv);
        fpFragmentShader.createShader();

        PreviewFragmentShader previewFragmentShader = new PreviewFragmentShader(glesv);
        previewFragmentShader.createShader();

        ZebraShader zebraShader = new ZebraShader(glesv);
        zebraShader.createShader();

        FocusPeakZebraShader focusPeakZebraShader = new FocusPeakZebraShader(glesv);
        focusPeakZebraShader.createShader();

        previewShape = new PreviewShape(glesv,previewModel);
        previewShape.setFragmentShader(previewFragmentShader);
        previewShape.setVertexShader(vertexShader);
        previewShape.createAndLinkProgram();
        previewShape.setGlTex(cameraInputTextureHolder);
        activeProgram = previewShape;

        fpShape = new FPShape(glesv,previewModel);
        fpShape.setVertexShader(vertexShader);
        fpShape.setFragmentShader(fpFragmentShader);
        fpShape.createAndLinkProgram();
        fpShape.setGlTex(cameraInputTextureHolder);

        zebraShape = new ZebraShape(glesv,previewModel);
        zebraShape.setVertexShader(vertexShader);
        zebraShape.setFragmentShader(zebraShader);
        zebraShape.createAndLinkProgram();
        zebraShape.setGlTex(cameraInputTextureHolder);

        focuspeakZebraShape = new FocuspeakZebraShape(glesv,previewModel);
        focuspeakZebraShape.setVertexShader(vertexShader);
        focuspeakZebraShape.setFragmentShader(focusPeakZebraShader);
        focuspeakZebraShape.createAndLinkProgram();
        focuspeakZebraShape.setGlTex(cameraInputTextureHolder);

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
