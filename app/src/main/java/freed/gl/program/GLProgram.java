package freed.gl.program;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

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
    private float[] vtmp = {1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f};
    private float[] ttmp = {1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f};
    protected FloatBuffer vertexBuffer;
    protected FloatBuffer textureBuffer;

    protected int vTexCoord;
    protected int vPosition;
    protected int sTexture;

    public GLProgram(int glesVersion)
    {
        this.glesVersion = glesVersion;

    }

    @Override
    public void create() {
        vertexBuffer = ByteBuffer.allocateDirect(vtmp.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vtmp);
        vertexBuffer.position(0);
        textureBuffer = ByteBuffer.allocateDirect(ttmp.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.put(ttmp);
        textureBuffer.position(0);
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
        vPosition = GLES20.glGetAttribLocation(hProgram, "vPosition");
        vTexCoord = GLES20.glGetAttribLocation(hProgram, "vTexCoord");
        //sTexture = GLES20.glGetAttribLocation(hProgram, "sTexture");
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

    protected void onDraw()
    {
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glEnableVertexAttribArray(vTexCoord);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(vPosition);
        GLES20.glDisableVertexAttribArray(vTexCoord);
    }

    private void onBindTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        if (glTex != null)
            GLES20.glBindTexture(glTex.getGLTextureType(), glTex.getId());
    }

    protected void onSetData()
    {
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 4 * 2, vertexBuffer);
        GLES20.glVertexAttribPointer(vTexCoord, 2, GLES20.GL_FLOAT, false, 4 * 2, textureBuffer);
        checkGlError("onSetData");
    }

    private void onUseProgram() {
        GLES20.glUseProgram(hProgram);
    }

    protected void onClear()
    {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

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
