package freed.gl.program;

import android.opengl.GLES11Ext;
import android.opengl.GLES31;
import android.opengl.GLES31;

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
    private Shader computeShader;
    protected GLTex glTex;
    private int glTex_id;
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

    @Override
    public void setComputeShader(Shader computeShader) {
        this.computeShader = computeShader;
    }

    public void setGlTex(GLTex glTex)
    {
        this.glTex = glTex;
    }

    @Override
    public void createAndLinkProgram()
    {
        hProgram = GLES31.glCreateProgram();
        checkGlError("glCreateProgram");
        Log.d(TAG,"createAndLinkProgram id:" +hProgram);
        if (vertexShader != null) {
            GLES31.glAttachShader(hProgram, vertexShader.getHandel());
            checkGlError("glAttachShader vertex");
        }
        if (fragmentShader != null) {
            GLES31.glAttachShader(hProgram, fragmentShader.getHandel());
            checkGlError("glAttachShader fragment");
        }
        if (computeShader != null) {
            GLES31.glAttachShader(hProgram, computeShader.getHandel());
            checkGlError("glAttachShader compute");
        }
        GLES31.glLinkProgram(hProgram);
        checkGlError("glLinkProgram");
        if (computeShader == null) {
            vPosition = GLES31.glGetAttribLocation(hProgram, "vPosition");
            vTexCoord = GLES31.glGetAttribLocation(hProgram, "vTexCoord");
            glTex_id = GLES31.glGetUniformLocation(hProgram, "sTexture");
        }
    }

    @Override
    public void close() {
        Log.d(TAG, "close program id:" +hProgram);
        GLES31.glDeleteProgram(hProgram);
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
        GLES31.glEnableVertexAttribArray(vPosition);
        GLES31.glEnableVertexAttribArray(vTexCoord);

        GLES31.glDrawArrays(GLES31.GL_TRIANGLE_STRIP, 0, 4);

        GLES31.glDisableVertexAttribArray(vPosition);
        GLES31.glDisableVertexAttribArray(vTexCoord);
    }

    protected void onBindTexture() {
        GLES31.glActiveTexture(GLES31.GL_TEXTURE0);
        checkGlError("onBindTexture glActiveTexture");
        if (glTex != null) {
            GLES31.glBindTexture(glTex.getGLTextureType(), glTex.getId());
            checkGlError("onBindTexture glBindTexture");
        }
        GLES31.glUniform1i(glTex_id,0);
        checkGlError("onBindTexture glUniform1i");
    }

    protected void onSetData()
    {
        GLES31.glVertexAttribPointer(vPosition, 2, GLES31.GL_FLOAT, false, 4 * 2, vertexBuffer);
        checkGlError("onSetData vertex Buffer");
        GLES31.glVertexAttribPointer(vTexCoord, 2, GLES31.GL_FLOAT, false, 4 * 2, textureBuffer);
        checkGlError("onSetData texture Buffer");
    }

    public void onUseProgram() {
        GLES31.glUseProgram(hProgram);
        checkGlError("onUseProgram");
    }

    protected void onClear()
    {
        GLES31.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        checkGlError("onClear glClearColor");
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT | GLES31.GL_DEPTH_BUFFER_BIT);
        checkGlError("onClear glClear");
    }

    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES31.glGetError()) != GLES31.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + getGLErrorString(error));
            //throw new RuntimeException(glOperation + ": glError " + getGLErrorString(error));
        }
    }

    private static String getGLErrorString(int error)
    {
        switch (error)
        {
            case GLES31.GL_INVALID_ENUM:
                return "GL_INVALID_ENUM";
                case GLES31.GL_INVALID_VALUE:
                return "GL_INVALID_VALUE";
                case GLES31.GL_INVALID_OPERATION:
                return "GL_INVALID_OPERATION";
                case GLES31.GL_OUT_OF_MEMORY:
                return "GL_OUT_OF_MEMORY";
            default:
                return  error+"";
        }
    }
}
