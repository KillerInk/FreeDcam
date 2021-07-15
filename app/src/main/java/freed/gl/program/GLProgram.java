package freed.gl.program;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import freed.gl.texture.GLTex;
import freed.gl.shader.Shader;
import freed.utils.Log;

public abstract class GLProgram implements GLProgamInterface {
    protected int glesVersion;
    protected int hProgram = -1;

    private final static String TAG = GLProgram.class.getSimpleName();
    private Shader vertexShader;
    private Shader fragmentShader;
    private GLTex glTex;

    public GLProgram(int glesVersion)
    {
        this.glesVersion = glesVersion;
    }

    @Override
    public void setVertexShader(Shader vertexShader) {
        this.vertexShader = vertexShader;
    }

    @Override
    public void setFragmentShader(Shader fragmentShader) {
        this.fragmentShader = fragmentShader;
    }

    public void setGlTex(GLTex glTex)
    {
        this.glTex = glTex;
    }

    @Override
    public void createAndLinkProgram()
    {
        hProgram = GLES20.glCreateProgram();
        checkGlError("glCreateProgram");
        Log.d(TAG,"createAndLinkProgram id:" +hProgram);
        GLES20.glAttachShader(hProgram, vertexShader.getHandel());
        checkGlError("glAttachShader vertex");
        GLES20.glAttachShader(hProgram, fragmentShader.getHandel());
        checkGlError("glAttachShader fragment");
        GLES20.glLinkProgram(hProgram);
        checkGlError("glLinkProgram");
    }

    @Override
    public void close() {
        Log.d(TAG, "close program id:" +hProgram);
        GLES20.glDeleteProgram(hProgram);
        checkGlError("glDeleteProgram");
        hProgram = -1;
    }

    @Override
    public void draw() {

        //step0 clear
        onClear();
        //step1 use program
        onUseProgram();
        //step2 active and bind custom data
        onSetData();
        //step3 bind texture
        onBindTexture();
        //step4 normal draw
        onDraw();
    }

    protected abstract void onDraw();

    private void onBindTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        if (glTex != null)
            GLES20.glBindTexture(glTex.getGLTextureType(), glTex.getId());
        //GLES20.glUniform1i(mUTexture, 0);
    }

    protected abstract void onSetData();

    private void onUseProgram() {
        GLES20.glUseProgram(hProgram);
    }

    protected void onClear()
    {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

  /*  public void bindTexture(GLTex glTex)
    {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(glTex.getGLTextureType(), glTex.getId());
    }*/


    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + getGLErrorString(error));
            //throw new RuntimeException(glOperation + ": glError " + getGLErrorString(error));
        }
    }

    private static String getGLErrorString(int error)
    {
        switch (error)
        {
            case GLES20.GL_INVALID_ENUM:
                return "GL_INVALID_ENUM";
                case GLES20.GL_INVALID_VALUE:
                return "GL_INVALID_VALUE";
                case GLES20.GL_INVALID_OPERATION:
                return "GL_INVALID_OPERATION";
                case GLES20.GL_OUT_OF_MEMORY:
                return "GL_OUT_OF_MEMORY";
            default:
                return  error+"";
        }
    }
}
